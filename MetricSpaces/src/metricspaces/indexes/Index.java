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
public interface Index<ObjectType, DescriptorType extends Descriptor> {
	/**
	 * Builds the index.
	 * @param descriptorFile
	 */
	void build();
	
	/**
	 * Finds all objects within a certain distance to a given query descriptor.
	 * @param query
	 * @param radius
	 * @return
	 */
	List<SearchResult<ObjectType>> search(DescriptorType query, double radius); 
	
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
	DescriptorFile<ObjectType, DescriptorType> getObjects();
}
