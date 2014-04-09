package metricspaces.metrics;

import metricspaces.descriptors.Descriptor;

public class ManhattanMetric implements Metric {
	@Override
	public double getDistance(Descriptor x, Descriptor y) {
		double[] xdata = x.getData();
		double[] ydata = y.getData();
		double acc = 0;
		
		for (int i = 0; i < xdata.length; i++) {
            acc += Math.abs(xdata[i] - ydata[i]);
        }

        return acc;
	}

	@Override
	public String getName() {
		return "Man";
	}

}
