package fortscale.collection.jobs.model;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

public class ModelBuildingJob extends FortscaleJob {
	// Job XML constants
	private static final String SESSION_ID_KEY_NAME = "sessionId";
	private static final String BUILD_ALL_MODELS_KEY_NAME = "buildAllModels";
	private static final String MODELS_TO_BUILD_KEY_NAME = "modelsToBuild";
	private static final String DELIMITER = ",";
	private static final String TARGET_TOPIC_KEY_NAME = "targetTopic";

	// Kafka topic event constants
	private static final String SESSION_ID_JSON_FIELD = "sessionId";
	private static final String MODEL_CONF_NAME_JSON_FIELD = "modelConfName";
	private static final String END_TIME_IN_SECONDS_JSON_FIELD = "endTimeInSeconds";
	private static final String ALL_MODELS_CONSTANT_VALUE = "ALL_MODELS";

	private String sessionId;
	private boolean buildAllModels;
	private List<String> modelsToBuild;
	private String targetTopic;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();

		sessionId = jobDataMapExtension.getJobDataMapStringValue(jobDataMap, SESSION_ID_KEY_NAME, null);
		Assert.hasText(sessionId, "Missing valid session ID.");

		buildAllModels = jobDataMapExtension.getJobDataMapBooleanValue(jobDataMap, BUILD_ALL_MODELS_KEY_NAME, false);
		if (!buildAllModels) {
			String errorMsg = String.format("Either %s must be set to true, or a %s list must be given.",
					BUILD_ALL_MODELS_KEY_NAME, MODELS_TO_BUILD_KEY_NAME);
			Assert.isTrue(jobDataMapExtension.isJobDataMapContainKey(jobDataMap, MODELS_TO_BUILD_KEY_NAME), errorMsg);

			String joinedModelsToBuild = jobDataMapExtension.getJobDataMapStringValue(
					jobDataMap, MODELS_TO_BUILD_KEY_NAME, StringUtils.EMPTY);
			modelsToBuild = Arrays.asList(StringUtils.split(joinedModelsToBuild, DELIMITER));

			for (String modelToBuild : modelsToBuild) {
				Assert.hasText(modelToBuild, String.format("Invalid model conf name %s.", modelToBuild));
			}
		}

		targetTopic = jobDataMapExtension.getJobDataMapStringValue(jobDataMap, TARGET_TOPIC_KEY_NAME, null);
		Assert.hasText(targetTopic, "Missing valid target topic name.");
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 1;
	}

	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep("Send model building commands to Kafka topic");
		KafkaEventsWriter kafkaEventsWriter = new KafkaEventsWriter(targetTopic);
		int counter = 0;

		JSONObject event = new JSONObject();
		event.put(SESSION_ID_JSON_FIELD, sessionId);
		long currTimeSec = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		event.put(END_TIME_IN_SECONDS_JSON_FIELD, currTimeSec);

		if (buildAllModels) {
			event.put(MODEL_CONF_NAME_JSON_FIELD, ALL_MODELS_CONSTANT_VALUE);
			kafkaEventsWriter.send(sessionId, event.toJSONString(JSONStyle.NO_COMPRESS));
			counter++;
		} else {
			for (String modelToBuild : modelsToBuild) {
				event.put(MODEL_CONF_NAME_JSON_FIELD, modelToBuild);
				kafkaEventsWriter.send(sessionId, event.toJSONString(JSONStyle.NO_COMPRESS));
				counter++;
			}
		}

		monitor.addDataReceived(getMonitorId(), new JobDataReceived("Model building commands", counter, "Command"));
		finishStep();
	}
}
