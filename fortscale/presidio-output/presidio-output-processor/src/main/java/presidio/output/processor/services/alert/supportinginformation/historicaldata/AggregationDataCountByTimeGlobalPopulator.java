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

public class AggregationDataCountByTimeGlobalPopulator implements AggregationDataPopulator {

    private static final String HOURLY_AGGREGATIONS_MAX_GLOBAL = "hourly_aggregations_max_global";

    private HistoricalDataFetcher historicalDataFetcher;

    public AggregationDataCountByTimeGlobalPopulator(HistoricalDataFetcher historicalDataFetcher) {
        this.historicalDataFetcher = historicalDataFetcher;
    }

    @Override
    public Aggregation createAggregationData(TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig) {

        List<Bucket<String, Double>> global_buckets = new ArrayList<>();
        List<DailyHistogram<Integer, Double>> globalDailyHistograms = historicalDataFetcher.getGlobalMaxDailyHistogramsForAggregatedFeature(timeRange, schema, featureName);

        // iterate over days
        for (DailyHistogram<Integer, Double> dailyHistogram : globalDailyHistograms) {

            if (dailyHistogram.getHistogram() == null) {
                continue;
            }

            // iterate over hours
            for (Integer hour : dailyHistogram.getHistogram().keySet()) {
                long epocTime = dailyHistogram.getDate().atStartOfDay().plus(hour, ChronoUnit.HOURS).toEpochSecond(ZoneOffset.UTC);
                Double valueForHour = dailyHistogram.getHistogram().get(hour);
                Bucket<String, Double> bucket = new Bucket<>(Long.toString(epocTime), valueForHour);
                global_buckets.add(bucket);
            }
        }

        return new TimeAggregation(global_buckets);
    }

    @Override
    public String getType() {
        return HOURLY_AGGREGATIONS_MAX_GLOBAL;
    }
}
