package ch.epfl.xblast.etape6;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.etape6.events.EventSequence;
import ch.epfl.xblast.etape6.generator.EventsGenerator;
import ch.epfl.xblast.etape6.generator.PlayersEventsGenerator;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Player.LifeState.State;
import ch.epfl.xblast.server.Ticks;

public class LifeTest {

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
        players = GameSimulation.createPlayers(INIT_LIVES, INIT_BOMBS, INIT_RANGE, POS_NW, POS_NE, POS_SE, POS_SW);
    }

    /**
     * Checks that a player's number of life and state are affected when touched by an explosion
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void lifeLostTest() throws URISyntaxException, IOException {
        //PlayerEvent coding: { Direction , isDropBomb }
        //Directions:
        //  1 : N
        //  2 : E
        //  3 : S
        //  4 : W
        //  isDropBomb:
        //  0 : false, otherwise : true
        
        // Bomb fuse is 100 ticks by default and player is in a dying state for 8 ticks. We also add a tick for bomb placement, and a tick for life removal.
        int totalTime = Ticks.BOMB_FUSE_TICKS + Ticks.PLAYER_DYING_TICKS + 1;
        int[][] eventsP1 = new int[totalTime][2];
        eventsP1[0][0] = 0;
        eventsP1[0][1] = 1; //drop a bomb
        for(int i = 1; i < eventsP1.length; i++) {
            eventsP1[i][0] = 0;
            eventsP1[i][1] = 0;
        }
        
        int[][] eventsP2 = new int[totalTime][2];
        int[][] eventsP3 = new int[totalTime][2];
        int[][] eventsP4 = new int[totalTime][2];
        for(int i = 0; i < eventsP2.length; i ++) {
            eventsP2[i][0] = eventsP3[i][0] = eventsP4[i][0] = 0;
            eventsP2[i][1] = eventsP3[i][1] = eventsP4[i][1] = 0;
        }
        
        // PlayerState coding: {lives, state, maxBombs, rangeBombs, xPos, yPos }
        
        //TODO: utiliser conditions ternaires
        int[][] outP1 = new int[totalTime][6];
        int[][] outP2 = new int[totalTime][6];
        int[][] outP3 = new int[totalTime][6];
        int[][] outP4 = new int[totalTime][6];
        for(int i = 0; i < Ticks.PLAYER_INVULNERABLE_TICKS - 1; i ++) {
            outP1[i][0] = outP2[i][0] = outP3[i][0] = outP4[i][0] = INIT_LIVES;
            outP1[i][1] = outP2[i][1] = outP3[i][1] = outP4[i][1] = State.INVULNERABLE.ordinal();
            outP1[i][2] = outP2[i][2] = outP3[i][2] = outP4[i][2] = INIT_BOMBS;
            outP1[i][3] = outP2[i][3] = outP3[i][3] = outP4[i][3] = INIT_RANGE;
            outP1[i][4] = POS_NW.x();
            outP2[i][4] = POS_NE.x();
            outP3[i][4] = POS_SE.x();
            outP4[i][4] = POS_SW.x();
            outP1[i][5] = POS_NW.y();
            outP2[i][5] = POS_NE.y();
            outP3[i][5] = POS_SE.y();
            outP4[i][5] = POS_SW.y();
        }
        for(int i = Ticks.PLAYER_INVULNERABLE_TICKS - 1; i < Ticks.BOMB_FUSE_TICKS; i ++) {
            outP1[i][0] = outP2[i][0] = outP3[i][0] = outP4[i][0] = INIT_LIVES;
            outP1[i][1] = outP2[i][1] = outP3[i][1] = outP4[i][1] = State.VULNERABLE.ordinal();
            outP1[i][2] = outP2[i][2] = outP3[i][2] = outP4[i][2] = INIT_BOMBS;
            outP1[i][3] = outP2[i][3] = outP3[i][3] = outP4[i][3] = INIT_RANGE;
            outP1[i][4] = POS_NW.x();
            outP2[i][4] = POS_NE.x();
            outP3[i][4] = POS_SE.x();
            outP4[i][4] = POS_SW.x();
            outP1[i][5] = POS_NW.y();
            outP2[i][5] = POS_NE.y();
            outP3[i][5] = POS_SE.y();
            outP4[i][5] = POS_SW.y();
        }
        for(int i = Ticks.BOMB_FUSE_TICKS; i < totalTime - 1; i ++) {
            outP1[i][0] = outP2[i][0] = outP3[i][0] = outP4[i][0] = INIT_LIVES;
            outP1[i][1] = State.DYING.ordinal();
            outP2[i][1] = outP3[i][1] = outP4[i][1] = State.VULNERABLE.ordinal();
            outP1[i][2] = outP2[i][2] = outP3[i][2] = outP4[i][2] = INIT_BOMBS;
            outP1[i][3] = outP2[i][3] = outP3[i][3] = outP4[i][3] = INIT_RANGE;
            outP1[i][4] = POS_NW.x();
            outP2[i][4] = POS_NE.x();
            outP3[i][4] = POS_SE.x();
            outP4[i][4] = POS_SW.x();
            outP1[i][5] = POS_NW.y();
            outP2[i][5] = POS_NE.y();
            outP3[i][5] = POS_SE.y();
            outP4[i][5] = POS_SW.y();
        }
        outP1[totalTime - 1][0] = INIT_LIVES - 1;
        outP2[totalTime - 1][0] = outP3[totalTime - 1][0] = outP4[totalTime - 1][0] = INIT_LIVES;
        outP1[totalTime - 1][1] = State.INVULNERABLE.ordinal();
        outP2[totalTime - 1][1] = outP3[totalTime - 1][1] = outP4[totalTime - 1][1] = State.VULNERABLE.ordinal();
        outP1[totalTime - 1][2] = outP2[totalTime - 1][2] = outP3[totalTime - 1][2] = outP4[totalTime - 1][2] = INIT_BOMBS;
        outP1[totalTime - 1][3] = outP2[totalTime - 1][3] = outP3[totalTime - 1][3] = outP4[totalTime - 1][3] = INIT_RANGE;
        outP1[totalTime - 1][4] = POS_NW.x();
        outP2[totalTime - 1][4] = POS_NE.x();
        outP3[totalTime - 1][4] = POS_SE.x();
        outP4[totalTime - 1][4] = POS_SW.x();
        outP1[totalTime - 1][5] = POS_NW.y();
        outP2[totalTime - 1][5] = POS_NE.y();
        outP3[totalTime - 1][5] = POS_SE.y();
        outP4[totalTime - 1][5] = POS_SW.y();

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
        Board b = Board.ofQuadrantNWBlocksWalled(Arrays.asList(
                Arrays.asList(__, __, __, xx, __, xx, __),
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
