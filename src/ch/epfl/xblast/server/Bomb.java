package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;

public final class Bomb {
    private final PlayerID ownerID;
    private final Cell position;
    private final Sq<Integer> fuseLengths;
    private final int range;

    /**
     * Main constructor for the class bomb.Constructs a new Bomb given an
     * PlayerID, the Cell where it is positioned, a sequence of Integers
     * representing different fuse lengths and the range of the bombs. Checks if
     * the parameters are null or if the sequence is empty.
     * 
     * @param ownerId of the bomb
     * @param position of the bomb
     * @param fuseLengths of the bomb
     * @param range of the bomb
     * @throws IllegalArgumentException if the given fuse length is empty
     * @throws NullPointerException is any given parameter is null
     */
    public Bomb(PlayerID ownerId, Cell position, Sq<Integer> fuseLengths,
            int range) {
        this.ownerID = Objects.requireNonNull(ownerId);
        this.position = Objects.requireNonNull(position);
        this.range = ArgumentChecker.requireNonNegative(range);
        if (fuseLengths.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            this.fuseLengths = Objects.requireNonNull(fuseLengths);
        }
    }

    /**
     * Secondary constructor of the class Bomb. It passes the given parameters
     * to the main constructor, after constructing a sequence representing the
     * fuse lengths given an initial length.
     * 
     * @param ownerId
     * @param position
     * @param fuseLength
     * @param range
     */
    public Bomb(PlayerID ownerId, Cell position, int fuseLength, int range) {
        this(ownerId, position,
                Sq.iterate(fuseLength, l -> l - 1).limit(fuseLength), range);
    }

    /**
     * Returns the PlayerID of the Bomb.
     * 
     * @return the id of the owner of the bomb
     */
    public PlayerID ownerId() {
        return ownerID;
    }

    /**
     * Returns the Cell on which the Bomb is placed.
     * 
     * @return Cell representing the position of the Bomb
     */
    public Cell position() {
        return position;
    }

    /**
     * Returns a sequence of Integers representing current and future fuse
     * lengths.
     * 
     * @return sequence of Integers
     */
    public Sq<Integer> fuseLengths() {
        return fuseLengths;
    }

    /**
     * Returns the current fuse length, which is the head (or first element) of
     * the sequence.
     * 
     * @return an Integer representing the current fuse length
     */
    public int fuseLength() {
        return fuseLengths().head();
    }

    /**
     * Returns an int representing the range of the Bomb.
     * 
     * @return the range of the bomb
     */
    public int range() {
        return range;
    }

    /**
     * Method which constructs the bomb as a List<Sq<Sq<Cell>>>. This list has
     * four sequences (one for each direction), and each sequence represents an
     * arm of the explosion.
     * 
     * @return List<Sq<Sq<Cell>>> representing the explosion in four directions
     */
    public List<Sq<Sq<Cell>>> explosion() {
        List<Sq<Sq<Cell>>> explosion = new ArrayList<Sq<Sq<Cell>>>();
        for (Direction direction : Direction.values()) {
            explosion.add(explosionArmTowards(direction));
        }
        return Collections.unmodifiableList(new ArrayList<>(explosion));
    }

    /**
     * Constructs a sequence of Cells corresponding to the evolution (sequence
     * of occupied Cells) of the explosion in a given direction.
     * 
     * @param dir
     * @return One arm of the explosion in the given direction
     */
    public Sq<Sq<Cell>> explosionArmTowards(Direction dir) {
        return Sq.repeat(Ticks.EXPLOSION_TICKS,
                Sq.iterate(position(), c -> c.neighbor(dir)).limit(range));
    }
}