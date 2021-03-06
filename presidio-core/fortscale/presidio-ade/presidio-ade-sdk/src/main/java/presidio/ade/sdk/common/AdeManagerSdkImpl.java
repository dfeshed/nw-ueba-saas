package presidio.ade.sdk.common;

import fortscale.aggregation.feature.bucket.*;
import fortscale.aggregation.feature.bucket.strategy.FeatureBucketStrategyData;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import fortscale.utils.store.StoreManager;
import org.springframework.data.util.Pair;
import org.springframework.util.Assert;
import presidio.ade.domain.pagination.smart.MultipleSmartCollectionsPaginationService;
import presidio.ade.domain.pagination.smart.SmartPaginationService;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.domain.store.enriched.StoreManagerAwareEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.smart.SmartRecordsMetadata;
import presidio.ade.sdk.aggregation_records.splitter.ScoreAggregationRecordContributors;
import presidio.ade.sdk.aggregation_records.splitter.ScoreAggregationRecordSplitter;
import presidio.ade.sdk.historical_runs.HistoricalRunParams;
import presidio.ade.sdk.online_run.OnlineRunParams;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * @author Barak Schuster
 */
public class AdeManagerSdkImpl implements AdeManagerSdk {
    private static final String SCHEMA = "schema";

    private StoreManagerAwareEnrichedDataStore storeManagerAwareEnrichedDataStore;
    private SmartDataReader smartDataReader;
    private ScoredEnrichedDataStore scoredEnrichedDataStore;
    private BucketConfigurationService bucketConfigurationService;
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    private FeatureBucketReader featureBucketReader;
    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;
    private SmartRecordConfService smartRecordConfService;
    private Map<String, List<String>> aggregationNameToAdeEventTypeMap;
    private Map<String, String> aggregationNameToFeatureBucketConfName;
    private StoreManager storeManager;
    private InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator;
    private ScoreAggregationRecordSplitter scoreAggregationRecordSplitter;

    public AdeManagerSdkImpl(
            StoreManagerAwareEnrichedDataStore storeManagerAwareEnrichedDataStore,
            SmartDataReader smartDataReader,
            ScoredEnrichedDataStore scoredEnrichedDataStore,
            BucketConfigurationService bucketConfigurationService,
            AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
            FeatureBucketReader featureBucketReader,
            AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader,
            SmartRecordConfService smartRecordConfService,
            StoreManager storeManager,
            InMemoryFeatureBucketAggregator inMemoryFeatureBucketAggregator,
            ScoreAggregationRecordSplitter scoreAggregationRecordSplitter) {

        this.storeManagerAwareEnrichedDataStore = storeManagerAwareEnrichedDataStore;
        this.smartDataReader = smartDataReader;
        this.scoredEnrichedDataStore = scoredEnrichedDataStore;
        this.bucketConfigurationService = bucketConfigurationService;
        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.featureBucketReader = featureBucketReader;
        this.aggregationEventsAccumulationDataReader = aggregationEventsAccumulationDataReader;
        this.smartRecordConfService = smartRecordConfService;
        this.storeManager = storeManager;
        this.inMemoryFeatureBucketAggregator = inMemoryFeatureBucketAggregator;
        this.scoreAggregationRecordSplitter = scoreAggregationRecordSplitter;
    }

    @Override
    public PrepareRunResult prepareHistoricalRun(RunId runId, HistoricalRunParams params) {
        // TODO: Implement
        return null;
    }

    @Override
    public void changeHistoricalRunParams(RunId runId, HistoricalRunParams params) {
        // TODO: Implement
    }

    @Override
    public void processNextHistoricalTimeRange(RunId runId) {
        // TODO: Implement
    }

    @Override
    public HistoricalRunParams getLatestHistoricalTimeRangeProcessed(RunId runId) {
        // TODO: Implement
        return null;
    }

    @Override
    public Set<HistoricalRunParams> getHistoricalTimeRangesInProgress(RunId runId) {
        // TODO: Implement
        return null;
    }

    @Override
    public RunStatus getHistoricalRunStatus(RunId runId) {
        // TODO: Implement
        return null;
    }

    @Override
    public void pauseHistoricalRun(RunId runId) {
        // TODO: Implement
    }

    @Override
    public void resumeHistoricalRun(RunId runId) {
        // TODO: Implement
    }

    @Override
    public void stopHistoricalRun(RunId runId) {
        // TODO: Implement
    }

    @Override
    public void stopHistoricalRunForcefully(RunId runId) {
        // TODO: Implement
    }

    @Override
    public PrepareRunResult prepareOnlineRun(OnlineRunParams params) {
        // TODO: Implement
        return null;
    }

    @Override
    public RunId getOnlineRunId() {
        // TODO: Implement
        return null;
    }

