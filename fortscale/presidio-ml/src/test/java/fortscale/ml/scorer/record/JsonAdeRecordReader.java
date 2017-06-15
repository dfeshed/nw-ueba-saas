package fortscale.ml.scorer.record;

import fortscale.utils.recordreader.RecordReader;
import org.json.JSONObject;
import presidio.ade.domain.record.AdeRecord;

/**
 * A record reader for {@link JsonAdeRecord}s.
 *
 * Created by Lior Govrin on 13/06/2017.
 */
public class JsonAdeRecordReader implements RecordReader<AdeRecord> {
	private String fieldPathDelimiter;

	/**
	 * Default c'tor.
	 * Sets the default field path delimiter.
	 */
	public JsonAdeRecordReader() {
		this.fieldPathDelimiter = "\\.";
	}

	@Override
	public <U> U get(AdeRecord record, String fieldPath, Class<U> fieldClass) {
		// Split the field path keys
		String[] keys = fieldPath.split(fieldPathDelimiter);
		// Get the underlying JSON object
		JSONObject jsonObject = ((JsonAdeRecord)record).getJsonObject();

		// Iterate the inner JSON objects until the final key is reached
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
