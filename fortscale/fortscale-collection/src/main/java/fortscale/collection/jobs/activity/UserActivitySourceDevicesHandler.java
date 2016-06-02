package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityLocationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public UserActivitySourceDevicesHandler(UserActivityLocationConfigurationService userActivityLocationConfigurationService, MongoTemplate mongoTemplate) {
        super(userActivityLocationConfigurationService, mongoTemplate);
    }

    @Override
    public void handle() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public String getActivityName() {
        return ACTIVITY_NAME;
    }
}
