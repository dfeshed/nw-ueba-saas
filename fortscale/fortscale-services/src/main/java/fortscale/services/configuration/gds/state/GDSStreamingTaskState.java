package fortscale.services.configuration.gds.state;

/**
 * Created by idanp on 1/11/2016.
 */
public abstract class GDSStreamingTaskState implements GDSConfigurationState   {
	String taskName;
	String lastState;
	String outputTopic;
	String outputTopicEntry;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getLastState() {
		return lastState;
	}

	public void setLastState(String lastState) {
		this.lastState = lastState;
	}

	public String getOutputTopic() {
		return outputTopic;
	}

	public void setOutputTopic(String outputTopic) {
		this.outputTopic = outputTopic;
	}

	public String getOutputTopicEntry() {
		return outputTopicEntry;
	}

	public void setOutputTopicEntry(String outputTopicEntry) {
		this.outputTopicEntry = outputTopicEntry;
	}

	@Override
	public void reset() {
		taskName = null;
		lastState = null;
		outputTopic = null;
		outputTopicEntry = null;
	}
}
