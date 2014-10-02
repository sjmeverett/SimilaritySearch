package metricspaces.indices;

import java.io.IOException;
import java.util.List;

import metricspaces.indices.resultcollectors.ResultCollector;
import metricspaces.indices.resultcollectors.StandardResultCollector;
import metricspaces.metrics.MetricSpaceObject;
import metricspaces.metrics.VantagePointList;
import metricspaces.util.LargeBinaryFile;
import metricspaces.util.Progress;

/**
 * A VP Tree index.
 * @author stewart
 *
 */
public class VPTree extends AbstractIndex implements ResultCollectorIndex {
	private final Progress progress;
	private boolean optimise;
	
	private static final int NODE_SIZE = 28;
	
	/**
	 * Constructor to open an existing file.
	 * @param file
	 * @param progress
	 * @throws IOException
	 */
	public VPTree(LargeBinaryFile file, Progress progress) throws IOException {
		super(file);
		
		this.optimise = true;
		this.progress = progress;
	}
	
	/**
	 * Constructor to create a new file.
	 * @param path The path to the new index file.
	 * @param size The size of the index, or -1 to use the descriptor file size.
	 * @param descriptorPath The path to the descriptor file.
	 * @param metricName The name of the metric to use.
	 * @param progress The progress reporting object.
	 * @throws IOException
	 */
	public VPTree(String path, int size, String descriptorPath, String metricName, Progress progress) throws IOException {
		super(path, AbstractIndex.VP_TREE, size, descriptorPath, metricName);

		this.optimise = true;
		this.progress = progress;
		
		super.resize(dataOffset + super.size * NODE_SIZE);
	}
	
	
	public void setOptimise(boolean value) {
		optimise = value;
	}
	
	
	@Override
	public void build(List<Integer> keys) {
		if (!file.isWritable())
            throw new IllegalStateException("Cannot build index in read-only mode.");

        progress.setOperation("Building index", keys.size());

        //make a list with pointers to all the records
        VantagePointList nodes = space.createVantagePointList(keys);
        createNode(nodes, 0, size - 1, null);
    }


    private int createNode(VantagePointList nodes, int start, int end, MetricSpaceObject parent) {
        int nodeOffset = buffer.position();
        //reserve space for the node
        buffer.position(nodeOffset + NODE_SIZE);

        double radius = 0;
        double distanceToParent = Double.NaN;
        int left = 0;
        int right = 0;

        MetricSpaceObject vantagePoint = nodes.get(start);
        int vantagePointId = vantagePoint.getObjectID();
        int size = end - start + 1;

        if (size > 1) {
            //take the vantage point out the list
            start++;

            //set the radius to be the median distance from the vantage point to all the other points
            int medianIndex = (end - start) / 2 + start;
            SearchResult median = nodes.quickSelect(start, end, medianIndex, vantagePointId);
            radius = median.getDistance();

            //everything less than or equal to the median distance (i.e., inside the circle)
            //goes in the left subtree
            left = createNode(nodes, start, medianIndex, vantagePoint);

            //everything greater than the median distance (i.e., outside the circle) goes in
            //the right subtree (as long as there are points left)
            if (end > medianIndex)
                right = createNode(nodes, medianIndex + 1, end, vantagePoint);
        }


        if (parent != null)
            distanceToParent = parent.getDistance(vantagePointId);

        //save the current buffer position which will be after this nodes entire subtree
        int position = buffer.position();

        //position the buffer at the space we reserved for the node
        buffer.position(nodeOffset);
        buffer.putDouble(radius);
        buffer.putDouble(distanceToParent);
        buffer.putInt(left);
        buffer.putInt(right);
        buffer.putInt(vantagePointId);

        //return the buffer position to after the subtree, which will be the start of the blank space
        buffer.position(position);

        progress.incrementDone();
        return nodeOffset;
    }

	@Override
	public List<SearchResult> search(int queryId, double radius) {
		StandardResultCollector results = new StandardResultCollector(radius);
        search(dataOffset, space.getObject(queryId), results, Double.NaN);

        return results.getResults();
    }
	
	
	@Override
	public List<SearchResult> searchPosition(int position, double radius) {
		int key = getKey(position);
		
		StandardResultCollector results = new StandardResultCollector(radius);
        search(dataOffset, space.getObject(key), results, Double.NaN);
        
        return results.getResults();
	}
	
	
	@Override
	public int getKey(int position) {
		//TODO: this is wrong
		buffer.position(dataOffset + position * NODE_SIZE + 24);
		return buffer.getInt();
	}
	
	@Override
	public void search(int queryId, ResultCollector collector) {
		search(dataOffset, space.getObject(queryId), collector, Double.NaN);
	}
	
	@Override
	public void searchPosition(int position, ResultCollector collector) {
		int key = getKey(position);
		search(dataOffset, space.getObject(key), collector, Double.NaN);
	}


    private void search(int offset, MetricSpaceObject query, ResultCollector collector, double parentToQueryDistance) {
        //move the buffer position to the start of the node
        buffer.position(offset);

        //the node should consist of a pointer to each child node then the point data, then the value data
        double nodeRadius = buffer.getDouble();
        double parentToThisDistance = buffer.getDouble();
        int left = buffer.getInt();
        int right = buffer.getInt();
        
        double searchRadius = collector.getRadius();

        //check if we can skip a distance calculation using the triangle inequality
        if (!optimise || Double.isNaN(parentToQueryDistance)
            || Math.abs(parentToThisDistance - parentToQueryDistance) <= nodeRadius + searchRadius)
        {
            int vantagePointId = buffer.getInt();
            double distance = query.getDistance(vantagePointId);

            if (distance <= searchRadius) {
                //this point is within the distance threshold to the query object, so add it to the results
                collector.add(new SearchResult(query.getObjectID(), vantagePointId, distance));
                
                //update the search radius in case the add changed it
                searchRadius = collector.getRadius();
            }

            if (left != 0 && distance <= nodeRadius + searchRadius) {
                //points within a distance threshold to the query object could be inside the radius,
                //so search the left subtree
                search(left, query, collector, distance);
            }

            if (right != 0 && distance >= nodeRadius - searchRadius) {
                //points within a distance threshold to the query object could be outside the radius,
                //so search the right subtree
                search(right, query, collector, distance);
            }
        }
        else if (right != 0) {
            search(right, query, collector, Double.NaN);
        }
    }
}
