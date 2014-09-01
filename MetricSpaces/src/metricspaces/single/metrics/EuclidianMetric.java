package metricspaces.single.metrics;

import metricspaces.metrics.AbstractMetric;
import metricspaces.single.SingleDescriptor;

public class EuclidianMetric extends AbstractMetric<SingleDescriptor> {

	@Override
	public double getDistance(SingleDescriptor x, SingleDescriptor y) {
		float[] xdata = x.getData();
		float[] ydata = y.getData();	
		double acc = 0;
		count++;
		
		for (int i = 0; i < xdata.length; i++) {
            final double d = xdata[i] - ydata[i];
            acc += d * d;
        }

        return Math.sqrt(acc);
	}
}
