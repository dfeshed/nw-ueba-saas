package fortscale.aggregation.feature.services;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;

/**
 * @author gils
 * Date: 05/08/2015
 */
public class SupportingInformationPopulatorFactory {

    private static final String COUNT_AGGREGATION_FUNC = "Count";
    private static final String HOURLY_COUNT_BY_GROUP_BY_DAY_OF_WEEK_AGGREGATION_FUNC = "hourlyCountGroupByDayOfWeek";

    public static SupportingInformationDataPopulator createSupportingInformationPopulator(String contextType, String dataEntity, String featureName, String aggregationFunction, BucketConfigurationService bucketConfigurationService, FeatureBucketsStore featureBucketsStore) {
        if (COUNT_AGGREGATION_FUNC.equalsIgnoreCase(aggregationFunction.toLowerCase())) {
            return new SupportingInformationDataCountPopulator(contextType, dataEntity, featureName, bucketConfigurationService, featureBucketsStore);
        }
        else if (HOURLY_COUNT_BY_GROUP_BY_DAY_OF_WEEK_AGGREGATION_FUNC.equalsIgnoreCase(aggregationFunction)) {
            return new SupportingInformationHeatMapDataPopulator(contextType, dataEntity, featureName, bucketConfigurationService, featureBucketsStore);
        }
        else {
            throw new UnsupportedOperationException("Aggregation function " + aggregationFunction + " is not supported");
        }
    }
}
