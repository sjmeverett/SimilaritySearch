package metricspaces.indexes;

import java.io.File;
import java.io.IOException;

import metricspaces.Progress;
import metricspaces.files.DescriptorFile;
import metricspaces.files.DescriptorFileHeader;
import metricspaces.files.LargeBinaryFile;
import metricspaces.metrics.Metric;
import metricspaces.metrics.Metrics;

public class IndexFileHeader extends LargeBinaryFile {
	private final byte indexImplementation;
	private final String descriptorFile, metricName;
	private final int dataOffset, capacity;
	private static final int HEADER_SIZE = 512;
	
	public static final byte VP_TREE = 0;
	public static final byte EXTREME_PIVOTS = 1;
	public static final byte PIVOTED_LIST = 2;
	
	public IndexFileHeader(String path) throws IOException {
		super(path);
		
		indexImplementation = buffer.get();
		capacity = buffer.getInt();
		descriptorFile = getString();
		metricName = getString();
		dataOffset = HEADER_SIZE;
		buffer.position(dataOffset);
	}
	
	
	public IndexFileHeader(String path, byte indexImplementation, int capacity, String descriptorFile, String metricName) throws IOException {
		super(path, HEADER_SIZE * 2);
		
		this.indexImplementation = indexImplementation;
		this.capacity = capacity;
		this.descriptorFile = descriptorFile;
		this.metricName = metricName;
		
		buffer.put(indexImplementation);
		buffer.putInt(capacity);
		putString(descriptorFile);
		putString(metricName);
		dataOffset = HEADER_SIZE;
		buffer.position(dataOffset);
	}
	
	
	public byte getIndexImplementation() {
		return indexImplementation;
	}
	
	public int getCapacity() {
		return capacity;
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
	
	
	public static Index open(String path, Progress progress) throws IOException {
		IndexFileHeader header = new IndexFileHeader(path);
		Metric metric = Metrics.getMetric(header.getMetricName());
		
		String descriptorFilePath = new File(new File(path).getParentFile(), header.getDescriptorFile()).getPath();
		DescriptorFile objects = DescriptorFileHeader.open(descriptorFilePath);
		
		switch (header.getIndexImplementation()) {
		
		case VP_TREE:
			return new VantagePointTreeIndex(header, objects, metric, progress);
		
		case EXTREME_PIVOTS:
			return new ExtremePivotsIndex(header, objects, metric, progress);
		
		case PIVOTED_LIST:
			return new PivotedList(header, objects, metric, progress);
			
		default:
			throw new UnsupportedOperationException("index type not supported");
		}
	}
}
