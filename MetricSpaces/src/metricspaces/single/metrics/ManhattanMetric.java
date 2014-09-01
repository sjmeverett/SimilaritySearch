package metricspaces.single.metrics;

import metricspaces.metrics.AbstractMetric;
import metricspaces.single.SingleDescriptor;

public class ManhattanMetric extends AbstractMetric<SingleDescriptor> {
	@Override
	public double getDistance(SingleDescriptor x, SingleDescriptor y) {
		float[] xdata = x.getData();
		float[] ydata = y.getData();
		double acc = 0;
		count++;
		
		for (int i = 0; i < xdata.length; i++) {
            acc += Math.abs(xdata[i] - ydata[i]);
        }

        return acc;
	}
}
