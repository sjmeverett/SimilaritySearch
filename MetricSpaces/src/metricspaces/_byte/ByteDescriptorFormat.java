package metricspaces._byte;

import java.nio.ByteBuffer;

import metricspaces.descriptors.DescriptorFormat;

public abstract class ByteDescriptorFormat<DescriptorType> implements DescriptorFormat<DescriptorType> {
	private final ByteBuffer buffer;
	private final int dimensions;
	private final int size;
	
	public ByteDescriptorFormat(ByteBuffer buffer, int dimensions, int size) {
		this.buffer = buffer;
		this.dimensions = dimensions;
		this.size = size;
	}
	
	
	@Override
	public void position(int index) {
		if (index >= size)
			throw new ArrayIndexOutOfBoundsException(index);
		
		buffer.position(index * dimensions);
	}
	
	
	protected byte[] getBytes() {
		byte[] data = new byte[dimensions];
		buffer.get(data);
		
		return data;
	}
	
	
	protected void putBytes(byte[] bytes) {
		buffer.put(bytes);
	}
}
