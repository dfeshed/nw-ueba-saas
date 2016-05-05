package fortscale.collection.jobs.fetch;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.jobs.fetch.siem.QRadar;
import fortscale.collection.jobs.fetch.siem.Splunk;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.fetch.FetchConfiguration;
import fortscale.domain.fetch.FetchConfigurationRepository;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.impl.SpringService;
import fortscale.utils.spring.SpringPropertiesUtil;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.time.DateUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Amir Keren on 4/4/16.
 */
@DisallowConcurrentExecution
public class FetchJob extends FortscaleJob {

	protected static Logger logger = LoggerFactory.getLogger(FetchJob.class);

	private FetchJob fetchJob;

	@Autowired
	protected FetchConfigurationRepository fetchConfigurationRepository;

	@Autowired
	protected ApplicationConfigurationService applicationConfigurationService;

	@Value("${collection.fetch.data.path}")
	protected String outputPath;

	@Value("${fortscale.collection.siem}")
	protected String configuredSIEM;

	// time limits sends to repository (can be epoch/dates/constant as -1h@h) - in the case of manual run,
	// this parameters will be used
	protected String earliest;
	protected String latest;
	protected String savedQuery;
	protected String returnKeys;
	protected String sortShellScript;
	protected String filenameFormat;
	protected String delimiter;
	//the type (data source) to bring saved configuration for.
	protected String type;
	// time limits as dates to allow easy paging - will be used in continues run
	protected Date earliestDate;
	protected Date latestDate;
	protected File outputDir;
	//time interval to bring in one fetch (uses for both regular single fetch, and paging in the case of miss fetch).
	//for manual fetch with time frame given as a parameter will keep the -1 default and the time frame won't be paged.
	protected int fetchIntervalInSeconds = -1;
	protected boolean encloseQuotes = true;
	//indicate if still have more pages to go over and fetch
	protected boolean keepFetching = false;
	protected File outputTempFile;
	protected File outputFile;

	protected boolean connect() throws Exception { return true; }
	protected void fetch() throws Exception { return; }

	protected void finish() throws Exception {}

	protected void getExtraJobParameters(JobDataMap map) throws JobExecutionException {}

	@Override
	protected void runSteps() throws Exception {
		logger.info("fetch job started");
		// ensure output path exists
		logger.debug("creating output file at {}", outputPath);
		monitor.startStep(getMonitorId(), "Prepare sink file", 1);
		outputDir = ensureOutputDirectoryExists(outputPath);
		// connect to repository
		monitor.startStep(getMonitorId(), "Connect to repository", 2);
		boolean connected = fetchJob.connect();
		if (!connected) {
			logger.error("failed to connect to repository");
			return;
		}
		monitor.startStep(getMonitorId(), "Query repository", 3);
		do {
			// preparer fetch page params
			if  (fetchIntervalInSeconds != -1 ) {
				preparerFetchPageParams();
			}
			// try to create output file
			createOutputFile(outputDir);
			logger.debug("created output file at {}", outputTempFile.getAbsolutePath());
			monitor.finishStep(getMonitorId(), "Prepare sink file");
			fetchJob.fetch();
			// report to monitor the file size
			monitor.addDataReceived(getMonitorId(), getJobDataReceived(outputTempFile));
			if (sortShellScript != null) {
				// sort the output
				monitor.startStep(getMonitorId(), "Sort Output", 4);
				sortOutput();
				monitor.finishStep(getMonitorId(), "Sort Output");
			} else {
				// rename output file once get from splunk finished
				monitor.startStep(getMonitorId(), "Rename Output", 4);
				renameOutput();
				monitor.finishStep(getMonitorId(), "Rename Output");
			}
			// update mongo with current fetch progress
			updateMongoWithCurrentFetchProgress();
			//support in smaller batches fetch - to avoid too big fetches - not relevant for manual fetches
		} while(keepFetching);
		fetchJob.finish();
		logger.info("fetch job finished");
	}

	/**
	 *
	 * This reads configuration from the service
	 *
	 * @param key
	 * @return
	 */
	protected String readFromConfigurationService(String key) {
		ApplicationConfiguration applicationConfiguration = applicationConfigurationService.
				getApplicationConfigurationByKey(key);
		if (applicationConfiguration != null) {
			return applicationConfiguration.getValue();
		}
		return null;
	}

	/**
	 *
	 * This method sets the parameters for specific page
	 *
	 */
	protected void preparerFetchPageParams() {
		earliest = String.valueOf(TimestampUtils.convertToSeconds(earliestDate.getTime()));
		Date pageLatestDate = DateUtils.addSeconds(earliestDate, fetchIntervalInSeconds);
		pageLatestDate = pageLatestDate.before(latestDate) ? pageLatestDate : latestDate;
		latest = String.valueOf(TimestampUtils.convertToSeconds(pageLatestDate.getTime()));
		//set for next page
		earliestDate = pageLatestDate;
	}

