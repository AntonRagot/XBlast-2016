package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.Lists;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.server.Player.DirectedPosition;
import ch.epfl.xblast.server.Player.LifeState;
import ch.epfl.xblast.server.Player.LifeState.State;

public final class GameState {
	private static final int NUMBER_OF_BONUSES = 3;
	private final int ticks;
	private final Board board;
	private final List<Player> players;
	private final List<Bomb> bombs;
	private final List<Sq<Sq<Cell>>> explosions;
	private final List<Sq<Cell>> blasts;
	private static List<List<PlayerID>> PERM = Collections
			.unmodifiableList(Lists.permutations(Arrays.asList(PlayerID.values())));
	private static final Random RANDOM = new Random(2016);
	public static final int COLUMNS = 15;
	public static final int ROWS = 13;

	/**
	 * The main constructor for the class GameState, it constructs the current
	 * state of the game for the given parameters. It also throws an
	 * IllegalArgumentException if there are less than four players or if ticks
	 * is strictly negative. Also checks that the other parameters are non null.
	 * 
	 * @param ticks
	 * @param board
	 * @param players
	 * @param bombs
	 * @param explosions
	 * @param blasts
	 */
	public GameState(int ticks, Board board, List<Player> players, List<Bomb> bombs, List<Sq<Sq<Cell>>> explosions,
			List<Sq<Cell>> blasts) throws IllegalArgumentException, NullPointerException {
		this.ticks = ArgumentChecker.requireNonNegative(ticks);
		this.board = Objects.requireNonNull(board);
		if (players.size() != 4) {
			throw new IllegalArgumentException("Le nombre de joueur est different de 4");
		} else {
			this.players = Collections.unmodifiableList(new ArrayList<>(players));
		}
		this.bombs = Collections.unmodifiableList(Objects.requireNonNull(new ArrayList<>(bombs)));
		this.explosions = Collections.unmodifiableList(Objects.requireNonNull(new ArrayList<>(explosions)));
		this.blasts = Collections.unmodifiableList(Objects.requireNonNull(new ArrayList<>(blasts)));
	}

	/**
	 * Secondary constructor that represents the beginning of the game.
	 * 
	 * @param board
	 * @param players
	 */
	public GameState(Board board, List<Player> players) {
		this(0, board, players, new ArrayList<Bomb>(), new ArrayList<Sq<Sq<Cell>>>(), new ArrayList<Sq<Cell>>());
	}

	/**
	 * This method returns the time associated to the state of the game.
	 * 
	 * @return ticks
	 */
	public int ticks() {
		return ticks;
	}

	/**
	 * Predicate that returns true if the game is over: if the time ticks has
	 * reached the total number of ticks for a game or if there are no alive
	 * players remaining.
	 * 
	 * @return boolean
	 */
	public boolean isGameOver() {
		return (ticks > Ticks.TOTAL_TICKS) || (alivePlayers().size() == 1);
	}

	/**
	 * This method returns the number of seconds left until the end of the game.
	 * 
	 * @return ticksRemaining seconds left until the end of the game
	 */
	public double remainingTime() {
		return (Ticks.TOTAL_TICKS - (double) ticks()) / Ticks.TICKS_PER_SECOND;
	}

	/**
	 * This method returns either the winner of the game, if there is one, or
	 * empty if there is none.
	 * 
	 * @return Optional<Player> the winner of the game or the optional value
	 *         empty
	 */
	public Optional<PlayerID> winner() {
		return (alivePlayers().size() == 1) ? Optional.of(alivePlayers().get(0).id()) : Optional.empty();
	}

	/**
	 * Returns the current Board of the game.
	 * 
	 * @return board
	 */
	public Board board() {
		return board;
	}

	/**
	 * Returns a list of all the players, dead or alive.
	 * 
	 * @return List<Player> which always contains four elements (four players)
	 */
	public List<Player> players() {
		return players;
	}

	/**
	 * Method that returns a list containing the players that are still alive
	 * (that have at least one life left).
	 * 
	 * @return List<Player>
	 */
	public List<Player> alivePlayers() {
		return players().stream().filter(Player::isAlive).collect(Collectors.toList());
	}

