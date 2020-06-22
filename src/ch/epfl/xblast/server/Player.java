package ch.epfl.xblast.server;

import java.util.Objects;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;

public final class Player {
    private final PlayerID id;
    private final Sq<LifeState> lifeStates;
    private final Sq<DirectedPosition> directedPos;
    private final int maxBombs;
    private final int bombRange;
    
    /**
     * Main constructor of the class Player. Constructs a new Player given an
     * id, a sequence of lifeStates, a sequence of DirectedPositions, a maximum
     * number of bombs and the range of the bombs.
     * 
     * @param id
     * @param lifeStates
     * @param directedPos
     * @param maxBombs
     * @param bombRange
     * @throws IllegalArgumentException if the bomb range or the max bomb is negative
     * @throws NullPointerException if the id, the lifeState or the directedPosition is null
     */
    public Player(PlayerID id, Sq<LifeState> lifeStates,
            Sq<DirectedPosition> directedPos, int maxBombs, int bombRange) 
            throws IllegalArgumentException, NullPointerException {
        this.id = Objects.requireNonNull(id);
        this.lifeStates = Objects.requireNonNull(lifeStates);
        this.directedPos = Objects.requireNonNull(directedPos);
        this.maxBombs = ArgumentChecker.requireNonNegative(maxBombs);
        this.bombRange = ArgumentChecker.requireNonNegative(bombRange);
    }

    /**
     * Secondary constructor of the class Player. Besides the parameters
     * described in the first constructor, it constructs the sequences of
     * lifeStates (using the method creationSq), and of DirectedPositions. These
     * parameters are then used by the main constructor.
     * 
     * @param id
     * @param lives
     * @param position
     * @param maxBombs
     * @param bombRange
     */
    public Player(PlayerID id, int lives, Cell position, int maxBombs,
            int bombRange) {
        this(id, creationSq(lives),
                DirectedPosition.stopped(new DirectedPosition(
                        SubCell.centralSubCellOf(position), Direction.S)),
                maxBombs, bombRange);
    }

    /**
     * 
     * @author Andra Bisca (257362)
     * @author Anton Ragot (258154)
     *
     */
    public final static class LifeState {
        private int lives;
        private final State state;
        public enum State {
            //Enumeration of all possible states.
            INVULNERABLE, VULNERABLE, DYING, DEAD;
        }
        
        /**
         * Constructor for the class LifeState. Constructs a new LifeState given
         * a number of lives and a State.
         * 
         * @param lives
         * @param state
         */
        public LifeState(int lives, State state) {
            this.lives = ArgumentChecker.requireNonNegative(lives);
            this.state = Objects.requireNonNull(state);
        }
        
        /**
         * Method which returns the number of lives associated to the LifeState.
         * 
         * @return int representing the lives associated with the current
         *         LifeState
         */
        public int lives() {
            return this.lives;
        }
        
        /**
         * Method that returns the state of the LifeState.
         * 
         * @return State of the current LifeState
         */
        public State state() {
            return this.state;
        }
        
        /**
         * Method that returns a boolean which is true if the player can move,
         * in which case he must be either vulnerable or invulnerable.
         * 
         * @return boolean
         */
        public boolean canMove() {
            return (this.state == State.INVULNERABLE
                        || this.state == State.VULNERABLE); 
        }
    }

    /**
     * 
     * @author Andra Bisca (257362)
     * @author Anton Ragot (258154)
     *
     */
    public final static class DirectedPosition {
        private final SubCell position;
        private final Direction direction;

        /**
         * Constructor for the class DirectedPosition. Creates a new
         * DirectedPosition given the current position (current SubCell) and the
         * Direction which we are currently looking at. Checks if the given
         * parameters are null.
         * 
         * @param position
         * @param direction
         */
        public DirectedPosition(SubCell position, Direction direction) {
            this.position = Objects.requireNonNull(position);
            this.direction = Objects.requireNonNull(direction);
        }

        /**
         * Method which returns an infinite sequence of DirectedPostions (the
         * given DirectedPosition repeated an infinite amount of times)
         * representing a player which has stopped moving.
         * 
         * @param p
         * @return Sq<DirectedPosition> representing a non-moving player
         */
        public static Sq<DirectedPosition> stopped(DirectedPosition p) {
            return Sq.constant(p);
        }

        /**
         * Method that returns an infinite sequence of DirectedPositions,
         * representing a moving player. The first element of the sequence is
         * the given DirectedPosition, followed by the DirectedPositions
         * corresponding to the neighboring SubCell (the next position) of the
         * current SubCell (current Position).
         * 
         * @param p
         * @return Sq<DirectedPosition> representing a moving player
         */
        public static Sq<DirectedPosition> moving(DirectedPosition p) {
            return Sq.iterate(p,
                    x -> x.withPosition(x.position().neighbor(x.direction())));
        }

        /**
         * Method that returns the current position.
         * 
         * @return SubCell representing the position
         */
        public SubCell position() {
            return this.position;
        }

