package fortscale.collection.jobs.fetch.siem;

import fortscale.collection.jobs.fetch.FetchJob;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.splunk.SplunkEventsHandlerLogger;
import fortscale.utils.splunk.SplunkApi;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

/**
 * Scheduler job to fetch data from Splunk and write it to a local csv file
 * In the case the job doesn't get time frame as job params, will continue the fetch process of the
 * data source from the last saved time
 */
public class Splunk extends FetchJob {

	public static final String SIEM_NAME = "splunk";

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

	/**
	 *
	 * This method gets the specific job parameters
	 *
	 * @param map
	 * @throws JobExecutionException
	 */
	@Override
	protected void getExtraJobParameters(JobDataMap map) throws JobExecutionException {
		// setting timeout for job (default is no-timeout)
		timeoutInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "timeoutInSeconds", SplunkApi.NO_TIMEOUT);
		// Sort command for the splunk output. Can be null (no sort is required)
		sortShellScript = jobDataMapExtension.getJobDataMapStringValue(map, "sortShellScript", null);
		runSavedQuery = jobDataMapExtension.getJobDataMapBooleanValue(map, "runSavedQuery", true);
	}

}