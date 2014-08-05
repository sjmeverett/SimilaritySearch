package metricspaces.quantised;

import metricspaces._double.DoubleDescriptor;
import metricspaces.descriptors.DescriptorFormat;

public class QuantisedDescriptorCommonFormat implements DescriptorFormat<DoubleDescriptor> {
	private QuantisedDescriptorFormat format;
	private QuantisedDescriptorContext context;
	
	public QuantisedDescriptorCommonFormat(QuantisedDescriptorFormat format) {
		this.format = format;
		context = format.descriptorContext;
	}
	
	@Override
	public DoubleDescriptor get() {
		return toCommon(format.get());
	}

	@Override
	public DoubleDescriptor get(int index) {
		return toCommon(format.get(index));
	}

	@Override
	public void put(DoubleDescriptor descriptor) {
		format.put(fromCommon(descriptor));
	}

	@Override
	public void put(int index, DoubleDescriptor descriptor) {
		format.put(index, fromCommon(descriptor));
	}

	@Override
	public void position(int index) {
		format.position(index);
	}

	
	private QuantisedDescriptor fromCommon(DoubleDescriptor common) {
		byte[] data = context.getByteData(common.getData());
		return new QuantisedDescriptor(data, context);
	}
	
	
	private DoubleDescriptor toCommon(QuantisedDescriptor quantised) {
		double[] data = context.getDoubleData(quantised.getData());
		return new DoubleDescriptor(data);
	}
}
