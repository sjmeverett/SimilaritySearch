package metricspaces.metrics.quantisation;

/**
 * P6 distance measure from "Distance measures for MPEG-7-based retrieval" - Eidenberger
 */
public class PatternDifference extends QuantisationMetric {

    public PatternDifference(double p) {
        super(p);
    }

    @Override
    protected double distance(double a, double b, double c, double d, int K) {
        return b * c / (K * K);
    }


    @Override
	public String getName() {
		return "PD";
	}
}
