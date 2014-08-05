package metricspaces.descriptors;

import java.io.IOException;

import metricspaces._double.DoubleDescriptor;

/**
 * Marks a descriptor file capable of converting to and from double format.
 * @author stewart
 *
 */
public interface CommonDescriptorFile {
	/**
	 * Gets the number of descriptors in a file.
	 * @return
	 */
	public int getSize();
	
	/**
	 * Gets the dimensionality of the descriptors.
	 * @return
	 */
	public int getDimensions();
	
	/**
	 * Gets the common format object for reading the descriptors.
	 * @return
	 */
	public DescriptorFormat<DoubleDescriptor> getCommonFormat();
	
	/**
	 * Closes the file.
	 */
	public void close() throws IOException;
}
