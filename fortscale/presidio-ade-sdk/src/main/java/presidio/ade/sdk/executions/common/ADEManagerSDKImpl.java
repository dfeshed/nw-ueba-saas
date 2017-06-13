package presidio.ade.sdk.executions.common;

import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.sdk.executions.historical.PrepareHistoricalRunTimeParams;
import presidio.ade.sdk.executions.online.PrepareOnlineRunTimeParams;

import java.util.List;
import java.util.Set;

/**
 * abstract class that implements common ADE SDK functionality regardless to the execution type
 * Created by barak_schuster on 5/18/17.
 */
public class ADEManagerSDKImpl implements ADEManagerSDK {
    protected EnrichedDataStore store;

    public ADEManagerSDKImpl(EnrichedDataStore store) {
        this.store = store;
    }

    @Override
    public RunPrepResult prepareHistoricalRun(RunId runId, PrepareHistoricalRunTimeParams params) {
        return null;
    }

    @Override
    public RunPrepResult prepareOnlineRun(PrepareOnlineRunTimeParams params) {
        return null;
    }

    @Override
    public RunId getOnlineRunId() {
        return null;
    }

    @Override
    public void changeHistoricalRunTimeParams(RunId runId, PrepareHistoricalRunTimeParams params) {

    }

    @Override
    public void changeOnlineRunTimeParams(PrepareOnlineRunTimeParams params) {

    }

    @Override
    public void processNextHistoricalTimeRange(RunId runId) {

    }

    @Override
    public PrepareHistoricalRunTimeParams getLastHistoricalProcessedEndTime(RunId runId) {
        return null;
    }

    @Override
    public PrepareOnlineRunTimeParams getLastOnlineProcessedEndTime() {
        return null;
    }

    @Override
    public Set<PrepareHistoricalRunTimeParams> getInProgressHistoricalHours(RunId runId) {
        return null;
    }

    @Override
    public Set<PrepareOnlineRunTimeParams> getInProgressOnlineHours() {
        return null;
    }

    @Override
    public RunStatus getHistoricalRunStatus(RunId runId) {
        return null;
    }

    @Override
    public void cleanup(AdeDataStoreCleanupParams params) {

    }

    @Override
    public void pauseHistorical(RunId runId) {

    }

    @Override
    public void unpauseHistorical(RunId runId) {

    }

    @Override
    public void stopHistorical(RunId runId) {

    }

    @Override
    public void stopHistoricalForcefully(RunId runId) {

    }

    @Override
    public Set<DirtyDataMarker> getDirtyDataMarkers() {
        return null;
    }

    @Override
    public void setDirtyDataMarkers(Set<DirtyDataMarker> dirtyDataMarkers) {

    }

    @Override
    public void store(EnrichedRecordsMetadata metaData, List<? extends EnrichedRecord> records) {
        store.store(metaData, records);
    }


}
