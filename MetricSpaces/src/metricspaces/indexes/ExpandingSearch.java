package metricspaces.indexes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * Provides support for increasing-radius search of items in an index against that index.
 * 
 * The caller should iterate over the results, calling remove() for acceptable ones. Once all
 * queries have produced acceptable results and have been removed, no more results will be returned. 
 * 
 * @author stewart
 *
 */
public class ExpandingSearch {
	private final Index index;
	private final double increasingFactor;
	private final List<Integer> queries;
	private double radius;
	
	
	/**
	 * Creates a new iterator to search all items in the given index against that index.
	 * @param index The index to search.
	 * @param initialRadius The initial search radius.
	 * @param increasingFactor The amount to increase the radius each iteration (e.g. 1.1 for 10%).
	 */
	public ExpandingSearch(Index index, double initialRadius, double increasingFactor) {
		this.index = index;
		this.increasingFactor = increasingFactor;
		
		int count = index.getHeader().getCapacity();
		queries = new ArrayList<>();
		
		//add all the query indices to a list
		for (int i = 0; i < count; i++)
			queries.add(i);
		
		radius = initialRadius;
	}
	
	
	/**
	 * Creates a new iterator to search only the supplied keys against the given index.  Note that if
	 * a given key is not found in the index, no error or warning is given.
	 *  
	 * @param index
	 * @param initialRadius
	 * @param increasingFactor
	 * @param keys
	 */
	public ExpandingSearch(Index index, double initialRadius, double increasingFactor, Collection<Integer> keys) {
		this.index = index;
		this.increasingFactor = increasingFactor;
		
		int count = index.getHeader().getCapacity();
		queries = new ArrayList<>();
		
		//go through the whole index storing positions which relate to our key list
		for (int i = 0; i < count; i++) {
			int key = index.getKey(i);
			
			if (keys.contains(key))
				queries.add(i);
		}
		
		radius = initialRadius;
	}

	
	public Iterator<List<SearchResult>> search() {
		final double currentRadius = radius;
		
		Iterator<List<SearchResult>> it = new Iterator<List<SearchResult>>() {
			Iterator<Integer> queryIterator = queries.iterator();
			
			@Override
			public boolean hasNext() {
				return queryIterator.hasNext();
			}

			
			@Override
			public List<SearchResult> next() {
				if (!hasNext())
					throw new NoSuchElementException();
				
				//return the search results for the next query
				return index.search(queryIterator.next(), currentRadius);
			}

			
			@Override
			public void remove() {
				//the caller is satisfied with the results from the last search
				//so remove the query
				queryIterator.remove();
			}
		};
		
		radius *= increasingFactor;
		return it;
	}
	
	
	public boolean hasQueries() {
		return queries.size() > 0;
	}
}
