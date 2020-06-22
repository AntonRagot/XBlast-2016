package ch.epfl.xblast;

import static ch.epfl.xblast.GameStateTestUtils.addToSet;
import static ch.epfl.xblast.GameStateTestUtils.asSet;
import static ch.epfl.xblast.GameStateTestUtils.bombDecreased;
import static ch.epfl.xblast.GameStateTestUtils.removeFromSet;
import static ch.epfl.xblast.GameStateTestUtils.xAssertEquals;
import static ch.epfl.xblast.GameStateTestUtils.xAssertNotEquals;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.Bomb;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Ticks;

public class GameStateTest05 {

    private static final Cell POS_NW = new Cell(1, 1);
    private static final Cell POS_NE = new Cell(-2, 1);
    private static final Cell POS_SE = new Cell(-2, -2);
    private static final Cell POS_SW = new Cell(1, -2);
    private static final Cell POS_CENTER = new Cell(7, 6);

    private static final List<Cell> CELLS = Arrays.asList(POS_NW, POS_NE, POS_SE, POS_SW);

    private static Board createBoard() {
        Block __ = Block.FREE;
        Block XX = Block.INDESTRUCTIBLE_WALL;
        Block xx = Block.DESTRUCTIBLE_WALL;
        return Board.ofQuadrantNWBlocksWalled(
                Arrays.asList(Arrays.asList(__, __, __, __, __, xx, __), Arrays.asList(__, XX, xx, XX, xx, XX, xx),
                        Arrays.asList(__, xx, __, __, __, xx, __), Arrays.asList(xx, XX, __, XX, XX, XX, XX),
                        Arrays.asList(__, xx, __, xx, __, __, __), Arrays.asList(xx, XX, xx, XX, xx, XX, __)));
    }

    private static Board createBoard(Block centralBlock) {
        Block __ = Block.FREE;
        return Board.ofQuadrantNWBlocksWalled(Arrays.asList(Arrays.asList(__, __, __, __, __, __, __),
                Arrays.asList(__, __, __, __, __, __, __), Arrays.asList(__, __, __, __, __, __, __),
                Arrays.asList(__, __, __, __, __, __, __), Arrays.asList(__, __, __, __, __, __, __),
                Arrays.asList(__, __, __, __, __, __, centralBlock)));
    }

    private static List<Player> createPlayers(int lives, int maxBombs, int bombRange, Cell p1, Cell p2, Cell p3,
                                              Cell p4) {
        return Arrays.asList(new Player(PlayerID.PLAYER_1, lives, p1, maxBombs, bombRange),
                new Player(PlayerID.PLAYER_2, lives, p2, maxBombs, bombRange),
                new Player(PlayerID.PLAYER_3, lives, p3, maxBombs, bombRange),
                new Player(PlayerID.PLAYER_4, lives, p4, maxBombs, bombRange));
    }

