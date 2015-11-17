package fortscale.streaming.service.tagging.computer;

import fortscale.streaming.service.StreamingTaskConfig;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;

import java.util.List;

/**
 * Configuration for computer tagging and clustering on a specific event type. This should be constructed from the topology
 * settings or from the streaming task configuration and passed to the ComputerTaggingService in order
 * to determine what action to take for each type of event.
 */
public class ComputerTaggingConfig implements StreamingTaskConfig {

	private StreamingTaskDataSourceConfigKey configKey;
	private String outputTopic;
	private String partitionField;
	private List<ComputerTaggingFieldsConfig> computerTaggingFieldsConfigList;

	public ComputerTaggingConfig(StreamingTaskDataSourceConfigKey configKey, String outputTopic,
			String partitionField, List<ComputerTaggingFieldsConfig> computerTaggingFieldsConfigList) {
		setConfigKey(configKey);
		setOutputTopic(outputTopic);
		setPartitionField(partitionField);
		setComputerTaggingFieldsConfigList(computerTaggingFieldsConfigList);
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
	public StreamingTaskDataSourceConfigKey getConfigKey() {
		return configKey;
	}

	public void setConfigKey(StreamingTaskDataSourceConfigKey configKey) {
		this.configKey = configKey;
	}

}
