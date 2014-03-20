package metricspaces.files;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.naming.Context;

import metricspaces.descriptors.ByteDescriptor;
import metricspaces.descriptors.ByteDescriptorContext;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.ObjectWithDescriptor;

/**
 * Implements a descriptor file with byte data and integer object IDs.
 * @author stewart
 *
 */
public class ByteDescriptorFile implements DescriptorFile<Integer, Descriptor> {
	private DescriptorFileHeader header;
	private final ByteBuffer buffer;
	private final int dataOffset, dimensions, capacity, recordSize;
	private final ByteDescriptorContext descriptorContext;
	
	public ByteDescriptorFile(DescriptorFileHeader header) {
		this.header = header;
		buffer = header.getBuffer();
		dimensions = header.getDimensions();
		capacity = header.getCapacity();
		recordSize = dimensions + 4;
		
		double elementMax = buffer.getDouble();
		int l1norm = buffer.getInt();
		descriptorContext = new ByteDescriptorContext(elementMax, l1norm);
		
		dataOffset = buffer.position();
	}
	
	public ByteDescriptorFile(DescriptorFileHeader header, double elementMax, int l1norm) throws IOException {
		if (!header.isWritable())
			throw new IllegalArgumentException("header must be writable for this constructor");
		
		this.header = header;
		dimensions = header.getDimensions();
		capacity = header.getCapacity();
		recordSize = dimensions + 4;
		
		ByteBuffer b = header.getBuffer();
		b.putDouble(elementMax);
		b.putInt(l1norm);
		descriptorContext = new ByteDescriptorContext(elementMax, l1norm);
		
		dataOffset = b.position();
		header.resize(dataOffset + recordSize * capacity);
		buffer = header.getBuffer();
		buffer.position(dataOffset);
	}
	
	@Override
	public ObjectWithDescriptor<Integer, Descriptor> get() {
		int objectId = buffer.getInt();
		byte[] data = new byte[dimensions];
		buffer.get(data);
		
		ByteDescriptor descriptor = new ByteDescriptor(data, descriptorContext);
		return new ObjectWithDescriptor<Integer, Descriptor>(objectId, descriptor);
	}

	@Override
	public ObjectWithDescriptor<Integer, Descriptor> get(int index) {
		if (index > capacity)
			throw new ArrayIndexOutOfBoundsException(index);
		
		position(index);
		return get();
	}

	@Override
	public void put(ObjectWithDescriptor<Integer, Descriptor> object) {
		if (!header.isWritable())
			throw new IllegalStateException("file is not writable");
		
		buffer.putInt(object.getObject());
	}

	@Override
	public void position(int index) {
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
