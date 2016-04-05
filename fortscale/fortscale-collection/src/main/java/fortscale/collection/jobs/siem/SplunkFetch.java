package fortscale.collection.jobs.siem;

import fortscale.collection.jobs.FetchJob;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.splunk.SplunkApi;
import fortscale.utils.splunk.SplunkEventsHandlerLogger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

/**
 * Scheduler job to fetch data from Splunk and write it to a local csv file
 * In the case the job doesn't get time frame as job params, will continue the fetch process of the
 * data source from the last saved time
 */
public class SplunkFetch extends FetchJob {

	// get common data from configuration
	@Value("${source.splunk.host}")
	private String hostName;
	@Value("${source.splunk.port}")
	private int port;
	@Value("${source.splunk.user}")
	private String username;
	@Value("${source.splunk.password}")
	private String password;

	private int timeoutInSeconds;
	private SplunkApi splunkApi;
	private boolean runSavedQuery;

	@Override
	protected void connect() throws Exception {
		// connect to splunk
		logger.debug("trying to connect Splunk at {}@{}:{}", username, hostName, port);
		splunkApi = new SplunkApi(hostName, port, username, EncryptionUtils.decrypt(password));
	}

	@Override
	protected void startFetch() throws Exception {
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
				if (runSavedQuery) {
					splunkApi.runSavedSearch(savedQuery, properties, null, handler, timeoutInSeconds);
				} else {
					splunkApi.runSearchQuery(savedQuery, properties, null, handler, timeoutInSeconds);
				}
			} catch (Exception e) {
				// log error and delete output
				logger.error("error running splunk query", e);
				monitor.error(getMonitorId(), "Query Splunk", "error during events from splunk to file " +
						outputTempFile.getName() + "\n" + e.toString());
				try {
					outputTempFile.delete();
				} catch (Exception ex) {
					logger.error("cannot delete temp output file " + outputTempFile.getName());
					monitor.error(getMonitorId(), "Query Splunk", "cannot delete temporary events file " +
							outputTempFile.getName());
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

	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();
		// If exists, get the output path from the job data map
		if (jobDataMapExtension.isJobDataMapContainKey(map,"path")){
			outputPath = jobDataMapExtension.getJobDataMapStringValue(map, "path");
		}
		runSavedQuery = jobDataMapExtension.getJobDataMapBooleanValue(map, "runSavedQuery", true);
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

}