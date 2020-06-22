package ch.epfl.xblast.client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import ch.epfl.xblast.PlayerAction;

public final class KeyboardEventHandler extends KeyAdapter implements KeyListener {
    private Map<Integer, PlayerAction> actionMap;
    private Consumer<PlayerAction> consumer;

    /**
     * Constructor for the class KeyboardEventHandler which requires a mapping of Integers
     * to PlayerActions and a Consumer of PlayerActions.
     * 
     * @param actionMap
     * @param consumer
     */
    public KeyboardEventHandler(Map<Integer, PlayerAction> actionMap, Consumer<PlayerAction> consumer) {
        this.actionMap = Collections.unmodifiableMap(new HashMap<>(actionMap));;
        this.consumer = consumer;
    }
    
    /**
     * Override for the keyPressed method.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (actionMap.containsKey(event.getKeyCode())) {
            consumer.accept(actionMap.get(event.getKeyCode()));
        }
    }
}