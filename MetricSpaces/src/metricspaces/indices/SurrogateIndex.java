package metricspaces.indices;

import java.io.IOException;
import java.util.List;

import metricspaces._double.DoubleMetricSpace;
import metricspaces._double.metrics.ChebyshevMetric;
import metricspaces.descriptors.DescriptorFile;
import metricspaces.metrics.MetricSpace;
import metricspaces.metrics.MetricSpaceObject;
import metricspaces.relative.RelativeDescriptorFile;

public class SurrogateIndex implements ResultCollectorIndex {
	private final DescriptorFile originalDescriptors;
	private final MetricSpace originalSpace;
	private final ResultCollectorIndex index;
	private final ChebyshevMetric metric;
	
	
	public SurrogateIndex(Index index) {
		if (!isSurrogateIndex(index))
			throw new IllegalArgumentException("index is not a surrogate index");
		
		this.index = (ResultCollectorIndex)index;
		originalDescriptors = index.getDescriptors();
		
		RelativeDescriptorFile relativeDescriptors = (RelativeDescriptorFile)originalDescriptors;
		originalSpace = relativeDescriptors.getOriginalSpace();
		
		metric = (ChebyshevMetric)DoubleMetricSpace.getMetric("Chebyshev");
	}
	
	
	/**
	 * Determines if the index is a surrogate index.
	 * @param index
	 * @return
	 */
	public static boolean isSurrogateIndex(Index index) {
		return index instanceof ResultCollectorIndex
			&& index.getDescriptors() instanceof RelativeDescriptorFile
			&& index.getMetricSpace().getMetricName().equals("Chebyshev");
	}
	

	@Override
	public void build(List<Integer> keys) {
		throw new UnsupportedOperationException("This index is built by first creating the surrogate space and building "
				+ "any index on that, using Chebyshev distance.");
	}

	@Override
	public List<SearchResult> search(int objectId, double radius) {
		return search(objectId, radius, radius);
	}

	@Override
	public List<SearchResult> searchPosition(int position, double radius) {
		int objectId = index.getKey(position);
		return search(objectId, radius, radius);
	}

	@Override
	public int getKey(int position) {
		return index.getKey(position);
	}

	@Override
	public void close() throws IOException {
		index.close();
	}

	@Override
	public DescriptorFile getDescriptors() {
		return originalDescriptors;
	}

	@Override
	public void search(int objectId, ResultCollector collector) {
		search(collector, objectId, collector.getRadius(), null);
	}

	@Override
	public void searchPosition(int position, ResultCollector collector) {
		int objectId = index.getKey(position);
		search(collector, objectId, collector.getRadius(), null);
	}
	
	
	public List<SearchResult> search(int queryId, double radius, double surrogateRadius) {
		StandardResultCollector collector = new StandardResultCollector(radius);
		
		metric.setThreshold(surrogateRadius);
		search(collector, queryId, radius, surrogateRadius);
		
		return collector.getResults();
	}
	
	
	public void search(int objectId, ResultCollector collector, double surrogateRadius) {
		search(collector, objectId, collector.getRadius(), surrogateRadius);
	}
	
	
	private void search(ResultCollector collector, int queryId, double radius, Double surrogateRadius) {
		MetricSpaceObject query = originalSpace.getObject(queryId);
		SurrogateResultCollector surrogateCollector = new SurrogateResultCollector(query, collector, radius, surrogateRadius);
		
		index.search(queryId, surrogateCollector);
	}

	@Override
	public MetricSpace getMetricSpace() {
		return index.getMetricSpace();
	}
	
	public MetricSpace getOriginalSpace() {
		return originalSpace;
	}


	@Override
	public int getSize() {
		return index.getSize();
	}
}
