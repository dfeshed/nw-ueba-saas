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
	private final UserActivityDataUsageHandler userActivityDataUsageHandler;

    @Autowired
    public UserActivityHandlerFactory(UserActivityLocationsHandler userActivityLocationsHandler,
			UserActivityNetworkAuthenticationHandler userActivityNetworkAuthenticationHandler,
			UserActivityDataUsageHandler userActivityDataUsageHandler) {
        this.userActivityLocationsHandler = userActivityLocationsHandler;
        this.userActivityNetworkAuthenticationHandler = userActivityNetworkAuthenticationHandler;
		this.userActivityDataUsageHandler = userActivityDataUsageHandler;
    }

    public UserActivityHandler createUserActivityHandler(String activityName) {
		UserActivityType activityType = UserActivityType.valueOf(activityName.toUpperCase());
		switch (activityType) {
			case LOCATIONS: return userActivityLocationsHandler;
			case NETWORK_AUTHENTICATION: return userActivityNetworkAuthenticationHandler;
			case DATA_USAGE: return userActivityDataUsageHandler;
			default: throw new UnsupportedOperationException("Could not find activity of type " + activityType.name());
		}
    }

}