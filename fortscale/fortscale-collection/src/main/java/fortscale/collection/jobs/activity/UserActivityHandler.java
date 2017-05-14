package fortscale.collection.jobs.activity;

/**
 * Interface for user activity calculations
 *
 */
public interface UserActivityHandler {
    /**
     * calculates the user activity
     *
     * @param numOfLastDaysToCalculate the num of last days to calculate
     */
    void calculate(int numOfLastDaysToCalculate);

    UserActivityType getActivity();
    /**
     * Runs all needs to be done after the calculation finished
     */
    void postCalculation();
}
