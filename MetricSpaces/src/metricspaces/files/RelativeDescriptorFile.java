package metricspaces.files;

import java.io.IOException;

import metricspaces.Progress;
import metricspaces.RelativePath;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.metrics.Metric;
import metricspaces.metrics.Metrics;


public class RelativeDescriptorFile extends DoubleDescriptorFile {
	private final DescriptorFile originalDescriptors;
	private final Metric metric;
	private final Descriptor[] referencePoints;
	private int distanceCalculations;
	
	
	public RelativeDescriptorFile(DescriptorFileHeader header) throws IOException {
		super(header);
		
		String originalDescriptorsPath = header.getString();
		originalDescriptors = DescriptorFileHeader.open(originalDescriptorsPath, header.getPath());
		
		String metricName = header.getString();
		metric = Metrics.getMetric(metricName);
		
		referencePoints = new Descriptor[dimensions];
		int dim = originalDescriptors.getDimensions();
		
		for (int i = 0; i < dimensions; i++) {
			double[] data = new double[dim];
			
			for (int j = 0; j < dim; j++) {
				data[j] = buffer.getDouble();
			}
			
			referencePoints[i] = new DoubleDescriptor(data);
		}
		
		dataOffset = buffer.position();
	}
	
	
	public RelativeDescriptorFile(DescriptorFileHeader header, DescriptorFile originalDescriptors, Metric metric,
			ReferencePointSelector referencePointSelector, Progress progress) throws IOException {
		
		super(header, false);
		
		this.originalDescriptors = originalDescriptors;
		this.metric = metric;
		
		RelativePath r = new RelativePath(originalDescriptors.getHeader().getPath());
		header.putString(r.getRelativeTo(header.getPath()));
		
		header.putString(metric.getName());
		
		header.resize(buffer.position() + dimensions * originalDescriptors.getDimensions() * 8 + recordSize * capacity);
		buffer = header.getBuffer();
		
		referencePoints = referencePointSelector.select(dimensions, originalDescriptors, metric, progress);
		
		for (int i = 0; i < referencePoints.length; i++) {
			double[] data = referencePoints[i].getData();
			
			for (int j = 0; j < data.length; j++) {
				buffer.putDouble(data[j]);
			}
		}
		
		dataOffset = buffer.position();
	}

	
	public Descriptor getRelativeDescriptor(Descriptor original) {
		double[] data = new double[referencePoints.length];
		
		for (int i = 0; i < data.length; i++) {
			data[i] = getDistance(original, referencePoints[i]);
		}
		
		return new DoubleDescriptor(data);
	}
	
	
	public void putOriginal(Descriptor original) {
		super.put(getRelativeDescriptor(original));
	}
	
	
	public void copy(Progress progress) {
		progress.setOperation("Copying", originalDescriptors.getCapacity());
		
		for (int i = 0; i < originalDescriptors.getCapacity(); i++) {
			putOriginal(originalDescriptors.get(i));
			progress.incrementDone();
		}
	}
	
	
	public int getNumberOfDistanceCalculations() {
		return distanceCalculations;
	}
	
	
	public void resetNumberOfDistanceCalculations() {
		distanceCalculations = 0;
	}
	
	
	public double getDistance(Descriptor x, int id) {
		Descriptor y = originalDescriptors.get(id);
		return getDistance(x, y);
	}
	
	
	private double getDistance(Descriptor x, Descriptor y) {
		distanceCalculations++;
		return metric.getDistance(x, y);
	}
}
