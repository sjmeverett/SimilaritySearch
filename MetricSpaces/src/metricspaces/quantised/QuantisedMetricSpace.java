package metricspaces.quantised;

import java.util.HashMap;
import java.util.Map;

import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.metrics.AbstractMetricSpace;
import metricspaces.metrics.Metric;
import metricspaces.quantised.metrics.SEDByComplexityMetric;

public class QuantisedMetricSpace extends AbstractMetricSpace<QuantisedDescriptor> {
	
	private static Map<String, Metric<QuantisedDescriptor>> metrics;
	
	/**
	 * Gets the list of metrics defined for this space.
	 * @return
	 */
	public static Map<String, Metric<QuantisedDescriptor>> getMetrics() {
		if (metrics == null) {
			metrics = new HashMap<>();
			metrics.put("SED", new SEDByComplexityMetric());
		}
		
		return metrics;
	}
	
	/**
	 * Gets the metric with the specified name in this space.
	 * @param name
	 * @return
	 */
	public static Metric<QuantisedDescriptor> getMetric(String name) {
		Metric<QuantisedDescriptor> metric = getMetrics().get(name);
		
		if (metric == null)
			throw new IllegalArgumentException("Metric " + name + " is not known in the quantised metric space");
		else
			return metric;
	}
	
	/**
	 * 
	 * @param descriptors
	 * @param metricName
	 */
	public QuantisedMetricSpace(AbstractDescriptorFile<QuantisedDescriptor> descriptors, String metricName) {
		super(descriptors, metricName, getMetric(metricName));
	}
}