	/**
	 * This method returns a map associating each Bomb on the Board to the Cell
	 * it occupies.
	 * 
	 * @return Map<Cell, Bomb> mapping a Cell to its corresponding Bomb
	 */
	public Map<Cell, Bomb> bombedCells() {
		return bombs.stream().collect(Collectors.toMap(Bomb::position, x -> x));
	}

	/**
	 * This method returns a Set of all the Cells which contain an explosion
	 * particle.
	 * 
	 * @return Set<Cell> which contains all Cell that have a blast on it
	 */
	public Set<Cell> blastedCells() {
		return blasts.stream().map(Sq::head).collect(Collectors.toSet());
	}

	/**
	 * Returns the GameState for the next tick given a map of PlayerIds to
	 * associated optional Directions and a Set of PlayerIds representing the
	 * players who wish to place new bombs.
	 * 
	 * @param speedChangeEvents
	 * @param bombDropEvents
	 * @return GameState for next tick
	 */
	public GameState next(Map<PlayerID, Optional<Direction>> speedChangeEvents, Set<PlayerID> bombDropEvents) {
		List<PlayerID> permutations = PERM.get(this.ticks() % PERM.size());
		// We create a ordered list of Player
		List<Player> orderedPlayers = new ArrayList<Player>();
		permutations.forEach(x -> orderedPlayers.add(players().get(x.ordinal())));
		// We create a Set of Cell which contains the consumed bonuses
		// We also map the PlayerID to the Bonus it has consumed
		Map<PlayerID, Bonus> playerBonuses = orderedPlayers.stream()
				.filter(x -> board.blockAt(x.position().containingCell()).isBonus())
				.filter(x -> x.position().isCentral()).collect(Collectors.toMap(Player::id,
						x -> board.blockAt(x.position().containingCell()).associatedBonus()));
		Set<Cell> consumedBonuses = orderedPlayers.stream()
				.filter(x -> board.blockAt(x.position().containingCell()).isBonus())
				.filter(x -> x.position().isCentral()).map(x -> x.position().containingCell())
				.collect(Collectors.toSet());
		Set<Cell> blastedCells1 = nextBlasts(blasts, board, explosions).stream().map(x -> x.head())
				.collect(Collectors.toSet());

		Board board1 = nextBoard(board, consumedBonuses, blastedCells1);
		List<Bomb> bombs0 = new ArrayList<>(bombs);
		bombs0.addAll(newlyDroppedBombs(orderedPlayers, bombDropEvents, bombs));
		List<Sq<Sq<Cell>>> nextExplosions = nextExplosions(explosions);
		List<Bomb> bombs1 = new ArrayList<Bomb>();

		for (Bomb bomb : bombs0) {
			if (blastedCells1.contains(bomb.position())) {
				nextExplosions.addAll(bomb.explosion());
			} else if (bomb.fuseLengths().tail().isEmpty()) {
				nextExplosions.addAll(bomb.explosion());
			} else {
				bombs1.add(new Bomb(bomb.ownerId(), bomb.position(), bomb.fuseLengths().tail(), bomb.range()));
			}
		}

		Set<Cell> bombedCells1 = bombs1.stream().map(x -> x.position()).collect(Collectors.toSet());

		List<Player> nextPlayers = nextPlayers(players, playerBonuses, bombedCells1, board1, blastedCells1,
				speedChangeEvents);

		return new GameState(ticks + 1, board1, nextPlayers, bombs1, nextExplosions,
				nextBlasts(blasts, board, explosions));
	}

