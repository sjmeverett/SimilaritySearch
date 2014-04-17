package metricspaces.indexes;

import java.io.IOException;
import java.util.List;

import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;

/**
 * Represents an index which structures data to make range searches efficient.
 * @author stewart
 *
 * @param <ObjectType>
 * @param <DescriptorType>
 */
public interface Index {
	/**
	 * Builds the index.
	 * @param keys The keys of the descriptors to put into the index.
	 */
	void build(List<Integer> keys);
	
	/**
	 * Finds all objects within a certain distance to a given query descriptor.  Note that the
	 * query property of the returned SearchResults will be null, as the key is unknown or the
	 * query was not drawn from the indexed population.
	 * 
	 * @param query
	 * @param radius
	 * @return
	 */
	List<SearchResult> search(Descriptor query, double radius);
	
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
	List<SearchResult> search(int position, double radius);
	
	/**
	 * Gets the key of the object at the specified position in the index.
	 * @param position
	 * @return
	 */
	int getKey(int position);
	
	/**
	 * Gets the number of distance calculations performed by this index since the last reset.
	 * @return
	 */
	int getNumberOfDistanceCalculations();
	
	/**
	 * Resets the number of distance calculations performed.
	 */
	void resetNumberOfDistanceCalculations();
	
	/**
	 * Closes the file used by this index.
	 * @throws IOException
	 */
	void close() throws IOException;
	
	/**
	 * Gets the objects that the index is built over.
	 * @return
	 */
	DescriptorFile getObjects();

	/**
	 * Gets the file header object associated with the index.
	 * @return
	 */
	IndexFileHeader getHeader();
}
