package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.HistoricalData;
import presidio.output.domain.records.alerts.TimeAggregation;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

import java.util.ArrayList;
import java.util.List;

public class HistoricalDataCountByTimeForScoreFeaturePopulator implements HistoricalDataPopulator {
    Logger logger = Logger.getLogger(HistoricalDataCountByTimeForScoreFeaturePopulator.class);

    public static final String HOURLY_SCORED_AGGREGATIONS = "hourly_scored_aggregations";


    HistoricalDataFetcher historicalDataFetcher;

    public HistoricalDataCountByTimeForScoreFeaturePopulator(HistoricalDataFetcher historicalDataFetcher) {
        this.historicalDataFetcher = historicalDataFetcher;
    }

    @Override
    public HistoricalData createHistoricalData(TimeRange timeRange, String contextField, String contextValue, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig) {

        List<Bucket<String, Double>> buckets = new ArrayList<>();

        // fetch daily histograms
        List<DailyHistogram<String>> dailyHistograms = historicalDataFetcher.getDailyHistogramsForFeature(timeRange, contextField, contextValue, schema, featureName, historicalDataConfig);

        // iterate over days
        for (DailyHistogram<String> dailyHistogram : dailyHistograms) {

            if (dailyHistogram.getHistogram() == null) {
                logger.debug("The histogram for day {} was null", dailyHistogram.date);
                continue;
            }

            // iterate over hours
            for (String hour : dailyHistogram.getHistogram().keySet()) {

                Double valueForHour = dailyHistogram.getHistogram().get(hour);
                boolean isAnomaly = anomalyValue.equals(valueForHour.toString());
                // The key format is : {featureName#epochtime} and we need to extract the epochtime
                Bucket<String, Double> bucket = new Bucket<>(hour.split("#")[1], valueForHour, isAnomaly);
                buckets.add(bucket);
            }
        }

        TimeAggregation aggregation = new TimeAggregation(buckets);
        return new HistoricalData(aggregation);
    }


    @Override
    public String getType() {
        return HOURLY_SCORED_AGGREGATIONS;
    }
}
