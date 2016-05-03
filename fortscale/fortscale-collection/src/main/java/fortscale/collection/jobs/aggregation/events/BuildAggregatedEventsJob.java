package fortscale.collection.jobs.aggregation.events;

import fortscale.aggregation.feature.event.batch.AggrFeatureEventBatchService;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.model.ModelBuildingSyncService;
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

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Created by tomerd on 31/12/2015.
 */
public class BuildAggregatedEventsJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(BuildAggregatedEventsJob.class);

	private static final int SECONDS_IN_HOUR = 3600;
	private static final String ENTITY_EVENTS_START_TIME_FIELD = "startTime";
	private static final int DEFAULT_BATCH_SIZE = 1000;
	private static final int DEFAULT_HOURS_TO_RUN = 24;
	private final int DEFAULT_CHECK_RETRIES = 60;
	private static final long DEFAULT_EVENT_PROCESSING_SYNC_TIMEOUT_IN_SECONDS = 600;
	private static final long DEFAULT_SECONDS_BETWEEN_MODEL_SYNCS = 86400;
	private static final long DEFAULT_MODEL_BUILDING_TIMEOUT_IN_SECONDS = 300;

	private static final String JOB_MONITOR_JOB_PARAM = "jobmonitor";
	private static final String CLASS_MONITOR_JOB_PARAM = "classmonitor";
	private static final String CHECK_RETRIES_JOB_PARAM = "retries";
	private static final String HOURS_TO_RUN_JOB_PARAM = "hoursToRun";
	private static final String BATCH_SIZE_JOB_PARAM = "batchSize";
	private static final String EVENT_PROCESSING_SYNC_TIMEOUT_IN_SECONDS_JOB_PARAM = "eventProcessingSyncTimeoutInSeconds";
	private static final String SECONDS_BETWEEN_MODEL_SYNCS_JOB_PARAM = "secondsBetweenModelSyncs";
	private static final String MODEL_BUILDING_TIMEOUT_IN_SECONDS_JOB_PARAM = "modelBuildingTimeoutInSeconds";
	private static final String BUILD_MODELS_FIRST_JOB_PARAM = "buildModelsFirst";


	@Autowired
	private AggrFeatureEventBatchService aggrFeatureEventBatchService;

	@Autowired
	private ModelConfServiceUtils modelConfServiceUtils;
	@Autowired
	private ModelStore modelStore;

	private String sessionId;
	private ModelBuildingSyncService modelBuildingSyncService;
	private Collection<ModelConf> modelConfs;
	private boolean buildModelsFirst;

    long batchStartTime;
	long batchEndTime;
	int batchSize;
	int checkRetries;
    long eventProcessingSyncTimeoutInSeconds;
	String jobToMonitor;
	String jobClassToMonitor;

	@Override protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Start Initializing BuildAggregatedEvents job");

		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		batchStartTime = jobDataMapExtension.getJobDataMapLongValue(map, ENTITY_EVENTS_START_TIME_FIELD);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(batchStartTime);
		int hoursToRun = jobDataMapExtension.getJobDataMapIntValue(map, HOURS_TO_RUN_JOB_PARAM, DEFAULT_HOURS_TO_RUN);
		cal.add(Calendar.HOUR, hoursToRun);
		batchStartTime = TimestampUtils.convertToSeconds(batchStartTime);
		batchEndTime = TimestampUtils.convertToSeconds(cal.getTimeInMillis());

		jobToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, JOB_MONITOR_JOB_PARAM);
		jobClassToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, CLASS_MONITOR_JOB_PARAM);

		// Parameters that can be override in the bdp.properties file
		batchSize = jobDataMapExtension.getJobDataMapIntValue(map, BATCH_SIZE_JOB_PARAM, DEFAULT_BATCH_SIZE);
		checkRetries = jobDataMapExtension.getJobDataMapIntValue(map, CHECK_RETRIES_JOB_PARAM, DEFAULT_CHECK_RETRIES);
        long secondsBetweenModelSyncs = jobDataMapExtension.getJobDataMapLongValue(map, SECONDS_BETWEEN_MODEL_SYNCS_JOB_PARAM, DEFAULT_SECONDS_BETWEEN_MODEL_SYNCS);
        long modelBuildingTimeoutInSeconds = jobDataMapExtension.getJobDataMapLongValue(map, MODEL_BUILDING_TIMEOUT_IN_SECONDS_JOB_PARAM, DEFAULT_MODEL_BUILDING_TIMEOUT_IN_SECONDS);
		eventProcessingSyncTimeoutInSeconds = jobDataMapExtension.getJobDataMapLongValue(map, EVENT_PROCESSING_SYNC_TIMEOUT_IN_SECONDS_JOB_PARAM, DEFAULT_EVENT_PROCESSING_SYNC_TIMEOUT_IN_SECONDS);
		buildModelsFirst = jobDataMapExtension.getJobDataMapBooleanValue(map, BUILD_MODELS_FIRST_JOB_PARAM, false);

		Assert.isTrue(modelBuildingTimeoutInSeconds >= 0);

		sessionId = getSessionId();

		// Map each aggr feature event conf of this job's data source to all of its model confs
		Map<String, Collection<ModelConf>> aggrEventConfNameToModelConfNamesMap = modelConfServiceUtils.getAggrEventConfNameToModelConfsMap();

		// Following service will build all the models relevant to this job's data source
		modelConfs = new ArrayList<>();
		aggrEventConfNameToModelConfNamesMap.values().forEach(modelConfs::addAll);
		Collection<String> modelConfNames = modelConfs.stream().map(ModelConf::getName).collect(Collectors.toList());
		modelBuildingSyncService = new ModelBuildingSyncService(sessionId, modelConfNames,
                secondsBetweenModelSyncs, modelBuildingTimeoutInSeconds);


		logger.info(String.format("Finish initializing BuildAggregatedEvents job: batchStartTime = %d, batchEndTime = %d, batchSize = %d, checkRetries = %d, secondsBetweenModelSyncs = %d, modelBuildingTimeoutInSeconds = %d, eventProcessingSyncTimeoutInSeconds = %d",
                batchStartTime, batchEndTime, batchSize, checkRetries, secondsBetweenModelSyncs, modelBuildingTimeoutInSeconds, eventProcessingSyncTimeoutInSeconds));
	}

	private String getSessionId() {
		long currentTimeSeconds = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		return String.format("%s_%d", getClass().getSimpleName(), currentTimeSeconds);
	}

	@Override protected int getTotalNumOfSteps() {
		return 1;
	}

	@Override protected boolean shouldReportDataReceived() {
		return false;
	}

	@Override protected void runSteps() throws Exception {
		logger.info("Running build aggregated events job");
		modelBuildingSyncService.init();

		if (buildModelsFirst) modelBuildingSyncService.buildModelsForcefully(batchStartTime);

		// Create event sender
		AggregationEventSender eventSender = new AggregationEventSender(batchSize, jobClassToMonitor, jobToMonitor,
                eventProcessingSyncTimeoutInSeconds);
		long endTimeGt = batchStartTime;
		while(endTimeGt<batchEndTime){
			long endTimeLte = Math.min(endTimeGt+ SECONDS_IN_HOUR, batchEndTime);

			runStep(eventSender, endTimeGt, endTimeLte);

			endTimeGt = endTimeLte;
		}

		eventSender.throttle(true);
		modelBuildingSyncService.close();
		modelStore.removeModels(modelConfs, sessionId);

		logger.info("**************** Finish running build aggregated events job ****************");
	}

	private void runStep(AggregationEventSender eventSender, long endTimeGt, long endTimeLte) throws Exception {
		// Run the aggregation event builder service
		aggrFeatureEventBatchService.buildAndSave(eventSender, endTimeGt, endTimeLte);

		// Check and build models if needed
		try {
			modelBuildingSyncService.buildModelsIfNeeded(endTimeLte);
		} catch (TimeoutException e) {
			logger.error(e.getMessage());
			throw e;
		}
		// Delete events after sending
		aggrFeatureEventBatchService.deleteAllEvents();
	}
}