	/**
	 *
	 * This method checks the number of events received
	 *
	 * @param output
	 * @return
	 */
	protected JobDataReceived getJobDataReceived(File output) {
		if (output.length() < 1024) {
			return new JobDataReceived("Events", new Integer((int)output.length()), "Bytes");
		} else {
			int sizeInKB = (int) (output.length() / 1024);
			return new JobDataReceived("Events", new Integer(sizeInKB), "KB");
		}
	}

	/**
	 *
	 * This helper method creates the output file
	 *
	 * @param outputDir
	 * @throws JobExecutionException
	 */
	protected void createOutputFile(File outputDir) throws JobExecutionException {
		// generate filename according to the job name and time
		String filename = String.format(filenameFormat, (new Date()).getTime());
		outputTempFile = new File(outputDir, filename + ".part");
		outputFile = new File(outputDir, filename);
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
	 * @param map
	 * @throws JobExecutionException
	 */
	protected void getRunTimeFrameFromMongo(JobDataMap map) throws JobExecutionException {
		type = jobDataMapExtension.getJobDataMapStringValue(map, "type");
		//time back (default 1 hour)
		fetchIntervalInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchIntervalInSeconds", 3600);
		int ceilingTimePartInt = jobDataMapExtension.getJobDataMapIntValue(map, "ceilingTimePartInt", Calendar.HOUR);
		int fetchDiffInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchDiffInSeconds", 0);
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
	 * This method sorts the output file
	 *
	 * @throws InterruptedException
	 */
	protected void sortOutput() throws InterruptedException {
		if (outputTempFile.length()==0) {
			logger.info("deleting empty output file {}", outputTempFile.getName());
			if (!outputTempFile.delete())
				logger.warn("cannot delete empty file {}", outputTempFile.getName());
		} else {
			Process pr =  runCmd(null, sortShellScript, outputTempFile.getAbsolutePath(), outputFile.getAbsolutePath());
			if(pr == null){
				logger.error("Failed to sort output of file {} using {}", outputTempFile.getAbsolutePath(),
						sortShellScript);
				addError(String.format("got the following error while running the shell command %s.",sortShellScript));
			} else if(pr.waitFor() != 0){ // wait for process to finish
				// error (return code is different than 0)
				handleCmdFailure(pr, sortShellScript);
			}
			outputTempFile.delete();
		}
	}

	/**
	 *
	 * This method handles the exceptions that occur during the fetch process
	 *
	 * @param monitorId
	 * @param e
	 * @throws JobExecutionException
	 */
	protected void handleExecutionException(String monitorId, Exception e) throws JobExecutionException {
		if (e instanceof JobExecutionException)
			throw (JobExecutionException)e;
		else {
			logger.error("unexpected error during repository fetch " + e.toString());
			throw new JobExecutionException(e);
		}
	}

	/**
	 *
	 * This method renames the output file when process is finished
	 *
	 */
	protected void renameOutput() {
		if (outputTempFile.length()==0) {
			logger.info("deleting empty output file {}", outputTempFile.getName());
			if (!outputTempFile.delete())
				logger.warn("cannot delete empty file {}", outputTempFile.getName());
		} else {
			outputTempFile.renameTo(outputFile);
		}
	}

	/**
	 *
	 * This method updates Mongo with the latest time fetched
	 *
	 */
	protected void updateMongoWithCurrentFetchProgress() {
		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(type);
		latest = TimestampUtils.convertSplunkTimeToUnix(latest);
		if (fetchConfiguration == null) {
			fetchConfiguration = new FetchConfiguration(type, latest);
		} else {
			fetchConfiguration.setLastFetchTime(latest);
		}
		fetchConfigurationRepository.save(fetchConfiguration);
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
	 * @param context
	 * @throws JobExecutionException
	 */
	@Override
	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();
		switch (configuredSIEM) {
			case Splunk.SIEM_NAME: fetchJob = SpringService.getInstance().resolve(Splunk.class);
			case QRadar.SIEM_NAME: fetchJob = SpringService.getInstance().resolve(QRadar.class);
			default: fetchJob = null;
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
			getRunTimeFrameFromMongo(map);
		}
		savedQuery = jobDataMapExtension.getJobDataMapStringValue(map, "savedQuery");
		if (savedQuery.startsWith("{") && savedQuery.endsWith("}")) {
			savedQuery = SpringPropertiesUtil.getProperty(configuredSIEM.toLowerCase() + ".savedQuery." +
					savedQuery.substring(1, savedQuery.length() - 1));
		}
		returnKeys = jobDataMapExtension.getJobDataMapStringValue(map, "returnKeys");
		if (returnKeys.startsWith("{") && returnKeys.endsWith("}")) {
			returnKeys = SpringPropertiesUtil.getProperty(configuredSIEM.toLowerCase() + ".returnKeys." +
					returnKeys.substring(1, returnKeys.length() - 1));
		}
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		// try and retrieve the delimiter value, if present in the job data map
		delimiter = jobDataMapExtension.getJobDataMapStringValue(map, "delimiter", ",");
		// try and retrieve the enclose quotes value, if present in the job data map
		encloseQuotes = jobDataMapExtension.getJobDataMapBooleanValue(map, "encloseQuotes", true);
		fetchJob.getExtraJobParameters(map);
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 4;
	}

	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}

}