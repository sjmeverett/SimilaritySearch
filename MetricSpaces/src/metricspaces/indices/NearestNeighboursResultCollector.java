package metricspaces.indices;

import java.util.Collection;

import metricspaces.util.FixedSizePriorityQueue;

public class NearestNeighboursResultCollector implements ResultCollector {
	private FixedSizePriorityQueue<SearchResult> results;
	private Integer query;
	
	public NearestNeighboursResultCollector(int size, Integer query) {
		this.query = query;
		results = new FixedSizePriorityQueue<>(size);
	}
	
	@Override
	public void add(SearchResult result) {
		if (query == null || result.getResult() != query)
			results.add(result);
	}

	@Override
	public double getRadius() {
		SearchResult end = results.getEnd();
		
		if (end == null)
			return Double.POSITIVE_INFINITY;
		else
			return end.getDistance();
	}

	public Collection<SearchResult> getResults() {
		return results;
	}
}
