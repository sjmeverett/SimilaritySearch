package ndi.webapi.domain.vptree;

import ndi.webapi.domain.VersionedEntity;

import org.bson.types.ObjectId;

public class Node implements VersionedEntity {
	private ObjectId _id;
	private ObjectId indexId;
	private ObjectId left;
	private ObjectId right;
	private ObjectId imageId;
	private double radius;
	private double distanceToParent;
	private int version;
	
	public Node() {
		this.distanceToParent = Double.NaN;
	}
	
	public Node(ObjectId imageId, ObjectId indexId) {
		this();
		this.imageId = imageId;
		this.indexId = indexId;
	}
	
	public ObjectId getId() {
		return _id;
	}
	
	public ObjectId getIndexId() {
		return indexId;
	}
	
	public ObjectId getLeft() {
		return left;
	}
	
	public ObjectId getRight() {
		return right;
	}
	
	public ObjectId getImageId() {
		return imageId;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public double getDistanceToParent() {
		return distanceToParent;
	}
	
	public void setLeft(ObjectId left) {
		this.left = left;
	}
	
	public void setRight(ObjectId right) {
		this.right = right;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public void setDistanceToParent(double distanceToParent) {
		this.distanceToParent = distanceToParent;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
}
