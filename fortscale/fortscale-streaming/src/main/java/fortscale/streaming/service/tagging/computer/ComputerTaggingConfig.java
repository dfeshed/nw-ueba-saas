package fortscale.streaming.service.tagging.computer;

import java.util.List;

/**
 * Configuration for computer tagging and clustering on a specific event type. This should be constructed from the topology
 * settings or from the streaming task configuration and passed to the ComputerTaggingService in order
 * to determine what action to take for each type of event.
 */
public class ComputerTaggingConfig {

	private String eventType;
	private String inputTopic;
	private String outputTopic;
	private String partitionField;
	private List<ComputerTaggingFieldsConfig> computerTaggingFieldsConfigList;

	public ComputerTaggingConfig(String eventType, String inputTopic, String outputTopic,
			String partitionField, List<ComputerTaggingFieldsConfig> computerTaggingFieldsConfigList) {
		setEventType(eventType);
		setInputTopic(inputTopic);
		setOutputTopic(outputTopic);
		setPartitionField(partitionField);
		setComputerTaggingFieldsConfigList(computerTaggingFieldsConfigList);
	}


	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getInputTopic() {
		return inputTopic;
	}

	public void setInputTopic(String inputTopic) {
		this.inputTopic = inputTopic;
	}

	public String getOutputTopic() {
		return outputTopic;
	}

	public void setOutputTopic(String outputTopic) {
		this.outputTopic = outputTopic;
	}

	public String getPartitionField() {
		return partitionField;
	}

	public void setPartitionField(String partitionField) {
		this.partitionField = partitionField;
	}

	public List<ComputerTaggingFieldsConfig> getComputerTaggingFieldsConfigList() {
		return computerTaggingFieldsConfigList;
	}

	public void setComputerTaggingFieldsConfigList(List<ComputerTaggingFieldsConfig> computerTaggingFieldsConfigList) {
		this.computerTaggingFieldsConfigList = computerTaggingFieldsConfigList;
	}
}
