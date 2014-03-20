package ndi;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Extends priority queue to make it fixed-size; that is, defines a data structure
 * for keeping the n 'best' objects.
 * @author stewart
 *
 * @param <E>
 */
public class FixedSizePriorityQueue<E> extends PriorityQueue<E> {
	private static final long serialVersionUID = -3676693773766312767L;
	private int fixedSize;
	
	public FixedSizePriorityQueue(int fixedSize, Comparator<? super E> comparator) {
		super(fixedSize + 1, comparator);
	}
	
	@Override
	public boolean offer(E e) {
		super.offer(e);
		
		if (size() > fixedSize) {
			super.poll();
		}
		
		return true;
	}
}
