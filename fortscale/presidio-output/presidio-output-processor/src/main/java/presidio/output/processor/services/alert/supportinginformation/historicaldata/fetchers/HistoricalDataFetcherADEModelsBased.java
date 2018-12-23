package presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers;

import fortscale.accumulator.aggregation.AccumulationsCache;
import fortscale.accumulator.aggregation.AccumulatorService;
import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketUtils;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.common.general.Schema;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimeService;
import org.apache.commons.collections.CollectionUtils;
import presidio.ade.domain.pagination.enriched.EnrichedRecordPaginationService;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.processor.config.HistoricalDataConfig;
import presidio.output.processor.services.alert.supportinginformation.historicaldata.DailyHistogram;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Fetching historical data which saved by the ADE for models building
 * The data is retrieved from the models data layer through SDK
 * Historical data for date range that is missing in the models is built in memory and
 * merged into the final results.
 * <p>
 * This fetcher is based on data that are already written for models creation and therefore
 * saves I/O operations
 */
public class HistoricalDataFetcherADEModelsBased implements HistoricalDataFetcher {

    Logger logger = Logger.getLogger(HistoricalDataFetcherADEModelsBased.class);

    AdeManagerSdk adeManagerSdk;

    EnrichedDataStore enrichedDataStore;

    InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;

    AggregationRecordsCreator aggregationRecordsCreator;

    AccumulatorService accumulatorService;

    AccumulationsCache accumulationsCache;


    public HistoricalDataFetcherADEModelsBased(AdeManagerSdk adeManagerSdk, EnrichedDataStore enrichedDataStore, InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator, AggregationRecordsCreator aggregationRecordsCreator, AccumulatorService accumulatorService, AccumulationsCache accumulationsCache) {
        this.adeManagerSdk = adeManagerSdk;
        this.enrichedDataStore = enrichedDataStore;
        this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
        this.aggregationRecordsCreator = aggregationRecordsCreator;
        this.accumulatorService = accumulatorService;
        this.accumulationsCache = accumulationsCache;
    }

    /**
     * Daily histograms for a single feature are fetched from the feature bucket collection aggr_<feature_name>.
     *
     * @param timeRange           the time range of the feature buckets
     * @param contexts            map of context keys and values (e.g. userId = Bob, machineId = BOB-PC1)
     * @param schema              the schema for which to populate historical behavior
     * @param featureName         the feature for which to populate historical behavior (e.g. operationType)
     * @param includeOnlyBaseline true if only the baseline time range should be included,
     *                            false if the rest of the time range should be completed
     * @return a list of daily histograms, one for each day in the time range. For example:
     * Feature: operationType, Date: 01/01/2017, Histogram {FILE_MOVED:5, FILE_COPY:9, ACCESS_RIGHTS_CHANGED:1}
     * Feature: operationType, Date: 01/02/2017, Histogram {FILE_OPENED:10, ACCESS_RIGHTS_CHANGED:1}
     */
    @Override
    public List<DailyHistogram<String, Number>> getDailyHistogramsForFeature(
            TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName,
            HistoricalDataConfig historicalDataConfig, boolean includeOnlyBaseline) {

        // Get historical data from model feature buckets.
        String contextId = FeatureBucketUtils.buildContextId(contexts);
        String featureBucketConfName = historicalDataConfig.getFeatureBucketConfName();
        List<FeatureBucket> featureBuckets = adeManagerSdk.findFeatureBuckets(contextId, featureBucketConfName, timeRange);

        // Convert the model feature buckets to daily histograms.
        List<DailyHistogram<String, Number>> dailyHistograms = convertFeatureBucketsToHistograms(featureName, featureBuckets);

        // Complete historical data in memory if required.
        if (!includeOnlyBaseline) {
            featureBuckets = calculateFeatureBuckets(timeRange, contexts, schema, featureName, featureBuckets, historicalDataConfig);
            dailyHistograms.addAll(convertFeatureBucketsToHistograms(featureName, featureBuckets));
        }

        return dailyHistograms;
    }

