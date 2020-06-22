package ch.epfl.xblast.client;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;

public final class GameState {
    private final List<Player> players;
    private final List<Image> board;
    private final List<Image> bombsAndExplosions;
    private final List<Image> score;
    private final List<Image> ticks;
    
    /**
     * Constructor for the class GameState, builds a GameState given a List of Players and four 
     * lists of Images corresponding to the board, bombs and explosions, score and ticks.
     * 
     * @param players
     * @param board
     * @param bombsAndExplosions
     * @param score
     * @param ticks
     */
    public GameState(List<Player> players, List<Image> board, 
            List<Image> bombsAndExplosions, List<Image> score, List<Image> ticks) {
        this.players = Collections.unmodifiableList(new ArrayList<>(players));
        this.board = Collections.unmodifiableList(new ArrayList<>(board));
        this.bombsAndExplosions = Collections.unmodifiableList(new ArrayList<>(bombsAndExplosions));
        this.score = Collections.unmodifiableList(new ArrayList<>(score));
        this.ticks = Collections.unmodifiableList(new ArrayList<>(ticks));
    }

    public final static class Player {
        private final PlayerID id;
        private final int lives;
        private final SubCell position;
        private final Image image;
        
        /**
         * Constructor for the class Player.
         * 
         * @param id
         * @param lives
         * @param position
         * @param image
         */
        public Player(PlayerID id, int lives, SubCell position, Image image) {
            this.id = id;
            this.lives = lives;
            this.position = position;
            this.image = image;
        }
        
        /**
         * Getter for the player's id.
         * @return player id
         */
        public PlayerID getId() {
            return id;
        }
        
        /**
         * Getter for the player's number of lives.
         * @return int lives
         */
        public int getLives() {
            return lives;
        }
        
        /**
         * Getter for the player's position.
         * @return SubCell position
         */
        public SubCell getPosition() {
            return position;
        }
        
        /**
         * Getter for the player's image.
         * @return image
         */
        public Image getImage() {
            return image;
        }
    }
    
    /**
     * Getter for the board as a list of Images.
     * @return List<Image> board
     */
    public final List<Image> getBoard() {
        return board;
    }
    
    /**
     * Getter for the list of players.
     * @return List<Player>
     */
    public final List<Player> getPlayers() {
        return players;
    }
    
    /**
     * Getter for the Bombs and explosions present on the board as a list of Images.
     * @return List<Image>
     */
    public final List<Image> getBombsAndExplosions() {
        return bombsAndExplosions;
    }
    
    /**
     * Getter for the score as a list of Images.
     * @return List<Image>
     */
    public final List<Image> getScore() {
        return score;
    }
    
    /**
     * Getter for the ticks of the GameState as a list of Images.
     * @return List<Image>
     */
    public final List<Image> getTicks() {
        return ticks;
    }
}