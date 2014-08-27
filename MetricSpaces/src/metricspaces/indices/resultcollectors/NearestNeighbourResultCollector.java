package metricspaces.indices.resultcollectors;

import metricspaces.indices.SearchResult;


public class NearestNeighbourResultCollector implements ResultCollector {
	private Integer query;
	private int nearestNeighbour;
	private double distance;
	
	public NearestNeighbourResultCollector(Integer query) {
		this.query = query;
		distance = Double.POSITIVE_INFINITY;
	}
	
	
	public NearestNeighbourResultCollector() {
		this(null);
	}
	
	
	@Override
	public void add(SearchResult result) {
		if ((query == null || result.getResult() != query) && result.getDistance() < distance) {
			distance = result.getDistance();
			nearestNeighbour = result.getResult();
		}
	}
	
	
	@Override
	public double getRadius() {
		return distance;
	}

	
	public int getResult() {
		return nearestNeighbour;
	}
}
