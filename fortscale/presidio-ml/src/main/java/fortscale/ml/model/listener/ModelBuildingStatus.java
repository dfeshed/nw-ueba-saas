package fortscale.ml.model.listener;

public enum ModelBuildingStatus {
	SUCCESS("Successful model building", false),
	DATA_FILTERED_OUT("All data filtered out", false),
	RETRIEVER_FAILURE("Failed to retrieve data", true),
	BUILDER_FAILURE("Failed to build model", true),
	STORE_FAILURE("Failed to save model", true);

	private final String message;
	private final boolean failure;

	ModelBuildingStatus(String message, boolean failure) {
		this.message = message;
		this.failure = failure;
	}

	public String getMessage() {
		return message;
	}

	public boolean isFailure() {
		return failure;
	}
}
