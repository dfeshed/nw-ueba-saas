package fortscale.ml.model;

import java.util.Map;

public class ModelBuilderData {
	public enum NoDataReason {
		ALL_DATA_FILTERED,  // There was data in the database, but the retriever filtered out all of it
		NO_DATA_IN_DATABASE // There wasn't any data in the database
	}

	private final Object data;
	private final NoDataReason noDataReason;
	private Map<String, String> contextFieldNameToValueMap;

	/**
	 * C'tor for a {@link ModelBuilderData} that contains data.
	 *
	 * @param data the data (cannot be null)
	 */
	public ModelBuilderData(Object data) {
		this(data, null);
	}

	public ModelBuilderData(Object data, Map<String, String> contextFieldNameToValueMap) {
		if (data == null) {
			throw new IllegalArgumentException("'data' cannot be null. If there is no data, use the other c'tor.");
		}

		this.data = data;
		this.noDataReason = null;
		this.contextFieldNameToValueMap = contextFieldNameToValueMap;
	}

	/**
	 * C'tor for a {@link ModelBuilderData} that does not contain data.
	 *
	 * @param noDataReason the reason why there is no data (cannot be null)
	 */
	public ModelBuilderData(NoDataReason noDataReason) {
		if (noDataReason == null) {
			throw new IllegalArgumentException("'noDataReason' cannot be null. If there is data, use the other c'tor.");
		}

		this.data = null;
		this.noDataReason = noDataReason;
		this.contextFieldNameToValueMap = null;
	}

	public boolean dataExists() {
		return data != null;
	}

	public Object getData() {
		return data;
	}

	public NoDataReason getNoDataReason() {
		return noDataReason;
	}

	public Map<String, String> getContextFieldNameToValueMap() {
		return contextFieldNameToValueMap;
	}
}
