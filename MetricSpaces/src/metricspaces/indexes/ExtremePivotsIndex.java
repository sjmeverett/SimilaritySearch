package metricspaces.indexes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.pivotselectors.PivotSelector;
import metricspaces.metrics.Metric;


public class ExtremePivotsIndex implements Index {
	private final IndexFileHeader header;
	private final DescriptorFile objects;
	private final Metric metric;
	private final ByteBuffer buffer;
	private final int dataOffset, capacity, numberOfPivots, numberOfGroups, totalPivots, tableOffset, recordSize;
	private final double mu;
	private final Progress progress;
	private final Descriptor[] pivots;
	private int distanceCalculations;
	
	
	public ExtremePivotsIndex(IndexFileHeader header, DescriptorFile objects, Metric metric, Progress progress) {
		this.header = header;
		this.objects = objects;
		this.metric = metric;
		this.progress = progress;
		capacity = header.getCapacity();
		
		buffer = header.getBuffer();
		numberOfPivots = buffer.getInt();
		numberOfGroups = buffer.getInt();
		totalPivots = numberOfPivots * numberOfGroups;
		mu = buffer.getDouble();
		dataOffset = buffer.position();
		tableOffset = dataOffset + totalPivots * 4;
		recordSize = numberOfGroups * 12 + 4;
		
		pivots = loadPivots();
	}
	
	
	public ExtremePivotsIndex(IndexFileHeader header, DescriptorFile objects, Metric metric,
			int numberOfPivots, int numberOfGroups, double mu, PivotSelector pivotSelector,
			Progress progress) throws IOException {
		
		if (!header.isWritable())
			throw new IllegalArgumentException("header must be writable for this constructor");
		
		this.header = header;
		this.objects = objects;
		this.metric = metric;
		this.progress = progress;
		capacity = header.getCapacity();
		
		this.numberOfPivots = numberOfPivots;
		this.numberOfGroups = numberOfGroups;
		this.mu = mu;
		totalPivots = numberOfPivots * numberOfGroups;
		recordSize = numberOfGroups * 12 + 4;
		
		ByteBuffer b = header.getBuffer();
		b.putInt(numberOfPivots);
		b.putInt(numberOfGroups);
		b.putDouble(mu);
		dataOffset = b.position();
		tableOffset = dataOffset + totalPivots * 4;
		header.resize(tableOffset + capacity * recordSize);
		
		buffer = header.getBuffer();
		pivots = initialisePivots(pivotSelector);
	}
	
	
	@Override
    public void build(List<Integer> keys) {
        if (!header.isWritable())
            throw new IllegalStateException("Cannot build index in read-only mode.");
        
        if (keys.size() > capacity)
        	throw new IllegalArgumentException("keys size is bigger than index capacity");

        //build the pivot table
        progress.setOperation("Building index", keys.size());
        buffer.position(tableOffset);

        for (Integer i: keys) {
        	buffer.putInt(i);
        	
            for (int j = 0; j < numberOfGroups; j++) {
                PivotEntry entry = getPivotWithMaxAlpha(i, j);
                buffer.putInt(entry.getPivotIndex());
                buffer.putDouble(entry.getDistance());
            }

            progress.incrementDone();
        }
    }


    private PivotEntry getPivotWithMaxAlpha(int objectIndex, int groupIndex) {
        int index = groupIndex * numberOfPivots;
        Descriptor descriptor = objects.get(objectIndex);

        //store details about the first pivot
        int maxPivotIndex = index;
        double distance = getDistance(descriptor, pivots[index++]);
        double max = Math.abs(distance - mu);

        //compare the rest of the pivots in the group to see if they're closer
        for (int i = 1; i < numberOfPivots; i++) {
            double d = getDistance(descriptor, pivots[index]);
            double alpha = Math.abs(d - mu);

            if (alpha > max) {
                maxPivotIndex = index;
                distance = d;
                max = alpha;
            }

            index++;
        }

        //return the details of the pivot found at the maximum distance
        return new PivotEntry(maxPivotIndex, distance);
    }

    
    private Descriptor[] loadPivots() {
        Descriptor[] pivots = new Descriptor[totalPivots];
        progress.setOperation("Loading pivots", totalPivots);

        for (int i = 0; i < totalPivots; i++) {
            int pivotObjectID = buffer.getInt();
            pivots[i] = objects.get(pivotObjectID);
            progress.incrementDone();
        }
        
        return pivots;
    }
    
    
	private Descriptor[] initialisePivots(PivotSelector pivotSelector) {
		Descriptor[] pivots = new Descriptor[totalPivots];
		Iterable<Integer> pivotIds = pivotSelector.select(totalPivots, objects, metric, progress);
		
        progress.setOperation("Reading pivots", totalPivots);
        int i = 0;
        
        for (int id: pivotIds) {
        	pivots[i++] = objects.get(id);
        	buffer.putInt(id);
        	progress.incrementDone();
        }
        
        return pivots;
    }


