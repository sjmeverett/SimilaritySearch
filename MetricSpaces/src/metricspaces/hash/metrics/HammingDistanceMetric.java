package metricspaces.hash.metrics;

import metricspaces.hash.HashDescriptor;
import metricspaces.metrics.AbstractMetric;

public class HammingDistanceMetric extends AbstractMetric<HashDescriptor> {

	@Override
	public double getDistance(HashDescriptor x, HashDescriptor y) {
		int distance = 0;
		byte[] xd = x.getHash();
		byte[] yd = y.getHash();
		
		for (int i = 0; i < xd.length; i++) {
			byte mask = 1;
			
			for (int j = 0; j < 8; j++) {
				if ((xd[i] & mask) != (yd[j] & mask))
					distance++;
				
				mask <<= 1;
			}
		}
		
		return distance;
	}

}
