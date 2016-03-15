package fortscale.collection.jobs.email;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.EmailService;
import fortscale.utils.logging.Logger;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Amir Keren on 14/03/2016.
 *
 * This task runs in 15 minute intervals to scan various logs and notify subscribers by email for errors
 *
 */
public class LogScannerJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(LogScannerJob.class);

	private enum Level { ERROR, WARN, INFO, DEBUG }

	private static final String LOG_SUBSCRIBERS_KEY = "system.logemail.subscribers";
	private static final String DELIMITER = ",";
	private static final String DATE_REGEX = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
	private static final String LOG_LEVEL_REGEX = "(\\[.+\\]) (\\S+)";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String COLLECTION_LOG_FILE = "logFile.log";
	private static final int RUN_FREQUENCY_IN_MINUTES = 15;

	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;
	@Autowired
	private EmailService emailService;

	private DateTimeFormatter dtf;
	private DateTime from;
	private Level logLevel;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		dtf = DateTimeFormat.forPattern(DATE_FORMAT);
		from = new DateTime().minusMinutes(RUN_FREQUENCY_IN_MINUTES);
		logLevel = Level.INFO;
	}

	@Override
	protected void runSteps() throws Exception {
		logger.info("Running log scanner job");
		String result = getLogSummary(COLLECTION_LOG_FILE);
		System.out.println(result);
		if (emailService.isEmailConfigured()) {
			ApplicationConfiguration applicationConfiguration = applicationConfigurationService.
					getApplicationConfigurationByKey(LOG_SUBSCRIBERS_KEY);
			if (applicationConfiguration != null) {
				String subscribers = applicationConfiguration.getValue();
				String subject = MessageFormat.format("Error Log Summary {0}, {1}", dtf.print(from),
						dtf.print(from.plusMinutes(RUN_FREQUENCY_IN_MINUTES)));
				String logSummary = getLogSummary(COLLECTION_LOG_FILE);
				emailService.sendEmail(subscribers.split(DELIMITER), null, null, subject, logSummary, null, true);
			}
		}
		finishStep();
	}

	private String getLogSummary(String logFile) throws IOException {
		Pattern timePattern = Pattern.compile(DATE_REGEX);
		Pattern levelPattern = Pattern.compile(LOG_LEVEL_REGEX);
		ReversedLinesFileReader fr = new ReversedLinesFileReader(new File(logFile));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = fr.readLine()) != null) {
			Matcher matcher = timePattern.matcher(line);
			if (matcher.find()) {
				if (dtf.parseDateTime(matcher.group(0)).isAfter(from)) {
					matcher = levelPattern.matcher(line);
					if (matcher.find() && logLevel.ordinal() >= Level.valueOf(matcher.group(2)).ordinal()) {
						sb.insert(0, line + "\n");
					}
				}
			} else {
				sb.insert(0, line + "\n");
			}
		}
		fr.close();
		return sb.toString();
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}