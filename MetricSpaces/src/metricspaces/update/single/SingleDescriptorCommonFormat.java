package metricspaces.update.single;

import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.descriptors.SingleDescriptor;
import metricspaces.update.common.DescriptorFormat;

public class SingleDescriptorCommonFormat implements DescriptorFormat<DoubleDescriptor> {
	private DescriptorFormat<SingleDescriptor> format;
	
	public SingleDescriptorCommonFormat(DescriptorFormat<SingleDescriptor> format) {
		this.format = format;
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

	
	private SingleDescriptor fromCommon(DoubleDescriptor d) {
		double[] data = d.getData();
		float[] single = new float[data.length];
		
		for (int i = 0; i < data.length; i++) {
			single[i] = (float)data[i];
		}
		
		return new SingleDescriptor(single);
	}
	
	
	private DoubleDescriptor toCommon(SingleDescriptor single) {
		float[] data = single.getData();
		double[] d = new double[data.length];
		
		for (int i = 0; i < data.length; i++) {
			d[i] = data[i];
		}
		
		return new DoubleDescriptor(d);
	}
}
