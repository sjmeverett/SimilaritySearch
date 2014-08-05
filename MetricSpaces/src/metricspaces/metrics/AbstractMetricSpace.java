package metricspaces.metrics;

import java.nio.ByteBuffer;
import java.util.List;

import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.descriptors.DescriptorFormat;
import metricspaces.objectselectors.ObjectSelector;
import metricspaces.objectselectors.SequentialObjectSelector;



public abstract class AbstractMetricSpace<DescriptorType> implements MetricSpace {
	public AbstractDescriptorFile<DescriptorType> descriptors;
	protected DescriptorFormat<DescriptorType> format;
	protected Metric<DescriptorType> metric;
	protected String metricName;
	protected int cacheId;
	protected DescriptorType cache;
	
	public AbstractMetricSpace(AbstractDescriptorFile<DescriptorType> descriptors, String metricName, Metric<DescriptorType> metric) {
		this.descriptors = descriptors;
		this.format = descriptors.getFormat();
		this.metricName = metricName;
		this.metric = metric;
	}
	
	@Override
	public double getDistance(int x, int y) {
		DescriptorType descriptorX = format.get(x);
		DescriptorType descriptorY = format.get(y);
		return metric.getDistance(descriptorX, descriptorY);
	}
	
	@Override
	public VantagePointList createVantagePointList(List<Integer> keys) {
		return new GenericVantagePointList<DescriptorType>(format, metric, keys);
	}
	
	@Override
	public MetricSpaceObject getObject(int id) {
		return new GenericMetricSpaceObject(id);
	}
	
	@Override
	public MetricSpaceFormat getFormat(ByteBuffer buffer, int size) {
		DescriptorFormat<DescriptorType> format = descriptors.getFormat(buffer, size);
		return new GenericMetricSpaceFormat(format);
	}
	
	@Override
	public String getMetricName() {
		return metricName;
	}
	
	@Override
	public int getDistanceCount() {
		return metric.getCount();
	}
	
	@Override
	public void resetDistanceCount() {
		metric.resetCount();
	}
	
	@Override
	public ObjectSelector getObjectSelector(String name) {
		if (name.equals("sequential")) {
			return new SequentialObjectSelector<DescriptorType>(this);
		}
		else {
			throw new IllegalArgumentException("Object selector '" + name + "' not known in this space");
		}
	}
	
	public class GenericMetricSpaceObject implements MetricSpaceObject {
		private int objectId;
		private DescriptorType descriptor;
		
		
		public GenericMetricSpaceObject(int objectId) {
			this.objectId = objectId;
			descriptor = format.get(objectId);
		}
		
		public GenericMetricSpaceObject(DescriptorType descriptor) {
			this.descriptor = descriptor;
			objectId = -1;
		}
				
		@Override
		public int getObjectID() {
			return objectId;
		}

		@Override
		public double getDistance(int objectId) {
			//if the distance between a load of objects and one specific object is being
			//calculated, it helps if we cache the last object used
			if (objectId != cacheId) {
				cache = format.get(objectId);
				cacheId = objectId;
			}
			
			return metric.getDistance(descriptor, cache);
		}
		
		
		public DescriptorType getDescriptor() {
			return descriptor;
		}
	}
	
	
	private class GenericMetricSpaceFormat implements MetricSpaceFormat {
		private DescriptorFormat<DescriptorType> format;
		
		public GenericMetricSpaceFormat(DescriptorFormat<DescriptorType> format) {
			this.format = format;
		}
		
		@Override
		public MetricSpaceObject get(int index) {
			DescriptorType descriptor = format.get(index);
			return new GenericMetricSpaceObject(descriptor);
		}
	}
}
