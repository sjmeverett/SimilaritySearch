package metricspaces.indexes;

import metricspaces.descriptors.Descriptor;
import metricspaces.indexes.resultcollectors.ResultCollector;

public interface ResultCollectorIndex extends Index {
	void search(Descriptor query, ResultCollector collector);
	void search(int position, ResultCollector collector);
}
