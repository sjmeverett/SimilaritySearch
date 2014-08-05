package metricspaces.objectselectors;

import java.nio.ByteBuffer;

import metricspaces.metrics.MetricSpaceObject;

/**
 * Selects or generates object in a metric space.
 * @author stewart
 *
 */
public interface ObjectSelector {
	/**
	 * Gets the next object.
	 * @return
	 */
	public MetricSpaceObject next();
	
	/**
	 * Sets the output buffer, so that calling next will also write the object to the specified buffer.
	 * @param buffer The buffer to write to.
	 * @param size The number of descriptors that can fit in the buffer.
	 */
	public void setOutputBuffer(ByteBuffer buffer, int size);
}