    /**
     * Histogram for aggregated feature are fetched from the AccumulatedAggregationFeatureRecord collections
     * (acc_<feature_name>. the missing data are completed using the aggregation framework (i.e: presidio-aggragtion package)
     *
     * @param timeRange
     * @param contexts     map of contexts key and values (the context id (i.e userId) -> the context value (i.e: the user name))
     * @param schema       the schema for which to populate historical behavior
     * @param featureName  the feature for which to populate historical behavior (e.g: login time)
     * @return List of aggregated feature histogram for each day in the range. The histogram key is hour of the day (0 .. 23)
     * e.g:   Aggregated Feature: numberOfFailedAuthentications, Day: 01/01/2017, histogram {0:1, 2:0, 3:0 ...}
     * Aggregated Feature: numberOfFailedAuthentications, Day: 01/02/2017, histogram {0:0, 2:1, 3:0 ...}
     */
    @Override
    public List<DailyHistogram<Integer, Double>> getDailyHistogramsForAggregatedFeature(TimeRange timeRange, Map<String, String> contexts,Schema schema, String featureName, HistoricalDataConfig historicalDataConfig) {
        // get historical data from models
        String contextId = AdeContextualAggregatedRecord.buildContextId(contexts);
        List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeatureRecordsFromModels = adeManagerSdk.getAccumulatedAggregatedFeatureEvents(featureName, contextId, timeRange);

        // complete historical data in memory
        List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeaturesInMemory = calculateAccumulatedAggregationFeatures(timeRange, contexts, schema, featureName, accumulatedAggregationFeatureRecordsFromModels);

        // translate AccumulatedAggregationFeatureRecord to dailyHistogram
        List<DailyHistogram<Integer, Double>> dailyHistogramList = new ArrayList<DailyHistogram<Integer, Double>>();
        dailyHistogramList.addAll(convertAggregationFeaturesToHistograms(accumulatedAggregationFeatureRecordsFromModels));
        dailyHistogramList.addAll(convertAggregationFeaturesToHistograms(accumulatedAggregationFeaturesInMemory));

        return dailyHistogramList;
    }


    private List<FeatureBucket> calculateFeatureBuckets(TimeRange timeRange, Map<String, String> contexts, Schema schema, String featureName, List<FeatureBucket> featureBucketsFromModels, HistoricalDataConfig historicalDataConfig) {
        String featureBucketConfName = historicalDataConfig.getFeatureBucketConfName();
        TimeRange inMemoryTimeRange = getMissingTimeRange(timeRange, featureBucketsFromModels, new Function<Object, Instant>() {
            @Override
            public Instant apply(Object f) {
                return ((FeatureBucket) f).getEndTime();
            }
        });

        Instant start = TimeService.floorTime(inMemoryTimeRange.getStart(), FixedDurationStrategy.DAILY.toDuration());
        Instant end = TimeService.floorTime(inMemoryTimeRange.getEnd(), FixedDurationStrategy.DAILY.toDuration());
        TimeRange flooredTimeRange = new TimeRange(start, end);
        List<TimeRange> dayPartitions = FixedDurationStrategyUtils.splitTimeRangeByStrategy(flooredTimeRange, FixedDurationStrategy.DAILY);
        dayPartitions.add(new TimeRange(end, inMemoryTimeRange.getEnd())); //add last partial day
        String contextField = contexts.keySet().stream().findFirst().get();
        String contextValue = contexts.get(contextField);
        String contextId = FeatureBucketUtils.buildContextId(contexts);

        List<FeatureBucket> featureBucketsInMemory = new ArrayList<>();

        // go over days in the range
        for (TimeRange dayPartition : dayPartitions) {

            PageIterator<EnrichedRecord> pageIterator = getEnrichedRecordPageIterator(contextField, contextValue, schema, dayPartition);
            FeatureBucketStrategyData featureBucketStrategyData =
                    new FeatureBucketStrategyData(FixedDurationStrategy.DAILY.toStrategyName(),
                            FixedDurationStrategy.DAILY.toStrategyName(),
                            dayPartition);
            List<FeatureBucket> featureBuckets = inMemoryFeatureBucketAggregator.aggregate(pageIterator, featureBucketConfName, featureBucketStrategyData);
            featureBuckets.removeIf(featureBucket -> !contextId.equals(featureBucket.getContextId()));
            featureBucketsInMemory.addAll(featureBuckets);
        }
        return featureBucketsInMemory;
    }

