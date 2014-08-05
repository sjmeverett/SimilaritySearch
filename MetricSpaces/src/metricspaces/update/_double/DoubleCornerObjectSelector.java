package metricspaces.update._double;

import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.update.common.AbstractMetricSpace;
import metricspaces.update.common.AbstractObjectSelector;
import metricspaces.update.common.MetricSpaceObject;

/**
 * Generates objects in the corner of the space.
 * @author stewart
 *
 */
public class DoubleCornerObjectSelector extends AbstractObjectSelector<DoubleDescriptor> {
	private int dimensions;
	private int i;
	
	public DoubleCornerObjectSelector(AbstractMetricSpace<DoubleDescriptor> space, int dimensions) {
		super(space);
		this.dimensions = dimensions;
		i = 0;
	}
	
	@Override
	public MetricSpaceObject next() {
		double[] data = new double[dimensions];
		
		for (int j = 1; j < i; j++) {
			data[j] = 1;
		}
		
		if (i < dimensions - 1)
			i++;
		
		DoubleDescriptor descriptor = new DoubleDescriptor(data);
		save(descriptor);
		return space.new GenericMetricSpaceObject(descriptor);
	}

}
