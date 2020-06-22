package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;

public final class Level {
	private final BoardPainter bPainter;
	private final GameState gameState;
	public static Level DEFAULT_LEVEL = new Level(new BoardPainter(paletteBuilder(), BlockImage.IRON_FLOOR_S),
			GameStateBuilderDeBase());

	/**
	 * Constructs a Level given a BoardPainter and a GameState.
	 * 
	 * @param bPainter
	 * @param gameState
	 */
	public Level(BoardPainter bPainter, GameState gameState) {
		this.bPainter = bPainter;
		this.gameState = gameState;
	}

	/**
	 * Returns the BoardPainter of the Level.
	 * 
	 * @return BoardPainter
	 */
	public BoardPainter getBoardPainter() {
		return bPainter;
	}

	/**
	 * Returns the GameState of the Level.
	 * 
	 * @return GameState
	 */
	public GameState getGameState() {
		return gameState;
	}

	/**
	 * Builds the list of the initial Players.
	 * 
	 * @return List<Player>
	 */
	private static List<Player> playerBuilder() {
		List<Player> player = new ArrayList<>();
		player.add(new Player(PlayerID.PLAYER_1, 3, new Cell(1, 1), 2, 3));
		player.add(new Player(PlayerID.PLAYER_2, 3, new Cell(Cell.COLUMNS - 2, 1), 2, 3));
		player.add(new Player(PlayerID.PLAYER_3, 3, new Cell(Cell.COLUMNS - 2, Cell.ROWS - 2), 2, 3));
		player.add(new Player(PlayerID.PLAYER_4, 3, new Cell(1, Cell.ROWS - 2), 2, 3));
		return new ArrayList<Player>(player);
	}

	/**
	 * Builds the mapping of each Block on the Board to its associated image.
	 * 
	 * @return Map<Block, BlockImage>
	 */
	private static Map<Block, BlockImage> paletteBuilder() {
		Map<Block, BlockImage> palette = new HashMap<>();
		palette.put(Block.FREE, BlockImage.IRON_FLOOR);
		palette.put(Block.INDESTRUCTIBLE_WALL, BlockImage.DARK_BLOCK);
		palette.put(Block.DESTRUCTIBLE_WALL, BlockImage.EXTRA);
		palette.put(Block.CRUMBLING_WALL, BlockImage.EXTRA_O);
		palette.put(Block.BONUS_BOMB, BlockImage.BONUS_BOMB);
		palette.put(Block.BONUS_RANGE, BlockImage.BONUS_RANGE);
		return new HashMap<Block, BlockImage>(palette);
	}

	/**
	 * Builds the GameState corresponding to the start of the game (tick 0).
	 * 
	 * @return GameState
	 */
	private static GameState GameStateBuilderDeBase() {
		Block __ = Block.FREE;
		Block XX = Block.INDESTRUCTIBLE_WALL;
		Block oo = Block.DESTRUCTIBLE_WALL;
		Board board = Board.ofQuadrantNWBlocksWalled(
				Arrays.asList(Arrays.asList(__, __, __, __, __, oo, __), Arrays.asList(__, XX, oo, XX, oo, XX, oo),
						Arrays.asList(__, oo, __, __, __, oo, __), Arrays.asList(oo, XX, __, XX, XX, XX, XX),
						Arrays.asList(__, oo, __, oo, __, __, __), Arrays.asList(oo, XX, oo, XX, oo, XX, __)));

		return new GameState(0, board, playerBuilder(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}
}