package ch.epfl.xblast.etape6;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.etape6.events.EventSequence;
import ch.epfl.xblast.etape6.generator.EventsGenerator;
import ch.epfl.xblast.etape6.generator.PlayersEventsGenerator;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Player.LifeState.State;

public class DirectionEvents {

    private static final Cell POS_NW = new Cell(1, 1);
    private static final Cell POS_NE = new Cell(-2, 1);
    private static final Cell POS_SE = new Cell(-2, -2);
    private static final Cell POS_SW = new Cell(1, -2);
    private static List<Player> players = null;
    
    
    @BeforeClass 
    public static void setUpClass() {            
        players = GameSimulation.createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW);
    }
    
    @Test
    public void testDoNothing2Iterations() {
     

        //Signaled events
        //
        //PlayerEvent coding: { Direction , isDropBomb }
        //Directions:
        // -1 : Nothing
        //  0 : Stop
        //  1 : N
        //  2 : E
        //  3 : S
        //  4 : W
        //  isDropBomb:
        //  0 : false, otherwise : true
        
        int[][] eventsP1 = { {-1, 0}, {-1, 0}};

        int[][] eventsP2 = { {-1, 0}, {-1, 0}};
        int[][] eventsP3 = { {-1, 0}, {-1, 0}};
        int[][] eventsP4 = { {-1, 0}, {-1, 0}};
        // console events ( output player states)
        // PlayerState coding: {lives, state, maxBombs, rangeBombs, xPos, yPos }
       
        int[][] outP1 = {
            {3, State.INVULNERABLE.ordinal(), 2, 3, 1, 1},
            {3, State.INVULNERABLE.ordinal(), 2, 3, 1, 1},};
        int[][] outP2 = {
            {3, State.INVULNERABLE.ordinal(), 2, 3, 13, 1},
            {3, State.INVULNERABLE.ordinal(), 2, 3, 13, 1},};
        int[][] outP3 = {
            {3, State.INVULNERABLE.ordinal(), 2, 3, 13, 11},
            {3, State.INVULNERABLE.ordinal(), 2, 3, 13, 11},};
        int[][] outP4 = {
            {3, State.INVULNERABLE.ordinal(), 2, 3, 1, 11},
            {3, State.INVULNERABLE.ordinal(), 2, 3, 1, 11},};

        // players Events
        EventSequence[] playersEvents = new EventSequence[4];
        playersEvents[0] = new EventSequence(players.get(0).id(), eventsP1, false);
        playersEvents[1] = new EventSequence(players.get(1).id(), eventsP2, false);
        playersEvents[2] = new EventSequence(players.get(2).id(), eventsP3, false);
        playersEvents[3] = new EventSequence(players.get(3).id(), eventsP4, false);
        // expected output players states
        EventSequence[] playersStates = new EventSequence[4];
        playersStates[0] = new EventSequence(players.get(0).id(), outP1, true);
        playersStates[1] = new EventSequence(players.get(1).id(), outP2, true);
        playersStates[2] = new EventSequence(players.get(2).id(), outP3, true);
        playersStates[3] = new EventSequence(players.get(3).id(), outP4, true);

        // Game Setup
        GameState s = new GameState(GameSimulation.createBoard(), players);

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
