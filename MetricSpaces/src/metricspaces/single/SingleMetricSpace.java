package metricspaces.single;

import java.util.Map;

import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.metrics.AbstractMetricSpace;
import metricspaces.metrics.Metric;

public class SingleMetricSpace extends AbstractMetricSpace<SingleDescriptor> {
	
	private static Map<String, Metric<SingleDescriptor>> metrics;
	
	/**
	 * Gets the list of metrics defined for this space.
	 * @return
	 */
	public static Map<String, Metric<SingleDescriptor>> getMetrics() {
		if (metrics == null) {
			//add metrics
		}
		
		return metrics;
	}
	
	/**
	 * Gets the metric with the specified name in this space.
	 * @param name
	 * @return
	 */
	public static Metric<SingleDescriptor> getMetric(String name) {
		Metric<SingleDescriptor> metric = getMetrics().get(name);
		
		if (metric == null)
			throw new IllegalArgumentException("Metric " + name + " is not known in the single metric space");
		else
			return metric;
	}
	
	/**
	 * 
	 * @param descriptors
	 * @param metricName
	 */
	public SingleMetricSpace(AbstractDescriptorFile<SingleDescriptor> descriptors, String metricName) {
		super(descriptors, metricName, getMetric(metricName));
	}
}
