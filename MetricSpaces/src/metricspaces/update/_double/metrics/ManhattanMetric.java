package metricspaces.update._double.metrics;

import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.update.common.AbstractMetric;

public class ManhattanMetric extends AbstractMetric<DoubleDescriptor> {
	@Override
	public double getDistance(DoubleDescriptor x, DoubleDescriptor y) {
		double[] xdata = x.getData();
		double[] ydata = y.getData();
		double acc = 0;
		
		for (int i = 0; i < xdata.length; i++) {
            acc += Math.abs(xdata[i] - ydata[i]);
        }

        return acc;
	}
}
