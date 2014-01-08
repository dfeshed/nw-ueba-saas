package fortscale.collection.jobs;

import java.io.File;
import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fortscale.utils.splunk.SplunkApi;

/**
 * vpn scheduler job to fetch data from splunk and write it to a local csv file
 */
public class VPNFetchJob implements Job {

	private static Logger logger = LoggerFactory.getLogger(VPNFetchJob.class);
	
	private static final String SPLUNK_SAVED_SEARCH = "getVPNdata";
	
	private String hostName;
	private int port;
	private String username;
	private String password;
	private String earliest;
	private String latest;
	private String outputDirPath;
	private boolean trimMultipleLineEvents = false;
	private String savedQuery;
	private String returnKeys;
	private String delimiter;
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("vpn fetch job started");
		
		// ensure output path exists
		File outputDirFile = new File(outputDirPath);
		try {
			if (!outputDirFile.exists()) {
				// try to create output directory
				outputDirFile.mkdirs();
			}
		} catch (SecurityException e) {
			logger.error("cannot create output path - " + outputDirPath, e);
			// stop execution, notify scheduler not to re-fire immediately
			throw new JobExecutionException(e,  false); 
		}
		
		// connect to splunk
		logger.debug("trying to connect splunk at server={}, port={}, username={}, password={}", hostName, port, username, password);
		SplunkApi splunkApi = new SplunkApi(hostName, port, username, password);
		
		// configure events handler
		SplunkEventsHandlerToFile handler = new SplunkEventsHandlerToFile(outputFile.getFile());
		handler.setSearchReturnKeys(returnKeys);
		handler.setDelimiter(delimiter);
		handler.setDisableQuotes(true);
		handler.setSkipFirstLine(true);
		handler.setForceSingleLineEvents(trimMultipleLineEvents);
			
		Properties properties = new Properties();
		properties.put("args.earliest", earliest);
		properties.put("args.latest", latest);
		
		splunkApi.runSavedSearch(savedQuery, properties, null, handler);
		
		
		
		
		
		logger.info("vpn fetch job finished");
	}
	
}
