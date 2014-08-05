package metricspaces.update.common;

import metricspaces.indexes.SearchResult;

/**
 * Represents a list of objects with methods for sorting them based on proximity to a vantage point.
 * @author stewart
 *
 */
public interface VantagePointList {

	/**
	 * Adds the object with the specified ID to the list.
	 * @param id
	 */
	public void add(int id);
	
	/**
	 * Gets the object at the specified index in the list.
	 * @param index
	 * @return
	 */
	public MetricSpaceObject get(int index);

	/**
	 * Partitions the list based on the the objects' distances to the specified vantage point,
	 * such that objects with distances less than the distance of the pivot object are moved
	 * to the left of the pivot, and objects with greater distances are moved to the right.
	 * @param start The starting index of the portion of the list to pivot.
	 * @param pivot The index of the pivot object.
	 * @param end The end index of the portion of the list to pivot.
	 * @param vantagePointId The object ID of the vantage point.
	 * @return The new index of the pivot object.
	 */
	public int partition(int start, int pivot, int end, int vantagePointId);

	/**
	 * Rearranges the portion of the list from start to end inclusive such that the k-th element
	 * (which must be within the portion) is moved to the place which it would be in had the portion
	 * been sorted in ascending order, based on distance to the specified pivot.
	 * 
	 * @param start The starting index of the portion of the list to rearrange.
	 * @param end The end index of the portion of the list to rearrange.
	 * @param k The index of the pivot object.
	 * @param vantagePointId The object ID of the vantage point. 
	 * @return The object ID of the element at the k-th index.
	 */
	public SearchResult quickSelect(int start, int end, int k, int vantagePointId);
}