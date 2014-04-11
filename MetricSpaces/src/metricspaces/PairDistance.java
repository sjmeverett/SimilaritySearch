package metricspaces;

import metricspaces.indexes.SearchResult;

/**
 * Represents a pair of objects and the distance between them.  The natural order is defined by
 * the distance.  Note that this is inconsistent with equals: two pairs are considered
 * equal if they refer to the same pair of objects.  Strange stuff will happen if you put this in a
 * TreeSet, specifically the TreeSet will not conform to the Set contract and results that happen
 * to have the same distance will disappear, which is probably not what you want.  Instead, use a
 * HashSet to gather distinct results first, then add them to a list and sort it.
 * 
 * Note also that the implementation guarantees object1 < object2 so long as they implement compareTo
 * properly. 
 * @author stewart
 *
 * @param <ObjectType> The type of objects in the pair.
 */
public class PairDistance implements Comparable<PairDistance> {
	private int object1, object2;
	private double distance;
	
	
	/**
	 * Create a pair from two objects and the distance between them.
	 * @param x
	 * @param y
	 * @param distance
	 */
	public PairDistance(int x, int y, double distance) {
		if (x < y) {
			object1 = x;
			object2 = y;
		}
		else {
			object1 = y;
			object2 = x;
		}
		
		this.distance = distance;
	}
	
	
	/**
	 * Create a pair from a search result.
	 * @param result
	 */
	public PairDistance(SearchResult result) {
		this(result.getQuery(), result.getResult(), result.getDistance());
	}
	
	
	public int getObject1() {
		return object1;
	}
	
	
	public int getObject2() {
		return object2;
	}
	
	
	public double getDistance() {
		return distance;
	}
	
	
	@Override
	public int compareTo(PairDistance o) {
		return Double.compare(distance, o.distance);
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PairDistance) {
			PairDistance p = (PairDistance)o;
			return object1 == p.object1 && object2 == p.object2;
		}
		else {
			return super.equals(o);
		}
	}
}
