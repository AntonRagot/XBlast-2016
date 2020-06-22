package ch.epfl.xblast.etape6;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.etape6.events.EventSequence;
import ch.epfl.xblast.etape6.generator.PlayersEventsGenerator;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;

public class DirectionChangeTest {

    private static final Cell POS_NW = new Cell(1, 1);
    private static final Cell POS_NE = new Cell(-2, 1);
    private static final Cell POS_SE = new Cell(-2, -2);
    private static final Cell POS_SW = new Cell(1, -2);
    private static List<Player> players = null;
    
    
    @BeforeClass 
    public static void setUpClass() {            
        players = GameSimulation.createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW);
    }
    
    /**
     * Couple of direction changes to test the fact that player turn only on subcells
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void playersChangeDirectionTest() throws IOException, URISyntaxException {
        //PlayerEvent coding: { Direction , isDropBomb }
        //Directions:
        //  1 : N
        //  2 : E
        //  3 : S
        //  4 : W
        //  isDropBomb:
        //  0 : false, otherwise : true
        
    	//players should effectively change direction from the next central subcell
        int[][] eventsP1 = {{2, 0}, {2, 0}, {3, 0}, {3, 0}};
        int[][] eventsP2 = {{3, 0}, {3, 0}, {2, 0}, {2, 0}};
        int[][] eventsP3 = {{4, 0}, {4, 0}, {1, 0}, {1, 0}};
        int[][] eventsP4 = {{1, 0}, {1, 0}, {2, 0}, {2, 0}};

        // players Events
        EventSequence[] playersEvents = new EventSequence[4];
        playersEvents[0] = new EventSequence(players.get(0).id(), eventsP1, false);
        playersEvents[1] = new EventSequence(players.get(1).id(), eventsP2, false);
        playersEvents[2] = new EventSequence(players.get(2).id(), eventsP3, false);
        playersEvents[3] = new EventSequence(players.get(3).id(), eventsP4, false);

        // Game Setup
        GameState s = new GameState(GameSimulation.createBoard(), players);

        PlayersEventsGenerator playersEventsGen = new PlayersEventsGenerator(playersEvents);

        String fileName = getClass().getResource("/stage6files/direction_changes_players_positions.txt").toURI().getPath();
		Stream<String> player_positions = Files.lines(Paths.get(fileName));
    	Iterator<String> pos_iterator = player_positions.iterator();
        
        GameSimulation.runGame(playersEventsGen, s, pos_iterator);
        player_positions.close();
    }
    
    /**
     * Checks that walls effectively block players
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void stuckOnWallTest() throws IOException, URISyntaxException {
        //PlayerEvent coding: { Direction , isDropBomb }
        //Directions:
        //  2 : E
        //  isDropBomb:
        //  0 : false, otherwise : true
        int[][] eventsP1 = {{2, 0}, {2, 0}, {2, 0}, {2, 0}};
        int[][] eventsP2 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP3 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP4 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};

        // players Events
        EventSequence[] playersEvents = new EventSequence[4];
        playersEvents[0] = new EventSequence(players.get(0).id(), eventsP1, false);
        playersEvents[1] = new EventSequence(players.get(1).id(), eventsP2, false);
        playersEvents[2] = new EventSequence(players.get(2).id(), eventsP3, false);
        playersEvents[3] = new EventSequence(players.get(3).id(), eventsP4, false);

        // Game Setup (with a wall next to player 1)
        Block __ = Block.FREE;
        Block XX = Block.INDESTRUCTIBLE_WALL;
        Block xx = Block.DESTRUCTIBLE_WALL;
        Board b = Board.ofQuadrantNWBlocksWalled(Arrays.asList(Arrays.asList(__, XX, __, __, __, xx, __),
                Arrays.asList(__, XX, xx, XX, xx, XX, xx), Arrays.asList(__, xx, __, __, __, xx, __),
                Arrays.asList(xx, XX, __, XX, XX, XX, XX), Arrays.asList(__, xx, __, xx, __, __, __),
                Arrays.asList(xx, XX, xx, XX, xx, XX, __)));
        GameState s = new GameState(b, players);

        PlayersEventsGenerator playersEventsGen = new PlayersEventsGenerator(playersEvents);

        String fileName = getClass().getResource("/stage6files/stuckonwall_positions.txt").toURI().getPath();
		Stream<String> player_positions = Files.lines(Paths.get(fileName));
    	Iterator<String> pos_iterator = player_positions.iterator();
        
        GameSimulation.runGame(playersEventsGen, s, pos_iterator);
        player_positions.close();
    }
    
    /**
     * Checks that bombs effectively block the players (at the proper position)
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void stuckOnBombTest() throws IOException, URISyntaxException {
        //PlayerEvent coding: { Direction , isDropBomb }
        //Directions:
        //  2 : E
        //  isDropBomb:
        //  0 : false, otherwise : true
        
        int[][] eventsP1 = new int[115][2];
        
    	// Get to the start of the next cell
        for(int i = 0; i < 8; i++) {
        	eventsP1[0][0] = 2;
        	eventsP1[0][1] = 0;
        }    
        //drop a bomb
        eventsP1[8][0] = 2;
        eventsP1[8][1] = 2;
        // try to move past the bomb
        for(int i = 9	; i < eventsP1.length; i++) {
        	eventsP1[i][0] = 2; 
        	eventsP1[i][1] = 0;
        }
        
        int[][] eventsP2 = new int[115][2];
        int[][] eventsP3 = new int[115][2];
        int[][] eventsP4 = new int[115][2];
        for(int i = 0; i < eventsP2.length; i ++) {
        	eventsP2[i][0] = eventsP3[i][0] = eventsP4[i][0] = 0;
        	eventsP2[i][1] = eventsP3[i][1] = eventsP4[i][1] = 0;
        }

        // players Events
        EventSequence[] playersEvents = new EventSequence[4];
        playersEvents[0] = new EventSequence(players.get(0).id(), eventsP1, false);
        playersEvents[1] = new EventSequence(players.get(1).id(), eventsP2, false);
        playersEvents[2] = new EventSequence(players.get(2).id(), eventsP3, false);
        playersEvents[3] = new EventSequence(players.get(3).id(), eventsP4, false);

        // Game Setup
        GameState s = new GameState(GameSimulation.createBoard(), players);

        PlayersEventsGenerator playersEventsGen = new PlayersEventsGenerator(playersEvents);

        String fileName = getClass().getResource("/stage6files/stuckonbomb_positions.txt").toURI().getPath();
		Stream<String> player_positions = Files.lines(Paths.get(fileName));
    	Iterator<String> pos_iterator = player_positions.iterator();
        
        GameSimulation.runGame(playersEventsGen, s, pos_iterator);
        player_positions.close();
    }
    
    /**
     * Checks that a player continues its path once a crumbling wall disappears
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void stuckOnCrumblingWallTest() throws IOException, URISyntaxException {
        //PlayerEvent coding: { Direction , isDropBomb }
        //Directions:
        //  2 : E
        //  isDropBomb:
        //  0 : false, otherwise : true
        
    	// Bomb fuse is 100 ticks by default and crumbling wall takes 30 ticks to disappear
        int[][] eventsP1 = new int[135][2];
        eventsP1[0][0] = 0;
        eventsP1[0][1] = 1; //drop a bomb
        for(int i = 1; i < eventsP1.length; i++) {
        	eventsP1[i][0] = 2; // try to move past the wall
        	eventsP1[i][1] = 0;
        }
        
        int[][] eventsP2 = new int[135][2];
        int[][] eventsP3 = new int[135][2];
        int[][] eventsP4 = new int[135][2];
        for(int i = 0; i < eventsP2.length; i ++) {
        	eventsP2[i][0] = eventsP3[i][0] = eventsP4[i][0] = 0;
        	eventsP2[i][1] = eventsP3[i][1] = eventsP4[i][1] = 0;
        }
        
        // players Events
        EventSequence[] playersEvents = new EventSequence[4];
        playersEvents[0] = new EventSequence(players.get(0).id(), eventsP1, false);
        playersEvents[1] = new EventSequence(players.get(1).id(), eventsP2, false);
        playersEvents[2] = new EventSequence(players.get(2).id(), eventsP3, false);
        playersEvents[3] = new EventSequence(players.get(3).id(), eventsP4, false);

        // Game Setup (with a destructible wall next to player 1)
        Block __ = Block.FREE;
        Block XX = Block.INDESTRUCTIBLE_WALL;
        Block xx = Block.DESTRUCTIBLE_WALL;
        Board b = Board.ofQuadrantNWBlocksWalled(Arrays.asList(Arrays.asList(__, xx, __, __, __, xx, __),
                Arrays.asList(__, XX, xx, XX, xx, XX, xx), Arrays.asList(__, xx, __, __, __, xx, __),
                Arrays.asList(xx, XX, __, XX, XX, XX, XX), Arrays.asList(__, xx, __, xx, __, __, __),
                Arrays.asList(xx, XX, xx, XX, xx, XX, __)));
        GameState s = new GameState(b, players);

        PlayersEventsGenerator playersEventsGen = new PlayersEventsGenerator(playersEvents);

        String fileName = getClass().getResource("/stage6files/stuckoncrumblingwall_positions.txt").toURI().getPath();
		Stream<String> player_positions = Files.lines(Paths.get(fileName));
    	Iterator<String> pos_iterator = player_positions.iterator();
        
        GameSimulation.runGame(playersEventsGen, s, pos_iterator);
        player_positions.close();
    }
    
    /**
     * Checks that a player stops on the next central subcell
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void playerStopsNextCentralTest() throws IOException, URISyntaxException {
        //PlayerEvent coding: { Direction , isDropBomb }
        //Directions:
    	//  0 : Stop
        //  2 : E
        //  isDropBomb:
        //  0 : false, otherwise : true
        
        int[][] eventsP1 = {{2, 0}, {2, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP2 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP3 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP4 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};

        // players Events
        EventSequence[] playersEvents = new EventSequence[4];
        playersEvents[0] = new EventSequence(players.get(0).id(), eventsP1, false);
        playersEvents[1] = new EventSequence(players.get(1).id(), eventsP2, false);
        playersEvents[2] = new EventSequence(players.get(2).id(), eventsP3, false);
        playersEvents[3] = new EventSequence(players.get(3).id(), eventsP4, false);

        // Game Setup
        GameState s = new GameState(GameSimulation.createBoard(), players);

        PlayersEventsGenerator playersEventsGen = new PlayersEventsGenerator(playersEvents);

        String fileName = getClass().getResource("/stage6files/playerstopsnextcentral_positions.txt").toURI().getPath();
		Stream<String> player_positions = Files.lines(Paths.get(fileName));
    	Iterator<String> pos_iterator = player_positions.iterator();
        
        GameSimulation.runGame(playersEventsGen, s, pos_iterator);
        player_positions.close();
    }
    
    /**
     * Checks that a player going in the direction opposite to the central subcell's direction can leave a bombed cell
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void playerLeavesBombedCellTest() throws IOException, URISyntaxException {
        //PlayerEvent coding: { Direction , isDropBomb }
        //Directions:
    	//  0 : Stop
        //  2 : E
        //  isDropBomb:
        //  0 : false, otherwise : true
        
        int[][] eventsP1 = {{2, 0}, {2, 0}, {2, 1}, {2, 0}, {2, 0}, {2, 0}, {2, 0}, {2, 0}, {2, 0}, {2, 0}, {2, 0}, {2, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP2 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP3 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP4 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};

        // players Events
        EventSequence[] playersEvents = new EventSequence[4];
        playersEvents[0] = new EventSequence(players.get(0).id(), eventsP1, false);
        playersEvents[1] = new EventSequence(players.get(1).id(), eventsP2, false);
        playersEvents[2] = new EventSequence(players.get(2).id(), eventsP3, false);
        playersEvents[3] = new EventSequence(players.get(3).id(), eventsP4, false);

        // Game Setup
        GameState s = new GameState(GameSimulation.createBoard(), players);

        PlayersEventsGenerator playersEventsGen = new PlayersEventsGenerator(playersEvents);


        String fileName = getClass().getResource("/stage6files/playerleavesbombedcell_positions.txt").toURI().getPath();
		Stream<String> player_positions = Files.lines(Paths.get(fileName));
    	Iterator<String> pos_iterator = player_positions.iterator();
        
        GameSimulation.runGame(playersEventsGen, s, pos_iterator);
        player_positions.close();
    }
}
