package fortscale.collection.jobs;

import fortscale.domain.fetch.FetchConfiguration;
import fortscale.domain.fetch.FetchConfigurationRepository;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.splunk.SplunkApi;
import fortscale.utils.splunk.SplunkEventsHandlerLogger;
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
import java.util.Properties;


/**
 * Scheduler job to fetch data from splunk and write it to a local csv file
 * In the case the job doesn't get time frame as job params, will continue the fetch process of the data source from the last saved time
 */
@DisallowConcurrentExecution
public class SplunkFetchSavedQueryJob extends FortscaleJob {

	private static Logger logger = LoggerFactory.getLogger(SplunkFetchSavedQueryJob.class);

	// get common data from configuration
	@Value("${source.splunk.host}")
	private String hostName;
	@Value("${source.splunk.port}")
	private int port;
	@Value("${source.splunk.user}")
	private String username;
	@Value("${source.splunk.password}")
	private String password;

	@Value("${collection.fetch.data.path}")
	private String outputPath;

	@Autowired
	private FetchConfigurationRepository fetchConfigurationRepository;

	/*
	 * data from job data map parameters
	 */
	// time limits sends to splank (can be epoch/dates/spalnk constant as -1h@h) - in the case of manual run, this parameters will be used
	private String earliest;
	private String latest;

	private String savedQuery;
	private String returnKeys;
	private String filenameFormat;
	private String delimiter;
	private boolean encloseQuotes = true;
	private String sortShellScript;
	private int timeoutInSeconds;

	private File outputTempFile;
	private File outputFile;

	//time interval to bring in one fetch (uses for both regular single fetch, and paging in the case of miss fetch).
	//for manual fetch with time frame given as a run parameter will keep the -1 default and the time frame won't be paged.
	private int fetchIntervalInSeconds = -1;

	// time limits as dates to allow easy paging - will be used in continues run
	private Date earliestDate;
	private Date latestDate;

	//indicate if still have more pages to go over and fetch
	private boolean keepFetching = false;

	//the type (data source) to bring saved configuration for.
	private String type;



