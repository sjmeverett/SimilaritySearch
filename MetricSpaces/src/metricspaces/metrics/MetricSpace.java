package metricspaces.metrics;

import java.nio.ByteBuffer;
import java.util.List;

import metricspaces.objectselectors.ObjectSelector;

/**
 * Defines a metric space; that is, a pair of a descriptor file and a metric.
 * @author stewart
 */
public interface MetricSpace {
	/**
	 * Gets the distance between the two specified objects.
	 * @param x
	 * @param y
	 * @return
	 */
	public double getDistance(int x, int y);
	
	/**
	 * Gets the object with the specified ID.
	 * @param id
	 * @return
	 */
	public MetricSpaceObject getObject(int id);
	
	/**
	 * Gets a MetricSpaceFormat for the specified buffer.
	 * @param buffer
	 * @param size
	 * @return
	 */
	public MetricSpaceFormat getFormat(ByteBuffer buffer, int size);
	
	/**
	 * Creates a new vantage point list in the space, filled with the objects represented by the given keys.
	 * @return
	 */
	public VantagePointList createVantagePointList(List<Integer> keys);
	
	/**
	 * Gets the name of the metric used in this space.
	 * @return
	 */
	public String getMetricName();
	
	/**
	 * Gets the number of calculations performed by the metric so far.
	 * @return
	 */
	public int getDistanceCount();
	
	/**
	 * Resets the count of the number of calculations performed by the metric.
	 */
	public void resetDistanceCount();
	
	/**
	 * Gets the object selector with the specified name.
	 * @param name
	 * @return
	 */
	public ObjectSelector getObjectSelector(String name);
}
