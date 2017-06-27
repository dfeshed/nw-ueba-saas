package fortscale.ml.scorer.record;

import org.json.JSONObject;
import org.springframework.data.annotation.Transient;
import presidio.ade.domain.record.AdeRecord;

import java.time.Instant;

/**
 * The underlying implementation of this ADE record is a JSON object.
 *
 * Created by Lior Govrin on 13/06/2017.
 */
public class JsonAdeRecord extends AdeRecord {
	private JSONObject jsonObject;

	public JsonAdeRecord(Instant date_time, JSONObject jsonObject) {
		super(date_time);
		this.jsonObject = jsonObject;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	/**
	 * Create a JSON ADE record that has one mapping from "key" to "value" and its date time is "Instant.now()".
	 *
	 * @param key   the key of the mapping
	 * @param value the value of the mapping
	 * @return the created JSON ADE record
	 */
	public static JsonAdeRecord getJsonAdeRecord(String key, Object value) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(key, value);
		return new JsonAdeRecord(Instant.now(), jsonObject);
	}

	@Transient
	public String getDataSource(){
		return null;
	}
}
