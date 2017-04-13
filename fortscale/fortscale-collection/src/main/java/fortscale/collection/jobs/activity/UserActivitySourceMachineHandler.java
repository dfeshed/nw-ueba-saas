package fortscale.collection.jobs.activity;

import fortscale.collection.services.UserActivityConfigurationService;
import fortscale.collection.services.UserActivityDataSourceConfiguration;
import fortscale.collection.services.UserActivitySourceMachineConfigurationService;
import fortscale.domain.core.User;
import fortscale.domain.core.activities.UserActivitySourceMachineDocument;
import fortscale.services.UserActivityService;
import fortscale.services.users.util.UserAndOrganizationActivityHelper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component
public class UserActivitySourceMachineHandler extends UserActivityBaseHandler {


    private static final String SOURCE_MACHINE_HISTOGRAM_FEATURE_NAME = "normalized_src_machine_histogram";
    private static final UserActivityType ACTIVITY = UserActivityType.SOURCE_MACHINE;

    @Autowired
    private UserActivitySourceMachineConfigurationService userActivitySourceMachineConfigurationService;

    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    public UserAndOrganizationActivityHelper userAndOrganizationActivityHelper;

    @Override
    protected List<String> getRelevantFields(String dataSource) throws IllegalArgumentException {
        final String dataSourceLowerCase = dataSource.toLowerCase();
        UserActivityDataSourceConfiguration conf = userActivitySourceMachineConfigurationService.getActivityDataSourceConfigurationMap().get(dataSourceLowerCase);
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
        return new ArrayList<>(Collections.singletonList(UserActivitySourceMachineDocument.class));
    }

    @Override
    protected void updateAdditionalActivitySpecificDocumentInDatabase(List<String> dataSources,
                                                                      long currBucketStartTime, long currBucketEndTime, Map<String, Double> additionalActivityHistogram) {
        //do nothing
    }

    @Override
    protected String getCollectionName() {
        return UserActivitySourceMachineDocument.COLLECTION_NAME;
    }

    @Override
    protected List<String> getRelevantAggregatedFeaturesFieldsNames() {
        return new ArrayList<>(Collections.singletonList(SOURCE_MACHINE_HISTOGRAM_FEATURE_NAME));
    }

    @Override
    public UserActivityType getActivity() {
        return ACTIVITY;
    }

    @Override
    protected UserActivityConfigurationService getUserActivityConfigurationService() {
        return userActivitySourceMachineConfigurationService;
    }

    /**
     * Update the User collection with the count of distinct source devices each user uses
     */
    @Override
    public void postCalculation() {
        // Get all the users
        List<ObjectId> userIds = userService.getDistinctValuesByFieldName(User.ID_FIELD);

        userIds.forEach(userId -> {

            // Get all the source machines for each user
            List<UserActivitySourceMachineDocument> userActivitySourceMachineEntries
                    = userActivityService.getUserActivitySourceMachineEntries(userId.toString(), Integer.MAX_VALUE);
            Set<String> machines = new HashSet<>();

            userActivitySourceMachineEntries.forEach(userActivitySourceMachineDocument -> {
                machines.addAll(userActivitySourceMachineDocument.getMachines().getMachinesHistogram().keySet());
            });

            // Remove irrelevant values
            machines.removeAll(userAndOrganizationActivityHelper.getDeviceValuesToFilter());
            machines.remove(userAndOrganizationActivityHelper.OTHER_MACHINE_VALUE);

            // Update the user document with the number
            userService.updateSourceMachineCount(userId.toString(), machines.size());
        });
    }
}