package metricspaces.indexes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import metricspaces.ListUtilities;
import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.ObjectWithDescriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.metrics.Metric;

public class VantagePointTreeIndex<ObjectType, DescriptorType extends Descriptor> implements Index<ObjectType, DescriptorType> {
	private final IndexFileHeader header;
	private final DescriptorFile<ObjectType, DescriptorType> objects;
	private final Metric<DescriptorType> metric;
	private final ByteBuffer buffer;
	private final Progress progress;
	private final int numberOfObjects;
	private int distanceCalculations;
	
	private static final int NODE_SIZE = 28;
	
	
	public VantagePointTreeIndex(IndexFileHeader header, DescriptorFile<ObjectType, DescriptorType> objects,
			Metric<DescriptorType> metric, Progress progress) throws IOException {
		
		this.header = header;
		this.objects = objects;
		this.metric = metric;
		this.progress = progress;
		numberOfObjects = objects.getCapacity();
		
		if (header.isWritable()) {
			header.resize(header.getDataOffset() + numberOfObjects * NODE_SIZE);
		}
		
		buffer = header.getBuffer();
	}
	
	
	@Override
	public void build() {
		if (!header.isWritable())
            throw new IllegalStateException("Cannot build index in read-only mode.");

        progress.setOperation("Building index", numberOfObjects);

        //make a list with pointers to all the records
        List<ObjectPointer> nodes = new ArrayList<ObjectPointer>(numberOfObjects);

        for (int i = 0; i < numberOfObjects; i++) {
            nodes.add(new ObjectPointer(i));
        }

        createNode(nodes, 0, numberOfObjects - 1, null);
    }


    private int createNode(List<ObjectPointer> nodes, int start, int end, DescriptorType parent) {
        int nodeOffset = buffer.position();
        //reserve space for the node
        buffer.position(nodeOffset + NODE_SIZE);

        double radius = 0;
        double distanceToParent = Double.NaN;
        int left = 0;
        int right = 0;

        final int vantagePointObjectId = nodes.get(start).getObjectID();
        final DescriptorType vantagePoint = objects.get(vantagePointObjectId).getDescriptor();

        int size = end - start + 1;

        if (size > 1) {
            //take the vantage point out the list
            start++;

            //we will partially sort the list based on the relative distance
            //of points to the vantage point
            Comparator<ObjectPointer> comparator = new Comparator<ObjectPointer>() {
                @Override
                public int compare(ObjectPointer a, ObjectPointer b) {
                    double distanceA = a.distance(vantagePointObjectId, vantagePoint, objects);
                    double distanceB = b.distance(vantagePointObjectId, vantagePoint, objects);

                    return Double.compare(distanceA, distanceB);
                }
            };

            //set the radius to be the median distance from the vantage point to all the other points
            int medianIndex = (end - start) / 2 + start;
            ObjectPointer medianPointer = ListUtilities.quickSelect(nodes, start, end, medianIndex, comparator);
            radius = medianPointer.distance(vantagePointObjectId, vantagePoint, objects);

            //everything less than or equal to the median distance (i.e., inside the circle)
            //goes in the left subtree
            left = createNode(nodes, start, medianIndex, vantagePoint);

            //everything greater than the median distance (i.e., outside the circle) goes in
            //the right subtree (as long as there are points left)
            if (end > medianIndex)
                right = createNode(nodes, medianIndex + 1, end, vantagePoint);
        }


        if (parent != null)
            distanceToParent = getDistance(parent, vantagePoint);

        //save the current buffer position which will be after this nodes entire subtree
        int position = buffer.position();

        //position the buffer at the space we reserved for the node
        buffer.position(nodeOffset);
        buffer.putDouble(radius);
        buffer.putDouble(distanceToParent);
        buffer.putInt(left);
        buffer.putInt(right);
        buffer.putInt(vantagePointObjectId);

        //return the buffer position to after the subtree, which will be the start of the blank space
        buffer.position(position);

        progress.incrementDone();
        return nodeOffset;
    }

	@Override
	public List<SearchResult<ObjectType>> search(DescriptorType query, double radius) {
		List<SearchResult<ObjectType>> results = new ArrayList<SearchResult<ObjectType>>();
		
        search(0, query, radius, results, Double.NaN);

        return results;
    }


    private void search(int offset, DescriptorType query, double searchRadius, List<SearchResult<ObjectType>> results, double parentToQueryDistance) {
        //move the buffer position to the start of the node
        buffer.position(offset);

        //the node should consist of a pointer to each child node then the point data, then the value data
        double nodeRadius = buffer.getDouble();
        double parentToThisDistance = buffer.getDouble();
        int left = buffer.getInt();
        int right = buffer.getInt();

        //check if we can skip a distance calculation using the triangle inequality
        if (Double.isNaN(parentToQueryDistance)
            || Math.abs(parentToThisDistance - parentToQueryDistance) <= nodeRadius + searchRadius)
        {
            int objectID = buffer.getInt();
            ObjectWithDescriptor<ObjectType, DescriptorType> vantagePoint = objects.get(objectID);
            double distance = getDistance(query, vantagePoint.getDescriptor());

            if (distance <= searchRadius) {
                //this point is within the distance threshold to the query object, so add it to the results
                results.add(new SearchResult<ObjectType>(vantagePoint.getObject(), distance));
            }

            if (left != 0 && distance <= nodeRadius + searchRadius) {
                //points within a distance threshold to the query object could be inside the radius,
                //so search the left subtree
                search(left, query, searchRadius, results, distance);
            }

            if (right != 0 && distance >= nodeRadius - searchRadius) {
                //points within a distance threshold to the query object could be outside the radius,
                //so search the right subtree
                search(right, query, searchRadius, results, distance);
            }
        }
        else if (right != 0) {
            search(right, query, searchRadius, results, Double.NaN);
        }
    }

	@Override
	public int getNumberOfDistanceCalculations() {
		return distanceCalculations;
	}

	@Override
	public void resetNumberOfDistanceCalculations() {
		distanceCalculations = 0;
	}

	@Override
	public void close() throws IOException {
		header.getFile().close();
	}
	
	@Override
	public DescriptorFile<ObjectType, DescriptorType> getObjects() {
		return objects;
	}
	
	
	private double getDistance(DescriptorType x, DescriptorType y) {
		distanceCalculations++;
		return metric.getDistance(x, y);
	}
	
	
	private class ObjectPointer {
	    private int objectID;
	    private double distance;
	    private int vantagePointObjectID;

	    public ObjectPointer(int objectID) {
	        this.objectID = objectID;
	        vantagePointObjectID = -1;
	    }

	    public int getObjectID() {
	        return objectID;
	    }

	    public double distance(int vantagePointObjectID, DescriptorType vantagePoint, DescriptorFile<ObjectType, DescriptorType> points) {
	        if (this.vantagePointObjectID != vantagePointObjectID) {
	            this.vantagePointObjectID = vantagePointObjectID;
	            this.distance = getDistance(vantagePoint, points.get(objectID).getDescriptor());
	        }

	        return distance;
	    }
	}
}
