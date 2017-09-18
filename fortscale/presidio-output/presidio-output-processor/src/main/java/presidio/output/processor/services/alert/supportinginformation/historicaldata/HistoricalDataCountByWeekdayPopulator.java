package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimeUtils;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.domain.records.alerts.WeekdayAggregation;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoricalDataCountByWeekdayPopulator implements HistoricalDataPopulator {

    public static final String WEEKDAY_AGGREGATIONS = "weekday_aggregations";

    HistoricalDataFetcher historicalDataFetcher;

    public HistoricalDataCountByWeekdayPopulator(HistoricalDataFetcher historicalDataFetcher) {
        this.historicalDataFetcher = historicalDataFetcher;
    }

    @Override
    public HistoricalData createHistoricalData(TimeRange timeRange, String contextField, String contextValue, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig) {

        // map of day of week -> <hour -> count>
        Map<Integer, Map<Integer, Double>> weekdayMap = new HashMap<Integer, Map<Integer, Double>>();

        // fetch daily histograms
        List<DailyHistogram<String>> dailyHistograms = historicalDataFetcher.getDailyHistogramsForFeature(timeRange, contextField, contextValue, schema, featureName, historicalDataConfig);

        // iterate over days
        for (DailyHistogram<String> dailyHistogram : dailyHistograms) {

            Integer dayOfWeek = dailyHistogram.getDate().getDayOfWeek().ordinal();

            if (dailyHistogram.getHistogram() == null) {
                // TODO: logger
                //logger.error("Cannot find histogram data for feature {} in bucket id {}", normalizedFeatureName, featureBucket.getBucketId());
                continue;
            }

            // iterate over hours and aggregate the data per dayofweek / hour
            for (Map.Entry<String, Double> hoursHistogram : dailyHistogram.getHistogram().entrySet()) {

                    Integer hour = Integer.parseInt(hoursHistogram.getKey());

                    if (TimeUtils.isOrdinalHourValid(hour)) {
                        throw new IllegalStateException("Hour value is out of range - " + hour);
                    }

                    Double currValue = hoursHistogram.getValue();

                    Map<Integer, Double> hoursMap = (weekdayMap.get(dayOfWeek) != null)? weekdayMap.get(dayOfWeek): new HashMap<Integer, Double>();

                    Double currHistogramValue = (hoursMap.get(hour) != null) ?  hoursMap.get(hour) : 0.0;

                    hoursMap.put(hour, currHistogramValue + currValue);

                    weekdayMap.put(dayOfWeek, hoursMap);
                }

            }


        // cretae output
        List<Bucket<String,List<Bucket<String, Integer>>>> buckets = new ArrayList<Bucket<String,List<Bucket<String, Integer>>>>();

        for (Integer datOfWeek : weekdayMap.keySet()) {

            Bucket<String,List<Bucket<String, Integer>>> dayOfWeekBucket = new Bucket<String,List<Bucket<String, Integer>>>();
            List<Bucket<String, Integer>> dayOfweekHours = new ArrayList<Bucket<String, Integer>>();

            for (Integer hour: weekdayMap.get(datOfWeek).keySet()) {

                Double valueInHour =   weekdayMap.get(datOfWeek).get(hour);
                Bucket<String, Integer> hourlyBucket = new Bucket<String, Integer>(hour.toString(), valueInHour.intValue());
                dayOfweekHours.add(hourlyBucket);
            }

            dayOfWeekBucket.setKey(datOfWeek.toString());
            dayOfWeekBucket.setValue(dayOfweekHours);
            buckets.add(dayOfWeekBucket);
        }

        WeekdayAggregation aggregation = new WeekdayAggregation(buckets);
        return new HistoricalData(aggregation);
    }


    @Override
    public String getType() {
        return WEEKDAY_AGGREGATIONS;
    }
}
