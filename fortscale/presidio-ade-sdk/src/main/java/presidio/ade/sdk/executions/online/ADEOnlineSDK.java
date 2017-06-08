package presidio.ade.sdk.executions.online;

import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.sdk.executions.common.ADECommonSDK;
import presidio.ade.sdk.executions.common.AbstractADESDK;
import presidio.ade.sdk.executions.common.RunId;
import presidio.ade.sdk.executions.common.RunPrepResult;

import java.util.Set;

/**
 * Online execution sdk, extends {@link ADECommonSDK} with ADE execution params that are relevant to online runs
 * Created by barak_schuster on 5/18/17.
 */
public class ADEOnlineSDK extends AbstractADESDK<PrepareOnlineRunTimeParams>{
    public ADEOnlineSDK(EnrichedDataStore store) {
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
    public PrepareOnlineRunTimeParams getLastProcessedEndTime(RunId runId) {
        return null;
    }

    @Override
    public Set<PrepareOnlineRunTimeParams> getInProgressHours(RunId runId) {
        return null;
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
