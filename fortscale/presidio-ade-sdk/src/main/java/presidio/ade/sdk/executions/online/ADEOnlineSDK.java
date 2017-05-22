package presidio.ade.sdk.executions.online;

import presidio.ade.domain.store.input.store.ADEInputDataStore;
import presidio.ade.sdk.executions.common.*;

import java.util.Set;

/**
 * Online execution sdk, extends {@link ADECommonSDK} with ADE execution params that are relevant to online runs
 * Created by barak_schuster on 5/18/17.
 */
public class ADEOnlineSDK extends AbstractADESDK<PrepareOnlineRunTimeParams>{
    public ADEOnlineSDK(ADEInputDataStore store) {
        super(store);
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

    /**
     *
     * @return execution id of the single online run ( should be only one)
     */
    public RunId getRunId()
    {
        return null;
    }
}
