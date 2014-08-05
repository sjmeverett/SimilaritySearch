package metricspaces.quantised;

import java.nio.ByteBuffer;

import metricspaces._byte.ByteDescriptorFormat;

public class QuantisedDescriptorFormat extends ByteDescriptorFormat<QuantisedDescriptor> {
	protected QuantisedDescriptorContext descriptorContext;
	
	
	public QuantisedDescriptorFormat(ByteBuffer buffer, int dimensions, int size, QuantisedDescriptorContext context) {
		super(buffer, dimensions, size);
		this.descriptorContext = context;
	}
	
	@Override
	public QuantisedDescriptor get() {
		return new QuantisedDescriptor(getBytes(), descriptorContext);
	}

	@Override
	public QuantisedDescriptor get(int index) {
		position(index);
		return get();
	}

	@Override
	public void put(QuantisedDescriptor descriptor) {
		putBytes(descriptor.getData());
	}

	@Override
	public void put(int index, QuantisedDescriptor descriptor) {
		position(index);
		put(descriptor);
	}
}
