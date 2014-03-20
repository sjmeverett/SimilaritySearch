package metricspaces.metrics.quantisation;

/**
 * P2 distance measure from "Distance measures for MPEG-7-based retrieval" - Eidenberger
 */
public class NumberOfCoOccurences extends QuantisationMetric {

    public NumberOfCoOccurences(double p) {
        super(p);
    }

    @Override
    protected double distance(double a, double b, double c, double d, int K) {
        return max * K - a;
    }


    @Override
	public String getName() {
		return "NCO";
	}	
}
