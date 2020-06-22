package ch.epfl.xblast.etape6;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import org.junit.Test;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Player.DirectedPosition;
import ch.epfl.xblast.server.debug.RandomEventGenerator;

import static org.junit.Assert.assertTrue;

/**
 * Checks that the player move as in the example for the random game provided as example
 */
public class RandomTestGame {
    private static final Cell POS_NW = new Cell(1, 1);
    private static final Cell POS_NE = new Cell(-2, 1);
    private static final Cell POS_SE = new Cell(-2, -2);
    private static final Cell POS_SW = new Cell(1, -2);

    private static Board createBoard() {
        Block __ = Block.FREE;
        Block XX = Block.INDESTRUCTIBLE_WALL;
        Block xx = Block.DESTRUCTIBLE_WALL;
        return Board.ofQuadrantNWBlocksWalled(
          Arrays.asList(
            Arrays.asList(__, __, __, __, __, xx, __),
            Arrays.asList(__, XX, xx, XX, xx, XX, xx),
            Arrays.asList(__, xx, __, __, __, xx, __),
            Arrays.asList(xx, XX, __, XX, XX, XX, XX),
            Arrays.asList(__, xx, __, xx, __, __, __),
            Arrays.asList(xx, XX, xx, XX, xx, XX, __)));
    }

    private static List<Player> createPlayers(int lives, int maxBombs, int bombRange, Cell p1, Cell p2, Cell p3, Cell p4) {
        return Arrays.asList(
                new Player(PlayerID.PLAYER_1, lives, p1, maxBombs, bombRange),
                new Player(PlayerID.PLAYER_2, lives, p2, maxBombs, bombRange),
                new Player(PlayerID.PLAYER_3, lives, p3, maxBombs, bombRange),
                new Player(PlayerID.PLAYER_4, lives, p4, maxBombs, bombRange));
    }

    
    
    @Test
    public void testPositionsRandomGame() throws InterruptedException, IOException, URISyntaxException {
        String fileName = getClass().getResource("/stage6files/randomgame_positions.txt").toURI().getPath();
		Stream<String> player_positions = Files.lines(Paths.get(fileName));
    	Iterator<String> pos_iterator = player_positions.iterator();
    	Scanner src = new Scanner(System.in);
    	
        RandomEventGenerator randEvents = new RandomEventGenerator(2016, 30, 100);
        GameState s = new GameState(createBoard(), createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        while (!s.isGameOver()) {
            s = s.next(randEvents.randomSpeedChangeEvents(), randEvents.randomBombDropEvents());

            for(Player p: s.players()) {
                List<List<Integer>> pos = GameSimulation.positionsList(pos_iterator.next());
                Sq<DirectedPosition> seq = p.directedPositions();

                for(List<Integer> e: pos) {
                	DirectedPosition h = seq.head();
                	//if(p.id().equals(PlayerID.PLAYER_4)) {
                	//System.out.println(" h : " + p.id() + " " + h.position() + h.direction().ordinal());
                    //System.out.println(" e : " + p.id() + " " + e);
                	//}
              
                    //if(!GameSimulation.compare(h, e)) {
                       // src.nextLine();
                    //}

                    assertTrue(GameSimulation.compare(h, e));

                	seq = seq.tail();                	
                }
            }
            //System.out.println(s.ticks());
        }
       // System.out.println("Fin");
        player_positions.close();
    }
}
