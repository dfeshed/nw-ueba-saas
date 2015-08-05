package fortscale.aggregation.feature.services;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketsStore;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.domain.core.HistogramDualKey;
import fortscale.domain.core.HistogramKey;
import fortscale.domain.core.HistogramSingleKey;
import fortscale.domain.core.SupportingInformationData;
import fortscale.utils.TimestampUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gils
 * Date: 05/08/2015
 */
public class SupportingInformationHeatMapDataPopulator extends SupportingInformationDataBasePopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationHeatMapDataPopulator.class);

    public SupportingInformationHeatMapDataPopulator(String contextType, String dataEntity, String featureName, BucketConfigurationService bucketConfigurationService, FeatureBucketsStore featureBucketsStore) {
        super(contextType, dataEntity, featureName, bucketConfigurationService, featureBucketsStore);
    }

    public SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime, int timePeriodInDays, String anomalyValue) {

        List<FeatureBucket> featureBuckets = fetchRelevantFeatureBuckets(contextValue, evidenceEndTime, timePeriodInDays);

        Map<HistogramKey, Double> histogramKeyObjectMap = new HashMap<>();

        for (FeatureBucket featureBucket : featureBuckets) {

            Feature feature = featureBucket.getAggregatedFeatures().get(featureName);

            if (feature == null) {
                logger.warn("Cannot find feature {} in feature bucket with ID {}", featureName, featureBucket.getBucketId());
                continue;
            }

            Object featureValue = feature.getValue();

            long bucketStartTime = featureBucket.getStartTime();

            Integer dayOfWeek = TimeUtils.getDayOfWeek(TimestampUtils.convertToMilliSeconds(bucketStartTime));

            if (featureValue instanceof GenericHistogram) {
                Map<Object, Double> histogramMap = ((GenericHistogram) featureValue).getHistogramMap();

                for (Map.Entry<Object, Double> histogramEntry : histogramMap.entrySet()) {
                    Integer hour = (Integer) histogramEntry.getKey();
                    Double currValue = histogramEntry.getValue();

                    HistogramKey histogramKey = new HistogramDualKey(dayOfWeek.toString(), hour.toString());

                    Double currHistogramValue = histogramKeyObjectMap.get(histogramKey);

                    histogramKeyObjectMap.put(histogramKey, currHistogramValue + currValue);
                }

            } else {
                // TODO is this considered illegal state? for now don't use the value and continue;
                logger.warn("Cannot find histogram data for feature {} in bucket id {}", featureName, featureBucket.getBucketId());
            }
        }

        // TODO adjust to dual key
        return new SupportingInformationData(histogramKeyObjectMap, new HistogramDualKey(anomalyValue, anomalyValue));
    }
}
