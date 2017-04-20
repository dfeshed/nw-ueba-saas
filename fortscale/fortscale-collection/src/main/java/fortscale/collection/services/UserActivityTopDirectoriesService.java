package fortscale.collection.services;


import fortscale.collection.jobs.activity.UserActivityBaseHandler;
import fortscale.collection.jobs.activity.UserActivityType;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service("userActivityTopDirectoriesService")
public class UserActivityTopDirectoriesService extends BaseUserActivityConfigurationService implements InitializingBean {

    private static final Logger logger = Logger.getLogger(UserActivityTopDirectoriesService.class);

    private static final String USER_ACTIVITY_TOP_DIRECTORIES_CONFIGURATION_KEY = "system.user_activity.top_directories";

    @PostConstruct
    public void init() throws Exception {
        activityDataSourceConfigurationMap.put("dlpfile", new UserActivityDataSourceConfiguration("dlpfile",
                "aggr_normalized_username_dlpfile_daily",
                UserActivityBaseHandler.AGGREGATED_FEATURES_PREFIX,
                UserActivityType.TOP_DIRECTORIES.name()));

    }

    @Override
    public String getActivityName() {
        return UserActivityType.TOP_DIRECTORIES.name();
    }

    @Override
    protected String getConfigurationKey() {
        return USER_ACTIVITY_TOP_DIRECTORIES_CONFIGURATION_KEY;
    }

    public Map<String, UserActivityDataSourceConfiguration> getActivityDataSourceConfigurationMap() {
        return activityDataSourceConfigurationMap;
    }
}
