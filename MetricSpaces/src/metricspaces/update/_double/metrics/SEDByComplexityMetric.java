package metricspaces.update._double.metrics;

import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.update.common.AbstractMetric;

public class SEDByComplexityMetric extends AbstractMetric<DoubleDescriptor> {
	public static final double FINAL_POWER = 0.483;

	@Override
	public double getDistance(DoubleDescriptor x, DoubleDescriptor y) {
		double e1 = x.getComplexity();
        double e2 = y.getComplexity();
        double e3 = x.getMergedComplexity(y);

        double t1 = Math.max(0, Math.min(1, (e3 / Math.sqrt(e1 * e2)) - 1));

        return Math.pow(t1, FINAL_POWER);
	}
}
