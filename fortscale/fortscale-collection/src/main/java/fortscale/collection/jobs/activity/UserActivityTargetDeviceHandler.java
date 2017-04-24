package fortscale.collection.jobs.activity;

import fortscale.collection.services.useractivity.UserActivityConfigurationService;
import fortscale.collection.services.useractivity.UserActivityDataSourceConfiguration;
import fortscale.collection.services.useractivity.UserActivityTargetDeviceConfigurationService;
import fortscale.domain.core.activities.UserActivityTargetDeviceDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class UserActivityTargetDeviceHandler extends UserActivityBaseHandler {


    private static final String TARGET_DEVICE_HISTOGRAM_FEATURE_NAME = "normalized_dst_machine_histogram";
    private static final UserActivityType ACTIVITY = UserActivityType.TARGET_DEVICE;

    @Autowired
    private UserActivityTargetDeviceConfigurationService userActivityTargetDeviceConfigurationService;

    @Override
    protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
        final String dataSourceLowerCase = dataSource.toLowerCase();
        UserActivityDataSourceConfiguration conf = userActivityTargetDeviceConfigurationService.getActivityDataSourceConfigurationMap().get(dataSourceLowerCase);
        if (conf != null) {
            return new ArrayList<>(Collections.singletonList(conf.getFeatureName()));
        } else {
            throw new IllegalArgumentException("Invalid data source: " + dataSource);
        }
    }

    @Override
    Function<Double, Double> valueReducer() {
        return (newValue) -> 1.0;
    }

    @Override
    protected List<Class> getRelevantDocumentClasses() {
        return new ArrayList<>(Collections.singletonList(UserActivityTargetDeviceDocument.class));
    }

    @Override
    protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram) {
        //do nothing
    }

    @Override
    protected String getCollectionName() {
        return UserActivityTargetDeviceDocument.COLLECTION_NAME;
    }

    @Override
    protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
        return new ArrayList<>(Collections.singletonList(TARGET_DEVICE_HISTOGRAM_FEATURE_NAME));
    }

    @Override
    public UserActivityType getActivity() {
        return ACTIVITY;
    }

    @Override
    protected UserActivityConfigurationService getUserActivityConfigurationService() {
        return userActivityTargetDeviceConfigurationService;
    }
}
