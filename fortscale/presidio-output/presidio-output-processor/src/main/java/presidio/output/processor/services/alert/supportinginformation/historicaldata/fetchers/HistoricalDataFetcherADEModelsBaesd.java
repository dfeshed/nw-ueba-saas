package presidio.output.processor.services.alert.supportinginformation.historicaldata.fetchers;

import fortscale.accumulator.aggregation.AccumulationsCache;
import fortscale.accumulator.aggregation.AccumulatorService;
import fortscale.aggregation.creator.AggregationRecordsCreator;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketUtils;
import fortscale.aggregation.feature.bucket.InMemoryFeatureBucketAggregator;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.common.feature.Feature;
import fortscale.common.general.CommonStrings;
import fortscale.common.general.Schema;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyUtils;
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
 *
 * This fetcher is based on data that are already written for models creation and therefore
 * saves I/O operations
 */
public class HistoricalDataFetcherADEModelsBaesd implements HistoricalDataFetcher {


    AdeManagerSdk adeManagerSdk;

    EnrichedDataStore enrichedDataStore;

    InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;

    AggregationRecordsCreator aggregationRecordsCreator;

    AccumulatorService accumulatorService;

    AccumulationsCache accumulationsCache;


    public HistoricalDataFetcherADEModelsBaesd(AdeManagerSdk adeManagerSdk, EnrichedDataStore enrichedDataStore, InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator, AggregationRecordsCreator aggregationRecordsCreator, AccumulatorService accumulatorService, AccumulationsCache accumulationsCache) {
        this.adeManagerSdk = adeManagerSdk;
        this.enrichedDataStore = enrichedDataStore;
        this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
        this.aggregationRecordsCreator = aggregationRecordsCreator;
        this.accumulatorService = accumulatorService;
        this.accumulationsCache = accumulationsCache;
    }

    /**
     * Histogram for single feature are fetched from the FeaureBuckets collections
     * (aggr_<feature_name>. the missing data are completed using the aggregation framework (i.e: presidio-aggragtion package)
     *
     * @param timeRange
     * @param contextField the context id (i.e userId)
     * @param contextValue the context value (i.e the user name)
     * @param schema the schema for which to populate historical behavior
     * @param featureName the feature for which to populate historical behavior (e.g: login time)
     *
     * @return List of feature histogram for each day in the range
     *         e.g:   Feature: operationType, Date: 01/01/2017, Histogram {FILE_MOVED:5, FILE_COPY:9, ACCESS_RIGHTS_CHANGED:1}
     *                Feature: operationType, Date: 01/02/2017, Histogram {FILE_OPENED:10, ACCESS_RIGHTS_CHANGED:1}
     */
    @Override
    public List<DailyHistogram<String>> getDailyHistogramsForFeature(TimeRange timeRange, String contextField, String contextValue, Schema schema, String featureName, HistoricalDataConfig historicalDataConfig) {

        // get historical data from models
        String contextId = FeatureBucketUtils.buildContextId(getContextIdMap(contextField, contextValue));
        String featureBucketConfName = historicalDataConfig.getFeatureBucketConfName();//getFeatureBucketConfName(schema, featureName);
        List<FeatureBucket> featureBucketsFromModels = adeManagerSdk.findFeatureBuckets(contextId, featureBucketConfName, timeRange);

        // complete historical data in memory
        List<FeatureBucket> featureBucketsInMemory = calculateFeatureBuckets(timeRange, contextValue, schema, featureName, featureBucketsFromModels, historicalDataConfig);

        // translate FeatureBuckets to dailyHistogram
        List<DailyHistogram<String>> dailyHistogramList = new ArrayList<DailyHistogram<String>>();
        dailyHistogramList.addAll(convertFeatureBucketsToHistograms(featureName, featureBucketsFromModels));
        dailyHistogramList.addAll(convertFeatureBucketsToHistograms(featureName, featureBucketsInMemory));

        return  dailyHistogramList;
    }

