package fortscale.collection.services;


import fortscale.collection.jobs.activity.UserActivityBaseHandler;
import fortscale.collection.jobs.activity.UserActivityType;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service("userActivityClassificationExposureService")
public class UserActivityClassificationExposureService extends BaseUserActivityConfigurationService implements InitializingBean {

    private static final Logger logger = Logger.getLogger(UserActivityClassificationExposureService.class);

    private static final String USER_ACTIVITY_CLASSIFICATION_EXPOSURE_CONFIGURATION_KEY = "system.user_activity.classification_exposure";

    @PostConstruct
    public void init() throws Exception {
        activityDataSourceConfigurationMap.put("dlpfile", new UserActivityDataSourceConfiguration("dlpfile",
                "aggr_normalized_username_dlpfile_daily",
                UserActivityBaseHandler.AGGREGATED_FEATURES_PREFIX,
                UserActivityType.CLASSIFICATION_EXPOSURE.name()));

    }

    @Override
    public String getActivityName() {
        return UserActivityType.CLASSIFICATION_EXPOSURE.name();
    }

    @Override
    protected String getConfigurationKey() {
        return USER_ACTIVITY_CLASSIFICATION_EXPOSURE_CONFIGURATION_KEY;
    }

    public Map<String, UserActivityDataSourceConfiguration> getActivityDataSourceConfigurationMap() {
        return activityDataSourceConfigurationMap;
    }
}