	/**
	 * This method computes the particles present on the board for the next
	 * state of the game, given the particles for the current state of the game,
	 * the current state of the board and the current explosions.
	 * 
	 * @param blast
	 * @param board0
	 * @param explosions0
	 * @return List<Sq<Cell>> particles present for the next state of the game
	 */
	private static List<Sq<Cell>> nextBlasts(List<Sq<Cell>> blasts0, Board board0, List<Sq<Sq<Cell>>> explosions0) {
		List<Sq<Cell>> blasts1 = new ArrayList<Sq<Cell>>();

		blasts1.addAll(blasts0.stream().filter(x -> !x.tail().isEmpty()).filter(x -> board0.blockAt(x.head()).isFree())
				.map(x -> x.tail()).collect(Collectors.toList()));

		blasts1.addAll(explosions0.stream().filter(x -> !x.isEmpty()).map(x -> x.head()).collect(Collectors.toList()));

		return Collections.unmodifiableList(new ArrayList<>(blasts1));
	}

	/**
	 * Creates a new board for the next state of the game given the current
	 * board, a Set of consumed bonuses and the Set of blasted Cells
	 * corresponding to the next tick.
	 * 
	 * @param board0
	 * @param consumedBonuses
	 * @param blastedCells1
	 * @return Board for next tick
	 */
	private static Board nextBoard(Board board0, Set<Cell> consumedBonuses, Set<Cell> blastedCells1) {
		List<Sq<Block>> board1 = new ArrayList<Sq<Block>>();

		for (Cell c : Cell.ROW_MAJOR_ORDER) {
			if (consumedBonuses.contains(c)) {
				board1.add(Sq.constant(Block.FREE));
			} else if (board0.blockAt(c).equals(Block.DESTRUCTIBLE_WALL) && blastedCells1.contains(c)) {
				Sq<Block> s1 = Sq.repeat(Ticks.WALL_CRUMBLING_TICKS, Block.CRUMBLING_WALL);
				switch (RANDOM.nextInt(NUMBER_OF_BONUSES)) {
				case 0:
					board1.add(s1.concat(Sq.constant(Block.BONUS_BOMB)));
					break;
				case 1:
					board1.add(s1.concat(Sq.constant(Block.BONUS_RANGE)));
					break;
				case 2:
				default:
					board1.add(s1.concat(Sq.constant(Block.FREE)));
					break;
				}
			} else if (board0.blockAt(c).isBonus() && blastedCells1.contains(c)) {
				board1.add(board0.blocksAt(c).limit(Ticks.BONUS_DISAPPEARING_TICKS).concat(Sq.constant(Block.FREE)));
			} else {
				board1.add(board0.blocksAt(c).tail());
			}
		}
		return new Board(board1);
	}

	/**
	 * This method return a list of newly placed Bombs by the players given the
	 * list of current players, a Set of PlayerIds representing new Bombs and a
	 * list of current Bombs on the board.
	 * 
	 * @param players0
	 * @param bombDropEvents
	 * @param bombs0
	 * @return List of newly placed Bombs
	 */
	private static List<Bomb> newlyDroppedBombs(List<Player> players0, Set<PlayerID> bombDropEvents,
			List<Bomb> bombs0) {
		List<Bomb> bombs1 = new ArrayList<Bomb>();
		List<Player> orderedPlayersForBomb = players0.stream().filter(x -> bombDropEvents.contains(x.id()))
				.collect(Collectors.toList());

		Set<Player> wantToDrop = new HashSet<>(orderedPlayersForBomb);
		for (Player eachPlayer : orderedPlayersForBomb) {
			long nbDroppedBombs = (int) bombs0.stream().filter(x -> x.ownerId().equals(eachPlayer.id())).count();
			if (nbDroppedBombs >= eachPlayer.maxBombs()) {
				wantToDrop.remove(eachPlayer);
			}
		}
		List<Cell> occupiedCells = bombs0.stream().map(x -> x.position()).collect(Collectors.toList());
		for (Player eachPlayer : orderedPlayersForBomb) {
			if (eachPlayer.lives() > 0 && wantToDrop.contains(eachPlayer)
					&& !occupiedCells.contains(eachPlayer.position().containingCell())) {
				occupiedCells.add(eachPlayer.position().containingCell());
				bombs1.add(eachPlayer.newBomb());
			}
		}
		return bombs1;
	}

