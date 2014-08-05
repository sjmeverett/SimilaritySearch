package metricspaces.metrics;

/**
 * Represents an object in a metric space.
 * @author stewart
 */
public interface MetricSpaceObject {
	/**
	 * Gets the ID of the object represented.
	 * @return
	 */
	public int getObjectID();
	
	/**
	 * Gets the distance between this object and the object with the specified ID.
	 * @param objectId
	 * @return
	 */
	public double getDistance(int objectId);
}
