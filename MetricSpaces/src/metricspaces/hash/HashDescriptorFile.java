package metricspaces.hash;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;

import metricspaces._double.DoubleDescriptor;
import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.descriptors.CommonDescriptorFile;
import metricspaces.descriptors.DescriptorFile;
import metricspaces.descriptors.DescriptorFormat;
import metricspaces.metrics.MetricSpace;
import metricspaces.util.LargeBinaryFile;

public class HashDescriptorFile extends AbstractDescriptorFile<HashDescriptor> implements CommonDescriptorFile {
	private HashDescriptorFormat format;
	
	public HashDescriptorFile(LargeBinaryFile file) throws IOException {
		super(file);
		
		ByteBuffer byteBuffer = file.channel.map(MapMode.READ_ONLY,
				dataOffset, dimensions * size);
		
		format = new HashDescriptorFormat(byteBuffer, dimensions, size);
	}
	
	public HashDescriptorFile(String path, int size, int dimensions, String descriptorName) throws IOException {
		super(path, DescriptorFile.HASH_TYPE, size, dimensions, descriptorName);
		
		ByteBuffer byteBuffer = file.channel.map(MapMode.READ_WRITE,
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

	@Override
	public DescriptorFormat<DoubleDescriptor> getCommonFormat() {
		return new HashDescriptorCommonFormat(format);
	}

}
