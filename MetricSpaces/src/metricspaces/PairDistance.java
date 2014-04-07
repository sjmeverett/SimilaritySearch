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
public class PairDistance<ObjectType extends Comparable<ObjectType>> implements Comparable<PairDistance<ObjectType>> {
	private ObjectType object1, object2;
	private double distance;
	
	
	/**
	 * Create a pair from two objects and the distance between them.
	 * @param x
	 * @param y
	 * @param distance
	 */
	public PairDistance(ObjectType x, ObjectType y, double distance) {
		if (x.compareTo(y) < 0) {
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
	 * Create a pair from a query and a search result for that query.
	 * @param query
	 * @param result
	 */
	public PairDistance(ObjectType query, SearchResult<ObjectType> result) {
		this(query, result.getResult(), result.getDistance());
	}
	
	
	public ObjectType getObject1() {
		return object1;
	}
	
	
	public ObjectType getObject2() {
		return object2;
	}
	
	
	public double getDistance() {
		return distance;
	}
	
	
	@Override
	public int compareTo(PairDistance<ObjectType> o) {
		return Double.compare(distance, o.distance);
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PairDistance) {
			PairDistance p = (PairDistance)o;
			return object1.equals(p.object1) && object2.equals(p.object2);
		}
		else {
			return super.equals(o);
		}
	}
}
