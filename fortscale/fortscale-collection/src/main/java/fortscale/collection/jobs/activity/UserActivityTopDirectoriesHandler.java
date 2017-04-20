package fortscale.collection.jobs.activity;


import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityDataSourceConfiguration;
import fortscale.collection.services.UserActivityTopDirectoriesService;
import fortscale.domain.core.activities.UserActivityTopDirectoriesDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class UserActivityTopDirectoriesHandler extends UserActivityBaseHandler {
    
    private static final UserActivityType ACTIVITY = UserActivityType.TOP_DIRECTORIES;
    public static final String SRC_NETWORK_FOLDER_PATHS_HISTOGRAM_FEATURE_NAME = "src_network_folder_paths_histogram";

    @Autowired
    private UserActivityTopDirectoriesService userActivityTopDirectoriesService;

    @Override
    protected String getCollectionName() {
        return UserActivityTopDirectoriesDocument.COLLECTION_NAME;
    }

    @Override
    protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
        return new ArrayList<>(Collections.singletonList(SRC_NETWORK_FOLDER_PATHS_HISTOGRAM_FEATURE_NAME));
    }

    @Override
    public UserActivityType getActivity() {
        return ACTIVITY;
    }

    @Override
    protected UserActivityConfigurationService getUserActivityConfigurationService() {
        return userActivityTopDirectoriesService;
    }

    @Override
    protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
        final String dataSourceLowerCase = dataSource.toLowerCase();
        UserActivityDataSourceConfiguration conf = userActivityTopDirectoriesService.getActivityDataSourceConfigurationMap().get(dataSourceLowerCase);
        if (conf != null) {
            return new ArrayList<>(Collections.singletonList(conf.getFeatureName()));
        } else {
            throw new IllegalArgumentException("Invalid data source: " + dataSource);
        }
    }

    @Override
    protected List<Class> getRelevantDocumentClasses() {
        return new ArrayList<>(Collections.singletonList(UserActivityTopDirectoriesDocument.class));
    }

    @Override
    protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources, long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram) {
        //do nothing
    }
}
