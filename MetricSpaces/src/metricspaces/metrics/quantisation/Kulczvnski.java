package metricspaces.metrics.quantisation;

/**
 * P5 distance measure from "Distance measures for MPEG-7-based retrieval" - Eidenberger
 * @author Stewart MacKenzie-Leigh
 */
public class Kulczvnski extends QuantisationMetric {

    public Kulczvnski(double p) {
        super(p);
    }

    @Override
    protected double distance(double a, double b, double c, double d, int K) {
        if (b + c == 0)
            return 0;
        else
            return a / (b + c);
    }


    @Override
    public String getName() {
        return "Kul";
    }
}
