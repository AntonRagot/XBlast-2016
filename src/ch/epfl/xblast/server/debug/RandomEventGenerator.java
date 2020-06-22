package ch.epfl.xblast.server.debug;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;

public final class RandomEventGenerator {
    private static final List<Optional<Direction>> possibleSpeeds =
            Arrays.asList(
                    Optional.empty(),
                    Optional.of(Direction.N),
                    Optional.of(Direction.E),
                    Optional.of(Direction.S),
                    Optional.of(Direction.W));

    private final Random rng;
    private final int speedChangeProb, bombProb;

    public RandomEventGenerator(long seed, int speedChangeProb, int bombProb) {
        this.rng = new Random(seed);
        this.speedChangeProb = speedChangeProb;
        this.bombProb = bombProb;
    }

    public Map<PlayerID, Optional<Direction>> randomSpeedChangeEvents() {
        Map<PlayerID, Optional<Direction>> events = new EnumMap<>(PlayerID.class);
        for (PlayerID pId: PlayerID.values()) {
            if (rng.nextInt(speedChangeProb) == 0)
                events.put(pId, possibleSpeeds.get(rng.nextInt(possibleSpeeds.size())));
        }
        return Collections.unmodifiableMap(events);
    }

    public Set<PlayerID> randomBombDropEvents() {
        Set<PlayerID> events = EnumSet.noneOf(PlayerID.class);
        for (PlayerID pID: PlayerID.values()) {
            if (rng.nextInt(bombProb) == 0)
                events.add(pID);
        }
        return Collections.unmodifiableSet(events);
    }
}