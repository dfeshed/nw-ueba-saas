package fortscale.aggregation.feature.services;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.domain.histogram.HistogramDualKey;
import fortscale.domain.histogram.HistogramKey;
import fortscale.domain.core.SupportingInformationData;
import fortscale.utils.TimestampUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gils
 * Date: 05/08/2015
 */
@Component
@Scope("prototype")
public class SupportingInformationHeatMapDataPopulator extends SupportingInformationDataBasePopulator {

    private static final int HOUR_UPPER_BOUND = 23;
    private static final int HOUR_LOWER_BOUND = 0;
    private static Logger logger = Logger.getLogger(SupportingInformationHeatMapDataPopulator.class);

    public SupportingInformationHeatMapDataPopulator(String contextType, String dataEntity, String featureName) {
        super(contextType, dataEntity, featureName);
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

                    if (isHourValueOutOfRange(hour)) {
                        throw new IllegalStateException("Hour value is out of range - " + hour);
                    }

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

    private boolean isHourValueOutOfRange(Integer hour) {
        return hour > HOUR_UPPER_BOUND || hour < HOUR_LOWER_BOUND;
    }
}
