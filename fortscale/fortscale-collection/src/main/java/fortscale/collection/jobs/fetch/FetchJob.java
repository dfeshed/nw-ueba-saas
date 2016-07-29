package fortscale.collection.jobs.fetch;

import fortscale.collection.JobDataMapExtension;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.fetch.FetchConfiguration;
import fortscale.domain.fetch.FetchConfigurationRepository;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.spring.SpringPropertiesUtil;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Amir Keren on 4/4/16.
 */
@DisallowConcurrentExecution
public abstract class FetchJob {

	protected static Logger logger = LoggerFactory.getLogger(FetchJob.class);

	@Autowired
	protected FetchConfigurationRepository fetchConfigurationRepository;

	@Autowired
	protected ApplicationConfigurationService applicationConfigurationService;

	@Value("${collection.fetch.data.path}")
	protected String outputPath;

	@Value("${default.siem.type:splunk}")
	private String defaultType;
	@Value("${default.siem.host:integ-splunk-07}")
	private String defaultHost;
	@Value("${default.siem.port:8089}")
	private String defaultPort;
	@Value("${default.siem.username:admin}")
	private String defaultUsername;
	@Value("${default.siem.password:iYTLjyA0VryKhpkvBrMMLQ==}")
	private String defaultPassword;

	private static final String SIEM_CONFIG_PREFIX = "system.siem";
	private static final String SIEM_TYPE_KEY = SIEM_CONFIG_PREFIX + ".type";
	private static final String SIEM_HOST_KEY = SIEM_CONFIG_PREFIX + ".host";
	private static final String SIEM_PORT_KEY = SIEM_CONFIG_PREFIX + ".port";
	private static final String SIEM_USER_KEY = SIEM_CONFIG_PREFIX + ".user";
	private static final String SIEM_PASSWORD_KEY = SIEM_CONFIG_PREFIX + ".password";

	// time limits sends to repository (can be epoch/dates/constant as -1h@h) - in the case of manual run,
	// this parameters will be used
	protected String earliest;
	protected String latest;
	protected String savedQuery;
	protected String returnKeys;
	protected String sortShellScript;
	protected String delimiter;
	// time limits as dates to allow easy paging - will be used in continues run
	protected Date earliestDate;
	protected Date latestDate;
	protected File outputDir;
	protected boolean encloseQuotes = true;

	//the type (data source) to bring saved configuration for.
	private String type;
	private String filenameFormat;
	//time interval to bring in one fetch (uses for both regular single fetch, and paging in the case of miss fetch).
	//for manual fetch with time frame given as a parameter will keep the -1 default and the time frame won't be paged.
	private int fetchIntervalInSeconds = -1;
	//indicate if still have more pages to go over and fetch
	private boolean keepFetching = false;
	private int fetchDiffInSeconds;
	private int ceilingTimePartInt;
	private String filename;
	private String tempfilename;
	// get common data from configuration
	private String hostName;
	private String port;
	private String username;
	private String password;

	protected abstract boolean connect(String hostName, String port, String username, String password) throws Exception;
	protected abstract void fetch(String filename, String tempfilename) throws Exception;

	protected void finish() throws Exception {}

	protected void getExtraParameters(JobDataMap map, JobDataMapExtension jobDataMapExtension)
			throws JobExecutionException {}

	public void runSteps() throws Exception {
		logger.info("fetch job started");
		// ensure output path exists
		logger.debug("creating output file at {}", outputPath);
		outputDir = ensureOutputDirectoryExists(outputPath);
		// connect to repository
		boolean connected;
		try {
			connected = connect(hostName, port, username, password);
		} catch (Exception ex) {
			logger.error("failed to connect to repository - " + ex);
			return;
		}
		if (!connected) {
			logger.error("failed to connect to repository");
			return;
		}
		do {
			// preparer fetch page params
			if  (fetchIntervalInSeconds != -1 ) {
				preparerFetchPageParams();
			}
			// try to create output file
			createOutputFile(outputDir);
			File outputTempFile = new File(outputDir, tempfilename);
			logger.debug("created output file at {}", outputTempFile.getAbsolutePath());
			try {
				fetch(filename, tempfilename);
			} catch (Exception ex) {
				logger.error("failed to fetch - {}", ex);
			}
			if (sortShellScript != null) {
				// sort the output
				sortOutput();
			} else {
				// rename output file once get from siem finished
				renameOutput();
			}
			attemptToDeleteEmptyFile();
			// update mongo with current fetch progress
			updateMongoWithCurrentFetchProgress();
			//support in smaller batches fetch - to avoid too big fetches - not relevant for manual fetches
		} while (keepFetching);
		finish();
		logger.info("fetch job finished");
	}

