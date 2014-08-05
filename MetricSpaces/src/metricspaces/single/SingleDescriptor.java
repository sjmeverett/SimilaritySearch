package metricspaces.single;

/**
 * Represents descriptors with single-precision data.  Not using a generic interface
 * because I want performance and Java sucks.
 * @author stewart
 *
 */
public class SingleDescriptor {
	protected float[] data;
	protected float[] normalisedData;
	protected float complexity;
	private float l1norm, magnitude;
	
	
	protected SingleDescriptor() {
		this.complexity = Float.NaN;
		this.l1norm = Float.NaN;
		this.magnitude = Float.NaN;
	}
	
	
	public SingleDescriptor(float[] data) {
		this.data = data;
		this.complexity = Float.NaN;
		this.l1norm = Float.NaN;
		this.magnitude = Float.NaN;
	}
	

	public float[] getData() {
		return data;
	}

	
	public float[] getNormalisedData() {
		if (normalisedData == null) {
			normalisedData = new float[data.length];
			float norm = getL1Norm();
			
			if (norm == 0) {
				float d = 1.0f / data.length;
				
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

	
	public float getComplexity() {
		if (Float.isNaN(complexity)) {
            float acc = 0;
            float[] normalised = getNormalisedData();

            for (int i = 0; i < normalised.length; i++) {
            	float d = normalised[i];
            	
                if (d != 0) {
                    acc -= d * Math.log(d);
                }
            }

            complexity = (float)Math.exp(acc);
        }

        return complexity;
	}
	
	
	public float getMergedComplexity(SingleDescriptor descriptor) {
		float[] x = getNormalisedData();
		float[] y = descriptor.getNormalisedData();
		float acc = 0;
		
		for (int i = 0; i < x.length; i++) {
			float d = (x[i] + y[i]) / 2;
			
			if (d != 0) {
                acc -= d * Math.log(d);
            }
        }

        return (float)Math.exp(acc);
	}
	
	
	public float getMagnitude() {
        if (Float.isNaN(magnitude)) {
            float acc = 0;

            for (int i = 0; i < data.length; i++) {
                acc += data[i] * data[i];
            }

            magnitude = (float)Math.sqrt(acc);
        }

        return magnitude;
    }

	
	public float getL1Norm() {
		if (Float.isNaN(l1norm)) {
			float norm = 0;
			
			for (int i = 0; i < data.length; i++) {
				norm += data[i];
			}
			
			this.l1norm = norm;
		}
		
		return this.l1norm;
	}
	
	
	public int getDimensions() {
		return data.length;
	}
}
