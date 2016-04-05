package fortscale.collection.jobs.siem;

import fortscale.collection.jobs.FetchJob;
import fortscale.utils.qradar.QRadarAPI;
import fortscale.utils.qradar.result.SearchResultRequestReader;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileWriter;

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

	private QRadarAPI qRadarAPI;

	@Override
	protected boolean connect() throws Exception {
		// connect to qradar
		logger.debug("trying to connect QRadar at {}", hostName);
		qRadarAPI = new QRadarAPI(hostName, token);
		return true;
	}

	@Override
	protected void startFetch() throws Exception {
		try {
			logger.debug("running QRadar saved query");
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
			logger.error("error running QRadar query", e);
			monitor.error(getMonitorId(), "Query QRadar", "error during events from qradar to file " +
					outputFile.getName() + "\n" + e.toString());
			try {
				outputFile.delete();
			} catch (Exception ex) {
				logger.error("cannot delete temp output file " + outputFile.getName());
				monitor.error(getMonitorId(), "Query QRadar", "cannot delete temporary events file " +
						outputFile.getName());
			}
			throw new JobExecutionException("error running QRadar query");
		}
	}

	@Override
	protected void finish() {}

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

}