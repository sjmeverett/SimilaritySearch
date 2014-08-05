package metricspaces.single;

import java.nio.FloatBuffer;

import metricspaces.descriptors.DescriptorFormat;

public class SingleDescriptorFormat implements DescriptorFormat<SingleDescriptor> {
	private final FloatBuffer buffer;
	private final int dimensions;
	private final int size;
	
	public SingleDescriptorFormat(FloatBuffer buffer, int dimensions, int size) {
		this.buffer = buffer;
		this.dimensions = dimensions;
		this.size = size;
	}
	
	@Override
	public SingleDescriptor get() {
		float[] data = new float[dimensions];
		buffer.get(data);
		
		return new SingleDescriptor(data);
	}

	@Override
	public SingleDescriptor get(int index) {
		position(index);
		return get();
	}

	@Override
	public void put(SingleDescriptor descriptor) {
		float[] data = descriptor.getData();
		buffer.put(data);
	}
	
	@Override
	public void put(int index, SingleDescriptor descriptor) {
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
