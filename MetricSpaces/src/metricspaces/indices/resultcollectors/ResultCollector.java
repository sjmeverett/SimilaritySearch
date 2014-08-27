package metricspaces.indices.resultcollectors;

import metricspaces.indices.SearchResult;


public interface ResultCollector {
	void add(SearchResult result);
	double getRadius();
}
