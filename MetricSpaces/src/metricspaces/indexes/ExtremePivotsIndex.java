package metricspaces.indexes;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import metricspaces.Progress;
import metricspaces.RandomHelper;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.ObjectWithDescriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.metrics.Metric;


public class ExtremePivotsIndex<ObjectType, DescriptorType extends Descriptor> implements Index<ObjectType, DescriptorType> {
	private final Class<DescriptorType> descriptorClass;
	private final IndexFileHeader header;
	private final DescriptorFile<ObjectType, DescriptorType> objects;
	private final Metric<DescriptorType> metric;
	private final ByteBuffer buffer;
	private final int dataOffset, numberOfPivots, numberOfGroups, totalPivots, tableOffset, numberOfObjects, recordSize;
	private final double mu;
	private final Progress progress;
	private final DescriptorType[] pivots;
	private int distanceCalculations;
	
	
	public ExtremePivotsIndex(Class<DescriptorType> descriptorClass, IndexFileHeader header, DescriptorFile<ObjectType, DescriptorType> objects,
			Metric<DescriptorType> metric, Progress progress) {
		
		this.descriptorClass = descriptorClass;
		this.header = header;
		this.objects = objects;
		this.metric = metric;
		this.progress = progress;
		this.numberOfObjects = objects.getCapacity();
		
		buffer = header.getBuffer();
		numberOfPivots = buffer.getInt();
		numberOfGroups = buffer.getInt();
		totalPivots = numberOfPivots * numberOfGroups;
		mu = buffer.getDouble();
		dataOffset = buffer.position();
		tableOffset = dataOffset + totalPivots * 4;
		recordSize = numberOfGroups * 12;
		
		pivots = loadPivots();
	}
	
	
	public ExtremePivotsIndex(Class<DescriptorType> descriptorClass, IndexFileHeader header, DescriptorFile<ObjectType, DescriptorType> objects, Metric<DescriptorType> metric,
			int numberOfPivots, int numberOfGroups, double mu, Progress progress) throws IOException {
		
		if (!header.isWritable())
			throw new IllegalArgumentException("header must be writable for this constructor");
		
		this.descriptorClass = descriptorClass;
		this.header = header;
		this.objects = objects;
		this.metric = metric;
		this.progress = progress;
		this.numberOfObjects = objects.getCapacity();
		
		this.numberOfPivots = numberOfPivots;
		this.numberOfGroups = numberOfGroups;
		this.mu = mu;
		totalPivots = numberOfPivots * numberOfGroups;
		recordSize = numberOfGroups * 12;
		
		ByteBuffer b = header.getBuffer();
		b.putInt(numberOfPivots);
		b.putInt(numberOfGroups);
		b.putDouble(mu);
		dataOffset = b.position();
		tableOffset = dataOffset + totalPivots * 4;
		header.resize(tableOffset + numberOfObjects * recordSize);
		
		buffer = header.getBuffer();
		pivots = initialisePivots();
	}
	
	
	@Override
    public void build() {
        if (!header.isWritable())
            throw new IllegalStateException("Cannot build index in read-only mode.");

        //build the pivot table
        progress.setOperation("Building index", numberOfObjects);
        buffer.position(tableOffset);

        for (int i = 0; i < numberOfObjects; i++) {
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
        DescriptorType descriptor = objects.get(objectIndex).getDescriptor();

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

    
    private DescriptorType[] loadPivots() {
		@SuppressWarnings("unchecked")
        DescriptorType[] pivots = (DescriptorType[])Array.newInstance(descriptorClass, totalPivots);
        progress.setOperation("Loading pivots", totalPivots);

        for (int i = 0; i < totalPivots; i++) {
            int pivotObjectID = buffer.getInt();
            pivots[i] = objects.get(pivotObjectID).getDescriptor();
            progress.incrementDone();
        }
        
        return pivots;
    }
    
    
	private DescriptorType[] initialisePivots() {
		@SuppressWarnings("unchecked")
		DescriptorType[] pivots = (DescriptorType[])Array.newInstance(descriptorClass, totalPivots);
        progress.setOperation("Selecting pivots", totalPivots);

        for (int i = 0; i < totalPivots; i++) {
            int pivotObjectID = RandomHelper.getNextInt(0, numberOfObjects - 1);
            pivots[i] = objects.get(pivotObjectID).getDescriptor();
            buffer.putInt(pivotObjectID);
            progress.incrementDone();
        }
        
        return pivots;
    }


    @Override
    public List<SearchResult<ObjectType>> search(DescriptorType query, double radius) {
        //first calculate the distance between all the pivots and the query
        double[] queryTable = buildQueryTable(query);

        //now work through the table finding results
        List<SearchResult<ObjectType>> results = new ArrayList<SearchResult<ObjectType>>();
        int position = tableOffset;
        buffer.position(position);

        for (int i = 0; i < numberOfObjects; i++) {
            if (!checkIfObjectCanBeExcluded(queryTable, radius)) {
                //we haven't been able to rule the object out, meaning we have to do a distance calculation
                ObjectWithDescriptor<ObjectType, DescriptorType> object = objects.get(i);
                double distance = getDistance(query, object.getDescriptor());

                if (distance <= radius)
                    results.add(new SearchResult<ObjectType>(object.getObject(), distance, i));
            }

            position += recordSize;
            buffer.position(position);
        }

        return results;
    }


    private double[] buildQueryTable(DescriptorType query) {
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
	public DescriptorFile<ObjectType, DescriptorType> getObjects() {
		return objects;
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
	
	
    private double getDistance(DescriptorType x, DescriptorType y) {
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
