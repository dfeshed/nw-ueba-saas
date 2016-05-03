package fortscale.collection.jobs.model;

import fortscale.utils.kafka.KafkaEventsWriter;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Configurable(preConstruction = true)
public class ModelBuildingSyncService {
	private static final long MILLIS_TO_SLEEP_BETWEEN_END_TIME_EQUALITY_CHECKS = 1000;

	@Value("${fortscale.model.build.control.input.topic}")
	private String controlInputTopic;
	@Value("${fortscale.model.build.control.output.topic}")
	private String controlOutputTopic;
	@Value("${fortscale.model.build.message.field.session.id}")
	private String sessionIdJsonField;
	@Value("${fortscale.model.build.message.field.model.conf.name}")
	private String modelConfNameJsonField;
	@Value("${fortscale.model.build.message.field.end.time.in.seconds}")
	private String endTimeInSecondsJsonField;

	private String sessionId;
	private Collection<String> modelConfNames;
	private long secondsBetweenEndTimes;
	private long timeoutInMillis;

	private long lastEndTimeInSeconds;
	private KafkaEventsWriter writer;
	private ModelBuildingSummaryReader reader;

	public ModelBuildingSyncService(
			String sessionId, Collection<String> modelConfNames,
			long secondsBetweenEndTimes, long timeoutInSeconds) {

		Assert.hasText(sessionId);
		Assert.notEmpty(modelConfNames);
		modelConfNames.forEach(Assert::hasText);
		Assert.isTrue(secondsBetweenEndTimes > 0);
		Assert.isTrue(timeoutInSeconds >= 0);

		this.sessionId = sessionId;
		this.modelConfNames = modelConfNames;
		this.secondsBetweenEndTimes = secondsBetweenEndTimes;
		this.timeoutInMillis = TimeUnit.SECONDS.toMillis(timeoutInSeconds);
	}

	public void init() {
		lastEndTimeInSeconds = -1;
		writer = new KafkaEventsWriter(controlInputTopic);
		reader = new ModelBuildingSummaryReader(getClass().getSimpleName(), controlOutputTopic, 0);
		reader.start();
	}

	public void close() {
		reader.end();
	}

	public void buildModelsIfNeeded(long currentTimeInSeconds) throws TimeoutException {
		long currentEndTimeInSeconds = (currentTimeInSeconds / secondsBetweenEndTimes) * secondsBetweenEndTimes;
		if (lastEndTimeInSeconds == -1) lastEndTimeInSeconds = currentEndTimeInSeconds;

		if (currentEndTimeInSeconds > lastEndTimeInSeconds) {
			lastEndTimeInSeconds = currentEndTimeInSeconds;
			sendCommands(lastEndTimeInSeconds);
			waitForSummaryMessages(lastEndTimeInSeconds);
		}
	}

	public void buildModelsForcefully(long currentTimeInSeconds) throws TimeoutException {
		sendCommands(currentTimeInSeconds);
		waitForSummaryMessages(currentTimeInSeconds);
	}

	private void sendCommands(long endTimeInSeconds) {
		JSONObject command = new JSONObject();
		command.put(sessionIdJsonField, sessionId);
		command.put(endTimeInSecondsJsonField, endTimeInSeconds);

		for (String modelConfName : modelConfNames) {
			command.put(modelConfNameJsonField, modelConfName);
			writer.send(null, command.toJSONString(JSONStyle.NO_COMPRESS));
		}
	}

	@SuppressWarnings("EmptyCatchBlock")
	private void waitForSummaryMessages(long endTimeInSeconds) throws TimeoutException {
		long startTimeInMillis = System.currentTimeMillis();

		while (!isEndTimeEqual(endTimeInSeconds)) {
			if (timeoutInMillis > 0 && System.currentTimeMillis() - startTimeInMillis > timeoutInMillis) {
				throwTimeoutException(endTimeInSeconds);
			}

			try {
				Thread.sleep(MILLIS_TO_SLEEP_BETWEEN_END_TIME_EQUALITY_CHECKS);
			} catch (InterruptedException e) {}
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
