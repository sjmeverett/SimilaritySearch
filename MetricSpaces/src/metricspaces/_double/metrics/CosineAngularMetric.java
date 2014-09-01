package metricspaces._double.metrics;

import metricspaces._double.DoubleDescriptor;
import metricspaces.metrics.AbstractMetric;

public class CosineAngularMetric extends AbstractMetric<DoubleDescriptor> {
	@Override
    public double getDistance(DoubleDescriptor x, DoubleDescriptor y) {
        double[] xdata = x.getData();
        double[] ydata = y.getData();
        assert xdata.length == ydata.length;
		count++;

        double dotProduct = 0;

        for (int i = 0; i < xdata.length; i++) {
            dotProduct += xdata[i] * ydata[i];
        }

        double magProduct = x.getMagnitude() * y.getMagnitude();
        double rawCosDist = dotProduct / magProduct;

        rawCosDist = Math.max(0, Math.min(1, rawCosDist));

        return Math.acos(rawCosDist) / (Math.PI / 2);
    }
}
