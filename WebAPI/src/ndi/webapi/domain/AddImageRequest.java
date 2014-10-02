package ndi.webapi.domain;

public class AddImageRequest {
	private String imageUrl;
	
	public AddImageRequest() {
		
	}
	
	public AddImageRequest(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
}
