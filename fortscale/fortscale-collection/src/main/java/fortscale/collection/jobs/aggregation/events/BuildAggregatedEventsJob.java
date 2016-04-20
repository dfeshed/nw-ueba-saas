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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
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
	private static final String EVENT_PROCESSING_SYNC_TIMEOUT_IN_SECONDS = "eventProcessingSyncTimeoutInSeconds";
	private static final String SECONDS_BETWEEN_MODEL_SYNCS_JOB_PARAM = "secondsBetweenModelSyncs";
	private static final String MODEL_BUILDING_TIMEOUT_IN_SECONDS_JOB_PARAM = "modelBuildingTimeoutInSeconds";

	@Autowired
	private AggrFeatureEventBatchService aggrFeatureEventBatchService;

	@Autowired
	private ModelConfServiceUtils modelConfServiceUtils;
	@Autowired
	private ModelStore modelStore;

	private String sessionId;
	private ModelBuildingSyncService modelBuildingSyncService;
	private Collection<ModelConf> modelConfs;

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
		int hoursToRun = jobDataMapExtension.getJobDataMapIntValue(map, "hoursToRun", DEFAULT_HOURS_TO_RUN);
		cal.add(Calendar.HOUR, hoursToRun);
		batchStartTime = TimestampUtils.convertToSeconds(batchStartTime);
		batchEndTime = TimestampUtils.convertToSeconds(cal.getTimeInMillis());

		batchSize = jobDataMapExtension.getJobDataMapIntValue(map, "batchSize", DEFAULT_BATCH_SIZE);
		jobToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, "jobmonitor");
		jobClassToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, "classmonitor");
		checkRetries = jobDataMapExtension.getJobDataMapIntValue(map, "retries", DEFAULT_CHECK_RETRIES);

        long secondsBetweenModelSyncs = jobDataMapExtension.getJobDataMapLongValue(map, SECONDS_BETWEEN_MODEL_SYNCS_JOB_PARAM);
        long modelBuildingTimeoutInSeconds = jobDataMapExtension.getJobDataMapLongValue(map, MODEL_BUILDING_TIMEOUT_IN_SECONDS_JOB_PARAM);
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

        eventProcessingSyncTimeoutInSeconds = jobDataMapExtension.getJobDataMapLongValue(map, EVENT_PROCESSING_SYNC_TIMEOUT_IN_SECONDS);

		logger.info("Finish initializing BuildAggregatedEvents job");
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

		// Create event sender
		AggregationEventSender eventSender = new AggregationEventSender(batchSize, jobClassToMonitor, jobToMonitor,
                eventProcessingSyncTimeoutInSeconds);
		long endTimeGt = batchStartTime;
		while(endTimeGt<batchEndTime){
			long endTimeLte = Math.min(endTimeGt+ SECONDS_IN_HOUR, batchEndTime);

			runStep(eventSender, endTimeGt, endTimeLte);

			endTimeGt = endTimeLte;
		}

		eventSender.callSynchronizer(0L);
		modelBuildingSyncService.close();
		modelStore.removeModels(modelConfs, sessionId);

		logger.info("Finish running build aggregated events job");
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
