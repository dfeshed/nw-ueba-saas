package fortscale.ml.model.listener;

public enum ModelBuildingStatus {
	SUCCESS("Successful model building"),
	RETRIEVER_FAILURE("No data to retrieve"),
	BUILDER_FAILURE("Failed to build model"),
	STORE_FAILURE("Failed to save model");

	private final String message;

	ModelBuildingStatus(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
