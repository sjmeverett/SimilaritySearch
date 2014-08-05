package metricspaces.update.single;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel.MapMode;

import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.descriptors.SingleDescriptor;
import metricspaces.update.common.AbstractDescriptorFile;
import metricspaces.update.common.CommonDescriptorFile;
import metricspaces.update.common.DescriptorFormat;
import metricspaces.update.common.LargeBinaryFile;
import metricspaces.update.common.MetricSpace;

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
		
		FloatBuffer floatBuffer = file.channel.map(file.isWritable() ? MapMode.READ_WRITE : MapMode.READ_ONLY,
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
