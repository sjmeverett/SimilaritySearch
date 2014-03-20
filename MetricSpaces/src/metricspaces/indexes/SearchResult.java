package metricspaces.indexes;

/**
 * Represents a result from a search using a similarity index.  Results can be ordered by distance, shortest first.
 *
 * @author stewart
 */
public class SearchResult<ObjectType> implements Comparable<SearchResult<ObjectType>> {
    private ObjectType result;
    private double distance;

    /**
     * Constructor.
     * @param result The result found from the query.
     * @param distance The distance from the query object to the result object.
     */
    public SearchResult(ObjectType result, double distance) {
        this.result = result;
        this.distance = distance;
    }


    /**
     * Gets the value of the actual result.
     * @return
     */
    public ObjectType getResult() {
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
    public int compareTo(SearchResult<ObjectType> that) {
        return Double.compare(this.distance, that.distance);
    }


    @Override
    public String toString() {
        return String.format("%s(%f)", result.toString(), distance);
    }
}