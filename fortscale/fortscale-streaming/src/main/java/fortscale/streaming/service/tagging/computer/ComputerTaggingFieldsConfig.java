package fortscale.streaming.service.tagging.computer;

/**
 * Configuration for computer tagging and clustering on a specific event type. This should be constructed from the topology
 * settings or from the streaming task configuration and passed to the ComputerTaggingService in order
 * to determine what action to take for each type of event.
 */
public class ComputerTaggingFieldsConfig {

	private String tagType;
	private String hostnameField;
	private String classificationField;
	private String clusteringField;
	private String isSensitiveMachineField;
	private boolean createNewComputerInstances;

	public ComputerTaggingFieldsConfig(String tagType, String hostnameField, String classificationField, String clusteringField, String isSensitiveMachineField,
			boolean createNewComputerInstances) {
		setTagType(tagType);
		setHostnameField(hostnameField);
		setClassificationField(classificationField);
		setClusteringField(clusteringField);
		setIsSensitiveMachineField(isSensitiveMachineField);
		setCreateNewComputerInstances(createNewComputerInstances);
	}

	public String getTagType() {
		return tagType;
	}

	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	public String getHostnameField() {
		return hostnameField;
	}

	public void setHostnameField(String hostnameField) {
		this.hostnameField = hostnameField;
	}

	public String getClassificationField() {
		return classificationField;
	}

	public void setClassificationField(String classificationField) {
		this.classificationField = classificationField;
	}

	public String getClusteringField() {
		return clusteringField;
	}

	public void setClusteringField(String clusteringField) {
		this.clusteringField = clusteringField;
	}

	public String getIsSensitiveMachineField() {
		return isSensitiveMachineField;
	}

	public void setIsSensitiveMachineField(String isSensitiveMachineField) {
		this.isSensitiveMachineField = isSensitiveMachineField;
	}

	public boolean isCreateNewComputerInstances() {
		return createNewComputerInstances;
	}

	public void setCreateNewComputerInstances(boolean createNewComputerInstances) {
		this.createNewComputerInstances = createNewComputerInstances;
	}
}
