package fortscale.collection.jobs;

import fortscale.collection.jobs.model.ModelBuildingSyncService;
import fortscale.collection.services.FeatureBucketSyncService;
import fortscale.ml.model.ModelConfServiceUtils;
import fortscale.utils.kafka.SimpleMetricsReader;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.concurrent.TimeoutException;

public class ScoreAggregateModelRawEvents extends EventsFromDataTableToStreamingJob {
	private static final Logger logger = Logger.getLogger(ScoreAggregateModelRawEvents.class);
	private static final String SECONDS_BETWEEN_SYNCS_JOB_PARAM = "secondsBetweenSyncs";
	private static final String MAX_SYNC_GAP_IN_SECONDS_JOB_PARAM = "maxSyncGapInSeconds";
	private static final String TIMEOUT_IN_SECONDS_JOB_PARAM = "timeoutInSeconds";

	@Autowired
	private ModelConfServiceUtils modelConfServiceUtils;

	@Value("${fortscale.samza.aggregation.events.streaming.metrics.task}")
	private String aggregationEventsJobName;
	@Value("${fortscale.samza.aggregation.events.streaming.metrics.class}")
	private String aggregationEventsClassName;
	@Value("${fortscale.samza.aggregation.events.streaming.metrics.last.message.epochtime}")
	private String lastMessageEpochtimeMetricName;

	private SimpleMetricsReader simpleMetricsReader;
	private FeatureBucketSyncService featureBucketSyncService;
	private ModelBuildingSyncService modelBuildingSyncService;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		super.getJobParameters(jobExecutionContext);
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		long secondsBetweenSyncs = jobDataMapExtension.getJobDataMapLongValue(map, SECONDS_BETWEEN_SYNCS_JOB_PARAM);
		long maxSyncGapInSeconds = jobDataMapExtension.getJobDataMapLongValue(map, MAX_SYNC_GAP_IN_SECONDS_JOB_PARAM);
		long timeoutInSeconds = jobDataMapExtension.getJobDataMapLongValue(map, TIMEOUT_IN_SECONDS_JOB_PARAM);

		// Map each feature bucket conf of this job's data source to all of its model confs
		Map<String, Collection<String>> bucketConfNameToModelConfNamesMap = modelConfServiceUtils
				.getBucketConfNameToModelConfNamesMap(dataSource);

		// Create a reader to track the aggregation events streaming task metrics
		simpleMetricsReader = new SimpleMetricsReader(getClass().getSimpleName(), 0, aggregationEventsJobName,
				aggregationEventsClassName, Collections.singleton(lastMessageEpochtimeMetricName));

		// Following service will sync the feature bucket metadata before the models are built
		featureBucketSyncService = new FeatureBucketSyncService(bucketConfNameToModelConfNamesMap.keySet(),
				secondsBetweenSyncs, maxSyncGapInSeconds, timeoutInSeconds);

		// Following service will build all the models relevant to this job's data source
		List<String> allModelConfNames = new ArrayList<>();
		bucketConfNameToModelConfNamesMap.values().forEach(allModelConfNames::addAll);
		modelBuildingSyncService = new ModelBuildingSyncService(getSessionId(), allModelConfNames,
				secondsBetweenSyncs, timeoutInSeconds);
	}

	@Override
	protected void runSteps() throws Exception {
		simpleMetricsReader.start();
		featureBucketSyncService.init();
		modelBuildingSyncService.init();

		super.runSteps();
		featureBucketSyncService.syncForcefully(latestEventTime);

		simpleMetricsReader.end();
		modelBuildingSyncService.close();
	}

	@Override
	protected void throttle(int numOfResults, long latestEpochTimeSent, long nextTimestampCursor) {
		super.throttle(numOfResults, latestEpochTimeSent, nextTimestampCursor);

		Long metric;
		do {
			metric = simpleMetricsReader.getLong(lastMessageEpochtimeMetricName);
		} while (metric == null || metric < latestEpochTimeSent - maxSourceDestinationTimeGap);

		try {
			featureBucketSyncService.syncIfNeeded(latestEpochTimeSent);
			modelBuildingSyncService.buildModelsIfNeeded(latestEpochTimeSent);
		} catch (TimeoutException e) {
			logger.error(e.getMessage());
		}
	}

	private String getSessionId() {
		long currentTimeSeconds = TimestampUtils.convertToSeconds(System.currentTimeMillis());
		return String.format("%s_%s_%d", getClass().getSimpleName(), dataSource, currentTimeSeconds);
	}
}
