package fortscale.streaming.task.evidence.pre.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.streaming.exceptions.StreamMessageNotContainFieldException;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;

/**
 * Created by tomerd on 17/08/2015.
 */
public abstract class AbstractEvidencePreProcess {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(AbstractEvidencePreProcess.class);

	/**
	 * JSON serializer
	 */
	protected ObjectMapper mapper = new ObjectMapper();

	/**
	 * Validate that the expected field has value in the message JSON and return the value
	 *
	 * @param message        The message JSON
	 * @param field    The requested field
	 * @return The value of the field
	 * @throws StreamMessageNotContainFieldException in case the field doesn't exist in the JSON
	 */
	protected Object validateFieldExistsAndGetValue(JSONObject message, String field) throws Exception {
		String[] fieldHierarchy = field.split("\\.");
		Object value = message;
		for(String fieldPart : fieldHierarchy){
			value = ((JSONObject) value).get(fieldPart);

		}
		if (value == null) {
			logger.error("message {} does not contains value in field {}", mapper.writeValueAsString(message), field);
			throw new StreamMessageNotContainFieldException(mapper.writeValueAsString(message), field);
		}
		return value;
	}
}
