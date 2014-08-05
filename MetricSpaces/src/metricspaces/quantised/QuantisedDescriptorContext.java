package metricspaces.quantised;

/**
 * ByteDescriptors are created from double values by normalising the vector by the maximum
 * element value and multiplying each element by 255.  This class stores the maximum
 * element value so that the double data can be reconstructed, and pre-calculates a
 * lookup table to make the resconstruction faster.
 * 
 * All ByteVectors for a given descriptor might also sum to the same value, which is
 * also stored in this class.
 * 
 * @author stewart
 *
 */
public class QuantisedDescriptorContext {
	private int l1norm;
	private double elementMax;
	public double[] logTable, valueTable, normalisedTable;
	
	public static final int NOT_NORMALISED = -1;
	
	/**
	 * Constructor.
	 * @param elementMax The maximum element value used when the byte data was constructed from double data.
	 * @param l1norm Either a value indicating the value that all the descriptors sum to, or NOT_NORMALISED to indicate
	 * that the descriptors are not normalised.
	 */
	public QuantisedDescriptorContext(double elementMax, int l1norm) {
		this.elementMax = elementMax;
		this.l1norm = l1norm;
		
		//calculate the double value table
		//each element has been quantised by normalising to the max value and
		//multiplying by 255
		valueTable = new double[256];
		
		for (int i = 0; i < valueTable.length; i++) {
			valueTable[i] = (double)i * elementMax / 255;
		}
		
		if (l1norm != NOT_NORMALISED) {
			//calculate the normalised value table
			normalisedTable = new double[256];
			
			for (int i = 0; i < normalisedTable.length; i++) {
				normalisedTable[i] = (double)i / l1norm;
			}
			
			//calculate the log table
			//the maximum value of two bytes added is 510
	        logTable = new double[511];
	
	        for (int i = 0; i < logTable.length; i++) {
	            final double d = (double)i / l1norm;
	            logTable[i] = -d * Math.log(d);
	        }
		}
	}
	
	
	/**
	 * Indicates if the vectors that use this context are supposed to sum to a known value. 
	 * @return
	 */
	public boolean isNormalised() {
		return l1norm != NOT_NORMALISED;
	}
	
	
	/**
	 * Gets the double data represented by the byte vector.
	 * @param data
	 * @return
	 */
	public double[] getDoubleData(byte[] data) {
		double[] doubleData = new double[data.length];
		
		for (int i = 0; i < data.length; i++) {
			doubleData[i] = valueTable[data[i] & 0xFF];
		}
		
		return doubleData;
	}
	
	
	/**
	 * Gets the normalised double data represented by the byte vector.
	 * @param data
	 * @return
	 */
	public double[] getNormalisedDoubleData(byte[] data) {
		if (l1norm == NOT_NORMALISED)
			throw new UnsupportedOperationException("This ByteVectorContext does not support normalisation.");
		
		double[] normalisedData = new double[data.length];
		double total = 0;
		
		for (int i = 0; i < data.length; i++) {
			double d = normalisedTable[data[i] & 0xFF];
			normalisedData[i] = d;
			total += d;
		}
		
		if (total == 0) {
			double d = 1.0 / data.length;
			
			for (int i = 0; i < data.length; i++) {
				normalisedData[i] = d;
			}
		}
		
		return normalisedData;
	}
	
	
	public double getComplexity(byte[] data) {
		if (l1norm == NOT_NORMALISED)
			throw new UnsupportedOperationException("This ByteVectorContext does not support normalisation.");
		
        double acc = 0;

        for (int i = 0; i < data.length; i++) {
            final int val = (data[i] & 0xFF) << 1;

            if (val != 0) {
                acc += logTable[val];
            }
        }

        return Math.exp(acc);
    }


    public double getMergedComplexity(byte[] x, byte[] y) {
    	if (l1norm == NOT_NORMALISED)
			throw new UnsupportedOperationException("This ByteVectorContext does not support normalisation.");
    	
        double acc = 0;

        for (int i = 0; i < x.length; i++) {
            final int val = (x[i] & 0xFF) + (y[i] & 0xFF);

            if (val != 0) {
                acc += logTable[val];
            }
        }

        return Math.exp(acc);
    }
    
    
    public byte[] getByteData(double[] data) {
    	byte[] byteData = new byte[data.length];
    	
    	for (int i = 0; i < data.length; i++) {
    		final double d = data[i] / elementMax;
    		assert(d <= 1);
    		
    		byteData[i] = (byte)Math.round(d * 255);
    	}
    	
    	return byteData;
    }
}
