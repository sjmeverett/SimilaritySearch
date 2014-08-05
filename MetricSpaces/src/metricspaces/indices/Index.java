package metricspaces.indices;

import java.io.IOException;
import java.util.List;

import metricspaces.descriptors.DescriptorFile;
import metricspaces.metrics.MetricSpace;

/**
 * Represents an index which structures data to make range searches efficient.
 * @author stewart
 */
public interface Index {
	public static final byte VP_TREE = 0;
	public static final byte EXTREME_PIVOTS = 1;
	public static final byte PIVOTED_LIST = 2;
	
	
	/**
	 * Builds the index.
	 * @param keys The keys of the descriptors to put into the index.
	 */
	void build(List<Integer> keys);
	
	/**
	 * Searches the index for the object with the specified ID.
	 * @param objectId
	 * @param radius
	 * @return
	 */
	List<SearchResult> search(int objectId, double radius);
	
	/**
	 * Finds all objects within a certain distance to a given item in the index.  This version
	 * allows possible efficiency savings since the object is known already, depending on the
	 * implementation.  Implementations should ensure that the query property of the
	 * SearchResults is set to the key of the object found at the specified radius.
	 * 
	 * @param position The object position in the index structure.
	 * @param radius
	 * @return
	 */
	List<SearchResult> searchPosition(int position, double radius);
	
	/**
	 * Gets the key of the object at the specified position in the index.
	 * @param position
	 * @return
	 */
	int getKey(int position);
	
	/**
	 * Closes the file used by this index.
	 * @throws IOException
	 */
	void close() throws IOException;
	
	/**
	 * Gets the objects that the index is built over.
	 * @return
	 */
	DescriptorFile getDescriptors();
	
	/**
	 * Gets the metric space that this index operates in.
	 * @return
	 */
	MetricSpace getMetricSpace();
	
	/**
	 * Gets the size of the index.
	 * @return
	 */
	int getSize();
}
