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

	private String dataSource;
	private String lastState;
	private String outputTopic;
	private String partitionField;
	private List<ComputerTaggingFieldsConfig> computerTaggingFieldsConfigList;

	public ComputerTaggingConfig(String dataSource , String lastState, String outputTopic,
			String partitionField, List<ComputerTaggingFieldsConfig> computerTaggingFieldsConfigList) {
		setDataSource(dataSource);
		setLastState(lastState);
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

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getLastState() {
		return lastState;
	}

	public void setLastState(String lastState) {
		this.lastState = lastState;
	}
}
