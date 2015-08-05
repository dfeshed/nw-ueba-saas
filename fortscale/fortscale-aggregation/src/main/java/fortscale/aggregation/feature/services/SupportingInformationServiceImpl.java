package fortscale.aggregation.feature.services;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.domain.core.HistogramKey;
import fortscale.domain.core.HistogramDualKey;
import fortscale.domain.core.HistogramSingleKey;
import fortscale.domain.core.SupportingInformationData;
import fortscale.utils.TimestampUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service implementation to provide Supporting Information data
 *
 * @author gils
 * Date: 29/07/2015
 */
public class SupportingInformationServiceImpl implements SupportingInformationService {

    private static Logger logger = Logger.getLogger(SupportingInformationServiceImpl.class);

    @Autowired
    private BucketConfigurationService bucketConfigurationService;

    @Autowired
    private FeatureBucketsStore featureBucketsStore;

    @Override
    public SupportingInformationData getEvidenceSupportingInformationData(String contextType, String contextValue, String dataEntity, String featureName,
                                                                          String anomalyType, String anomalyValue, long evidenceEndTime, int timePeriodInDays, String aggregationFunction) {
        logger.info("Going to calculate Evidence Supporting Information. Context type = {} # Context value = {} # Data entity = {} " +
                "# Feature name = {} # Evidence end time = {} # Aggregation function = {} # Time period = {} days..", contextType, contextValue, dataEntity, featureName, TimeUtils.getFormattedTime(evidenceEndTime), aggregationFunction, timePeriodInDays);

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

        SupportingInformationPopulator supportingInformationPopulator = SupportingInformationPopulatorFactory.createSupportingInformationPopulator(aggregationFunction);

        return supportingInformationPopulator.createSupportingInformationData(featureBuckets, featureName, anomalyValue, aggregationFunction);
    }

    private String findBucketConfigurationName(String contextType, String dataEntity) {
        // TODO need to generify
        return contextType + "_" + dataEntity + "_daily";
    }
}
