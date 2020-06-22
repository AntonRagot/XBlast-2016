package ch.epfl.xblast.server;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player.DirectedPosition;
import ch.epfl.xblast.server.Player.LifeState;
import ch.epfl.xblast.server.Player.LifeState.State;

public class PlayerTest {
    private static final int SUBDIVISIONS = 16;
    private static final int SUBCOLUMNS = SUBDIVISIONS * Cell.COLUMNS;
    private static final int SUBROWS = SUBDIVISIONS * Cell.ROWS;

    // Looping limit to check if a Sq is constant.
    // Currently set to the total ticks for a game as it doesn't slow
    // the tests too much.
    // Should be bigger than any number of player period ticks.
    private final int THRESHOLD = 2400;
    private int lives;
    private int maxBombs;
    private int newMaxBombs;
    private int bombRange;
    private int newBombRange;
    private PlayerID id;
    private Direction direction;
    private Sq<LifeState> lifeStates;
    private Sq<DirectedPosition> directedPos;
    private Cell cellPosition;
    private SubCell subCellPosition;
    private Player player;
    private Player player2; // Player made with constructor 2
    private Player deadPlayer;
    private List<State> states = Arrays.asList( State.INVULNERABLE, State.VULNERABLE,
                                                State.DYING, State.DEAD );

    /**
     *  Initial set up
     */
    @Before
    public void setUp() {

        lives = 2;
        maxBombs = 2;
        newMaxBombs = 3;
        bombRange = 2;
        newBombRange = 3;
        id = PlayerID.PLAYER_1;
        direction = Direction.S;
        cellPosition = new Cell(0, 0);
        subCellPosition = new SubCell(0, 0);

        lifeStates = Sq.constant(new LifeState(lives, LifeState.State.VULNERABLE));
        directedPos = DirectedPosition.stopped(new DirectedPosition(subCellPosition, direction));
        player = new Player(id, lifeStates, directedPos, maxBombs, bombRange);
        player2 = new Player(id, lives, cellPosition, maxBombs, bombRange);
        deadPlayer = new Player(id, 0, cellPosition, maxBombs, bombRange);
    }


