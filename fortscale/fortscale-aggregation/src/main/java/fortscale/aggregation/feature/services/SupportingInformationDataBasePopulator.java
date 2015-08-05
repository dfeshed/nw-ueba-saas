package fortscale.aggregation.feature.services;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;

import java.util.List;

/**
 * @author gils
 * Date: 05/08/2015
 */
public abstract class SupportingInformationDataBasePopulator implements SupportingInformationDataPopulator{

    private static Logger logger = Logger.getLogger(SupportingInformationDataBasePopulator.class);

    protected String contextType;
    protected String dataEntity;
    protected String featureName;
    protected BucketConfigurationService bucketConfigurationService;
    protected FeatureBucketsStore featureBucketsStore;

    public SupportingInformationDataBasePopulator(String contextType, String dataEntity, String featureName, BucketConfigurationService bucketConfigurationService, FeatureBucketsStore featureBucketsStore) {
        this.contextType = contextType;
        this.dataEntity = dataEntity;
        this.featureName = featureName;
        this.bucketConfigurationService = bucketConfigurationService;
        this.featureBucketsStore = featureBucketsStore;
    }

    protected List<FeatureBucket> fetchRelevantFeatureBuckets(String contextValue, long evidenceEndTime, int timePeriodInDays) {
        String bucketConfigName = findBucketConfigurationName(contextType, dataEntity);

        FeatureBucketConf bucketConfig = bucketConfigurationService.getBucketConf(bucketConfigName);

        if (bucketConfig != null) {
            logger.info("Using bucket configuration {} with strategy {}", bucketConfig.getName(), bucketConfig.getStrategyName());
        }
        else {
            throw new SupportingInformationException("Could not find Bucket configuration with name " + bucketConfigName);
        }

        Long supportingInformationStartTime = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);

        List<FeatureBucket> featureBuckets = featureBucketsStore.getFeatureBuckets(bucketConfig, contextType, contextValue, featureName, supportingInformationStartTime, evidenceEndTime);

        logger.info("Found {} relevant featureName buckets", featureBuckets.size());

        return featureBuckets;
    }

    protected String findBucketConfigurationName(String contextType, String dataEntity) {
        return contextType + "_" + dataEntity + "_daily";
    }
}
