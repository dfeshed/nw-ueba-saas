package fortscale.collection.jobs;

import com.cisco.pxgrid.model.core.IPInterfaceIdentifier;
import com.cisco.pxgrid.model.net.Session;
import com.cisco.pxgrid.model.net.User;
import com.cisco.pxgrid.stub.identity.SessionDirectoryFactory;
import com.cisco.pxgrid.stub.identity.SessionDirectoryQuery;
import com.cisco.pxgrid.stub.identity.SessionIterator;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.fetch.FetchConfiguration;
import fortscale.domain.fetch.FetchConfigurationRepository;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.pxGrid.PxGridConnectionStatus;
import fortscale.utils.pxGrid.PxGridHandler;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.time.DateUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by tomerd on 27/11/2015.
 */
public class PxGridFetchJob extends FortscaleJob {

	//<editor-fold desc="Variables">
	private static final String COMMA_DELIMITER = ",";

	private static Logger logger = LoggerFactory.getLogger(PxGridFetchJob.class);

	private final static String HOSTS_KEY = "system.pxgrid.hosts";
	private final static String USERNAME_KEY = "system.pxgrid.username";
	private final static String GROUP_KEY = "system.pxgrid.group";
	private final static String KEYSTOREPATH_KEY = "system.pxgrid.keystorepath";
	private final static String KEYSTORE_PASSPHARSE_KEY = "system.pxgrid.keystorepasspharse";
	private final static String TRUSTSTORE_PATH_KEY = "system.pxgrid.truststore";
	private final static String TRUSTSTORE_PASSPHARSE_KEY = "system.pxgrid.truststorepasspharse";
	private final static String CONNECTION_RETRY_MILLISECOND_KEY = "system.pxgrid.connectionretrymillisecond";

	@Autowired ApplicationConfigurationService applicationConfigurationService;

	//<editor-fold desc="Fetch timeframe vars">
	// time limits sends to pxGrid
	private String earliest;
	private String latest;

	// time limits as dates to allow easy paging - will be used in continues run
	private Date earliestDate;
	private Date latestDate;

	//time interval to bring in one fetch (uses for both regular single fetch, and paging in the case of miss fetch).
	//for manual fetch with time frame given as a run parameter will keep the -1 default and the time frame won't be paged.
	private int fetchIntervalInSeconds = -1;
	//</editor-fold>

	PxGridHandler pxGridHandler;

	//indicate if still have more pages to go over and fetch
	private boolean keepFetching = false;

	//the type (data source) to bring saved configuration for.
	private String type;

	// The output file format
	private String filenameFormat;

	@Value("${collection.fetch.data.path}") private String outputPath;

	@Value("${pxgrid.numberOfRetries:6000}")
	private int numberOfRetries;

	private FileWriter outputTempFile;
	private File tempOutput;
	private File outputFile;

	@Autowired private FetchConfigurationRepository fetchConfigurationRepository;
	//</editor-fold>

	//<editor-fold desc="Override Job functions">
	@Override protected int getTotalNumOfSteps() {
		return 3;
	}

	@Override protected boolean shouldReportDataReceived() {
		return true;
	}

