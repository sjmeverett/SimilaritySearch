package metricspaces.metrics.quantisation;

/**
 * Implements the Coefficient of Racial Likeness[1] distance measure using a quantisation model[2] (P7).
 * 
 * [1] Pearson, K. On the coefficients of racial likeness. Biometrica, 18 (1926), 105-117.
 * [2] Eidenberger, H.  Distance measures for MPEG-7-based retrieval. Proceedings of the 5th ACM SIGMM
 * international workshop on Multimedia information retrieval, 2003. 130-137.
 * 
 * @author Stewart MacKenzie-Leigh
 *
 */
public class CoefficientOfRacialLikeness extends QuantisationMetric {

    public CoefficientOfRacialLikeness(double p) {
        super(p);
    }

    @Override
    protected double distance(double a, double b, double c, double d, int K) {
        double t = Math.sqrt((a + b) * (a + c) * (b + d) * (c + d));

        if (t == 0)
            t = 1;

        double dist = (a * d - b * c) / t;

        return 1 - dist;
    }


	@Override
	public String getName() {
		return "CRL";
	}
}