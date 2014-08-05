package metricspaces.update.common;

/**
 * Selects objects in sequence from ID 0.
 * @author stewart
 *
 * @param <DescriptorType>
 */
public class SequentialObjectSelector<DescriptorType> extends AbstractObjectSelector<DescriptorType> {
	private int i;
	
	public SequentialObjectSelector(AbstractMetricSpace<DescriptorType> space) {
		super(space);
		i = 0;
	}
	
	@Override
	public MetricSpaceObject next() {
		AbstractMetricSpace<DescriptorType>.GenericMetricSpaceObject object = space.new GenericMetricSpaceObject(i++);
		save(object.getDescriptor());
		return object;
	}
}
