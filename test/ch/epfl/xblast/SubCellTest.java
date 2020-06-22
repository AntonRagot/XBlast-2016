package ch.epfl.xblast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SubCellTest {
    @Test
    public void centralSubCellOfKnowCellIsCorrect() {
        SubCell c = SubCell.centralSubCellOf(new Cell(2, 1));
        assertEquals(40, c.x());
        assertEquals(24, c.y());
    }

    @Test
    public void centralSubCellIsCentral() {
        for (Cell c: Cell.ROW_MAJOR_ORDER)
            assertTrue(SubCell.centralSubCellOf(c).isCentral());
    }

    @Test
    public void distanceToCentralOfCentralIsZero() {
        for (Cell c: Cell.ROW_MAJOR_ORDER)
            assertEquals(0, SubCell.centralSubCellOf(c).distanceToCentral());
    }

    @Test
    public void constructorCorrectlyNormalizesCoordinates() {
        for (int i = -2; i <= 2; ++i) {
            SubCell c = new SubCell(239 + 240 * i, 207 + 208 * i);
            assertEquals(239, c.x());
            assertEquals(207, c.y());
        }
    }

    @Test
    public void distanceToCentralOfOriginIsCorrect() {
        SubCell s = new SubCell(0, 0);
        assertEquals(16, s.distanceToCentral());
    }

    @Test
    public void containingCellOfCentralsNeighborIsCorrect() {
        for (Cell c: Cell.ROW_MAJOR_ORDER) {
            SubCell s = SubCell.centralSubCellOf(c);
            for (Direction d: Direction.values())
                assertEquals(c, s.neighbor(d).containingCell());
        }
    }
    
    @Test
    public void equalsCorrect() {
        SubCell c1 = new SubCell(0,0);
        SubCell c2 = new SubCell(0,1);
        SubCell c4 = new SubCell(1,0);
        SubCell c3 = new SubCell(0,0);
        assertFalse(c1.equals(c2));
        assertFalse(c1.equals(c4));
        assertFalse(c4.equals(c2));
        assertTrue(c1.equals(c3));
    }
    
    @Test
    public void neighborOfCornerCorrect() {
        SubCell NE = new SubCell(239,   0);
        SubCell NW = new SubCell(  0,   0);
        SubCell SE = new SubCell(239, 207);
        SubCell SW = new SubCell(  0, 207);
        
        assertEquals(new SubCell(  0, 207), NW.neighbor(Direction.N));
        assertEquals(new SubCell(  1,   0), NW.neighbor(Direction.E));
        assertEquals(new SubCell(  0,   1), NW.neighbor(Direction.S));
        assertEquals(new SubCell(239,   0), NW.neighbor(Direction.W));
        
        assertEquals(SE, NE.neighbor(Direction.N));
        assertEquals(NW, NE.neighbor(Direction.E));
        assertEquals(new SubCell(239,  1), NE.neighbor(Direction.S));
        assertEquals(new SubCell(238,  0), NE.neighbor(Direction.W));
        
        assertEquals(new SubCell(0, 206), SW.neighbor(Direction.N));
        assertEquals(new SubCell(1, 207), SW.neighbor(Direction.E));
        assertEquals(NW, SW.neighbor(Direction.S));
        assertEquals(SE, SW.neighbor(Direction.W));
        
        assertEquals(new SubCell(239, 206), SE.neighbor(Direction.N));
        assertEquals(SW, SE.neighbor(Direction.E));
        assertEquals(NE, SE.neighbor(Direction.S));
        assertEquals(new SubCell(238, 207), SE.neighbor(Direction.W));
    }

    @Test
    public void distanceToCentralOfSomeSubCellIsCorrect() {
        assertEquals(10, new SubCell( 0, 10).distanceToCentral());
        assertEquals(5,  new SubCell( 5, 10).distanceToCentral());
        assertEquals(4,  new SubCell(10, 10).distanceToCentral());
        assertEquals(9,  new SubCell(15, 10).distanceToCentral());
        assertEquals(11, new SubCell( 0,  5).distanceToCentral());
        assertEquals(6,  new SubCell( 5,  5).distanceToCentral());
        assertEquals(9,  new SubCell(10, 15).distanceToCentral());
    }
    
    @Test
    public void isCentralOfFirstCellIsCorrect() {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (i == 8 && j == 8) assertTrue(new SubCell(i, j).isCentral());
                else assertFalse(new SubCell(i, j).isCentral());
            }
        }
    }
    
    @Test
    public void borderCaseOfContainingCell() {
        assertEquals(new Cell(0,0), new SubCell(15, 15).containingCell());
        assertEquals(new Cell(1,1), new SubCell(16, 16).containingCell());
        assertEquals(new Cell(0,1), new SubCell(15, 16).containingCell());
        assertEquals(new Cell(1,0), new SubCell(16, 15).containingCell());
    }
}
