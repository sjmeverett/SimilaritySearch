package metricspaces.files;

import java.io.IOException;

public class DescriptorFileHeader extends LargeBinaryFile {
    private byte descriptorType;
    private int dimensions, capacity, statsOffset, dataOffset;
    private double elementMean, elementStdDev, elementMax;
    private String descriptorName;
    private static final int HEADER_SIZE = 512;
    
    public static final byte DOUBLE_TYPE = 0;
    public static final byte BYTE_TYPE = 1;
    
    
	public DescriptorFileHeader(String path) throws IOException {
		super(path);
        descriptorType = buffer.get();
        capacity = buffer.getInt();
        dimensions = buffer.getInt();
        descriptorName = getString();
        
        //stats for quantisation-based metrics
        statsOffset = buffer.position();
        elementMean = buffer.getDouble();
        elementStdDev = buffer.getDouble();
        elementMax = buffer.getDouble();
        
        //reserve space for future expansion of header
        dataOffset = HEADER_SIZE;
        buffer.position(dataOffset);
	}
	
	public DescriptorFileHeader(String path, byte descriptorType, int capacity, int dimensions, String descriptorName) throws IOException {
		super(path, HEADER_SIZE * 2);
		
		this.descriptorType = descriptorType;
		this.capacity = capacity;
		this.dimensions = dimensions;
		this.descriptorName = descriptorName;
		
		buffer.put(descriptorType);
		buffer.putInt(capacity);
		buffer.putInt(dimensions);
		putString(descriptorName);
		
		//don't know the stats yet, so just note where we can return to fill them in
		statsOffset = buffer.position();
		
		//reserve space for future expansion of header
		dataOffset = HEADER_SIZE;
		buffer.position(dataOffset);
	}
	
	public void setStats(double elementMean, double elementStdDev, double elementMax) {
		this.elementMean = elementMean;
		this.elementStdDev = elementStdDev;
		this.elementMax = elementMax;
		
		buffer.position(statsOffset);
		buffer.putDouble(elementMean);
		buffer.putDouble(elementStdDev);
		buffer.putDouble(elementMax);
	}
	
	public byte getDescriptorType() {
		return descriptorType;
	}
	
	public int getCapacity() {
		return capacity;
	}
	
	public int getDimensions() {
		return dimensions;
	}
	
	public String getDescriptorName() {
		return descriptorName;
	}
	
	public int getDataOffset() {
		return dataOffset;
	}
	
	public double getElementMean() {
		return elementMean;
	}
	
	public double getElementStdDev() {
		return elementStdDev;
	}
	
	public double getElementMax() {
		return elementMax;
	}
}
