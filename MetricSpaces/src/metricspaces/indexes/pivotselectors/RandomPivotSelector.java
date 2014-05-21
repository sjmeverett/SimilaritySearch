package metricspaces.indexes.pivotselectors;

import java.util.ArrayList;
import java.util.List;

import metricspaces.Progress;
import metricspaces.RandomHelper;
import metricspaces.files.DescriptorFile;
import metricspaces.metrics.Metric;

public class RandomPivotSelector implements PivotSelector {

	@Override
	public List<Integer> select(int count, DescriptorFile objects, Metric metric, Progress progress) {
		List<Integer> pivots = new ArrayList<>(count);
		int objectCount = objects.getCapacity();
		
		for (int i = 0; i < count; i++) {
			pivots.add(RandomHelper.getNextInt(0, objectCount));
		}
		
		return pivots;
	}

}
