package presidio.ade.sdk.executions.common;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStoreCleanupParams;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;

import java.util.List;
import java.util.Set;

/**
 * SDK signature that is common to all kinds of executions
 * Created by barak_schuster on 5/18/17.
 */
public interface ADECommonSDK<ADERunParams> {

    /**
     * Executes the needed preparation steps before starting to process data
     *
     * @param runId  the execution identifier
     * @param params containing parameters related to the data time range a processing strategy
     *               i.e. the data is between "some start date" to "some end date" and should be processed in chunks of 1 Hour
     * @return the preparation result, i.e.: running, failed etc...
     */
    RunPrepResult prepareRun(RunId runId, ADERunParams params);

    /**
     * change the execution params of a run id
     * usage example: when we want to change the scheduling of an execution to run once in a hour instead of once a day
     *
     * @param runId  execution identifier
     * @param params
     */
    void changeRunTimeParams(RunId runId, ADERunParams params);

    /**
     * notify the component that the required data needed for processing the next hour is ready for use
     *
     * @param runId execution identifier
     */
    void processNextTimeRange(RunId runId);

    /**
     * @param runId
     * @return the last hour that was requested to process.
     */
    ADERunParams getLastProcessedEndTime(RunId runId);

    /**
     * @param runId
     * @return currently running execution hours
     */
    Set<ADERunParams> getInProgressHours(RunId runId);

    /**
     * @param runId
     * @return the execution status
     */
    RunStatus getRunStatus(RunId runId);

    /**
     * cleans the data between by given params
     *
     * @param params
     */
    void cleanup(EnrichedDataStoreCleanupParams params);

    /**
     * pauses the execution. the ADE will pause gracefully as fast as possible and would not continue to the nearest sub step
     *
     * @param runId the execution to be paused
     */
    void pause(RunId runId);

    /**
     * the execution will continue from where you left it
     *
     * @param runId the execution to be un-paused
     */
    void unpause(RunId runId);

    /**
     * the execution will stop at the end of the processed day
     *
     * @param runId
     */
    void stop(RunId runId);

    /**
     * Execution should stop, no matter what is currently running (AKA SIGKILL).
     * NOTICE: you will probably leave the system in dirty-data state (not fully processed),
     * you will probably want to clean the dirty data, and then move the execution cursor to the start of the last mission
     *
     * @param runId the execution to be stopped
     */
    void stopForcefully(RunId runId);

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
     * those records will be processed whenever the relevant {@link #processNextTimeRange(RunId)} call will occur
     *
     * @param metaData some metadata considering the data to be stored. i.e. what is the data source, what is the time range etc...
     * @param records  data to be stored
     */
    void store(EnrichedRecordsMetadata metaData, List<? extends EnrichedRecord> records);
}
