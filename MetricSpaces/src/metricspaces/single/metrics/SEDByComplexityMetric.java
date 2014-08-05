package metricspaces.single.metrics;

import metricspaces.metrics.AbstractMetric;
import metricspaces.single.SingleDescriptor;

public class SEDByComplexityMetric extends AbstractMetric<SingleDescriptor> {
	public static final float FINAL_POWER = 0.483f;

	@Override
	public double getDistance(SingleDescriptor x, SingleDescriptor y) {
		float e1 = x.getComplexity();
        float e2 = y.getComplexity();
        float e3 = x.getMergedComplexity(y);

        float t1 = (float)Math.max(0f, Math.min(1f, (e3 / Math.sqrt(e1 * e2)) - 1f));

        return (float)Math.pow(t1, FINAL_POWER);
	}
}
