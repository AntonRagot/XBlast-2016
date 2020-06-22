package ch.epfl.xblast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import org.junit.Assert;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.Bomb;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;

public class GameStateTestUtils {

    private static final int SQ_CHECK_COUNT = 1000;

    public static <T> void xAssertEquals(Sq<T> expected, Sq<T> given, BiConsumer<T, T> equals) {
        for (int i = 0; i < SQ_CHECK_COUNT && !expected.isEmpty() && !given.isEmpty(); ++i) {
            equals.accept(expected.head(), given.head());
            expected = expected.tail();
            given = given.tail();
        }
        assertEquals(expected.isEmpty(), given.isEmpty());
    }

    public static void xAssertEquals(Board expected, Board given) {
        Cell.ROW_MAJOR_ORDER.forEach(c -> xAssertEquals(expected.blocksAt(c), given.blocksAt(c), Assert::assertEquals));
    }

    public static void xAssertEquals(Player.LifeState expected, Player.LifeState given) {
        assertEquals(expected.state(), given.state());
        assertEquals(expected.lives(), given.lives());
    }

    public static void xAssertEquals(Player.DirectedPosition expected, Player.DirectedPosition given) {
        assertEquals(expected.direction(), given.direction());
        assertEquals(expected.position(), given.position());
    }

    public static void xAssertEquals(Player expected, Player given) {
        assertEquals(expected.id(), given.id());
        xAssertEquals(expected.lifeStates(), given.lifeStates(), GameStateTestUtils::xAssertEquals);
        assertEquals(expected.lives(), given.lives());
        xAssertEquals(expected.directedPositions(), given.directedPositions(), GameStateTestUtils::xAssertEquals);
        assertEquals(expected.maxBombs(), given.maxBombs());
        assertEquals(expected.bombRange(), given.bombRange());
    }

    public static <T> void xAssertEquals(List<T> expected, List<T> given, BiConsumer<T, T> equals) {
        assertEquals(expected.size(), given.size());
        IntStream.range(0, expected.size()).forEach(i -> equals.accept(expected.get(i), given.get(i)));
    }

    public static <K, V> void xAssertEquals(Map<K, V> expected, Map<K, V> given, BiConsumer<V, V> equals) {
        assertEquals(expected.size(), given.size());
        expected.forEach((k, v) -> {
            assertTrue(given.containsKey(k)); // need key hashcode redefinition
            equals.accept(v, given.get(k));
        });
    }

    public static void xAssertEquals(Bomb expected, Bomb given) {
        assertEquals(expected.ownerId(), given.ownerId());
        assertEquals(expected.position(), given.position());
        xAssertEquals(expected.fuseLengths(), given.fuseLengths(), Assert::assertEquals);
        assertEquals(expected.range(), given.range());
    }

    public static void xAssertEquals(GameState expected, GameState given) {
        assertEquals(expected.ticks(), given.ticks());
        assertEquals(expected.winner(), given.winner());
        xAssertEquals(expected.board(), given.board());
        xAssertEquals(expected.players(), given.players(), GameStateTestUtils::xAssertEquals);
        xAssertEquals(expected.alivePlayers(), given.alivePlayers(), GameStateTestUtils::xAssertEquals);
        xAssertEquals(expected.bombedCells(), given.bombedCells(), GameStateTestUtils::xAssertEquals);
        assertTrue(expected.blastedCells().containsAll(given.blastedCells()));
    }

    public static void xAssertNotEquals(GameState expected, GameState given) {
        boolean failing = true;
        try {
            xAssertEquals(expected, given);
            failing = false;
        } catch (AssertionError ignored) {}
        if (! failing) {
            fail();
        }
    }

    public static Bomb bombDecreased(Bomb b) {
        return new Bomb(b.ownerId(), b.position(), b.fuseLength() - 1, b.range());
    }

    @SafeVarargs
    public static <T> Set<T> asSet(T... t) {
        return new HashSet<>(Arrays.asList(t));
    }

    public static <T> Set<T> addToSet(Set<T> to, Set<T> add) {
        Set<T> tmp = new HashSet<>(to);
        tmp.addAll(add);
        return Collections.unmodifiableSet(tmp);
    }


    public static <T> Set<T> removeFromSet(Set<T> from, Set<T> remove) {
        Set<T> tmp = new HashSet<>(from);
        tmp.removeAll(remove);
        return Collections.unmodifiableSet(tmp);
    }

}
