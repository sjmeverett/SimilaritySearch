package metricspaces.metrics;

import metricspaces.descriptors.Descriptor;

public class SEDByComplexityMetric implements Metric<Descriptor> {
	public static final double FINAL_POWER = 0.486;

	@Override
	public double getDistance(Descriptor x, Descriptor y) {
		double e1 = x.getComplexity();
        double e2 = y.getComplexity();
        double e3 = x.getMergedComplexity(y);

        double t1 = Math.max(0, Math.min(1, (e3 / Math.sqrt(e1 * e2)) - 1));

        return Math.pow(t1, FINAL_POWER);
	}

	@Override
	public String getName() {
		return "SED";
	}
}
