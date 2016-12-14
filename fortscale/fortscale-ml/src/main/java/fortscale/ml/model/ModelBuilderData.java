package fortscale.ml.model;

public class ModelBuilderData {
	public enum Code {
		DATA_EXISTS,   // There was data in the database, and not all of it was filtered out
		DATA_FILTERED, // There was data in the database, but the retriever filtered out all of it
		NO_DATA        // There wasn't any data in the database
	}

	private final Object data;
	private final Code code;

	public ModelBuilderData(Object data, Code code) {
		this.data = data;
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public Code getCode() {
		return code;
	}
}
