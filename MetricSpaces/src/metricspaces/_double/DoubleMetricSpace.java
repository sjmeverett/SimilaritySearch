package metricspaces._double;

import java.util.HashMap;
import java.util.Map;

import metricspaces._double.metrics.ChebyshevMetric;
import metricspaces._double.metrics.CosineAngularMetric;
import metricspaces._double.metrics.EuclidianMetric;
import metricspaces._double.metrics.ManhattanMetric;
import metricspaces._double.metrics.SEDByComplexityMetric;
import metricspaces._double.metrics.TriangularDiscriminationMetric;
import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.metrics.AbstractMetricSpace;
import metricspaces.metrics.Metric;
import metricspaces.objectselectors.ObjectSelector;

public class DoubleMetricSpace extends AbstractMetricSpace<DoubleDescriptor> {

	private static Map<String, Metric<DoubleDescriptor>> metrics;
	
	/**
	 * Gets the list of metrics defined for this space.
	 * @return
	 */
	public static Map<String, Metric<DoubleDescriptor>> getMetrics() {
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
	public static Metric<DoubleDescriptor> getMetric(String name) {
		Metric<DoubleDescriptor> metric = getMetrics().get(name);
		
		if (metric == null)
			throw new IllegalArgumentException("Metric " + name + " is not known in the double metric space");
		else
			return metric;
	}
	
	/**
	 * 
	 * @param descriptors
	 * @param metricName
	 */
	public DoubleMetricSpace(AbstractDescriptorFile<DoubleDescriptor> descriptors, String metricName) {
		super(descriptors, metricName, getMetric(metricName));
	}
	
	
	@Override
	public ObjectSelector getObjectSelector(String name) {
		if (name.equals("corner")) {
			return new DoubleCornerObjectSelector(this, super.descriptors.getDimensions());
		}
		else {
			return super.getObjectSelector(name);
		}
	}
}
