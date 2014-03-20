package metricspaces.descriptors;

/**
 * Represents a descriptor vector.
 * @author stewart
 *
 */
public interface Descriptor {
	double[] getData();
	double[] getNormalisedData();
	double getComplexity();
	double getMergedComplexity(Descriptor descriptor);
	double getMagnitude();
}
