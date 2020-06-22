package ch.epfl.xblast;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CellTest05 {

    @Test
    public void hashCodeCellsConsistentWithEquals() {
        for (Cell c : Cell.ROW_MAJOR_ORDER) {
            Cell c1 = new Cell(c.x(), c.y());
            if (c.equals(c1)) {
                assertEquals(c.hashCode(), c1.hashCode());
            }
        }
    }
    
}
