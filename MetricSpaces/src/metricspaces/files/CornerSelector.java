package metricspaces.files;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.metrics.Metric;

public class CornerSelector implements ReferencePointSelector {

	@Override
	public Descriptor[] select(int count, DescriptorFile objects, Metric metric, Progress progress) {
		Descriptor[] points = new Descriptor[count];
		int dim = objects.getDimensions();
		
		for (int i = 0; i < count; i++) {
			double[] point = new double[dim];
			
			for (int j = 1; j < i; j++) {
				point[j] = 1;
			}
			
			points[i] = new DoubleDescriptor(point);
		}
		
		return points;
	}

}
