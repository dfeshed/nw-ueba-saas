package fortscale.ml.model.listener;

import net.minidev.json.JSONObject;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.springframework.util.Assert;

import java.util.Map;

public class KafkaModelBuildingListener implements IModelBuildingListener {
	private static final String MODEL_CONF_NAME_JSON_FIELD = "modelConfName";
	private static final String CONTEXT_JSON_FIELD = "context";
	private static final String SUCCESS_JSON_FIELD = "success";

	private String outputTopicName;
	private MessageCollector collector;

	public KafkaModelBuildingListener(String outputTopicName) {
		Assert.hasText(outputTopicName);
		this.outputTopicName = outputTopicName;
	}

	@Override
	public void modelBuildingStatus(String modelConfName, Map<String, String> context, boolean success) {
		String statusAsJsonString = statusToJsonString(modelConfName, context, success);
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopicName), statusAsJsonString));
	}

	public void setMessageCollector(MessageCollector collector) {
		Assert.notNull(collector);
		this.collector = collector;
	}

	private static String statusToJsonString(String modelConfName, Map<String, String> context, boolean success) {
		JSONObject json = new JSONObject();

		json.put(MODEL_CONF_NAME_JSON_FIELD, modelConfName);
		json.put(CONTEXT_JSON_FIELD, context);
		json.put(SUCCESS_JSON_FIELD, success);

		return json.toJSONString();
	}
}
