package fortscale.collection.jobs;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fortscale.collection.JobDataMapExtension;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.utils.splunk.SplunkApi;
import fortscale.utils.splunk.SplunkEventsHandlerLogger;


/**
 * Scheduler job to fetch data from splunk and write it to a local csv file
 */
@DisallowConcurrentExecution
public class SplunkFetchSavedQueryJob extends FortscaleJob {

	private static Logger logger = LoggerFactory.getLogger(SplunkFetchSavedQueryJob.class);

	// get common data from configuration
	@Value("${splunk.host}")
	private String hostName;
	@Value("${splunk.port}")
	private int port;
	@Value("${splunk.user}")
	private String username;
	@Value("${splunk.password}")
	private String password;

	@Value("${collection.fetch.data.path}")
	private String outputPath;

	// data from job data map parameters
	private String earliest;
	private String latest;
	private String savedQuery;
	private String returnKeys;
	private String filenameFormat;
	private String delimiter;
	private boolean encloseQuotes = true;
	private String sortShellScript;

	private File outputTempFile;
	private File outputFile;


	@Autowired
	private JobDataMapExtension jobDataMapExtension;

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
		SplunkApi splunkApi = new SplunkApi(hostName, port, username, password);

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
			splunkApi.runSavedSearch(savedQuery, properties, null, handler);
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

		logger.info("fetch job finished");

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

		// get parameters values from the job data map
		earliest = jobDataMapExtension.getJobDataMapStringValue(map, "earliest");
		latest = jobDataMapExtension.getJobDataMapStringValue(map, "latest");
		savedQuery = jobDataMapExtension.getJobDataMapStringValue(map, "savedQuery");
		returnKeys = jobDataMapExtension.getJobDataMapStringValue(map, "returnKeys");
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");

		// Sort command for the splunk output. Can be null (no sort is required)
		sortShellScript = jobDataMapExtension.getJobDataMapStringValue(map, "sortShellScript", null);

		// try and retrieve the delimiter value, if present in the job data map
		delimiter = jobDataMapExtension.getJobDataMapStringValue(map, "delimiter", ",");
		// try and retrieve the enclose quotes value, if present in the job data map
		encloseQuotes = jobDataMapExtension.getJobDataMapBooleanValue(map, "encloseQuotes", true);
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
			return new JobDataReceived("Events", (int)output.length(), "Bytes");
		} else {
			int sizeInKB = (int) (output.length() / 1024);
			return new JobDataReceived("Events", sizeInKB, "KB");
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