	/**
	 * This method creates the explosions for the next tick by evolving (aging)
	 * the current explosions.
	 * 
	 * @param explosions0
	 * @return List<Sq<Sq<Cell>>> explosions for the next state of the game
	 */
	private static List<Sq<Sq<Cell>>> nextExplosions(List<Sq<Sq<Cell>>> explosions0) {
		return explosions0.stream().filter(x -> !x.isEmpty()).filter(x -> !x.head().tail().isEmpty()).map(x -> x.tail())
				.collect(Collectors.toList());
	}

	/**
	 * This method returns a list of players for the next state of the game
	 * given the current players, a map of PlayersIds to associated Bonuses, the
	 * Sets of bombedCells and blastedCells for the next tick, the next Board,
	 * and a Optional which contains possible Directions for the players.
	 * 
	 * @param players0
	 * @param playerBonuses
	 * @param bombedCells1
	 * @param board1
	 * @param blastedCells1
	 * @param speedChangeEvents
	 * @return list of players for the next tick
	 */
	private static List<Player> nextPlayers(List<Player> players0, Map<PlayerID, Bonus> playerBonuses,
			Set<Cell> bombedCells1, Board board1, Set<Cell> blastedCells1,
			Map<PlayerID, Optional<Direction>> speedChangeEvents) {
		List<Player> players1 = new ArrayList<Player>();

		for (Player eachPlayer : players0) {
			Sq<DirectedPosition> dp1 = eachPlayer.directedPositions();
			Sq<LifeState> newLS = eachPlayer.lifeStates().tail();
			PlayerID playerId = eachPlayer.id();

			DirectedPosition directedPositionCentrale = eachPlayer.directedPositions()
					.findFirst(u -> u.position().isCentral());
			if (speedChangeEvents.containsKey(playerId)) {
				if (!(speedChangeEvents.get(playerId).isPresent())) {
					dp1 = eachPlayer.directedPositions().takeWhile(x -> !(x.position().isCentral()));
					dp1 = dp1.concat(Player.DirectedPosition.stopped(directedPositionCentrale));
				} else if (speedChangeEvents.get(playerId).isPresent()) {
					if (speedChangeEvents.get(playerId).get().isParallelTo(eachPlayer.direction())) {
						dp1 = Player.DirectedPosition.moving(
								new DirectedPosition(eachPlayer.position(), speedChangeEvents.get(playerId).get()));
					} else if (!(speedChangeEvents.get(playerId).get().isParallelTo(eachPlayer.direction()))) {
						dp1 = eachPlayer.directedPositions().takeWhile(x -> !(x.position().isCentral()));
						dp1 = dp1.concat(Player.DirectedPosition.moving(new DirectedPosition(
								directedPositionCentrale.position(), speedChangeEvents.get(playerId).get())));
					}
				}
			}
			Cell c = dp1.head().position().containingCell();
			DirectedPosition actualDp = dp1.head();
			DirectedPosition nextDp = dp1.tail().head();
			boolean nextBlockCanHost = board1.blockAt(c.neighbor(nextDp.direction())).canHostPlayer();

			if (eachPlayer.lifeState().canMove()) {
				if (!(bombedCells1.contains(actualDp.position().containingCell())
						&& nextDp.position().distanceToCentral() < actualDp.position().distanceToCentral()
						&& actualDp.position().distanceToCentral() == 6)) {
					if ((!nextBlockCanHost && !actualDp.position().isCentral()) || nextBlockCanHost) {
						dp1 = dp1.tail();
					}
				}
			}
											
			if (blastedCells1.contains(dp1.head().position().containingCell())
					&& eachPlayer.lifeState().state().equals(State.VULNERABLE)) {
				newLS = eachPlayer.statesForNextLife();
			} else {
				newLS = eachPlayer.lifeStates().tail();
			}
			Player temporaryPlayer = eachPlayer;
			if (playerBonuses.containsKey(temporaryPlayer.id())) {
				temporaryPlayer = playerBonuses.get(temporaryPlayer.id()).applyTo(temporaryPlayer);
			}
			players1.add(
					new Player(eachPlayer.id(), newLS, dp1, temporaryPlayer.maxBombs(), temporaryPlayer.bombRange()));
		}
		return players1;
	}
}