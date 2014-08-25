package metricspaces.descriptors;

import java.io.IOException;
import java.nio.ByteBuffer;

import metricspaces.util.LargeBinaryFile;

public abstract class AbstractDescriptorFile<DescriptorType> implements DescriptorFile {
	protected ByteBuffer buffer;
	protected LargeBinaryFile file;
	protected byte descriptorType;
    protected int dimensions, size, statsOffset, dataOffset;
    protected double elementMean, elementStdDev, elementMax;
    protected String descriptorName;
    
    public static final int HEADER_SIZE = 512;
    
    /**
     * Constructor for opening existing files.
     * @param file
     * @throws IOException
     */
	public AbstractDescriptorFile(LargeBinaryFile file) throws IOException {
		this.file = file;
		this.buffer = file.getBuffer();
		
		buffer.position(0);
        descriptorType = buffer.get();
        size = buffer.getInt();
        dimensions = buffer.getInt();
        descriptorName = file.getString();
        
        //stats for quantisation-based metrics
        statsOffset = buffer.position();
        elementMean = buffer.getDouble();
        elementStdDev = buffer.getDouble();
        elementMax = buffer.getDouble();

        //reserve space for future expansion of header
        dataOffset = HEADER_SIZE;
        buffer.position(dataOffset);
	}
	
	
	public AbstractDescriptorFile(String filename, byte descriptorType) throws IOException {
		this.file = new LargeBinaryFile(filename, HEADER_SIZE * 2);
		this.buffer = file.getBuffer();
		this.descriptorType = descriptorType;
		
		buffer.position(0);
		buffer.put(descriptorType);
	}
	
	
	public AbstractDescriptorFile(String filename, byte descriptorType, int size, int dimensions, String descriptorName) throws IOException {
		this(filename, descriptorType);
		writeHeader(size, dimensions, descriptorName);
	}
	
	
	protected void writeHeader(int size, int dimensions, String descriptorName) {
		this.size = size;
		this.dimensions = dimensions;
		this.descriptorName = descriptorName;
		
		buffer.putInt(size);
		buffer.putInt(dimensions);
		file.putString(descriptorName);
		
		//don't know the stats yet, so just note where we can return to fill them in
		statsOffset = buffer.position();
		
		//reserve space for future expansion of header
		dataOffset = HEADER_SIZE;
		buffer.position(dataOffset);
	}
	
	
	/**
	 * Sets the descriptor stats used by some metrics.
	 * @param elementMean
	 * @param elementStdDev
	 * @param elementMax
	 */
	public void setStats(double elementMean, double elementStdDev, double elementMax) {
		this.elementMean = elementMean;
		this.elementStdDev = elementStdDev;
		this.elementMax = elementMax;
		
		buffer.position(statsOffset);
		buffer.putDouble(elementMean);
		buffer.putDouble(elementStdDev);
		buffer.putDouble(elementMax);
	}
	
	
	/**
	 * Returns one of the constants defined in DescriptorFile indicating what the descriptor type is.
	 * @return
	 */
	@Override
	public byte getDescriptorType() {
		return descriptorType;
	}
	
	/**
	 * Gets the number of descriptors in the file.
	 * @return
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Gets the dimensions of the descriptors.
	 */
	public int getDimensions() {
		return dimensions;
	}
	
	/**
	 * Gets the name of the descriptor.
	 * @return
	 */
	public String getDescriptorName() {
		return descriptorName;
	}
	
	/**
	 * Gets the byte offset of the start of the data.
	 * @return
	 */
	public int getDataOffset() {
		return dataOffset;
	}
	
	/**
	 * Gets the mean value of the descriptor elements (if stored).
	 * @return
	 */
	public double getElementMean() {
		return elementMean;
	}
	
	/**
	 * Gets the standard deviation of the descriptor elements (if stored).
	 * @return
	 */
	public double getElementStdDev() {
		return elementStdDev;
	}
	
	/**
	 * Gets the maximum of the descriptor elements (if stored).
	 * @return
	 */
	public double getElementMax() {
		return elementMax;
	}
	
	/**
	 * Closes the file.
	 * @throws IOException 
	 */
	@Override
	public void close() throws IOException {
		file.getFile().close();
	}
	
	
	/**
	 * Gets the format object to read and write descriptors for this file.
	 * @return
	 */
	public abstract DescriptorFormat<DescriptorType> getFormat();
	
	/**
	 * Gets the format object to read and write descriptors for a given buffer, using the same
	 * settings as this file.
	 * @param buffer
	 * @param size
	 * @return
	 */
	public abstract DescriptorFormat<DescriptorType> getFormat(ByteBuffer buffer, int size);
}
