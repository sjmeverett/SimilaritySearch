package metricspaces.quantised.metrics;

import metricspaces.metrics.AbstractMetric;
import metricspaces.quantised.QuantisedDescriptor;
import metricspaces.quantised.QuantisedDescriptorContext;

public class SEDByComplexityMetric extends AbstractMetric<QuantisedDescriptor> {
	public static final double FINAL_POWER = 0.483;

	@Override
	public double getDistance(QuantisedDescriptor x, QuantisedDescriptor y) {
		if (x.getContext() != y.getContext())
			throw new UnsupportedOperationException("contexts don't match");
		
		QuantisedDescriptorContext context = x.getContext();
		byte[] xd = x.getData();
		byte[] yd = y.getData();
		count++;
		
		double e1 = context.getComplexity(xd);
        double e2 = context.getComplexity(yd);
        double e3 = context.getMergedComplexity(xd, yd);

        double t1 = Math.max(0, Math.min(1, (e3 / Math.sqrt(e1 * e2)) - 1));
        return Math.pow(t1, FINAL_POWER);
	}
}
