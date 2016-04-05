package fortscale.collection.jobs.model;

import fortscale.utils.kafka.AbstractKafkaTopicReader;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Configurable(preConstruction = true)
public class ModelBuildingSummaryReader extends AbstractKafkaTopicReader {
	@Value("${fortscale.model.build.message.field.session.id}")
	private String sessionIdJsonField;
	@Value("${fortscale.model.build.message.field.model.conf.name}")
	private String modelConfNameJsonField;
	@Value("${fortscale.model.build.message.field.end.time.in.seconds}")
	private String endTimeInSecondsJsonField;
	@Value("${fortscale.model.build.message.field.context.id}")
	private String contextIdJsonField;

	private volatile Map<String, JSONObject> summaryMessages;

	public ModelBuildingSummaryReader(String clientId, String topic, int partition) {
		super(clientId, topic, partition);
		summaryMessages = new HashMap<>();
	}

	public boolean isEndTimeEqual(String sessionId, String modelConfName, long endTimeInSeconds) {
		JSONObject message = summaryMessages.get(getSummaryMessageKey(sessionId, modelConfName));
		return message != null && message.getLong(endTimeInSecondsJsonField) == endTimeInSeconds;
	}

	@Override
	protected void handleMessage(JSONObject message) {
		// Check that input message is a summary message
		if (!message.has(contextIdJsonField)) {
			String sessionId = message.getString(sessionIdJsonField);
			String modelConfName = message.getString(modelConfNameJsonField);
			summaryMessages.put(getSummaryMessageKey(sessionId, modelConfName), message);
		}
	}

	private static String getSummaryMessageKey(String sessionId, String modelConfName) {
		return String.format("%s.%s", sessionId, modelConfName);
	}
}
