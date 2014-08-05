package metricspaces.hash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;

import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.descriptors.DescriptorFormat;
import metricspaces.metrics.MetricSpace;
import metricspaces.util.LargeBinaryFile;

public class HashDescriptorFile extends AbstractDescriptorFile<HashDescriptor> {
	private HashDescriptorFormat format;
	
	public HashDescriptorFile(LargeBinaryFile file) throws IOException {
		super(file);
		
		ByteBuffer byteBuffer = file.channel.map(file.isWritable() ? MapMode.READ_WRITE : MapMode.READ_ONLY,
				dataOffset, dimensions * size);
		
		format = new HashDescriptorFormat(byteBuffer, dimensions, size);
	}

	@Override
	public MetricSpace getMetricSpace(String metricName) {
		return new HashMetricSpace(this, metricName);
	}

	@Override
	public DescriptorFormat<HashDescriptor> getFormat() {
		return format;
	}

	@Override
	public DescriptorFormat<HashDescriptor> getFormat(ByteBuffer buffer, int size) {
		return new HashDescriptorFormat(buffer, dimensions, size);
	}

}
