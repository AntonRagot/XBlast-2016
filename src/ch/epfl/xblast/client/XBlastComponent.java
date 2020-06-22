package ch.epfl.xblast.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;

import javax.swing.JComponent;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.client.GameState.Player;

@SuppressWarnings("serial")
public final class XBlastComponent extends JComponent {
    private static final int SIZE_OF_FONT = 25;
	private static final int WINDOW_HEIGHT = 688;
	private static final int WINDOW_WIDTH = 960;
	private static final int yText = 659;
    private final static List<Integer> xText = Arrays.asList(96, 240, 768, 912);
    private final List<Cell> rowMajor = Cell.ROW_MAJOR_ORDER;
    private GameState currentGameState;
    private PlayerID playerIDRequest;
    private final static UnaryOperator<Integer> X_VALUE = x -> 4 * x - 24;
    private final static UnaryOperator<Integer> Y_VALUE = y -> 3 * y - 52;


    /**
     * Override for the method getPreferredSize.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    /**
     * Override for the method paintComponent.
     */
    @Override
    protected void paintComponent(Graphics g0) {
    	if(currentGameState == null ) {
    		return;
    	}
    	List<Image> gameStateBoard = currentGameState.getBoard();
    	List<Image> gameStateExplosion = currentGameState.getBombsAndExplosions();
        Font font = new Font("Arial", Font.BOLD, SIZE_OF_FONT);
        Graphics2D graphic = (Graphics2D) g0;
        graphic.setColor(Color.WHITE);
        graphic.setFont(font);
        List<ch.epfl.xblast.client.GameState.Player> sortedPlayer = new ArrayList<>(
                currentGameState.getPlayers());
        final int HEIGHT_BLOCK = currentGameState.getBoard().get(0)
                .getHeight(null);
        final int WIDTH_BLOCK = currentGameState.getBoard().get(0)
                .getWidth(null);

        //Displaying the Board and explosions.
        for (Cell c : rowMajor) {
            graphic.drawImage(
                    gameStateBoard.get(rowMajor.indexOf(c)),
                    c.x() * WIDTH_BLOCK, c.y() * HEIGHT_BLOCK, null);

            graphic.drawImage(
                    gameStateExplosion.get(rowMajor.indexOf(c)),
                    c.x() * WIDTH_BLOCK, c.y() * HEIGHT_BLOCK, null);
        }

        //Sort player.
        Comparator<Player> comparatorY = Comparator
                .comparingInt(x -> x.getPosition().y());
        List<PlayerID> listIDs = Arrays.asList(PlayerID.values());
        Collections.rotate(listIDs,
                listIDs.size() - (playerIDRequest.ordinal() + 1));
        Comparator<Player> comparatorIDs = Comparator
                .comparingInt(x -> listIDs.indexOf(x.getId()));
        Collections.sort(sortedPlayer,
                comparatorY.thenComparing(comparatorIDs));

        //Displaying the players.
        for (ch.epfl.xblast.client.GameState.Player p : sortedPlayer) {
        	if(p.getLives() > 0) {
            graphic.drawImage(p.getImage(), X_VALUE.apply(p.getPosition().x()),
                    Y_VALUE.apply(p.getPosition().y()), null);
        	}
        }			

        //Displaying the score line.
        int xPositionScore = 0;
        int yPositionScore = rowMajor.get(rowMajor.size() - 1).y() * HEIGHT_BLOCK
                + currentGameState.getScore().get(0).getHeight(null);
        for (Image i : currentGameState.getScore()) {
            graphic.drawImage(i, xPositionScore, yPositionScore, null);
            xPositionScore = xPositionScore + i.getWidth(null);
        }

        //Number of lives.
        currentGameState.getPlayers().forEach(x -> graphic.drawString(Integer.toString(x.getLives()),
                xText.get(x.getId().ordinal()), yText));

        //Displaying the time(ticks) line.
        int xPositionTicks = 0;
        int yPositionTicks = yPositionScore
                + currentGameState.getTicks().get(0).getHeight(null);
        for (Image i : currentGameState.getTicks()) {
            graphic.drawImage(i, xPositionTicks,
                    yPositionTicks + (i.getHeight(null) * 2), null);
            xPositionTicks = xPositionTicks + i.getWidth(null);
        }
    }

    /**
     * Method that changes the current GameState given a new GameState and a PlayerID (of the Player for which
     * this new GameState will be displayed).
     * 
     * @param g
     * @param id
     */
    public void setGameState(GameState g, PlayerID id) {
        currentGameState = g;
        playerIDRequest = id;
        this.repaint();
    }
}