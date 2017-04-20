package fortscale.collection.services;


import fortscale.collection.jobs.activity.UserActivityBaseHandler;
import fortscale.collection.jobs.activity.UserActivityType;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("userActivityEmailRecipientDomainService")
public class UserActivityEmailRecipientDomainService extends BaseUserActivityConfigurationService implements InitializingBean {

    private static final Logger logger = Logger.getLogger(UserActivityEmailRecipientDomainService.class);

    private static final String USER_ACTIVITY_EMAIL_RECIPIENT_DOMAIN_CONFIGURATION_KEY = "system.user_activity.email_recipient_domain";
    private Map<String, UserActivityDataSourceConfiguration> activityDataSourceConfigurationMap = new HashMap<>();

    @PostConstruct
    public void init() throws Exception {
        activityDataSourceConfigurationMap.put("dlpmail", new UserActivityDataSourceConfiguration("dlpmail",
                "aggr_normalized_username_dlpmail_daily",
                UserActivityBaseHandler.AGGREGATED_FEATURES_PREFIX,
                UserActivityType.EMAIL_RECIPIENT_DOMAIN.name()));
    }

    @Override
    public String getActivityName() {
        return UserActivityType.EMAIL_RECIPIENT_DOMAIN.name();
    }

    @Override
    protected String getConfigurationKey() {
        return USER_ACTIVITY_EMAIL_RECIPIENT_DOMAIN_CONFIGURATION_KEY;
    }

    @Override
    public UserActivityConfiguration createUserActivityConfiguration() {
        final Set<String> activities = new HashSet<>();
        final Map<String, String> dataSourceToCollection = new HashMap<>();
        final Map<String, List<String>> activityToDataSources = new HashMap<>();

        for (UserActivityDataSourceConfiguration activity : activityDataSourceConfigurationMap.values()) {
            activities.add(activity.getPropertyName());
            dataSourceToCollection.put(activity.getDatasource(), activity.getCollectionName());
            activityToDataSources.put(activity.getPropertyName(), new ArrayList<>(Collections.singletonList(activity.getDatasource())));
        }

        return new UserActivityConfiguration(activities, dataSourceToCollection, activityToDataSources);
    }

    public Map<String, UserActivityDataSourceConfiguration> getActivityDataSourceConfigurationMap() {
        return activityDataSourceConfigurationMap;
    }
}
