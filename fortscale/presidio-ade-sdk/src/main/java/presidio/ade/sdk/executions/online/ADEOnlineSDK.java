package presidio.ade.sdk.executions.online;

import presidio.ade.sdk.executions.common.ADECommonSDK;
import presidio.ade.sdk.executions.common.RunId;

/**
 * Online execution sdk, extends {@link ADECommonSDK} with ADE execution params that are relevant to online runs
 * Created by barak_schuster on 5/18/17.
 */
public interface ADEOnlineSDK extends ADECommonSDK<PrepareOnlineRunTimeParams> {
    /**
     *
     * @return execution id of the single online run ( should be only one)
     */
    RunId getRunId();
}
