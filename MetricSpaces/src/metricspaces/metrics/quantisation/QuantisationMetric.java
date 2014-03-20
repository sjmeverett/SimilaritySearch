package metricspaces.metrics.quantisation;

import metricspaces.descriptors.Descriptor;
import metricspaces.metrics.Metric;


/**
 * Base class for quantisation-based metrics.
 */
public abstract class QuantisationMetric implements Metric<Descriptor> {
	protected double p, max, mean, stddev;
	protected double epsilon1, epsilon2;


    public QuantisationMetric(double p) {
        this.p = p;
    }


    /**
     * The behaviour of the quantisation model depends on the stats of the data.
     * @param mean
     * @param stddev
     * @param max
     */
	public void setStats(double mean, double stddev, double max) {
		this.mean = mean;
		this.stddev = stddev;
		this.max = max;
		setP(max);
	}


    @Override
    public double getDistance(Descriptor x, Descriptor y) {
        double[] xi = x.getData();
        double[] xj = y.getData();
        double a = 0, b = 0, c = 0, d = 0;

        for (int k = 0; k < xi.length; k++) {
            final double xik = xi[k], xjk = xj[k];
            final double m = (xik + xjk) / 2;
            final double bi = xik - xjk;
            final double ci = xjk - xik;

            if (max - m <= epsilon1)
                a += m;

            if (max - bi <= epsilon2)
                b += bi;

            if (max - ci <= epsilon2)
                c += ci;

            if (m <= epsilon1)
                d += max - m;
        }

        return distance(a, b, c, d, xi.length);
    }

    public void setP(double value) {
    	this.p = value;
    	
    	if (p >= mean)
			epsilon1 = max * (1 - mean / p);
		else
			epsilon1 = 0;
		
		if (p >= stddev)
			epsilon2 = max * (1 - stddev / p);
		else
			epsilon2 = 0;
    }

    protected abstract double distance(double a, double b, double c, double d, int K);
}