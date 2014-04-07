package metricspaces.indexes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import metricspaces.Progress;
import metricspaces.RandomHelper;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.ObjectWithDescriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.metrics.Metric;

public class PivotedList<ObjectType, DescriptorType extends Descriptor> implements Index<ObjectType, DescriptorType> {
	private final IndexFileHeader header;
	private final ByteBuffer buffer;
	private final int numberOfPivots, numberOfObjects, itemsOffset, itemSize;
	private final DescriptorFile<ObjectType, DescriptorType> objects;
	private final List<DescriptorType> pivots;
	private final Metric<DescriptorType> metric;
	private final Progress progress;
	
	private int distanceCalculations;
	
	
	public PivotedList(IndexFileHeader header, DescriptorFile<ObjectType, DescriptorType> objects,
			Metric<DescriptorType> metric, Progress progress) {
		
		this.header = header;
		this.objects = objects;
		this.metric = metric;
		this.progress = progress;
		numberOfObjects = objects.getCapacity();
		
		buffer = header.getBuffer();
		numberOfPivots = buffer.getInt();
		pivots = new ArrayList<>();
		
		for (int i = 0; i < numberOfPivots; i++) {
			int pivot = buffer.getInt();
			pivots.add(objects.get(pivot).getDescriptor());
		}
		
		itemsOffset = buffer.position();
		itemSize = numberOfPivots * 8;
	}
	
	
	public PivotedList(IndexFileHeader header, DescriptorFile<ObjectType, DescriptorType> objects,
			Metric<DescriptorType> metric, int numberOfPivots, Progress progress) throws IOException {
		
		if (!header.isWritable())
			throw new IllegalArgumentException("header must be writable for this constructor");
		
		this.header = header;
		this.objects = objects;
		this.metric = metric;
		this.progress = progress;
		this.numberOfPivots = numberOfPivots;
		numberOfObjects = objects.getCapacity();
		
		ByteBuffer b = header.getBuffer();
		b.putInt(numberOfPivots);
		itemsOffset = b.position() + numberOfPivots * 4;
		itemSize = numberOfPivots * 8;
		
		header.resize(itemsOffset + numberOfObjects * itemSize);
		
		buffer = header.getBuffer();
		pivots = new ArrayList<>();
		
		for (int i = 0; i < numberOfPivots; i++) {
			int pivot = RandomHelper.getNextInt(0, numberOfObjects - 1);
			pivots.add(objects.get(pivot).getDescriptor());
			buffer.putInt(pivot);
		}	
	}
	

	@Override
	public void build() {
		progress.setOperation("Building index", numberOfObjects);
		buffer.position(itemsOffset);
		
		for (int i = 0; i < numberOfObjects; i++) {
			DescriptorType descriptor = objects.get(i).getDescriptor();
			double[] item = getDistanceList(descriptor);
			
			for (double d: item) {
				buffer.putDouble(d);
			}
			
			progress.incrementDone();
		}
	}
	

	@Override
	public List<SearchResult<ObjectType>> search(DescriptorType query, double radius) {
		return search(query, getDistanceList(query), radius);
	}
	
	
	public List<SearchResult<ObjectType>> search(DescriptorType queryDescriptor, double[] queryList, double radius) {
		List<SearchResult<ObjectType>> results = new ArrayList<>();
		
		for (int i = 0; i < numberOfObjects; i++) {
			SearchResult<ObjectType> result = getDistance(queryDescriptor, queryList, i, radius);
			
			if (result != null) {
				results.add(result);
			}
		}
		
		return results;
	}
	
	
	public double[] getDistanceList(int index) {
		double[] table = new double[numberOfPivots];
		
		buffer.position(itemsOffset + itemSize * index);
		
		for (int i = 0; i < numberOfPivots; i++) {
			table[i] = buffer.getDouble();
		}
		
		return table;
	}
	
	
	private double[] getDistanceList(DescriptorType descriptor) {
		double[] table = new double[numberOfPivots];
		
		for (int i = 0; i < numberOfPivots; i++) {
            table[i] = distance(descriptor, pivots.get(i));
        }
		
		return table;
	}
	
	
	private SearchResult<ObjectType> getDistance(DescriptorType queryDescriptor, double[] queryList, int entryIndex, double radius) {
		//reading directly from the buffer is much faster than using getDistanceList here...
		buffer.position(itemsOffset + itemSize * entryIndex);
		
		//check if we're able to rule out a distance calculation
		for (int i = 0; i < queryList.length; i++) {
			if (Math.abs(queryList[i] - buffer.getDouble()) > radius) {
				return null;
			}
		}
		
		//haven't been able to rule it out, better do a calculation
		ObjectWithDescriptor<ObjectType, DescriptorType> obj = objects.get(entryIndex);
        double distance = distance(queryDescriptor, obj.getDescriptor());
        
        if (distance < radius)
        	return new SearchResult<ObjectType>(obj.getObject(), distance, entryIndex);
        else
        	return null;
	}
	
	
	public SearchResult<ObjectType> findNearestNeighbour(DescriptorType queryDescriptor, double[] queryList,
			int queryIndex, double radius) {
		
    	double min = Double.POSITIVE_INFINITY;
		SearchResult<ObjectType> closest = null;
		
		for (int i = 0; i < numberOfObjects; i++) {
			//skip cases where the query is the current entry
			if (i == queryIndex)
				continue;

			SearchResult<ObjectType> result = getDistance(queryDescriptor, queryList, i, radius);
			
			if (result != null && result.getDistance() < min) {
				min = result.getDistance();
				closest = result;
			}
		}
		
		return closest;
    }
	

	@Override
	public int getNumberOfDistanceCalculations() {
		return distanceCalculations;
	}

	@Override
	public void resetNumberOfDistanceCalculations() {
		distanceCalculations = 0;
	}
	
	private double distance(DescriptorType x, DescriptorType y) {
		distanceCalculations++;
		return metric.getDistance(x, y);
	}

	@Override
	public void close() throws IOException {
		header.getFile().close();
	}

	@Override
	public DescriptorFile<ObjectType, DescriptorType> getObjects() {
		return objects;
	}
}
