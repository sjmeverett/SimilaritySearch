package metricspaces.single;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel.MapMode;

import metricspaces._double.DoubleDescriptor;
import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.descriptors.CommonDescriptorFile;
import metricspaces.descriptors.DescriptorFile;
import metricspaces.descriptors.DescriptorFormat;
import metricspaces.metrics.MetricSpace;
import metricspaces.util.LargeBinaryFile;

/**
 * Represents a file with descriptors composed of single-precision float data.
 * @author stewart
 *
 */
public class SingleDescriptorFile extends AbstractDescriptorFile<SingleDescriptor> implements CommonDescriptorFile {
	protected final int recordSize;
	protected SingleDescriptorFormat format;
	
	/**
	 * Constructor.
	 * @param file
	 * @throws IOException
	 */
	public SingleDescriptorFile(LargeBinaryFile file) throws IOException {
		super(file);
		
		recordSize = dimensions * 4;
		
		FloatBuffer floatBuffer = file.channel.map(MapMode.READ_ONLY,
			dataOffset, recordSize * size).asFloatBuffer();
		
		format = new SingleDescriptorFormat(floatBuffer, dimensions, size);
	}
	
	
	public SingleDescriptorFile(String path, int size, int dimensions, String descriptorName) throws IOException {
		super(path, DescriptorFile.SINGLE_TYPE, size, dimensions, descriptorName);
		
		recordSize = dimensions * 4;
		
		FloatBuffer floatBuffer = file.channel.map(MapMode.READ_WRITE,
			dataOffset, recordSize * size).asFloatBuffer();
		
		format = new SingleDescriptorFormat(floatBuffer, dimensions, size);
	}

	@Override
	public MetricSpace getMetricSpace(String metricName) {
		return new SingleMetricSpace(this, metricName);
	}

	@Override
	public DescriptorFormat<SingleDescriptor> getFormat() {
		return format;
	}

	@Override
	public DescriptorFormat<SingleDescriptor> getFormat(ByteBuffer buffer, int size) {
		return new SingleDescriptorFormat(buffer.asFloatBuffer(), dimensions, size);
	}

	@Override
	public DescriptorFormat<DoubleDescriptor> getCommonFormat() {
		return new SingleDescriptorCommonFormat(format);
	}
}
