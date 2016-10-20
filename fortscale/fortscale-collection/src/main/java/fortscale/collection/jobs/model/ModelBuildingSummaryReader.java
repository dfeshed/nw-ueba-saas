package fortscale.collection.jobs.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import fortscale.ml.model.message.ModelBuildingStatusMessage;
import fortscale.ml.model.message.ModelBuildingSummaryMessage;
import fortscale.utils.kafka.AbstractKafkaTopicReader;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashMap;
import java.util.Map;

@Configurable(preConstruction = true)
public class ModelBuildingSummaryReader extends AbstractKafkaTopicReader {

	// sessionId.modelConfName to ModelBuildingSummaryMessage map
	private volatile Map<String, ModelBuildingSummaryMessage> summaryMessages;
	private ObjectMapper objectMapper;

	public ModelBuildingSummaryReader(String clientId, String topic, int partition) {
		super(clientId, topic, partition);
		summaryMessages = new HashMap<>();

		objectMapper = new ObjectMapper().registerModule(new JsonOrgModule());
	}

	public boolean isEndTimeEqual(String sessionId, String modelConfName, long endTimeInSeconds) {
		ModelBuildingSummaryMessage message = summaryMessages.get(getSummaryMessageKey(sessionId, modelConfName));
		return message != null && message.getEndTimeInSeconds() == endTimeInSeconds;
	}

	@Override
	protected void handleMessage(JSONObject message) {
		// Check that input message is a summary message
		if (!message.has(ModelBuildingStatusMessage.CONTEXT_ID_FIELD_NAME)) {
			ModelBuildingSummaryMessage summaryMessage = objectMapper.convertValue(message, ModelBuildingSummaryMessage.class);
			String sessionId = summaryMessage.getSessionId();
			String modelConfName = summaryMessage.getModelConfName();
			summaryMessages.put(getSummaryMessageKey(sessionId, modelConfName), summaryMessage);
		}
	}

	private static String getSummaryMessageKey(String sessionId, String modelConfName) {
		return String.format("%s.%s", sessionId, modelConfName);
	}
}
