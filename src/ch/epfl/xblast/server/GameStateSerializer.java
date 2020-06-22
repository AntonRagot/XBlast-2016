package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.RunLengthEncoder;

public final class GameStateSerializer {
	private final static List<Cell> spiralOrder = Cell.SPIRAL_ORDER;
	private final static List<Cell> rowMajorOrder = Cell.ROW_MAJOR_ORDER;
    /**
     * Private constructor for the class GameStateSerializer.
     */
    private GameStateSerializer() { }

    /**
     * Given a BoardPainter and a GameState, this method returns the serialized version
     * of the GameState as a list of Bytes.
     * 
     * @param boardPainter
     * @param gameState
     * @return List<Byte> serialized GameState
     */
    public static List<Byte> serialize(BoardPainter boardPainter,
            GameState gameState) {
        List<Byte> serialized = new ArrayList<>();
        List<Byte> explosionsSerialized = new ArrayList<>();
        Board board = gameState.board();
        List<Byte> boardSerialized = spiralOrder.stream()
                                                .map(x -> boardPainter.byteForCell(board, x))
                                                .collect(Collectors.toList());
        
        List<Byte> encodedBoard = RunLengthEncoder.encode(boardSerialized);
		serialized.add((byte)encodedBoard.size());
        serialized.addAll(encodedBoard);
        
        for (Cell c : rowMajorOrder) {
            if (gameState.bombedCells().containsKey(c)) {
                explosionsSerialized.add(ExplosionPainter
                        .byteForBomb(gameState.bombedCells().get(c)));
            } else if (gameState.blastedCells().contains(c) && gameState.board().blockAt(c).isFree()) {
                    boolean north = hasNeighbouringBlast(gameState, c, Direction.N);
                    boolean south = hasNeighbouringBlast(gameState, c, Direction.S);
                    boolean east = hasNeighbouringBlast(gameState, c, Direction.E);
                    boolean west = hasNeighbouringBlast(gameState, c, Direction.W);
                    explosionsSerialized.add(ExplosionPainter.byteForBlast(north, east, south, west));
            } else {
                explosionsSerialized.add(ExplosionPainter.BYTE_FOR_EMPTY);
            }
        }
        List<Byte> encodedBlastAndExplosion = RunLengthEncoder.encode(explosionsSerialized);
		serialized.add((byte)encodedBlastAndExplosion.size());
        serialized.addAll(encodedBlastAndExplosion);
        
        for (Player p : gameState.players()) {
            serialized.add((byte)p.lives());
            serialized.add((byte)p.position().x());
            serialized.add((byte)p.position().y());
            serialized.add((byte) PlayerPainter.byteForPlayer(gameState.ticks(), p));
        }
        serialized.add((byte) Byte.toUnsignedInt((byte) (Math.ceil(gameState.remainingTime())/2)));
        return Collections.unmodifiableList(new ArrayList<>(serialized));
    }
    
    /**
     * Check if the Cell in a given Direction contains a blast.
     * 
     * @param gameState
     * @param Cell c
     * @param Direction d
     * @return Boolean
     */
    private static boolean hasNeighbouringBlast(GameState gameState, Cell c, Direction d) {
    	return gameState.blastedCells().contains(c.neighbor(d));
    }
}