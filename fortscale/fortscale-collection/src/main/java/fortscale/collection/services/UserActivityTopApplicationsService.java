package fortscale.collection.services;


import fortscale.collection.jobs.activity.UserActivityBaseHandler;
import fortscale.collection.jobs.activity.UserActivityType;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service("userActivityTopApplicationsService")
public class UserActivityTopApplicationsService extends BaseUserActivityConfigurationService implements InitializingBean {

    private static final Logger logger = Logger.getLogger(UserActivityTopApplicationsService.class);

    private static final String USER_ACTIVITY_TOP_APPLICATIONS_CONFIGURATION_KEY = "system.user_activity.top_applications";

    public UserActivityTopApplicationsService() throws Exception {
        activityDataSourceConfigurationMap.put("dlpmail", new UserActivityDataSourceConfiguration("dlpmail",
                "aggr_normalized_username_dlpmail_daily",
                UserActivityBaseHandler.AGGREGATED_FEATURES_PREFIX,
                UserActivityType.TOP_APPLICATIONS.name()));


        activityDataSourceConfigurationMap.put("dlpfile", new UserActivityDataSourceConfiguration("dlpfile",
                "aggr_normalized_username_dlpfile_daily",
                UserActivityBaseHandler.AGGREGATED_FEATURES_PREFIX,
                UserActivityType.TOP_APPLICATIONS.name()));
    }

    @Override
    public String getActivityName() {
        return UserActivityType.TOP_APPLICATIONS.name();
    }

    @Override
    protected String getConfigurationKey() {
        return USER_ACTIVITY_TOP_APPLICATIONS_CONFIGURATION_KEY;
    }

    public Map<String, UserActivityDataSourceConfiguration> getActivityDataSourceConfigurationMap() {
        return activityDataSourceConfigurationMap;
    }
}
