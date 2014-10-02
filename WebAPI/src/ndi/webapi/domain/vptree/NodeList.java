package ndi.webapi.domain.vptree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import metricspaces._double.DoubleDescriptor;
import metricspaces.metrics.Metric;
import metricspaces.util.RandomHelper;
import ndi.webapi.domain.Image;

import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

public class NodeList {
	private List<ObjectPointer> objects;
	private MongoCollection images;
	private Metric<DoubleDescriptor> metric;
	private String descriptorName;

	public NodeList(Jongo jongo, MongoCursor<Node> nodesToAdd, Metric<DoubleDescriptor> metric, String descriptorName) {
		this.images = jongo.getCollection("images");
		this.metric = metric;
		this.descriptorName = descriptorName;
		
		objects = new ArrayList<>(nodesToAdd.count());
		
		for (Node node: nodesToAdd) {
			objects.add(new ObjectPointer(node.getImageId()));
		}
	}
	
	
	public ObjectPointer get(int index) {
		return objects.get(index);
	}
	
	
	public int partition(int start, int pivot, int end, ObjectId vantagePointId) {
		DoubleDescriptor vantagePoint = getDescriptor(vantagePointId);
		return partition(start, pivot, end, vantagePointId, vantagePoint);
	}
	
	
	private int partition(int start, int pivot, int end, ObjectId vantagePointId, DoubleDescriptor vantagePoint) {
		//swap the pivot to the end
		ObjectPointer pivotValue = objects.get(pivot);
		Collections.swap(objects, pivot, end);
		
		//swap everything less than the pivot to the left of the list
		int left = start;
		double pivotDistance = pivotValue.getDistance(vantagePointId, vantagePoint);
		
		for (int i = start; i < end; i++) {
			if (objects.get(i).getDistance(vantagePointId, vantagePoint) < pivotDistance) {
				Collections.swap(objects, left, i);
				left++;
			}
		}
		
		//swap the pivot into its correct position
		Collections.swap(objects, end, left);
		return left;
	}
	
	
	public QuickSelectResult quickSelect(int start, int end, int k, ObjectId vantagePointId, DoubleDescriptor vantagePoint) {
		int pivot = start;
		
		while (end > start) {
			//randomly select a pivot index and partition the list
			pivot = RandomHelper.getNextInt(start, end);
			pivot = partition(start, pivot, end, vantagePointId, vantagePoint);

			if (pivot == k) {
				//the k-th element is the pivot, so is in its correct place
				break;
			}
			else if (k < pivot) {
				//the k-th element is to the left of the pivot
				end = pivot - 1;
			}
			else {
				//the k-th element is to the right of the pivots
				start = pivot + 1;
			}
		}
		
		
		ObjectPointer ptr = objects.get(pivot);
		double distance = ptr.getDistance(vantagePointId, vantagePoint);
		return new QuickSelectResult(ptr.getImageId(), distance);
	}
	
	
	private DoubleDescriptor getDescriptor(ObjectId id) {
		Image image = images
			.findOne(id)
			.projection("{'descriptors." + descriptorName + "': 1}")
			.as(Image.class);
		
		return image.getDescriptor(descriptorName);
	}
	
	
	public class ObjectPointer {
	    private ObjectId imageId;
	    private double distance;
	    private ObjectId vantagePointId;

	    public ObjectPointer(ObjectId imageId) {
	        this.imageId = imageId;
	        vantagePointId = null;
	    }

	    public ObjectId getImageId() {
	        return imageId;
	    }

	    public double getDistance(ObjectId vantagePointId, DoubleDescriptor vantagePoint) {
	        if (!this.vantagePointId.equals(vantagePointId)) {
	            this.vantagePointId = vantagePointId;
	            this.distance = metric.getDistance(vantagePoint, getDescriptor(imageId));
	        }

	        return distance;
	    }
	}
	
	
	public class QuickSelectResult {
		private ObjectId imageId;
		private double distance;
		
		private QuickSelectResult(ObjectId imageId, double distance) {
			this.imageId = imageId;
			this.distance = distance;
		}
		
		public ObjectId getImageId() {
			return imageId;
		}
		
		public double getDistance() {
			return distance;
		}
	}
}

