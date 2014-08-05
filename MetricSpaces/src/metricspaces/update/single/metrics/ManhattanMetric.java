package metricspaces.update.single.metrics;

import metricspaces.descriptors.SingleDescriptor;
import metricspaces.update.common.AbstractMetric;

public class ManhattanMetric extends AbstractMetric<SingleDescriptor> {
	@Override
	public double getDistance(SingleDescriptor x, SingleDescriptor y) {
		float[] xdata = x.getData();
		float[] ydata = y.getData();
		float acc = 0;
		
		for (int i = 0; i < xdata.length; i++) {
            acc += Math.abs(xdata[i] - ydata[i]);
        }

        return acc;
	}
}
