package metricspaces;

/**
 * Encapsulates the progress of a task.
 *
 * @author stewart
 */
public class Progress {
    private int total, done;
    private String operation;


    public Progress() {
        operation = "";
    }


    /**
     * Sets the total (i.e., what constitutes 100%).
     * @param total
     */
    public void setTotal(int total) {
        this.total = total;
    }


    /**
     * Sets a description of the current operation.
     *
     * @param operation
     */
    public void setOperation(String operation) {
        this.operation = operation;
        this.done = 0;
    }


    /**
     * Sets the description of the current operation and the total.
     *
     * @param operation
     * @param total
     */
    public void setOperation(String operation, int total) {
        setOperation(operation);
        setTotal(total);
    }


    /**
     * Increments the amount done.
     */
    public void incrementDone() {
        done++;
    }


    /**
     * Resets the amount done to zero.
     */
    public void resetDone() {
        done = 0;
    }


    /**
     * Gets the description of the current operation.
     * @return
     */
    public String getOperation() {
        return operation;
    }


    /**
     * Returns the amount done.
     * @return
     */
    public int getDone() {
        return done;
    }


    /**
     * Sets the amount done.
     * @param done
     */
    public void setDone(int done) {
        this.done = done;
    }


    /**
     * Gets the total.
     *
     * @return
     */
    public int getTotal() {
        return total;
    }


    /**
     * Gets the fraction done.
     * @return
     */
    public double getFractionDone() {
        return (double)done / total;
    }
}