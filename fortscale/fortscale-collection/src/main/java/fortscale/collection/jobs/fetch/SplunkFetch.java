package fortscale.collection.jobs.fetch;

import fortscale.utils.EncryptionUtils;
import fortscale.utils.splunk.SplunkApi;
import fortscale.utils.siem.SplunkEventsHandlerLogger;
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
	protected boolean connect() throws Exception {
		// connect to Splunk
		logger.debug("trying to connect Splunk at {}@{}:{}", username, hostName, port);
		splunkApi = new SplunkApi(hostName, port, username, EncryptionUtils.decrypt(password));
		return true;
	}

	@Override
	protected void fetch() throws Exception {
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
	}

	@Override
	protected void finish() throws Exception {}

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