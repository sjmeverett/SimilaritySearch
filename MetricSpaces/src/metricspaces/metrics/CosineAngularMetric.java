package metricspaces.metrics;

import metricspaces.descriptors.Descriptor;

public class CosineAngularMetric implements Metric {
	@Override
    public double getDistance(Descriptor x, Descriptor y) {
        double[] xdata = x.getData();
        double[] ydata = y.getData();
        assert xdata.length == ydata.length;

        double dotProduct = 0;

        for (int i = 0; i < xdata.length; i++) {
            dotProduct += xdata[i] * ydata[i];
        }

        double magProduct = x.getMagnitude() * y.getMagnitude();
        double rawCosDist = dotProduct / magProduct;

        rawCosDist = Math.max(0, Math.min(1, rawCosDist));

        return Math.acos(rawCosDist) / (Math.PI / 2);
    }

    @Override
    public String getName() {
        return "Cos";
    }
}