    private static List<Player> createPlayers() {
        return createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW);
    }

    private static List<Bomb> createBombs() {
        return CELLS.stream().map(c -> new Bomb(PlayerID.PLAYER_1, c, 3, 3)).collect(toList());
    }

    private static List<Sq<Cell>> createBlasts() {
        return CELLS.stream().map(Sq::constant).collect(toList());
    }

    private static void bonusDisappearsPlayer(Block block) {
        Board board = createBoard(block);
        List<Player> players = Arrays.asList(new Player(PlayerID.PLAYER_1, 1, POS_CENTER, 1, 3),
                new Player(PlayerID.PLAYER_2, 1, POS_NW, 0, 3), new Player(PlayerID.PLAYER_3, 1, POS_SE, 0, 3),
                new Player(PlayerID.PLAYER_4, 1, POS_SW, 0, 3));

        GameState gameState = new GameState(0, board, players, emptyList(), emptyList(),
                emptyList());
        assertEquals(block, gameState.board().blockAt(POS_CENTER));

        gameState = gameState.next(emptyMap(), emptySet());
        assertEquals(Block.FREE, gameState.board().blockAt(POS_CENTER));
    }

    private static void bonusDisappearsBlast(Block block) {
        Board board = createBoard(block);
        List<Player> players = Arrays.asList(new Player(PlayerID.PLAYER_1, 1, POS_NE, 1, 3),
                new Player(PlayerID.PLAYER_2, 1, POS_NW, 0, 3), new Player(PlayerID.PLAYER_3, 1, POS_SE, 0, 3),
                new Player(PlayerID.PLAYER_4, 1, POS_SW, 0, 3));

        List<Sq<Cell>> blasts =
                singletonList(Sq.iterate(POS_CENTER.neighbor(Direction.N), c -> c.neighbor(Direction.S)));

        GameState gameState = new GameState(0, board, players, emptyList(), emptyList(),
                blasts);

        assertEquals(block, gameState.board().blockAt(POS_CENTER));

        for (int i = 0; i < Ticks.EXPLOSION_TICKS; ++i) {
            gameState = gameState.next(emptyMap(), emptySet());
            assertEquals(block, gameState.board().blockAt(POS_CENTER));
        }

        gameState = gameState.next(emptyMap(), emptySet());
        assertEquals(Block.FREE, gameState.board().blockAt(POS_CENTER));
    }

    private static void bonusDisappearsPlayerBeforeBlast(Block block) {
        Board board = createBoard(block);
        List<Player> players = Arrays.asList(new Player(PlayerID.PLAYER_1, 1, POS_CENTER.neighbor(Direction.W), 1, 3),
                new Player(PlayerID.PLAYER_2, 1, POS_NW, 0, 3), new Player(PlayerID.PLAYER_3, 1, POS_SE, 0, 3),
                new Player(PlayerID.PLAYER_4, 1, POS_SW, 0, 3));

        List<Sq<Cell>> blasts =
                singletonList(Sq.iterate(POS_CENTER.neighbor(Direction.N), c -> c.neighbor(Direction.S)));

        GameState gameState = new GameState(0, board, players, emptyList(), emptyList(),
                blasts);

        assertEquals(block, gameState.board().blockAt(POS_CENTER));

        singletonMap(PlayerID.PLAYER_1, Direction.E);

        for (int i = 0; i <= Ticks.EXPLOSION_TICKS / 2.; ++i) {
            gameState = gameState.next(singletonMap(PlayerID.PLAYER_1, Optional.of(Direction.E)),
                    emptySet());
            assertEquals(block, gameState.board().blockAt(POS_CENTER));
        }

        gameState = gameState.next(emptyMap(), emptySet());
        assertEquals(Block.FREE, gameState.board().blockAt(POS_CENTER));
    }

    private static void assertBlastsStoppedBy(Block block) {
        Board board = createBoard(block);
        List<Player> players = createPlayers();

        // Create blast in direction of the center cell
        List<Sq<Cell>> singleBlast =
                singletonList(Sq.iterate(POS_CENTER.neighbor(Direction.W), q -> q.neighbor(Direction.E)));

        GameState gameState = new GameState(0, board, players, emptyList(), emptyList(),
                singleBlast);

        gameState = gameState.next(emptyMap(), emptySet());
        assertEquals(1, gameState.blastedCells().size());
        assertTrue(gameState.blastedCells().contains(POS_CENTER));

        gameState = gameState.next(emptyMap(), emptySet());
        assertEquals(0, gameState.blastedCells().size());
    }

    @Test
    public void xAssertEqualsCorrectlyAssert() {
        GameState ig1 = new GameState(createBoard(), createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        GameState ig2 = new GameState(createBoard(), createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        GameState ig3 = new GameState(createBoard(), createPlayers(3, 1, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        GameState ig4 = new GameState(createBoard(), createPlayers(2, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        GameState ig5 = new GameState(createBoard(), createPlayers(3, 2, 2, POS_NW, POS_NE, POS_SE, POS_SW));
        GameState ig6 = new GameState(createBoard(), createPlayers(3, 2, 3, POS_NE, POS_NW, POS_SE, POS_SW));
        GameState ig7 = new GameState(createBoard(Block.FREE), createPlayers(3, 2, 3, POS_NE, POS_NW, POS_SE, POS_SW));
        xAssertEquals(ig1, ig2);
        xAssertNotEquals(ig1, ig3);
        xAssertNotEquals(ig1, ig4);
        xAssertNotEquals(ig1, ig5);
        xAssertNotEquals(ig1, ig6);
        xAssertNotEquals(ig1, ig7);
    }

    @Test
    public void bombedCellsIsWorking() {
        GameState ig1 = new GameState(createBoard(), createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        assertTrue(ig1.bombedCells().isEmpty());

        GameState ig2 = new GameState(0, createBoard(), createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW),
                createBombs(), emptyList(), emptyList());
        assertEquals(new HashSet<>(CELLS), ig2.bombedCells().keySet());
    }

    @Test
    public void bombedCellsImmutable() {
        List<Bomb> bombs = createBombs();
        GameState ig = new GameState(0, createBoard(), createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW), bombs,
                emptyList(), emptyList());
        Map<Cell, Bomb> bombedCells1 = ig.bombedCells();
        bombs.remove(0);
        Map<Cell, Bomb> bombedCells2 = ig.bombedCells();
        assertEquals(bombedCells1, bombedCells2);
    }

    @Test
    public void blastedCellsIsWorking() {
        GameState ig1 = new GameState(createBoard(), createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW));
        assertTrue(ig1.blastedCells().isEmpty());

        GameState ig2 = new GameState(0, createBoard(), createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW),
                emptyList(), emptyList(), createBlasts());
        assertEquals(new HashSet<>(CELLS), ig2.blastedCells());
    }

    @Test
    public void blastedCellsImmutable() {
        List<Sq<Cell>> blasts = createBlasts();
        GameState ig = new GameState(0, createBoard(), createPlayers(3, 2, 3, POS_NW, POS_NE, POS_SE, POS_SW),
                emptyList(), emptyList(), blasts);
        Set<Cell> blastedCells1 = ig.blastedCells();
        blasts.remove(0);
        Set<Cell> blastedCells2 = ig.blastedCells();
        assertEquals(blastedCells1, blastedCells2);
    }

    @Test
    public void newlyDroppedBombed() {
        Board board = createBoard();
        List<Player> players = createPlayers();
        GameState gameState = new GameState(board, players);

        Player droppingPlayer = players.get(0);
        gameState = gameState.next(emptyMap(), singleton(droppingPlayer.id()));

        Map<Cell, Bomb> cellBombMap = gameState.bombedCells();
        assertEquals(1, cellBombMap.size());

        Bomb droppedBomb = cellBombMap.get(droppingPlayer.position().containingCell());
        assertNotNull(droppedBomb);
        assertEquals(droppedBomb.range(), droppingPlayer.bombRange());
        assertEquals(Ticks.BOMB_FUSE_TICKS - 1, droppedBomb.fuseLength());
    }

    @Test
    public void cannotDropBombWhenDead() {
        Board board = createBoard(Block.FREE);
        List<Player> players = Arrays.asList(new Player(PlayerID.PLAYER_1, 0, POS_NE, 0, 3),
                new Player(PlayerID.PLAYER_2, 1, POS_NW, 0, 3), new Player(PlayerID.PLAYER_3, 1, POS_SE, 0, 3),
                new Player(PlayerID.PLAYER_4, 1, POS_SW, 0, 3));

        GameState gameState = new GameState(board, players);
        gameState = gameState.next(emptyMap(), singleton(PlayerID.PLAYER_1));

        assertTrue(gameState.bombedCells().isEmpty());
    }

    @Test
    public void cannotDropBombWhenMaxReached() {
        Board board = createBoard(Block.FREE);
        List<Player> players = createPlayers();

        List<Bomb> existingBombs = new ArrayList<>();
        Player droppingPlayer = players.get(0);
        for (int i = 0; i < droppingPlayer.maxBombs(); i++) {
            existingBombs.add(new Bomb(droppingPlayer.id(), new Cell(i + 1, POS_CENTER.y()), 10, 3));
        }

        GameState gameState = new GameState(0, board, players, existingBombs, emptyList(),
                emptyList());
        gameState = gameState.next(emptyMap(), singleton(droppingPlayer.id()));

        assertEquals(2, gameState.bombedCells().size());
        assertFalse(gameState.bombedCells().containsKey(droppingPlayer.position().containingCell()));
    }

    @Test
    public void bombToExplosion() {
        Board board = createBoard(Block.FREE); // The bomb should live during 2
        // ticks Bomb bomb
        Bomb bomb = new Bomb(PlayerID.PLAYER_1, POS_CENTER, 2, 2);

        GameState gameState = new GameState(0, board, createPlayers(), singletonList(bomb),
                emptyList(), emptyList());

        Collection<Bomb> result = gameState.bombedCells().values();
        assertEquals(1, result.size());
        xAssertEquals(bomb, result.iterator().next());

        gameState = gameState.next(emptyMap(), emptySet());
        result = gameState.bombedCells().values();
        assertEquals(1, result.size());
        xAssertEquals(bombDecreased(bomb), result.iterator().next());

        gameState = gameState.next(emptyMap(), emptySet());
        result = gameState.bombedCells().values();
        assertEquals(0, result.size());

        gameState = gameState.next(emptyMap(), emptySet());
        assertEquals(singleton(bomb.position()), gameState.blastedCells());

        gameState = gameState.next(emptyMap(), emptySet());

        Set<Cell> centerNeighbors = Stream.of(Direction.values()).map(POS_CENTER::neighbor).collect(toSet());

        centerNeighbors.add(POS_CENTER);
        assertEquals(centerNeighbors, gameState.blastedCells());

        for (int i = 0; i <= Ticks.EXPLOSION_TICKS - 3; ++i) {
            gameState = gameState.next(emptyMap(), emptySet());
            assertEquals(centerNeighbors, gameState.blastedCells());
        }

        gameState = gameState.next(emptyMap(), emptySet());

        centerNeighbors.remove(POS_CENTER);
        assertEquals(centerNeighbors, gameState.blastedCells());

        gameState = gameState.next(emptyMap(), emptySet());
        assertEquals(emptySet(), gameState.blastedCells());
    }

    @Test
    public void cannotDropBombWhenCellOccupied() {
        Board board = createBoard(Block.FREE);
        List<Player> players = createPlayers();

        Player droppingPlayer = players.get(0);
        Player otherPlayer = players.get(1);

        List<Bomb> existingBombs =
                singletonList(new Bomb(otherPlayer.id(), droppingPlayer.position().containingCell(), 3, 3));

        GameState gameState = new GameState(0, board, players, existingBombs, emptyList(),
                emptyList());
        gameState = gameState.next(emptyMap(), singleton(droppingPlayer.id()));

        assertEquals(1, gameState.bombedCells().size());
        assertEquals(otherPlayer.id(),
                gameState.bombedCells().get(droppingPlayer.position().containingCell()).ownerId());
    }

    @Test
    public void wallCrumblesWhenHit() {
        Board board = createBoard(Block.DESTRUCTIBLE_WALL);
        List<Player> players = createPlayers();

        Cell centerNeighbor = POS_CENTER.neighbor(Direction.W);

        // Create blast in direction of the center cell
        List<Sq<Cell>> singleBlast =
                singletonList(Sq.iterate(centerNeighbor, q -> q.neighbor(Direction.E)));

        GameState gameState = new GameState(0, board, players, emptyList(), emptyList(),
                singleBlast);

        assertEquals(Block.DESTRUCTIBLE_WALL, gameState.board().blockAt(POS_CENTER));
        for (int i = 0; i < Ticks.WALL_CRUMBLING_TICKS; i++) {
            gameState = gameState.next(emptyMap(), emptySet());
            assertEquals(Block.CRUMBLING_WALL, gameState.board().blockAt(POS_CENTER));
        }

        gameState = gameState.next(emptyMap(), emptySet());

        Set<Block> possibleBlocks = asSet(Block.BONUS_BOMB, Block.BONUS_RANGE, Block.FREE);
        assertTrue(possibleBlocks.contains(gameState.board().blockAt(POS_CENTER)));
    }

    @Test
    public void bonusBombDisappearsPlayer() {
        bonusDisappearsPlayer(Block.BONUS_BOMB);
    }

    @Test
    public void bonusRangeDisappearsPlayer() {
        bonusDisappearsPlayer(Block.BONUS_RANGE);
    }

    @Test
    public void bonusBombDisappearsBlast() {
        bonusDisappearsBlast(Block.BONUS_BOMB);
    }

    @Test
    public void bonusRangeDisappearsBlast() {
        bonusDisappearsBlast(Block.BONUS_RANGE);
    }

    /*
     * This test is ignored as it uses some feature of step 6 that we do not want to test yet
     * Remove Ignore and add test if you want to
     */
    @Ignore
    public void bonusBombDisappearsPlayerBeforeBlast() {
        bonusDisappearsPlayerBeforeBlast(Block.BONUS_BOMB);
    }

    @Ignore
    public void bonusRangeDisappearsPlayerBeforeBlast() {
        bonusDisappearsPlayerBeforeBlast(Block.BONUS_RANGE);
    }

    @Test
    public void droppedBombConflictNotOnlySamePlayer() {
        Board board = createBoard(Block.FREE);
        List<Player> players = Arrays.asList(new Player(PlayerID.PLAYER_1, 30, POS_CENTER, 10, 3),
                new Player(PlayerID.PLAYER_2, 30, POS_CENTER, 10, 3),
                new Player(PlayerID.PLAYER_3, 30, POS_CENTER, 10, 3),
                new Player(PlayerID.PLAYER_4, 30, POS_CENTER, 10, 3));

        List<PlayerID> bombsDropped = new ArrayList<>();
        Set<PlayerID> bombEvents = asSet(PlayerID.values());

        GameState gameState;

        int fact = IntStream.range(1, players.size() + 1).reduce(1, (x, y) -> x * y);

        for (int i = 1; i <= fact * 100; ++i) {
            gameState = new GameState(i - 1, board, players, emptyList(), emptyList(),
                    emptyList());
            assertEquals(0, gameState.bombedCells().size());
            gameState = gameState.next(emptyMap(), bombEvents);
            assertEquals(1, gameState.bombedCells().size());
            bombsDropped.add(gameState.bombedCells().values().iterator().next().ownerId());
        }

        // Remember : in a set all the elements are unique, if the size of the
        // given set is 1, then we can be sure that the bombs were dropped in a fair way
        assertEquals(1, asSet(PlayerID.values()).stream().map(p -> bombsDropped.stream().filter(p2 -> p2 == p).count())
                .collect(toSet()).size());
    }


    @Test
    public void blastsMoveCorrectly() {
        Board board = createBoard(Block.FREE);
        List<Player> players = createPlayers();

        // Create a single blast
        int blastRange = 5;
        Direction blastDirection = Direction.E;
        Cell blastStartPosition = POS_CENTER;

        List<Sq<Cell>> singleBlast =
                singletonList(Sq.iterate(blastStartPosition, q -> q.neighbor(blastDirection)).limit(blastRange));

        // Create a game state with a single blast
        GameState gameState = new GameState(0, board, players, emptyList(), emptyList(),
                singleBlast);

        // The blast should move at each tick
        Cell nextExpectedPos = blastStartPosition;
        for (int i = 0; i < blastRange; i++) {
            assertEquals(1, gameState.blastedCells().size());
            assertTrue(gameState.blastedCells().contains(nextExpectedPos));

            gameState = gameState.next(emptyMap(), emptySet());
            nextExpectedPos = nextExpectedPos.neighbor(blastDirection);
        }

        // Eventually the blast should disappears
        assertEquals(0, gameState.blastedCells().size());
    }

    @Test
    public void blastsStoppedByWalls() {
        Set<Block> blockingBlocks = asSet(Block.BONUS_BOMB, Block.BONUS_RANGE, Block.CRUMBLING_WALL,
                Block.DESTRUCTIBLE_WALL, Block.INDESTRUCTIBLE_WALL);

        blockingBlocks.forEach(b -> assertBlastsStoppedBy(b));
    }

    @Test
    public void completeEvolutionOfExplosion() {
        Board board = createBoard(Block.FREE);
        int bombRange = 5;
        List<Player> players = Arrays.asList(new Player(PlayerID.PLAYER_1, 3, POS_CENTER, 1, bombRange),
                new Player(PlayerID.PLAYER_2, 3, POS_NW, 0, 3), new Player(PlayerID.PLAYER_3, 3, POS_SE, 0, 3),
                new Player(PlayerID.PLAYER_4, 3, POS_SW, 0, 3));

        GameState gameState = new GameState(0, board, players, emptyList(), emptyList(),
                emptyList());

        // First step : the bomb consumes its fuse length
        for (int i = 1; i < Ticks.BOMB_FUSE_TICKS; ++i) {
            gameState = gameState.next(emptyMap(), asSet(PlayerID.PLAYER_1));
            assertEquals(1, gameState.bombedCells().size());
            assertEquals(0, gameState.blastedCells().size());
            xAssertEquals(new Bomb(PlayerID.PLAYER_1, POS_CENTER, Ticks.BOMB_FUSE_TICKS - i, bombRange), gameState.bombedCells().values().iterator().next());
        }

        // We create here the successive positions of the blasts
        // (for each direction, we store one arm of the explosion at the given tick)
        Map<Direction, Sq<Set<Cell>>> neigbors = new HashMap<>();
        for (Direction d : Direction.values()) {
            UnaryOperator<Set<Cell>> addToSet = u -> addToSet(u, u.stream().map(c -> c.neighbor(d)).collect(toSet()));
            Sq<Set<Cell>> cells = Sq.iterate(asSet(POS_CENTER), addToSet).limit(bombRange);

            Set<Cell> maxCells = cells.findFirst(s -> s.size() == bombRange);
            cells = cells.concat(Sq.repeat(Ticks.EXPLOSION_TICKS - bombRange, maxCells));
            cells = cells.concat(Sq.repeat(1, removeFromSet(maxCells, cells.head())));
            cells = cells.concat(Sq.repeat(1, removeFromSet(maxCells, cells.tail().head())));
            cells = cells.concat(Sq.repeat(1, removeFromSet(maxCells, cells.tail().tail().head())));
            cells = cells.concat(Sq.repeat(1, removeFromSet(maxCells, cells.tail().tail().tail().head())));
            cells = cells.concat(Sq.repeat(1, removeFromSet(maxCells, cells.tail().tail().tail().tail().head())));
            neigbors.put(d, cells);
        }

        gameState = gameState.next(emptyMap(), emptySet());

        // Second step : the bomb disappears and the blasts appear
        for (int i = 0; i < Ticks.EXPLOSION_TICKS + bombRange; ++i) {
            gameState = gameState.next(emptyMap(), emptySet());
            assertEquals(0, gameState.bombedCells().size());

            Set<Cell> cells = new HashSet<>();
            for (Map.Entry<Direction, Sq<Set<Cell>>> entry : neigbors.entrySet()) {
                cells.addAll(entry.getValue().head());
                neigbors.put(entry.getKey(), entry.getValue().tail());
            }
            assertEquals(cells, gameState.blastedCells());
        }

        assertEquals(0, gameState.bombedCells().size());
        assertEquals(0, gameState.blastedCells().size());
    }
}