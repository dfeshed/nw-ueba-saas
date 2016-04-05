package fortscale.collection.jobs.siem;

import fortscale.collection.jobs.FetchJob;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.utils.qradar.QRadarAPI;
import fortscale.utils.qradar.result.SearchResultRequestReader;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.time.DateUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Scheduler job to fetch data from QRadar and write it to a local csv file
 * In the case the job doesn't get time frame as job params, will continue the fetch process of the data source from
 * the last saved time
 */
public class QRadarFetch extends FetchJob {

	// get common data from configuration
	@Value("${source.qradar.host}")
	private String hostName;
	@Value("${source.qradar.token}")
	private String token;
	@Value("${source.qradar.batchSize:1000}")
	private int batchSize;
	@Value("${source.qradar.maxNumberOfRetires:10}")
	private int maxNumberOfRetires;
	@Value("${source.qradar.sleepInMilliseconds:30000}")
	private long sleepInMilliseconds;

	/*
	 * data from job data map parameters
	 */
	protected File outputTempFile;
	protected File outputFile;

	@Override
	protected void startFetch() throws Exception {
		logger.info("fetch job started");
		// ensure output path exists
		logger.debug("creating output file at {}", outputPath);
		monitor.startStep(getMonitorId(), "Prepare sink file", 1);
		File outputDir = ensureOutputDirectoryExists(outputPath);
		// connect to qradar
		logger.debug("trying to connect qradar at {}", hostName);
		QRadarAPI qRadarAPI = new QRadarAPI(hostName, token);
		do {
			// preparer fetch page params
			if  (fetchIntervalInSeconds != -1 ) {
				preparerFetchPageParams();
			}
			// try to create output file
			createOutputFile(outputDir);
			logger.debug("created output file at {}", outputFile.getAbsolutePath());
			monitor.finishStep(getMonitorId(), "Prepare sink file");
			// execute the search
			try {
				logger.debug("running qradar saved query");
				SearchResultRequestReader reader = qRadarAPI.runQuery(savedQuery, returnKeys, earliest, latest,
						batchSize, maxNumberOfRetires, sleepInMilliseconds );
				String queryResults = reader.getNextBatch();
				try (FileWriter fw = new FileWriter(outputTempFile)) {
					while (queryResults != null) {
						fw.write(queryResults);
						queryResults = reader.getNextBatch();
					}
					fw.flush();
					fw.close();
				}
			} catch (Exception e) {
				// log error and delete output
				logger.error("error running qradar query", e);
				monitor.error(getMonitorId(), "Query QRadar", "error during events from qradar to file " +
						outputFile.getName() + "\n" + e.toString());
				try {
					outputFile.delete();
				} catch (Exception ex) {
					logger.error("cannot delete temp output file " + outputFile.getName());
					monitor.error(getMonitorId(), "Query QRadar", "cannot delete temporary events file " +
							outputFile.getName());
				}
				throw new JobExecutionException("error running qradar query");
			}
			monitor.finishStep(getMonitorId(), "Query QRadar");
			// report to monitor the file size
			monitor.addDataReceived(getMonitorId(), getJobDataReceived(outputFile));
			// rename output file once get from qradar finished
			monitor.startStep(getMonitorId(), "Rename Output", 3);
			renameOutput();
			monitor.finishStep(getMonitorId(), "Rename Output");
			// update mongo with current fetch progress
			updateMongoWithCurrentFetchProgress();
			//support in smaller batches fetch - to avoid too big fetches - not relevant for manual fetches
		} while (keepFetching);
	}

	protected void preparerFetchPageParams() {
		earliest = String.valueOf(TimestampUtils.convertToSeconds(earliestDate.getTime()));
		Date pageLatestDate = DateUtils.addSeconds(earliestDate, fetchIntervalInSeconds);
		pageLatestDate = pageLatestDate.before(latestDate) ? pageLatestDate : latestDate;
		latest = String.valueOf(TimestampUtils.convertToSeconds(pageLatestDate.getTime()));
		//set for next page
		earliestDate = pageLatestDate;
	}

	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();
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
		returnKeys = jobDataMapExtension.getJobDataMapStringValue(map, "returnKeys");
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		// try and retrieve the delimiter value, if present in the job data map
		delimiter = jobDataMapExtension.getJobDataMapStringValue(map, "delimiter", ",");
		// try and retrieve the enclose quotes value, if present in the job data map
		encloseQuotes = jobDataMapExtension.getJobDataMapBooleanValue(map, "encloseQuotes", true);
	}

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

	protected JobDataReceived getJobDataReceived(File output) {
		if (output.length() < 1024) {
			return new JobDataReceived("Events", new Integer((int) output.length()), "Bytes");
		} else {
			int sizeInKB = (int) (output.length() / 1024);
			return new JobDataReceived("Events", new Integer(sizeInKB), "KB");
		}
	}

	protected void renameOutput() {
		if (outputTempFile.length()==0) {
			logger.info("deleting empty output file {}", outputTempFile.getName());
			if (!outputTempFile.delete())
				logger.warn("cannot delete empty file {}", outputTempFile.getName());
		} else {
			outputTempFile.renameTo(outputFile);
		}
	}

	private String combine(String firstPath, String secondPath) {
		File firstFile = new File(firstPath);
		outputFile = new File(firstFile, secondPath);
		return outputFile.getPath();
	}

}