package metricspaces.metrics.quantisation;

/**
 * P1 distance measure from "Distance measures for MPEG-7-based retrieval" - Eidenberger
 */
public class FeatureContrastModel extends QuantisationMetric {
	private double alpha, beta;

	
	public FeatureContrastModel(double p, double alpha, double beta) {
        super(p);
		this.alpha = alpha;
		this.beta = beta;
	}


    @Override
    protected double distance(double a, double b, double c, double d, int K) {
        return a - alpha * b - beta * c;
    }


    @Override
	public String getName() {
		return "FCM";
	}
}
