package metricspaces.metrics;

import metricspaces.descriptors.Descriptor;

/**
 * Represents a function which calculates a distance between two descriptor vectors.
 * @author stewart
 *
 * @param <DescriptorType>
 */
public interface Metric<DescriptorType extends Descriptor> {
	public double getDistance(DescriptorType x, DescriptorType y);
	public String getName();
}
