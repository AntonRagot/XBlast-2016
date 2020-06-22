package ch.epfl.xblast.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;

public final class BoardPainter {
	private final Map<Block, BlockImage> palette;
	private final BlockImage sBlock;

	/**
	 * Constructor for the class BoardPainter; it receives a mapping of Blocks
	 * to their corresponding images and the image of a shadowed Block.
	 * 
	 * @param palette
	 * @param sBlock
	 */
	public BoardPainter(Map<Block, BlockImage> palette, BlockImage sBlock) {
		this.palette = Collections.unmodifiableMap(new HashMap<>(palette));
		this.sBlock = sBlock;
	}

	/**
	 * For given Cell and Board, this method return the byte corresponding to
	 * the Block that occupies the Cell on the Board.
	 * 
	 * @param board
	 * @param cell
	 * @return byte associated to given Cell
	 */
	public byte byteForCell(Board board, Cell cell) {
		return !(board.blockAt(cell) == Block.FREE) ? (byte) palette.get(board.blockAt(cell)).ordinal()
				: board.blockAt(cell.neighbor(Direction.W)).castsShadow() ? (byte) sBlock.ordinal()
						: (byte) BlockImage.IRON_FLOOR.ordinal();
	}
}