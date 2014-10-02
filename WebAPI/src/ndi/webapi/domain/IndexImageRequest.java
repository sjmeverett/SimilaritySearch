package ndi.webapi.domain;

import org.bson.types.ObjectId;

public class IndexImageRequest {
	private String imageId;
	private String descriptorName;
	private String metricName;
	
	public IndexImageRequest() {
		
	}
	
	public IndexImageRequest(String imageId, String descriptorName, String metricName) {
		this.imageId = imageId;
		this.descriptorName = descriptorName;
		this.metricName = metricName;
	}
	
	public ObjectId getImageId() {
		return new ObjectId(imageId);
	}
	
	public String getDescriptorName() {
		return descriptorName;
	}
	
	public String getMetricName() {
		return metricName;
	}
}
