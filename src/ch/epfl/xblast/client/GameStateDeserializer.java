package ch.epfl.xblast.client;

import java.awt.Image;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.RunLengthEncoder;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.client.GameState.Player;

public final class GameStateDeserializer {
	private static final int NUMBER_OF_LED_IMAGES = 60;
	private static final int NUMBER_OF_BLANKS = 8;
	private static final int NUMBER_OF_FACES_PER_PLAYER = 2;
	private static final int ORDINAL_TEXT_MIDDLE = 10;
	private static final int ORDINAL_TEXT_RIGHT = 11;
	private static final int ORDINAL_TILE_VOID = 12;
	private static final int ORDINAL_LED_OFF = 20;
	private static final int ORDINAL_LED_ON = 21;
	private static final int BOARD_SIZE = 195;
	private final static ImageCollection imageCollectionPlayer = new ImageCollection("player");
	private final static ImageCollection imageCollectionBlock = new ImageCollection("block");
	private final static ImageCollection imageCollectionBombsAndExplosions = new ImageCollection("explosion");
	private final static ImageCollection imageCollectionScoreAndTicks = new ImageCollection("score");

	/**
	 * Private constructor for the class GameStateDeserializer.
	 */
	private GameStateDeserializer() {
	}

	/**
	 * Returns a deserialized GameState after decoding the given list of Bytes.
	 * 
	 * @param serialized
	 * @return deserialized GameState
	 */
	public static GameState deserializeGameState(List<Byte> serialized) {
		int breakBoardExplosion = Byte.toUnsignedInt(serialized.get(0)) + 1;
		int breakExplosionPlayer = Byte.toUnsignedInt(serialized.get(breakBoardExplosion)) + 1;

		List<Image> board = deserializeBoard(RunLengthEncoder.decode(serialized.subList(1, breakBoardExplosion)));
		List<Byte> sExplosion = serialized.subList(breakBoardExplosion + 1, breakBoardExplosion + breakExplosionPlayer);
		List<Byte> bombdecode = RunLengthEncoder.decode(sExplosion);
		List<Image> bombsAndExplosions = deserializeBombsAndExplosions(bombdecode);
		List<Byte> deserializedPlayers = serialized.subList(breakBoardExplosion + breakExplosionPlayer,
				serialized.size() - 1);
		List<Player> players = deserializePlayers(deserializedPlayers);
		List<Image> score = deserializeScore(players);
		List<Image> ticks = deserializeTime(serialized.get(serialized.size() - 1));
		return new GameState(players, board, bombsAndExplosions, score, ticks);
	}

	/**
	 * Method that given a list of Bytes, returns the deserialized list of
	 * Players.
	 * 
	 * @param players
	 * @return List<Player>
	 */
	private static List<Player> deserializePlayers(List<Byte> players) {
		List<Player> deserializedPlayers = new ArrayList<>();
		int x;
		int y;
		int lives;
		Image image;
		Deque<Byte> d = new ArrayDeque<>(players);
		for (int i = 0; i < PlayerID.values().length; i++) {
			lives = d.pollFirst();
			x = Byte.toUnsignedInt(d.pollFirst());
			y = Byte.toUnsignedInt(d.pollFirst());
			image = imageCollectionPlayer.imageOrNull(d.pollFirst());
			deserializedPlayers.add(new Player(PlayerID.values()[i], lives, new SubCell(x, y), image));
		}
		return deserializedPlayers;
	}

	/**
	 * Method that given a list of Bytes, returns the deserialized Board as a
	 * list of Images.
	 * 
	 * @param board
	 * @return List<Image>
	 */
	private static List<Image> deserializeBoard(List<Byte> board) {
		List<Cell> spiralOrder = Cell.SPIRAL_ORDER;
		Image[] deserializedBoard = new Image[BOARD_SIZE];
		spiralOrder.forEach(x -> deserializedBoard[x.rowMajorIndex()] = imageCollectionBlock
				.image(board.get(spiralOrder.indexOf(x))));
		return Arrays.asList(deserializedBoard);
	}

	/**
	 * Method that given a list of Bytes, returns the deserialized bombs and
	 * explosions as a list of Images.
	 * 
	 * @param bombsAndExplosions
	 * @return List<Image>
	 */
	private static List<Image> deserializeBombsAndExplosions(List<Byte> bombsAndExplosions) {
		List<Image> deserializedBombsandExplosions = new ArrayList<>();
		bombsAndExplosions.forEach(x -> deserializedBombsandExplosions.add(imageCollectionBombsAndExplosions.imageOrNull(x)));
		return deserializedBombsandExplosions;
	}

	/**
	 * Method that given a list of Bytes, returns the deserialized score as a
	 * list of Images.
	 * 
	 * @param score
	 * @return List<Image>
	 */
	private static List<Image> deserializeScore(List<Player> player) {
		List<Image> deserializedScore = new ArrayList<>();
		for (Player p : player) {
			deserializedScore.add(p.getLives() > 0
					? imageCollectionScoreAndTicks.image(p.getId().ordinal() * NUMBER_OF_FACES_PER_PLAYER)
					: imageCollectionScoreAndTicks.image(p.getId().ordinal() * NUMBER_OF_FACES_PER_PLAYER + 1));
			deserializedScore.add(imageCollectionScoreAndTicks.image(ORDINAL_TEXT_MIDDLE));
			deserializedScore.add(imageCollectionScoreAndTicks.image(ORDINAL_TEXT_RIGHT));
		}
		deserializedScore.addAll(6,
				Collections.nCopies(NUMBER_OF_BLANKS, imageCollectionScoreAndTicks.image(ORDINAL_TILE_VOID)));
		return deserializedScore;
	}

	/**
	 * Method that given a list of Bytes, returns the deserialized ticks as a
	 * list of Images.
	 * 
	 * @param ticks
	 * @return List<Image>
	 */
	private static List<Image> deserializeTime(byte ticks) {
		List<Image> deserializedTime = new ArrayList<>();
		deserializedTime.addAll(Collections.nCopies(ticks, imageCollectionScoreAndTicks.image(ORDINAL_LED_ON)));
		deserializedTime.addAll(
				Collections.nCopies(NUMBER_OF_LED_IMAGES - ticks, imageCollectionScoreAndTicks.image(ORDINAL_LED_OFF)));
		return deserializedTime;
	}
}