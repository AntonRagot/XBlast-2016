package ch.epfl.xblast;

public interface Time {
    /**
     * This interface contains various constants relating to time conversion.
     * 
     */
    //Second per minute
    public final static int S_PER_MIN= 60; 
    //Milisecond per second
    public final static int MS_PER_S= 1000;
    //Picosecond per second
    public final static int US_PER_S = MS_PER_S * 1000;
    //nanosecond per second
    public final static int NS_PER_S = US_PER_S * 1000;
}