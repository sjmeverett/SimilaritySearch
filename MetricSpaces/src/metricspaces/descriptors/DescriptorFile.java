package metricspaces.descriptors;

import java.io.IOException;

import metricspaces.metrics.MetricSpace;

/**
 * Represents a file with descriptors in it.
 * @author stewart
 */
public interface DescriptorFile {
	public static final byte DOUBLE_TYPE = 0;
    public static final byte QUANTISED_TYPE = 1;
    public static final byte SINGLE_TYPE = 2;
    public static final byte RELATIVE_TYPE = 3;
	public static final byte HASH_TYPE = 4;
 
    /**
     * Gets the name of the descriptor.
     * @return
     */
    public String getDescriptorName();
    
    /**
     * Returns one of the constants defined in DescriptorFile indicating what the descriptor type is.
     * @return
     */
    public byte getDescriptorType();
	
	/**
	 * Gets the number of descriptors in the file.
	 * @return
	 */
	public int getSize();
	
	/**
	 * Gets the number of dimensions of the descriptor.
	 * @return
	 */
	public int getDimensions();
	
	/**
	 * Gets a metric space from this descriptor file.
	 * @param metricName The name of the metric to compose the metric space.
	 * @return
	 */
	public MetricSpace getMetricSpace(String metricName);
	
	/**
     * Closes the file.
     * @throws IOException
     */
    void close() throws IOException;
}
