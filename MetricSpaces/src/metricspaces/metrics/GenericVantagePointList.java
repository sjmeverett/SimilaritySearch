package metricspaces.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import metricspaces.descriptors.DescriptorFormat;
import metricspaces.indices.SearchResult;
import metricspaces.util.RandomHelper;


public class GenericVantagePointList<DescriptorType> implements VantagePointList {
	private List<ObjectPointer> objects;
	private DescriptorFormat<DescriptorType> descriptors;
	private Metric<DescriptorType> metric;
	
	
	/**
	 * Constructor
	 * @param descriptors
	 * @param metric
	 * @param keys
	 */
	public GenericVantagePointList(DescriptorFormat<DescriptorType> descriptors, Metric<DescriptorType> metric, List<Integer> keys) {
		this.descriptors = descriptors;
		this.metric = metric;
		
		if (keys != null) {
			this.objects = new ArrayList<>(keys.size());
			
			for (int id: keys) {
				add(id);
			}
		}
		else {
			this.objects = new ArrayList<>();
		}
	}
	
	
	@Override
	public void add(int id) {
		objects.add(new ObjectPointer(id));
	}
	
	
	@Override
	public MetricSpaceObject get(int index) {
		return objects.get(index);
	}
	
	
	@Override
	public int partition(int start, int pivot, int end, int vantagePointId) {
		DescriptorType vantagePoint = descriptors.get(vantagePointId);
		return partition(start, pivot, end, vantagePointId, vantagePoint);
	}
	
	
	private int partition(int start, int pivot, int end, int vantagePointId, DescriptorType vantagePoint) {
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
	
	
	@Override
	public SearchResult quickSelect(int start, int end, int k, int vantagePointId) {
		DescriptorType vantagePoint = descriptors.get(vantagePointId);
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
		return new SearchResult(ptr.getObjectID(), distance);
	}
	
	
	private class ObjectPointer implements MetricSpaceObject {
	    private int objectID;
	    private double distance;
	    private int vantagePointId;

	    public ObjectPointer(int objectID) {
	        this.objectID = objectID;
	        vantagePointId = -1;
	    }

	    public int getObjectID() {
	        return objectID;
	    }

	    public double getDistance(int vantagePointId, DescriptorType vantagePoint) {
	        if (this.vantagePointId != vantagePointId) {
	            this.vantagePointId = vantagePointId;
	            this.distance = metric.getDistance(vantagePoint, descriptors.get(objectID));
	        }

	        return distance;
	    }
	    
	    @Override
	    public double getDistance(int vantagePointId) {
	    	if (this.vantagePointId != vantagePointId) {
	            this.vantagePointId = vantagePointId;
	            DescriptorType vantagePoint = descriptors.get(vantagePointId);
	            this.distance = metric.getDistance(vantagePoint, descriptors.get(objectID));
	        }

	        return distance;
	    }
	}
}
