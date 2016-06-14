package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.logging.Logger;

import java.util.List;
import java.util.Map;

/**
 * User activity source devices handler implementation
 *
 * @author gils
 * 31/05/2016
 */
public class UserActivitySourceDevicesHandler extends UserActivityBaseHandler {

    private static final String ACTIVITY_NAME = "source_devices";
    private static final String AGGREGATED_FEATURES_COUNTRY_HISTOGRAM_FIELD_NAME = "aggregatedFeatures.source_machines";
    private static final String COUNTRY_HISTOGRAM_FEATURE_NAME = "src_machines_histogram";
    private static Logger logger = Logger.getLogger(UserActivitySourceDevicesHandler.class);

    @Override
    protected Logger getLogger() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected GenericHistogram convertFeatureToHistogram(Object objectToConvert, String histogramFeatureName) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected List<Class> getRelevantDocumentClasses() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected String getCollectionName() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public String getActivityName() {
        return ACTIVITY_NAME;
    }

    @Override
    protected UserActivityConfigurationService getUserActivityConfigurationService() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
