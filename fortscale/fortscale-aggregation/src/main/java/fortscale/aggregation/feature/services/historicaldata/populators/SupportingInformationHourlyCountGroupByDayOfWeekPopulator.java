package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.domain.core.Evidence;
import fortscale.domain.histogram.HistogramDualKey;
import fortscale.domain.histogram.HistogramKey;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Supporting information populator class for aggregation based on hour and day of week
 *
 * @author gils
 * Date: 05/08/2015
 */

@Component
@Scope("prototype")
public class SupportingInformationHourlyCountGroupByDayOfWeekPopulator extends SupportingInformationBasePopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationHourlyCountGroupByDayOfWeekPopulator.class);

    private static final String HOURLY_HISTOGRAM_OF_EVENTS_FEATURE_NAME = "hourly_histogram_of_events";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public SupportingInformationHourlyCountGroupByDayOfWeekPopulator(String contextType, String dataEntity, String featureName) {
        super(contextType, dataEntity, featureName);
    }

    /**
     * Creating histogram data of {day of week + hour} based on hourly distribution of events on daily basis.
     * Day value is extracted from the bucket itself and the events distribution in the feature values of the bucket.
     * Assuming hour range is positive integer in the range [0..23].
     */
    @Override
    protected Map<HistogramKey, Double> createSupportingInformationHistogram(List<FeatureBucket> featureBuckets) {
        Map<HistogramKey, Double> histogramKeyObjectMap = new HashMap<>();

        for (FeatureBucket featureBucket : featureBuckets) {

            String normalizedFeatureName = getNormalizedFeatureName(featureName);

            Feature feature = featureBucket.getAggregatedFeatures().get(normalizedFeatureName);

            if (feature == null) {
                logger.warn("Cannot find feature {} in feature bucket with ID {}", normalizedFeatureName, featureBucket.getBucketId());
                continue;
            }

            Object featureValue = feature.getValue();

            long bucketStartTime = featureBucket.getStartTime();

            Integer dayOfWeek = TimeUtils.getOrdinalDayOfWeek(TimestampUtils.convertToMilliSeconds(bucketStartTime));

            if (featureValue instanceof GenericHistogram) {
                Map<String, Double> histogramMap = ((GenericHistogram) featureValue).getHistogramMap();

                for (Map.Entry<String, Double> histogramEntry : histogramMap.entrySet()) {
                    Integer hour = Integer.parseInt(histogramEntry.getKey());

                    if (TimeUtils.isOrdinalHourValid(hour)) {
                        throw new IllegalStateException("Hour value is out of range - " + hour);
                    }

                    Double currValue = histogramEntry.getValue();

                    HistogramKey histogramKey = new HistogramDualKey(TimeUtils.getDayOfWeek(dayOfWeek), hour.toString());

                    Double currHistogramValue = (histogramKeyObjectMap.get(histogramKey) != null) ?  histogramKeyObjectMap.get(histogramKey) : 0;

                    histogramKeyObjectMap.put(histogramKey, currHistogramValue + currValue);
                }

            } else {
                logger.error("Cannot find histogram data for feature {} in bucket id {}", normalizedFeatureName, featureBucket.getBucketId());
            }
        }
        return histogramKeyObjectMap;
    }

    @Override
    /*
     * Converting the anomaly value which is in concrete date format to an histogram key
     * consist of the day of week (Sunday, Monday etc.) AND the cieled hour (0, 1, .. 23)
     */
    HistogramKey createAnomalyHistogramKey(Evidence evidence, String featureName) {
        String anomalyValue = extractAnomalyValue(evidence, featureName);

        // the anomaly value in this case is date string, i.e. 2015-07-15 02:05:53.
        // first convert to date, than ciel the hour to get the right histogram entry
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        try {
            Date date = dateFormat.parse(anomalyValue);
            Date truncatedDate = DateUtils.truncate(date, Calendar.HOUR);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(truncatedDate);

            Integer dayOfWeekOrdinalVal = calendar.get(Calendar.DAY_OF_WEEK);
            Integer hourVal = calendar.get(Calendar.HOUR);

            String dayOfWeek = TimeUtils.getDayOfWeek(dayOfWeekOrdinalVal);

            if (dayOfWeek == null) {
                logger.error("Illegal day of week value: {}", dayOfWeekOrdinalVal);
            }

            return new HistogramDualKey(dayOfWeek, hourVal.toString());

        } catch (ParseException e) {
            logger.error("Cannot parse date string {} to format {}", anomalyValue, DATE_FORMAT);

            return null;
        }
    }

    @Override
    protected String getNormalizedContextType(String contextType) {
        return contextType;
    }

    @Override
    String getNormalizedFeatureName(String featureName) {
        return HOURLY_HISTOGRAM_OF_EVENTS_FEATURE_NAME;
    }
}