    /**
     * Histogram for aggregated feature are fetched from the AccumulatedAggregationFeatureRecord collections
     * (acc_<feature_name>. the missing data are completed using the aggregation framework (i.e: presidio-aggragtion package)
     *
     * @param timeRange
     * @param contextField the context id (i.e userId)
     * @param contextValue the context value (i.e the user name)
     * @param schema the schema for which to populate historical behavior
     * @param featureName the feature for which to populate historical behavior (e.g: login time)
     *
     * @return List of aggregated feature histogram for each day in the range. The histogram key is hour of the day (0 .. 23)
     *         e.g:   Aggregated Feature: numberOfFailedAuthentications, Day: 01/01/2017, histogram {0:1, 2:0, 3:0 ...}
     *                Aggregated Feature: numberOfFailedAuthentications, Day: 01/02/2017, histogram {0:0, 2:1, 3:0 ...}
     */
    @Override
    public List<DailyHistogram<Integer>> getDailyHistogramsForAggregatedFeature(TimeRange timeRange, String contextField, String contextValue, Schema schema, String featureName, HistoricalDataConfig historicalDataConfig) {
        // get historical data from models
        String contextId = //FeatureBucketUtils.buildContextId(getContextIdMap(contextField, contextValue));
                AdeContextualAggregatedRecord.getAggregatedFeatureContextId(getContextIdMap(contextField, contextValue));
        List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeatureRecordsFromModels = adeManagerSdk.getAccumulatedAggregatedFeatureEvents(featureName,contextId,  timeRange);

        // complete historical data in memory
        List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeaturesInMemory = calculateAccumulatedAggregationFeatures(timeRange, contextValue, schema, featureName, accumulatedAggregationFeatureRecordsFromModels);

        // translate AccumulatedAggregationFeatureRecord to dailyHistogram
        List<DailyHistogram<Integer>> dailyHistogramList = new ArrayList<DailyHistogram<Integer>>();
        dailyHistogramList.addAll(convertAggregationFeaturesToHistograms(accumulatedAggregationFeatureRecordsFromModels));
        dailyHistogramList.addAll(convertAggregationFeaturesToHistograms(accumulatedAggregationFeaturesInMemory));

        return  dailyHistogramList;
    }


    private List<FeatureBucket> calculateFeatureBuckets(TimeRange timeRange, String contextValue, Schema schema, String featureName, List<FeatureBucket> featureBucketsFromModels, HistoricalDataConfig historicalDataConfig) {
        String featureBucketConfName = historicalDataConfig.getFeatureBucketConfName();
        TimeRange inMemoryTimeRange = getMissingTimeRange(timeRange, featureBucketsFromModels, new Function<Object, Instant>() {
            @Override
            public Instant apply(Object f) {
                return ((FeatureBucket)f).getEndTime();
            }
        });

        List<String> contextTypes = Collections.singletonList(CommonStrings.CONTEXT_USERID);
        PageIterator<EnrichedRecord> pageIterator = getEnrichedRecordPageIterator(contextValue, schema, inMemoryTimeRange);
        FeatureBucketStrategyData featureBucketStrategyData =
                new FeatureBucketStrategyData(FixedDurationStrategy.DAILY.toStrategyName(),
                                              FixedDurationStrategy.DAILY.toStrategyName(),
                                              inMemoryTimeRange);
        List<FeatureBucket> featureBucketsInMemory = inMemoryFeatureBucketAggregator.aggregate(pageIterator, contextTypes, featureBucketStrategyData);
        if (CollectionUtils.isNotEmpty(featureBucketsInMemory)) {
                //TODO: check how to run featureBuckets on single feature only
                // filter only feature buckets with the given feature bucket conf
                featureBucketsInMemory = featureBucketsInMemory.stream()
                                        .filter(f ->  f.getFeatureBucketConfName().equals(featureBucketConfName))
                                        .collect(Collectors.toList());
        }
        return featureBucketsInMemory;
    }

