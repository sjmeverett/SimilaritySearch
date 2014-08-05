package metricspaces.metrics;

/**
 * A wrapper around DescriptorFormat to read MetricSpaceObjects instead of Descriptors.
 * @author stewart
 *
 */
public interface MetricSpaceFormat {
	/**
	 * Gets a MetricSpaceObject for the descriptor at the specified index in the buffer. 
	 * @param index
	 * @return
	 */
	public MetricSpaceObject get(int index);
}
