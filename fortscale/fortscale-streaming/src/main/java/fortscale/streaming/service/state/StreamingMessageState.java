package fortscale.streaming.service.state;

/**
 * @author gils
 * Date: 09/11/2015
 */
public class StreamingMessageState {

    private static final String STREAMING_MESSAGE_STATE_DELIMITER = "_";

    private StreamingStepType stepType;
    private String taskName;

    public StreamingMessageState(StreamingStepType stepType, String taskName) {
        this.stepType = stepType;
        this.taskName = taskName;
    }

    public StreamingMessageState(StreamingStepType stepType) {
        this.stepType = stepType;
    }

    public StreamingStepType getStepType() {
        return stepType;
    }

    public String getTaskName() {
        return taskName;
    }

    public String serialize() {
        return this.toString();
    }

    @Override
    public String toString() {
        if (taskName != null) {
            return stepType.name() + STREAMING_MESSAGE_STATE_DELIMITER + taskName;
        }
        else {
            return stepType.name();
        }
    }
}
