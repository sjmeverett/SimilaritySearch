package metricspaces.update.common;

/**
 * Represents a distance function.
 * @author stewart
 *
 * @param <DescriptorType>
 */
public interface Metric<DescriptorType> {
	/**
	 * Gets the distance between two descriptors.
	 * @param x
	 * @param y
	 * @return
	 */
	double getDistance(DescriptorType x, DescriptorType y);
	
	
	/**
	 * Gets the number of times this metric has been used.
	 * @return
	 */
	int getCount();
	
	/**
	 * Resets the count of how many times the metric has been used.
	 */
	void resetCount();
}
