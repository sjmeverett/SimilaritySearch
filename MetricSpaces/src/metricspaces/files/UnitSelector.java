package metricspaces.files;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.metrics.Metric;

public class UnitSelector implements ReferencePointSelector {

	@Override
	public Descriptor[] select(int count, DescriptorFile objects, Metric metric, Progress progress) {
		Descriptor[] referencePoints = new Descriptor[count];
		int dim = objects.getDimensions();
		
		if (count > dim)
			throw new IllegalArgumentException("count > dim");
		
		for (int i = 0; i < count; i++) {
			double[] data = new double[dim];
			data[i] = 1;
			referencePoints[i] = new DoubleDescriptor(data);
		}
		
		return referencePoints;
	}

}