    private List<AccumulatedAggregationFeatureRecord> calculateAccumulatedAggregationFeatures(TimeRange timeRange,  Map<String, String> contexts, Schema schema, String featureName, List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeatureRecordsFromModels) {

        TimeRange inMemoryTimeRange = getMissingTimeRange(timeRange, accumulatedAggregationFeatureRecordsFromModels, new Function<Object, Instant>() {
            @Override
            public Instant apply(Object f) {
                return ((AccumulatedAggregationFeatureRecord) f).getEndInstant();
            }
        });

        List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeatureRecordsInMemory = new ArrayList<AccumulatedAggregationFeatureRecord>();

        Instant start = TimeService.floorTime(inMemoryTimeRange.getStart(), FixedDurationStrategy.DAILY.toDuration());
        Instant end = TimeService.floorTime(inMemoryTimeRange.getEnd().minus(1, ChronoUnit.DAYS), FixedDurationStrategy.DAILY.toDuration());
        TimeRange flooredTimeRange = new TimeRange(start, end);
        List<TimeRange> dayPartitions = FixedDurationStrategyUtils.splitTimeRangeByStrategy(flooredTimeRange, FixedDurationStrategy.DAILY);
        dayPartitions.add(new TimeRange(end, inMemoryTimeRange.getEnd())); //add last partial day
        String contextField = contexts.keySet().stream().findFirst().get();
        String contextValue = contexts.get(contextField);
        String contextId = FeatureBucketUtils.buildContextId(contexts);

        // go over days in the range
        for (TimeRange dayPartition : dayPartitions) {
            List<FeatureBucket> featureBucketsInMemory = new ArrayList<FeatureBucket>();

            // create feature bucket per hour
            for (TimeRange hourPartition : FixedDurationStrategyUtils.splitTimeRangeByStrategy(dayPartition, FixedDurationStrategy.HOURLY)) {

                PageIterator<EnrichedRecord> pageIterator = getEnrichedRecordPageIterator(contextField, contextValue, schema, hourPartition);

                FeatureBucketStrategyData featureBucketStrategyData =
                        new FeatureBucketStrategyData(FixedDurationStrategy.HOURLY.toStrategyName(),
                                FixedDurationStrategy.HOURLY.toStrategyName(),
                                hourPartition);
                List<FeatureBucket> featureBuckets = inMemoryFeatureBucketAggregator.aggregate(pageIterator, schema.getName(), contextField,
                        Collections.emptyList(), featureBucketStrategyData);
                featureBuckets.removeIf(featureBucket -> !contextId.equals(featureBucket.getContextId()));
                featureBucketsInMemory.addAll(featureBuckets);
            }

            // aggregate the entire day
            List<AdeAggregationRecord> adeAggregationRecords = aggregationRecordsCreator.createAggregationRecords(featureBucketsInMemory);
            if (CollectionUtils.isNotEmpty(adeAggregationRecords)) {
                try {
                    accumulatorService.accumulate(adeAggregationRecords.stream().filter(e -> e.getFeatureName().equals(featureName)).collect(Collectors.toList()));
                    accumulatedAggregationFeatureRecordsInMemory.addAll(accumulationsCache.getAllAccumulatedRecords());
                } finally {
                    // TODO: add non threadsafe comment
                    accumulationsCache.clean();
                }
            }
        }

        return accumulatedAggregationFeatureRecordsInMemory;
    }


