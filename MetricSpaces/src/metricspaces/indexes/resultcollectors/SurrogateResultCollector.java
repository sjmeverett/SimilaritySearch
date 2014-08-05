package metricspaces.indexes.resultcollectors;

import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.SearchResult;
import metricspaces.metrics.Metric;

public class SurrogateResultCollector implements ResultCollector {
	private ResultCollector collector;
	private double radius;
	private Double surrogateRadius;
	private DescriptorFile objects;
	private Metric metric;
	private Descriptor query;
	private Integer queryId;
	
	public SurrogateResultCollector(ResultCollector collector, double radius, Double surrogateRadius, DescriptorFile objects, Metric metric, Descriptor query, Integer queryId) {
		this.collector = collector;
		this.radius = radius;
		this.surrogateRadius = surrogateRadius;
		this.objects = objects;
		this.metric = metric;
		this.query = query;
		this.queryId = queryId;
	}
	
	@Override
	public void add(SearchResult result) {
		double distance = metric.getDistance(query, objects.get(result.getResult()));
		
		if (distance <= radius) {
			result.setDistance(distance);
			result.setQuery(queryId);
			collector.add(result);
		}
	}
	
	@Override
	public double getRadius() {
		if (surrogateRadius == null)
			return collector.getRadius();
		else
			return surrogateRadius;
	}
}