    @Override
    public void changeOnlineRunParams(OnlineRunParams params) {
        // TODO: Implement
    }

    @Override
    public void processNextOnlineTimeRange() {
        processNextHistoricalTimeRange(getOnlineRunId());
    }

    @Override
    public OnlineRunParams getLatestOnlineTimeRangeProcessed() {
        // TODO: Implement
        return null;
    }

    @Override
    public Set<OnlineRunParams> getOnlineTimeRangesInProgress() {
        // TODO: Implement
        return null;
    }

    @Override
    public RunStatus getOnlineRunStatus() {
        return getHistoricalRunStatus(getOnlineRunId());
    }

    @Override
    public void pauseOnlineRun() {
        pauseHistoricalRun(getOnlineRunId());
    }

    @Override
    public void resumeOnlineRun() {
        resumeHistoricalRun(getOnlineRunId());
    }

    @Override
    public void stopOnlineRun() {
        stopHistoricalRun(getOnlineRunId());
    }

    @Override
    public void stopOnlineRunForcefully() {
        stopHistoricalRunForcefully(getOnlineRunId());
    }

    @Override
    public void cleanup(AdeDataStoreCleanupParams params) {
        // TODO: Implement
    }

    @Override
    public void storeEnrichedRecords(EnrichedRecordsMetadata metadata, List<? extends EnrichedRecord> records) {
        StoreMetadataProperties storeMetadataProperties = createStoreMetadataProperties(metadata.getAdeEventType());
        storeManagerAwareEnrichedDataStore.store(metadata, records, storeMetadataProperties);
    }

    @Override
    public void cleanupEnrichedRecords(AdeDataStoreCleanupParams adeDataStoreCleanupParams) {
        String storeName = storeManagerAwareEnrichedDataStore.getStoreName();
        StoreMetadataProperties storeMetadataProperties = createStoreMetadataProperties(adeDataStoreCleanupParams.getAdeEventType());
        storeManager.cleanupCollections(storeName, new TimeRange(adeDataStoreCleanupParams.getStartDate(), adeDataStoreCleanupParams.getEndDate()), storeMetadataProperties);
    }

    @Override
    public FeatureBucket createFeatureBucketFromEnrichedRecords(
            String featureBucketConfName, TimeRange timeRange, Map<String, String> context, int pageSize) {

        FeatureBucketConf featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
        // Assume there is only one ADE event type.
        String adeEventType = featureBucketConf.getAdeEventTypes().get(0);
        String strategyName = featureBucketConf.getStrategyName();
        PageIterator<EnrichedRecord> pageIterator = new PageIterator<>() {
            private final long count = storeManagerAwareEnrichedDataStore.count(adeEventType, timeRange, context);
            private final long numberOfPages = (count + pageSize - 1) / pageSize;
            private long nextPageIndex = 0;

            @Override
            public boolean hasNext() {
                return nextPageIndex < numberOfPages;
            }

            @Override
            public List<EnrichedRecord> next() {
                List<EnrichedRecord> enrichedRecords = storeManagerAwareEnrichedDataStore.find(
                        adeEventType, timeRange, context, nextPageIndex * pageSize, pageSize);
                ++nextPageIndex;
                return enrichedRecords;
            }
        };
        FeatureBucketStrategyData featureBucketStrategyData = new FeatureBucketStrategyData(strategyName, strategyName, timeRange);
        return inMemoryFeatureBucketAggregator.createFeatureBucket(pageIterator, featureBucketConfName, featureBucketStrategyData);
    }

    @Override
    public List<AdeScoredEnrichedRecord> findScoredEnrichedRecords(List<String> eventIds, String adeEventType, Double scoreThreshold) {
        return scoredEnrichedDataStore.findScoredEnrichedRecords(eventIds, adeEventType, scoreThreshold);
    }

    /**
     * This method is a hack. Should be removed!!!
     * @param adeEventType type of {@link AdeScoredEnrichedRecord} - symbolize the scored feature name
     * @param contextFieldAndValue i.e. "userId","someUser"
     * @param timeRange time line filtering param
     * @param distinctFieldName field to retrieve distinct values on
     * @param scoreThreshold distinct values would be fetched only for records having score greater then this value
     */
    @Override
    public List<String> findScoredEnrichedRecordsDistinctFeatureValues(String adeEventType, Pair<String, String> contextFieldAndValue, TimeRange timeRange, String distinctFieldName, Double scoreThreshold) {
        return scoredEnrichedDataStore.findScoredEnrichedRecordsDistinctFeatureValues(adeEventType,contextFieldAndValue,timeRange,distinctFieldName,scoreThreshold);
    }

