package metricspaces.update.indices;

import metricspaces.indexes.resultcollectors.ResultCollector;

/**
 * Represents an index with additional method for searching using ResultCollectors.
 * @author stewart
 *
 */
public interface ResultCollectorIndex extends Index {
	/**
	 * Searches the index for the object with the specified ID using the specified result collector.
	 * @param objectId
	 * @param collector
	 */
	void search(int objectId, ResultCollector collector);
	
	/**
	 * Searches the index for the object at the specified position using the specified result collector.
	 * @param position
	 * @param collector
	 */
	void searchPosition(int position, ResultCollector collector);
}
