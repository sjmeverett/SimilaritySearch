package metricspaces.indexes.pivotselectors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import metricspaces.Progress;
import metricspaces.RandomHelper;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.DivergenceCalculator;
import metricspaces.files.DescriptorFile;
import metricspaces.metrics.Metric;

public class DivergencePivotSelector implements PivotSelector {	
	private int maxCandidates;
	
	public DivergencePivotSelector(int maxCandidates) {
		this.maxCandidates = maxCandidates;
	}
	
	@Override
	public Iterable<Integer> select(int count, DescriptorFile objects, Metric metric, Progress progress) {
		int size = Math.min(maxCandidates, objects.getCapacity());
		Set<Integer> pivotIDs = new HashSet<>(count);
		List<Descriptor> pivots = new ArrayList<>(count);
		
		DivergenceCalculator divergence = new DivergenceCalculator();
		progress.setOperation("Calculating pivots", count);
		
		//the first pivot is a random one
		int firstPivot = RandomHelper.getNextInt(0, size - 1);
		pivotIDs.add(firstPivot);
		pivots.add(objects.get(firstPivot));
		progress.incrementDone();
		
		//work out the rest of the pivots
		for (int i = 1; i < count; i++) {
			double max = Double.NEGATIVE_INFINITY;
			int pivot = Integer.MIN_VALUE;
			
			//calculate the pivot that would lead to the max divergence
			for (int j = 0; j < size; j++) {
				if (pivotIDs.contains(j))
					continue;
				
				pivots.add(objects.get(j));
				double d = divergence.calculate(pivots);
				pivots.remove(i);
				
				if (d > max) {
					d = max;
					pivot = j;
				}
			}
			
			pivots.add(objects.get(pivot));
			pivotIDs.add(pivot);
			progress.incrementDone();
		}
		
		return pivotIDs;
	}
}
