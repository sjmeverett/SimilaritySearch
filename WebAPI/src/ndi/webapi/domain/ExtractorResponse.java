package ndi.webapi.domain;

import metricspaces._double.DoubleDescriptor;

public class ExtractorResponse { 
	private String imageUrl;
	private String descriptorName;
	private DoubleDescriptor descriptor;

	
	public ExtractorResponse(DoubleDescriptor descriptor, ExtractorRequest request) {
		this.imageUrl = request.getImageUrl();
		this.descriptorName = request.getDescriptorName();
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
