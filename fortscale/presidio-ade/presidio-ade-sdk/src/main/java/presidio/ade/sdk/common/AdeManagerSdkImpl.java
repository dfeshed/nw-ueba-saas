package presidio.ade.sdk.common;

import fortscale.accumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.domain.SMART.EntityEvent;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;
import org.springframework.util.Assert;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.domain.store.smart.SmartDataStore;
import presidio.ade.domain.store.smart.SmartPageIterator;
import presidio.ade.sdk.historical_runs.HistoricalRunParams;
import presidio.ade.sdk.online_run.OnlineRunParams;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

/**
 * @author Barak Schuster
 */
public class AdeManagerSdkImpl implements AdeManagerSdk {
    private EnrichedDataStore enrichedDataStore;
    private SmartDataStore smartDataStore;
    private ScoredEnrichedDataStore scoredEnrichedDataStore;
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    private Map<String, List<String>> aggregationNameToAdeEventTypeMap;
    public AdeManagerSdkImpl(EnrichedDataStore enrichedDataStore, SmartDataStore smartDataStore, ScoredEnrichedDataStore scoredEnrichedDataStore, AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService) {
        this.enrichedDataStore = enrichedDataStore;
        this.smartDataStore = smartDataStore;
        this.scoredEnrichedDataStore = scoredEnrichedDataStore;
        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
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
        enrichedDataStore.store(metadata, records);
    }

    @Override
    public List<AdeScoredEnrichedRecord> findScoredEnrichedRecords(List<String> eventIds, String adeEventType, Double scoreThreshold) {
        return scoredEnrichedDataStore.findScoredEnrichedRecords(eventIds,adeEventType, scoreThreshold);
    }

    @Override
    public List<String> findScoredEnrichedRecordsDistinctFeatureValues(String adeEventType, Pair<String, String> contextFieldAndValue, TimeRange timeRange, String distinctFieldName, Double scoreThreshold) {
        return scoredEnrichedDataStore.findScoredEnrichedRecordsDistinctFeatureValues(adeEventType,contextFieldAndValue,timeRange,distinctFieldName,scoreThreshold);
    }

    @Override
    public List<AdeAggregationRecord> getAggregationRecords(String featureName, String contextId, TimeRange timeRange) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AccumulatedAggregatedFeatureEvent> getAccumulatedAggregatedFeatureEvents(String featureName, String contextId, TimeRange timeRange) {
        throw new UnsupportedOperationException();
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
                        // map conf to featurename, bucketConf.adeEventTypes
                        .map(aggregatedFeatureEventConf -> new SimpleEntry<String, List<String>>(aggregatedFeatureEventConf.getName(), aggregatedFeatureEventConf.getBucketConf().getAdeEventTypes()))
                        // collect to map :)
                        .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue));

        return aggregationNameToAdeEventTypeMap;
    }

    @Override
    public PageIterator<EntityEvent> getSmartRecords(TimeRange timeRange, int pageSize, int scoreThreshold) {
        // TODO: Replace temporary implementation
        return new SmartPageIterator<>(smartDataStore, timeRange, scoreThreshold);
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
    public List<FeatureBucket> getFeatureBuckets(String featureBucketName, String aggregatedFeatureName, String contextId, TimeRange timeRange) {
        throw new UnsupportedOperationException();
    }
}
