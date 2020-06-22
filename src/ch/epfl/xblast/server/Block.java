package ch.epfl.xblast.server;

import java.util.NoSuchElementException;

// Enumeration of all possible blocks.
public enum Block {
    FREE, INDESTRUCTIBLE_WALL, DESTRUCTIBLE_WALL, CRUMBLING_WALL, BONUS_BOMB(
            Bonus.INC_BOMB), BONUS_RANGE(Bonus.INC_RANGE);
    
    private Bonus maybeAssociatedBonus;

    /**
     * Default constructor
     * 
     */
    private Block() {
        maybeAssociatedBonus = null;
    }

    /**
     * Main constructor used by bonus blocks
     * 
     * @param maybeAssociatedBonus
     */
    private Block(Bonus maybeAssociatedBonus) {
        this.maybeAssociatedBonus = maybeAssociatedBonus;
    }

    /**
     * Predicate that returns true if the block is free.
     * 
     * @return boolean
     */
    public boolean isFree() {
        return this == FREE;
    }

    /**
     * Predicate that returns true if the block can host a player, in which case
     * the block is free or it contains a bonus.
     * 
     * @return boolean
     */
    public boolean canHostPlayer() {
        return (this.isFree() || this.isBonus());
    }

    /**
     * Predicate that returns true if the current block casts a shadow, which is
     * the case if there are various types of wall on the block.
     * 
     * @return boolean
     */
    public boolean castsShadow() {
        return (this == INDESTRUCTIBLE_WALL || this == DESTRUCTIBLE_WALL
                || this == CRUMBLING_WALL);
    }

    /**
     * Predicate that returns true if the Block in question is a bonus Block.
     * 
     * @return boolean
     */
    public boolean isBonus() {
        return (this == BONUS_BOMB || this == BONUS_RANGE);
    }

    /**
     * This method returns the bonus associated with the current block.
     * 
     * @throws NoSuchElementException if there is no bonus in relation to the
     * Block.
     * @return Bonus of the Block in question
     */
    public Bonus associatedBonus() throws NoSuchElementException {
        if (maybeAssociatedBonus == null) {
            throw new NoSuchElementException();
        } else {
            return maybeAssociatedBonus;
        }
    }
}