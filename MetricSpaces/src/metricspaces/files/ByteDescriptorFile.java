package metricspaces.files;

import java.io.IOException;
import java.nio.ByteBuffer;

import metricspaces.descriptors.ByteDescriptor;
import metricspaces.descriptors.QuantisedDescriptorContext;
import metricspaces.descriptors.Descriptor;

/**
 * Implements a descriptor file with byte data.
 * @author stewart
 *
 */
public class ByteDescriptorFile implements DescriptorFile {
	private DescriptorFileHeader header;
	private final ByteBuffer buffer;
	private final int dataOffset, dimensions, capacity, recordSize;
	private final QuantisedDescriptorContext descriptorContext;
	
	public ByteDescriptorFile(DescriptorFileHeader header) {
		this.header = header;
		buffer = header.getBuffer();
		dimensions = header.getDimensions();
		capacity = header.getCapacity();
		recordSize = dimensions;
		
		double elementMax = buffer.getDouble();
		int l1norm = buffer.getInt();
		descriptorContext = new QuantisedDescriptorContext(elementMax, l1norm);
		
		dataOffset = buffer.position();
	}
	
	public ByteDescriptorFile(DescriptorFileHeader header, double elementMax, int l1norm) throws IOException {
		if (!header.isWritable())
			throw new IllegalArgumentException("header must be writable for this constructor");
		
		this.header = header;
		dimensions = header.getDimensions();
		capacity = header.getCapacity();
		recordSize = dimensions;
		
		ByteBuffer b = header.getBuffer();
		b.putDouble(elementMax);
		b.putInt(l1norm);
		descriptorContext = new QuantisedDescriptorContext(elementMax, l1norm);
		
		dataOffset = b.position();
		header.resize(dataOffset + recordSize * capacity);
		buffer = header.getBuffer();
		buffer.position(dataOffset);
	}
	
	@Override
	public Descriptor get() {
		byte[] data = new byte[dimensions];
		buffer.get(data);
		
		return new ByteDescriptor(data, descriptorContext);
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
		
		if (!(descriptor instanceof ByteDescriptor))
			throw new IllegalArgumentException("descriptor is not a byte descriptor");
		
		buffer.put(((ByteDescriptor)descriptor).getByteData());
	}
	
	@Override
	public void put(int index, Descriptor descriptor) {
		position(index);
		put(descriptor);
	}

	@Override
	public void position(int index) {
		if (index > capacity)
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
