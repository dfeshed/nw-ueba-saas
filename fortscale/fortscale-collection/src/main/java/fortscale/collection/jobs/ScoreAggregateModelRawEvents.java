package fortscale.collection.jobs;

import fortscale.collection.jobs.model.ModelBuildingSyncService;
import fortscale.collection.services.FeatureBucketSyncService;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfServiceUtils;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.kafka.SimpleMetricsReader;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class ScoreAggregateModelRawEvents extends EventsFromDataTableToStreamingJob {
	private static final Logger logger = Logger.getLogger(ScoreAggregateModelRawEvents.class);
	private static final String SECONDS_BETWEEN_SYNCS_JOB_PARAM = "secondsBetweenSyncs";
	private static final String TIMEOUT_IN_SECONDS_JOB_PARAM = "timeoutInSeconds";
	private static final String SESSION_ID_JOB_PARAM = "sessionId";
	private static final String BUILD_MODELS_FIRST_JOB_PARAM = "buildModelsFirst";
	private static final String REMOVE_MODELS_FINALLY_JOB_PARAM = "removeModelsFinally";
	private static final String MAX_SYNC_GAP_IN_SECONDS_JOB_PARAM = "maxSyncGapInSeconds";

	@Autowired
	private ModelConfServiceUtils modelConfServiceUtils;
	@Autowired
	private ModelStore modelStore;

	@Value("${fortscale.samza.aggregation.events.streaming.metrics.task}")
	private String aggregationEventsJobName;
	@Value("${fortscale.samza.aggregation.events.streaming.metrics.class}")
	private String aggregationEventsClassName;
	@Value("${fortscale.samza.aggregation.events.streaming.metrics.last.message.epochtime}")
	private String lastMessageEpochtimeMetricName;

	private long secondsBetweenSyncs;
	private long timeoutInSeconds;
	private String sessionId;
	private boolean buildModelsFirst;
	private boolean removeModelsFinally;

	private Collection<ModelConf> modelConfs;
	private long lastEpochtimeSent;

	private SimpleMetricsReader simpleMetricsReader;
	private FeatureBucketSyncService featureBucketSyncService;
	private ModelBuildingSyncService modelBuildingSyncService;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		super.getJobParameters(jobExecutionContext);
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();

		secondsBetweenSyncs = jobDataMapExtension.getJobDataMapLongValue(map, SECONDS_BETWEEN_SYNCS_JOB_PARAM);
		timeoutInSeconds = jobDataMapExtension.getJobDataMapLongValue(map, TIMEOUT_IN_SECONDS_JOB_PARAM);
		sessionId = jobDataMapExtension.getJobDataMapStringValue(map, SESSION_ID_JOB_PARAM, generateSessionId());
		buildModelsFirst = jobDataMapExtension.getJobDataMapBooleanValue(map, BUILD_MODELS_FIRST_JOB_PARAM, false);
		removeModelsFinally = jobDataMapExtension.getJobDataMapBooleanValue(map, REMOVE_MODELS_FINALLY_JOB_PARAM, true);
		long maxSyncGapInSeconds = jobDataMapExtension.getJobDataMapLongValue(map, MAX_SYNC_GAP_IN_SECONDS_JOB_PARAM);

		Assert.isTrue(timeoutInSeconds >= 0);
		Assert.hasText(sessionId);
		Assert.isTrue(TimeUnit.MINUTES.toSeconds(fetchEventsStepInMinutes) + maxSourceDestinationTimeGap
				<= maxSyncGapInSeconds);

		modelConfs = new ArrayList<>();
		lastEpochtimeSent = -1;

		// Map each feature bucket conf of this job's data source to all of its model confs
		Map<String, Collection<ModelConf>> bucketConfNameToModelConfsMap = modelConfServiceUtils
				.getBucketConfNameToModelConfsMap(dataSource);

		// Create a reader to track the aggregation events streaming task metrics
		simpleMetricsReader = new SimpleMetricsReader(getClass().getSimpleName(), 0, aggregationEventsJobName,
				aggregationEventsClassName, Collections.singleton(lastMessageEpochtimeMetricName));

		// Following service will sync the feature bucket metadata before the models are built
		featureBucketSyncService = new FeatureBucketSyncService(bucketConfNameToModelConfsMap.keySet(),
				secondsBetweenSyncs, maxSyncGapInSeconds, timeoutInSeconds);

		// Following service will build all the models relevant to this job's data source
		bucketConfNameToModelConfsMap.values().forEach(modelConfs::addAll);
		Collection<String> modelConfNames = modelConfs.stream().map(ModelConf::getName).collect(Collectors.toList());
		modelBuildingSyncService = new ModelBuildingSyncService(sessionId, modelConfNames,
				secondsBetweenSyncs, timeoutInSeconds);
	}

	@Override
	protected void runSteps() throws Exception {
		simpleMetricsReader.start();
		featureBucketSyncService.init();
		modelBuildingSyncService.init();

		if (buildModelsFirst) {
			modelBuildingSyncService.buildModelsForcefully(latestEventTime - deltaTimeInSec);
		}

		super.runSteps();
		waitForEventWithEpochtimeToReachAggregation(lastEpochtimeSent);
		long lastSyncEpochtime = (lastEpochtimeSent / secondsBetweenSyncs) * secondsBetweenSyncs + secondsBetweenSyncs;
		featureBucketSyncService.syncForcefully(lastSyncEpochtime);

		simpleMetricsReader.end();
		modelBuildingSyncService.close();

		if (removeModelsFinally) {
			logger.info("Removing models with session ID {} finally.", sessionId);
			modelStore.removeModels(modelConfs, sessionId);
			modelBuildingSyncService.initModelBuildingRegistrations();
		}
	}

	@Override
	protected void throttle(int numOfResults, long latestEpochTimeSent, long nextTimestampCursor) throws Exception {
		lastEpochtimeSent = latestEpochTimeSent;

		try {
			featureBucketSyncService.syncIfNeeded(latestEpochTimeSent);
			modelBuildingSyncService.buildModelsIfNeeded(latestEpochTimeSent);
		} catch (TimeoutException e) {
			logger.error(e.getMessage());
			throw e;
		}

		logger.info("Throttling against destination table: Latest epochtime sent to topic = {}.", latestEpochTimeSent);
		super.throttle(numOfResults, latestEpochTimeSent, nextTimestampCursor);
		waitForEventWithEpochtimeToReachAggregation(latestEpochTimeSent - maxSourceDestinationTimeGap);
	}

	private String generateSessionId() {
		long currentTimeSeconds = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		return String.format("%s_%s_%d", getClass().getSimpleName(), dataSource, currentTimeSeconds);
	}

	private void waitForEventWithEpochtimeToReachAggregation(long epochtime) throws TimeoutException {
		if (throttlingSleepField == null || throttlingSleepField <= 0) {
			return;
		} else {
			logger.info("Waiting for event with epochtime {} to reach aggregation.", epochtime);
		}

		boolean found = false;
		long timeoutInMillis = TimeUnit.SECONDS.toMillis(timeoutInSeconds);
		long startTimeInMillis = System.currentTimeMillis();

		while (!found) {
			Long metricValue = simpleMetricsReader.getLong(lastMessageEpochtimeMetricName);

			if (metricValue == null || metricValue < epochtime) {
				if (timeoutInMillis > 0 && System.currentTimeMillis() - startTimeInMillis > timeoutInMillis) {
					throwTimeoutException(epochtime);
				}

				try {
					Thread.sleep(TimeUnit.SECONDS.toMillis(throttlingSleepField));
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			} else {
				found = true;
			}
		}
	}

	private void throwTimeoutException(long epochtime) throws TimeoutException {
		String exceptionMsg = String.format("%s did not receive event with epochtime %d in %d seconds.",
				aggregationEventsJobName, epochtime, timeoutInSeconds);
		throw new TimeoutException(exceptionMsg);
	}
}
