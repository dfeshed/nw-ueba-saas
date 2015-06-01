package fortscale.streaming.service.usernameNormalization;

/**
 * Configuration for computer tagging and clustering on a specific event type. This should be constructed from the topology
 * settings or from the streaming task configuration and passed to the ComputerTaggingService in order
 * to determine what action to take for each type of event.
 */
public class UsernameNormalizationConfig {

	private String inputTopic;
	private String outputTopic;
	private String usernameField;
	private String domainField;
	private String fakeDomain;
	private String normalizedUsernameField;
	private String partitionField;
	private Boolean updateOnlyFlag;
	private String classifier;
	private UsernameNormalizationService usernameNormalizationService;


	public UsernameNormalizationConfig(String inputTopic, String outputTopic, String usernameField, String
			domainField, String fakeDomain, String normalizedUsernameField, String partitionField, Boolean
			updateOnlyFlag, String classifier,
			UsernameNormalizationService usernameNormalizationService) {
		this.inputTopic = inputTopic;
		this.outputTopic = outputTopic;
		this.usernameField = usernameField;
		this.domainField = domainField;
		this.fakeDomain = fakeDomain;
		this.normalizedUsernameField = normalizedUsernameField;
		this.partitionField = partitionField;
		this.updateOnlyFlag = updateOnlyFlag;
		this.classifier = classifier;
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

	public String getUsernameField() {
		return usernameField;
	}

	public void setUsernameField(String usernameField) {
		this.usernameField = usernameField;
	}

	public String getDomainField() {
		return domainField;
	}

	public void setDomainField(String domainField) {
		this.domainField = domainField;
	}

	public String getFakeDomain() {
		return fakeDomain;
	}

	public void setFakeDomain(String fakeDomain) {
		this.fakeDomain = fakeDomain;
	}

	public String getNormalizedUsernameField() {
		return normalizedUsernameField;
	}

	public void setNormalizedUsernameField(String normalizedUsernameField) {
		this.normalizedUsernameField = normalizedUsernameField;
	}

	public String getPartitionField() {
		return partitionField;
	}

	public void setPartitionField(String partitionField) {
		this.partitionField = partitionField;
	}

	public Boolean getUpdateOnlyFlag() {
		return updateOnlyFlag;
	}

	public void setUpdateOnlyFlag(Boolean updateOnlyFlag) {
		this.updateOnlyFlag = updateOnlyFlag;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public UsernameNormalizationService getUsernameNormalizationService() {
		return usernameNormalizationService;
	}

	public void setUsernameNormalizationService(UsernameNormalizationService usernameNormalizationService) {
		this.usernameNormalizationService = usernameNormalizationService;
	}
}
