package metricspaces._double.metrics;

import metricspaces._double.DoubleDescriptor;
import metricspaces.metrics.AbstractMetric;

public class TriangularDiscriminationMetric extends AbstractMetric<DoubleDescriptor> {

	@Override
	public double getDistance(DoubleDescriptor x, DoubleDescriptor y) {
		double[] xdata = x.getNormalisedData();
		double[] ydata = y.getNormalisedData();
		double sum = 0;
		count++;
		
		for (int i = 0; i < xdata.length; i++) {
			double d = xdata[i] - ydata[i];
			sum += d * d / (xdata[i] + ydata[i]);
		}
		
		return Math.sqrt(sum);
	}

}
