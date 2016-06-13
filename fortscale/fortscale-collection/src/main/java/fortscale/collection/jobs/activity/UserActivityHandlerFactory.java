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
    private final UserActivitySourceMachineHandler userActivitySourceMachineHandler;

    @Autowired
    public UserActivityHandlerFactory(UserActivityLocationsHandler userActivityLocationsHandler,
                                      UserActivityNetworkAuthenticationHandler userActivityNetworkAuthenticationHandler,
                                      UserActivitySourceMachineHandler userActivitySourceMachineHandler) {

        this.userActivityLocationsHandler = userActivityLocationsHandler;
        this.userActivityNetworkAuthenticationHandler = userActivityNetworkAuthenticationHandler;
        this.userActivitySourceMachineHandler = userActivitySourceMachineHandler;
    }

    public UserActivityHandler createUserActivityHandler(String activityName) {

        UserActivityType activityType;
        try {
            activityType = UserActivityType.valueOf(activityName);
        } catch (Exception e){
            throw new UnsupportedOperationException("Could not find activity of type " + activityName);
        }

        switch (activityType){
            case LOCATIONS:                 return userActivityLocationsHandler;
            case NETWORK_AUTHENTICATION:    return userActivityNetworkAuthenticationHandler;
            case SOURCE_MACHINE:            return userActivitySourceMachineHandler;

            default: throw new UnsupportedOperationException("Could not find activity of type " + activityName);
        }

    }

}

