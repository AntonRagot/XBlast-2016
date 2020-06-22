package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.Math;

public final class Cell {
    private final int x;
    private final int y;
    public final static int COLUMNS = 15;
    public final static int ROWS = 13;
    public final static int COUNT = COLUMNS * ROWS;
    public final static List<Cell> ROW_MAJOR_ORDER = Collections
            .unmodifiableList(rowMajorOrder());
    public final static List<Cell> SPIRAL_ORDER = Collections
            .unmodifiableList(rowSpiralOrder());

    /**
     * Constructor for the class Cell.
     * 
     * @param x
     * @param y
     */
    public Cell(int x, int y) {
        this.x = Math.floorMod(x, COLUMNS);
        this.y = Math.floorMod(y, ROWS);
    }

    /**
     * Normalizes the x coordinate.
     * 
     * @return normalized x coordinate
     */
    public int x() {
        return x;
    }

    /**
     * Normalizes the y coordinate.
     * 
     * @return normalized y coordinate
     */
    public int y() {
        return y;
    }

    /**
     * Returns the index of the Cell in rowMajorOrder.
     * 
     * @return index of the Cell
     */
    public int rowMajorIndex() {
        return ROW_MAJOR_ORDER.indexOf(this);
    }

    /**
     * Returns the neighboring Cell in a given direction.
     * 
     * @param dir
     * @return neighboring Cell
     */
    public Cell neighbor(Direction dir) {
        switch (dir) {
        case N:
            return new Cell(x(), y() - 1);
        case S:
            return new Cell(x(), y() + 1);
        case E:
            return new Cell(x() + 1, y());
        case W:
            return new Cell(x() - 1, y());
        default:
            return new Cell(x(), y());
        }
    }

    /**
     * An override for the equals method.
     * 
     * @return boolean
     */
    @Override
    public boolean equals(Object that) {
        return (that instanceof Cell && ((Cell) that).x() == this.x()
                    && ((Cell) that).y() == this.y());
    }

    /**
     * An override for the toString method.
     * 
     * @return String
     */
    @Override
    public String toString() {
        return "(" + x + " ," + y + ")";
    }

    /**
     * An override for the hashCode method.
     * 
     * @return hash value for the Cell
     */
    @Override
    public int hashCode() {
        return rowMajorIndex();
    }

    /**
     * This method creates an ArrayList which contains the Cells in row major
     * order.
     * 
     * @return ArrayList of rowMajor ordered Cells
     */

    private static ArrayList<Cell> rowMajorOrder() {
        ArrayList<Cell> rowMajorOrder = new ArrayList<Cell>();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Cell cell = new Cell(j, i);
                rowMajorOrder.add(cell);
            }
        }
        return rowMajorOrder;
    }

    /**
     * Method that creates an ArrayList which contains the Cells in row spiral
     * order.
     * 
     * @return ArrayList of spiral ordered Cells
     */
    private static ArrayList<Cell> rowSpiralOrder() {
        int c1;
        int c2;
        boolean horizontal = true;
        ArrayList<Cell> rowSpiralOrder = new ArrayList<Cell>();
        ArrayList<Integer> ix = new ArrayList<Integer>();
        ArrayList<Integer> iy = new ArrayList<Integer>();
        ArrayList<Integer> i1 = new ArrayList<Integer>();
        ArrayList<Integer> i2 = new ArrayList<Integer>();
        for (int i = 0; i < COLUMNS; i++) {
            ix.add(i);
        }
        for (int j = 0; j < ROWS; j++) {
            iy.add(j);
        }
        while (!ix.isEmpty() && !iy.isEmpty()) {
            if (horizontal) {
                i1 = ix;
                i2 = iy;
            } else {
                i1 = iy;
                i2 = ix;
            }
            c2 = i2.get(0);
            i2.remove(0);
            for (int i = 0; i < i1.size(); i++) {
                c1 = i1.get(i);
                Cell c;
                if (horizontal) {
                    c = new Cell(c1, c2);
                } else {
                    c = new Cell(c2, c1);
                }
                rowSpiralOrder.add(c);
            }
            Collections.reverse(i1);
            horizontal = !horizontal;
        }
        return rowSpiralOrder;
    }
}