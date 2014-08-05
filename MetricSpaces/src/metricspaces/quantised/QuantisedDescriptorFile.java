package metricspaces.quantised;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;

import metricspaces._double.DoubleDescriptor;
import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.descriptors.CommonDescriptorFile;
import metricspaces.descriptors.DescriptorFormat;
import metricspaces.metrics.MetricSpace;
import metricspaces.util.LargeBinaryFile;

public class QuantisedDescriptorFile extends AbstractDescriptorFile<QuantisedDescriptor> implements CommonDescriptorFile {
	private final QuantisedDescriptorContext descriptorContext;
	private QuantisedDescriptorFormat format;
	
	public QuantisedDescriptorFile(LargeBinaryFile file) throws IOException {
		super(file);
		
		double elementMax = buffer.getDouble();
		int l1norm = buffer.getInt();
		descriptorContext = new QuantisedDescriptorContext(elementMax, l1norm);
		dataOffset = buffer.position();
		
		ByteBuffer byteBuffer = file.channel.map(file.isWritable() ? MapMode.READ_WRITE : MapMode.READ_ONLY,
				dataOffset, dimensions * size);
		
		format = new QuantisedDescriptorFormat(byteBuffer, dimensions, size, descriptorContext);
	}
	
	public QuantisedDescriptorFile(LargeBinaryFile file, double elementMax, int l1Norm) throws IOException {
		super(file);
		
		if (!file.isWritable())
			throw new IllegalStateException("file must be writable for this constructor");
		
		descriptorContext = new QuantisedDescriptorContext(elementMax, l1Norm);
		buffer.putDouble(elementMax);
		buffer.putInt(l1Norm);
		dataOffset = buffer.position();
		
		ByteBuffer byteBuffer = file.channel.map(MapMode.READ_WRITE, dataOffset, dimensions * size * 8);
		format = new QuantisedDescriptorFormat(byteBuffer, dimensions, size, descriptorContext);
	}

	@Override
	public MetricSpace getMetricSpace(String metricName) {
		return new QuantisedMetricSpace(this, metricName);
	}

	@Override
	public DescriptorFormat<QuantisedDescriptor> getFormat() {
		return format;
	}

	@Override
	public DescriptorFormat<QuantisedDescriptor> getFormat(ByteBuffer buffer, int size) {
		return new QuantisedDescriptorFormat(buffer, dimensions, size, descriptorContext);
	}

	@Override
	public DescriptorFormat<DoubleDescriptor> getCommonFormat() {
		return new QuantisedDescriptorCommonFormat(format);
	}
	
	/**
	 * Gets the settings used to quantise descriptors.
	 * @return
	 */
	public QuantisedDescriptorContext getDescriptorContext() {
		return descriptorContext;
	}
}
