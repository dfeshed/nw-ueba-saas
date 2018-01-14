package fortscale.common.SDK;

import java.util.List;

/**
 * controls data pipeline over the different components
 * Created by barak_schuster on 9/17/17.
 */
public interface DataPipelineSDK {

    /**
     * @return cursor for currently running hours. null if not running on any hour.
     */
    List<PipelineStateDataProcessingCursor> getCurrentlyRunningCursor();

    /**
     * @return current status of a component.
     */
    PipelineState.StatusEnum getStatus();

    /**
     * @return a merged result of {@link this#getStatus()} & {@link this#getCurrentlyRunningCursor()}
     */
    default PipelineState getPipelineState() {
        PipelineState pipelineState = new PipelineState();
        PipelineState.StatusEnum status = getStatus();
        pipelineState.setStatus(status);

        if(!status.equals(PipelineState.StatusEnum.CLEANING))
        {
            List<PipelineStateDataProcessingCursor> currentlyRunningCursor = getCurrentlyRunningCursor();
            pipelineState.setDataProcessingCursor(currentlyRunningCursor);
        }

        return pipelineState;
    }

    void cleanAndRun();
}
