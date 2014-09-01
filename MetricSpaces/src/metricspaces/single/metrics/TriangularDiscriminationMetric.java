package metricspaces.single.metrics;

import metricspaces.metrics.AbstractMetric;
import metricspaces.single.SingleDescriptor;

public class TriangularDiscriminationMetric extends AbstractMetric<SingleDescriptor> {

	@Override
	public double getDistance(SingleDescriptor x, SingleDescriptor y) {
		float[] xdata = x.getNormalisedData();
		float[] ydata = y.getNormalisedData();
		double sum = 0;
		count++;
		
		for (int i = 0; i < xdata.length; i++) {
			double d = xdata[i] - ydata[i];
			sum += d * d / (xdata[i] + ydata[i]);
		}
		
		return Math.sqrt(sum);
	}

}
