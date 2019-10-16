package presidio.output.processor.services.alert.supportinginformation.historicaldata;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import presidio.output.domain.records.alerts.Aggregation;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.TimeAggregation;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers.HistoricalDataFetcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AggregationDataCountByTimeForScoreFeaturePopulator implements AggregationDataPopulator {
    Logger logger = Logger.getLogger(AggregationDataCountByTimeForScoreFeaturePopulator.class);

    public static final String HOURLY_SCORED_AGGREGATIONS = "hourly_scored_aggregations";


    HistoricalDataFetcher historicalDataFetcher;

    public AggregationDataCountByTimeForScoreFeaturePopulator(HistoricalDataFetcher historicalDataFetcher) {
        this.historicalDataFetcher = historicalDataFetcher;
    }

    @Override
    public Aggregation createAggregationData(TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName, String anomalyValue, HistoricalDataConfig historicalDataConfig, boolean skipAnomaly, Date startDate) {

        List<Bucket<String, Double>> buckets = new ArrayList<>();

        // fetch daily histograms
        List<DailyHistogram<String, Number>> dailyHistograms = historicalDataFetcher.getDailyHistogramsForFeature(
                timeRange, contexts, schema, featureName, historicalDataConfig, false);

        // iterate over days
        for (DailyHistogram<String, Number> dailyHistogram : dailyHistograms) {

            if (dailyHistogram.getHistogram() == null) {
                logger.debug("The histogram for day {} was null", dailyHistogram.date);
                continue;
            }

            // iterate over hours
            for (String hour : dailyHistogram.getHistogram().keySet()) {

                Double valueForHour = dailyHistogram.getHistogram().get(hour).doubleValue();
                long startTimeInSeconds = startDate.getTime() / 1000;

                // The key format is : {featureName#epochtime} and we need to extract the epochtime
                String epochTime = hour.split("#")[1];

                boolean isAnomaly = !skipAnomaly && Long.toString(startTimeInSeconds).equals(epochTime) && anomalyValue.equals(valueForHour.toString());
                Bucket<String, Double> bucket = new Bucket<>(epochTime, valueForHour, isAnomaly);
                buckets.add(bucket);
            }
        }

        return new TimeAggregation(buckets, contexts);
    }


    @Override
    public String getType() {
        return HOURLY_SCORED_AGGREGATIONS;
    }
}
