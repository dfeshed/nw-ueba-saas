package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityDataSourceConfiguration;
import fortscale.collection.services.UserActivityDlpTopApplicationsService;
import fortscale.domain.core.activities.UserActivityDlpTopApplicationsDocument;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserActivityDlpTopApplicationsHandler extends UserActivityBaseHandler {

    private static final UserActivityType ACTIVITY = UserActivityType.DLP_TOP_APPLICATIONS;
    public static final String APPLICATION_HISTOGRAM_FEATURE_NAME = "application_histogram";

    @Autowired
    private UserActivityDlpTopApplicationsService userActivityDlpTopApplicationsConfigurationService;

    @Override
    protected String getCollectionName() {
        return UserActivityDlpTopApplicationsDocument.COLLECTION_NAME;
    }

    @Override
    protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
        return new ArrayList<>(Collections.singletonList(APPLICATION_HISTOGRAM_FEATURE_NAME));
    }

    @Override
    public UserActivityType getActivity() {
        return ACTIVITY;
    }

    @Override
    protected UserActivityConfigurationService getUserActivityConfigurationService() {
        return userActivityDlpTopApplicationsConfigurationService;
    }

    @Override
    protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
        final String dataSourceLowerCase = dataSource.toLowerCase();
        UserActivityDataSourceConfiguration conf = userActivityDlpTopApplicationsConfigurationService.getActivityDataSourceConfigurationMap().get(dataSourceLowerCase);
        if (conf != null) {
            return new ArrayList<>(Collections.singletonList(conf.getFeatureName()));
        } else {
            throw new IllegalArgumentException("Invalid data source: " + dataSource);
        }
    }

    @Override
    protected List<Class> getRelevantDocumentClasses() {
        return new ArrayList<>(Collections.singletonList(UserActivityDlpTopApplicationsDocument.class));
    }

    @Override
    protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram) {
        //do nothing
    }
}
