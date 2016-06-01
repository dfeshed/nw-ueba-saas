package fortscale.collection.jobs.activity;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author gils
 * 31/05/2016
 */
public class UserActivityHandlerFactory implements ApplicationContextAware{

    private ApplicationContext applicationContext;

    private static final String USER_ACTIVITY_LOCATIONS_HANDLER = "userActivityLocationsHandler";

    public UserActivityLocationsHandler createUserActivityHandler(String activityName) {
        if (UserActivityType.LOCATIONS.name().equalsIgnoreCase(activityName)) {
            return (UserActivityLocationsHandler) applicationContext.getBean(USER_ACTIVITY_LOCATIONS_HANDLER);
        }
        else {
            throw new UnsupportedOperationException("Could not find activity of type " + activityName);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
