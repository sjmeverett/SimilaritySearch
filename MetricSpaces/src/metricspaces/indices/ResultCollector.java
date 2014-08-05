package metricspaces.indices;


public interface ResultCollector {
	void add(SearchResult result);
	double getRadius();
}
