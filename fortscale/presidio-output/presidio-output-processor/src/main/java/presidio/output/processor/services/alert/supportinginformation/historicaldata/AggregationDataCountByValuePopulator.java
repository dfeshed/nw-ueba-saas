package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import presidio.output.domain.records.alerts.Aggregation;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.CountAggregation;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AggregationDataCountByValuePopulator implements AggregationDataPopulator {
    public static final String COUNT_AGGREGATIONS = "count_aggregations";

    private final HistoricalDataFetcher historicalDataFetcher;

    public AggregationDataCountByValuePopulator(HistoricalDataFetcher historicalDataFetcher) {
        this.historicalDataFetcher = historicalDataFetcher;
    }

    @Override
    public Aggregation createAggregationData(TimeRange timeRange, Map<String, String> contexts, Schema schema,
                                             String featureName, String anomalyValue,
                                             HistoricalDataConfig historicalDataConfig,
                                             boolean skipAnomaly, Date indicatorStartDate) {

        // Get the daily histograms.
        // Each daily histogram has a map from a feature value to its number of occurrences on that day.
        List<DailyHistogram<String, Number>> dailyHistograms = historicalDataFetcher.getDailyHistogramsForFeature(
                timeRange, contexts, schema, featureName, historicalDataConfig, true);

        // Create a map from a feature value to the number of days on which it occurred.
        Map<String, Double> featureValueToNumberOfDaysMap = dailyHistograms.stream()
                // Flatten the daily histograms to a stream of all the entries.
                // Each entry is of type <feature value, number of occurrences on a certain day>.
                .filter(dailyHistogram -> dailyHistogram.getHistogram() != null)
                .flatMap(dailyHistogram -> dailyHistogram.getHistogram().entrySet().stream())
                // Collect all the entries to a map, while counting for each feature value its number of entries,
                // i.e. the number of days on which it occurred.
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> 1d, Double::sum));

        // Add one day to the count of the anomalous feature value (the day on which the anomaly occurred).
        featureValueToNumberOfDaysMap.compute(anomalyValue, (key, value) -> value == null ? 1 : value + 1);

        // Convert each entry in the map to a Bucket instance.
        List<Bucket<String, Double>> buckets = featureValueToNumberOfDaysMap.entrySet().stream()
                .map(entry -> new Bucket<>(entry.getKey(), entry.getValue(),  !skipAnomaly && entry.getKey().equals(anomalyValue)))
                .collect(Collectors.toList());

        return new CountAggregation(buckets, contexts);
    }

    @Override
    public String getType() {
        return COUNT_AGGREGATIONS;
    }
}
