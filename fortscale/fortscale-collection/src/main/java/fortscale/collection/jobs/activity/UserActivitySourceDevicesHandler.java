package fortscale.collection.jobs.activity;

import fortscale.utils.logging.Logger;

/**
 * User activity source devices handler implementation
 *
 * @author gils
 * 31/05/2016
 */
public class UserActivitySourceDevicesHandler extends UserActivityBaseHandler {
    private static Logger logger = Logger.getLogger(UserActivitySourceDevicesHandler.class);

    private static final String ACTIVITY_NAME = "source_devices";

    private static final String AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME = "aggregatedFeatures.source_machines";
    private static final String COUNTRY_HISTOGRAM_FEATURE_NAME = "src_machines_histogram";

    public void calculate() {
    }

    @Override
    public String getActivityName() {
        return ACTIVITY_NAME;
    }
}
