package fortscale.aggregation.feature.services.historicaldata;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.domain.core.SupportingInformationData;
import fortscale.domain.histogram.HistogramDualKey;
import fortscale.domain.histogram.HistogramKey;
import fortscale.utils.TimestampUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Supporting information populator class for heatmap-based aggregations
 *
 * @author gils
 * Date: 05/08/2015
 */

@Component
@Scope("prototype")
public class SupportingInformationHeatMapDataPopulator extends SupportingInformationDataBasePopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationHeatMapDataPopulator.class);

    private static final int HOUR_LOWER_BOUND = 0;
    private static final int HOUR_UPPER_BOUND = 23;

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String UTC_TIMEZONE = "UTC";

    public SupportingInformationHeatMapDataPopulator(String contextType, String dataEntity, String featureName) {
        super(contextType, dataEntity, featureName);
    }

    @Override
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

        HistogramKey anomalyHistogramKey = createHistogramKey(anomalyValue);

        return new SupportingInformationData(histogramKeyObjectMap, anomalyHistogramKey);
    }

    @Override
    HistogramKey createHistogramKey(String anomalyValue) {
        // the anomaly value in this case is date string, i.e. 2015-07-15 02:05:53.
        // first convert to date, than ciel the hour to get the right histogram entry
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date date = dateFormat.parse(anomalyValue);
            Date roundedDate = DateUtils.ceiling(date, Calendar.HOUR);

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE));
            calendar.setTime(roundedDate);

            Integer dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            Integer hour = calendar.get(Calendar.HOUR);

            return new HistogramDualKey(dayOfWeek.toString(), hour.toString());

        } catch (ParseException e) {
            logger.error("Cannot parse date string {} to format {}", anomalyValue, DATE_FORMAT);

            return null;
        }
    }

    private boolean isHourValueOutOfRange(Integer hour) {
        return hour > HOUR_UPPER_BOUND || hour < HOUR_LOWER_BOUND;
    }
}
