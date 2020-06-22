package ch.epfl.xblast;

public final class ArgumentChecker {

    /**
     * Constructor for the class ArgumentChecker.
     * 
     */
    private ArgumentChecker() { }

    /**
     * Method that checks if a given value is strictly negative.
     * 
     * @param value
     * @throws IllegalArgumentException
     *             if the value is negative
     * @return received value if equal to 0 or positive
     */
    public static int requireNonNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException(
                    "The value " + value + " is negative");
        } else {
            return value;
        }
    }
}