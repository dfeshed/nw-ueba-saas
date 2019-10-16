package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import presidio.output.domain.records.alerts.Aggregation;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.TimeAggregation;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class AggregationDataCountByTimeForLastDayPopulator implements AggregationDataPopulator {

    private static final String HOURLY_AGGREGATIONS_LAST_DAY = "hourly_aggregations_last_day";

    private HistoricalDataFetcher historicalDataFetcher;

    public AggregationDataCountByTimeForLastDayPopulator(HistoricalDataFetcher historicalDataFetcher) {
        this.historicalDataFetcher = historicalDataFetcher;
    }

    @Override
    public Aggregation createAggregationData(TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig, boolean skipAnomaly, Date startDate) {

        List<Bucket<String, Double>> buckets = new ArrayList<>();

        // fetch daily histograms from memory
        List<DailyHistogram<Integer, Double>> dailyHistogramsByContext = historicalDataFetcher.getLastDayHistogramsForAggregatedFeature(timeRange, contexts, schema, featureName);

        // iterate over days
        for (DailyHistogram<Integer, Double> dailyHistogram : dailyHistogramsByContext) {

            if (dailyHistogram.getHistogram() == null) {
                continue;
            }

            // iterate over hours
            for (Integer hour : dailyHistogram.getHistogram().keySet()) {
                long startTimeInSeconds = startDate.getTime() / 1000;
                long epocTime = dailyHistogram.getDate().atStartOfDay().plus(hour, ChronoUnit.HOURS).toEpochSecond(ZoneOffset.UTC);
                Double valueForHour = dailyHistogram.getHistogram().get(hour);
                boolean isAnomaly = !skipAnomaly && startTimeInSeconds == epocTime && anomalyValue.equals(valueForHour.toString());
                Bucket<String, Double> bucket = new Bucket<>(Long.toString(epocTime), valueForHour, isAnomaly);
                buckets.add(bucket);
            }
        }

        return new TimeAggregation(buckets, contexts);
    }

    @Override
    public String getType() {
        return HOURLY_AGGREGATIONS_LAST_DAY;
    }
}
