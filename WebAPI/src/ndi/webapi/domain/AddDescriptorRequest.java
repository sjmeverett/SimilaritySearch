package ndi.webapi.domain;

import metricspaces._double.DoubleDescriptor;

public class AddDescriptorRequest {
	private String imageUrl;
	private String descriptorName;
	private DoubleDescriptor descriptor;
	
	public AddDescriptorRequest() {
		
	}
	
	public AddDescriptorRequest(String imageUrl, String descriptorName, DoubleDescriptor descriptor) {
		this.imageUrl = imageUrl;
		this.descriptorName = descriptorName;
		this.descriptor = descriptor;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public String getDescriptorName() {
		return descriptorName;
	}
	
	public DoubleDescriptor getDescriptor() {
		return descriptor;
	}
}
