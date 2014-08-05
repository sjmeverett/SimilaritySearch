package metricspaces.hash;

import java.nio.ByteBuffer;

import metricspaces._byte.ByteDescriptorFormat;

public class HashDescriptorFormat extends ByteDescriptorFormat<HashDescriptor> {

	public HashDescriptorFormat(ByteBuffer buffer, int dimensions, int size) {
		super(buffer, dimensions, size);
	}

	@Override
	public HashDescriptor get() {
		return new HashDescriptor(getBytes());
	}

	@Override
	public void put(HashDescriptor descriptor) {
		putBytes(descriptor.getHash());
	}
}
