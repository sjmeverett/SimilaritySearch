package metricspaces.single.metrics;

import metricspaces.metrics.AbstractMetric;
import metricspaces.single.SingleDescriptor;

public class CosineAngularMetric extends AbstractMetric<SingleDescriptor> {
	@Override
    public double getDistance(SingleDescriptor x, SingleDescriptor y) {
        float[] xdata = x.getData();
        float[] ydata = y.getData();
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
