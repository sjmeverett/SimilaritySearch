package metricspaces.single;

import java.util.HashMap;
import java.util.Map;

import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.metrics.AbstractMetricSpace;
import metricspaces.metrics.Metric;
import metricspaces.single.metrics.ChebyshevMetric;
import metricspaces.single.metrics.CosineAngularMetric;
import metricspaces.single.metrics.EuclidianMetric;
import metricspaces.single.metrics.ManhattanMetric;
import metricspaces.single.metrics.SEDByComplexityMetric;
import metricspaces.single.metrics.TriangularDiscriminationMetric;

public class SingleMetricSpace extends AbstractMetricSpace<SingleDescriptor> {
	
	private static Map<String, Metric<SingleDescriptor>> metrics;
	
	/**
	 * Gets the list of metrics defined for this space.
	 * @return
	 */
	public static Map<String, Metric<SingleDescriptor>> getMetrics() {
		if (metrics == null) {
			metrics = new HashMap<>();
			metrics.put("Chebyshev", new ChebyshevMetric());
			metrics.put("Cos", new CosineAngularMetric());
			metrics.put("Euc", new EuclidianMetric());
			metrics.put("Man", new ManhattanMetric());
			metrics.put("SED", new SEDByComplexityMetric());
			metrics.put("TD", new TriangularDiscriminationMetric());
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
