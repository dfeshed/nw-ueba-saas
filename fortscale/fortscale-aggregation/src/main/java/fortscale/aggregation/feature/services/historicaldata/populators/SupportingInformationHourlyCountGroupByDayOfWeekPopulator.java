package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationException;
import fortscale.common.feature.Feature;
import fortscale.common.util.GenericHistogram;
import fortscale.domain.core.Evidence;
import fortscale.domain.historical.data.SupportingInformationDualKey;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
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
public class SupportingInformationHourlyCountGroupByDayOfWeekPopulator extends SupportingInformationHistogramBySingleEventsPopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationHourlyCountGroupByDayOfWeekPopulator.class);

    private static final String NUMBER_OF_EVENTS_PER_HOUR_HISTOGRAM_FEATURE_NAME = "number_of_events_per_hour_histogram";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public SupportingInformationHourlyCountGroupByDayOfWeekPopulator(String contextType, String dataEntity, String featureName) {
        super(contextType, dataEntity, featureName);
    }

    protected Map<SupportingInformationKey, Double> createSupportingInformationHistogram(String contextValue, long evidenceEndTime, Integer timePeriodInDays) {
        List<FeatureBucket> featureBuckets = fetchRelevantFeatureBuckets(contextValue, evidenceEndTime, timePeriodInDays);

        if (featureBuckets.isEmpty()) {
            throw new SupportingInformationException("Could not find any relevant bucket for histogram creation");
        }

        Map<SupportingInformationKey, Double> lastDayMap = createLastDayBucket(getNormalizedContextType(contextType), contextValue, evidenceEndTime, dataEntity);

        return createSupportingInformationHistogram(featureBuckets, lastDayMap);
    }

    /**
     * Creating histogram data of {day of week + hour} based on hourly distribution of events on daily basis.
     * Day value is extracted from the bucket itself and the events distribution in the feature values of the bucket.
     * Assuming hour range is positive integer in the range [0..23].
     */
    protected Map<SupportingInformationKey, Double> createSupportingInformationHistogram(List<FeatureBucket> featureBuckets,
            Map<SupportingInformationKey, Double> lastDayMap) {
        Map<SupportingInformationKey, Double> histogramKeyObjectMap = new HashMap<>();

        String normalizedFeatureName = getNormalizedFeatureName(featureName);

        for (FeatureBucket featureBucket : featureBuckets) {

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

                    SupportingInformationKey supportingInformationKey = new SupportingInformationDualKey(TimeUtils.getDayOfWeek(dayOfWeek), hour.toString());

                    Double currHistogramValue = (histogramKeyObjectMap.get(supportingInformationKey) != null) ?  histogramKeyObjectMap.get(supportingInformationKey) : 0.0;

                    histogramKeyObjectMap.put(supportingInformationKey, currHistogramValue + currValue);
                }

            } else {
                logger.error("Cannot find histogram data for feature {} in bucket id {}", normalizedFeatureName, featureBucket.getBucketId());
            }
        }

        //Merge last days map into histogramKeyObjectMap
        if (lastDayMap !=null ){
            for (Map.Entry<SupportingInformationKey, Double> lastDayEntry: lastDayMap.entrySet()){
                updateHistoricalDataEntry(histogramKeyObjectMap, lastDayEntry.getKey() ,lastDayEntry.getValue());
            }
        }

        return histogramKeyObjectMap;
    }

    /**
     * Check if key exists.
     * If key exists, add current value to old value.
     * If not - add new entry with the current value
     *
     * @param histogramKeyObjectMap
     * @param supportingInformationKey
     * @param currValue
     */
    private void updateHistoricalDataEntry(Map<SupportingInformationKey, Double> histogramKeyObjectMap, SupportingInformationKey supportingInformationKey, Double currValue){

        Double currHistogramValue = histogramKeyObjectMap.get(supportingInformationKey);
        if (currHistogramValue == null){
            currHistogramValue = new Double(0);
        }

        histogramKeyObjectMap.put(supportingInformationKey, currHistogramValue + currValue);
    }

    @Override
    /*
     * Converting the anomaly value which is in concrete date format to an histogram key
     * consist of the day of week (Sunday, Monday etc.) AND the cieled hour (0, 1, .. 23)
     */
    SupportingInformationKey createAnomalyHistogramKey(Evidence evidence, String featureName) {
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
            Integer hourVal = calendar.get(Calendar.HOUR_OF_DAY);

            String dayOfWeek = TimeUtils.getDayOfWeek(dayOfWeekOrdinalVal);

            if (dayOfWeek == null) {
                logger.error("Illegal day of week value: {}", dayOfWeekOrdinalVal);
            }

            return new SupportingInformationDualKey(dayOfWeek, hourVal.toString());

        } catch (ParseException e) {
            logger.error("Cannot parse date string {} to format {}", anomalyValue, DATE_FORMAT);

            return null;
        }
    }



    protected Map<SupportingInformationKey, Double> buildLastDayMap(List<Map<String, Object>> queryList) {
        Map<SupportingInformationKey, Double> lastDayMap = new HashMap<>();
        for (Map<String, Object> bucketMap : queryList){
			Timestamp eventTime = (Timestamp)bucketMap.get("event_time");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(eventTime);
			Integer dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
            lastDayMap.put(new SupportingInformationDualKey(TimeUtils.getDayOfWeek(dayOfWeek), Integer.toString(hour)), ((Long) bucketMap.get("countField")).doubleValue());
        }
        return lastDayMap;
    }

    @Override
    protected String getNormalizedContextType(String contextType) {
        return contextType;
    }

    @Override
    String getNormalizedFeatureName(String featureName) {
        return NUMBER_OF_EVENTS_PER_HOUR_HISTOGRAM_FEATURE_NAME;
    }

    @Override
    protected boolean isAnomalyIndicationRequired(Evidence evidence) {
        return true;
    }
}
