package fortscale.collection.jobs.activity;

/**
 * @author gils
 * 31/05/2016
 */
public class UserActivityHandlerFactory {

    public UserActivityLocationsHandler createUserActivityHandler(String activityName) {
        if (UserActivityType.LOCATIONS.name().equalsIgnoreCase(activityName)) {
            return new UserActivityLocationsHandler();
        }
        else {
            throw new UnsupportedOperationException("Could not find activity of type " + activityName);
        }
    }
}
