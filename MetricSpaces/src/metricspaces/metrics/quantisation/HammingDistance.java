package metricspaces.metrics.quantisation;

/**
 * P3 distance measure from "Distance measures for MPEG-7-based retrieval" - Eidenberger
 */
public class HammingDistance extends QuantisationMetric {

    public HammingDistance(double p) {
        super(p);
    }

    @Override
    protected double distance(double a, double b, double c, double d, int K) {
        return b + c;
    }


    @Override
	public String getName() {
		return "Ham";
	}
}
