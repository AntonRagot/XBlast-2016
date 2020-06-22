package ch.epfl.xblast;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.server.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.Assert.*;

public class TestEtape4 {

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

    private static GameState createGameState(int tick) {
        List<Player> players = new ArrayList<>(createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        return new GameState(tick,
                createBoard(),
                players,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }

    @Test
    public void testApplyBombBonus() {
        Player p = new Player(PlayerID.PLAYER_1, 3, new Cell(1, 1), 2, 3);
        int oldBombs = p.maxBombs();
        Bonus b = Bonus.INC_BOMB;

        p = b.applyTo(p);
        assertEquals("Bomb bonus should increase maxBombs by one", oldBombs+1, p.maxBombs());
    }

    @Test
    public void testMaxBomb() {
        Player p = new Player(PlayerID.PLAYER_1, 3, new Cell(1, 1), 2, 3);

        Bonus b = Bonus.INC_BOMB;

        p = b.applyTo(p);
        for(int i = 0; i < 10; i++)  {
            p = b.applyTo(p);
        }

        assertEquals("Max bombs can't be greater than 9", 9, p.maxBombs());
    }

    @Test
    public void testApplyRangeBonus() {
        Player p = new Player(PlayerID.PLAYER_1, 3, new Cell(1, 1), 2, 3);
        int oldRange = p.bombRange();
        Bonus b = Bonus.INC_RANGE;

        p = b.applyTo(p);
        assertEquals("Range bonus should increase range by one", oldRange+1, p.bombRange());
    }

    @Test
    public void testMaxRange() {
        Player p = new Player(PlayerID.PLAYER_1, 3, new Cell(1, 1), 2, 3);
        Bonus b = Bonus.INC_RANGE;

        p = b.applyTo(p);
        for(int i = 0; i < 10; i++)  {
            p = b.applyTo(p);
        }

        assertEquals("Max range can't be greater than 9", 9, p.bombRange());
    }

    @Test
    public void testIsBonus() {
        for(Block block : Block.values()) {
            switch(block) {
                case BONUS_BOMB:
                case BONUS_RANGE:
                    assertTrue("isBonus on bonus should be true", block.isBonus());
                    break;
                default:
                    assertFalse("isBonus on non bonus should be false", block.isBonus());

            }
        }
    }

    /*
    // Team 2 is doing those tests
    @Test(expected = NoSuchElementException.class)
    public void testNonAssociatedBonus() {

    }

    public void testAssociatedBonus() {

    }

    @Test
    public void testCanHostPlayerBonus() {

    }
     */

    @Test
    public void testTimeEnum() {
        assertEquals(Time.S_PER_MIN, 60);
        assertEquals(Time.MS_PER_S, 1_000);
        assertEquals(Time.US_PER_S, 1_000 * 1_000);
        assertEquals(Time.NS_PER_S, 1_000 * 1_000 * 1_000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGameStateNegativeTick() {
        new GameState(-1,
                createBoard(),
                createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }

    private void constructGameState(List<Player> players) {
        new GameState(0,
                createBoard(),
                players,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGameStateLess4Players() {
        List<Player> p = new ArrayList<>(createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        p.remove(0);
        constructGameState(p);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGameStateGreater4Players() {
        List<Player> p = new ArrayList<>(createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        p.add(new Player(PlayerID.PLAYER_1, 3, new Cell(1, 1), 2, 3));
        constructGameState(p);
    }

    @Test(expected = NullPointerException.class)
    public void testGameStateNullBoard() {
        List<Player> p = new ArrayList<>(createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        new GameState(0,
                null,
                p,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }

    @Test(expected = NullPointerException.class)
    public void testGameStateNullPlayers() {
        new GameState(0,
                createBoard(),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }

    @Test(expected = NullPointerException.class)
    public void testGameStateNullBombs() {
        List<Player> p = new ArrayList<>(createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        new GameState(0,
                createBoard(),
                p,
                null,
                new ArrayList<>(),
                new ArrayList<>());
    }

    @Test(expected = NullPointerException.class)
    public void testGameStateNullExplosions() {
        List<Player> p = new ArrayList<>(createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        new GameState(0,
                createBoard(),
                p,
                new ArrayList<>(),
                null,
                new ArrayList<>());
    }

    @Test(expected = NullPointerException.class)
    public void testGameStateNullBlasts() {
        List<Player> p = new ArrayList<>(createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        new GameState(0,
                createBoard(),
                p,
                new ArrayList<>(),
                new ArrayList<>(),
                null);
    }

    // GameState methods
    @Test
    public void testIsGameOverDead() {
        List<Player> players = new ArrayList<>(createPlayers(0, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        players.remove(0);
        players.add(new Player(PlayerID.PLAYER_1, 3, new Cell(1, 1), 2, 3));
        GameState game = new GameState(0,
                createBoard(),
                players,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
        assertTrue("Game is over, as three players are dead", game.isGameOver());
    }

    @Test
    public void testIsGameOverTime() {
        GameState game = createGameState(Ticks.TOTAL_TICKS+1);
        assertTrue("Game is over, because of timeout", game.isGameOver());
    }

    @Test
    public void testRemainingTime() {
        for(int i = 0; i < Ticks.TOTAL_TICKS; i += 10) {
            GameState state = createGameState(i);
            assertEquals((double)(Ticks.TOTAL_TICKS-i)/Ticks.TICKS_PER_SECOND, state.remainingTime(), 1);
        }
    }

    @Test
    public void testTicks() {
        for(int i = 0; i < Ticks.TOTAL_TICKS; i += 10) {
            GameState state = createGameState(i);
            assertEquals("Ticks should be equals", i, state.ticks());
        }
    }

    @Test
    public void testWinner() {
        List<Player> players = new ArrayList<>(createPlayers(0, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        players.remove(0);
        Player winner = new Player(PlayerID.PLAYER_1, 3, new Cell(1, 1), 2, 3);
        players.add(winner);
        GameState game = new GameState(0,
                createBoard(),
                players,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
        assertTrue("Option should not be empty", game.winner().isPresent());
        assertEquals("Winner should be alive", winner.id(), game.winner().get());
    }

    @Test
    public void testPlayers() {
        GameState game = createGameState(0);
        for (int i = 0; i < 4; i++) {
            Player p = game.players().get(i);
            assertEquals("Player ID should be equals", PlayerID.values()[i], p.id());
            assertEquals("Player should be alive", 3, p.lives());
        }
    }

    @Test
    public void testAlivePlayer() {
        List<Player> players = new ArrayList<>(createPlayers(0, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        players.remove(0);
        Player winner = new Player(PlayerID.PLAYER_1, 3, new Cell(1, 1), 2, 3);
        players.add(winner);
        GameState game = new GameState(0,
                createBoard(),
                players,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
        assertEquals("Only one remaining", 1, game.alivePlayers().size());
        assertEquals("ID of the remaining is correct", PlayerID.PLAYER_1,game.alivePlayers().get(0).id());
    }

    /* Thx to group 5 for those methods */
    private static final int SQ_CHECK_COUNT = 1000;
    private static <T> void xAssertEquals(Sq<T> expected, Sq<T> given, BiConsumer<T, T> equals) {
        for (int i = 0; i < SQ_CHECK_COUNT && !expected.isEmpty() && !given.isEmpty(); ++i) {
            equals.accept(expected.head(), given.head());
            expected = expected.tail();
            given = given.tail();
        }
        assertEquals(expected.isEmpty(), given.isEmpty());
    }
    private static void xAssertEquals(Board expected, Board given) {
        Cell.ROW_MAJOR_ORDER.forEach(c -> xAssertEquals(expected.blocksAt(c), given.blocksAt(c), Assert::assertEquals));
    }

    @Test
    public void testBoard() {
        GameState state = createGameState(0);
        xAssertEquals(createBoard(), state.board());
    }

}
