package fortscale.collection.jobs.model;

import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

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

	@SuppressWarnings("EmptyCatchBlock")
	public void buildModelsIfNeeded(long currentTimeInSeconds) {
		long currentEndTimeInSeconds = (currentTimeInSeconds / secondsBetweenEndTimes) * secondsBetweenEndTimes;
		if (lastEndTimeInSeconds == -1) lastEndTimeInSeconds = currentEndTimeInSeconds;

		if (currentEndTimeInSeconds > lastEndTimeInSeconds) {
			lastEndTimeInSeconds = currentEndTimeInSeconds;
			sendCommands();
			long startTimeInMillis = System.currentTimeMillis();

			while (!isEndTimeEqual()) {
				if (timeoutInMillis > 0 && System.currentTimeMillis() - startTimeInMillis > timeoutInMillis) {
					throwTimeoutException(currentTimeInSeconds);
				}

				try {
					Thread.sleep(MILLIS_TO_SLEEP_BETWEEN_END_TIME_EQUALITY_CHECKS);
				} catch (InterruptedException e) {}
			}
		}
	}

	private void sendCommands() {
		JSONObject command = new JSONObject();
		command.put(sessionIdJsonField, sessionId);
		command.put(endTimeInSecondsJsonField, TimestampUtils.convertToSeconds(lastEndTimeInSeconds));

		for (String modelConfName : modelConfNames) {
			command.put(modelConfNameJsonField, modelConfName);
			writer.send(null, command.toJSONString(JSONStyle.NO_COMPRESS));
		}
	}

	private boolean isEndTimeEqual() {
		for (String modelConfName : modelConfNames) {
			if (!reader.isEndTimeEqual(sessionId, modelConfName, lastEndTimeInSeconds)) {
				return false;
			}
		}

		return true;
	}

	private void throwTimeoutException(long currentTimeInSeconds) {
		String msg1 = String.format("Did not receive all model building summary messages in %d seconds.",
				TimeUnit.MILLISECONDS.toSeconds(timeoutInMillis));
		String msg2 = String.format("Session ID = %s, current time in seconds = %d.",
				sessionId, currentTimeInSeconds);
		throw new RuntimeException(String.format("%s %s", msg1, msg2));
	}
}
