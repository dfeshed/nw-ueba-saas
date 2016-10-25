package fortscale.ml.model.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.ml.model.message.ModelBuildingStatusMessage;
import fortscale.ml.model.message.ModelBuildingSummaryMessage;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Date;

@Configurable(preConstruction = true)
public class KafkaModelBuildingListener implements IModelBuildingListener {

	private static final Logger logger = Logger.getLogger(KafkaModelBuildingListener.class);

	private SystemStream systemStream;
	private MessageCollector collector;
	private ObjectMapper mapper;

	public KafkaModelBuildingListener(String outputTopicName) {
		Assert.hasText(outputTopicName);
		systemStream = new SystemStream("kafka", outputTopicName);

		mapper = new ObjectMapper();
	}

	@Override
	public void modelBuildingStatus(String modelConfName, String sessionId, String contextId, Date endTime, ModelBuildingStatus status) {
		boolean isSuccessful = status.equals(ModelBuildingStatus.SUCCESS);

		ModelBuildingStatusMessage message =
				new ModelBuildingStatusMessage(sessionId,modelConfName,
						TimestampUtils.convertToSeconds(endTime),contextId,isSuccessful,status.getMessage());

		try {
			String messageJsonString = mapper.writeValueAsString(message);
			collector.send(new OutgoingMessageEnvelope(systemStream, messageJsonString ));
		} catch (JsonProcessingException e) {
			logger.error("error while parsing message={} to json string",message,e);
		}
	}

	@Override
	public void modelBuildingSummary(String modelConfName, String sessionId, Date endTime, long numOfSuccesses, long numOfFailures) {
		ModelBuildingSummaryMessage message = new ModelBuildingSummaryMessage(sessionId,modelConfName,TimestampUtils.convertToSeconds(endTime),numOfSuccesses,numOfFailures);
		try {
			String messageJsonString = mapper.writeValueAsString(message);
			collector.send(new OutgoingMessageEnvelope(systemStream, messageJsonString));
		} catch (JsonProcessingException e) {
			logger.error("error while parsing message={} to json string",message,e);
		}

	}

	public void setMessageCollector(MessageCollector collector) {
		Assert.notNull(collector);
		this.collector = collector;
	}
}
