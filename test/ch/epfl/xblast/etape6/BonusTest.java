package ch.epfl.xblast.etape6;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.etape6.events.EventSequence;
import ch.epfl.xblast.etape6.generator.EventsGenerator;
import ch.epfl.xblast.etape6.generator.PlayersEventsGenerator;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Player.LifeState.State;

public class BonusTest {

    private static final Cell POS_NW = new Cell(1, 1);
    private static final Cell POS_NE = new Cell(-2, 1);
    private static final Cell POS_SE = new Cell(-2, -2);
    private static final Cell POS_SW = new Cell(1, -2);
    private static List<Player> players = null;
    
    private static final int INIT_LIVES = 3;
    private static final int INIT_BOMBS = 2;
    private static final int INIT_RANGE = 3;
    
    @BeforeClass
    public static void setUpClass() {
        players = GameSimulation.createPlayers(INIT_LIVES, INIT_BOMBS, INIT_RANGE, POS_NW.neighbor(Direction.E), POS_NE, POS_SE, POS_SW);
    }

    /**
     * Checks that the range bonus has effect on the player
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void bonusRangeTest() throws URISyntaxException, IOException {
        //PlayerEvent coding: { Direction , isDropBomb }
        //Directions:
        //  1 : N
        //  2 : E
        //  3 : S
        //  4 : W
        //  isDropBomb:
        //  0 : false, otherwise : true
        int[][] eventsP1 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP2 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP3 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP4 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
        
        // PlayerState coding: {lives, state, maxBombs, rangeBombs, xPos, yPos }
        
        int[][] outP1 = {
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE + 1, POS_NW.neighbor(Direction.E).x(), POS_NW.neighbor(Direction.E).y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE + 1, POS_NW.neighbor(Direction.E).x(), POS_NW.neighbor(Direction.E).y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE + 1, POS_NW.neighbor(Direction.E).x(), POS_NW.neighbor(Direction.E).y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE + 1, POS_NW.neighbor(Direction.E).x(), POS_NW.neighbor(Direction.E).y()},};
        int[][] outP2 = {
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_NE.x(), POS_NE.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_NE.x(), POS_NE.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_NE.x(), POS_NE.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_NE.x(), POS_NE.y()},};
        int[][] outP3 = {
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SE.x(), POS_SE.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SE.x(), POS_SE.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SE.x(), POS_SE.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SE.x(), POS_SE.y()},};
        int[][] outP4 = {
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SW.x(), POS_SW.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SW.x(), POS_SW.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SW.x(), POS_SW.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SW.x(), POS_SW.y()},};

        // players Events
        EventSequence[] playersEvents = new EventSequence[4];
        playersEvents[0] = new EventSequence(players.get(0).id(), eventsP1, false);
        playersEvents[1] = new EventSequence(players.get(1).id(), eventsP2, false);
        playersEvents[2] = new EventSequence(players.get(2).id(), eventsP3, false);
        playersEvents[3] = new EventSequence(players.get(3).id(), eventsP4, false);
        
        //player States
        EventSequence[] playersStates = new EventSequence[4];
        playersStates[0] = new EventSequence(players.get(0).id(), outP1, true);
        playersStates[1] = new EventSequence(players.get(1).id(), outP2, true);
        playersStates[2] = new EventSequence(players.get(2).id(), outP3, true);
        playersStates[3] = new EventSequence(players.get(3).id(), outP4, true);
        
        
        // Game Setup (with a wall next to player 1)
        Block __ = Block.FREE;
        Block XX = Block.INDESTRUCTIBLE_WALL;
        Block xx = Block.DESTRUCTIBLE_WALL;
        Block RR = Block.BONUS_RANGE;
        Board b = Board.ofQuadrantNWBlocksWalled(Arrays.asList(
                Arrays.asList(__, RR, __, xx, __, xx, __),
                Arrays.asList(__, XX, xx, XX, xx, XX, xx),
                Arrays.asList(__, xx, __, __, __, xx, __),
                Arrays.asList(xx, XX, __, XX, XX, XX, XX),
                Arrays.asList(__, xx, __, xx, __, __, __),
                Arrays.asList(xx, XX, xx, XX, xx, XX, __)));
        GameState s = new GameState(b, players);
        
        PlayersEventsGenerator playersEventGen = new PlayersEventsGenerator(playersEvents);
        EventsGenerator playersStatesGen = new EventsGenerator(playersStates);

        try {
            GameSimulation.runGame(playersEventGen, s, playersStatesGen);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Checks that the bomb bonus has effect on the players
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void bonusBombsTest() throws URISyntaxException, IOException {
        //PlayerEvent coding: { Direction , isDropBomb }
        //Directions:
        //  1 : N
        //  2 : E
        //  3 : S
        //  4 : W
        //  isDropBomb:
        //  0 : false, otherwise : true
        int[][] eventsP1 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP2 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP3 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
        int[][] eventsP4 = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};
        
        // PlayerState coding: {lives, state, maxBombs, rangeBombs, xPos, yPos }
        
        int[][] outP1 = {
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS + 1, INIT_RANGE, POS_NW.neighbor(Direction.E).x(), POS_NW.neighbor(Direction.E).y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS + 1, INIT_RANGE, POS_NW.neighbor(Direction.E).x(), POS_NW.neighbor(Direction.E).y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS + 1, INIT_RANGE, POS_NW.neighbor(Direction.E).x(), POS_NW.neighbor(Direction.E).y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS + 1, INIT_RANGE, POS_NW.neighbor(Direction.E).x(), POS_NW.neighbor(Direction.E).y()},};
        int[][] outP2 = {
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_NE.x(), POS_NE.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_NE.x(), POS_NE.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_NE.x(), POS_NE.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_NE.x(), POS_NE.y()},};
        int[][] outP3 = {
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SE.x(), POS_SE.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SE.x(), POS_SE.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SE.x(), POS_SE.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SE.x(), POS_SE.y()},};
        int[][] outP4 = {
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SW.x(), POS_SW.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SW.x(), POS_SW.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SW.x(), POS_SW.y()},
                {INIT_LIVES, State.INVULNERABLE.ordinal(), INIT_BOMBS, INIT_RANGE, POS_SW.x(), POS_SW.y()},};

        // players Events
        EventSequence[] playersEvents = new EventSequence[4];
        playersEvents[0] = new EventSequence(players.get(0).id(), eventsP1, false);
        playersEvents[1] = new EventSequence(players.get(1).id(), eventsP2, false);
        playersEvents[2] = new EventSequence(players.get(2).id(), eventsP3, false);
        playersEvents[3] = new EventSequence(players.get(3).id(), eventsP4, false);
        
        // players states
        EventSequence[] playersStates = new EventSequence[4];
        playersStates[0] = new EventSequence(players.get(0).id(), outP1, true);
        playersStates[1] = new EventSequence(players.get(1).id(), outP2, true);
        playersStates[2] = new EventSequence(players.get(2).id(), outP3, true);
        playersStates[3] = new EventSequence(players.get(3).id(), outP4, true);
        
        
        // Game Setup (with a bonus next to player 1)
        Block __ = Block.FREE;
        Block XX = Block.INDESTRUCTIBLE_WALL;
        Block xx = Block.DESTRUCTIBLE_WALL;
        Block BB = Block.BONUS_BOMB;
        Board b = Board.ofQuadrantNWBlocksWalled(Arrays.asList(
                Arrays.asList(__, BB, __, xx, __, xx, __),
                Arrays.asList(__, XX, xx, XX, xx, XX, xx),
                Arrays.asList(__, xx, __, __, __, xx, __),
                Arrays.asList(xx, XX, __, XX, XX, XX, XX),
                Arrays.asList(__, xx, __, xx, __, __, __),
                Arrays.asList(xx, XX, xx, XX, xx, XX, __)));
        GameState s = new GameState(b, players);
        
        PlayersEventsGenerator playersEventGen = new PlayersEventsGenerator(playersEvents);
        EventsGenerator playersStatesGen = new EventsGenerator(playersStates);

        try {
            GameSimulation.runGame(playersEventGen, s, playersStatesGen);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
