package fortscale.streaming.service.state;

/**
 * @author gils
 * Date: 09/11/2015
 */
public class StreamingMessageStateExtractor {
    private static final String STREAMING_MESSAGE_STATE_DELIMITER = "_";

    public static StreamingMessageState extract(String streamingMessageState) {
        String[] streamingStateSplitted = streamingMessageState.split(STREAMING_MESSAGE_STATE_DELIMITER);

        if (streamingStateSplitted.length > 1) { // == 2
            StreamingStepType streamingStepType = extractStreamingStepType(streamingStateSplitted[0]);

            String taskName = streamingStateSplitted[1];

            return new StreamingMessageState(streamingStepType, taskName);
        }
        else if (streamingStateSplitted.length == 1) {
            StreamingStepType streamingStepType = extractStreamingStepType(streamingStateSplitted[0]);

            return new StreamingMessageState(streamingStepType);
        }

        throw new IllegalStateException("Could not extract message state from message state: " + streamingMessageState);
    }

    private static StreamingStepType extractStreamingStepType(String streamingStateType) {
        String streamingStateTypeStr = streamingStateType.toUpperCase();
        return StreamingStepType.valueOf(streamingStateTypeStr);
    }
}