    /**
     * Helper function to skip `LifeState` in a Sq while a predicate
     * is true. In case of constant Sq, signal it with an empty
     * Optional.
     *
     * By skipping elements, we don't penalize if the sequence was not
     * implemented with the right length and allows us to test what we
     * really want.
     *
     * @param lifeStates the Sq that will be skipped
     * @param skip the predicate: while it succeeds, we skip.
     * @return The skipped Sq of LifeState wrapped in a Optional or an
     *         empty Optional in case the Sq is thought to be constant
     */
    private Optional<Sq<LifeState>> skipSqWhile(Sq<LifeState> lifeStates, Predicate<State> skip) {
        Sq<LifeState> ls = lifeStates;

        int ticks = 0;  // used to avoid an infinite loop in case of a constant Sq
        while (ticks < THRESHOLD && ! ls.isEmpty() && skip.test(ls.head().state())) {
            ls = ls.tail();
            ++ticks;
        }

        // Sq is constant or the Sq was empty
        if (ticks >= THRESHOLD || ls.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(ls);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void lifeStateConstructorWithNegativeLivesThrowsException() {
        new LifeState(-1, State.VULNERABLE);
    }

    @Test(expected = NullPointerException.class)
    public void lifeStateConstructorWithNullStateThrowsException() {
        new LifeState(3, null);
    }

    @Test
    public void lifeStateCanBuildWithAllStates() {
        for (State state: states) {
            new LifeState(3, state);
        }
    }

    @Test
    public void lifeState_state_returnsCorrectState() {
        for (State state: states) {
            LifeState ls = new LifeState(3, state);
            assertEquals(state, ls.state());
        }
    }

    @Test
    public void lifeState_lives_returnsCorrectLifeCount() {
        List<Integer> allLives = Arrays.asList( 0, 1, 2, 3, 4 );
        for (Integer lives: allLives) {
            LifeState ls = new LifeState(lives, State.INVULNERABLE);
            assertEquals((int)lives, ls.lives());
        }
    }

    @Test
    public void canMoveWorksForRightStates() {
        for (State state: states) {
            LifeState ls = new LifeState(1, state);
            boolean expected = state == State.INVULNERABLE || state == State.VULNERABLE;
            assertEquals(expected, ls.canMove());
        }
    }

    @Test
    public void statesForNextLifeShouldHaveAPeriodOfDyingState() {
        Sq<LifeState> lifeStates = player.statesForNextLife();
        for (int tick = 0; tick < Ticks.PLAYER_DYING_TICKS; ++tick) {
            LifeState ls = lifeStates.head();
            assertEquals("State should be DYING, not " + ls.state(), State.DYING, ls.state());
            lifeStates = lifeStates.tail();
        }

        assertTrue("State should be different than DYING after PLAYER_DYING_DICKS",
                   lifeStates.head().state() != State.DYING);
    }

    @Test
    public void statesForNextLifeShouldHaveOneLessLifeAFTERDyingPeriod() {
        int livesAmount = 3;
        Player player = new Player(id, livesAmount, cellPosition, maxBombs, bombRange);
        Sq<LifeState> lifeStates = player.statesForNextLife();

        assertEquals("The DYING period should have the same amount of life than at the begining",
                   livesAmount, lifeStates.head().lives());

        // Skip DYING state
        Optional<Sq<LifeState>> maybeSkipped =
            skipSqWhile(lifeStates, (s) -> s == State.DYING);
        assertTrue("The sequence was a State.DYING constant when it shouldn't",
                   maybeSkipped.isPresent());
        lifeStates = maybeSkipped.get();

        assertEquals("After the dying period, lifeState should have one less life",
                     livesAmount - 1, lifeStates.head().lives());

    }

    @Test
    public void statesForNextLifeWithOneLifeShouldBeDeath() {
        Player player = new Player(id, 1, cellPosition, maxBombs, bombRange);
        Sq<LifeState> lifeStates = player.statesForNextLife();

        // Skip DYING state
        Optional<Sq<LifeState>> maybeSkipped =
            skipSqWhile(lifeStates, (s) -> s == State.DYING);
        assertTrue("The sequence was a State.DYING constant when it shouldn't.",
                   maybeSkipped.isPresent());
        lifeStates = maybeSkipped.get();

        // Check that the player stays dead: there is no invulnerability period
        for (int tick = 0; tick < THRESHOLD; ++tick) {
            assertEquals("Player should stay dead. Or pretend to be...",
                         State.DEAD, lifeStates.head().state());
            lifeStates = lifeStates.tail();
        }
    }

    @Test
    public void statesForNextLifeShouldHaveAnInvulnerablePeriod() {
        Player player = new Player(id, 3, cellPosition, maxBombs, bombRange);
        Sq<LifeState> lifeStates = player.statesForNextLife();

        // Skip DYING state
        Optional<Sq<LifeState>> maybeSkipped =
            skipSqWhile(lifeStates, (s) -> s == State.DYING);
        assertTrue("The sequence was a State.DYING constant when it shouldn't.",
                   maybeSkipped.isPresent());
        lifeStates = maybeSkipped.get();

        // Test for invulnerability period
        for (int tick = 0; tick < Ticks.PLAYER_INVULNERABLE_TICKS; ++tick) {
            assertEquals("State should be INVULNERABLE for tick 0 to " +
                         (Ticks.PLAYER_INVULNERABLE_TICKS-1) +
                         " but faded at tick " + tick,
                         State.INVULNERABLE, lifeStates.head().state());
            lifeStates = lifeStates.tail();
        }

        assertTrue("State should not be INVULNERABLE after the invulnerability period faded.",
                   lifeStates.head().state() != State.INVULNERABLE);
    }

    @Test
    public void statesForNextLifeShouldHaveAnVulnerablePeriodAfterInvulnerabilty() {
        Player player = new Player(id, 3, cellPosition, maxBombs, bombRange);
        Sq<LifeState> lifeStates = player.statesForNextLife();

        // Skip until the invulnerability period
        Optional<Sq<LifeState>> maybeSkipped =
            skipSqWhile(lifeStates, (s) -> s != State.INVULNERABLE);

        // Skip the invulnerability period
        maybeSkipped = maybeSkipped.flatMap(
            (lstates) -> skipSqWhile(lstates, (s) -> s == State.INVULNERABLE)
        );

        assertTrue("The LifeState does not have or does not escape the INVULNERABLE state",
                   maybeSkipped.isPresent());
        lifeStates = maybeSkipped.get();

        // Test for vulnerability period
        for (int tick = 0; tick < THRESHOLD; ++tick) {
            assertEquals("Player should be in a constant vulnerable state",
                         State.VULNERABLE, lifeStates.head().state());
            lifeStates = lifeStates.tail();
        }
    }

    @Test
    public void lifeStatesShouldReturnTheSameSequence() {
        // We have a special character that comes back from the deads
        Sq<LifeState> lifeStates = Sq.repeat(3, new LifeState(3, State.DYING))
            .concat(Sq.repeat(2, new LifeState(3, State.DEAD)))
            .concat(Sq.repeat(5, new LifeState(3, State.INVULNERABLE)));
        Player player = new Player(id, lifeStates, directedPos, maxBombs, bombRange);

        Sq<LifeState> playerLifeStates = player.lifeStates();
        // We know lifeStates is not infinite
        while (! lifeStates.isEmpty()) {
            assertEquals(lifeStates.head().state(), playerLifeStates.head().state());
            assertEquals(lifeStates.head().lives(), playerLifeStates.head().lives());
            lifeStates = lifeStates.tail();
            playerLifeStates = playerLifeStates.tail();
        }
    }

    @Test
    public void lifeStateReturnTheFirstStateOfTheSequence() {
        Sq<LifeState> lifeStates = Sq.repeat(3, new LifeState(3, State.DYING))
            .concat(Sq.repeat(2, new LifeState(3, State.DEAD)))
            .concat(Sq.repeat(5, new LifeState(3, State.INVULNERABLE)));

        // We know lifeStates is not infinite
        while (! lifeStates.isEmpty()) {
            LifeState playerLifeState = new Player(id, lifeStates, directedPos, maxBombs, bombRange).lifeState();
            assertEquals(lifeStates.head().state(), playerLifeState.state());
            assertEquals(lifeStates.head().lives(), playerLifeState.lives());
            lifeStates = lifeStates.tail();
        }
    }

    /*
     * Tests sur le constructeur principal
     */
    @Test(expected = NullPointerException.class)
    public void constructorWithNullPlayerIdThrowsException() {
        new Player(null, lifeStates, directedPos, maxBombs, bombRange);
    }

    @Test(expected = NullPointerException.class)
    public void constructorWithNullLifeStatesThrowsException() {
        new Player(id, null, directedPos, maxBombs, bombRange);
    }

    @Test(expected = NullPointerException.class)
    public void constructorWithNullDirectedPositionThrowsException() {
        new Player(id, lifeStates, null, maxBombs, bombRange);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNegativeMaxBombsThrowsException() {
        new Player(id, lifeStates, directedPos, -1, bombRange);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNegativeBombRangeThrowsException() {
        new Player(id, lifeStates, directedPos, maxBombs, -1);
    }

    /*
     * Mêmes tests sur le second constructeur
     */
    @Test(expected = NullPointerException.class)
    public void constructor2WithNullPlayerIdThrowsException() {
        new Player(null, lives, cellPosition, maxBombs, bombRange);
    }

    @Test(expected = NullPointerException.class)
    public void constructor2WithNullCellThrowsException() {
        new Player(id, lives, null, maxBombs, bombRange);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor2WithNegativeLivesThrowsException() {
        new Player(id, -1, cellPosition, maxBombs, bombRange);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor2WithNegativeMaxBombsThrowsException() {
        new Player(id, lives, cellPosition, -1, bombRange);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor2WithNegativeBombRangeThrowsException() {
        new Player(id, lives, cellPosition, maxBombs, -1);
    }

    @Test
    public void constructor2ReturnsPlayerInvulnerableForTicksConstantDurationFollowedByVulnerableStatesWhenLifeGreaterThanZero() {

        Sq<LifeState> invulnerable = player2.lifeStates().takeWhile(s -> (s.state().equals(State.INVULNERABLE)) && (s.lives() == lives));
        Sq<LifeState> vulnerable = player2.lifeStates().dropWhile(s -> (s.state().equals(State.INVULNERABLE)) && (s.lives() == lives));

        for (int i = 0; i < Ticks.PLAYER_INVULNERABLE_TICKS; ++i) {
            assertFalse(invulnerable.isEmpty());
            invulnerable = invulnerable.tail();
        }

        for (int i = 0; i < THRESHOLD; ++i) {
            assertEquals(State.VULNERABLE, vulnerable.head().state());
            assertEquals(lives, vulnerable.head().lives());
            vulnerable = vulnerable.tail();
        }

    }

    @Test
    public void constructor2ReturnsConstantDeadPlayerWhenLifeEqualToZero() {
        Sq<LifeState> dead = deadPlayer.lifeStates();

        for (int i = 0; i < THRESHOLD; ++i) {
            assertEquals(State.DEAD, dead.head().state());
            assertEquals(0, dead.head().lives());
            dead = dead.tail();
        }
    }

    @Test
    public void constructor2HasConstantDirectedPositionDirectedSouthLocatedAtCentralSubCellOfPosition() {
        Sq<DirectedPosition> dp = player2.directedPositions();

        for (int i = 0; i < THRESHOLD; ++i) {
            assertEquals(Direction.S, dp.head().direction());
            assertEquals(SubCell.centralSubCellOf(cellPosition), dp.head().position());
            dp = dp.tail();
        }
    }


    /*
     * Tests sur les méthods publiques de Player
     */

    @Test
    public void idReturnsPlayerID() {
        assertEquals(PlayerID.PLAYER_1, player.id());
    }

    @Test
    public void livesReturnsPlayerLives() {
        assertEquals(lives, player.lives());
    }

    @Test
    public void isAliveReturnsTrueWhenLifeIsGreaterThanZero() {
        assertTrue(player.isAlive());
    }

    @Test
    public void isAliveReturnsFalseWhenLifeIsEqualToZero() {
        assertFalse(deadPlayer.isAlive());
    }

    @Test
    public void directedPositionsReturnsDirectedPositions() {
        assertEquals(directedPos, player.directedPositions());
    }

    @Test
    public void positionReturnsPosition() {
        assertEquals(subCellPosition, player.position());
    }

    @Test
    public void directionReturnsDirection() {
        assertEquals(direction, player.direction());
    }

    @Test
    public void maxBombsReturnsMaxBombs() {
        assertEquals(maxBombs, player.maxBombs());
    }

    @Test
    public void withMaxBombsReturnsNewIdenticalPlayerWithNewMaxBombs() {
        assertEquals(newMaxBombs, player.withMaxBombs(newMaxBombs).maxBombs());
    }

    @Test
    public void bombRangeReturnsbombRange() {
        assertEquals(bombRange, player.bombRange());
    }

    @Test
    public void withBombRangeReturnsNewIdenticalPlayerWithNewBombRange() {
        assertEquals(newBombRange, player.withBombRange(newBombRange).bombRange());
    }

    @Test
    public void newBombReturnsBombWithCorrectParameters() {
        Bomb bomb = player.newBomb();
        assertEquals(id, bomb.ownerId());
        assertEquals(cellPosition, bomb.position());
        assertEquals(Ticks.BOMB_FUSE_TICKS, bomb.fuseLength());
        assertEquals(bombRange, bomb.range());
    }

    @Test
    public void newDirectedPositionWithCorrectParameters() {
        DirectedPosition pos = new DirectedPosition(new SubCell(12, 7), Direction.E);

        assertEquals("Coord X is correct", 12, pos.position().x());
        assertEquals("Coord Y is correct",  7, pos.position().y());
        assertEquals("Direction is correct", Direction.E, pos.direction());
    }

    @Test(expected=NullPointerException.class)
    public void newDirectedPositionFailWithoutCell() {
        new DirectedPosition(null, Direction.E);
    }

    @Test(expected=NullPointerException.class)
    public void newDirectedPositionFailWithoutDirection() {
        new DirectedPosition(new SubCell(12, 7), null);
    }

    @Test
    public void DirectedPosition_stoppedReturnsConstantPosition() {
        DirectedPosition startingPos = new DirectedPosition(new SubCell(12, 7), Direction.E);
        Sq<DirectedPosition> stopped = DirectedPosition.stopped(startingPos);

        for (int i = 0; i <= THRESHOLD; i++) {
            if (stopped.isEmpty())
                fail(".stopped() must return an inifinite Sq, thus it cannot be empty");

            assertEquals("Stopped still on the same position", startingPos, stopped.head());
            stopped = stopped.tail();
        }
    }

    private void assertIsMoving(DirectedPosition startingPos) {
        int dx = 0, dy = 0;
        int ex = startingPos.position().x(), ey = startingPos.position().y();
        Sq<DirectedPosition> moving = DirectedPosition.moving(startingPos);

        switch (startingPos.direction()) {
            case N: dy = -1; break;
            case E: dx =  1; break;
            case S: dy =  1; break;
            case W: dx = -1; break;
        }

        for (int i = 0; i <= THRESHOLD; i++) {
            if (moving.isEmpty())
                fail(".moving() must return an inifinite Sq, thus it cannot be empty");

            DirectedPosition pos = moving.head();

            assertEquals("Position X evolved correctly", ex, pos.position().x());
            assertEquals("Position Y evolved correctly", ey, pos.position().y());
            assertEquals("Direction is still the same", startingPos.direction(), pos.direction());

            ex = Math.floorMod(ex + dx, SUBCOLUMNS);
            ey = Math.floorMod(ey + dy, SUBROWS);
            moving = moving.tail();
        }
    }

    @Test
    public void DirectedPosition_moving() {
        SubCell start = new SubCell(12, 7);

        assertIsMoving(new DirectedPosition(start, Direction.N));
        assertIsMoving(new DirectedPosition(start, Direction.E));
        assertIsMoving(new DirectedPosition(start, Direction.S));
        assertIsMoving(new DirectedPosition(start, Direction.W));
    }

    @Test
    public void DirectedPosition_withPosition() {
        DirectedPosition pos = new DirectedPosition(new SubCell(12, 7), Direction.S);
        DirectedPosition newPos = pos.withPosition(new SubCell(9, 14));

        assertEquals("Position X is correct",  9, newPos.position().x());
        assertEquals("Position Y is correct", 14, newPos.position().y());
        assertEquals("Direction is correct", Direction.S, newPos.direction());
    }

    @Test
    public void DirectedPosition_withDirection() {
        DirectedPosition pos = new DirectedPosition(new SubCell(12, 7), Direction.S);
        DirectedPosition newPos = pos.withDirection(Direction.E);

        assertEquals("Position X is correct", 12, newPos.position().x());
        assertEquals("Position Y is correct",  7, newPos.position().y());
        assertEquals("Direction is correct", Direction.E, newPos.direction());
    }
}
