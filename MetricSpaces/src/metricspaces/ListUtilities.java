package metricspaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ListUtilities {
	/**
	 * Partitions the portion of the list between start and end inclusive according to the value stored at the
	 * index indicated by pivot; that is to say, everything less than the value at the pivot index is placed
	 * before the pivot, and everything greater than the pivot value is placed after the pivot.
	 * 
	 * @param list The list to partition.
	 * @param start The index of the start of the portion of the list to partition.
	 * @param pivot The index of the the value to use in partitioning.
	 * @param end The index of the end of the portion of the list to partition.
	 * @param comparator The comparator to use when comparing objects.
	 * @return The final index of the partition value.
	 */
	public static <C, T extends C> int partition(List<T> list, int start, int pivot, int end, Comparator<C> comparator) {
		//swap the pivot to the end
		T pivotValue = list.get(pivot);
		Collections.swap(list, pivot, end);
		
		//swap everything less than the pivot to the left of the list
		int left = start;
		
		for (int i = start; i < end; i++) {
			if (comparator.compare(list.get(i), pivotValue) <= 0) {
				Collections.swap(list, left, i);
				left++;
			}
		}
		
		//swap the pivot into its correct position
		Collections.swap(list, end, left);
		return left;
	}
	
	
	/**
	 * Rearranges the portion of the list from start to end inclusive such that the k-th element
	 * (which must be within the portion) is moved to the place which it would be in had the portion
	 * been sorted in ascending order.
	 * 
	 * @param list The list to rearrange.
	 * @param start The start of the portion to be rearranged.
	 * @param end The end of the portion to be rearranged.
	 * @param k The element index to find.
	 * @param comparator The comparator to use when sorting.
	 * @return The value of the k-th element.
	 */
	public static <C, T extends C> T quickSelect(List<T> list, int start, int end, int k, Comparator<C> comparator) {
		while (end > start) {
			//randomly select a pivot index and partition the list
			int pivot = RandomHelper.getNextInt(start, end);
			pivot = partition(list, start, pivot, end, comparator);

			if (pivot == k) {
				//the k-th element is the pivot, so is in its correct place
				return list.get(pivot);
			}
			else if (k < pivot) {
				//the k-th element is to the left of the pivot
				end = pivot - 1;
			}
			else {
				//the k-th element is to the right of the pivots
				start = pivot + 1;
			}
		}
		
		//there is only one item in the list
		return list.get(start);
	}
	
	
	/**
	 * Gets the median value of a portion of an unsorted list, keeping sorting to a minimum.
	 * @param list The list to find a median in.
	 * @param start The start index of the portion for which the median is sought. 
	 * @param end The end index of the portion for which the median is sought.
	 * @param comparator The comparator to use when sorting.
	 * @return The value of the median value in the portion of the list from start to end inclusive.
	 */
	public static <T> T median(List<T> list, int start, int end, Comparator<T> comparator) {
		return quickSelect(list, start, end, (end - start + 1) / 2, comparator);
	}
	
	
	/**
	 * Copies all the items in an Iterable to a list.
	 * 
	 * @param iterable
	 * @return
	 */
	public static <T> List<T> fromIterable(Iterable<T> iterable) {
		List<T> list = new ArrayList<T>();
		
		for (T item: iterable)
			list.add(item);
		
		return list;
	}
}