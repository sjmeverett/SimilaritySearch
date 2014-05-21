package metricspaces.indexes.pivotselectors;

import metricspaces.Progress;
import metricspaces.files.DescriptorFile;
import metricspaces.metrics.Metric;

public interface PivotSelector {

	public Iterable<Integer> select(int count, DescriptorFile objects, Metric metric, Progress progress);

}