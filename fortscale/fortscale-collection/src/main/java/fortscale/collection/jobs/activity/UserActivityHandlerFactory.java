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

    @Autowired
    public UserActivityHandlerFactory(UserActivityLocationsHandler userActivityLocationsHandler, UserActivityNetworkAuthenticationHandler userActivityNetworkAuthenticationHandler) {
        this.userActivityLocationsHandler = userActivityLocationsHandler;
        this.userActivityNetworkAuthenticationHandler = userActivityNetworkAuthenticationHandler;
    }

    public UserActivityHandler createUserActivityHandler(String activityName) {
        if (UserActivityType.LOCATIONS.name().equalsIgnoreCase(activityName)) {
            return userActivityLocationsHandler;
        }
        else if (UserActivityType.NETWORK_AUTHENTICATION.name().equalsIgnoreCase(activityName)) {
            return userActivityNetworkAuthenticationHandler;
        }
        else {
            throw new UnsupportedOperationException("Could not find activity of type " + activityName);
        }
    }

}