        /**
         * Method that constructs a new DirectedPosition with the same direction
         * as the current one, but with the new given position (SubCell).
         * 
         * @param newPosition
         * @return DirectedPostion
         */
        public DirectedPosition withPosition(SubCell newPosition) {
            return new DirectedPosition(newPosition, direction);
        }

        /**
         * Method which returns the current direction.
         * 
         * @return Direction
         */
        public Direction direction() {
            return this.direction;
        }

        /**
         * Method that constructs a new DirectedPosition with the same postion
         * as the current one, but with the new given direction.
         * 
         * @param newDirection
         * @return DirectedPosition
         */
        public DirectedPosition withDirection(Direction newDirection) {
            return new DirectedPosition(position, newDirection);
        }
    }

    /**
     * Method which returns the player's id.
     * 
     * @return PlayerID
     */
    public PlayerID id() {
        return this.id;
    }

    /**
     * Returns the sequence of LifeStates representing the lives and States of
     * the player.
     * 
     * @return Sq<LifeState>
     */
    public Sq<LifeState> lifeStates() {
        return this.lifeStates;
    }

    /**
     * Returns the head of the sequence returned by the previous method, which
     * is the current LifeState of the Player.
     * 
     * @return current LifeState
     */
    public LifeState lifeState() {
        return lifeStates().head();
    }

    /**
     * Method that returns a new sequence of LifeStates which represents the
     * Player after he is hit with a Bomb.
     * 
     * @return Sq<LifeState> representing the Player's new number of lives
     */
    public Sq<LifeState> statesForNextLife() {
        
        return Sq.repeat(Ticks.PLAYER_DYING_TICKS,
                new LifeState(lives(), LifeState.State.DYING)).concat(creationSq(lives() - 1));
    }

    /**
     * Method that returns the current number of lives of the Player,
     * corresponding with their LifeState.
     * 
     * @return int (current number of lives)
     */
    public int lives() {
        return lifeState().lives();
    }

    /**
     * Method that returns true if the number of lives of the Player is greater
     * than 0.
     * 
     * @return boolean
     */
    public boolean isAlive() {
        return (lives() > 0);
    }

    /**
     * Method that returns the sequence of DirectedPostions of the Player.
     * 
     * @return Sq<DirectedPosition>
     */
    public Sq<DirectedPosition> directedPositions() {
        return this.directedPos;
    }

    /**
     * Method that returns the current position of the player, which is the
     * position associated with the first LifeState (the head) of the sequence
     * returned by the previous method.
     * 
     * @return SubCell representing the current position of the player
     */
    public SubCell position() {
        return directedPositions().head().position();
    }

    /**
     * Method that returns the current direction of the player, which is the
     * direction associated with the first LifeState (the head) of the sequence
     * returned by the directedPositions() method.
     * 
     * @return current direction of the player
     */
    public Direction direction() {
        return directedPositions().head().direction();
    }

    /**
     * Returns the maximal numbers of Bombs that the Player can place.
     * 
     * @return int maxBomb
     */
    public int maxBombs() {
        return this.maxBombs;
    }

    /**
     * Method that returns a new Player, identical to the current one in every
     * aspect except that the new Player has the given number of maximum Bombs
     * he can place.
     * 
     * @param newMaxBombs
     * @return Player
     */
    public Player withMaxBombs(int newMaxBombs) {
        return new Player(this.id, this.lifeStates, this.directedPos,
                newMaxBombs, this.bombRange);
    }

    /**
     * Returns the range of the Player's Bombs (which is a number of Cells).
     * 
     * @return int bombRange
     */
    public int bombRange() {
        return this.bombRange;
    }

    /**
     * Method that returns a new Player, identical to the current one in every
     * aspect except that the new Player's Bombs have the given range.
     * 
     * @param newBombRange
     * @return Player
     */
    public Player withBombRange(int newBombRange) {
        return new Player(this.id, this.lifeStates, this.directedPos,
                this.maxBombs, newBombRange);
    }

    /**
     * Method which returns a new Bomb, which is placed on the SubCell the
     * player is currently occupying, with a fuse length corresponding to
     * Ticks.BOMB_FUSE_TICKS and the same range as the Player's Bombs.
     * 
     * @return Bomb
     */
    public Bomb newBomb() {
        return new Bomb(this.id, this.position().containingCell(),
                Ticks.BOMB_FUSE_TICKS, this.bombRange);
    }

    /**
     * Method which constructs a sequence of lifeStates depending on the number
     * of lives received. Throws a NullPointerException if the number of lives
     * is strictly negative.
     * 
     * @param lives
     * @return a Sq based on the number of lives
     */
    private static Sq<LifeState> creationSq(int lives) {
        if (lives < 0) {
            throw new IllegalArgumentException();
        } else if (lives == 0) {
            return Sq.constant(new LifeState(lives, LifeState.State.DEAD));
        } else {
            return Sq
                    .repeat(Ticks.PLAYER_INVULNERABLE_TICKS,
                            new LifeState(lives, LifeState.State.INVULNERABLE))
                    .concat(Sq.constant(
                            new LifeState(lives, LifeState.State.VULNERABLE)));
        }
    }
}