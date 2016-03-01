package fortscale.collection.jobs.syslog;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.email.Frequency;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.ForwardingService;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Tomer Dvir on 21/02/2016.
 * This task reads all the newly created alerts from Mongo, and forward the alerts to syslog
 */
public class AlertSyslogForwarding extends FortscaleJob {

	private static Logger logger = Logger.getLogger(AlertSyslogForwarding.class);

	private static final String WEEKLY_FREQUENCY_KEY = "system.alertsSyslogForwarding.weekly";
	private static final String MONTHLY_FREQUENCY_KEY = "system.alertsSyslogForwarding.monthly";
	
	@Autowired private ForwardingService forwardingService;
	@Autowired private ApplicationConfigurationService applicationConfigurationService;

	@Override protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
	}

	@Override protected void runSteps() throws Exception {
		logger.info("Running alerts syslog forwarding job");
		DateTime date = new DateTime();
		ApplicationConfiguration applicationConfiguration;
		int weeklyFrequencyDate = 1, monthlyFrequencyDate = 1;
		//daily
		// forwardingService.forwardLatestAlerts(Frequency.Daily);
		try {
			applicationConfiguration = applicationConfigurationService.
					getApplicationConfigurationByKey(WEEKLY_FREQUENCY_KEY);
			if (applicationConfiguration != null) {
				weeklyFrequencyDate = Integer.parseInt(applicationConfiguration.getValue());
			}
			applicationConfiguration = applicationConfigurationService.
					getApplicationConfigurationByKey(MONTHLY_FREQUENCY_KEY);
			if (applicationConfiguration != null) {
				monthlyFrequencyDate = Integer.parseInt(applicationConfiguration.getValue());
			}
		} catch (Exception ex) {
			logger.error("failed to read frequency configuration from db, reverting to default - {}", ex);
		}
		//if monday - weekly
		if (date.getDayOfWeek() == weeklyFrequencyDate) {
			forwardingService.forwardLatestAlerts(Frequency.Weekly);
		}
		//if 1st - monthly
		if (date.getDayOfMonth() == monthlyFrequencyDate) {
			forwardingService.forwardLatestAlerts(Frequency.Monthly);
		}
		finishStep();
	}

	@Override protected int getTotalNumOfSteps() {
		return 1;
	}

	@Override protected boolean shouldReportDataReceived() {
		return false;
	}

}