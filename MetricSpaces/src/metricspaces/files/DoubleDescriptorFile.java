package metricspaces.files;

import java.io.IOException;
import java.nio.ByteBuffer;

import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.DoubleDescriptor;

/**
 * Implements a descriptor file with double data.
 * @author stewart
 *
 */
public class DoubleDescriptorFile implements DescriptorFile {
	protected final DescriptorFileHeader header;
	protected ByteBuffer buffer;
	protected final int dimensions, capacity, recordSize;
	protected int dataOffset;
	
	protected DoubleDescriptorFile(DescriptorFileHeader header, boolean resize) throws IOException {
		this.header = header;
		dataOffset = header.getDataOffset();
		dimensions = header.getDimensions();
		capacity = header.getCapacity();
		recordSize = dimensions * 8;
		
		if (resize) {
			header.resize(dataOffset + recordSize * capacity);
		}
		
		buffer = header.getBuffer();
		buffer.position(dataOffset);
	}
	
	public DoubleDescriptorFile(DescriptorFileHeader header) throws IOException {
		this(header, header.isWritable());
	}

	@Override
	public Descriptor get() {
		double[] data = new double[dimensions];
		
		for (int i = 0; i < data.length; i++) {
			data[i] = buffer.getDouble(); 
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
			buffer.putDouble(data[i]);
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
