package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.CountAggregation;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoricalDataCountByValuePopulator implements HistoricalDataPopulator {

    public static final String COUNT_AGGREGATIONS = "count_aggregations";
    HistoricalDataFetcher historicalDataFetcher;

    public HistoricalDataCountByValuePopulator(HistoricalDataFetcher historicalDataFetcher) {
        this.historicalDataFetcher = historicalDataFetcher;
    }

    @Override
    public HistoricalData createHistoricalData(TimeRange timeRange, String contextField, String contextValue, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig) {

        // map of feature value -> count for the entire timeRange
        Map<String, Double> histogramMap = new HashMap<String, Double>();

        // fetch daily histograms
        List<DailyHistogram<String>> dailyHistograms = historicalDataFetcher.getDailyHistogramsForFeature(timeRange, contextField, contextValue, schema, featureName, historicalDataConfig);

        // iterate over days
        for (DailyHistogram<String> dailyHistogram: dailyHistograms) {

            LocalDate day = dailyHistogram.getDate();

            if (dailyHistogram.getHistogram() == null) {
                // TODO: logger
                continue;
            }

            // merge the values
            dailyHistogram.getHistogram().forEach((k, v) -> histogramMap.merge(k, v, Double::sum));

        }

        // create output
        List<Bucket<String, Double>> buckets = new ArrayList<Bucket<String, Double>>();
        buckets.addAll(histogramMap.entrySet().stream()
                .map(e -> new Bucket<String,Double>(e.getKey(), e.getValue(), e.getKey().equals(anomalyValue)))
                .collect(Collectors.toList()));

        CountAggregation aggregation = new CountAggregation(buckets);
        return new HistoricalData(aggregation);
    }


    @Override
    public String getType() {
        return COUNT_AGGREGATIONS;
    }
}
