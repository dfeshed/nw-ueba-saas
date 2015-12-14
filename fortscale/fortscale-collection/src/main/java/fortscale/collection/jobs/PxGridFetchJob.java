package fortscale.collection.jobs;

import com.cisco.pxgrid.GridConnection;
import com.cisco.pxgrid.GridConnection.Listener;
import com.cisco.pxgrid.ReconnectionManager;
import com.cisco.pxgrid.TLSConfiguration;
import com.cisco.pxgrid.model.core.GenericAttribute;
import com.cisco.pxgrid.model.core.GenericAttributeValueType;
import com.cisco.pxgrid.model.core.IPInterfaceIdentifier;
import com.cisco.pxgrid.model.net.*;
import com.cisco.pxgrid.stub.identity.SessionDirectoryFactory;
import com.cisco.pxgrid.stub.identity.SessionDirectoryQuery;
import com.cisco.pxgrid.stub.identity.SessionIterator;
import fortscale.domain.fetch.FetchConfiguration;
import fortscale.domain.fetch.FetchConfigurationRepository;
import fortscale.utils.time.TimeUtils;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.time.DateUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by tomerd on 27/11/2015.
 */
public class PxGridFetchJob extends FortscaleJob {

	private static final String COMMA_DELIMITER = ",";

	private static Logger logger = LoggerFactory.getLogger(PxGridFetchJob.class);

	// time limits sends to pxGrid
	private String earliest;
	private String latest;

	//time interval to bring in one fetch (uses for both regular single fetch, and paging in the case of miss fetch).
	//for manual fetch with time frame given as a run parameter will keep the -1 default and the time frame won't be paged.
	private int fetchIntervalInSeconds = -1;

	// time limits as dates to allow easy paging - will be used in continues run
	private Date earliestDate;
	private Date latestDate;

	//indicate if still have more pages to go over and fetch
	private boolean keepFetching = false;

	//the type (data source) to bring saved configuration for.
	private String type;

	// The output file format
	private String filenameFormat;

	// pxGrid params
	private String hosts;
	private String userName;
	private String group;
	private String keystorePath;
	private String keystorePassphrase;
	private String truststorePath;
	private String truststorePassphrase;
	private int connectionRetryMillisecond;

	@Value("${collection.fetch.data.path}") private String outputPath;

	private FileWriter outputTempFile;
	private File tempOutput;
	private File outputFile;


	@Autowired private FetchConfigurationRepository fetchConfigurationRepository;

	// Flag to indicate whether connection is established to the grid
	private boolean connected;

	// Hold the connection to the grid
	GridConnection con;
	ReconnectionManager recon;

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
			connectToGrid();

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
				begin = Calendar.getInstance();
				//begin.set(Calendar.YEAR, begin.get(Calendar.YEAR) - 1);
				begin.setTimeInMillis(TimestampUtils.convertToMilliSeconds(Long.parseLong(earliest)));
				end = Calendar.getInstance();
				end.setTimeInMillis(TimestampUtils.convertToMilliSeconds(Long.parseLong(latest)));
				SessionDirectoryQuery sd = SessionDirectoryFactory.createSessionDirectoryQuery(con);
				SessionIterator iterator = sd.getSessionsByTime(begin, end);
				iterator.open();

				Session s;
				while ((s = iterator.next()) != null) {
					addSessionToFile(s);
				}

				iterator.close();

				renameOutput();
				updateMongoWithCurrentFetchProgress();
			} while (keepFetching);

			logger.info("fetch job finished successfully");
		} catch (Exception e) {
			logger.error("Error while fetching data from pxGrid. Error: " + e.getMessage());
		} finally {
			if (recon != null && con.isConnected()) {
				// disconnect from pxGrid
				recon.stop();
			}
			outputTempFile.flush();
			outputTempFile.close();
		}
	}

	private void addSessionToFile(Session session) throws IOException{
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

	private void renameOutput() {
		if (tempOutput.length()==0) {
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

	private String combine(String firstPath, String secondPath)
	{
		File firstFile = new File(firstPath);
		tempOutput = new File(firstFile, secondPath);
		return tempOutput.getPath();
	}

	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		hosts = jobDataMapExtension.getJobDataMapStringValue(map, "hosts");
		userName = jobDataMapExtension.getJobDataMapStringValue(map, "userName");
		group = jobDataMapExtension.getJobDataMapStringValue(map, "group");
		keystorePath = jobDataMapExtension.getJobDataMapStringValue(map, "keystorePath");
		keystorePassphrase = jobDataMapExtension.getJobDataMapStringValue(map, "keystorePassphrase");
		truststorePath = jobDataMapExtension.getJobDataMapStringValue(map, "truststorePath");
		truststorePassphrase = jobDataMapExtension.getJobDataMapStringValue(map, "truststorePassphrase");
		connectionRetryMillisecond = jobDataMapExtension.getJobDataMapIntValue(map, "connectionRetryMillisecond");

		try {
			keystoreLoadTest(keystorePath, keystorePassphrase);
			keystoreLoadTest(truststorePath, truststorePassphrase);
		} catch (Exception e) {
			throw new JobExecutionException("Error loading keys; Error: " + e.getMessage());
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
	}

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

	private void connectToGrid() throws Exception {

		// configure the connection properties
		TLSConfiguration config = createConfigObject();

		con = new GridConnection(config);
		//con.addListener(new MyListener());

		recon = new ReconnectionManager(con);
		recon.setRetryMillisecond(connectionRetryMillisecond);
		recon.start();

		// Wait for the connection to establish
		while (!con.isConnected()) {
			Thread.sleep(100);
		}
	}

	private void keystoreLoadTest(String filename, String password) throws GeneralSecurityException, IOException {
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(filename), password.toCharArray());
	}

	private TLSConfiguration createConfigObject() {
		TLSConfiguration config = new TLSConfiguration();
		config.setHosts(new String[] { hosts });
		config.setUserName(userName);
		config.setGroup(group);
		config.setKeystorePath(keystorePath);
		config.setKeystorePassphrase(keystorePassphrase);
		config.setTruststorePath(truststorePath);
		config.setTruststorePassphrase(truststorePassphrase);

		return config;
	}
}
