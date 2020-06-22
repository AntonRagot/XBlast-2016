package ch.epfl.xblast;

import java.util.NoSuchElementException;

public enum Direction {
    // N: North, E: East, S: South, W: West
    N, E, S, W;

    /**
     * Returns the opposite direction of the direction in question.
     * 
     * @return opposite direction
     * @throws NoSuchElementException if the direction is different from N,S,E,W
     */
    public Direction opposite() throws NoSuchElementException{
        switch (this) {
        case N:
            return S;
        case S:
            return N;
        case E:
            return W;
        case W:
            return E;
        default :
            throw new NoSuchElementException();    
        }
    }

    /**
     * Returns true if the direction in question is horizontal.
     * 
     * @return boolean
     */
    public Boolean isHorizontal() {
        return (this == E || this == W);
    }

    /**
     * Returns true if the given direction is parallel to the current direction.
     * 
     * @param that
     * @return boolean
     */
    public Boolean isParallelTo(Direction that) {
        return (this == that || this == that.opposite());
    }
}