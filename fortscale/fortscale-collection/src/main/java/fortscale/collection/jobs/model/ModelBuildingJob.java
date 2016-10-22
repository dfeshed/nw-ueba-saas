package fortscale.collection.jobs.model;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.List;

public class ModelBuildingJob extends FortscaleJob {
	// Job XML constants
	private static final String SESSION_ID_KEY_NAME = "sessionId";
	private static final String BUILD_ALL_MODELS_KEY_NAME = "buildAllModels";
	private static final String MODELS_TO_BUILD_KEY_NAME = "modelsToBuild";
	private static final String DELIMITER = ",";
	private static final String TARGET_TOPIC_KEY_NAME = "targetTopic";
	private static final String SELECT_HIGH_SCORE_CONTEXTS = "selectHighScoreContexts";

	@Value("${fortscale.model.build.message.field.session.id}")
	private String sessionIdJsonField;
	@Value("${fortscale.model.build.message.field.model.conf.name}")
	private String modelConfNameJsonField;
	@Value("${fortscale.model.build.message.field.end.time.in.seconds}")
	private String endTimeInSecondsJsonField;
	@Value("${fortscale.model.build.message.field.select.high.score.contexts}")
	private String selectHighScoreContextsJsonField;
	@Value("${fortscale.model.build.message.constant.all.models}")
	private String allModelsConstantValue;

	private String sessionId;
	private boolean buildAllModels;
	private List<String> modelsToBuild;
	private String targetTopic;
	private boolean selectHighScoreContexts;

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

			modelsToBuild = jobDataMapExtension.getJobDataMapListOfStringsValue(
					jobDataMap, MODELS_TO_BUILD_KEY_NAME, DELIMITER);

			for (String modelToBuild : modelsToBuild) {
				Assert.hasText(modelToBuild, String.format("Invalid model conf name %s.", modelToBuild));
			}
		}

		targetTopic = jobDataMapExtension.getJobDataMapStringValue(jobDataMap, TARGET_TOPIC_KEY_NAME, null);
		Assert.hasText(targetTopic, "Missing valid target topic name.");
		selectHighScoreContexts = jobDataMapExtension.getJobDataMapBooleanValue(jobDataMap, SELECT_HIGH_SCORE_CONTEXTS, false);
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
		event.put(sessionIdJsonField, sessionId);
		long currTimeSec = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		event.put(endTimeInSecondsJsonField, currTimeSec);
		event.put(selectHighScoreContextsJsonField, selectHighScoreContexts ? 1 : 0);

		if (buildAllModels) {
			event.put(modelConfNameJsonField, allModelsConstantValue);
			kafkaEventsWriter.send(sessionId, event.toJSONString(JSONStyle.NO_COMPRESS));
			counter++;
		} else {
			for (String modelToBuild : modelsToBuild) {
				event.put(modelConfNameJsonField, modelToBuild);
				kafkaEventsWriter.send(sessionId, event.toJSONString(JSONStyle.NO_COMPRESS));
				counter++;
			}
		}

		monitor.addDataReceived(getMonitorId(), new JobDataReceived("Model building commands", counter, "Command"));
		finishStep();
	}
}
