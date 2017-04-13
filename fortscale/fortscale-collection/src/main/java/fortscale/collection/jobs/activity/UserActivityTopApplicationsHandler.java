package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityDataSourceConfiguration;
import fortscale.collection.services.UserActivityTopApplicationsService;
import fortscale.domain.core.activities.UserActivityTopApplicationsDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configurable(preConstruction = true)
@Component
public class UserActivityTopApplicationsHandler extends UserActivityBaseHandler {

    private static final UserActivityType ACTIVITY = UserActivityType.TOP_APPLICATIONS;
    public static final String EXECUTING_APPLICATION_HISTOGRAM_FEATURE_NAME = "executing_application_histogram";

    @Autowired
    private UserActivityTopApplicationsService userActivityTopApplicationsConfigurationService;

    @Override
    protected String getCollectionName() {
        return UserActivityTopApplicationsDocument.COLLECTION_NAME;
    }

    @Override
    protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
        return new ArrayList<>(Collections.singletonList(EXECUTING_APPLICATION_HISTOGRAM_FEATURE_NAME));
    }

    @Override
    public UserActivityType getActivity() {
        return ACTIVITY;
    }

    @Override
    protected UserActivityConfigurationService getUserActivityConfigurationService() {
        return userActivityTopApplicationsConfigurationService;
    }

    @Override
    protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
        final String dataSourceLowerCase = dataSource.toLowerCase();
        UserActivityDataSourceConfiguration conf = userActivityTopApplicationsConfigurationService.getActivityDataSourceConfigurationMap().get(dataSourceLowerCase);
        if (conf != null) {
            return new ArrayList<>(Collections.singletonList(conf.getFeatureName()));
        } else {
            throw new IllegalArgumentException("Invalid data source: " + dataSource);
        }
    }

    @Override
    protected List<Class> getRelevantDocumentClasses() {
        return new ArrayList<>(Collections.singletonList(UserActivityTopApplicationsDocument.class));
    }

    @Override
    protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram) {
        //do nothing
    }
}
