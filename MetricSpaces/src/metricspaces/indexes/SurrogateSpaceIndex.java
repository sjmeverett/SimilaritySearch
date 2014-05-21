package metricspaces.indexes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.files.RelativeDescriptorFile;
import metricspaces.metrics.ChebyshevMetric;
import metricspaces.metrics.Metrics;

public class SurrogateSpaceIndex implements Index {
	private final DescriptorFile originalObjects;
	private final Index surrogateIndex;
	private final RelativeDescriptorFile surrogateObjects;
	private final ChebyshevMetric metric;
	
	
	public SurrogateSpaceIndex(DescriptorFile originalObjects, Index surrogateIndex) throws IOException {
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
		return search(query, surrogateQuery, radius, radius);
	}

	@Override
	public List<SearchResult> search(int position, double radius) {
		return search(originalObjects.get(position), surrogateObjects.get(position), radius, radius);
	}
	
	
	public List<SearchResult> search(int position, double radius, double surrogateRadius) {
		return search(originalObjects.get(position), surrogateObjects.get(position), radius, surrogateRadius);
	}
	
	
	private List<SearchResult> search(Descriptor query, Descriptor surrogateQuery, double radius, double surrogateRadius) {
		metric.setThreshold(surrogateRadius);
		
		List<SearchResult> results = surrogateIndex.search(surrogateQuery, surrogateRadius);
		List<SearchResult> filteredResults = new ArrayList<SearchResult>();
		
		for (SearchResult result: results) {
			if (surrogateObjects.getDistance(query, result.getResult()) <= radius)
				filteredResults.add(result);
		}
		
		return filteredResults;
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

}
