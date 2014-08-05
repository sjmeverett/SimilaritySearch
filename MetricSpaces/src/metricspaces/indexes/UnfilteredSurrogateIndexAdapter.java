package metricspaces.indexes;

import java.io.IOException;
import java.util.List;

import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;

public class UnfilteredSurrogateIndexAdapter implements Index {
	private SurrogateSpaceIndex index;
	
	public UnfilteredSurrogateIndexAdapter(SurrogateSpaceIndex index) {
		this.index = index;
	}
	
	@Override
	public void build(List<Integer> keys) {
		index.build(keys);
	}

	@Override
	public List<SearchResult> search(Descriptor query, double radius) {
		return index.searchSurrogate(query, radius);
	}

	@Override
	public List<SearchResult> search(int position, double radius) {
		return index.searchSurrogate(position, radius);
	}

	@Override
	public int getKey(int position) {
		return index.getKey(position);
	}

	@Override
	public int getNumberOfDistanceCalculations() {
		return index.getNumberOfDistanceCalculations();
	}

	@Override
	public void resetNumberOfDistanceCalculations() {
		index.resetNumberOfDistanceCalculations();
	}

	@Override
	public void close() throws IOException {
		index.close();
	}

	@Override
	public DescriptorFile getObjects() {
		return index.getObjects();
	}

	@Override
	public IndexFileHeader getHeader() {
		return index.getHeader();
	}

}
