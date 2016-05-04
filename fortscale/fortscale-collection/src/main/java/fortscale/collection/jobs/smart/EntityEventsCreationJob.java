package fortscale.collection.jobs.smart;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.model.ModelBuildingSyncService;
import fortscale.entity.event.EntityEventDataStore;
import fortscale.entity.event.EntityEventService;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfServiceUtils;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class EntityEventsCreationJob extends FortscaleJob {
	private static Logger logger = Logger.getLogger(EntityEventsCreationJob.class);
	private static final String STEP_NAME = "Create and send entity events to Kafka topic";
	private static final String START_TIME_IN_SECONDS_ARG = "startTimeInSeconds";
	private static final String END_TIME_IN_SECONDS_ARG = "endTimeInSeconds";
	private static final String TIME_INTERVAL_IN_SECONDS_ARG = "timeIntervalInSeconds";
	private static final String BATCH_SIZE_ARG = "batchSize";
	private static final String CHECK_RETRIES_ARG = "retries";

	private static final String EVENT_PROCESSING_SYNC_TIMEOUT_IN_SECONDS_JOB_PARAM = "eventProcessingSyncTimeoutInSeconds";
	private static final String SECONDS_BETWEEN_MODEL_SYNCS_JOB_PARAM = "secondsBetweenModelSyncs";
	private static final String MODEL_BUILDING_TIMEOUT_IN_SECONDS_JOB_PARAM = "modelBuildingTimeoutInSeconds";

	private static final long DEFAULT_EVENT_PROCESSING_SYNC_TIMEOUT_IN_SECONDS = 600;
	private static final long DEFAULT_SECONDS_BETWEEN_MODEL_SYNCS = 86400;
	private static final long DEFAULT_MODEL_BUILDING_TIMEOUT_IN_SECONDS = 300;
	private static final String BUILD_MODELS_FIRST_JOB_PARAM = "buildModelsFirst";


	@Autowired
	private EntityEventDataStore entityEventDataStore;
	@Autowired
	private ModelConfServiceUtils modelConfServiceUtils;
	@Autowired
	private ModelStore modelStore;

	private long startTimeInSeconds;
	private long endTimeInSeconds;
	private long timeIntervalInSeconds;
	private MongoThrottlerEntityEventSender sender;
	private EntityEventService entityEventService;

	int batchSize;
	int checkRetries;
	long eventProcessingSyncTimeoutInSeconds;

	private String sessionId;
	private ModelBuildingSyncService modelBuildingSyncService;
	private Collection<ModelConf> modelConfs;
	private boolean buildModelsFirst;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();

		// Use converter to ensure start and end times are in seconds
		startTimeInSeconds = jobDataMapExtension.getJobDataMapLongValue(jobDataMap, START_TIME_IN_SECONDS_ARG);
		startTimeInSeconds = TimestampUtils.convertToSeconds(startTimeInSeconds);
		endTimeInSeconds = jobDataMapExtension.getJobDataMapLongValue(jobDataMap, END_TIME_IN_SECONDS_ARG);
		endTimeInSeconds = TimestampUtils.convertToSeconds(endTimeInSeconds);
		timeIntervalInSeconds = jobDataMapExtension.getJobDataMapLongValue(jobDataMap, TIME_INTERVAL_IN_SECONDS_ARG);
		Assert.isTrue(startTimeInSeconds >= 0);
		Assert.isTrue(endTimeInSeconds >= startTimeInSeconds);
		Assert.isTrue(timeIntervalInSeconds > 0);

		// Parameters that can be override in the bdp.properties file
		this.batchSize = jobDataMapExtension.getJobDataMapIntValue(jobDataMap, BATCH_SIZE_ARG);
		this.checkRetries = jobDataMapExtension.getJobDataMapIntValue(jobDataMap, CHECK_RETRIES_ARG);
		long secondsBetweenModelSyncs = jobDataMapExtension.getJobDataMapLongValue(jobDataMap, SECONDS_BETWEEN_MODEL_SYNCS_JOB_PARAM, DEFAULT_SECONDS_BETWEEN_MODEL_SYNCS);
		long modelBuildingTimeoutInSeconds = jobDataMapExtension.getJobDataMapLongValue(jobDataMap, MODEL_BUILDING_TIMEOUT_IN_SECONDS_JOB_PARAM, DEFAULT_MODEL_BUILDING_TIMEOUT_IN_SECONDS);
		eventProcessingSyncTimeoutInSeconds = jobDataMapExtension.getJobDataMapLongValue(jobDataMap, EVENT_PROCESSING_SYNC_TIMEOUT_IN_SECONDS_JOB_PARAM, DEFAULT_EVENT_PROCESSING_SYNC_TIMEOUT_IN_SECONDS);
		buildModelsFirst = jobDataMapExtension.getJobDataMapBooleanValue(jobDataMap, BUILD_MODELS_FIRST_JOB_PARAM, false);

		Assert.isTrue(modelBuildingTimeoutInSeconds >= 0);

		sessionId = getSessionId();

		entityEventService = new EntityEventService(entityEventDataStore);
		// Map each aggr feature event conf of this job's data source to all of its model confs
		Map<String, Collection<ModelConf>> entityEventConfNameToModelConfNamesMap = modelConfServiceUtils.getEntityEventConfNameToModelConfsMap();

		// Following service will build all the models relevant to this job's data source
		modelConfs = new ArrayList<>();
		entityEventConfNameToModelConfNamesMap.values().forEach(modelConfs::addAll);
		Collection<String> modelConfNames = modelConfs.stream().map(ModelConf::getName).collect(Collectors.toList());
		modelBuildingSyncService = new ModelBuildingSyncService(sessionId, modelConfNames,
				secondsBetweenModelSyncs, modelBuildingTimeoutInSeconds);


		logger.info(String.format("Finish initializing EntityEventsCreationJob: startTimeInSeconds = %d, endTimeInSeconds = %d, batchSize = %d, checkRetries = %d, secondsBetweenModelSyncs = %d, modelBuildingTimeoutInSeconds = %d, eventProcessingSyncTimeoutInSeconds = %d",
				startTimeInSeconds, endTimeInSeconds, batchSize, checkRetries, secondsBetweenModelSyncs, modelBuildingTimeoutInSeconds, eventProcessingSyncTimeoutInSeconds));

	}

	@Override
	protected int getTotalNumOfSteps() {
		return 1;
	}

	@Override
	protected boolean shouldReportDataReceived() {
		return false;
	}

	@Override
	protected void runSteps() throws Exception {
		startNewStep(STEP_NAME);
		logger.info("**************** Start sending and scoring entity events job ****************");
		modelBuildingSyncService.init();

		if (buildModelsFirst) {
			logger.info("Building models before starting to create events...");
			modelBuildingSyncService.buildModelsForcefully(startTimeInSeconds);
			logger.info("Finished to build models.");
		}

		long currentTimeInSeconds = startTimeInSeconds;
		Date currentStartTime;
		Date currentEndTime;

		sender = new MongoThrottlerEntityEventSender(batchSize, checkRetries, eventProcessingSyncTimeoutInSeconds);

		while (currentTimeInSeconds < endTimeInSeconds) {
			currentStartTime = new Date(TimestampUtils.convertToMilliSeconds(currentTimeInSeconds));
			currentTimeInSeconds = Math.min(currentTimeInSeconds + timeIntervalInSeconds, endTimeInSeconds);
			currentEndTime = new Date(TimestampUtils.convertToMilliSeconds(currentTimeInSeconds));

			entityEventService.sendEntityEventsInTimeRange(currentStartTime, currentEndTime,
					System.currentTimeMillis(), sender, false);

			// Check and build models if needed
			try {
				modelBuildingSyncService.buildModelsIfNeeded(currentTimeInSeconds);
			} catch (TimeoutException e) {
				logger.error(e.getMessage());
				throw e;
			}
		}

		sender.throttle();
		modelBuildingSyncService.close();
		modelStore.removeModels(modelConfs, sessionId);

		logger.info("**************** Finish sending and scoring entity events job ****************");
		finishStep();
	}

	private String getSessionId() {
		long currentTimeSeconds = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		return String.format("%s_%d", getClass().getSimpleName(), currentTimeSeconds);
	}
}
