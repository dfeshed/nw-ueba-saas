package fortscale.collection.jobs.email;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.email.Frequency;
import fortscale.services.AlertEmailService;
import fortscale.services.ForwardingService;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Amir Keren on 26/07/2015.
 *
 * This task runs in batches in a constant interval, collects all of the notifications from Mongo that were created
 * since its last run and converts them into Evidence objects. Finally, it pushes the Evidence objects to the proper
 * Kafka topic to be streamed into the system.
 *
 */
public class AlertSummaryJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(AlertSummaryJob.class);

	@Autowired
	private ForwardingService forwardingService;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {}

	@Override
	protected void runSteps() throws Exception {
		logger.info("Running email alert summary job");
		DateTime date = new DateTime().withZone(DateTimeZone.UTC);
		//daily
		forwardingService.forwardAlertSummary(Frequency.Daily);
		//if monday - weekly
		if (date.getDayOfWeek() == 1) {
			forwardingService.forwardAlertSummary(Frequency.Weekly);
		}
		//if 1st - monthly
		if (date.getDayOfMonth() == 1) {
			forwardingService.forwardAlertSummary(Frequency.Monthly);
		}
		finishStep();
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}