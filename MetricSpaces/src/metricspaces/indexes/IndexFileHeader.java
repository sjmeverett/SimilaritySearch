package metricspaces.indexes;

import java.io.IOException;

import metricspaces.files.LargeBinaryFile;

public class IndexFileHeader extends LargeBinaryFile {
	private final byte indexImplementation;
	private final String descriptorFile, metricName;
	private final int dataOffset;
	private static final int MAX_HEADER_SIZE = 1024;
	
	public static final byte VP_TREE = 0;
	public static final byte EXTREME_PIVOTS = 1;
	
	public IndexFileHeader(String path) throws IOException {
		super(path);
		
		indexImplementation = buffer.get();
		descriptorFile = getString();
		metricName = getString();
		dataOffset = buffer.position();
	}
	
	
	public IndexFileHeader(String path, byte indexImplementation, String descriptorFile, String metricName) throws IOException {
		super(path, MAX_HEADER_SIZE);
		
		this.indexImplementation = indexImplementation;
		this.descriptorFile = descriptorFile;
		this.metricName = metricName;
		
		buffer.put(indexImplementation);
		putString(descriptorFile);
		putString(metricName);
		dataOffset = buffer.position();
	}
	
	
	public byte getIndexImplementation() {
		return indexImplementation;
	}
	
	public String getDescriptorFile() {
		return descriptorFile;
	}
	
	public String getMetricName() {
		return metricName;
	}
	
	public int getDataOffset() {
		return dataOffset;
	}
}
