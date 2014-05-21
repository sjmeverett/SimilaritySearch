package metricspaces.indexes.pivotselectors;

import java.util.ArrayList;
import java.util.List;

import metricspaces.Progress;
import metricspaces.RandomHelper;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.metrics.Metric;

public class KMeansPivotSelector implements PivotSelector {

	@Override
	public List<Integer> select(int count, DescriptorFile objects, Metric metric, Progress progress) {
		List<Integer> pivots = new ArrayList<>(count);
		pivots.add(RandomHelper.getNextInt(0, objects.getCapacity() - 1));
		
		progress.setOperation("Selecting pivots", count);
		progress.incrementDone();
		
		for (int i = 1; i < count; i++) {
			addNewPivot(pivots, objects, metric);
			progress.incrementDone();
		}
		
		return pivots;
	}
	
	
	private void addNewPivot(List<Integer> pivots, DescriptorFile objects, Metric metric) {
		int objectCount = objects.getCapacity();
		double[] distances = new double[objectCount];
		double sum = 0;
		
		for (int i = 0; i < objectCount; i++) {
			double min = Double.POSITIVE_INFINITY;
			Descriptor obj = objects.get(i);
			
			for (Integer pivotId: pivots) {
				double d = metric.getDistance(obj, objects.get(pivotId));
				
				if (d < min)
					min = d;
			}
			
			min *= min;
			distances[i] = min;
			sum += min;
		}
		
		double p = Math.random();
		double cumulativeProbability = 0;
		
		for (int i = 0; i < distances.length; i++) {
			double probability = distances[i] / sum;
			cumulativeProbability += probability;
			
			if ((sum == 0 || cumulativeProbability >= p) && !pivots.contains(i)) {
				pivots.add(i);
				return;
			}
		}
		
		for (int i = distances.length; i >= 0; i--) {
			if (!pivots.contains(i)) {
				pivots.add(i);
				return;
			}
		}
		
		throw new IllegalStateException("Could not add pivot.");
	}
}
