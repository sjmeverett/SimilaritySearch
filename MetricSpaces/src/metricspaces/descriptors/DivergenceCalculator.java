package metricspaces.descriptors;

import java.util.List;


public class DivergenceCalculator {
	public double calculate(List<Descriptor> descriptors) {
		if (descriptors.size() == 0)
			return Double.NaN;
		else if (descriptors.size() == 1)
			return 0;
		
		int dimensions = descriptors.get(0).getDimensions();
		double[] merged = new double[dimensions];
		double[] entropy = new double[dimensions];
		
		for (Descriptor descriptor: descriptors) {
			double[] data = descriptor.getNormalisedData();
			
			for (int i = 0; i < data.length; i++) {
				merged[i] += data[i];
				entropy[i] += entropy(data[i]);
			}
		}
		
		double acc = 0;
		int n = descriptors.size();
		
		for (int i = 0; i < merged.length; i++) {
			acc += entropy(merged[i] / n) - entropy[i] / n;
		}
		
		return (Math.exp(acc) - 1) / (n - 1);
	}

	private double entropy(double d) {
		if (d == 0)
			return 0;
		else
			return -d * Math.log(d);
	}
}
