package metricspaces.metrics;

import java.util.HashMap;
import java.util.Map;

public class Metrics {
	private static Map<String, Metric> metrics;
	
	public static Map<String, Metric> getMetrics() {
		if (metrics == null) {
			metrics = new HashMap<>();
			addMetric(new ChebyshevMetric());
			addMetric(new CosineAngularMetric());
			addMetric(new EuclidianMetric());
			addMetric(new ManhattanMetric());
			addMetric(new SEDByComplexityMetric());
		}
		
		return metrics;
	}
	
	
	public static Metric getMetric(String name) {
		Metric metric = getMetrics().get(name);
		
		if (metric == null)
			throw new IllegalArgumentException("Unknown metric: " + metric);
		
		return metric;
	}
	
	
	public static void addMetric(Metric metric) {
		metrics.put(metric.getName(), metric);
	}
}
