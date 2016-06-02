package fortscale.collection.jobs.activity;

/**
 * @author gils
 * 24/05/2016
 */
public interface UserActivityHandler {
    void handle();
    String getActivityName();
}
