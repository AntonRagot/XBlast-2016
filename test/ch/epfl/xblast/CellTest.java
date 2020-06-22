package ch.epfl.xblast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CellTest {
    @Test
    public void rowMajorIndexCorrespondsToOrder() {
        int i = 0;
        for (Cell c: Cell.ROW_MAJOR_ORDER)
            assertEquals(i++, c.rowMajorIndex());
        assertEquals(Cell.COUNT, i);
    }

    @Test
    public void spiralOrderContainsAllCells() {
        assertEquals(Cell.COUNT, Cell.SPIRAL_ORDER.size());

        boolean[] cellSeen = new boolean[Cell.COUNT];
        for (Cell c: Cell.SPIRAL_ORDER) {
            assertFalse(cellSeen[c.rowMajorIndex()]);
            cellSeen[c.rowMajorIndex()] = true;
        }
    }

    @Test
    public void spiralOrderNeighborsAreSpatialNeighbors() {
        Cell pred = Cell.SPIRAL_ORDER.get(0);
        for (Cell c: Cell.SPIRAL_ORDER.subList(1, Cell.SPIRAL_ORDER.size())) {
            int areNeighborsCount = 0;
            for (Direction d: Direction.values()) {
                if (pred.equals(c.neighbor(d)))
                    areNeighborsCount += 1;
            }
            assertEquals(1, areNeighborsCount);
            pred = c;
        }
    }

    @Test
    public void constructorCorrectlyNormalizesCoordinates() {
        for (int i = -2; i <= 2; ++i) {
            Cell c = new Cell(14 + 15 * i, 12 + 13 * i);
            assertEquals(14, c.x());
            assertEquals(12, c.y());
        }
    }
    
	@Test
	public void equalsCorrect() {
		Cell c1 = new Cell(0,0);
		Cell c2 = new Cell(0,1);
		Cell c4 = new Cell(1,0);
		Cell c3 = new Cell(0,0);
		assertFalse(c1.equals(c2));
		assertFalse(c1.equals(c4));
		assertFalse(c4.equals(c2));
		assertTrue(c1.equals(c3));
	}
    
	@Test
	public void neighborOfCornerCorrect() {
		Cell NE = new Cell(14,  0);
		Cell NW = new Cell( 0,  0);
		Cell SE = new Cell(14, 12);
		Cell SW = new Cell( 0, 12);
		
		assertEquals(new Cell( 0, 12), NW.neighbor(Direction.N));
        assertEquals(new Cell( 1,  0), NW.neighbor(Direction.E));
        assertEquals(new Cell( 0,  1), NW.neighbor(Direction.S));
        assertEquals(new Cell(14,  0), NW.neighbor(Direction.W));
        
        assertEquals(SE, NE.neighbor(Direction.N));
        assertEquals(NW, NE.neighbor(Direction.E));
        assertEquals(new Cell(14, 1), NE.neighbor(Direction.S));
        assertEquals(new Cell(13, 0), NE.neighbor(Direction.W));
        
        assertEquals(new Cell( 0, 11), SW.neighbor(Direction.N));
        assertEquals(new Cell( 1, 12), SW.neighbor(Direction.E));
        assertEquals(NW, SW.neighbor(Direction.S));
        assertEquals(SE, SW.neighbor(Direction.W));
        
        assertEquals(new Cell(14, 11), SE.neighbor(Direction.N));
        assertEquals(SW, SE.neighbor(Direction.E));
        assertEquals(NE, SE.neighbor(Direction.S));
        assertEquals(new Cell(13, 12), SE.neighbor(Direction.W));
	}

    @Test
    public void oppositeNeighborOfNeighborIsThis() {
        for (Cell c: Cell.ROW_MAJOR_ORDER) {
            for (Direction d: Direction.values()) {
                assertEquals(c, c.neighbor(d).neighbor(d.opposite()));
            }
        }
    }
    
	@Test(expected = UnsupportedOperationException.class)
	public void isRowMajorOrderModifiable(){
		Cell.ROW_MAJOR_ORDER.set(0, new Cell(1,0));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void isSpiralOrderModifiable(){
		 Cell.SPIRAL_ORDER.set(0, new Cell(1,0));
	}
	
	@Test
	public void constantsAreCorrect() {
	    assertEquals(15, Cell.COLUMNS);
	    assertEquals(13, Cell.ROWS);
	    assertEquals(15*13, Cell.COUNT);
	}
}
