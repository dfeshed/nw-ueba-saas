package presidio.ade.sdk.common;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.smart.record.conf.SmartRecordConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.TtlService;
import org.springframework.data.util.Pair;
import org.springframework.util.Assert;
import presidio.ade.domain.pagination.smart.MultipleSmartCollectionsPaginationService;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.domain.store.enriched.TtlServiceAwareEnrichedDataStore;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.sdk.historical_runs.HistoricalRunParams;
import presidio.ade.sdk.online_run.OnlineRunParams;

import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * @author Barak Schuster
 */
public class AdeManagerSdkImpl implements AdeManagerSdk {
    private TtlServiceAwareEnrichedDataStore ttlServiceAwareEnrichedDataStore;
    private SmartDataReader smartDataReader;
    private ScoredEnrichedDataStore scoredEnrichedDataStore;
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    private FeatureBucketReader featureBucketReader;
    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;
    private SmartRecordConfService smartRecordConfService;
    private Map<String, List<String>> aggregationNameToAdeEventTypeMap;
    private Map<String, String> aggregationNameToFeatureBucketConfName;
    private TtlService ttlService;

    public AdeManagerSdkImpl(
            TtlServiceAwareEnrichedDataStore ttlServiceAwareEnrichedDataStore,
            SmartDataReader smartDataReader,
            ScoredEnrichedDataStore scoredEnrichedDataStore,
            AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService,
            FeatureBucketReader featureBucketReader,
            AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader,
            SmartRecordConfService smartRecordConfService,
            TtlService ttlService) {

        this.ttlServiceAwareEnrichedDataStore = ttlServiceAwareEnrichedDataStore;
        this.smartDataReader = smartDataReader;
        this.scoredEnrichedDataStore = scoredEnrichedDataStore;
        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.featureBucketReader = featureBucketReader;
        this.aggregationEventsAccumulationDataReader = aggregationEventsAccumulationDataReader;
        this.smartRecordConfService = smartRecordConfService;
        this.ttlService = ttlService;
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
        ttlServiceAwareEnrichedDataStore.store(metadata, records);
    }

    @Override
    public List<AdeScoredEnrichedRecord> findScoredEnrichedRecords(List<String> eventIds, String adeEventType, Double scoreThreshold) {
        return scoredEnrichedDataStore.findScoredEnrichedRecords(eventIds, adeEventType, scoreThreshold);
    }

    @Override
    public List<String> findScoredEnrichedRecordsDistinctFeatureValues(String adeEventType, Pair<String, String> contextFieldAndValue, TimeRange timeRange, String distinctFieldName, Double scoreThreshold) {
        return scoredEnrichedDataStore.findScoredEnrichedRecordsDistinctFeatureValues(adeEventType,contextFieldAndValue,timeRange,distinctFieldName,scoreThreshold);
    }

    @Override
    public List<AccumulatedAggregationFeatureRecord> getAccumulatedAggregatedFeatureEvents(String featureName, String contextId, TimeRange timeRange) {
        return aggregationEventsAccumulationDataReader.findAccumulatedEventsByContextIdAndStartTimeRange(featureName,contextId,timeRange);
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
    public PageIterator<SmartRecord> getSmartRecords(int pageSize, int maxGroupSize, TimeRange timeRange, int scoreThreshold) {
        Collection<String> configurationNames = smartRecordConfService.getSmartRecordConfs().stream().map(SmartRecordConf::getName).collect(Collectors.toSet());
        return new MultipleSmartCollectionsPaginationService(configurationNames, smartDataReader, pageSize, maxGroupSize).getPageIterator(timeRange, scoreThreshold);
    }

    @Override
    public Set<DirtyDataMarker> getDirtyDataMarkers() {
        // TODO: Implement
        return null;
    }

    @Override
    public void setDirtyDataMarkers(Set<DirtyDataMarker> dirtyDataMarkers) {
        // TODO: Implement
    }

    @Override
    public void cleanupEnrichedData(Instant until, Duration enrichedTtl, Duration enrichedCleanupInterval) {
        String storeName = ttlServiceAwareEnrichedDataStore.getStoreName();
        ttlService.cleanupCollections(storeName, until, enrichedTtl, enrichedCleanupInterval);
    }
}
