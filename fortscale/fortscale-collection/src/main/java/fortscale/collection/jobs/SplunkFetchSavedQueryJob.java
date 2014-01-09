package fortscale.collection.jobs;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fortscale.utils.splunk.SplunkApi;
import fortscale.utils.splunk.SplunkEventsHandlerLogger;
import static fortscale.collection.JobDataMapExtension.getJobDataMapIntValue;
import static fortscale.collection.JobDataMapExtension.getJobDataMapStringValue;


/**
 * Scheduler job to fetch data from splunk and write it to a local csv file
 */
@DisallowConcurrentExecution
public class SplunkFetchSavedQueryJob implements Job {

	private static Logger logger = LoggerFactory.getLogger(SplunkFetchSavedQueryJob.class);
		
	private String hostName;
	private int port;
	private String username;
	private String password;
	private String earliest;
	private String latest;
	private String savedQuery;
	private String outputPath;
	private String returnKeys;
	private String filenameFormat;
	
	private File outputTempFile;
	private File outputFile;
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("fetch job started");

		// get the job group name to be used using monitoring 
		String sourceName = context.getJobDetail().getKey().getGroup();
		String jobName = context.getJobDetail().getKey().getName();
		
		// get parameters from context
		logger.debug("getting parameters from job context");
		getJobParameters(context);
		
		// ensure output path exists
		logger.debug("creating output file at {}", outputPath);
		File outputDir = ensureOutputDirectoryExists(outputPath);
		
		// try to create output file
		createOutputFile(context, outputDir);
		logger.debug("created output file at {}", outputTempFile.getAbsolutePath());
		
		
		// connect to splunk
		logger.debug("trying to connect splunk at {}@{}:{}", username, hostName, port);
		SplunkApi splunkApi = new SplunkApi(hostName, port, username, password);
		
		// configure events handler to save events to csv file
		SplunkEventsHandlerLogger handler = new SplunkEventsHandlerLogger(outputTempFile.getAbsolutePath());
		handler.setSearchReturnKeys(returnKeys);
		handler.setDelimiter(",");
		handler.setDisableQuotes(true);
		handler.setSkipFirstLine(true);
		handler.setForceSingleLineEvents(true);
			
		Properties properties = new Properties();
		properties.put("args.earliest", earliest);
		properties.put("args.latest", latest);
		
		// execute the search
		try {
			logger.debug("running splunk saved query");
			splunkApi.runSavedSearch(savedQuery, properties, null, handler);
		} catch (Exception e) {
			logger.error("error running splunk query", e);
			throw new JobExecutionException("error running splunk query");
		}
		
		// rename output file once get from splunk finished
		outputTempFile.renameTo(outputFile);
		
		logger.info("vpn fetch job finished");
	}
	
	private void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();
		
		// get parameters values from the job data map
		hostName = getJobDataMapStringValue(map, "hostName");
		port = getJobDataMapIntValue(map, "port");
		username = getJobDataMapStringValue(map, "username");
		password = getJobDataMapStringValue(map, "password");
		earliest = getJobDataMapStringValue(map, "earliest");
		latest = getJobDataMapStringValue(map, "latest");
		savedQuery = getJobDataMapStringValue(map, "savedQuery");
		outputPath = getJobDataMapStringValue(map, "outputPath");
		returnKeys = getJobDataMapStringValue(map, "returnKeys");
		filenameFormat = getJobDataMapStringValue(map, "filenameFormat");
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
	
	private void createOutputFile(JobExecutionContext context, File outputDir) throws JobExecutionException {
		
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
}
