package fortscale.collection.jobs.smart;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.entity.event.EntityEventService;
import fortscale.utils.time.TimestampUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Date;

public class EntityEventsCreationJob extends FortscaleJob {
	private static final String START_TIME_IN_SECONDS_ARG = "startTimeInSeconds";
	private static final String END_TIME_IN_SECONDS_ARG = "endTimeInSeconds";
	private static final String TIME_INTERVAL_IN_SECONDS_ARG = "timeIntervalInSeconds";
	private static final String BATCH_SIZE_ARG = "batchSize";
	private static final String CHECK_RETRIES_ARG = "checkRetries";

	@Autowired
	private EntityEventService entityEventService;

	private long startTimeInSeconds;
	private long endTimeInSeconds;
	private long timeIntervalInSeconds;
	private KafkaThrottlerEntityEventSender sender;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();

		startTimeInSeconds = jobDataMapExtension.getJobDataMapLongValue(jobDataMap, START_TIME_IN_SECONDS_ARG);
		endTimeInSeconds = jobDataMapExtension.getJobDataMapLongValue(jobDataMap, END_TIME_IN_SECONDS_ARG);
		timeIntervalInSeconds = jobDataMapExtension.getJobDataMapLongValue(jobDataMap, TIME_INTERVAL_IN_SECONDS_ARG);
		Assert.isTrue(startTimeInSeconds >= 0);
		Assert.isTrue(endTimeInSeconds >= startTimeInSeconds);
		Assert.isTrue(timeIntervalInSeconds > 0);

		int batchSize = jobDataMapExtension.getJobDataMapIntValue(jobDataMap, BATCH_SIZE_ARG);
		int checkRetries = jobDataMapExtension.getJobDataMapIntValue(jobDataMap, CHECK_RETRIES_ARG);
		sender = new KafkaThrottlerEntityEventSender(batchSize, checkRetries);
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
		startNewStep("Create and send entity events to Kafka topic");
		long currentTimeInSeconds = startTimeInSeconds;
		Date currentStartTime;
		Date currentEndTime;

		while (currentTimeInSeconds <= endTimeInSeconds) {
			currentStartTime = new Date(TimestampUtils.convertToMilliSeconds(currentTimeInSeconds));
			currentTimeInSeconds += timeIntervalInSeconds;

			if (currentTimeInSeconds > endTimeInSeconds) {
				currentEndTime = new Date(TimestampUtils.convertToMilliSeconds(endTimeInSeconds));
			} else {
				currentEndTime = new Date(TimestampUtils.convertToMilliSeconds(currentTimeInSeconds));
			}

			entityEventService.sendEntityEventsInTimeRange(currentStartTime, currentEndTime,
					System.currentTimeMillis(), sender, false);
		}

		finishStep();
	}
}
