package ch.epfl.xblast;

import java.lang.Math;

public final class SubCell {
    private final int x;
    private final int y;
    public static final int COLUMNS = 240;
    public static final int ROWS = 208;
    private static final int NB_SUBCELL_IN_CELL = 16;

    /**
     * Constructor for the class SubCell.
     * 
     * @param x
     * @param y
     */
    public SubCell(int x, int y) {
        this.x = Math.floorMod(x, COLUMNS);
        this.y = Math.floorMod(y, ROWS);
    }

    /**
     * For a given Cell this method returns the corresponding central SubCell.
     * 
     * @param cell
     * @return central SubCell of the given Cell
     */
    public static SubCell centralSubCellOf(Cell cell) {
        int Xcell = cell.x() * NB_SUBCELL_IN_CELL + (NB_SUBCELL_IN_CELL/2);
        int Ycell = cell.y() * NB_SUBCELL_IN_CELL + (NB_SUBCELL_IN_CELL/2);
        return new SubCell(Xcell, Ycell);
    }

    /**
     * Normalizes the x coordinate.
     * 
     * @return normalized x coordinate
     */
    public int x() {
        return Math.floorMod(x, COLUMNS);
    }

    /**
     * Normalizes the y coordinate.
     * 
     * @return normalized y coordinate
     */
    public int y() {
        return Math.floorMod(y, ROWS);
    }

    /**
     * Calculates the Manhattan distance to the central SubCell.
     * 
     * @return Manhattan distance to the central SubCell
     */
    public int distanceToCentral() {
        int vectX = Math
                .abs(x() - centralSubCellOf(this.containingCell()).x());
        int vectY = Math
                .abs(y() - centralSubCellOf(this.containingCell()).y());
        return vectX + vectY;
    }

    /**
     * Checks if the SubCell is central.
     * 
     * @return boolean
     */
    public boolean isCentral() {
        return distanceToCentral() == 0;
    }

    /**
     * Returns the neighboring SubCell in a given direction.
     * 
     * @param d
     * @return neighboring SubCell in given direction
     */
    public SubCell neighbor(Direction d) {
        switch (d) {
        case N:
            return new SubCell(x(), y() - 1);

        case S:
            return new SubCell(x(), y() + 1);

        case E:
            return new SubCell(x() + 1, y());

        case W:
            return new SubCell(x() - 1, y());

        default:
            return new SubCell(x(), y());
        }
    }

    /**
     * Returns the Cell which contains the SubCell in question.
     * 
     * @return Cell containing the SubCell
     */
    public Cell containingCell() {
        return new Cell(x() / NB_SUBCELL_IN_CELL, y() / NB_SUBCELL_IN_CELL);
    }

    /**
     * Override of the equals method.
     * 
     * @return boolean
     */
    @Override
    public boolean equals(Object that) {
        return (that instanceof SubCell && ((SubCell) that).x() == this.x()
                    && ((SubCell) that).y() == this.y());
    }

    /**
     * Override of the equals method.
     * 
     * @return String
     */
    @Override
    public String toString() {
       return "(" + x() + " ," + y() + ")";
    }

    /**
     * An override of the hashCode method which also calculates the row major
     * order for SubCells.
     * 
     * @return hash value for the SubCell
     */
    @Override
    public int hashCode() {
        return y() * COLUMNS + x();
    }
}