	@Override
	protected int getTotalNumOfSteps() {
		return 3;
	}

	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}

	@Override
	protected void runSteps() throws Exception {

		logger.info("fetch job started");


		// ensure output path exists
		logger.debug("creating output file at {}", outputPath);
		monitor.startStep(getMonitorId(), "Prepare sink file", 1);
		File outputDir = ensureOutputDirectoryExists(outputPath);

		// connect to splunk
		logger.debug("trying to connect splunk at {}@{}:{}", username, hostName, port);
		monitor.startStep(getMonitorId(), "Query Splunk", 2);
		SplunkApi splunkApi = new SplunkApi(hostName, port, username, EncryptionUtils.decrypt(password));



		do {

			// preparer fetch page params
			if  (fetchIntervalInSeconds != -1 ) {
				preparerFetchPageParams();
			}

			// try to create output file
			createOutputFile(outputDir);
			logger.debug("created output file at {}", outputTempFile.getAbsolutePath());
			monitor.finishStep(getMonitorId(), "Prepare sink file");

			// configure events handler to save events to csv file
			SplunkEventsHandlerLogger handler = new SplunkEventsHandlerLogger(outputTempFile.getAbsolutePath());
			handler.setSearchReturnKeys(returnKeys);
			handler.setDelimiter(delimiter);
			handler.setDisableQuotes(!encloseQuotes);
			handler.setSkipFirstLine(true);
			handler.setForceSingleLineEvents(true);

			Properties properties = new Properties();
			properties.put("args.earliest", earliest);
			properties.put("args.latest", latest);

			// execute the search
			try {
				logger.debug("running splunk saved query");
				splunkApi.runSearchQuery(savedQuery, properties, null, handler, timeoutInSeconds);
			} catch (Exception e) {
				// log error and delete output
				logger.error("error running splunk query", e);
				monitor.error(getMonitorId(), "Query Splunk", "error during events from splunk to file " + outputTempFile.getName() + "\n" + e.toString());
				try {
					outputTempFile.delete();
				} catch (Exception ex) {
					logger.error("cannot delete temp output file " + outputTempFile.getName());
					monitor.error(getMonitorId(), "Query Splunk", "cannot delete temporary events file " + outputTempFile.getName());
				}
				throw new JobExecutionException("error running splunk query");
			}
			monitor.finishStep(getMonitorId(), "Query Splunk");

			// report to monitor the file size
			monitor.addDataReceived(getMonitorId(), getJobDataReceived(outputTempFile));

			if (sortShellScript != null) {
				// sort the output
				monitor.startStep(getMonitorId(), "Sort Output", 3);
				sortOutput();
				monitor.finishStep(getMonitorId(), "Sort Output");
			} else {
				// rename output file once get from splunk finished
				monitor.startStep(getMonitorId(), "Rename Output", 3);
				renameOutput();
				monitor.finishStep(getMonitorId(), "Rename Output");
			}

			// update mongo with current fetch progress

			updateMongoWithCurrentFetchProgress();
		//support in smaller batches fetch - to avoid too big fetches - not relevant for manual fetches
		} while(keepFetching);

		logger.info("fetch job finished");

	}

	private void preparerFetchPageParams(){
		earliest = String.valueOf(TimestampUtils.convertToSeconds(earliestDate.getTime()));
		Date pageLatestDate = DateUtils.addSeconds(earliestDate, fetchIntervalInSeconds);
		pageLatestDate = pageLatestDate.before(latestDate) ? pageLatestDate : latestDate;
		latest = String.valueOf(TimestampUtils.convertToSeconds(pageLatestDate.getTime()));
		//set for next page
		earliestDate = pageLatestDate;
	}

	private void updateMongoWithCurrentFetchProgress(){
		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(type);
		latest = TimestampUtils.convertSplunkTimeToUnix(latest);
		if(fetchConfiguration == null){
			fetchConfiguration = new FetchConfiguration(type, latest);
		}
		else {
			fetchConfiguration.setLastFetchTime(latest);
		}
		fetchConfigurationRepository.save(fetchConfiguration);
		
		if (earliestDate != null && latestDate != null) {
			if (earliestDate.after(latestDate) || earliestDate.equals(latestDate)) {
				keepFetching = false;
			}
		}
	}

	protected void handleExecutionException(String monitorId, Exception e) throws JobExecutionException {
		if (e instanceof JobExecutionException)
			throw (JobExecutionException)e;
		else {
			logger.error("unexpected error during splunk fetch " + e.toString());
			throw new JobExecutionException(e);
		}

	}

	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		// If exists, get the output path from the job data map
		if (jobDataMapExtension.isJobDataMapContainKey(map,"path")){
			outputPath = jobDataMapExtension.getJobDataMapStringValue(map, "path");
		}

		// get parameters values from the job data map
		if (jobDataMapExtension.isJobDataMapContainKey(map,"earliest") &&
				jobDataMapExtension.isJobDataMapContainKey(map,"latest") &&
				jobDataMapExtension.isJobDataMapContainKey(map,"type")){
			earliest = jobDataMapExtension.getJobDataMapStringValue(map, "earliest");
			latest = jobDataMapExtension.getJobDataMapStringValue(map, "latest");
			type = jobDataMapExtension.getJobDataMapStringValue(map, "type");
		}
		else{
			//calculate query run times from mongo in the case not provided as job params
			logger.info("No Time frame was specified as input param, continuing from the previous run ");
			getRunTimeFrameFromMongo(map);
		}

		savedQuery = jobDataMapExtension.getJobDataMapStringValue(map, "savedQuery");
		returnKeys = jobDataMapExtension.getJobDataMapStringValue(map, "returnKeys");
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");

		// Sort command for the splunk output. Can be null (no sort is required)
		sortShellScript = jobDataMapExtension.getJobDataMapStringValue(map, "sortShellScript", null);

		// try and retrieve the delimiter value, if present in the job data map
		delimiter = jobDataMapExtension.getJobDataMapStringValue(map, "delimiter", ",");
		// try and retrieve the enclose quotes value, if present in the job data map
		encloseQuotes = jobDataMapExtension.getJobDataMapBooleanValue(map, "encloseQuotes", true);

		// setting timeout for job (default is no-timeout)
		timeoutInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "timeoutInSeconds", SplunkApi.NO_TIMEOUT);
	}


	private void getRunTimeFrameFromMongo(JobDataMap map) throws JobExecutionException{
		type = jobDataMapExtension.getJobDataMapStringValue(map, "type");
		//time back (default 1 hour)
		fetchIntervalInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchIntervalInSeconds", 3600);
		int ceilingTimePartInt = jobDataMapExtension.getJobDataMapIntValue(map, "ceilingTimePartInt", Calendar.HOUR);
		int fetchDiffInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchDiffInSeconds", 0);
		//set fetch until the ceiling of now (according to the given interval
		latestDate = DateUtils.ceiling(new Date(), ceilingTimePartInt);
		//shift the date by the configured diff
		latestDate = DateUtils.addSeconds(latestDate,-1*fetchDiffInSeconds);
		keepFetching = true;

		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(type);
		if (fetchConfiguration != null) {
			earliest = fetchConfiguration.getLastFetchTime();
			earliestDate = new Date(TimestampUtils.convertToMilliSeconds(Long.parseLong(earliest)));
		}
		else {
			earliestDate = DateUtils.addSeconds(latestDate,-1*fetchIntervalInSeconds);
		}
	}

	private void createOutputFile(File outputDir) throws JobExecutionException {
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

	private JobDataReceived getJobDataReceived(File output) {
		if (output.length() < 1024) {
			return new JobDataReceived("Events", new Integer((int)output.length()), "Bytes");
		} else {
			int sizeInKB = (int) (output.length() / 1024);
			return new JobDataReceived("Events", new Integer(sizeInKB), "KB");
		}
	}

	private void renameOutput() {
		if (outputTempFile.length()==0) {
			logger.info("deleting empty output file {}", outputTempFile.getName());
			if (!outputTempFile.delete())
				logger.warn("cannot delete empty file {}", outputTempFile.getName());
		} else {
			outputTempFile.renameTo(outputFile);
		}
	}


	private void sortOutput() throws InterruptedException {

		if (outputTempFile.length()==0) {
			logger.info("deleting empty output file {}", outputTempFile.getName());
			if (!outputTempFile.delete())
				logger.warn("cannot delete empty file {}", outputTempFile.getName());
		} else {

			Process pr =  runCmd(null, sortShellScript, outputTempFile.getAbsolutePath(), outputFile.getAbsolutePath());
			if(pr == null){
				logger.error("Failed to sort output of file {} using {}", outputTempFile.getAbsolutePath(), sortShellScript);
				addError(String.format("got the following error while running the shell command %s.",sortShellScript));
			} else if(pr.waitFor() != 0){ // wait for process to finish
				// error (return code is different than 0)
				handleCmdFailure(pr, sortShellScript);
			}
			outputTempFile.delete();

		}
	}
}
