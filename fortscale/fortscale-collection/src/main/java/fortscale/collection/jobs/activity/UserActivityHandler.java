package fortscale.collection.jobs.activity;

/**
 * Interface for user activity calculations
 *
 * @author gils
 * 24/05/2016
 */
public interface UserActivityHandler {
    /**
     * calculates the user activity
     *
     * @param numOfLastDaysToCalculate the num of last days to calculate
     */
    void calculate(int numOfLastDaysToCalculate);
}
