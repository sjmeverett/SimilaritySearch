package metricspaces.indices.resultcollectors;

import java.util.ArrayList;
import java.util.List;

import metricspaces.indices.SearchResult;

public class StandardResultCollector implements ResultCollector {
	private List<SearchResult> results;
	private double radius;
	
	public StandardResultCollector(double radius) {
		this.radius = radius;
		results = new ArrayList<>();
	}
	
	@Override
	public void add(SearchResult result) {
		results.add(result);
	}

	public List<SearchResult> getResults() {
		return results;
	}

	@Override
	public double getRadius() {
		return radius;
	}
}
