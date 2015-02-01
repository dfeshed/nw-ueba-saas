package fortscale.streaming.service.usernameNormalization;


/**
 * Configuration for computer tagging and clustering on a specific event type. This should be constructed from the topology
 * settings or from the streaming task configuration and passed to the ComputerTaggingService in order
 * to determine what action to take for each type of event.
 */
public class UsernameNormalizationConfig {

	private String inputTopic;
	private String outputTopic;
	private String partitionField;
	private UsernameNormalizationService usernameNormalizationService;

	public UsernameNormalizationConfig(String inputTopic, String outputTopic, String partitionField,
			UsernameNormalizationService usernameNormalizationService) {
		this.inputTopic = inputTopic;
		this.outputTopic = outputTopic;
		this.partitionField = partitionField;
		this.usernameNormalizationService = usernameNormalizationService;
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

	public UsernameNormalizationService getUsernameNormalizationService() {
		return usernameNormalizationService;
	}

	public void setUsernameNormalizationService(UsernameNormalizationService usernameNormalizationService) {
		this.usernameNormalizationService = usernameNormalizationService;
	}
}
