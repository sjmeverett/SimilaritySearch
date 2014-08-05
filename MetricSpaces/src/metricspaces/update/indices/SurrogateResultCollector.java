package metricspaces.update.indices;

import metricspaces.indexes.SearchResult;
import metricspaces.indexes.resultcollectors.ResultCollector;
import metricspaces.update.common.MetricSpaceObject;

public class SurrogateResultCollector implements ResultCollector {
	private MetricSpaceObject query;
	private ResultCollector collector;
	private double radius;
	private Double surrogateRadius;
	private int queryId;
	
	public SurrogateResultCollector(MetricSpaceObject query, ResultCollector collector, double radius, Double surrogateRadius) {
		this.query = query;
		this.queryId = query.getObjectID();
		this.collector = collector;
		this.radius = radius;
		this.surrogateRadius = surrogateRadius;
	}
	
	@Override
	public void add(SearchResult result) {
		double distance = query.getDistance(result.getResult());
		
		if (distance < radius) {
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
