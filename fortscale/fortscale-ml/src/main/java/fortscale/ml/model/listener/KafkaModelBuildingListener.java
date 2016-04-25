package fortscale.ml.model.listener;

import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.Date;

@Configurable(preConstruction = true)
public class KafkaModelBuildingListener implements IModelBuildingListener {
	@Value("${fortscale.model.build.message.field.session.id}")
	private String sessionIdJsonField;
	@Value("${fortscale.model.build.message.field.model.conf.name}")
	private String modelConfNameJsonField;
	@Value("${fortscale.model.build.message.field.end.time.in.seconds}")
	private String endTimeInSecondsJsonField;
	@Value("${fortscale.model.build.message.field.context.id}")
	private String contextIdJsonField;
	@Value("${fortscale.model.build.message.field.is.successful}")
	private String isSuccessfulJsonField;
	@Value("${fortscale.model.build.message.field.details}")
	private String detailsJsonField;
	@Value("${fortscale.model.build.message.field.num.of.successes}")
	private String numOfSuccessesJsonField;
	@Value("${fortscale.model.build.message.field.num.of.failures}")
	private String numOfFailuresJsonField;

	private SystemStream systemStream;
	private MessageCollector collector;

	public KafkaModelBuildingListener(String outputTopicName) {
		Assert.hasText(outputTopicName);
		systemStream = new SystemStream("kafka", outputTopicName);
	}

	@Override
	public void modelBuildingStatus(String modelConfName, String sessionId, String contextId, Date endTime, ModelBuildingStatus status) {
		boolean isSuccessful = status.equals(ModelBuildingStatus.SUCCESS);
		String statusAsJsonString = statusToJsonString(sessionId, modelConfName, endTime, contextId, isSuccessful, status.getMessage());
		collector.send(new OutgoingMessageEnvelope(systemStream, statusAsJsonString));
	}

	@Override
	public void modelBuildingSummary(String modelConfName, String sessionId, Date endTime, long numOfSuccesses, long numOfFailures) {
		String summaryAsJsonString = summaryToJsonString(sessionId, modelConfName, endTime, numOfSuccesses, numOfFailures);
		collector.send(new OutgoingMessageEnvelope(systemStream, summaryAsJsonString));
	}

	public void setMessageCollector(MessageCollector collector) {
		Assert.notNull(collector);
		this.collector = collector;
	}

	private String statusToJsonString(String sessionId, String modelConfName, Date endTime, String contextId, boolean isSuccessful, String details) {
		JSONObject json = new JSONObject();
		json.put(sessionIdJsonField, sessionId);
		json.put(modelConfNameJsonField, modelConfName);
		json.put(endTimeInSecondsJsonField, TimestampUtils.convertToSeconds(endTime));
		json.put(contextIdJsonField, contextId);
		json.put(isSuccessfulJsonField, isSuccessful);
		json.put(detailsJsonField, details);
		return json.toJSONString();
	}

	private String summaryToJsonString(String sessionId, String modelConfName, Date endTime, long numOfSuccesses, long numOfFailures) {
		JSONObject json = new JSONObject();
		json.put(sessionIdJsonField, sessionId);
		json.put(modelConfNameJsonField, modelConfName);
		json.put(endTimeInSecondsJsonField, TimestampUtils.convertToSeconds(endTime));
		json.put(numOfSuccessesJsonField, numOfSuccesses);
		json.put(numOfFailuresJsonField, numOfFailures);
		return json.toJSONString();
	}
}
