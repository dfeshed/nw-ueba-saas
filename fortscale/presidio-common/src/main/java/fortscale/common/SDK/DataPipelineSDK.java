package fortscale.common.SDK;

import java.util.List;

/**
 * controls data pipeline over the different components
 * Created by barak_schuster on 9/17/17.
 */
public interface DataPipelineSDK {

    /**
     *
     * @return cursor for currently running hours. null if not running on any hour.
     */
    List<PipelineStateDataProcessingCursor> getCurrentlyRunningCursor();

    /**
     *
     * @return current status of a component.
     */
    PipelineState.StatusEnum getStatus();

    /**
     *
     * @return a merged result of {@link this#getStatus()} & {@link this#getCurrentlyRunningCursor()}
     */
    default PipelineState getPipelineState()
    {
        List<PipelineStateDataProcessingCursor> currentlyRunningCursor = getCurrentlyRunningCursor();
        PipelineState.StatusEnum status = getStatus();
        PipelineState pipelineState = new PipelineState();
        pipelineState.setDataProcessingCursor(currentlyRunningCursor);
        pipelineState.setStatus(status);

        return pipelineState;
    }

    /**
     * starts the component
     */
    void start();

    /**
     * stops the component
     */
    void stop();
}
