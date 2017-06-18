package fortscale.ml.scorer.record;

import fortscale.utils.recordreader.RecordReader;
import org.json.JSONObject;

import javax.validation.constraints.NotNull;

/**
 * A record reader for {@link JsonAdeRecord}s.
 *
 * Created by Lior Govrin on 13/06/2017.
 */
public class JsonAdeRecordReader implements RecordReader {
	private static final String DEFAULT_FIELD_PATH_DELIMITER = "\\.";

	private JsonAdeRecord record;
	private String fieldPathDelimiter;

	/**
	 * C'tor.
	 *
	 * @param record             the JSON ADE record from which values are extracted
	 * @param fieldPathDelimiter this JSON ADE record reader's field path delimiter (evaluated as a regular expression)
	 */
	public JsonAdeRecordReader(@NotNull JsonAdeRecord record, @NotNull String fieldPathDelimiter) {
		this.record = record;
		this.fieldPathDelimiter = fieldPathDelimiter;
	}

	/**
	 * Default c'tor (default field path delimiter is used).
	 *
	 * @param record the JSON ADE record from which values are extracted
	 */
	public JsonAdeRecordReader(@NotNull JsonAdeRecord record) {
		this(record, DEFAULT_FIELD_PATH_DELIMITER);
	}

	/**
	 * @see RecordReader#get(String, Class)
	 */
	@Override
	public <T> T get(String fieldPath, Class<T> fieldClass) {
		// Split the field path keys according to the delimiter
		String[] keys = fieldPath.split(fieldPathDelimiter);
		// Get the underlying JSON object from the ADE record
		JSONObject jsonObject = record.getJsonObject();

		// Traverse the inner JSON objects until the final key is reached
		for (int i = 0; i < keys.length - 1; i++) {
			// Return null if one of the inner keys does not exist
			if (!jsonObject.has(keys[i])) return null;
			jsonObject = jsonObject.getJSONObject(keys[i]);
		}

		// Return null if the final key does not exist
		if (!jsonObject.has(keys[keys.length - 1])) return null;
		Object object = jsonObject.get(keys[keys.length - 1]);
		// Return null if the type of the value is not as expected
		if (!fieldClass.isInstance(object)) return null;
		return fieldClass.cast(object);
	}
}
