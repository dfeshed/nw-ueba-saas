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

public class AggregationDataCountByTimeForNewOccurrencePopulator implements AggregationDataPopulator {

    private static final String HOURLY_AGGREGATIONS_NEW_OCCURRENCES = "hourly_aggregations_new_occurrence";

    private HistoricalDataFetcher historicalDataFetcher;

    public AggregationDataCountByTimeForNewOccurrencePopulator(HistoricalDataFetcher historicalDataFetcher) {
        this.historicalDataFetcher = historicalDataFetcher;
    }

    @Override
    public Aggregation createAggregationData(TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig) {

        List<Bucket<String, Double>> context_buckets = new ArrayList<>();

        // fetch daily histograms from memory
        List<DailyHistogram<Integer, Double>> dailyHistogramsByContext = historicalDataFetcher.getNewOccurrenceDailyHistogramsForAggregatedFeature(timeRange, contexts, schema, featureName);

        String contextValue = contexts.entrySet().iterator().next().getValue();

        // iterate over days
        for (DailyHistogram<Integer, Double> dailyHistogram : dailyHistogramsByContext) {

            if (dailyHistogram.getHistogram() == null) {
                continue;
            }

            // iterate over hours
            for (Integer hour : dailyHistogram.getHistogram().keySet()) {
                long epocTime = dailyHistogram.getDate().atStartOfDay().plus(hour, ChronoUnit.HOURS).toEpochSecond(ZoneOffset.UTC);
                Double valueForHour = dailyHistogram.getHistogram().get(hour);
                boolean isAnomaly = anomalyValue.equals(valueForHour.toString());
                Bucket<String, Double> bucket = new Bucket<>(Long.toString(epocTime), valueForHour, isAnomaly);
                context_buckets.add(bucket);
            }
        }

        return new TimeAggregation(context_buckets);
    }

    @Override
    public String getType() {
        return HOURLY_AGGREGATIONS_NEW_OCCURRENCES;
    }
}
