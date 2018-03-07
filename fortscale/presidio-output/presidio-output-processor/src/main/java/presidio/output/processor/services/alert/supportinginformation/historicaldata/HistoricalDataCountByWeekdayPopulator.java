package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimeUtils;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.domain.records.alerts.WeekdayAggregation;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
        List<DailyHistogram<String, Number>> dailyHistograms = historicalDataFetcher.getDailyHistogramsForFeature(timeRange, contextField, contextValue, schema, featureName, historicalDataConfig);

        // iterate over days
        for (DailyHistogram<String, Number> dailyHistogram : dailyHistograms) {

            Integer dayOfWeek = dailyHistogram.getDate().getDayOfWeek().ordinal();
            if (dailyHistogram.getHistogram() == null) {
                continue;
            }

            Map<Integer, Double> hoursMap = (weekdayMap.get(dayOfWeek) != null)? weekdayMap.get(dayOfWeek): new HashMap<Integer, Double>();

            // iterate over hours and aggregate the data per dayofweek / hour
            for (Map.Entry<String, Number> hoursHistogram : dailyHistogram.getHistogram().entrySet()) {

                Instant instant = Instant.ofEpochSecond(Long.parseLong(hoursHistogram.getKey()));
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
                int hour = localDateTime.getHour();
                Double currValue = (Double) hoursHistogram.getValue();
                Double currHistogramValue = (hoursMap.get(hour) != null) ?  hoursMap.get(hour) : 0.0;
                hoursMap.put(hour, currHistogramValue + currValue);
            }

            weekdayMap.put(dayOfWeek, hoursMap);

        }


        // cretae output
        List<Bucket<String,List<Bucket<String, Integer>>>> buckets = new ArrayList<Bucket<String,List<Bucket<String, Integer>>>>();
        Instant anomalyInstant = Instant.parse(anomalyValue);
        int anomalyDayOfWeek = LocalDateTime.ofInstant(anomalyInstant, ZoneOffset.UTC).getDayOfWeek().ordinal();
        int anomalyHourOfWeek = LocalDateTime.ofInstant(anomalyInstant, ZoneOffset.UTC).getHour();

        for (Integer dayOfWeek : weekdayMap.keySet()) {

            Bucket<String,List<Bucket<String, Integer>>> dayOfWeekBucket = new Bucket<String,List<Bucket<String, Integer>>>();
            List<Bucket<String, Integer>> dayOfweekHours = new ArrayList<Bucket<String, Integer>>();

            for (Integer hour: weekdayMap.get(dayOfWeek).keySet()) {

                Double valueInHour =   weekdayMap.get(dayOfWeek).get(hour);
                boolean anomaly = anomalyDayOfWeek == dayOfWeek.intValue() && anomalyHourOfWeek == hour.intValue();
                Bucket<String, Integer> hourlyBucket = new Bucket<String, Integer>(hour.toString(), valueInHour.intValue(), anomaly);
                dayOfweekHours.add(hourlyBucket);
            }

            dayOfWeekBucket.setKey(DayOfWeek.of(dayOfWeek+1).name());
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
