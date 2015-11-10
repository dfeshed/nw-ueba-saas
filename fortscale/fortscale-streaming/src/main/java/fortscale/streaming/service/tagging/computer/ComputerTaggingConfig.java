package fortscale.streaming.service.tagging.computer;

import fortscale.streaming.service.StreamingTaskConfig;

import java.util.List;

/**
 * Configuration for computer tagging and clustering on a specific event type. This should be constructed from the topology
 * settings or from the streaming task configuration and passed to the ComputerTaggingService in order
 * to determine what action to take for each type of event.
 */
public class ComputerTaggingConfig implements StreamingTaskConfig {

	private String dataSource;
	private String inputTopic;
	private String outputTopic;
	private String partitionField;
	private List<ComputerTaggingFieldsConfig> computerTaggingFieldsConfigList;

	public ComputerTaggingConfig(String dataSource, String inputTopic, String outputTopic,
			String partitionField, List<ComputerTaggingFieldsConfig> computerTaggingFieldsConfigList) {
		setDataSource(dataSource);
		setInputTopic(inputTopic);
		setOutputTopic(outputTopic);
		setPartitionField(partitionField);
		setComputerTaggingFieldsConfigList(computerTaggingFieldsConfigList);
	}


	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
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
