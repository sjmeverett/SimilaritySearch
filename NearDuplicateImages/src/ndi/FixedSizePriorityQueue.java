package ndi;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A data structure for keeping the n 'best' objects.
 * @author stewart
 *
 * @param <E>
 */
public class FixedSizePriorityQueue<E extends Comparable<E>> extends AbstractQueue<E> {
	private final Object[] items;
	private int start, end;
	
	public FixedSizePriorityQueue(int size) {
		items = new Object[size];
		start = 0;
		end = 0;
	}

	@Override
	public boolean offer(E e) {
		//figure out where to put the item
		int pos;
		
		if (start < items.length) {
			pos = Arrays.binarySearch(items, start, end, e);
			
			if (pos < 0) {
				//get the insertion point
				pos = -pos - 1;
			}
			else {
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
	@SuppressWarnings("unchecked")
	public E poll() {
		if (size() == 0)
			return null;
		
		return (E)items[start++];
	}

	@Override
	@SuppressWarnings("unchecked")
	public E peek() {
		if (size() == 0)
			return null;
		
		return (E)items[start];
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
			@SuppressWarnings("unchecked")
			public E next() {
				if (!hasNext())
					throw new NoSuchElementException();
				
				return (E)items[i++];
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
}
