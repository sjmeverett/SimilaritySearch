package ndi.extractors.pdna;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class Histogram {
    private int[] bins;
    private double interval;

    public Histogram(int numberOfBins, double maxValue) {
        bins = new int[numberOfBins];
        interval = maxValue / numberOfBins;
    }


    public void addValue(double value) {
        int bin = (int)Math.ceil(value / interval);
        if (bin > 0) bin--;

        bins[bin]++;
    }


    public int[] getCounts() {
        return bins;
    }


    public double[] getL1Normalised() {
        int norm = 0;

        for (int i = 0; i < bins.length; i++)
            norm += bins[i];

        double[] normalised = new double[bins.length];

        if (norm == 0)
            return normalised;

        for (int i = 0; i < bins.length; i++)
            normalised[i] = (double)bins[i] / norm;

        return normalised;
    }


    public byte[] getL1NormalisedByte() {
        SortedMap<Double, Integer> map = new TreeMap<Double, Integer>(Collections.reverseOrder());
        double[] point = getL1Normalised();
        byte[] bytePoint = new byte[point.length];
        int total = 0;

        for (int i = 0; i < point.length; i++) {
            final double p = point[i];
            final byte b = (byte)(point[i] * 255);

            map.put(p - (int)p, i);
            bytePoint[i] = b;
            total += b;
        }

        for (int i: map.values()) {
            if (total >= 255)
                break;

            bytePoint[i]++;
            total++;
        }

        return bytePoint;
    }
}
