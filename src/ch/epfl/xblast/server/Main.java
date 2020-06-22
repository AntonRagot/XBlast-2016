package ch.epfl.xblast.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerAction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.Time;

public class Main {
	private static final int PORT = 2016;
	private static Map<SocketAddress, PlayerID> registeredPlayers = new HashMap<>();
	private static final Level level = Level.DEFAULT_LEVEL;
	private static DatagramChannel channel;
	private static GameState gameState;
	private static final byte BYTEJOINGAME = (byte) PlayerAction.JOIN_GAME.ordinal();
	private static final byte BYTEDROPBOMB = (byte) PlayerAction.DROP_BOMB.ordinal();
	private static final byte BYTESTOP = (byte) PlayerAction.STOP.ordinal();

	/**
	 * The main for the Server which serializes and sends the GameState and
	 * handles KeyEvents from the Clients.
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		channel = DatagramChannel.open(StandardProtocolFamily.INET);
		channel.bind(new InetSocketAddress(PORT));
		int playerNumber = (args.length == 0) ? 1 : Integer.parseInt(args[0]);
		ByteBuffer buffer = ByteBuffer.allocate(1);

		// Mapping of SocketAddresses to PlayerIDs.
		while (registeredPlayers.keySet().size() != 1) {
			SocketAddress senderAddress = channel.receive(buffer);
			if (buffer.get(0) == BYTEJOINGAME) {
				registeredPlayers.putIfAbsent(senderAddress, PlayerID.values()[registeredPlayers.keySet().size()]);
			}
			buffer.clear();
		}
		long startTime = System.nanoTime();
		channel.configureBlocking(false);
		gameState = Level.DEFAULT_LEVEL.getGameState();

		while (!gameState.isGameOver()) {
			Set<PlayerID> bombDropEvents = new HashSet<>();
			Map<PlayerID, Optional<Direction>> speedChangeEvents = new HashMap<>();

			sendGameState(registeredPlayers, gameState);
			long sleepTime = startTime + ((long) gameState.ticks() * Ticks.TICK_NANOSECOND_DURATION)
					- System.nanoTime();
			if (sleepTime > 0) {
				long millisecond = (long) Math.floor(sleepTime / Time.US_PER_S);
				int nanosecond = (int) (sleepTime - millisecond * Time.US_PER_S);
				Thread.sleep(millisecond, nanosecond);
			}
			SocketAddress address = channel.receive(buffer);
			while (address != null) {
				buffer.rewind();
				Byte byteKeyEvent = buffer.get();
				if (byteKeyEvent != BYTEJOINGAME) {
					if (byteKeyEvent == BYTEDROPBOMB) {
						bombDropEvents.add(registeredPlayers.get(address));
					} else {
						if (byteKeyEvent == BYTESTOP) {
							speedChangeEvents.put(registeredPlayers.get(address), Optional.empty());
						} else {
							speedChangeEvents.put(registeredPlayers.get(address),
									Optional.of(Direction.values()[byteKeyEvent - 1]));
						}
					}

				}
				buffer.clear();
				address = channel.receive(buffer);
			}
			gameState = gameState.next(speedChangeEvents, bombDropEvents);
		}
		channel.close();
	}

	/**
	 * Method that sends the serialized GameState to each player.
	 * 
	 * @param registeredPlayer
	 * @param gameStateToSend
	 * @throws IOException
	 */
	private static void sendGameState(Map<SocketAddress, PlayerID> registeredPlayer, GameState gameStateToSend)
			throws IOException {
		List<Byte> serialiser = GameStateSerializer.serialize(level.getBoardPainter(), gameStateToSend);
		for (SocketAddress socketAddress : registeredPlayers.keySet()) {
			ByteBuffer byteBuffer = ByteBuffer.allocate(serialiser.size() + 1)
					.put((byte) registeredPlayers.get(socketAddress).ordinal());
			for (Byte b : serialiser) {
				byteBuffer.put(b);
			}
			byteBuffer.flip();
			channel.send(byteBuffer, socketAddress);
			byteBuffer.clear();
		}
	}
}