package ndi.webapi.domain;

import java.util.HashMap;
import java.util.Map;

import metricspaces._double.DoubleDescriptor;

import org.jongo.marshall.jackson.oid.ObjectId;


public class Image {
	@ObjectId
	private String _id;
	private String imageUrl;
	private Map<String, DoubleDescriptor> descriptors;
	
	public Image(String imageUrl) {
		this();
		this.imageUrl = imageUrl;
	}
	
	public Image() {
		descriptors = new HashMap<>();
	}
	
	public String getId() {
		return _id;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public DoubleDescriptor getDescriptor(String descriptor) {
		return descriptors.get(descriptor);
	}
}
