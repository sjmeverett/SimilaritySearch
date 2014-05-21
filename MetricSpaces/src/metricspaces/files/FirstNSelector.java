package metricspaces.files;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.metrics.Metric;

public class FirstNSelector implements ReferencePointSelector {

	@Override
	public Descriptor[] select(int count, DescriptorFile objects, Metric metric, Progress progress) {
		Descriptor[] referencePoints = new Descriptor[count];
		
		for (int i = 0; i < count; i++) {
			referencePoints[i] = objects.get(i);
		}
		
		return referencePoints;
	}

}
