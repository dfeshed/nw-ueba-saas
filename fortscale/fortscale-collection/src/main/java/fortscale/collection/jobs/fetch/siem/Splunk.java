package fortscale.collection.jobs.fetch.siem;

import fortscale.collection.JobDataMapExtension;
import fortscale.collection.jobs.fetch.FetchJob;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.splunk.SplunkApi;
import fortscale.utils.splunk.SplunkEventsHandlerLogger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;

import java.io.File;
import java.util.Properties;

/**
 * Scheduler job to fetch data from Splunk and write it to a local csv file
 * In the case the job doesn't get time frame as job params, will continue the fetch process of the
 * data source from the last saved time
 */
@DisallowConcurrentExecution
public class Splunk extends FetchJob {

	public static final String SIEM_NAME = "splunk";
	public static final String DEFAULT_USER = "admin";
	public static final int DEFAULT_PORT = 8089;

	private int timeoutInSeconds;
	private SplunkApi splunkApi;
	private boolean runSavedQuery;

	@Override
	protected boolean connect(String hostName, int port, String username, String password) throws Exception {
		// connect to Splunk
		int portNumber = port == 0 ? DEFAULT_PORT : port;
		String user = username == null ? DEFAULT_USER : username;
		logger.debug("trying to connect Splunk at {}@{}:{}", username, hostName, port);
		splunkApi = new SplunkApi(hostName, portNumber, user, EncryptionUtils.decrypt(password));
		return true;
	}

	@Override
	protected void fetch(String filename, String tempfilename, File outputDir, String returnKeys, String delimiter,
						 boolean encloseQuotes, String earliest, String latest, String savedQuery) throws Exception {
		// configure events handler to save events to csv file
		File outputTempFile = new File(outputDir, tempfilename);
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
		logger.debug("running splunk saved query");
		try {
			if (runSavedQuery) {
				splunkApi.runSavedSearch(savedQuery, properties, null, handler, timeoutInSeconds);
			} else {
				splunkApi.runSearchQuery(savedQuery, properties, null, handler, timeoutInSeconds);
			}
		} catch (Exception e) {
			// log error and delete output
			logger.error("error running splunk query", e);
			throw new JobExecutionException("error running splunk query");
		}
	}

	/**
	 *
	 * This method gets the specific job parameters
	 *
	 * @param map
	 * @param jobDataMapExtension
	 * @throws JobExecutionException
	 */
	@Override
	protected void getExtraParameters(JobDataMap map, JobDataMapExtension jobDataMapExtension)
			throws JobExecutionException {
		// setting timeout for job (default is no-timeout)
		timeoutInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "timeoutInSeconds", SplunkApi.NO_TIMEOUT);
		// Sort command for the splunk output. Can be null (no sort is required)
		sortShellScript = jobDataMapExtension.getJobDataMapStringValue(map, "sortShellScript", null);
		runSavedQuery = jobDataMapExtension.getJobDataMapBooleanValue(map, "runSavedQuery", true);
	}

}