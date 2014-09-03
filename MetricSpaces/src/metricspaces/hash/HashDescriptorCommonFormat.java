package metricspaces.hash;

import metricspaces._double.DoubleDescriptor;
import metricspaces.descriptors.DescriptorFormat;

public class HashDescriptorCommonFormat implements DescriptorFormat<DoubleDescriptor> {
	private HashDescriptorFormat format;
	
	public HashDescriptorCommonFormat(HashDescriptorFormat format) {
		this.format = format;
	}
	
	@Override
	public DoubleDescriptor get() {
		throw new UnsupportedOperationException("Conversion from hash descriptor to common format is not supported " +
				"as there is not enough information.");
	}

	@Override
	public DoubleDescriptor get(int index) {
		throw new UnsupportedOperationException("Conversion from hash descriptor to common format is not supported " +
				"as there is not enough information.");
	}

	@Override
	public void put(DoubleDescriptor descriptor) {
		format.put(makeHash(descriptor.getData()));
	}

	@Override
	public void put(int index, DoubleDescriptor descriptor) {
		format.put(index, makeHash(descriptor.getData()));
	}

	@Override
	public void position(int index) {
		format.position(index);
	}

	
	private HashDescriptor makeHash(double[] values) {
		//compute the average value
		double average = 0;
		
		for (int i = 0; i < values.length; i++) {
			average += values[i];
		}
		
		average /= values.length;
		
		//make the hash
		int dimensions = (int)Math.ceil((double)values.length / 8);
		byte[] data = new byte[dimensions];
		int index = 0;
		
		for (int i = 0; i < dimensions; i++) {
			byte b = 0;
			
			for (int j = 0; j < 8; j++) {
				b <<= 1;
				
				if (values[index++] > average) {
					b |= 1;
				}
			}
			
			data[i] = b;
		}
		
		return new HashDescriptor(data);
	}
}
