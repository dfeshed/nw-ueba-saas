package presidio.ade.sdk.executions.common;

import fortscale.accumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.common.event.EntityEvent;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.scored.AdeScoredRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.sdk.executions.historical.PrepareHistoricalRunTimeParams;
import presidio.ade.sdk.executions.online.PrepareOnlineRunTimeParams;

import java.util.List;
import java.util.Set;

/**
 * SDK signature that is common to all kinds of executions
 * Created by barak_schuster on 5/18/17.
 */
public interface ADEManagerSDK {

    /**
     * Executes the needed preparation steps before starting to process data
     *
     * @param runId  the execution identifier
     * @param params containing parameters related to the data time range a processing strategy
     *               i.e. the data is between "some start date" to "some end date" and should be processed in chunks of 1 Hour
     * @return the preparation result, i.e.: running, failed etc...
     */
    RunPrepResult prepareHistoricalRun(RunId runId, PrepareHistoricalRunTimeParams params);

    RunPrepResult prepareOnlineRun(PrepareOnlineRunTimeParams params);

    /**
     * @return the online execution run id
     */
    RunId getOnlineRunId();

    /**
     * change the execution params of a run id
     * usage example: when we want to change the scheduling of an execution to run once in a hour instead of once a day
     *
     * @param runId  execution identifier
     * @param params
     */
    void changeHistoricalRunTimeParams(RunId runId, PrepareHistoricalRunTimeParams params);

    void changeOnlineRunTimeParams(PrepareOnlineRunTimeParams params);

    /**
     * notify the component that the required data needed for processing the next hour is ready for use
     *
     * @param runId execution identifier
     */
    void processNextHistoricalTimeRange(RunId runId);

    default void processNextOnlineTimeRange() {
        processNextHistoricalTimeRange(getOnlineRunId());
    }

    /**
     * @param runId
     * @return the last hour that was requested to process.
     */
    PrepareHistoricalRunTimeParams getLastHistoricalProcessedEndTime(RunId runId);

    PrepareOnlineRunTimeParams getLastOnlineProcessedEndTime();

    /**
     * @param runId
     * @return currently running execution hours
     */
    Set<PrepareHistoricalRunTimeParams> getInProgressHistoricalHours(RunId runId);

    Set<PrepareOnlineRunTimeParams> getInProgressOnlineHours();


    /**
     * @param runId
     * @return the execution status
     */
    RunStatus getHistoricalRunStatus(RunId runId);
    default RunStatus getOnlineRunStatus()
    {
        return getHistoricalRunStatus(getOnlineRunId());
    }

    /**
     * cleans the data between by given params
     *
     * @param params
     */
    void cleanup(AdeDataStoreCleanupParams params);

    /**
     * pauses the execution. the ADE will pauseHistorical gracefully as fast as possible and would not continue to the nearest sub step
     *
     * @param runId the execution to be paused
     */
    void pauseHistorical(RunId runId);

    default void pauseOnline()
    {
        unpauseHistorical(getOnlineRunId());
    }

    /**
     * the execution will continue from where you left it
     *
     * @param runId the execution to be un-paused
     */
    void unpauseHistorical(RunId runId);
    default void unpauseOnline()
    {
        unpauseHistorical(getOnlineRunId());
    }


    /**
     * the execution will stop at the end of the processed day
     *
     * @param runId
     */
    void stopHistorical(RunId runId);

    default void stopOnline(RunId runId)
    {
        stopHistorical(getOnlineRunId());
    }

    /**
     * Execution should stop, no matter what is currently running (AKA SIGKILL).
     * NOTICE: you will probably leave the system in dirty-data state (not fully processed),
     * you will probably want to clean the dirty data, and then move the execution cursor to the start of the last mission
     *
     * @param runId the execution to be stopped
     */
    void stopHistoricalForcefully(RunId runId);
    default void stopOnlineForcefully()
    {
        stopHistoricalForcefully(getOnlineRunId());
    }

    /**
     * @return the data characteristics that marks records as dirty
     * dirty means that the data is not part of the models. we will not learn from it due to some reason of corruption
     */
    Set<DirtyDataMarker> getDirtyDataMarkers();

    /**
     * @param dirtyDataMarkers {@link #getDirtyDataMarkers()}
     */
    void setDirtyDataMarkers(Set<DirtyDataMarker> dirtyDataMarkers);

    /**
     * persist given records into db
     * those records will be processed whenever the relevant {@link #processNextHistoricalTimeRange(RunId)} call will occur
     *
     * @param metaData some metadata considering the data to be stored. i.e. what is the data source, what is the time range etc...
     * @param records  data to be stored
     */
    void store(EnrichedRecordsMetadata metaData, List<? extends EnrichedRecord> records);

    /**
     * returns an iterator over SMART events at a given time range
     * @param timeRange start and end time of the smarts
     * @param pageSize num of events in each page
     * @return an iterator over SMART events
     */
    PageIterator<EntityEvent> findSmartsByTime(TimeRange timeRange, int pageSize);

    /**
     * returns list of FeatureBuckets for a given context and time range
     * @param featureName
     * @param contextId i.e. username
     * @param timeRange start and end time
     * @return list of FeatureBuckets
     */
    List<FeatureBucket> findFeatureBucketsByContextAndTime(String featureName, String contextId, TimeRange timeRange);

    /**
     * returns list of Aggregated events for a given context and time range
     * @param aggregatedFeatureName
     * @param contextId i.e. username
     * @param timeRange start and end time of the events
     * @return list of Aggregated events
     */
    List<AggrEvent> findAggrEventsByContextAndTime (String aggregatedFeatureName, String contextId, TimeRange timeRange);

    /**
     * returns list of Accumulated aggregated events for a given context and time range
     * @param aggregatedFeatureName
     * @param contextId i.e. username
     * @param timeRange start and end time of the events
     * @return list of Accumulated aggregated events
     */
    List<AccumulatedAggregatedFeatureEvent> findAccumulatedAggrEventsByContextIdAndTime (String aggregatedFeatureName, String contextId, TimeRange timeRange);

    /**
     * returns list of all the scored events for a context (e.g: user) and feature type at a given time range
     * @param featureName
     * @param contextId i.e. username
     * @param timeRange start and end time of the events
     * @return list of scored events
     */
    List<AdeScoredRecord> findScoredEventsByContextAndTimeAndFeature(String featureName, String contextId, TimeRange timeRange);

}
