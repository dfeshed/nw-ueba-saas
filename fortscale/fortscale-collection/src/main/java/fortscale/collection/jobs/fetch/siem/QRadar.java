package fortscale.collection.jobs.fetch.siem;

import fortscale.collection.jobs.fetch.FetchJob;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.qradar.QRadarAPI;
import fortscale.utils.qradar.result.SearchResultRequestReader;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileWriter;

/**
 * Scheduler job to fetch data from QRadar and write it to a local csv file
 * In the case the job doesn't get time frame as job params, will continue the fetch process of the data source from
 * the last saved time
 */
@DisallowConcurrentExecution
public class QRadar extends FetchJob {

	public static final String SIEM_NAME = "qradar";

	@Value("${source.qradar.batchSize:1000}")
	private int batchSize;
	@Value("${source.qradar.maxNumberOfRetires:10}")
	private int maxNumberOfRetires;
	@Value("${source.qradar.sleepInMilliseconds:30000}")
	private long sleepInMilliseconds;

	private QRadarAPI qRadarAPI;

	@Override
	protected boolean connect(String hostName, String port, String username, String password) throws Exception {
		// connect to QRadar
		logger.debug("trying to connect QRadar at {}", hostName);
		qRadarAPI = new QRadarAPI(hostName, EncryptionUtils.decrypt(password));
		return true;
	}

	@Override
	protected void fetch(String filename, String tempfilename) throws Exception {
		try {
			logger.debug("running QRadar saved query");
			SearchResultRequestReader reader = qRadarAPI.runQuery(savedQuery, returnKeys, earliest, latest, batchSize,
					maxNumberOfRetires, sleepInMilliseconds);
			String queryResults = reader.getNextBatch();
			File outputTempFile = new File(outputDir, tempfilename);
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
			throw new JobExecutionException("error running QRadar query");
		}
	}

}