	/**
	 *
	 * This method sets the parameters for specific page
	 *
	 */
	private void preparerFetchPageParams() {
		earliest = String.valueOf(TimestampUtils.convertToSeconds(earliestDate.getTime()));
		Date pageLatestDate = DateUtils.addSeconds(earliestDate, fetchIntervalInSeconds);
		pageLatestDate = pageLatestDate.before(latestDate) ? pageLatestDate : latestDate;
		latest = String.valueOf(TimestampUtils.convertToSeconds(pageLatestDate.getTime()));
		//set for next page
		earliestDate = pageLatestDate;
	}

	/**
	 *
	 * This helper method creates the output file
	 *
	 * @param outputDir
	 * @throws JobExecutionException
	 */
	private void createOutputFile(File outputDir) throws JobExecutionException {
		// generate filename according to the job name and time
		filename = String.format(filenameFormat, (new Date()).getTime());
		tempfilename = filename + ".part";
		File outputTempFile = new File(outputDir, tempfilename);
		try {
			if (!outputTempFile.createNewFile()) {
				logger.error("cannot create output file {}", outputTempFile);
				throw new JobExecutionException("cannot create output file " + outputTempFile.getAbsolutePath());
			}
		} catch (IOException e) {
			logger.error("error creating file " + outputTempFile.getPath(), e);
			throw new JobExecutionException("cannot create output file " + outputTempFile.getAbsolutePath());
		}
	}

	/**
	 *
	 * This method gets the fetch times from Mongo
	 *
	 * @throws JobExecutionException
	 */
	private void getRunTimeFrameFromMongo() throws JobExecutionException {
		//set fetch until the ceiling of now (according to the given interval
		latestDate = DateUtils.ceiling(new Date(), ceilingTimePartInt);
		//shift the date by the configured diff
		latestDate = DateUtils.addSeconds(latestDate, -1 * fetchDiffInSeconds);
		keepFetching = true;
		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(type);
		if (fetchConfiguration != null) {
			earliest = fetchConfiguration.getLastFetchTime();
			earliestDate = new Date(TimestampUtils.convertToMilliSeconds(Long.parseLong(earliest)));
		}
		else {
			earliestDate = DateUtils.addSeconds(latestDate, -1 * fetchIntervalInSeconds);
		}
	}

	/**
	 *
	 * This method attempts to delete a file if one is empty
	 *
	 */
	private void attemptToDeleteEmptyFile() {
		File outputTempFile = new File(outputDir, tempfilename);
		if (outputTempFile.length() == 0) {
			logger.info("deleting empty output file {}", outputTempFile.getName());
			if (!outputTempFile.delete()) {
				logger.warn("cannot delete empty file {}", outputTempFile.getName());
			}
		}
	}

	/**
	 *
	 * This method sorts the output file
	 *
	 * @throws InterruptedException
	 */
	private void sortOutput() throws InterruptedException {
		File outputTempFile = new File(outputDir, tempfilename);
		if (outputTempFile.length() > 0) {
			File outputFile = new File(outputDir, filename);
			Process pr = runCmd(null, sortShellScript, outputTempFile.getAbsolutePath(), outputFile.getAbsolutePath());
			if (pr == null) {
				logger.error("Failed to sort output of file {} using {}", outputTempFile.getAbsolutePath(),
						sortShellScript);
			} else if (pr.waitFor() != 0) { // wait for process to finish
				// error (return code is different than 0)
				logger.error("Failed to run cmd");
			}
			outputTempFile.delete();
		}
	}

	private Process runCmd(File workingDir, String... commands){
		ProcessBuilder processBuilder;
		Process pr;
		try {
			processBuilder = new ProcessBuilder(commands);
			if (workingDir != null) {
				processBuilder.directory(workingDir);
			}
			pr = processBuilder.start();
		} catch (Exception e) {
			String cmd = StringUtils.join(commands, " ");
			logger.error(String.format("while running the command \"%s\", got the following exception", cmd), e);
			return null;
		}
		return pr;
	}

	private File ensureOutputDirectoryExists(String outputPath) throws JobExecutionException {
		File outputDir = new File(outputPath);
		try {
			if (!outputDir.exists()) {
				// try to create output directory
				outputDir.mkdirs();
			}
			return outputDir;
		} catch (SecurityException e) {
			logger.error("cannot create output path - " + outputPath, e);
			// stop execution, notify scheduler not to re-fire immediately
			throw new JobExecutionException(e,  false);
		}
	}

	/**
	 *
	 * This method renames the output file when process is finished
	 *
	 */
	private void renameOutput() {
		File outputTempFile = new File(outputDir, tempfilename);
		if (outputTempFile.length() > 0) {
			File outputFile = new File(outputDir, filename);
			if (!outputTempFile.renameTo(outputFile)) {
				logger.warn("cannot rename file {}", outputTempFile.getName());
			}
		}
	}

