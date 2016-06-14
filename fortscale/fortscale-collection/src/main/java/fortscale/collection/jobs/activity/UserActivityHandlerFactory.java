package fortscale.collection.jobs.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gils
 * 31/05/2016
 */
@Component
public class UserActivityHandlerFactory {

    private final UserActivityLocationsHandler userActivityLocationsHandler;
    private final UserActivityNetworkAuthenticationHandler userActivityNetworkAuthenticationHandler;
    private final UserActivityWorkingHoursHandler userActivityWorkingHoursHandler;

    @Autowired
    public UserActivityHandlerFactory(UserActivityLocationsHandler userActivityLocationsHandler, UserActivityNetworkAuthenticationHandler userActivityNetworkAuthenticationHandler, UserActivityWorkingHoursHandler userActivityWorkingHoursHandler) {
        this.userActivityLocationsHandler = userActivityLocationsHandler;
        this.userActivityNetworkAuthenticationHandler = userActivityNetworkAuthenticationHandler;
        this.userActivityWorkingHoursHandler = userActivityWorkingHoursHandler;
    }

    public UserActivityHandler createUserActivityHandler(String activityName) {
        if (UserActivityType.LOCATIONS.name().equalsIgnoreCase(activityName)) {
            return userActivityLocationsHandler;
        }
        else if (UserActivityType.NETWORK_AUTHENTICATION.name().equalsIgnoreCase(activityName)) {
            return userActivityNetworkAuthenticationHandler;
        }
        else if (UserActivityType.WORKING_HOURS.name().equalsIgnoreCase(activityName)) {
            return userActivityWorkingHoursHandler;
        }
        else {
            throw new UnsupportedOperationException("Could not find activity of type " + activityName);
        }
    }

}
