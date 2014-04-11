package metricspaces.indexes;

/**
 * Represents a result from a search using a similarity index.  The natural order is defined by
 * the distance.  Note that this is inconsistent with equals: two results are considered
 * equal if they refer to the same object.  Strange stuff will happen if you put this in a
 * TreeSet, specifically the TreeSet will not conform to the Set contract and results that happen
 * to have the same distance will disappear, which is probably not what you want.  Instead, use a
 * HashSet to gather distinct results first, then add them to a list and sort it.
 *
 * @author stewart
 */
public class SearchResult implements Comparable<SearchResult> {
	private Integer query;
    private int result;
    private double distance;

    /**
     * Constructor.
     * @param result The result found from the query.
     * @param distance The distance from the query object to the result object.
     */
    public SearchResult(int result, double distance) {
        this.result = result;
        this.distance = distance;
    }
    
    
    /**
     * 
     * @param query
     * @param result
     * @param distance
     */
    public SearchResult(Integer query, int result, double distance) {
    	this(result, distance);
    	this.query = query;
    }
    

    /**
     * Gets the query key, if it is known; otherwise, null.
     * @return
     */
    public Integer getQuery() {
    	return query;
    }
    

    /**
     * Gets the result key.
     * @return
     */
    public int getResult() {
        return result;
    }


    /**
     * Gets the distance from the query object to the result object.
     * @return
     */
    public double getDistance() {
        return distance;
    }


    @Override
    public int compareTo(SearchResult that) {
        return Double.compare(this.distance, that.distance);
    }
    
    @Override
    public boolean equals(Object o) {
    	//uh-oh equals is inconsistent with compareTo
    	if (o instanceof SearchResult)
    		return result == ((SearchResult) o).result;
    	else
    		return super.equals(o);
    }


    @Override
    public String toString() {
        return String.format("%d (%f)", result, distance);
    }
}