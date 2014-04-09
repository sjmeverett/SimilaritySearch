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

public class PivotedList implements Index {
	private final IndexFileHeader header;
	private final ByteBuffer buffer;
	private final int capacity, numberOfPivots, itemsOffset, itemSize;
	private final DescriptorFile objects;
	private final Descriptor[] pivots;
	private final Metric metric;
	private final Progress progress;
	
	private int distanceCalculations;
	
	
	public PivotedList(IndexFileHeader header, DescriptorFile objects, Metric metric, Progress progress) {
		this.header = header;
		this.objects = objects;
		this.metric = metric;
		this.progress = progress;
		capacity = header.getCapacity();
		
		buffer = header.getBuffer();
		numberOfPivots = buffer.getInt();
		pivots = new Descriptor[numberOfPivots];
		
		for (int i = 0; i < numberOfPivots; i++) {
			int pivot = buffer.getInt();
			pivots[i] = objects.get(pivot);
		}
		
		itemsOffset = buffer.position();
		itemSize = numberOfPivots * 8 + 4;
	}
	
	
	public PivotedList(IndexFileHeader header, DescriptorFile objects, Metric metric, int numberOfPivots, Progress progress)
			throws IOException {
		
		if (!header.isWritable())
			throw new IllegalArgumentException("header must be writable for this constructor");
		
		this.header = header;
		this.objects = objects;
		this.metric = metric;
		this.progress = progress;
		this.numberOfPivots = numberOfPivots;
		capacity = header.getCapacity();
		
		ByteBuffer b = header.getBuffer();
		b.putInt(numberOfPivots);
		itemsOffset = b.position() + numberOfPivots * 4;
		itemSize = numberOfPivots * 8 + 4;
		
		header.resize(itemsOffset + capacity * itemSize);
		
		buffer = header.getBuffer();
		pivots = new Descriptor[numberOfPivots];
		
		for (int i = 0; i < numberOfPivots; i++) {
			int pivot = RandomHelper.getNextInt(0, capacity - 1);
			pivots[i] = objects.get(pivot);
			buffer.putInt(pivot);
		}	
	}
	

	@Override
	public void build(List<Integer> keys) {
		progress.setOperation("Building index", keys.size());
		buffer.position(itemsOffset);
		
		for (Integer i: keys) {
			buffer.putInt(i);
			Descriptor descriptor = objects.get(i);
			double[] item = getDistanceList(descriptor);
			
			for (double d: item) {
				buffer.putDouble(d);
			}
			
			progress.incrementDone();
		}
	}
	

	@Override
	public List<SearchResult> search(Descriptor query, double radius) {
		return search(query, getDistanceList(query), radius);
	}
	
	
	@Override
	public List<SearchResult> search(int position, double radius) {
		double[] list = new double[numberOfPivots];
		
		buffer.position(itemsOffset + itemSize * position);
		int key = buffer.getInt();
		
		for (int i = 0; i < numberOfPivots; i++) {
			list[i] = buffer.getDouble();
		}
		
		return search(objects.get(key), list, radius);
	}
	
	
	private List<SearchResult> search(Descriptor queryDescriptor, double[] queryList, double radius) {
		List<SearchResult> results = new ArrayList<>();
		
		for (int i = 0; i < capacity; i++) {
			SearchResult result = getDistance(queryDescriptor, queryList, i, radius);
			
			if (result != null) {
				results.add(result);
			}
		}
		
		return results;
	}

	
	private double[] getDistanceList(Descriptor descriptor) {
		double[] list = new double[numberOfPivots];
		
		for (int i = 0; i < numberOfPivots; i++) {
            list[i] = distance(descriptor, pivots[i]);
        }
		
		return list;
	}
	
	
	private SearchResult getDistance(Descriptor queryDescriptor, double[] queryList, int entryIndex, double radius) {
		//reading directly from the buffer is much faster than using getDistanceList here...
		buffer.position(itemsOffset + itemSize * entryIndex);
		int key = buffer.getInt();
		
		//check if we're able to rule out a distance calculation
		for (int i = 0; i < queryList.length; i++) {
			if (Math.abs(queryList[i] - buffer.getDouble()) > radius) {
				return null;
			}
		}
		
		//haven't been able to rule it out, better do a calculation
		Descriptor descriptor = objects.get(key);
        double distance = distance(queryDescriptor, descriptor);
        
        if (distance < radius)
        	return new SearchResult(key, distance);
        else
        	return null;
	}
	

	@Override
	public int getNumberOfDistanceCalculations() {
		return distanceCalculations;
	}

	@Override
	public void resetNumberOfDistanceCalculations() {
		distanceCalculations = 0;
	}
	
	private double distance(Descriptor x, Descriptor y) {
		distanceCalculations++;
		return metric.getDistance(x, y);
	}

	@Override
	public void close() throws IOException {
		header.getFile().close();
	}

	@Override
	public DescriptorFile getObjects() {
		return objects;
	}
	
	@Override
	public IndexFileHeader getHeader() {
		return header;
	}
}
