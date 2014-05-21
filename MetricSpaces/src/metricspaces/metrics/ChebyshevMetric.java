package metricspaces.metrics;

import metricspaces.descriptors.Descriptor;

public class ChebyshevMetric implements Metric {
	private double threshold;
	
	public ChebyshevMetric() {
		threshold = Double.POSITIVE_INFINITY;
	}
	
	@Override
	public double getDistance(Descriptor x, Descriptor y) {
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
	

	@Override
	public String getName() {
		return "Chebyshev";
	}

}
