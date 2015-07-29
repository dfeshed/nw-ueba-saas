package fortscale.aggregation.feature.services;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.domain.core.SupportingInformationData;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service implementation to provide Supporting Information data
 *
 * @author gils
 * Date: 29/07/2015
 */
@Component("supportingInformationService")
public class SupportingInformationServiceImpl implements SupportingInformationService {

    private static Logger logger = Logger.getLogger(SupportingInformationServiceImpl.class);

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    @Value("${evidence.supporting.information.time.period.in.months:3}")
    private int timePeriodInMonths;

    @Autowired
    private BucketConfigurationService bucketConfigurationService;

    @Autowired
    private FeatureBucketsStore featureBucketsStore;

    @Override
    public SupportingInformationData getEvidenceSupportingInformationData(String entityType, String entityName, String dataSource, String feature, Long evidenceTime) {
        logger.info("Going to fetch Evidence Supporting Information. Entity type = {} # Entity name = {} # Data source = {} " +
                "# Feature = {} # Evidence time = {} . Time period = {} months..", entityType, entityName, dataSource, feature, getFormattedTime(evidenceTime), timePeriodInMonths);

        String bucketConfigName = getBucketConfigurationName(entityType, dataSource);

        logger.debug("Bucket configuration name = " + bucketConfigName);

        FeatureBucketConf bucketConfig = bucketConfigurationService.getBucketConf(bucketConfigName);

        if (bucketConfig != null) {
            // TODO add toString method to FeatureBucketConf
            logger.info("Using bucket configuration {} with strategy " + bucketConfig.getName(), bucketConfig.getStrategyName());
        }
        else {
            throw new SupportingInformationException("Could not find Bucket configuration with name " + bucketConfigName);
        }

        Long supportingInformationStartTime = calculateSupportingInformationStartTime(evidenceTime, timePeriodInMonths);

        List<FeatureBucket> featureBuckets = featureBucketsStore.getFeatureBuckets(bucketConfig, entityType, entityName, feature, supportingInformationStartTime, evidenceTime);

        logger.info("Found {} relevant feature buckets", featureBuckets.size());

        SupportingInformationData supportingInformationData = populateSupportingInformationData(featureBuckets, feature);

        return supportingInformationData;
    }

    private String getFormattedTime(Long evidenceTime) {
        Calendar calInstance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calInstance.setTimeInMillis(evidenceTime);

        DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        return formatter.format(calInstance.getTime());
    }

    private Long calculateSupportingInformationStartTime(Long evidenceTime, int timePeriodInMonths) {
        Calendar calEvidenceTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calEvidenceTime.setTimeInMillis(evidenceTime);

        calEvidenceTime.add(Calendar.MONTH, -1 * timePeriodInMonths);

        return calEvidenceTime.getTimeInMillis();
    }

    private SupportingInformationData populateSupportingInformationData(List<FeatureBucket> featureBuckets, String featureName) {
        SupportingInformationData supportingInformationData = new SupportingInformationData();

        if (!featureBuckets.isEmpty()) {
            logger.info("Creating supporting information data");

            GenericHistogram supportingInformationHistogram = createSupportingInformationHistogram(featureBuckets, featureName);

            Map<Object, Double> histogramMap = supportingInformationHistogram.getHistogramMap();

            supportingInformationData.setHistogram(histogramMap);
        }

        return supportingInformationData;

    }

    private GenericHistogram createSupportingInformationHistogram(List<FeatureBucket> featureBuckets, String featureName) {
        GenericHistogram supportingInformationHistogram = new GenericHistogram();

        for (FeatureBucket featureBucket : featureBuckets) {
            Feature feature = featureBucket.getAggregatedFeatures().get(featureName);

            if (feature == null) {
                continue;
            }

            Object featureValue = feature.getValue();

            if (featureValue instanceof GenericHistogram) {
                supportingInformationHistogram.add((GenericHistogram) featureValue);
            } else {
                // TODO is this considered illegal state? for now don't use the value and continue;
                logger.warn("Cannot find histogram data for feature {} in bucket id " + featureName, featureBucket.getBucketId());
            }
        }

        return supportingInformationHistogram;
    }

    private String getBucketConfigurationName(String entityType, String dataSource) {
        return entityType + "_" + dataSource;
    }
}