    private List<AccumulatedAggregationFeatureRecord> calculateAccumulatedAggregationFeatures(TimeRange timeRange, String contextValue, Schema schema, String featureName, List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeatureRecordsFromModels) {

        TimeRange inMemoryTimeRange = getMissingTimeRange(timeRange, accumulatedAggregationFeatureRecordsFromModels, new Function<Object, Instant>() {
            @Override
            public Instant apply(Object f) {
                return ((AccumulatedAggregationFeatureRecord)f).getEndInstant();
            }
        });

        List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeatureRecordsInMemory = new ArrayList<AccumulatedAggregationFeatureRecord>();

        Instant start = TimeService.floorTime(inMemoryTimeRange.getStart(), FixedDurationStrategy.DAILY.toDuration());
        Instant end = TimeService.floorTime(inMemoryTimeRange.getEnd().minus(1, ChronoUnit.DAYS), FixedDurationStrategy.DAILY.toDuration());
        TimeRange flooredTimeRange = new TimeRange(start,end);
        List<TimeRange> dayPartitions = FixedDurationStrategyUtils.splitTimeRangeByStrategy(flooredTimeRange, FixedDurationStrategy.DAILY);
        dayPartitions.add(new TimeRange(end, inMemoryTimeRange.getEnd())); //add last partial day

        // go over days in the range
        for (TimeRange dayPartition : dayPartitions) {
            List<FeatureBucket> featureBucketsInMemory = new ArrayList<FeatureBucket>();

            // create feature bucket per hour
            for (TimeRange hourPartition : FixedDurationStrategyUtils.splitTimeRangeByStrategy(dayPartition, FixedDurationStrategy.HOURLY)) {
                PageIterator<EnrichedRecord> pageIterator = getEnrichedRecordPageIterator(contextValue, schema, hourPartition);
                List<String> contextTypes = Collections.singletonList(CommonStrings.CONTEXT_USERID);
                FeatureBucketStrategyData featureBucketStrategyData =
                            new FeatureBucketStrategyData(FixedDurationStrategy.HOURLY.toStrategyName(),
                                FixedDurationStrategy.HOURLY.toStrategyName(),
                                hourPartition);
                featureBucketsInMemory.addAll(inMemoryFeatureBucketAggregator.aggregate(pageIterator, contextTypes, featureBucketStrategyData));
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


    private List<DailyHistogram<String>> convertFeatureBucketsToHistograms(String featureName, List<FeatureBucket> featureBuckets) {
        List<DailyHistogram<String>> dailyHistogramList = new <DailyHistogram<String>> ArrayList();
        if (CollectionUtils.isNotEmpty(featureBuckets)) {
            for (FeatureBucket featureBucket : featureBuckets) {
                LocalDate day = featureBucket.getStartTime().atZone(ZoneOffset.UTC).toLocalDate();
                Feature feature = featureBucket.getAggregatedFeatures().get(featureName);
                if (feature == null) {
                    //logger.warn("Cannot find feature {} in feature bucket with ID {}", featureName, featureBucket.getBucketId());
                    continue;
                }

                Object featureValue = feature.getValue();
                if (!(feature.getValue() instanceof GenericHistogram)) {
                    // logger.error("Cannot find histogram data for feature {} in bucket id {}", normalizedFeatureName, featureBucket.getBucketId());
                    continue;
                }

                Map<String, Double> histogramMap = ((GenericHistogram) featureValue).getHistogramMap();
                DailyHistogram<String> dailyHistogram = new DailyHistogram<String>(day, histogramMap);
                dailyHistogramList.add(dailyHistogram);
            }
        }
        return dailyHistogramList;
    }

    private List<DailyHistogram<Integer>> convertAggregationFeaturesToHistograms(List<AccumulatedAggregationFeatureRecord> accumulatedAggregationFeatureRecords) {

        List<DailyHistogram<Integer>> dailyHistogramList = new <DailyHistogram<String>> ArrayList();
        if (CollectionUtils.isNotEmpty(accumulatedAggregationFeatureRecords)) {

            for (AccumulatedAggregationFeatureRecord accumulatedAggregationFeatureRecord : accumulatedAggregationFeatureRecords) {
                LocalDate day = accumulatedAggregationFeatureRecord.getStartInstant().atZone(ZoneOffset.UTC).toLocalDate();
                Map<Integer, Double> aggregatedFeatureValues = accumulatedAggregationFeatureRecord.getAggregatedFeatureValues();
                if (aggregatedFeatureValues == null) {
                    //logger.warn("Cannot find feature {} in feature bucket with ID {}", featureName, featureBucket.getBucketId());
                    continue;
                }

                DailyHistogram<Integer> dailyHistogram = new DailyHistogram<Integer>(day, aggregatedFeatureValues);
                dailyHistogramList.add(dailyHistogram);
            }
        }
        return dailyHistogramList;
    }


    private PageIterator<EnrichedRecord> getEnrichedRecordPageIterator(String contextValue, Schema schema, TimeRange inMemoryTimeRange) {
        Set<String> user = new HashSet<String>(Arrays.asList(contextValue));
        EnrichedRecordPaginationService enrichedRecordPaginationService = new EnrichedRecordPaginationService(enrichedDataStore, 1000, 100, CommonStrings.CONTEXT_USERID);
        return enrichedRecordPaginationService.getPageIterator(schema.name().toLowerCase(), inMemoryTimeRange, user, 100);
    }

    private Map<String, String> getContextIdMap(String contextField, String contextValue) {
        Map<String, String> contextIdMap = new HashMap<String, String>();
        contextIdMap.put(contextField, contextValue);
        return contextIdMap;
    }

    private String getFeatureBucketConfName(Schema schema, String featureName) {
        return null;
    }

    private TimeRange getMissingTimeRange(TimeRange timeRange, List<?> list, Function<Object, Instant> mapToInstant) {
        if (CollectionUtils.isEmpty(list)){
            return timeRange;
        }

        // find the largest instant in the the models data and add 1 ms.
        Instant startInstantForMemory = list.stream()
                                        .map(mapToInstant)
                                        .max(Instant::compareTo)
                                        .get()
                                        .plus(1, ChronoUnit.MILLIS);
        return new TimeRange(startInstantForMemory,  timeRange.getEnd());
    }
}
