package ndi.webapi.domain;

public class SearchResult {
	private Image image;
	private double distance;
	
	public SearchResult(Image image, double distance) {
		this.image = image;
		this.distance = distance;
	}
	
	public Image getImage() {
		return image;
	}
	
	public double getDistance() {
		return distance;
	}
}
