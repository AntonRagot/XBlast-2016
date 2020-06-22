 package ch.epfl.xblast.client;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ch.epfl.xblast.PlayerAction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.Time;

public class Main {
    private static final int PORT = 2016;
	private static XBlastComponent xComponent;
    private static SocketAddress address;
    private static DatagramChannel channel;
    private static final int MAX_SERIALIZER_LENGTH = 409;
    
    /**
     * The main method for the Client which connects to the Server and deserializes
     * and displays the GameState.
     * 
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        //Gather name of the host.
        String host = args.length == 0 ? "localhost" : args[0];

        //Set up the channel and the address.
        channel = DatagramChannel.open(StandardProtocolFamily.INET);
        address = new InetSocketAddress(host, PORT);
        channel.configureBlocking(false);

        //Creating all buffers necessary to connect to the host.
        ByteBuffer buffer = ByteBuffer.allocate(1)
                .put((byte) PlayerAction.JOIN_GAME.ordinal());
        buffer.flip();
        ByteBuffer bufferSerialiser = ByteBuffer.allocate(MAX_SERIALIZER_LENGTH + 1);

        //Tries to connect to the host.
        do {
            channel.send(buffer, address);
            Thread.sleep(Time.MS_PER_S);
            channel.receive(bufferSerialiser);
        } while (bufferSerialiser.remaining() == MAX_SERIALIZER_LENGTH + 1);

        try {
            SwingUtilities.invokeAndWait(() -> createUI(createMap(), createConsumer()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        channel.configureBlocking(true);
        while (true) {
            bufferSerialiser.flip();
            PlayerID id = PlayerID.values()[bufferSerialiser.get()];
            List<Byte> serialiser = new ArrayList<>();
            while (bufferSerialiser.hasRemaining()) {
                serialiser.add(bufferSerialiser.get());
            }
            GameState gameState = GameStateDeserializer.deserializeGameState(serialiser);
            SwingUtilities.invokeLater(() -> xComponent.setGameState(gameState, id));
            bufferSerialiser.clear();
            address = channel.receive(bufferSerialiser);
        }
    }

    /**
     * Method that creates the user interface given a mapping of Integers to PlayerActions
     * and a Consumer of PlayerActions.
     * 
     * @param p
     * @param c
     */
    private static void createUI(Map<Integer, PlayerAction> p, Consumer<PlayerAction> c) {
        xComponent = new XBlastComponent();
        xComponent.addKeyListener(new KeyboardEventHandler(p, c));
        xComponent.setFocusable(true);
        xComponent.requestFocusInWindow();
        JFrame j = new JFrame("XBlast Game");
        j.setVisible(true);
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.getContentPane().add(xComponent);
        j.pack();
    }

    /**
     * Method that creates a mapping of Integers to PlayerActions.
     * 
     * @return Map<Integer, PlayerAction>
     */
    private static Map<Integer, PlayerAction> createMap() {
        Map<Integer, PlayerAction> kb = new HashMap<>();
        kb.put(KeyEvent.VK_UP, PlayerAction.MOVE_N);
        kb.put(KeyEvent.VK_RIGHT, PlayerAction.MOVE_E);
        kb.put(KeyEvent.VK_DOWN, PlayerAction.MOVE_S);
        kb.put(KeyEvent.VK_LEFT, PlayerAction.MOVE_W);
        kb.put(KeyEvent.VK_SPACE, PlayerAction.DROP_BOMB);
        kb.put(KeyEvent.VK_SHIFT, PlayerAction.STOP);
        return Collections.unmodifiableMap(kb);
    }

    /**
     * Method that creates a Consumer of PlayerActions.
     * 
     * @return Consumer<PlayerAction>
     */
    private static Consumer<PlayerAction> createConsumer() {
        return (action) -> {
            ByteBuffer bufferToSend = ByteBuffer.allocate(1);
            bufferToSend.put((byte) action.ordinal());
            bufferToSend.flip();
            try {
                channel.send(bufferToSend, address);
            } catch (Exception e) {
                e.printStackTrace();
            }
            bufferToSend.clear();
        };
    }
}