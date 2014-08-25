package metricspaces.indices;

import java.io.IOException;
import java.nio.ByteBuffer;

import metricspaces.descriptors.DescriptorFile;
import metricspaces.descriptors.DescriptorFileFactory;
import metricspaces.metrics.MetricSpace;
import metricspaces.util.LargeBinaryFile;

public abstract class AbstractIndex implements Index {
	protected LargeBinaryFile file;
	protected ByteBuffer buffer;
	
	private final byte indexImplementation;
	protected final int size;
	protected final int dataOffset;
	
	protected DescriptorFile descriptors;
	protected MetricSpace space;
	
	private static final int HEADER_SIZE = 512;
	
	
	/**
	 * Constructor for opening an existing file.
	 * @param file
	 * @throws IOException
	 */
	public AbstractIndex(LargeBinaryFile file) throws IOException {
		this.file = file;
		
		buffer = file.getBuffer();
		buffer.position(0);
		indexImplementation = buffer.get();
		size = buffer.getInt();
		dataOffset = HEADER_SIZE;
		
		String descriptorPath = file.getString();
		String metricName = file.getString();
		init(descriptorPath, metricName);
		
		buffer.position(dataOffset);
	}
	
	
	/**
	 * Constructor for creating a new file.
	 * @param path The path to create the new file at.
	 * @param indexImplementation The type of index to create.
	 * @param size The size of the index, or -1 to make the size the same as the descriptor file.
	 * @param descriptorPath The path to the descriptor file.
	 * @param metricName The name of the metric to use.
	 * @throws IOException
	 */
	public AbstractIndex(String path, byte indexImplementation, int size, String descriptorPath, String metricName) throws IOException {
		init(descriptorPath, metricName);
		
		this.indexImplementation = indexImplementation;
		this.size = size == -1 ? descriptors.getSize() : size;
		dataOffset = HEADER_SIZE;
		
		writeHeader(path, descriptorPath, metricName);
	}
	
	
	protected void writeHeader(String path, String descriptorPath, String metricName) throws IOException {
		this.file = new LargeBinaryFile(path, true);
		
		buffer = file.getBuffer();
		buffer.put(indexImplementation);
		buffer.putInt(size);
		file.putString(descriptorPath);
		file.putString(metricName);
		buffer.position(dataOffset);
	}
	
	
	protected void init(String descriptorPath, String metricName) throws IOException {
		descriptors = DescriptorFileFactory.open(descriptorPath, false);
		space = descriptors.getMetricSpace(metricName);
	}
	
	@Override
	public int getSize() {
		return size;
	}
	
	@Override
	public void close() throws IOException {
		file.getFile().close();
	}
	
	@Override
	public DescriptorFile getDescriptors() {
		return descriptors;
	}

	@Override
	public MetricSpace getMetricSpace() {
		return space;
	}
	
	public byte getIndexImplementation() {
		return indexImplementation;
	}


	protected void resize(int size) throws IOException {
		file.resize(size);
		buffer = file.getBuffer();
	}
}
