package fortscale.streaming.service.usernameNormalization;

import fortscale.streaming.service.StreamingTaskConfig;

/**
 * Configuration for computer tagging and clustering on a specific event type. This should be constructed from the topology
 * settings or from the streaming task configuration and passed to the ComputerTaggingService in order
 * to determine what action to take for each type of event.
 */
public class UsernameNormalizationConfig implements StreamingTaskConfig {

	private String outputTopic;
	private String normalizationBasedField;
	private String domainField;
	private String fakeDomain;
	private String normalizedUsernameField;
	private String partitionField;
	private Boolean updateOnlyFlag;
	private String classifier;
	private UsernameNormalizationService usernameNormalizationService;


	public UsernameNormalizationConfig(String outputTopic, String normalizationBasedField, String
			domainField, String fakeDomain, String normalizedUsernameField, String partitionField, Boolean
			updateOnlyFlag, String classifier,
			UsernameNormalizationService usernameNormalizationService) {
		this.outputTopic = outputTopic;
		this.normalizationBasedField = normalizationBasedField;
		this.domainField = domainField;
		this.fakeDomain = fakeDomain;
		this.normalizedUsernameField = normalizedUsernameField;
		this.partitionField = partitionField;
		this.updateOnlyFlag = updateOnlyFlag;
		this.classifier = classifier;
		this.usernameNormalizationService = usernameNormalizationService;
	}

	public String getOutputTopic() {
		return outputTopic;
	}

	public void setOutputTopic(String outputTopic) {
		this.outputTopic = outputTopic;
	}

	public String getNormalizationBasedField() {
		return normalizationBasedField;
	}

	public void setNormalizationBasedField(String normalizationBasedField) {
		this.normalizationBasedField = normalizationBasedField;
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
