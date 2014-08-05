package metricspaces.util;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A data structure for keeping the n 'best' objects.
 * @author stewart
 *
 * @param <E>
 */
public class FixedSizePriorityQueue<E> extends AbstractQueue<E> {
	private final E[] items;
	private final Comparator<? super E> comparator;
	private int start, end;
	
	@SuppressWarnings("unchecked")
	public FixedSizePriorityQueue(int size, Comparator<? super E> comparator) {
		this.comparator = comparator;
		items = (E[])new Object[size];
		start = 0;
		end = 0;
	}
	
	public FixedSizePriorityQueue(int size) {
		this(size, null);
	}

	@Override
	public boolean offer(E e) {
		//figure out where to put the item
		int pos;
		
		if (start < items.length) {
			if (comparator == null)
				pos = Arrays.binarySearch(items, start, end, e);
			else
				pos = Arrays.binarySearch(items, start, end, e, comparator);
			
			if (pos < 0) {
				//get the insertion point
				pos = -pos - 1;
			}
			else {
				//check if the item being inserted is the same as the one found
				//allows equals to be inconsistent with compareTo 
				if (e.equals(items[pos])) return true;
				
				//insert ties after, might get to do less work
				pos += 1;
			}
		}
		else {
			pos = items.length - 1;
		}
		
		if (pos < items.length) {
			if (start == 0 && pos < items.length - 1) {
				//if start==0, we move the items after 1 place right, as long as we're not inserting at the end
				System.arraycopy(items, pos, items, pos + 1, items.length - pos - 1);
				
				if (end < items.length)
					end++;
			}
			else if (start > 0) {
				if (pos > start) {
					System.arraycopy(items, start, items, start - 1, pos - 1);
				}
				
				pos -= 1;
				start -= 1;
			}
			
			//insert
			items[pos] = e;
		}
		
		return true;
	}

	@Override
	public E poll() {
		if (size() == 0)
			return null;
		
		return items[start++];
	}

	@Override
	public E peek() {
		if (size() == 0)
			return null;
		
		return items[start];
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			int i = start;
			
			@Override
			public boolean hasNext() {
				return i < end;
			}

			@Override
			public E next() {
				if (!hasNext())
					throw new NoSuchElementException();
				
				return items[i++];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}

	@Override
	public int size() {
		return end - start;
	}
	
	/**
	 * Gets the item at the final position of the queue.  Note that if the queue is not full,
	 * this will return null.
	 * @return
	 */
	public E getEnd() {
		if (size() < items.length)
			return null;
		else
			return items[items.length - 1];
	}
}
