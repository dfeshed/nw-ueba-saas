package fortscale.collection.jobs.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.ml.model.message.ModelBuildingCommandMessage;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Configurable(preConstruction = true)
public class ModelBuildingSyncService {
	public static final String FORTSCALE_MODEL_BUILD_CONTROL_INPUT_TOPIC = "fortscale.model.build.control.input.topic";
	public static final String FORTSCALE_MODEL_BUILD_CONTROL_OUTPUT_TOPIC = "fortscale.model.build.control.output.topic";
	private static final Logger logger = Logger.getLogger(ModelBuildingSyncService.class);
	private static final long MILLIS_TO_SLEEP_BETWEEN_END_TIME_EQUALITY_CHECKS = 1000;
	private final ObjectMapper objectMapper;

	private String controlInputTopic;
	private String controlOutputTopic;

	private String sessionId;
	private Collection<String> modelConfNames;
	private long secondsBetweenEndTimes;
	private long timeoutInMillis;

	private long lastEndTimeInSeconds;
	private KafkaEventsWriter writer;
	private ModelBuildingSummaryReader reader;

	public ModelBuildingSyncService(
			String sessionId, Collection<String> modelConfNames,
			long secondsBetweenEndTimes, long timeoutInSeconds,
			String controlInputTopic,String controlOutputTopic
	) {

		Assert.hasText(sessionId);
		Assert.notEmpty(modelConfNames);
		modelConfNames.forEach(Assert::hasText);
		Assert.isTrue(secondsBetweenEndTimes > 0);
		Assert.isTrue(timeoutInSeconds >= 0);

		this.sessionId = sessionId;
		this.modelConfNames = modelConfNames;
		this.secondsBetweenEndTimes = secondsBetweenEndTimes;
		this.timeoutInMillis = TimeUnit.SECONDS.toMillis(timeoutInSeconds);
		this.controlInputTopic = controlInputTopic;
		this.controlOutputTopic = controlOutputTopic;
		this.objectMapper = new ObjectMapper();
	}

	public void init() {
		lastEndTimeInSeconds = -1;
		writer = new KafkaEventsWriter(controlInputTopic);
		reader = new ModelBuildingSummaryReader(getClass().getSimpleName(), controlOutputTopic, 0);
		reader.start();
	}

	public void close() {
		try {
			reader.end();
		} catch (Exception e){
			logger.error("got an exception while ending the ModelBuildingSummaryReader", e);
		}
		try {
			writer.close();
		} catch (Exception e){
			logger.error("got an exception while closing the KafkaEventsWriter", e);
		}
	}

	public void buildModelsIfNeeded(long currentTimeInSeconds) throws TimeoutException, JsonProcessingException {
		long currentEndTimeInSeconds = (currentTimeInSeconds / secondsBetweenEndTimes) * secondsBetweenEndTimes;
		if (lastEndTimeInSeconds == -1) lastEndTimeInSeconds = currentEndTimeInSeconds;

		if (currentEndTimeInSeconds > lastEndTimeInSeconds) {
			lastEndTimeInSeconds = currentEndTimeInSeconds;
			sendCommands(lastEndTimeInSeconds);
			waitForSummaryMessages(lastEndTimeInSeconds);
		}
	}

	public void buildModelsForcefully(long currentTimeInSeconds) throws TimeoutException, JsonProcessingException {
		sendCommands(currentTimeInSeconds);
		waitForSummaryMessages(currentTimeInSeconds);
	}

	public void initModelBuildingRegistrations() throws JsonProcessingException {
		logger.info("Initializing model building registrations: Session ID = {}.", sessionId);

		for (String modelConfName : modelConfNames) {
			ModelBuildingCommandMessage command = new ModelBuildingCommandMessage(sessionId,modelConfName,-1,false);
			String commandJsonString = objectMapper.writeValueAsString(command);
			writer.send(null, commandJsonString);
		}
	}

	private void sendCommands(long endTimeInSeconds) throws JsonProcessingException {
		logger.info("Sending model building commands: Session ID = {}, end time in seconds = {}.",
				sessionId, endTimeInSeconds);

		for (String modelConfName : modelConfNames) {
			ModelBuildingCommandMessage command = new ModelBuildingCommandMessage(sessionId,modelConfName,endTimeInSeconds,false);
			String commandJsonString = objectMapper.writeValueAsString(command);
			writer.send(null, commandJsonString);
		}
	}

	private void waitForSummaryMessages(long endTimeInSeconds) throws TimeoutException {
		logger.info("Waiting for model building summary messages: Session ID = {}, end time in seconds = {}.",
				sessionId, endTimeInSeconds);
		long startTimeInMillis = System.currentTimeMillis();

		while (!isEndTimeEqual(endTimeInSeconds)) {
			if (timeoutInMillis > 0 && System.currentTimeMillis() - startTimeInMillis > timeoutInMillis) {
				throwTimeoutException(endTimeInSeconds);
			}

			try {
				Thread.sleep(MILLIS_TO_SLEEP_BETWEEN_END_TIME_EQUALITY_CHECKS);
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}
	}

	private boolean isEndTimeEqual(long endTimeInSeconds) {
		for (String modelConfName : modelConfNames) {
			if (!reader.isEndTimeEqual(sessionId, modelConfName, endTimeInSeconds)) {
				return false;
			}
		}
		return true;
	}

	private void throwTimeoutException(long endTimeInSeconds) throws TimeoutException {
		String msg1 = String.format("Did not receive all model building summary messages in %d seconds.",
				TimeUnit.MILLISECONDS.toSeconds(timeoutInMillis));
		String msg2 = String.format("Session ID = %s, end time in seconds = %d.",
				sessionId, endTimeInSeconds);
		throw new TimeoutException(String.format("%s %s", msg1, msg2));
	}
}
