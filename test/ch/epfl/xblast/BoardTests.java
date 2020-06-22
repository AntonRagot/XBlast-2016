package ch.epfl.xblast;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Lists;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.epfl.xblast.server.Block.*;
import static org.junit.Assert.*;

public class BoardTests {

    @Test
    public void boardClassIsImmutable() {
        List<Sq<Block>> matrix = new ArrayList<>(Cell.COUNT);

        for (int i = 0; i < Cell.COUNT; i++) {
            matrix.add(Sq.constant(INDESTRUCTIBLE_WALL));
        }

        Board b = new Board(matrix);

        for (int i = 0; i < Cell.COUNT; i++) {
            matrix.set(i, matrix.get(i).limit(1).concat(Sq.constant(FREE)));
        }

        for (int y = 0; y < Cell.ROWS; y++) {
            for (int x = 0; x < Cell.COLUMNS; x++) {
                assertNotEquals(FREE, b.blocksAt(new Cell(x, y)).tail().head());
            }
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyListOfBlockSequencesRaisesException() {
        new Board(new ArrayList<Sq<Block>>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidNumberOfBlockSequencesRaisesException() {
        Sq<Block> freeBlockSequence = Sq.constant(FREE);
        List<Sq<Block>> wholeBoardList = new ArrayList<>(Cell.COUNT);

        //Creates a board
        for (int k = 0; k < Cell.COUNT - 1; k++) {
            wholeBoardList.add(freeBlockSequence);
        }

        new Board(wholeBoardList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidNumberOfRowsRaisesException() {
        List<Block> freeRow = new ArrayList<>(Collections.nCopies(Cell.COLUMNS, FREE));
        List<List<Block>> matrix = Collections.nCopies(Cell.ROWS + 1, freeRow);
        Board.ofRows(new ArrayList<>(matrix));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidColumnRaisesException() {
        List<Block> validRow = new ArrayList<>(Collections.nCopies(Cell.COLUMNS, FREE));
        List<Block> incompleteRow = new ArrayList<>(validRow);
        incompleteRow.remove(FREE);

        List<List<Block>> matrix = new ArrayList<>(Collections.nCopies(Cell.ROWS - 1, validRow));

        matrix.add(incompleteRow);
        Board.ofRows(matrix);
    }

    @Test
    public void blockAtIsCorrect() {
        Block[] blockTypes = Block.values();

        List<List<Block>> matrix = new ArrayList<>(Collections.nCopies(Cell.ROWS, generateRow(Cell.COLUMNS, blockTypes)));
        Board board = Board.ofRows(matrix);

        //Check at every position if the returned block corresponds to what we constructed
        for (int y = 0; y < Cell.ROWS; y++) {
            for (int x = 0; x < Cell.COLUMNS; x++) {
                int index = x % blockTypes.length;
                Cell c = new Cell(x, y);

                assertEquals(blockTypes[index], board.blockAt(c));
            }
        }
    }

    @Test
    public void blocksAtIsCorrect() {
        Block[] blockTypes = Block.values();

        List<List<Block>> matrix = Collections.nCopies(Cell.ROWS, generateRow(Cell.COLUMNS, blockTypes));

        /*Build a board where every block is followed in the sequence by
        the next block type as specified by the enumeration order*/
        List<Sq<Block>> blocks = new ArrayList<>(Cell.COUNT);
        for (List<Block> l : matrix) {
            for (Block b : l)
                blocks.add(Sq.repeat(1, b).concat(Sq.repeat(1, blockTypes[(b.ordinal() + 1) % blockTypes.length])));
        }

        Board board = new Board(blocks);

        //Check at every position if the sequence corresponds to what we constructed
        for (int y = 0; y < Cell.ROWS; y++) {
            for (int x = 0; x < Cell.COLUMNS; x++) {
                int index = Math.floorMod(x, blockTypes.length);
                Cell c = new Cell(x, y);
                Sq<Block> blockSequence = board.blocksAt(c);

                assertEquals(blockTypes[index], blockSequence.head());
                assertEquals(blockTypes[(index + 1) % blockTypes.length], blockSequence.tail().head());
                assertTrue(blockSequence.tail().tail().isEmpty());
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void ofQuadrantNWInvalidColumnsRaisesException() {
        List<Block> someRow = new ArrayList<>(Collections.nCopies(Cell.COLUMNS / 2 + 1, FREE));
        List<List<Block>> matrix = new ArrayList<>(Collections.nCopies(Cell.ROWS / 2, someRow));
        Board.ofQuadrantNWBlocksWalled(matrix);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ofQuadrantNWInvalidRowsRaisesException() {
        List<Block> someRow = new ArrayList<>(Collections.nCopies(Cell.COLUMNS / 2, FREE));
        List<List<Block>> matrix = new ArrayList<>(Collections.nCopies(Cell.ROWS / 2 - 1, someRow));
        Board.ofQuadrantNWBlocksWalled(matrix);
    }

    @Test
    public void ofQuadrantNWBlocksWalledIsCorrect() {

        List<List<Block>> matrix = Collections.nCopies(Cell.ROWS / 2, generateRow(Cell.COLUMNS / 2, Block.values()));
        Board board = Board.ofQuadrantNWBlocksWalled(new ArrayList<>(matrix));

        boardIsWalled(board);

        for (int y = Cell.ROWS / 2; y >= 1; y--) {
            List<Block> mirroredRow = Lists.mirrored(matrix.get(y - 1));
            for (int x = 1; x < Cell.COLUMNS - 1; x++) {
                // b1 checks the upper half board, whereas b2 checks the lower half board.
                // When y = Cell.ROWS / 2, we are checking the middle line, and when y = 1,
                // we are checking the line 1 with b1 and the line 13 with b2.
                Block b1 = board.blockAt(new Cell(x, y));
                assertEquals(mirroredRow.get(x - 1), b1);
                Block b2 = board.blockAt(new Cell(x, Cell.ROWS - 1 - y));
                assertEquals(mirroredRow.get(x - 1), b2);
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void ofInnerBlocksInvalidArgumentRaisesException() {
        List<List<Block>> blockList = Collections.nCopies(Cell.ROWS - 2, Collections.emptyList());
        Board.ofInnerBlocksWalled(new ArrayList<>(blockList));
    }

    @Test
    public void ofInnerBlocksWalledIsCorrect() {
        List<Block> aRow = Collections.nCopies(Cell.COLUMNS - 2, CRUMBLING_WALL);
        List<List<Block>> matrix = Collections.nCopies(Cell.ROWS - 2, new ArrayList<>(aRow));
        Board board = Board.ofInnerBlocksWalled(new ArrayList<>(matrix));

        boardIsWalled(board);

        for (int x = 1; x < Cell.COLUMNS - 1; x++) {
            for (int y = 1; y < Cell.ROWS - 1; y++) {
                Block b = board.blockAt(new Cell(x, y));
                assertEquals(CRUMBLING_WALL, b);
            }
        }
    }

    private void boardIsWalled(Board board) {
        for (int x = 0; x < Cell.COLUMNS; x++) {
            //Checks the upper row
            assertEquals(INDESTRUCTIBLE_WALL, board.blockAt(new Cell(x, 0)));

            //Checks the bottom row
            assertEquals(INDESTRUCTIBLE_WALL, board.blockAt(new Cell(x, Cell.ROWS - 1)));
        }

        for (int y = 0; y < Cell.ROWS; y++) {
            //Checks the left column
            assertEquals(INDESTRUCTIBLE_WALL, board.blockAt(new Cell(0, y)));

            //Checks the right column
            assertEquals(INDESTRUCTIBLE_WALL, board.blockAt(new Cell(Cell.COLUMNS - 1, y)));
        }

    }

    //Generate a row using available block types, consecutively in the order they are declared in the enumeration
    private List<Block> generateRow(int requiredSize, Block[] blockTypes) {

        List<Block> someRow = new ArrayList<>();
        int x;
        for (int i = 0; i < requiredSize; i++) {
            x = i % blockTypes.length;
            someRow.add(blockTypes[x]);
        }

        return someRow;
    }

}
