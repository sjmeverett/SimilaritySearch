package ndi.extractors.phash;

/**
 * Performs the Discrete Cosine Transform, not very quickly.
 * Copied from http://dx.doi.org/10.1007/978-1-84628-968-2_15
 */
public class DiscreteCosineTransform {
	private static final double INV_SQRT2 = 1.0 / Math.sqrt(2);
	
	/**
	 * Applies the DCT.
	 * @param g
	 * @return
	 */
	public double[] apply(double[] g) {
		int M = g.length;
		double M2 = 2.0 * M;
		double s = Math.sqrt(2.0 / M);
		double[] G = new double[M];
		
		for (int m = 0; m < M; m++) {
			double sum = 0;
			double cm = 1.0;
			
			if (m == 0)
				cm = INV_SQRT2;
			
			for (int u = 0; u < M; u++) {
				double Phi = (Math.PI * m * (2 * u + 1)) / M2;
				sum += g[u] * cm * Math.cos(Phi);
			}
			
			G[m] = s * sum;
		}
		
		return G;
	}
	
	
	/**
	 * Performs the inverse DCT.
	 * @param G
	 * @return
	 */
	public double[] inverse(double[] G) {
		int M = G.length;
		double M2 = 2.0 * M;
		double s = Math.sqrt(2.0 / M);
		double[] g = new double[M];
		
		for (int u = 0; u < M; u++) {
			double sum = 0;
			
			for (int m = 0; m < M; m++) {
				double cm = 1.0;
				
				if (m == 0)
					cm = INV_SQRT2;
				
				double Phi = (Math.PI * (2 * u + 1) * m) / M2;
				sum += cm * G[m] * Math.cos(Phi);
			}
			
			g[u] = s * sum;
		}
		
		return g;
	}
}
