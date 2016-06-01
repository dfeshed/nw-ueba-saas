package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author gils
 * 31/05/2016
 */
public class UserActivitySourceDevicesHandler extends UserActivityBaseHandler {
    private static Logger logger = Logger.getLogger(UserActivitySourceDevicesHandler.class);

    private static final String ACTIVITY_NAME = "source_devices";

    private static final String AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME = "aggregatedFeatures.source_machines";
    private static final String COUNTRY_HISTOGRAM_FEATURE_NAME = "src_machines_histogram";

    public void handle(long startTime, long endTime, UserActivityConfigurationService userActivityConfigurationService1, MongoTemplate mongoTemplate1) {
    }

    @Override
    public String getActivityName() {
        return ACTIVITY_NAME;
    }
}
