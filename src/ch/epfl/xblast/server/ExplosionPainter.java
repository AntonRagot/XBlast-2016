package ch.epfl.xblast.server;

import ch.epfl.xblast.server.Bomb;

public final class ExplosionPainter {
    private static final byte BOMB_IMAGE_BLACK = 20;
	private static final byte BOMB_IMAGE_WHITE = 21;
	public static final byte BYTE_FOR_EMPTY = 16;

    /**
     * Private constructor for the class ExplosionPainter.
     */
    private ExplosionPainter () { }
    
    /**
     * Returns the byte for Bomb image; if the fuse length of the image is a power of 2 (so if the 
     * fuse length written as a byte has a single bit at 1) then the image used is the white one, 
     * otherwise it is the black one.
     * 
     * @param bomb
     * @return byte for Bomb image
     */
    public static byte byteForBomb(Bomb bomb) {
        return Integer.bitCount(bomb.fuseLength()) == 1 ?
                BOMB_IMAGE_WHITE
              : BOMB_IMAGE_BLACK;
    }
    
    /**
     * Returns the byte of the image for an explosion particle given fours booleans 
     * which correspond to the four possible directions around the particle (true if 
     * free, false if already occupied by another Block).
     * 
     * @param N
     * @param E
     * @param S
     * @param W
     * @return byte for blast image
     */
    public static byte byteForBlast(boolean N, boolean E, boolean S, boolean W) {
        return (byte) (directionBit(N) << 3 | directionBit(E) << 2 | directionBit(S) << 1 | directionBit(W));
    }
    
    /**
     * Method that given a boolean returns 1 if true, or 0 if false.
     * 
     * @param dir
     * @return int
     */
    private static int directionBit(boolean dir) {
        return (dir ? 1 : 0);
    }
}