package metricspaces.files;

import java.io.IOException;
import java.nio.ByteBuffer;

import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.descriptors.ObjectWithDescriptor;

/**
 * Implements a descriptor file with double data and integer object IDs.
 * @author stewart
 *
 */
public class DoubleDescriptorFile implements DescriptorFile<Integer, Descriptor> {
	private DescriptorFileHeader header;
	private final ByteBuffer buffer;
	private final int dataOffset, dimensions, capacity, recordSize;
	
	public DoubleDescriptorFile(DescriptorFileHeader header) throws IOException {
		this.header = header;
		dataOffset = header.getDataOffset();
		dimensions = header.getDimensions();
		capacity = header.getCapacity();
		recordSize = dimensions * 8 + 4;
		
		if (header.isWritable()) {
			header.resize(dataOffset + recordSize * capacity);
		}
		
		buffer = header.getBuffer();
		buffer.position(dataOffset);
	}

	@Override
	public ObjectWithDescriptor<Integer, Descriptor> get() {
		int objectId = buffer.getInt();
		double[] data = new double[dimensions];
		
		for (int i = 0; i < data.length; i++) {
			data[i] = buffer.getDouble(); 
		}
		
		return new ObjectWithDescriptor<Integer, Descriptor>(objectId, new DoubleDescriptor(data));
	}

	@Override
	public ObjectWithDescriptor<Integer, Descriptor> get(int index) {
		if (index >= capacity)
			throw new ArrayIndexOutOfBoundsException(index);
		
		position(index);
		return get();
	}

	@Override
	public void put(ObjectWithDescriptor<Integer, Descriptor> object) {
		if (!header.isWritable())
			throw new IllegalStateException("file is not writable");
			
		buffer.putInt(object.getObject());
		
		double[] data = object.getDescriptor().getData();
		assert(data.length == dimensions);
		
		for (int i = 0; i < data.length; i++) {
			buffer.putDouble(data[i]);
		}
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