    private List<DailyHistogram<String, Number>> convertFeatureBucketsToHistograms(String featureName, List<FeatureBucket> featureBuckets) {
        List<DailyHistogram<String, Number>> dailyHistogramList = new <DailyHistogram<String, Number>>ArrayList();
        if (CollectionUtils.isNotEmpty(featureBuckets)) {
            for (FeatureBucket featureBucket : featureBuckets) {
                LocalDate day = featureBucket.getStartTime().atZone(ZoneOffset.UTC).toLocalDate();
                Feature feature = featureBucket.getAggregatedFeatures().get(featureName);
                if (feature == null) {
                    //logger.warn("Cannot find feature {} in feature bucket with ID {}", featureName, featureBucket.getBucketId());
                    continue;
                }

                Object featureValue = feature.getValue();

                Map histogramMap = null;
                if (feature.getValue() instanceof GenericHistogram) {
                    histogramMap = ((GenericHistogram) featureValue).getHistogramMap();
                } else if (feature.getValue() instanceof AggrFeatureValue) {
                    histogramMap = (Map) ((AggrFeatureValue) featureValue).getValue();
                } else {
                    logger.debug("Cannot find histogram data for feature {} in bucket id {}", featureName, featureBucket.getBucketId());
                    continue;
                }

                DailyHistogram<String, Number> dailyHistogram = new DailyHistogram<String, Number>(day, histogramMap);
                dailyHistogramList.add(dailyHistogram);
            }
        }
        return dailyHistogramList;
    }

    private List<DailyHistogram<Integer, Double>> convertAggregationFeaturesToHistograms(List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeatureRecords) {

        List<DailyHistogram<Integer, Double>> dailyHistogramList = new <DailyHistogram<String, Number>>ArrayList();
        if (CollectionUtils.isNotEmpty(accumulatedAggregationFeatureRecords)) {

            for (AccumulatedAggregationFeatureRecord accumulatedAggregationFeatureRecord : accumulatedAggregationFeatureRecords) {
                LocalDate day = accumulatedAggregationFeatureRecord.getStartInstant().atZone(ZoneOffset.UTC).toLocalDate();
                Map<Integer, Double> aggregatedFeatureValues = accumulatedAggregationFeatureRecord.getAggregatedFeatureValues();
                if (aggregatedFeatureValues == null) {
                    //logger.warn("Cannot find feature {} in feature bucket with ID {}", featureName, featureBucket.getBucketId());
                    continue;
                }

                DailyHistogram<Integer, Double> dailyHistogram = new DailyHistogram<Integer, Double>(day, aggregatedFeatureValues);
                dailyHistogramList.add(dailyHistogram);
            }
        }
        return dailyHistogramList;
    }


    private PageIterator<EnrichedRecord> getEnrichedRecordPageIterator(String contextType, String contextValue, Schema schema, TimeRange inMemoryTimeRange) {
        Set<String> user = new HashSet<String>(Arrays.asList(contextValue));
        EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, 1000, 100, contextType);
        EnrichedRecordsMetadata enrichedRecordsMetadata = new EnrichedRecordsMetadata(schema.getName().toLowerCase(), inMemoryTimeRange.getStart(), inMemoryTimeRange.getEnd());
        int total = (int) enrichedDataStore.countRecords(enrichedRecordsMetadata, contextType, contextValue);
        return enrichedRecordPaginationService.getPageIterator(schema.name().toLowerCase(), inMemoryTimeRange, user, total);
    }

    private TimeRange getMissingTimeRange(TimeRange timeRange, List<?> list, Function<Object, Instant> mapToInstant) {
        if (CollectionUtils.isEmpty(list)) {
            return timeRange;
        }

        // find the largest instant in the the models data and add 1 ms.
        Instant startInstantForMemory = list.stream()
                .map(mapToInstant)
                .max(Instant::compareTo)
                .get()
                .plus(1, ChronoUnit.MILLIS);
        return new TimeRange(startInstantForMemory, timeRange.getEnd());
    }
}
