package metricspaces.indexes;

import java.io.IOException;
import java.util.List;

import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.files.RelativeDescriptorFile;
import metricspaces.indexes.resultcollectors.ResultCollector;
import metricspaces.indexes.resultcollectors.StandardResultCollector;
import metricspaces.indexes.resultcollectors.SurrogateResultCollector;
import metricspaces.metrics.ChebyshevMetric;
import metricspaces.metrics.Metric;
import metricspaces.metrics.Metrics;

public class SurrogateSpaceIndex implements ResultCollectorIndex {
	private final DescriptorFile originalObjects;
	private final ResultCollectorIndex surrogateIndex;
	private final RelativeDescriptorFile surrogateObjects;
	private final ChebyshevMetric metric;
	
	
	public SurrogateSpaceIndex(DescriptorFile originalObjects, ResultCollectorIndex surrogateIndex) throws IOException {
		this.originalObjects = originalObjects;
		this.surrogateIndex = surrogateIndex;
		
		DescriptorFile objects = surrogateIndex.getObjects();
		
		if (!(objects instanceof RelativeDescriptorFile && surrogateIndex.getHeader().getMetricName().equals("Chebyshev")))
			throw new IllegalArgumentException("Index is not a surrogate index.");
		
		surrogateObjects = (RelativeDescriptorFile)objects;
		metric = (ChebyshevMetric)Metrics.getMetric("Chebyshev"); //singleton
	}
	
	
	@Override
	public void build(List<Integer> keys) {
		throw new UnsupportedOperationException("This index is built by first creating the surrogate space and building "
				+ "any index on that, using Chebyshev distance.");
	}

	@Override
	public List<SearchResult> search(Descriptor query, double radius) {
		Descriptor surrogateQuery = surrogateObjects.getRelativeDescriptor(query);
		return search(query, surrogateQuery, null, radius, radius);
	}

	@Override
	public List<SearchResult> search(int position, double radius) {
		return search(originalObjects.get(position), surrogateObjects.get(position), null, radius, radius);
	}
	
	@Override
	public void search(Descriptor query, ResultCollector collector) {
		search(collector, query, surrogateObjects.getRelativeDescriptor(query), null, collector.getRadius(), null);
	}
	
	@Override
	public void search(int position, ResultCollector collector) {
		Descriptor query = originalObjects.get(position);
		search(collector, query, surrogateObjects.getRelativeDescriptor(query), position, collector.getRadius(), null);
	}
	
	
	public void search(Descriptor query, ResultCollector collector, double surrogateRadius) {
		search(collector, query, surrogateObjects.getRelativeDescriptor(query), null, collector.getRadius(), surrogateRadius);
	}
	
	public void search(int position, ResultCollector collector, double surrogateRadius) {
		search(collector, originalObjects.get(position), surrogateObjects.get(position), position, collector.getRadius(), surrogateRadius);
	}
	
	
	public List<SearchResult> search(int position, double radius, double surrogateRadius) {
		return search(originalObjects.get(position), surrogateObjects.get(position), position, radius, surrogateRadius);
	}
	
	
	public List<SearchResult> search(Descriptor query, double radius, double surrogateRadius) {
		return search(query, surrogateObjects.getRelativeDescriptor(query), null, radius, surrogateRadius);
	}
	
	public List<SearchResult> searchSurrogate(Descriptor query, double surrogateRadius) {
		return search(query, surrogateObjects.getRelativeDescriptor(query), null, Double.POSITIVE_INFINITY, surrogateRadius);
	}
	
	
	public List<SearchResult> searchSurrogate(int position, double surrogateRadius) {
		return search(originalObjects.get(position), surrogateObjects.get(position), position, Double.POSITIVE_INFINITY, surrogateRadius);
	}
	
	
	private List<SearchResult> search(Descriptor query, Descriptor surrogateQuery, Integer queryId, double radius, double surrogateRadius) {
		StandardResultCollector collector = new StandardResultCollector(radius);
		
		metric.setThreshold(surrogateRadius);
		search(collector, query, surrogateQuery, queryId, radius, surrogateRadius);
		
		return collector.getResults();
	}
	
	private void search(ResultCollector collector, Descriptor query, Descriptor surrogateQuery, Integer queryId,
			double radius, Double surrogateRadius) {
		
		SurrogateResultCollector surrogateCollector = new SurrogateResultCollector(collector, radius, surrogateRadius,
			originalObjects, surrogateObjects, query, queryId);
		
		surrogateIndex.search(surrogateQuery, surrogateCollector);
	}

	@Override
	public int getKey(int position) {
		return position;
	}

	@Override
	public int getNumberOfDistanceCalculations() {
		return surrogateObjects.getNumberOfDistanceCalculations();
	}
	
	public int getNumberOfSurrogateDistanceCalculations() {
		return surrogateIndex.getNumberOfDistanceCalculations();
	}

	@Override
	public void resetNumberOfDistanceCalculations() {
		surrogateObjects.resetNumberOfDistanceCalculations();
	}

	@Override
	public void close() throws IOException {
		
	}

	@Override
	public DescriptorFile getObjects() {
		return originalObjects;
	}

	@Override
	public IndexFileHeader getHeader() {
		throw new UnsupportedOperationException("No header associated with this type of index.");
	}

	
	public ResultCollectorIndex getSurrogateIndex() {
		return surrogateIndex;
	}
	
	
	public Metric getMetric() {
		return surrogateObjects;
	}
}
