package fortscale.streaming.service.state;

/**
 * Representation of the message state in the streaming topology
 *
 * @author gils
 * Date: 09/11/2015
 */
public class StreamingTaskMessageState {

    private static final String STREAMING_MESSAGE_STATE_DELIMITER = "_";

    private StreamingTaskStepType stepType;
    private String taskName;

    public StreamingTaskMessageState(StreamingTaskStepType stepType, String taskName) {
        this.stepType = stepType;
        this.taskName = taskName;
    }

    public StreamingTaskMessageState(StreamingTaskStepType stepType) {
        this.stepType = stepType;
    }

    public StreamingTaskStepType getStepType() {
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
