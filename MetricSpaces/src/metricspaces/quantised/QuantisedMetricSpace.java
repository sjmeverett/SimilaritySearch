package metricspaces.quantised;

import java.util.Map;

import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.metrics.AbstractMetricSpace;
import metricspaces.metrics.Metric;

public class QuantisedMetricSpace extends AbstractMetricSpace<QuantisedDescriptor> {
	
	private static Map<String, Metric<QuantisedDescriptor>> metrics;
	
	/**
	 * Gets the list of metrics defined for this space.
	 * @return
	 */
	public static Map<String, Metric<QuantisedDescriptor>> getMetrics() {
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
	public static Metric<QuantisedDescriptor> getMetric(String name) {
		Metric<QuantisedDescriptor> metric = getMetrics().get(name);
		
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
	public QuantisedMetricSpace(AbstractDescriptorFile<QuantisedDescriptor> descriptors, String metricName) {
		super(descriptors, metricName, getMetric(metricName));
	}

	
	@Override
	public double getDistance(int id) {
		QuantisedDescriptor x = format.get(id);
		//generate y
		return 0;
	}

	
	@Override
	public double getDistance(Object x, Object y) {
		if (x instanceof QuantisedDescriptor && y instanceof QuantisedDescriptor) {
			return metric.getDistance((QuantisedDescriptor)x, (QuantisedDescriptor)y);
		}
		else {
			throw new UnsupportedOperationException("Either x or y is not in the quantised space.");
		}
	}
}
