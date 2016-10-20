package fortscale.collection.jobs.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.ml.model.message.ModelBuildingCommandMessage;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.time.TimestampUtils;
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

	@Value("${fortscale.model.build.message.constant.all.models}")
	private String allModelsConstantValue;

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

			modelsToBuild = jobDataMapExtension.getJobDataMapListOfStringsValue(
					jobDataMap, MODELS_TO_BUILD_KEY_NAME, DELIMITER);

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
		long currTimeSec = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		ObjectMapper mapper = new ObjectMapper();

		if (buildAllModels) {
			ModelBuildingCommandMessage commandMessage = new ModelBuildingCommandMessage(sessionId,allModelsConstantValue,currTimeSec);
			sendBuildCommand(kafkaEventsWriter, mapper, commandMessage);
			counter++;
		} else {
			for (String modelToBuild : modelsToBuild) {
				ModelBuildingCommandMessage commandMessage = new ModelBuildingCommandMessage(sessionId,modelToBuild,currTimeSec);
				sendBuildCommand(kafkaEventsWriter,mapper,commandMessage);
				counter++;
			}
		}

		monitor.addDataReceived(getMonitorId(), new JobDataReceived("Model building commands", counter, "Command"));
		finishStep();
	}

	/**
	 * converts commandMessage to json-string and sends to kafka
	 * @param kafkaEventsWriter
	 * @param mapper
	 * @param commandMessage
	 * @throws JsonProcessingException
     */
	private void sendBuildCommand(KafkaEventsWriter kafkaEventsWriter, ObjectMapper mapper, ModelBuildingCommandMessage commandMessage) throws JsonProcessingException {
		String commandMessageAsJsonString = mapper.writeValueAsString(commandMessage);
		kafkaEventsWriter.send(sessionId, commandMessageAsJsonString);
	}
}
