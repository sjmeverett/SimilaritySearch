package metricspaces.update.single.metrics;

import metricspaces.descriptors.SingleDescriptor;
import metricspaces.update.common.AbstractMetric;

public class EuclidianMetric extends AbstractMetric<SingleDescriptor> {

	@Override
	public double getDistance(SingleDescriptor x, SingleDescriptor y) {
		float[] xdata = x.getData();
		float[] ydata = y.getData();	
		float acc = 0;
		
		for (int i = 0; i < xdata.length; i++) {
            final float d = xdata[i] - ydata[i];
            acc += d * d;
        }

        return Math.sqrt(acc);
	}
}
