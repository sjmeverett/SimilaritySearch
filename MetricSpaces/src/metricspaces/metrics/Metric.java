package metricspaces.metrics;

import metricspaces.descriptors.Descriptor;

/**
 * Represents a function which calculates a distance between two descriptor vectors.
 * @author stewart
 *
 * @param <DescriptorType>
 */
public interface Metric {
	public double getDistance(Descriptor x, Descriptor y);
	public String getName();
}
