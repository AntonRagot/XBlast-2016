package ch.epfl.xblast.etape6;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.etape6.events.Event;
import ch.epfl.xblast.etape6.events.PlayerState;
import ch.epfl.xblast.etape6.generator.EventsGenerator;
import ch.epfl.xblast.etape6.generator.PlayersEventsGenerator;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Player.DirectedPosition;

public class GameSimulation {

    public static Board createBoard() {
        Block __ = Block.FREE;
        Block XX = Block.INDESTRUCTIBLE_WALL;
        Block xx = Block.DESTRUCTIBLE_WALL;
        return Board.ofQuadrantNWBlocksWalled(Arrays.asList(Arrays.asList(__, __, __, __, __, xx, __),
                Arrays.asList(__, XX, xx, XX, xx, XX, xx), Arrays.asList(__, xx, __, __, __, xx, __),
                Arrays.asList(xx, XX, __, XX, XX, XX, XX), Arrays.asList(__, xx, __, xx, __, __, __),
                Arrays.asList(xx, XX, xx, XX, xx, XX, __)));
    }

    public static List<Player> createPlayers(int lives, int maxBombs, int bombRange, Cell p1, Cell p2,
            Cell p3, Cell p4) {
        return Arrays.asList(new Player(PlayerID.PLAYER_1, lives, p1, maxBombs, bombRange), new Player(
                PlayerID.PLAYER_2, lives, p2, maxBombs, bombRange), new Player(PlayerID.PLAYER_3, lives, p3,
                maxBombs, bombRange), new Player(PlayerID.PLAYER_4, lives, p4, maxBombs, bombRange));
    }

  //parses positions from a line of player_positions.txt
    public static List<List<Integer>> positionsList(String positions) {
    	List<List<Integer>> pos = new ArrayList<List<Integer>>(100);
    	String[] strPos = positions.split(";");
    	for(int i = 1; i < strPos.length; i++) {
    		List<String> parsed = Arrays.asList(strPos[i].replace("(", "").replace(")", "").split(","));
    		List<Integer> intPos = new ArrayList<Integer>(3);
    		for(String e: parsed) {
    			intPos.add(Integer.decode(e));
    		}
    		pos.add(intPos);
    	}
    	return pos;
    }
    
    public static boolean compare(DirectedPosition p, List<Integer> expected) {
    	return expected.get(0).intValue() == p.position().x() && 
    		   expected.get(1).intValue() == p.position().y() &&
    		   expected.get(2).intValue() == p.direction().ordinal();
    }
    
    public static void runGame(PlayersEventsGenerator playersEventsGen,
    		GameState s,
            EventsGenerator playersStates) throws InterruptedException {
        Map<PlayerID, Event> nextEvent = playersEventsGen.next();
        Map<PlayerID, Event> nextOutput = null;
        if (playersStates != null)
            nextOutput = playersStates.next();
        while (!nextEvent.isEmpty() && !s.isGameOver()) {
            s = s.next(playersEventsGen.speedChangeEvents(), playersEventsGen.bombDropEvents());
            if (playersStates != null) {
                checkStatus(s, nextOutput);
                nextOutput = playersStates.next();
            }
            nextEvent = playersEventsGen.next();

        }
    }
    
    /**
     *  Uses a String iterator to compare current player positions to reference positions
     * @param playersEventsGen
     * @param s
     * @param reference_file
     */
    public static void runGame(PlayersEventsGenerator playersEventsGen,
    		GameState s,
    		Iterator<String> reference_file) {

    	Map<PlayerID, Event> nextEvent = playersEventsGen.next();
 
        while (!nextEvent.isEmpty() && !s.isGameOver() && reference_file.hasNext()) {
            s = s.next(playersEventsGen.speedChangeEvents(), playersEventsGen.bombDropEvents());
            
            for(Player p: s.players()) {
                List<List<Integer>> pos = GameSimulation.positionsList(reference_file.next());
                Sq<DirectedPosition> seq = p.directedPositions();

                for(List<Integer> e: pos) {
                	DirectedPosition h = seq.head();
                	assertTrue(GameSimulation.compare(h, e));

                	seq = seq.tail();                	
                }
            }          
            nextEvent = playersEventsGen.next();
        }
    }
    
    /**
     * Used to generate the reference positions
     * @param playersEventsGen
     * @param s
     */
    public static void runGame(PlayersEventsGenerator playersEventsGen, GameState s) {

    	Map<PlayerID, Event> nextEvent = playersEventsGen.next();
 
        while (!nextEvent.isEmpty() && !s.isGameOver()) {
            s = s.next(playersEventsGen.speedChangeEvents(), playersEventsGen.bombDropEvents());
            nextEvent = playersEventsGen.next();
        }
    }

    /*
     * Check the actual game status with the expected output status
     */
    private static void checkStatus(GameState s, Map<PlayerID, Event> nextOutput) {

        for (Player p : s.players()) {
            PlayerState state = new PlayerState(p);

            assertEquals("For player : " + p.id(), nextOutput.get(p.id()), state);

        }

    }
}
