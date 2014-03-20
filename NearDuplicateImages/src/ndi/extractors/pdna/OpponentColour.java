package ndi.extractors.pdna;

/**
 * Represents a colour in the opponent colour model.
 * @author stewart
 */
public class OpponentColour {
    private double i, o1, o2;


    /**
     * Creates a colour from an int containing RGB channels.
     * @param rgb
     */
    public OpponentColour(int rgb) {
        this((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }


    /**
     * Creates a colour from separate integer RGB channels.
     * @param r
     * @param g
     * @param b
     */
    public OpponentColour(int r, int g, int b) {
        this((double)r / 255, (double)g / 255, (double)b / 255);
    }


    /**
     * Creates a colour from double RGB channels, ranging from 0 to 1.
     * @param r
     * @param g
     * @param b
     */
    public OpponentColour(double r, double g, double b) {
        i = (r + g + b) / 3;
        o1 = (r + g - 2 * b) / 4 + 0.5;
        o2 = (r - 2 * g + b) / 4 + 0.5;
    }


    public double getI() {
        return i;
    }


    public double getO1() {
        return o1;
    }


    public double getO2() {
        return o2;
    }
}