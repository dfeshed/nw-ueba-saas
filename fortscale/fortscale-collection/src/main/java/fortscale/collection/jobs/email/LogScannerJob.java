package fortscale.collection.jobs.email;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.EmailService;
import fortscale.utils.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;

/**
 * Created by Amir Keren on 14/03/2016.
 *
 * This task runs in 15 minute intervals to scan various logs and notify subscribers by email for errors
 *
 */
public class LogScannerJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(LogScannerJob.class);

	private static final String LOG_SUBSCRIBERS_KEY = "system.logemail.subscribers";
	private static final String DELIMITER = ",";
	private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";
	private static final int RUN_FREQUENCY_IN_MINUTES = 15;

	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;
	@Autowired
	private EmailService emailService;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {}

	@Override
	protected void runSteps() throws Exception {
		logger.info("Running log scanner job");
		DateTime to = new DateTime();
		DateTime from = to.minusMinutes(RUN_FREQUENCY_IN_MINUTES);
		if (emailService.isEmailConfigured()) {
			ApplicationConfiguration applicationConfiguration = applicationConfigurationService.
					getApplicationConfigurationByKey(LOG_SUBSCRIBERS_KEY);
			if (applicationConfiguration != null) {
				DateTimeFormatter dtf = DateTimeFormat.forPattern(DATE_FORMAT);
				String subscribers = applicationConfiguration.getValue();
				String subject = MessageFormat.format("Error Log Summary {0}, {1}", dtf.print(from), dtf.print(to));
				String logSummary = getLogSummary();
				emailService.sendEmail(subscribers.split(DELIMITER), null, null, subject, logSummary, null, true);
			}
		}
		finishStep();
	}

	private String getLogSummary() {
		//TODO - implement
		return null;
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}