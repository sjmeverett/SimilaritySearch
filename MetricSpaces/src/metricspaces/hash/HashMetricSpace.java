package metricspaces.hash;

import java.util.Map;

import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.metrics.AbstractMetricSpace;
import metricspaces.metrics.Metric;

public class HashMetricSpace extends AbstractMetricSpace<HashDescriptor> {
	private static Map<String, Metric<HashDescriptor>> metrics;
	
	/**
	 * Gets the list of metrics defined for this space.
	 * @return
	 */
	public static Map<String, Metric<HashDescriptor>> getMetrics() {
		if (metrics == null) {
			
		}
		
		return metrics;
	}
	
	/**
	 * Gets the metric with the specified name in this space.
	 * @param name
	 * @return
	 */
	public static Metric<HashDescriptor> getMetric(String name) {
		Metric<HashDescriptor> metric = getMetrics().get(name);
		
		if (metric == null)
			throw new IllegalArgumentException("Metric " + name + " is not known in the hash metric space");
		else
			return metric;
	}

	public HashMetricSpace(AbstractDescriptorFile<HashDescriptor> descriptors, String metricName) {
		super(descriptors, metricName, getMetric(metricName));
	}
}
