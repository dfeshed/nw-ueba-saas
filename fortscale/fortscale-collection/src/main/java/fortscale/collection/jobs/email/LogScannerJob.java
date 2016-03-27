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
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Amir Keren on 25/03/2016.
 *
 * This task runs in 60 minute intervals to scan various logs and notify subscribers by email for errors
 *
 */
public class LogScannerJob extends FortscaleJob {

	private static Logger logger = Logger.getLogger(LogScannerJob.class);

	private enum Level { ERROR, WARN, INFO, DEBUG }

	private static final String LOG_SUBSCRIBERS_KEY = "system.logemail.subscribers";
	private static final String DELIMITER = ",";
	private static final String DATE_REGEX = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
	private static final String LOG_LEVEL_REGEX = "(ERROR|WARN|INFO|DEBUG)";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String LOG_SUFFIX = ".log";
	private static final String EMAIL_DATE_FORMAT = "dd/MM/yy HH:mm";
	private static final String EMAIL_SUBJECT = "Fortscale Error Log Summary";
	private static final String[] IGNORE_LIST = new String[] { "gc.log" };
	private static final int RUN_FREQUENCY_IN_MINUTES = 60;

	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;
	@Autowired
	private EmailService emailService;

	private DateTimeFormatter dtf;
	private DateTime from;
	private Level logLevel;
	private String[] logs;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		dtf = DateTimeFormat.forPattern(DATE_FORMAT);
		from = new DateTime().minusMinutes(RUN_FREQUENCY_IN_MINUTES);
		logLevel = Level.valueOf(jobDataMapExtension.getJobDataMapStringValue(map, "logLevel"));
		logs = jobDataMapExtension.getJobDataMapStringValue(map, "logs").split(",");
	}

	@Override
	protected void runSteps() throws Exception {
		logger.info("Running log scanner job");
		if (emailService.isEmailConfigured()) {
			ApplicationConfiguration applicationConfiguration = applicationConfigurationService.
					getApplicationConfigurationByKey(LOG_SUBSCRIBERS_KEY);
			if (applicationConfiguration != null) {
				String logSummary = getLogsSummary();
				if (!logSummary.isEmpty()) {
					String subscribers = applicationConfiguration.getValue();
					logger.info("found errors in logs, notifying subscribers {} via email", subscribers);
					DateTimeFormatter dtf = DateTimeFormat.forPattern(EMAIL_DATE_FORMAT);
					DateTimeFormatter dtfHour = DateTimeFormat.forPattern(EMAIL_DATE_FORMAT.split(" ")[1]);
					String subject = MessageFormat.format("{0} {1} - {2}", EMAIL_SUBJECT, dtf.print(from),
							dtfHour.print(from.plusMinutes(RUN_FREQUENCY_IN_MINUTES)));
					emailService.sendEmail(subscribers.split(DELIMITER), null, null, subject, logSummary, null, true);
				} else {
					logger.info("no errors found");
				}
			}
		}
		finishStep();
	}

	/**
	 *
	 * This methos scans the various log fils and extracts messages in the proper log level
	 *
	 * @return
	 * @throws IOException
	 */
	private String getLogsSummary() throws IOException, JobExecutionException {
		Pattern timePattern = Pattern.compile(DATE_REGEX);
		Pattern levelPattern = Pattern.compile(LOG_LEVEL_REGEX);
		StringBuilder result = new StringBuilder();
		for (String logFile: logs) {
			//check if logfile exists and handle file or folder
			File tempFile = new File(logFile);
			if (!tempFile.exists()) {
				logger.error("File {} not found", logFile);
				throw new JobExecutionException();
			}
			File[] files;
			if (tempFile.isDirectory()) {
				files = tempFile.listFiles((dir, name) -> name.endsWith(LOG_SUFFIX) && !shouldIgnore(name));
			} else {
				files = new File[] { tempFile };
			}
			StringBuilder sb = new StringBuilder();
			for (File file: files) {
				ReversedLinesFileReader fr = new ReversedLinesFileReader(file);
				String line;
				while ((line = fr.readLine()) != null) {
					Matcher matcher = timePattern.matcher(line);
					if (matcher.find()) {
						if (dtf.parseDateTime(matcher.group(0)).isAfter(from)) {
							matcher = levelPattern.matcher(line);
							if (matcher.find() && logLevel.ordinal() >= Level.valueOf(matcher.group(0)).ordinal()) {
								sb.insert(0, line + "<br/>");
							}
						//time is too old
						} else {
							break;
						}
					} else {
						//exception of some sort
						sb.insert(0, line + "<br/>");
					}
				}
				fr.close();
				if (!sb.toString().isEmpty()) {
					result.append(file.getName()).append(":<br/>").append(sb.toString());
				}
			}
		}
		return result.toString();
	}

	/**
	 *
	 * This method checks if we should ignore this file
	 *
	 * @param name
	 * @return
	 */
	private boolean shouldIgnore(String name) {
		for (String toIgnore: IGNORE_LIST) {
			if (toIgnore.equals(name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected int getTotalNumOfSteps() { return 1; }

	@Override
	protected boolean shouldReportDataReceived() { return false; }

}