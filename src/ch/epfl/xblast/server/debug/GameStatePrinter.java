package ch.epfl.xblast.server.debug;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.Bomb;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;

public final class GameStatePrinter {
    
    private GameStatePrinter() { }

    public static void printGameState(GameState s) {
        List<Player> ps = s.alivePlayers();
        Board board = s.board();
        Set<Cell> blasts = s.blastedCells();
        Map<Cell, Bomb> bombs = s.bombedCells();
        for (int y = 0; y < Cell.ROWS; ++y) {
            xLoop: for (int x = 0; x < Cell.COLUMNS; ++x) {
                Cell c = new Cell(x, y);
                for (Player p : ps) {
                    if (/*!blasts.contains(c) && !bombs.containsKey(c)
                            &&*/ p.position().containingCell().equals(c)) {
                        System.out.print(stringForPlayer(p));
                        continue xLoop;
                    }
                }
                if (blasts.contains(c) && board.blockAt(c).equals(Block.FREE)) {
                    System.out.print(stringForBlasts(s.blastedCells()));
                } else if (bombs.containsKey(c)) {
                    System.out.print(stringForBomb(bombs.get(c)));
                } else {
                    Block b = board.blockAt(c);
                    
                    System.out.print(stringForBlock(b));
                }
            }
            System.out.println();
        }
    }

    private static String stringForPlayer(Player p) {
        StringBuilder b = new StringBuilder();
        b.append("\u001b[46m" + (p.id().ordinal() + 1) + "\u001b[49m");
        switch (p.direction()) {
        case N:
            b.append( "\u001b[46m" + '^' + "\u001b[49m");
            break;
        case E:
            b.append( "\u001b[46m" + '>' + "\u001b[49m");
            break;
        case S:
            b.append( "\u001b[46m" + 'v' + "\u001b[49m");
            break;
        case W:
            b.append("\u001b[46m" + '<' + "\u001b[49m");
            break;
        }
        return b.toString();
    }

    private static String stringForBlock(Block b) {
        switch (b) {
        case FREE:
            return "\u001b[1;47m" + "  " + "\u001b[49m";
        case INDESTRUCTIBLE_WALL:
            return "\u001b[40m" + "  " + "\u001b[49m";
        case DESTRUCTIBLE_WALL:
            return "\u001b[40m" + "??" + "\u001b[49m";
        case CRUMBLING_WALL:
            return "\u001b[5m" + "¿¿" + "\u001b[25m";
        case BONUS_BOMB:
            return "\u001b[41m" + "+b" + "\u001b[25m";
        case BONUS_RANGE:
            return "\u001b[41m" + "+*" + "\u001b[25m";
        default:
            throw new Error();
        }
    }

    private static String stringForBlasts(Set<Cell> bl) {
        StringBuilder b = new StringBuilder();
        b.append("\u001b[43m" + "**" + "\u001b[25m");
        return b.toString();
    }

    private static String stringForBomb(Bomb b) {
        StringBuilder bl = new StringBuilder();
        if (b.fuseLength() < 10) {
            bl.append("\u001b[43m" + "B" + b.fuseLength() + "\u001b[25m");
        } else {
            bl.append(b.fuseLength());
        }
        return bl.toString();
    }
}