	/**
	 *
	 * This method updates Mongo with the latest time fetched
	 *
	 */
	private void updateMongoWithCurrentFetchProgress() {
		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(type);
		latest = TimestampUtils.convertSplunkTimeToUnix(latest);
		if (fetchConfiguration == null) {
			fetchConfiguration = new FetchConfiguration(type, latest);
		} else {
			fetchConfiguration.setLastFetchTime(latest);
		}
		try {
			fetchConfigurationRepository.save(fetchConfiguration);
		} catch (OptimisticLockingFailureException ex) {
			logger.warn("failed to save fetch configuration - {}", ex);
		}
		if (earliestDate != null && latestDate != null) {
			if (earliestDate.after(latestDate) || earliestDate.equals(latestDate)) {
				keepFetching = false;
			}
		}
	}

	/**
	 *
	 * This method gets the specific job parameters
	 *
	 * @param map
	 * @paran jobDataMapExtension
	 * @param configuredSIEM
	 * @throws JobExecutionException
	 */
	public void getJobParameters(JobDataMap map, JobDataMapExtension jobDataMapExtension, String configuredSIEM)
			throws JobExecutionException {
		Map<String, String> configuration = applicationConfigurationService.
				getApplicationConfigurationByNamespace(SIEM_CONFIG_PREFIX);
		if (configuration != null && !configuration.isEmpty()) {
			hostName = configuration.get(SIEM_HOST_KEY);
			port = configuration.get(SIEM_PORT_KEY);
			username = configuration.get(SIEM_USER_KEY);
			password = configuration.get(SIEM_PASSWORD_KEY);
		} else {
			//initialize with default test values
			logger.warn("SIEM configuration not found, reverting to default test values");
			hostName = defaultHost;
			port = defaultPort;
			username = defaultUsername;
			password = defaultPassword;
			Map<String, String> defaultValues = new HashMap();
			defaultValues.put(SIEM_HOST_KEY, hostName);
			defaultValues.put(SIEM_PORT_KEY, port);
			defaultValues.put(SIEM_USER_KEY, username);
			defaultValues.put(SIEM_PASSWORD_KEY, password);
			defaultValues.put(SIEM_TYPE_KEY, defaultType);
			applicationConfigurationService.insertConfigItems(defaultValues);
		}
		// If exists, get the output path from the job data map
		if (jobDataMapExtension.isJobDataMapContainKey(map, "path")) {
			outputPath = jobDataMapExtension.getJobDataMapStringValue(map, "path");
		}
		// get parameters values from the job data map
		if (jobDataMapExtension.isJobDataMapContainKey(map, "earliest") &&
				jobDataMapExtension.isJobDataMapContainKey(map, "latest") &&
				jobDataMapExtension.isJobDataMapContainKey(map, "type")) {
			earliest = jobDataMapExtension.getJobDataMapStringValue(map, "earliest");
			latest = jobDataMapExtension.getJobDataMapStringValue(map, "latest");
			type = jobDataMapExtension.getJobDataMapStringValue(map, "type");
		} else {
			//calculate query run times from mongo in the case not provided as job params
			logger.info("No Time frame was specified as input param, continuing from the previous run ");
			type = jobDataMapExtension.getJobDataMapStringValue(map, "type");
			//time back (default 1 hour)
			fetchIntervalInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchIntervalInSeconds", 3600);
			ceilingTimePartInt = jobDataMapExtension.getJobDataMapIntValue(map, "ceilingTimePartInt", Calendar.HOUR);
			fetchDiffInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchDiffInSeconds", 0);
			getRunTimeFrameFromMongo();
		}
		savedQuery = jobDataMapExtension.getJobDataMapStringValue(map, "savedQuery");
		if (savedQuery.startsWith("{") && savedQuery.endsWith("}")) {
			savedQuery = SpringPropertiesUtil.getProperty(configuredSIEM.toLowerCase() + "." +
					savedQuery.substring(1, savedQuery.length() - 1) + ".savedQuery");
		}
		returnKeys = jobDataMapExtension.getJobDataMapStringValue(map, "returnKeys");
		if (returnKeys.startsWith("{") && returnKeys.endsWith("}")) {
			returnKeys = SpringPropertiesUtil.getProperty(configuredSIEM.toLowerCase() + "." +
					returnKeys.substring(1, returnKeys.length() - 1) + ".returnKeys");
		}
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		// try and retrieve the delimiter value, if present in the job data map
		delimiter = jobDataMapExtension.getJobDataMapStringValue(map, "delimiter", ",");
		// try and retrieve the enclose quotes value, if present in the job data map
		encloseQuotes = jobDataMapExtension.getJobDataMapBooleanValue(map, "encloseQuotes", true);
		getExtraParameters(map, jobDataMapExtension);
	}

}