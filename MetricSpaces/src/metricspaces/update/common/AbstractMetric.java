package metricspaces.update.common;

public abstract class AbstractMetric<DescriptorType> implements Metric<DescriptorType> {
	protected int count;
	
	
	protected void incrementCount() {
		count++;
	}
	
	
	@Override
	public int getCount() {
		return count;
	}
	
	@Override
	public void resetCount() {
		count = 0;
	}
}
