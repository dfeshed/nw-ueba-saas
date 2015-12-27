package fortscale.ml.model.listener;

import net.minidev.json.JSONObject;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class KafkaModelBuildingListener implements IModelBuildingListener {
	private static final String MODEL_CONF_NAME_JSON_FIELD = "modelConfName";
	private static final String SESSION_ID_JSON_FIELD = "sessionId";
	private static final String CONTEXT_ID_JSON_FIELD = "contextId";
	private static final String END_TIME_JSON_FIELD = "endTime";
	private static final String SUCCESS_JSON_FIELD = "success";
	private static final String MESSAGE_JSON_FIELD = "message";

	private static final SimpleDateFormat utc;
	static {
		utc = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'");
		utc.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	private String outputTopicName;
	private MessageCollector collector;

	public KafkaModelBuildingListener(String outputTopicName) {
		Assert.hasText(outputTopicName);
		this.outputTopicName = outputTopicName;
	}

	@Override
	public void modelBuildingStatus(String modelConfName, String sessionId, String contextId,
			Date endTime, ModelBuildingStatus status) {

		boolean success = status.equals(ModelBuildingStatus.SUCCESS);
		String statusAsJsonString = statusToJsonString(modelConfName, sessionId, contextId,
				endTime, success, status.getMessage());
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopicName),
				statusAsJsonString));
	}

	public void setMessageCollector(MessageCollector collector) {
		Assert.notNull(collector);
		this.collector = collector;
	}

	private static String statusToJsonString(String modelConfName, String sessionId, String contextId,
			Date endTime, boolean success, String message) {

		JSONObject json = new JSONObject();
		json.put(MODEL_CONF_NAME_JSON_FIELD, modelConfName);
		json.put(SESSION_ID_JSON_FIELD, sessionId);
		json.put(CONTEXT_ID_JSON_FIELD, contextId);
		json.put(END_TIME_JSON_FIELD, utc.format(endTime));
		json.put(SUCCESS_JSON_FIELD, success);
		json.put(MESSAGE_JSON_FIELD, message);
		return json.toJSONString();
	}
}
