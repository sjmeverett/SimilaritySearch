package metricspaces.update.relative;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.channels.FileChannel.MapMode;

import metricspaces.Progress;
import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.update._double.DoubleDescriptorFormat;
import metricspaces.update._double.DoubleMetricSpace;
import metricspaces.update.common.AbstractDescriptorFile;
import metricspaces.update.common.DescriptorFile;
import metricspaces.update.common.DescriptorFileFactory;
import metricspaces.update.common.DescriptorFormat;
import metricspaces.update.common.LargeBinaryFile;
import metricspaces.update.common.MetricSpace;
import metricspaces.update.common.MetricSpaceFormat;
import metricspaces.update.common.MetricSpaceObject;
import metricspaces.update.common.ObjectSelector;


/**
 * Represents a file of descriptors which are composed of the distances between each point and a set of reference points,
 * also stored in the file.
 * @author stewart
 *
 */
public class RelativeDescriptorFile extends AbstractDescriptorFile<DoubleDescriptor> {
	private MetricSpace originalSpace;
	private MetricSpaceFormat referenceFormat;
	private MetricSpaceObject[] referencePoints;
	private DoubleDescriptorFormat format;
	private DescriptorFile originalDescriptors;
	
	
	public RelativeDescriptorFile(LargeBinaryFile file) throws IOException {
		super(file);
		
		//read header info
		String originalDescriptorsPath = file.getString();
		String metricName = file.getString();
		dataOffset = file.getBuffer().position();
		
		//set up the original space
		originalDescriptors = DescriptorFileFactory.open(originalDescriptorsPath, false);
		originalSpace = originalDescriptors.getMetricSpace(metricName);
		
		//read the reference points
		int referenceSize = (int)file.channel.size() - dataOffset;
		ByteBuffer referenceBuffer = file.channel.map(MapMode.READ_ONLY, dataOffset, referenceSize);
		referenceFormat = originalSpace.getFormat(referenceBuffer, dimensions);
		referencePoints = new MetricSpaceObject[dimensions];
		
		for (int i = 0; i < dimensions; i++) {
			referencePoints[i] = referenceFormat.get(i);
		}
		
		//setup the buffer for reading the relative points
		DoubleBuffer doubleBuffer = file.channel.map(MapMode.READ_ONLY, dataOffset + referenceBuffer.position(),
			super.size * super.dimensions * 8).asDoubleBuffer();
		
		format = new DoubleDescriptorFormat(doubleBuffer, super.dimensions, super.size);
	}
	
	
	/**
	 * Constructor for creating a new file.
	 * @param file The LargeBinaryFile object representing the file to create.
	 * @param originalDescriptorsPath The path to the file containing the descriptors to make relative descriptors from.
	 * @param metricName The name of the metric to use.
	 * @param selectorName The name of the selector to use when selecting reference points.
	 * @param dimensions The number of reference points to use.
	 * @throws IOException
	 */
	public RelativeDescriptorFile(LargeBinaryFile file, String originalDescriptorsPath, String metricName, String selectorName, int dimensions) throws IOException {
		super(file);
		
		if (!file.isWritable())
			throw new IllegalArgumentException("file must be writable");
		
		file.putString(originalDescriptorsPath);
		file.putString(metricName);
		dataOffset = file.getBuffer().position();
		
		writeHeader(RELATIVE_TYPE, originalDescriptors.getSize(), dimensions, originalDescriptors.getDescriptorName() + "Relative" + dimensions);
		
		originalDescriptors = DescriptorFileFactory.open(originalDescriptorsPath, false);
		originalSpace = originalDescriptors.getMetricSpace(metricName);
		
		//write the reference points
		int referenceSize = (int)file.channel.size() - dataOffset;
		ByteBuffer referenceBuffer = file.channel.map(MapMode.READ_ONLY, dataOffset, referenceSize);
		referenceFormat = originalSpace.getFormat(referenceBuffer, dimensions);
		referencePoints = new MetricSpaceObject[dimensions];
		
		ObjectSelector selector = originalSpace.getObjectSelector(selectorName);
		selector.setOutputBuffer(buffer, referenceSize);
		
		for (int i = 0; i < dimensions; i++) {
			referencePoints[i] = selector.next();
		}
		
		//setup the buffer for writing
		DoubleBuffer doubleBuffer = file.channel.map(MapMode.READ_WRITE, dataOffset + referenceBuffer.position(),
				super.size * super.dimensions * 8).asDoubleBuffer();
			
		format = new DoubleDescriptorFormat(doubleBuffer, super.dimensions, super.size);
	}
	
	
	/**
	 * Creates a descriptor composed of the distances between the given descriptor and the set of reference points.
	 * @param original
	 * @return
	 */
	public DoubleDescriptor getRelativeDescriptor(Object original) {
		double[] data = new double[dimensions];
		
		for (int i = 0; i < dimensions; i++) {
			data[i] = originalSpace.getDistance(original, referencePoints[i]);
		}
		
		return new DoubleDescriptor(data);
	}
	
	/**
	 * Creates a descriptor composed of the distances between the given descriptor and the set of reference points.
	 * @param originalId
	 * @return
	 */
	public DoubleDescriptor getRelativeDescriptor(int originalId) {
		double[] data = new double[dimensions];
		
		for (int i = 0; i < dimensions; i++) {
			//this is reasonably efficient because the original object is cached among the reference points
			data[i] = referencePoints[i].getDistance(originalId);
		}
		
		return new DoubleDescriptor(data);
	}
	
	
	/**
	 * Gets a relative descriptor for the specified original object and writes it out to the file.
	 * @param originalId
	 */
	public void writeRelativeDescriptor(int id) {
		format.put(id, getRelativeDescriptor(id));
	}
	
	
	/**
	 * Creates relative descriptors for all the descriptors in the original file and copies them into this file.
	 * @param progress
	 */
	public void copy(Progress progress) {
		int size = originalDescriptors.getSize();
		
		progress.setOperation("copying", size);
		
		for (int i = 0; i < size; i++) {
			writeRelativeDescriptor(i);
			progress.incrementDone();
		}
	}

	@Override
	public MetricSpace getMetricSpace(String metricName) {
		return new DoubleMetricSpace(this, metricName);
	}

	@Override
	public DescriptorFormat<DoubleDescriptor> getFormat() {
		return format;
	}

	@Override
	public DescriptorFormat<DoubleDescriptor> getFormat(ByteBuffer buffer, int size) {
		return new DoubleDescriptorFormat(buffer.asDoubleBuffer(), dimensions, size);
	}

	
	public MetricSpace getOriginalSpace() {
		return originalSpace;
	}
	
	
	public DescriptorFile getOriginalDescriptors() {
		return originalDescriptors;
	}
}
