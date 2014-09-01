package metricspaces.single.metrics;

import metricspaces.metrics.AbstractMetric;
import metricspaces.single.SingleDescriptor;

public class SEDByComplexityMetric extends AbstractMetric<SingleDescriptor> {
	public static final double FINAL_POWER = 0.483f;

	@Override
	public double getDistance(SingleDescriptor x, SingleDescriptor y) {
		double e1 = x.getComplexity();
        double e2 = y.getComplexity();
        double e3 = x.getMergedComplexity(y);
		count++;

        double t1 = Math.max(0f, Math.min(1f, (e3 / Math.sqrt(e1 * e2)) - 1f));

        return Math.pow(t1, FINAL_POWER);
	}
}
