package metricspaces.descriptors;

/**
 * An implementation of Descriptor backed by double data.
 * @author stewart
 *
 */
public class DoubleDescriptor implements Descriptor {
	protected double[] data;
	protected double[] normalisedData;
	protected double complexity;
	private double l1norm, magnitude;
	
	
	protected DoubleDescriptor() {
		this.complexity = Double.NaN;
		this.l1norm = Double.NaN;
		this.magnitude = Double.NaN;
	}
	
	
	public DoubleDescriptor(double[] data) {
		this.data = data;
		this.complexity = Double.NaN;
		this.l1norm = Double.NaN;
		this.magnitude = Double.NaN;
	}
	

	@Override
	public double[] getData() {
		return data;
	}

	
	@Override
	public double[] getNormalisedData() {
		if (normalisedData == null) {
			normalisedData = new double[data.length];
			double norm = getL1Norm();
			
			if (norm == 0) {
				double d = 1.0 / data.length;
				
				for (int i = 0; i < data.length; i++) {
					normalisedData[i] = d;
				}
			}
			else {
				for (int i = 0; i < data.length; i++) {
					normalisedData[i] = data[i] / norm;
				}
			}
		}
		
		return normalisedData;
	}

	
	@Override
	public double getComplexity() {
		if (Double.isNaN(complexity)) {
            double acc = 0;
            double[] normalised = getNormalisedData();

            for (int i = 0; i < normalised.length; i++) {
            	double d = normalised[i];
            	
                if (d != 0) {
                    acc -= d * Math.log(d);
                }
            }

            complexity = Math.exp(acc);
        }

        return complexity;
	}
	
	
	@Override
	public double getMergedComplexity(Descriptor descriptor) {
		double[] x = getNormalisedData();
		double[] y = descriptor.getNormalisedData();
		double acc = 0;
		
		for (int i = 0; i < x.length; i++) {
			double d = (x[i]+ y[i]) / 2;
			
			if (d != 0) {
                acc -= d * Math.log(d);
            }
        }

        return Math.exp(acc);
	}
	
	@Override
	public double getMagnitude() {
        if (Double.isNaN(magnitude)) {
            double acc = 0;

            for (int i = 0; i < data.length; i++) {
                acc += data[i] * data[i];
            }

            magnitude = Math.sqrt(acc);
        }

        return magnitude;
    }

	
	public double getL1Norm() {
		if (Double.isNaN(l1norm)) {
			double norm = 0;
			
			for (int i = 0; i < data.length; i++) {
				norm += data[i];
			}
			
			this.l1norm = norm;
		}
		
		return this.l1norm;
	}
}
