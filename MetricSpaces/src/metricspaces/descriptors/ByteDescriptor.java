package metricspaces.descriptors;

/**
 * An implementation of Descriptor backed by byte data, in order to take advantage of more
 * efficient algorithms.
 * @author stewart
 *
 */
public class ByteDescriptor extends DoubleDescriptor {
	private byte[] byteData;
	private QuantisedDescriptorContext context;

	
	public ByteDescriptor(byte[] data, QuantisedDescriptorContext context) {
		this.byteData = data;
		this.context = context;
	}
	
	
	public ByteDescriptor(double[] data, QuantisedDescriptorContext context) {
		super(data);
		this.context = context;
		this.byteData = context.getByteData(data);
	}
	
	
	@Override
	public double[] getData() {
		if (super.data == null) {
			super.data = context.getDoubleData(byteData);
		}
		
		return super.data;
	}
	

	@Override
	public double[] getNormalisedData() {
		if (super.normalisedData == null) {
			if (context.isNormalised()) {
				//if the context supports it, we can normalise quickly using a lookup table
				super.normalisedData = context.getNormalisedDoubleData(byteData);
			}
			else {
				return super.getNormalisedData();
			}
		}
		
		return super.normalisedData;
	}


	@Override
	public double getComplexity() {
		if (Double.isNaN(super.complexity)) {
			if (context.isNormalised()) {
				complexity = context.getComplexity(byteData);
			}
			else {
				return super.getComplexity();
			}
		}
		
		return super.complexity;
	}

	
	@Override
	public double getMergedComplexity(Descriptor descriptor) {
		if (context.isNormalised() && descriptor instanceof ByteDescriptor) {
			ByteDescriptor byteDescriptor = (ByteDescriptor)descriptor;
			
			if (byteDescriptor.context == this.context)
				return context.getMergedComplexity(byteData, byteDescriptor.byteData);
		}
		
		return super.getMergedComplexity(descriptor);
	}
	
	
	@Override
	public double getMagnitude() {
		//make sure the superclass has the double data
		getData();
		return super.getMagnitude();
	}
	
	
	public byte[] getByteData() {
		return byteData;
	}
	
	
	public QuantisedDescriptorContext getContext() {
		return context;
	}
	
	
	@Override
	public int getDimensions() {
		return byteData.length;
	}
}
