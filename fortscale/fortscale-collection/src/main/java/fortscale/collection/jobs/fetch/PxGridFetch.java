package fortscale.collection.jobs.fetch;

import com.cisco.pxgrid.model.core.IPInterfaceIdentifier;
import com.cisco.pxgrid.model.net.Session;
import com.cisco.pxgrid.model.net.User;
import com.cisco.pxgrid.stub.identity.SessionDirectoryFactory;
import com.cisco.pxgrid.stub.identity.SessionDirectoryQuery;
import com.cisco.pxgrid.stub.identity.SessionIterator;
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

	@Value("${pxgrid.numberOfRetries:6000}")
	private int numberOfRetries;

	private final static String HOSTS_KEY = "system.pxgrid.hosts";
	private final static String USERNAME_KEY = "system.pxgrid.username";
	private final static String GROUP_KEY = "system.pxgrid.group";
	private final static String KEYSTOREPATH_KEY = "system.pxgrid.keystorepath";
	private final static String KEYSTORE_PASSPHARSE_KEY = "system.pxgrid.keystorepasspharse";
	private final static String TRUSTSTORE_PATH_KEY = "system.pxgrid.truststore";
	private final static String TRUSTSTORE_PASSPHARSE_KEY = "system.pxgrid.truststorepasspharse";
	private final static String CONNECTION_RETRY_MILLISECOND_KEY = "system.pxgrid.connectionretrymillisecond";

	private PxGridHandler pxGridHandler;
	private FileWriter fw;

	@Override
	protected boolean connect() throws Exception {
		// establishing a connection with the pxGrid controller
		logger.debug("establishing a connection with the pxGrid controller");
		PxGridConnectionStatus status = pxGridHandler.connectToGrid();
		if (status != PxGridConnectionStatus.CONNECTED) {
			logger.warn("Could not connect to pxGrid. Error: {}", status.message());
			return false;
		}
		return true;
	}

	@Override
	protected void fetch() throws Exception {
		// create query we'll use to make call
		// Set the query time frame
		File outputTempFile = new File(outputDir, tempfilename);
		fw = new FileWriter(outputTempFile);
		Calendar begin = Calendar.getInstance();
		begin.setTimeInMillis(TimestampUtils.convertToMilliSeconds(Long.parseLong(earliest)));
		Calendar end = Calendar.getInstance();
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
		fw.flush();
		fw.close();
		// Rename the output file
		renameOutput();
		// Update the current run params in the repository
		updateMongoWithCurrentFetchProgress();
	}

	@Override
	protected void finish() throws Exception {
		pxGridHandler.close();
	}

	/**
	 * Write session to the output file
	 *
	 * @param session
	 * @throws IOException
	 */
	private void addSessionToFile(Session session) throws IOException {
		fw.append(session.getLastUpdateTime().getTime().toString());
		fw.append(delimiter);
		// Get the first IP
		// TODO: How to handle multi IP's?
		List<IPInterfaceIdentifier> intfIDs = session.getInterface().getIpIntfIDs();
		if (intfIDs.size() > 0) {
			fw.append(intfIDs.get(0).getIpAddress());
		}
		fw.append(delimiter);
		User user = session.getUser();
		if (user != null) {
			fw.append(user.getName());
		}
		fw.append(System.lineSeparator());
	}

	/**
	 *
	 * This method gets the specific job parameters
	 *
	 * @param context
	 */
	@Override
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
			type = jobDataMapExtension.getJobDataMapStringValue(map, "type");
			//time back (default 1 hour)
			fetchIntervalInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchIntervalInSeconds", 3600);
			ceilingTimePartInt = jobDataMapExtension.getJobDataMapIntValue(map, "ceilingTimePartInt", Calendar.HOUR);
			fetchDiffInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchDiffInSeconds", 0);
			getRunTimeFrameFromMongo(map);
		}
		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		// try and retrieve the delimiter value, if present in the job data map
		delimiter = jobDataMapExtension.getJobDataMapStringValue(map, "delimiter", ",");
		// try and retrieve the enclose quotes value, if present in the job data map
		encloseQuotes = jobDataMapExtension.getJobDataMapBooleanValue(map, "encloseQuotes", true);
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

}