package fortscale.collection.jobs;

import fortscale.aggregation.feature.event.AggregationEventSender;
import fortscale.aggregation.feature.event.IAggregationEventSender;
import fortscale.aggregation.feature.event.batch.AggrFeatureEventBatchService;
import fortscale.utils.logging.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tomerd on 31/12/2015.
 */
public class BuildAggregatedEventsJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(BuildAggregatedEventsJob.class);

	private static final String ENTITY_EVENTS_START_TIME_FIELD = "start_time_unix";
	private static final String ENTITY_EVENTS_END_TIME_FIELD = "end_time_unix";
	private static final int DEFAULT_BATCH_SIZE = 1000;
	private final int DEFAULT_CHECK_RETRIES = 60;
	protected static final int MILLISECONDS_TO_WAIT = 1000 * 60;

	@Autowired
	private AggrFeatureEventBatchService aggrFeatureEventBatchService;

	long batchStartTime;
	long batchEndTime;
	int batchSize;
	int checkRetries;
	String jobToMonitor;
	String jobClassToMonitor;

	@Override protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Start Initializing BuildAggregatedEvents job");

		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		batchStartTime = jobDataMapExtension.getJobDataMapLongValue(map, ENTITY_EVENTS_START_TIME_FIELD);
		batchEndTime = jobDataMapExtension.getJobDataMapLongValue(map, ENTITY_EVENTS_END_TIME_FIELD);
		batchSize = jobDataMapExtension.getJobDataMapIntValue(map, "batchSize", DEFAULT_BATCH_SIZE);
		jobToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, "jobmonitor");
		jobClassToMonitor = jobDataMapExtension.getJobDataMapStringValue(map, "classmonitor");
		checkRetries = jobDataMapExtension.getJobDataMapIntValue(map, "retries", DEFAULT_CHECK_RETRIES);
		logger.info("Finish initializing BuildAggregatedEvents job");
	}

	@Override protected int getTotalNumOfSteps() {
		return 1;
	}

	@Override protected boolean shouldReportDataReceived() {
		return false;
	}

	@Override protected void runSteps() throws Exception {
		logger.info("Running build aggregated events job");

		// Create event sender
		AggregationEventSender eventSender = new AggregationEventSender(batchSize, jobClassToMonitor, jobToMonitor,
				MILLISECONDS_TO_WAIT, checkRetries);

		// Run the aggregation event builder service
		aggrFeatureEventBatchService.buildAndSave(eventSender, batchStartTime, batchEndTime);

		// Delete events after sending
		aggrFeatureEventBatchService.deleteEvents(batchStartTime, batchEndTime);

		logger.info("Finish running build aggregated events job");
	}
}
