package ch.epfl.xblast.etape6.events;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import ch.epfl.xblast.Direction;

public class PlayerEvent extends Event {
    public final static int length = 2;
    private Optional<Direction> direction;
    private boolean dropBomb;
    private final static int directionIdx = 0;
    private final static int dropBombIdx = 1;
    private static final List<Optional<Direction>> possibleSpeeds = Arrays.asList(Optional.empty(),
            Optional.of(Direction.N), Optional.of(Direction.E), Optional.of(Direction.S), Optional.of(Direction.W));

    public PlayerEvent(int direction, int dropBomb) {
        if (direction == -1)
            this.direction = null;
        else {
            this.direction = possibleSpeeds.get(direction);
        }

        this.dropBomb = (dropBomb != 0);
    }

    public PlayerEvent(int[] event) {

        this(event[directionIdx], event[dropBombIdx]);

    }

    public boolean hasChangedSpeed() {

        return (direction != null);

    }

    public Optional<Direction> getDirection() {
        return direction;
    }

    public boolean isDropBomb() {
        return dropBomb;
    }

}
