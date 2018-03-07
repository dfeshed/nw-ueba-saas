package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.domain.records.alerts.TimeAggregation;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class HistoricalDataCountByTimePopulator implements HistoricalDataPopulator {

    public static final String HOURLY_AGGREGATIONS = "hourly_aggregations";


    HistoricalDataFetcher historicalDataFetcher;

    public HistoricalDataCountByTimePopulator(HistoricalDataFetcher historicalDataFetcher) {
        this.historicalDataFetcher = historicalDataFetcher;
    }

    @Override
    public HistoricalData createHistoricalData(TimeRange timeRange, String contextField, String contextValue, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig) {

        List<Bucket<String,Double >> buckets = new ArrayList<Bucket<String, Double>>();

        // fetch daily histograms
        List<DailyHistogram<Integer, Double>> dailyHistograms = historicalDataFetcher.getDailyHistogramsForAggregatedFeature(timeRange, contextField, contextValue, schema, featureName, historicalDataConfig);

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

        TimeAggregation aggregation = new TimeAggregation(buckets);
        return new HistoricalData(aggregation);
    }


    @Override
    public String getType() {
        return HOURLY_AGGREGATIONS;
    }
}
