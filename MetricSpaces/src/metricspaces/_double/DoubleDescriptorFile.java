package metricspaces._double;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.FileChannel.MapMode;

import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.descriptors.CommonDescriptorFile;
import metricspaces.descriptors.DescriptorFile;
import metricspaces.descriptors.DescriptorFormat;
import metricspaces.metrics.MetricSpace;
import metricspaces.util.LargeBinaryFile;

/**
 * Represents a file containing descriptors composed of double information.
 * @author stewart
 *
 */
public class DoubleDescriptorFile extends AbstractDescriptorFile<DoubleDescriptor> implements CommonDescriptorFile {
	protected DoubleDescriptorFormat format;
	
	/**
	 * Constructor.
	 * @param file
	 * @throws IOException
	 */
	public DoubleDescriptorFile(LargeBinaryFile file) throws IOException {
		super(file);
		
		DoubleBuffer doubleBuffer = file.channel.map(MapMode.READ_ONLY,
			dataOffset, dimensions * size * 8).asDoubleBuffer();
		
		format = new DoubleDescriptorFormat(doubleBuffer, dimensions, size);
	}
	
	
	public DoubleDescriptorFile(String path, int size, int dimensions, String descriptorName) throws IOException {
		super(path, DescriptorFile.DOUBLE_TYPE, size, dimensions, descriptorName);
		
		DoubleBuffer doubleBuffer = file.channel.map(MapMode.READ_WRITE,
			dataOffset, dimensions * size * 8).asDoubleBuffer();
		
		format = new DoubleDescriptorFormat(doubleBuffer, dimensions, size);
	}

	@Override
	public MetricSpace getMetricSpace(String metricName) {
		return new DoubleMetricSpace(this, metricName);
	}

	@Override
	public DescriptorFormat<DoubleDescriptor> getFormat() {
		return format;
	}

	@Override
	public DescriptorFormat<DoubleDescriptor> getFormat(ByteBuffer buffer, int size) {
		return new DoubleDescriptorFormat(buffer.asDoubleBuffer(), dimensions, size);
	}

	@Override
	public DescriptorFormat<DoubleDescriptor> getCommonFormat() {
		return format;
	}
}
