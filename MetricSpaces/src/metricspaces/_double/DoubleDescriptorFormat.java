package metricspaces._double;

import java.nio.DoubleBuffer;

import metricspaces.descriptors.DescriptorFormat;

public class DoubleDescriptorFormat implements DescriptorFormat<DoubleDescriptor> {
	private final DoubleBuffer buffer;
	private final int dimensions;
	private final int size;
	
	public DoubleDescriptorFormat(DoubleBuffer buffer, int dimensions, int size) {
		this.buffer = buffer;
		this.dimensions = dimensions;
		this.size = size;
	}
	
	@Override
	public DoubleDescriptor get() {
		double[] data = new double[dimensions];
		buffer.get(data);
		
		return new DoubleDescriptor(data);
	}

	@Override
	public DoubleDescriptor get(int index) {
		position(index);
		return get();
	}

	@Override
	public void put(DoubleDescriptor descriptor) {
		double[] data = descriptor.getData();
		buffer.put(data);
	}
	
	@Override
	public void put(int index, DoubleDescriptor descriptor) {
		position(index);
		put(descriptor);
	}
	
	@Override
	public void position(int index) {
		if (index >= size)
			throw new ArrayIndexOutOfBoundsException(index);
		
		buffer.position(index * dimensions);
	}
}
