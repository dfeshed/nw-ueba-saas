package presidio.ade.sdk.executions.online;

import presidio.ade.domain.store.input.ADEInputCleanupParams;
import presidio.ade.domain.store.input.ADEInputRecord;
import presidio.ade.domain.store.input.ADEInputRecordsMetaData;
import presidio.ade.sdk.executions.common.DirtyDataMarker;
import presidio.ade.sdk.executions.common.RunId;
import presidio.ade.sdk.executions.common.RunPrepResult;
import presidio.ade.sdk.executions.common.RunStatus;

import java.util.List;
import java.util.Set;

/**
 * Created by barak_schuster on 5/21/17.
 */
public class ADEOnlineSDKImpl implements ADEOnlineSDK{
    @Override
    public RunId getRunId() {
        return null;
    }

    @Override
    public RunPrepResult prepareRun(RunId runId, PrepareOnlineRunTimeParams prepareOnlineRunTimeParams) {
        return null;
    }

    @Override
    public void changeRunTimeParams(RunId runId, PrepareOnlineRunTimeParams prepareOnlineRunTimeParams) {

    }

    @Override
    public void processNextHour(RunId runId) {

    }

    @Override
    public PrepareOnlineRunTimeParams getLastHour(RunId runId) {
        return null;
    }

    @Override
    public Set<PrepareOnlineRunTimeParams> getInProgressHours(RunId runId) {
        return null;
    }

    @Override
    public RunStatus getRunStatus(RunId runId) {
        return null;
    }

    @Override
    public void cleanup(ADEInputCleanupParams params) {

    }

    @Override
    public void pause(RunId runId) {

    }

    @Override
    public void unpause(RunId runId) {

    }

    @Override
    public void stop(RunId runId) {

    }

    @Override
    public void stopForcefully(RunId runId) {

    }

    @Override
    public Set<DirtyDataMarker> getDirtyDataMarkers() {
        return null;
    }

    @Override
    public void setDirtyDataMarkers(Set<DirtyDataMarker> dirtyDataMarkers) {

    }

    @Override
    public void store(ADEInputRecordsMetaData metaData, List<ADEInputRecord> records) {

    }
}
