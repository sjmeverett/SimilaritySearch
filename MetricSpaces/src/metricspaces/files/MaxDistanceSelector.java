package metricspaces.files;

import java.util.Collections;
import java.util.Queue;

import metricspaces.FixedSizePriorityQueue;
import metricspaces.OrderedPair;
import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.metrics.Metric;

public class MaxDistanceSelector implements ReferencePointSelector {
	private int size;
	
	public MaxDistanceSelector(int size) {
		this.size = size;
	}
	
	
	@Override
	public Descriptor[] select(int count, DescriptorFile objects, Metric metric, Progress progress) {
		Queue<OrderedPair<Double, Descriptor>> points = new FixedSizePriorityQueue<>(count, Collections.reverseOrder());
		progress.setOperation("Finding reference points", size);
		
		//initialise
		for (int i = 0; i < count; i++) {
			Descriptor point = objects.get(i);
			double sum = 0;
			
			for (int j = 0; j < count; j++) {
				if (i == j) continue;
				sum += metric.getDistance(point, objects.get(j));
			}
			
			points.add(new OrderedPair<>(sum, point));
			progress.incrementDone();
		}
		
		//find better points
		for (int i = count; i < size; i++) {
			Descriptor point = objects.get(i);
			double sum = 0;
			
			for (OrderedPair<Double, Descriptor> pair: points) {
				sum += metric.getDistance(point, pair.getSecond());
			}
			
			points.add(new OrderedPair<>(sum, point));
			progress.incrementDone();
		}
		
		//extract the points into an array
		Descriptor[] referencePoints = new Descriptor[count];
		int i = 0;
		
		for (OrderedPair<Double, Descriptor> pair: points) {
			referencePoints[i++] = pair.getSecond();
		}
		
		return referencePoints;
	}

}
