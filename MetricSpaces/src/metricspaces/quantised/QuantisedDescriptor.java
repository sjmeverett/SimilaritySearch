package metricspaces.quantised;


public class QuantisedDescriptor {
	private byte[] data;
	private QuantisedDescriptorContext context;
	
	public QuantisedDescriptor(byte[] data, QuantisedDescriptorContext context) {
		this.data = data;
		this.context = context;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public QuantisedDescriptorContext getContext() {
		return context;
	}
}
