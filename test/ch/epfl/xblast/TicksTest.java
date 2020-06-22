package ch.epfl.xblast;

import ch.epfl.xblast.Time;
import org.junit.Test;

import static ch.epfl.xblast.server.Ticks.*;
import static org.junit.Assert.assertEquals;

public class TicksTest {

    @Test
    public void trivialConstantsHaveCorrectValues() {
        assertEquals(20, TICKS_PER_SECOND);
        assertEquals(8, PLAYER_DYING_TICKS);
        assertEquals(64, PLAYER_INVULNERABLE_TICKS);
        assertEquals(100, BOMB_FUSE_TICKS);
        assertEquals(30, EXPLOSION_TICKS);
    }

    @Test
    public void composedConstantsHaveCorrectValues() {
        assertEquals(Time.NS_PER_S / TICKS_PER_SECOND, TICK_NANOSECOND_DURATION);
        assertEquals(2 * Time.S_PER_MIN * TICKS_PER_SECOND, TOTAL_TICKS);
        assertEquals(EXPLOSION_TICKS, WALL_CRUMBLING_TICKS);
        assertEquals(EXPLOSION_TICKS, BONUS_DISAPPEARING_TICKS);
    }
}
