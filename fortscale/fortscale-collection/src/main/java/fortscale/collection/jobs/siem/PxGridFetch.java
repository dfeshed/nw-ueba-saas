package fortscale.collection.jobs.siem;

import com.cisco.pxgrid.model.core.IPInterfaceIdentifier;
import com.cisco.pxgrid.model.net.Session;
import com.cisco.pxgrid.model.net.User;
import com.cisco.pxgrid.stub.identity.SessionDirectoryFactory;
import com.cisco.pxgrid.stub.identity.SessionDirectoryQuery;
import com.cisco.pxgrid.stub.identity.SessionIterator;
import fortscale.collection.jobs.FetchJob;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.utils.pxGrid.PxGridConnectionStatus;
import fortscale.utils.pxGrid.PxGridHandler;
import fortscale.utils.time.TimestampUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by tomerd on 27/11/2015.
 */
public class PxGridFetch extends FetchJob {

	//<editor-fold desc="Variables">
	@Value("${pxgrid.numberOfRetries:6000}") private int numberOfRetries;

	private final static String HOSTS_KEY = "system.pxgrid.hosts";
	private final static String USERNAME_KEY = "system.pxgrid.username";
	private final static String GROUP_KEY = "system.pxgrid.group";
	private final static String KEYSTOREPATH_KEY = "system.pxgrid.keystorepath";
	private final static String KEYSTORE_PASSPHARSE_KEY = "system.pxgrid.keystorepasspharse";
	private final static String TRUSTSTORE_PATH_KEY = "system.pxgrid.truststore";
	private final static String TRUSTSTORE_PASSPHARSE_KEY = "system.pxgrid.truststorepasspharse";
	private final static String CONNECTION_RETRY_MILLISECOND_KEY = "system.pxgrid.connectionretrymillisecond";

	private PxGridHandler pxGridHandler;
	private FileWriter outputTempFile;
	private File tempOutput;
	private File outputFile;
	//</editor-fold>
	//<editor-fold desc="Override Job functions">

	@Override
	protected void connect() throws Exception {
		// establishing a connection with the pxGrid controller
		logger.debug("establishing a connection with the pxGrid controller");
		PxGridConnectionStatus status = pxGridHandler.connectToGrid();
		if (status != PxGridConnectionStatus.CONNECTED) {
			logger.warn("Could not connect to pxGrid. Error: {}", status.message());
			System.exit(1);
		}
	}

	@Override
	protected void startFetch() throws Exception {
		try {
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
		// try and retrieve the delimiter value, if present in the job data map
		delimiter = jobDataMapExtension.getJobDataMapStringValue(map, "delimiter", ",");
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
		if (retryMillisecond != null && !retryMillisecond.isEmpty()) {
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
		outputTempFile.append(delimiter);
		// Get the first IP
		// TODO: How to handle multi IP's?
		List<IPInterfaceIdentifier> intfIDs = session.getInterface().getIpIntfIDs();
		if (intfIDs.size() > 0) {
			outputTempFile.append(intfIDs.get(0).getIpAddress());
		}
		outputTempFile.append(delimiter);
		User user = session.getUser();
		if (user != null) {
			outputTempFile.append(user.getName());
		}
		outputTempFile.append(System.lineSeparator());
	}
	//</editor-fold>

}