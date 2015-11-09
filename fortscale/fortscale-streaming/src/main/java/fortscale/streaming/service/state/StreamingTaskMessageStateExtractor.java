package fortscale.streaming.service.state;

/**
 * Extractor of the streaming task message state
 *
 * @author gils
 * Date: 09/11/2015
 */
public class StreamingTaskMessageStateExtractor {

    private static final String STREAMING_MESSAGE_STATE_DELIMITER = "_";

    private static final int STEP_TYPE_INDEX = 0;
    private static final int TASK_NAME_INDEX = 1;

    public static StreamingTaskMessageState extract(String streamingMessageState) {
        String[] streamingStateSplitted = streamingMessageState.split(STREAMING_MESSAGE_STATE_DELIMITER);

        if (streamingStateSplitted.length > 1) {
            StreamingTaskStepType streamingTaskStepType = extractStreamingStepType(streamingStateSplitted[STEP_TYPE_INDEX]);

            String taskName = streamingStateSplitted[TASK_NAME_INDEX];

            return new StreamingTaskMessageState(streamingTaskStepType, taskName);
        }
        else if (streamingStateSplitted.length == 1) {
            StreamingTaskStepType streamingTaskStepType = extractStreamingStepType(streamingStateSplitted[STEP_TYPE_INDEX]);

            return new StreamingTaskMessageState(streamingTaskStepType);
        }

        throw new IllegalStateException("Could not extract message state from message state: " + streamingMessageState);
    }

    private static StreamingTaskStepType extractStreamingStepType(String streamingStateType) {
        String streamingStateTypeStr = streamingStateType.toUpperCase();
        return StreamingTaskStepType.valueOf(streamingStateTypeStr);
    }
}
