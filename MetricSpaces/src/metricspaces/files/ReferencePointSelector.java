package metricspaces.files;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.metrics.Metric;

public interface ReferencePointSelector {
	Descriptor[] select(int count, DescriptorFile objects, Metric metric, Progress progress);
}
