package metricspaces.indexes.resultcollectors;

import metricspaces.indexes.SearchResult;

public interface ResultCollector {
	void add(SearchResult result);
	double getRadius();
}
