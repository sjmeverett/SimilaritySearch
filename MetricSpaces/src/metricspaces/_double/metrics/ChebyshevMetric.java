package metricspaces._double.metrics;

import metricspaces._double.DoubleDescriptor;
import metricspaces.metrics.AbstractMetric;

public class ChebyshevMetric extends AbstractMetric<DoubleDescriptor> {
	private double threshold;
	
	public ChebyshevMetric() {
		threshold = Double.POSITIVE_INFINITY;
	}
	
	@Override
	public double getDistance(DoubleDescriptor x, DoubleDescriptor y) {
		double[] xdata = x.getData();
		double[] ydata = y.getData();
		double max = Double.NEGATIVE_INFINITY;
		
		for (int i = 0; i < xdata.length && max <= threshold; i++) {
			double d = Math.abs(xdata[i] - ydata[i]);
			
			if (d > max)
				max = d;
		}
		
		return max;
	}
	
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
}
