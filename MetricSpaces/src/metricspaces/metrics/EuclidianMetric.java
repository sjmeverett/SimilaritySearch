package metricspaces.metrics;

import metricspaces.descriptors.Descriptor;

public class EuclidianMetric implements Metric {

	@Override
	public double getDistance(Descriptor x, Descriptor y) {
		double[] xdata = x.getData();
		double[] ydata = y.getData();	
		double acc = 0;
		
		for (int i = 0; i < xdata.length; i++) {
            final double d = xdata[i] - ydata[i];
            acc += d * d;
        }

        return Math.sqrt(acc);
	}

	@Override
	public String getName() {
		return "Euc";
	}

}