	@Override protected void runSteps() throws Exception {
		try {
			// establishing a connection with the pxGrid controller
			logger.debug("establishing a connection with the pxGrid controller");
			PxGridConnectionStatus status = pxGridHandler.connectToGrid();
			if (status != PxGridConnectionStatus.CONNECTED) {
				logger.warn("Could not connect to pxGrid. Error: {}", status.message());
				return;
			}

			// ensure output path exists
			logger.debug("creating output file at {}", outputPath);
			monitor.startStep(getMonitorId(), "Prepare sink file", 1);
			File outputDir = ensureOutputDirectoryExists(outputPath);

			Calendar begin;
			Calendar end;

			do {

				// preparer fetch page params
				if (fetchIntervalInSeconds != -1) {
					preparerFetchPageParams();
				}

				// try to create output file
				createOutputFile(outputDir);
				monitor.finishStep(getMonitorId(), "Prepare sink file");

				// create query we'll use to make call

				// Set the query time frame
				begin = Calendar.getInstance();
				begin.setTimeInMillis(TimestampUtils.convertToMilliSeconds(Long.parseLong(earliest)));
				end = Calendar.getInstance();
				end.setTimeInMillis(TimestampUtils.convertToMilliSeconds(Long.parseLong(latest)));

				// Create iterator
				SessionDirectoryQuery sd = SessionDirectoryFactory.createSessionDirectoryQuery(pxGridHandler.
						getGridConnection());
				SessionIterator iterator = sd.getSessionsByTime(begin, end);
				iterator.open();

				// Iterate the active sessions and write to output file
				Session s;
				while ((s = iterator.next()) != null) {
					addSessionToFile(s);
				}

				iterator.close();

				// Flush & close the file stream
				outputTempFile.flush();
				outputTempFile.close();

				// Rename the output file
				renameOutput();

				// Update the current run params in the repository
				updateMongoWithCurrentFetchProgress();

			} while (keepFetching);

			logger.info("fetch job finished successfully");
		} catch (Exception e) {
			logger.error("Error while fetching data from pxGrid. Error: " + e.getMessage());
		} finally {
			pxGridHandler.close();
		}
	}

	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		loadPxGridParams();
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");

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
	}

	private void loadPxGridParams() {
		String hosts = readFromConfigurationService(HOSTS_KEY);
		String userName = readFromConfigurationService(USERNAME_KEY);
		String group = readFromConfigurationService(GROUP_KEY);
		String keystorePath = readFromConfigurationService(KEYSTOREPATH_KEY);
		String keystorePassphrase = readFromConfigurationService(KEYSTORE_PASSPHARSE_KEY);
		String truststorePath = readFromConfigurationService(TRUSTSTORE_PATH_KEY);
		String truststorePassphrase = readFromConfigurationService(TRUSTSTORE_PASSPHARSE_KEY);
		String retryMillisecond = readFromConfigurationService(CONNECTION_RETRY_MILLISECOND_KEY);
		int connectionRetryMillisecond = 0;
		if (retryMillisecond != null && !retryMillisecond.isEmpty()){
			connectionRetryMillisecond = Integer.parseInt(retryMillisecond);
		}

		pxGridHandler = new PxGridHandler(hosts, userName, group, keystorePath, keystorePassphrase, truststorePath,
				truststorePassphrase, connectionRetryMillisecond, numberOfRetries);
	}

	private String readFromConfigurationService(String key) {
		ApplicationConfiguration applicationConfiguration = applicationConfigurationService.
				getApplicationConfigurationByKey(key);
		if (applicationConfiguration != null) {
			return applicationConfiguration.getValue();
		}

		return null;
	}

	//</editor-fold>

	//<editor-fold desc="pxGrid methods">

	/**
	 * Write session to the output file
	 *
	 * @param session
	 * @throws IOException
	 */
	private void addSessionToFile(Session session) throws IOException {
		outputTempFile.append(session.getLastUpdateTime().getTime().toString());
		outputTempFile.append(COMMA_DELIMITER);

		// Get the first IP
		// TODO: How to handle multi IP's?
		List<IPInterfaceIdentifier> intfIDs = session.getInterface().getIpIntfIDs();
		if (intfIDs.size() > 0) {
			outputTempFile.append(intfIDs.get(0).getIpAddress());
		}
		outputTempFile.append(COMMA_DELIMITER);

		User user = session.getUser();
		if (user != null) {
			outputTempFile.append(user.getName());
		}

		outputTempFile.append(System.lineSeparator());
	}
	//</editor-fold>

	//<editor-fold desc="Path construction params">
	private void renameOutput() {
		if (tempOutput.length() == 0) {
			logger.info("deleting empty output file {}", tempOutput.getName());
			if (!tempOutput.delete())
				logger.warn("cannot delete empty file {}", tempOutput.getName());
		} else {
			tempOutput.renameTo(outputFile);
		}
	}

	private void createOutputFile(File outputDir) throws JobExecutionException {
		// generate filename according to the job name and time
		String filename = String.format(filenameFormat, (new Date()).getTime());
		String path = combine(outputPath, filename + ".part");
		try {
			outputTempFile = new FileWriter(path);
		} catch (IOException e) {
			logger.error("error creating file " + path);
			throw new JobExecutionException("cannot create output file " + path);
		}

		outputFile = new File(outputDir, filename);
	}

	private String combine(String firstPath, String secondPath) {
		File firstFile = new File(firstPath);
		tempOutput = new File(firstFile, secondPath);
		return tempOutput.getPath();
	}
	//</editor-fold>

	//<editor-fold desc="Handle fetch params">
	private void preparerFetchPageParams() {
		earliest = String.valueOf(TimestampUtils.convertToSeconds(earliestDate.getTime()));
		Date pageLatestDate = DateUtils.addSeconds(earliestDate, fetchIntervalInSeconds);
		pageLatestDate = pageLatestDate.before(latestDate) ? pageLatestDate : latestDate;
		latest = String.valueOf(TimestampUtils.convertToSeconds(pageLatestDate.getTime()));
		//set for next page
		earliestDate = pageLatestDate;
	}

	private void updateMongoWithCurrentFetchProgress() {
		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(type);
		latest = TimestampUtils.convertSplunkTimeToUnix(latest);
		if (fetchConfiguration == null) {
			fetchConfiguration = new FetchConfiguration(type, latest);
		} else {
			fetchConfiguration.setLastFetchTime(latest);
		}
		fetchConfigurationRepository.save(fetchConfiguration);

		if (earliestDate != null && latestDate != null) {
			if (earliestDate.after(latestDate) || earliestDate.equals(latestDate)) {
				keepFetching = false;
			}
		}
	}

	private void getRunTimeFrameFromMongo(JobDataMap map) throws JobExecutionException {
		type = jobDataMapExtension.getJobDataMapStringValue(map, "type");
		//time back (default 1 hour)
		fetchIntervalInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchIntervalInSeconds", 3600);
		int ceilingTimePartInt = jobDataMapExtension.getJobDataMapIntValue(map, "ceilingTimePartInt", Calendar.HOUR);
		int fetchDiffInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchDiffInSeconds", 0);
		//set fetch until the ceiling of now (according to the given interval
		latestDate = DateUtils.ceiling(new Date(), ceilingTimePartInt);
		//shift the date by the configured diff
		latestDate = DateUtils.addSeconds(latestDate, -1 * fetchDiffInSeconds);
		keepFetching = true;

		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(type);
		if (fetchConfiguration != null) {
			earliest = fetchConfiguration.getLastFetchTime();
			earliestDate = new Date(TimestampUtils.convertToMilliSeconds(Long.parseLong(earliest)));
		} else {
			earliestDate = DateUtils.addSeconds(latestDate, -1 * fetchIntervalInSeconds);
		}
	}
	//</editor-fold>
}