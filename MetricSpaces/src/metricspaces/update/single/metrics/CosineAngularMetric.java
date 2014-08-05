package metricspaces.update.single.metrics;

import metricspaces.descriptors.SingleDescriptor;
import metricspaces.update.common.AbstractMetric;

public class CosineAngularMetric extends AbstractMetric<SingleDescriptor> {
	@Override
    public double getDistance(SingleDescriptor x, SingleDescriptor y) {
        float[] xdata = x.getData();
        float[] ydata = y.getData();
        assert xdata.length == ydata.length;

        float dotProduct = 0;

        for (int i = 0; i < xdata.length; i++) {
            dotProduct += xdata[i] * ydata[i];
        }

        float magProduct = x.getMagnitude() * y.getMagnitude();
        float rawCosDist = dotProduct / magProduct;

        rawCosDist = Math.max(0, Math.min(1, rawCosDist));

        return Math.acos(rawCosDist) / (Math.PI / 2);
    }
}
