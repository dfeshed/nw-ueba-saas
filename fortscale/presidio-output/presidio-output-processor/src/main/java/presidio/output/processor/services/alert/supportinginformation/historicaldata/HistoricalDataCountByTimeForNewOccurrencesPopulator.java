package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.domain.records.alerts.NewOccurrencesAggregation;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class HistoricalDataCountByTimeForNewOccurrencesPopulator implements HistoricalDataPopulator {

    public static final String HOURLY_AGGREGATIONS_NEW_OCCURRENCES = "hourly_aggregations_new_occurrences";
    private static final String GLOBAL_KEY = "GLOBAL";
    private static final String CONTEXT_KEY = "CONTEXT";

    HistoricalDataFetcher historicalDataFetcher;

    public HistoricalDataCountByTimeForNewOccurrencesPopulator(HistoricalDataFetcher historicalDataFetcher) {
        this.historicalDataFetcher = historicalDataFetcher;
    }

    @Override
    public List<HistoricalData> createHistoricalData(TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig) {


        List<Bucket<String, List<Bucket<String, Bucket<String, Double>>>>> buckets = new ArrayList<>();
        List<Bucket<String, Bucket<String, Double>>> global_buckets = new ArrayList<>();

        List<DailyHistogram<Integer, HashMap<String, Double>>> globalDailyHistograms = historicalDataFetcher.getGlobalDailyHistogramsForAggregatedFeature(timeRange, schema, featureName, historicalDataConfig);

        // iterate over days
        for (DailyHistogram<Integer, HashMap<String, Double>> dailyHistogram: globalDailyHistograms) {

            if (dailyHistogram.getHistogram() == null) {
                continue;
            }

            // iterate over hours
            for (Integer hour : dailyHistogram.getHistogram().keySet()) {
                long epocTime = dailyHistogram.getDate().atStartOfDay().plus(hour, ChronoUnit.HOURS).toEpochSecond(ZoneOffset.UTC);
                Double valueForHour = dailyHistogram.getHistogram().get(hour).entrySet().iterator().next().getValue();
                String contextValue = dailyHistogram.getHistogram().get(hour).entrySet().iterator().next().getKey();
                Bucket<String, Double> bucketVal = new Bucket<String, Double>(contextValue, valueForHour);
                Bucket<String, Bucket<String, Double>> bucket = new Bucket<String, Bucket<String, Double>>(Long.toString(epocTime), bucketVal);
                global_buckets.add(bucket);
            }
        }

        buckets.add(new Bucket<>(GLOBAL_KEY, global_buckets));

        List<Bucket<String, Bucket<String, Double>>> context_buckets = new ArrayList<>();

        // fetch daily histograms from memory
        List<DailyHistogram<Integer, Double>> dailyHistogramsByContext = historicalDataFetcher.getNewOccurrenceDailyHistogramsForAggregatedFeature(timeRange, contexts, schema, featureName, historicalDataConfig);

        String contextValue = contexts.entrySet().iterator().next().getValue();

        // iterate over days
        for (DailyHistogram<Integer, Double> dailyHistogram: dailyHistogramsByContext) {

            if (dailyHistogram.getHistogram() == null) {
                continue;
            }

            // iterate over hours
            for (Integer hour : dailyHistogram.getHistogram().keySet()) {
                long epocTime = dailyHistogram.getDate().atStartOfDay().plus(hour, ChronoUnit.HOURS).toEpochSecond(ZoneOffset.UTC);
                Double valueForHour = dailyHistogram.getHistogram().get(hour);
                boolean isAnomaly = anomalyValue.equals(valueForHour.toString());
                Bucket<String, Double> bucketVal = new Bucket<>(contextValue, valueForHour);
                Bucket<String, Bucket<String, Double>> bucket = new Bucket<>(Long.toString(epocTime), bucketVal, isAnomaly);
                context_buckets.add(bucket);
            }
        }

        buckets.add(new Bucket<>(CONTEXT_KEY, context_buckets));

        NewOccurrencesAggregation aggregation = new NewOccurrencesAggregation(buckets);
        return Arrays.asList(new HistoricalData(aggregation));
    }


    @Override
    public String getType() {
        return HOURLY_AGGREGATIONS_NEW_OCCURRENCES;
    }
}
