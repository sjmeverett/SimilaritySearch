package metricspaces.files;

import java.io.IOException;
import java.nio.ByteBuffer;

import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.DoubleDescriptor;

/**
 * Implements a descriptor file with single (float) data.
 * @author stewart
 *
 */
public class SingleDescriptorFile implements DescriptorFile {
	private DescriptorFileHeader header;
	private final ByteBuffer buffer;
	private final int dataOffset, dimensions, capacity, recordSize;
	
	public SingleDescriptorFile(DescriptorFileHeader header) throws IOException {
		this.header = header;
		dataOffset = header.getDataOffset();
		dimensions = header.getDimensions();
		capacity = header.getCapacity();
		recordSize = dimensions * 4;
		
		if (header.isWritable()) {
			header.resize(dataOffset + recordSize * capacity);
		}
		
		buffer = header.getBuffer();
		buffer.position(dataOffset);
	}

	@Override
	public Descriptor get() {
		double[] data = new double[dimensions];
		
		for (int i = 0; i < data.length; i++) {
			data[i] = buffer.getFloat(); 
		}
		
		return new DoubleDescriptor(data);
	}

	@Override
	public Descriptor get(int index) {
		position(index);
		return get();
	}

	@Override
	public void put(Descriptor descriptor) {
		if (!header.isWritable())
			throw new IllegalStateException("file is not writable");
		
		double[] data = descriptor.getData();
		assert(data.length == dimensions);
		
		for (int i = 0; i < data.length; i++) {
			buffer.putFloat((float)data[i]);
		}
	}
	
	@Override
	public void put(int index, Descriptor descriptor) {
		position(index);
		put(descriptor);
	}

	@Override
	public void position(int index) {
		if (index >= capacity)
			throw new ArrayIndexOutOfBoundsException(index);
		
		buffer.position(dataOffset + index * recordSize);
	}

	@Override
	public void close() throws IOException {
		header.getFile().close();
	}
	
	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public int getDimensions() {
		return dimensions;
	}

	@Override
	public DescriptorFileHeader getHeader() {
		return header;
	}
}
