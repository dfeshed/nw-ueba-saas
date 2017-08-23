package presidio.ade.sdk.common;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketReader;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.springframework.data.util.Pair;
import org.springframework.util.Assert;
import presidio.ade.domain.pagination.smart.ScoreThresholdSmartPaginationService;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.domain.store.scored.ScoredEnrichedDataStore;
import presidio.ade.sdk.historical_runs.HistoricalRunParams;
import presidio.ade.sdk.online_run.OnlineRunParams;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * @author Barak Schuster
 */
public class AdeManagerSdkImpl implements AdeManagerSdk {
    private EnrichedDataStore enrichedDataStore;
    private SmartDataReader smartDataReader;
    private ScoredEnrichedDataStore scoredEnrichedDataStore;
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    private Map<String, List<String>> aggregationNameToAdeEventTypeMap;
    private Map<String, String> aggregationNameToFeatureBucketConfName;
    private FeatureBucketReader featureBucketReader;
    private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;

    public AdeManagerSdkImpl(EnrichedDataStore enrichedDataStore, SmartDataReader smartRecordDataReader, ScoredEnrichedDataStore scoredEnrichedDataStore, AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService, FeatureBucketReader featureBucketReader, AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader) {
        this.enrichedDataStore = enrichedDataStore;
        this.smartDataReader = smartDataReader;
        this.scoredEnrichedDataStore = scoredEnrichedDataStore;
        this.aggregatedFeatureEventsConfService = aggregatedFeatureEventsConfService;
        this.featureBucketReader = featureBucketReader;
        this.aggregationEventsAccumulationDataReader = aggregationEventsAccumulationDataReader;
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
                        // map conf to featurename, bucketConf.adeEventTypes
                        .map(aggregatedFeatureEventConf -> new SimpleEntry<String, List<String>>(aggregatedFeatureEventConf.getName(), aggregatedFeatureEventConf.getBucketConf().getAdeEventTypes()))
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
    public PageIterator<SmartRecord> getSmartRecords(TimeRange timeRange, int pageSize, int scoreThreshold) {
        ScoreThresholdSmartPaginationService smartPaginationService = new ScoreThresholdSmartPaginationService(smartDataReader, pageSize);
        return smartPaginationService.getPageIterator(timeRange, scoreThreshold);
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

}
