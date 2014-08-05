package metricspaces.single.metrics;

import metricspaces.metrics.AbstractMetric;
import metricspaces.single.SingleDescriptor;

public class ChebyshevMetric extends AbstractMetric<SingleDescriptor> {
	private float threshold;
	
	public ChebyshevMetric() {
		threshold = Float.POSITIVE_INFINITY;
	}
	
	@Override
	public double getDistance(SingleDescriptor x, SingleDescriptor y) {
		float[] xdata = x.getData();
		float[] ydata = y.getData();
		float max = Float.NEGATIVE_INFINITY;
		
		for (int i = 0; i < xdata.length && max <= threshold; i++) {
			float d = Math.abs(xdata[i] - ydata[i]);
			
			if (d > max)
				max = d;
		}
		
		return max;
	}
	
	
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
}