    @Override
    public List<SearchResult> search(Descriptor query, double radius) {
        return search(query, null, buildQueryTable(query), radius);
    }
    
    
    private List<SearchResult> search(Descriptor query, Integer queryKey, double[] queryTable, double radius) {
    	//work through the table finding results
        List<SearchResult> results = new ArrayList<SearchResult>();
        int position = tableOffset;
        buffer.position(position);

        for (int i = 0; i < capacity; i++) {
        	int key = buffer.getInt();
        	
            if (!checkIfObjectCanBeExcluded(queryTable, radius)) {
                //we haven't been able to rule the object out, meaning we have to do a distance calculation
                Descriptor descriptor = objects.get(key);
                double distance = getDistance(query, descriptor);

                if (distance <= radius)
                    results.add(new SearchResult(queryKey, key, distance));
            }

            position += recordSize;
            buffer.position(position);
        }

        return results;
    }
    
    
    @Override
    public List<SearchResult> search(int position, double radius) {
    	double[] queryTable = new double[totalPivots];
    	int key = getKey(position);
    	Descriptor query = objects.get(key);
    	
    	//fill out the distances we know already
    	for (int i = 0; i < numberOfGroups; i++) {
    		int pivotIndex = buffer.getInt();
            double distance = buffer.getDouble();
            queryTable[pivotIndex] = distance;
    	}
    	
    	//fill out the rest
    	//if m=1, the last step will have filled out all of them already
    	if (numberOfPivots > 1) {
	    	for (int i = 0; i < totalPivots; i++) {
	    		if (queryTable[i] == 0)
	    			queryTable[i] = getDistance(query, pivots[i]);
	    	}
    	}
    	
    	return search(query, key, queryTable, radius);
    }
    
    
    /**
     * Get the pivot indices used by the object at the specified position.
     * @param position
     * @return
     */
    public int[] getPivots(int position) {
    	int[] pivots = new int[numberOfGroups];
    	//move the buffer to the right position
    	getKey(position);
    	
    	for (int i = 0; i < pivots.length; i++) {
    		pivots[i] = buffer.getInt();
    		//toss the distance - don't need it
    		buffer.getDouble();
    	}
    	
    	return pivots;
    }
    
    
    @Override
    public int getKey(int position) {
    	buffer.position(tableOffset + position * recordSize);
    	return buffer.getInt();
    }


    private double[] buildQueryTable(Descriptor query) {
        double[] queryTable = new double[totalPivots];

        for (int i = 0; i < totalPivots; i++) {
            queryTable[i] = getDistance(query, pivots[i]);
        }

        return queryTable;
    }


    private boolean checkIfObjectCanBeExcluded(double[] queryTable, double radius) {
        for (int j = 0; j < numberOfGroups; j++) {
            int pivotIndex = buffer.getInt();
            double distance = buffer.getDouble();

            if (Math.abs(distance - queryTable[pivotIndex]) > radius) {
                return true;
            }
        }

        return false;
    }


    @Override
    public void close() throws IOException {
        header.getFile().close();
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
	public DescriptorFile getObjects() {
		return objects;
	}


	@Override
	public IndexFileHeader getHeader() {
		return header;
	}
    
	
	public int getNumberOfPivots() {
		return numberOfPivots;
	}
	
	public int getNumberOfGroups() {
		return numberOfGroups;
	}
	
	public double getMu() {
		return mu;
	}
	
	
	public Descriptor[] getPivots() {
		return pivots;
	}
	
	
    private double getDistance(Descriptor x, Descriptor y) {
    	distanceCalculations++;
    	return metric.getDistance(x, y);
    }
    
    
    private class PivotEntry {
        private int pivotIndex;
        private double distance;

        public PivotEntry(int pivotIndex, double distance) {
            this.pivotIndex = pivotIndex;
            this.distance = distance;
        }

        public int getPivotIndex() {
            return pivotIndex;
        }

        public double getDistance() {
            return distance;
        }
    }
}
