package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import presidio.output.domain.records.alerts.Aggregation;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.TimeAggregation;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AggregationDataCountByTimePopulator implements AggregationDataPopulator {

    public static final String HOURLY_AGGREGATIONS = "hourly_aggregations";


    HistoricalDataFetcher historicalDataFetcher;

    public AggregationDataCountByTimePopulator(HistoricalDataFetcher historicalDataFetcher) {
        this.historicalDataFetcher = historicalDataFetcher;
    }

    @Override
    public Aggregation createAggregationData(TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig) {

        List<Bucket<String,Double >> buckets = new ArrayList<Bucket<String, Double>>();

        // fetch daily histograms
        List<DailyHistogram<Integer, Double>> dailyHistograms = historicalDataFetcher.getDailyHistogramsForAggregatedFeature(timeRange, contexts, schema, featureName);

        // iterate over days
        for (DailyHistogram<Integer, Double> dailyHistogram: dailyHistograms) {

            LocalDate day = dailyHistogram.getDate();

            if (dailyHistogram.getHistogram() == null) {
                // TODO: logger
                continue;
            }

            // iterate over hours
            for (Integer hour : dailyHistogram.getHistogram().keySet()) {
                long epocTime = dailyHistogram.getDate().atStartOfDay().plus(hour, ChronoUnit.HOURS).toEpochSecond(ZoneOffset.UTC);
                Double valueForHour = dailyHistogram.getHistogram().get(hour);
                boolean isAnomaly = anomalyValue.equals(valueForHour.toString());
                Bucket<String, Double> bucket = new Bucket<String, Double>(Long.toString(epocTime), valueForHour, isAnomaly);
                buckets.add(bucket);
            }
        }

        return new TimeAggregation(buckets);
    }


    @Override
    public String getType() {
        return HOURLY_AGGREGATIONS;
    }
}
