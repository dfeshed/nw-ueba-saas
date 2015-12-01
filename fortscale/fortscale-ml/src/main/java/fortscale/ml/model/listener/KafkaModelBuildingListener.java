package fortscale.ml.model.listener;

import net.minidev.json.JSONObject;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.springframework.util.Assert;

import java.util.Date;

public class KafkaModelBuildingListener implements IModelBuildingListener {
	private static final String MODEL_CONF_NAME_JSON_FIELD = "modelConfName";
	private static final String CONTEXT_ID_JSON_FIELD = "contextId";
	private static final String END_TIME_JSON_FIELD = "endTime";
	private static final String SUCCESS_JSON_FIELD = "success";

	private String outputTopicName;
	private MessageCollector collector;

	public KafkaModelBuildingListener(String outputTopicName) {
		Assert.hasText(outputTopicName);
		this.outputTopicName = outputTopicName;
	}

	@Override
	public void modelBuildingStatus(String modelConfName, String contextId, Date endTime, boolean success) {
		String statusAsJsonString = statusToJsonString(modelConfName, contextId, endTime, success);
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopicName), statusAsJsonString));
	}

	public void setMessageCollector(MessageCollector collector) {
		Assert.notNull(collector);
		this.collector = collector;
	}

	private static String statusToJsonString(String modelConfName, String contextId, Date endTime, boolean success) {
		JSONObject json = new JSONObject();

		json.put(MODEL_CONF_NAME_JSON_FIELD, modelConfName);
		json.put(CONTEXT_ID_JSON_FIELD, contextId);
		json.put(END_TIME_JSON_FIELD, endTime.toString());
		json.put(SUCCESS_JSON_FIELD, success);

		return json.toJSONString();
	}
}
