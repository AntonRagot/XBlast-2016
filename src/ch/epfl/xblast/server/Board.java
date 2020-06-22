package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Lists;

public final class Board {
    private final List<Sq<Block>> blocks;
    private final static List<Block> rowOfIndestructibleWall = Collections.nCopies(Cell.COLUMNS, Block.INDESTRUCTIBLE_WALL);

    /**
     * The constructor for the class Board which also checks if the received
     * list of sequences of Blocks has the correct size.
     * 
     * @param blocks
     * @throws IllegalArgumentException if the list does not contains 195 elements
     */
    public Board(List<Sq<Block>> blocks) throws IllegalArgumentException {
        if (blocks.size() != Cell.COUNT) {
            throw new IllegalArgumentException(
                    "List does not contain 195 elements.");
        } else {
            this.blocks = Collections.unmodifiableList(new ArrayList<Sq<Block>>(blocks));
        }
    }

    /**
     * Method that creates a new Board given a List<List<Block>>.
     * 
     * @param rows
     * @return new Board
     * @throws IllegalArgumentException if rows doesn't correspond to the expected size
     */
    public static final Board ofRows(List<List<Block>> rows) throws IllegalArgumentException {
        checkBlockMatrix(rows, Cell.ROWS, Cell.COLUMNS);
        return Board.builder(rows);
    }

    /**
     * Creates a walled Board given a List<List<Block>> of 11*13 Blocks.
     * 
     * @param innerBlocks
     * @return walled Board
     * @throws IllegalArgumentException if innerBlocks doesn't correspond to the expected size
     */
    public static final Board ofInnerBlocksWalled(
            List<List<Block>> innerBlocks) throws IllegalArgumentException{

        checkBlockMatrix(innerBlocks, Cell.ROWS - 2, Cell.COLUMNS - 2);
        List<List<Block>> walledBoard = new ArrayList<List<Block>>();
        walledBoard.add(rowOfIndestructibleWall);

        List<Block> blockedRow = new ArrayList<Block>();
        for (int i = 1; i < Cell.ROWS - 1; i++) {
            blockedRow.add(Block.INDESTRUCTIBLE_WALL);
            for (int j = 0; j < innerBlocks.get(0).size(); j++) {
                blockedRow.add(innerBlocks.get(i - 1).get(j));
            }
            blockedRow.add(Block.INDESTRUCTIBLE_WALL);
            walledBoard.add(new ArrayList<Block>(blockedRow));
            blockedRow.clear();
            if (i == 11) {
                walledBoard.add(rowOfIndestructibleWall);
            }
        }
        return Board.builder(walledBoard);
    }

    /**
     * Creates a walled Board following two symmetry axis, given the upper left
     * quarter of the final Board.
     * 
     * @param quadrantNWBlocks
     * @return walled Board
     * @throws IllegalArgumentException if quadrantNWBlocks doesn't correspond to the expected size
     */
    public static final Board ofQuadrantNWBlocksWalled(
            List<List<Block>> quadrantNWBlocks) throws IllegalArgumentException {
        checkBlockMatrix(quadrantNWBlocks, Cell.ROWS / 2, Cell.COLUMNS / 2);
        List<List<Block>> mirorred = new ArrayList<List<Block>>();

        for (int i = 0; i < quadrantNWBlocks.size(); i++) {
            mirorred.add(Lists.mirrored(quadrantNWBlocks.get(i)));
        }
        int sizeMirrored = mirorred.size();
        for (int i = sizeMirrored - 2; i >= 0; i--) {
            mirorred.add(mirorred.get(i));
        }
        return ofInnerBlocksWalled(mirorred);
    }

    /**
     * Returns the sequence of Blocks for a given Cell.
     * 
     * @param c
     * @return Sq<Block> for given Cell
     */
    public Sq<Block> blocksAt(Cell c) {
        return blocks.get((c.rowMajorIndex()));
    }

    /**
     * Returns the first Block in the sequence for a given Cell.
     * 
     * @param a given cell
     * @return first Block of the Sq<Block> of the Cell
     */
    public Block blockAt(Cell c) {
        return blocksAt(c).head();
    }

    /**
     * Method that checks if the List<List<Block>> received as an argument has
     * the correct dimensions, namely rows and columns.
     * 
     * @param matrix
     * @param rows
     * @param columns
     * @throws IllegalArgumentException if the given matrix doesn't have the given rows and the given columns
     */
     private static void checkBlockMatrix(List<List<Block>> matrix, int rows,
            int columns) throws IllegalArgumentException {
        if (matrix.size() != rows) {
            throw new IllegalArgumentException();
        } else {
            for (int i = 0; i < matrix.size(); i++) {
                if (matrix.get(i).size() != columns) {
                    throw new IllegalArgumentException();
                }
            }
        }
    }

    /**
     * A builder for the class Board. Builds a new Board given a list of list of
     * Blocks.
     * 
     * @param matrix
     * @return new Board
     */
    private static Board builder(List<List<Block>> matrix) {
        List<Sq<Block>> board = new ArrayList<Sq<Block>>();
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                Sq<Block> blocks = Sq.constant(((matrix.get(i)).get(j)));
                board.add(blocks);
            }
        }
        return new Board(board);
    }
}