package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import presidio.output.domain.records.alerts.Aggregation;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.GlobalAggregation;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class AggregationDataCountByTimeGlobalPopulator implements AggregationDataPopulator {

    private static final String HOURLY_AGGREGATIONS_NEW_OCCURRENCES_GLOBAL = "hourly_aggregations_global";

    private HistoricalDataFetcher historicalDataFetcher;

    public AggregationDataCountByTimeGlobalPopulator(HistoricalDataFetcher historicalDataFetcher) {
        this.historicalDataFetcher = historicalDataFetcher;
    }

    @Override
    public Aggregation createAggregationData(TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig) {

        List<Bucket<String, Bucket<String, Double>>> global_buckets = new ArrayList<>();
        List<DailyHistogram<Integer, HashMap<String, Double>>> globalDailyHistograms = historicalDataFetcher.getGlobalDailyHistogramsForAggregatedFeature(timeRange, schema, featureName, historicalDataConfig);

        // iterate over days
        for (DailyHistogram<Integer, HashMap<String, Double>> dailyHistogram : globalDailyHistograms) {

            if (dailyHistogram.getHistogram() == null) {
                continue;
            }

            // iterate over hours
            for (Integer hour : dailyHistogram.getHistogram().keySet()) {
                long epocTime = dailyHistogram.getDate().atStartOfDay().plus(hour, ChronoUnit.HOURS).toEpochSecond(ZoneOffset.UTC);
                Double valueForHour = dailyHistogram.getHistogram().get(hour).entrySet().iterator().next().getValue();
                String contextValue = dailyHistogram.getHistogram().get(hour).entrySet().iterator().next().getKey();
                Bucket<String, Double> bucketVal = new Bucket<>(contextValue, valueForHour);
                Bucket<String, Bucket<String, Double>> bucket = new Bucket<>(Long.toString(epocTime), bucketVal);
                global_buckets.add(bucket);
            }
        }

        return new GlobalAggregation(global_buckets);
    }

    @Override
    public String getType() {
        return HOURLY_AGGREGATIONS_NEW_OCCURRENCES_GLOBAL;
    }
}
