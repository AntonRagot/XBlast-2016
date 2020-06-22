package ch.epfl.xblast.etape6.events;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player;
import ch.epfl.xblast.server.Player.LifeState.State;

public class PlayerState extends Event {
    int lives = -1;
    int state = -1;
    int maxBombs = -1;
    int bombRange = -1;
    int x = -1;
    int y = -1;

    public PlayerState(Player p) {
        this.lives = p.lives();
        this.state = p.lifeState().state().ordinal();
        this.maxBombs = p.maxBombs();
        this.bombRange = p.bombRange();
        SubCell s = p.position();
        Cell c = s.containingCell();
        this.x = c.x();
        this.y = c.y();
    }

    public PlayerState(int lives, int state, int maxBombs, int bombRange, int x, int y) {
        this.lives = lives;
        this.state = state;
        this.maxBombs = maxBombs;
        this.bombRange = bombRange;
        this.x = x;
        this.y = y;
        

    }

    public PlayerState(int[] event) {
        this(event[0], event[1], event[2], event[3], event[4], event[5]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if (this.getClass().equals(obj.getClass())) {
            PlayerState that = (PlayerState) obj;
            return (this.state == that.state) && (this.bombRange == that.bombRange) && (this.maxBombs == that.maxBombs)
                    && (this.lives == that.lives) && (this.x == that.x) && (this.y == that.y);

        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String l = System.lineSeparator();
        StringBuffer buffer = new StringBuffer();
        buffer.append("lives : " + this.lives + "  ");
        buffer.append("state : " + State.values()[this.state].name() + l);
        buffer.append("maxbombs : " + this.maxBombs + "  ");
        buffer.append("bombRange : " + this.bombRange + l);
        buffer.append("position : (" + this.x + "," + this.y + ")" + l);
        return buffer.toString();
    }

}
