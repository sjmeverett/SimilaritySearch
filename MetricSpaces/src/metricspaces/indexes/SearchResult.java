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
public class SearchResult<ObjectType> implements Comparable<SearchResult<ObjectType>> {
    private ObjectType result;
    private double distance;
    private int resultIndex;

    /**
     * Constructor.
     * @param result The result found from the query.
     * @param distance The distance from the query object to the result object.
     * @param resultIndex The index of the result in the descriptor file it was retrieved from.
     */
    public SearchResult(ObjectType result, double distance, int resultIndex) {
        this.result = result;
        this.distance = distance;
        this.resultIndex = resultIndex;
    }


    /**
     * Gets the value of the actual result.
     * @return
     */
    public ObjectType getResult() {
        return result;
    }
    
    
    /**
     * Gets the index of the result in the descriptor file it was retrieved from.
     * @return
     */
    public int getResultIndex() {
    	return resultIndex;
    }


    /**
     * Gets the distance from the query object to the result object.
     * @return
     */
    public double getDistance() {
        return distance;
    }


    @Override
    public int compareTo(SearchResult<ObjectType> that) {
        return Double.compare(this.distance, that.distance);
    }
    
    @Override
    public boolean equals(Object o) {
    	//uh-oh equals is inconsistent with compareTo
    	if (o instanceof SearchResult)
    		return result.equals(((SearchResult) o).result);
    	else
    		return super.equals(o);
    }


    @Override
    public String toString() {
        return String.format("%s (%f)", result.toString(), distance);
    }
}