    @Override
    public List<AccumulatedAggregationFeatureRecord> getAccumulatedAggregatedFeatureEventsByContextId(String featureName, String contextId, TimeRange timeRange) {
        return aggregationEventsAccumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(featureName,contextId,timeRange);
    }

    @Override
    public List<AccumulatedAggregationFeatureRecord> getAccumulatedAggregatedFeatureEvents(String featureName, TimeRange timeRange) {
        return aggregationEventsAccumulationDataReader.findAccumulatedEventsByStartTimeRange(featureName, timeRange);
    }

    @Override
    public Map<String, List<String>> getAggregationNameToAdeEventTypeMap() {
        // lazy initiation of the map
        if(aggregationNameToAdeEventTypeMap != null)
        {
            return aggregationNameToAdeEventTypeMap;
        }
        // get only score aggregation confs
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfList = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        Assert.notEmpty(aggregatedFeatureEventConfList,"no score aggregations are defined. should have at least one");
        aggregationNameToAdeEventTypeMap =
                aggregatedFeatureEventConfList.stream()
                        // map conf to feature name, bucketConf.adeEventTypes
                        .map(aggregatedFeatureEventConf -> new SimpleEntry<>(aggregatedFeatureEventConf.getName(), aggregatedFeatureEventConf.getBucketConf().getAdeEventTypes()))
                        // collect to map :)
                        .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));

        return aggregationNameToAdeEventTypeMap;
    }

    @Override
    public Map<String, String> getAggregationNameToFeatureBucketConfNameMap() {
        if(aggregationNameToFeatureBucketConfName != null)
        {
            return aggregationNameToFeatureBucketConfName;
        }
        aggregationNameToFeatureBucketConfName =
                aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList().stream().collect(Collectors.toMap(AggregatedFeatureEventConf::getName, AggregatedFeatureEventConf::getBucketConfName));
        return aggregationNameToFeatureBucketConfName;
    }

    @Override
    public List<FeatureBucket> findFeatureBuckets(String contextId, String bucketConfName, TimeRange timeRange) {
        return featureBucketReader.getFeatureBuckets(bucketConfName,contextId,timeRange);
    }

    @Override
    public ScoreAggregationRecordContributors splitScoreAggregationRecordToContributors(
            AdeAggregationRecord scoreAggregationRecord, List<String> splitFieldNames) {

        return scoreAggregationRecordSplitter.split(scoreAggregationRecord, splitFieldNames);
    }

    @Override
    public PageIterator<SmartRecord> getSmartRecords(int pageSize, int maxGroupSize, TimeRange timeRange, int scoreThreshold) {
        Collection<String> configurationNames = smartRecordConfService.getSmartRecordConfs().stream().map(SmartRecordConf::getName).collect(Collectors.toSet());
        return new MultipleSmartCollectionsPaginationService(configurationNames, smartDataReader, pageSize, maxGroupSize).getPageIterator(timeRange, scoreThreshold);
    }

    @Override
    public List<PageIterator<SmartRecord>> getSmartRecords(int pageSize, int maxGroupSize, TimeRange timeRange, int scoreThreshold, String configurationName) {
        return new SmartPaginationService(smartDataReader, pageSize, maxGroupSize).getPageIterators(configurationName, timeRange, scoreThreshold);
    }

    @Override
    public int getNumOfDistinctSmartEntities(TimeRange timeRange, String configurationName) {
        SmartRecordsMetadata smartRecordsMetadata = new SmartRecordsMetadata(configurationName, timeRange.getStart(), timeRange.getEnd());
        List<ContextIdToNumOfItems> contextIdToNumOfSmarts = smartDataReader.aggregateContextIdToNumOfEvents(smartRecordsMetadata, 0);
        if(contextIdToNumOfSmarts == null) {
            return 0;
        }
        return contextIdToNumOfSmarts.size();
    }

    @Override
    public Set<DirtyDataMarker> getDirtyDataMarkers() {
        // TODO: Implement
        return null;
    }

    @Override
    public void setDirtyDataMarkers(Set<DirtyDataMarker> dirtyDataMarkers) {
        // TODO: Implement TODO
    }

    @Override
    public void cleanupEnrichedData(Instant until, Duration enrichedTtl, Duration enrichedCleanupInterval) {
        String storeName = storeManagerAwareEnrichedDataStore.getStoreName();
        storeManager.cleanupCollections(storeName, until, enrichedTtl, enrichedCleanupInterval);
    }

    /**
     * Create StoreMetadataProperties
     *
     * @return StoreMetadataProperties
     */
    private StoreMetadataProperties createStoreMetadataProperties(String schema){
        StoreMetadataProperties storeMetadataProperties = new StoreMetadataProperties();
        storeMetadataProperties.setProperty(SCHEMA, schema);
        return storeMetadataProperties;
    }
}
