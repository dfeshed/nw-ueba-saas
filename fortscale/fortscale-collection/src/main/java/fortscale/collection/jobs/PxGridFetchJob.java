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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by tomerd on 27/11/2015.
 */
public class PxGridFetchJob extends FortscaleJob {

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

	@Value("${collection.fetch.data.path}")
	private String outputPath;

	private File outputTempFile;
	private File outputFile;

	@Autowired
	private FetchConfigurationRepository fetchConfigurationRepository;

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
				if  (fetchIntervalInSeconds != -1 ) {
					preparerFetchPageParams();
				}

				// try to create output file
				createOutputFile(outputDir);
				logger.debug("created output file at {}", outputTempFile.getAbsolutePath());
				monitor.finishStep(getMonitorId(), "Prepare sink file");

				//begin.setTime(earliest);

			} while(keepFetching);


			// create query we'll use to make call
			begin = Calendar.getInstance();
			begin.set(Calendar.YEAR, begin.get(Calendar.YEAR) - 1);
			end = Calendar.getInstance();
			SessionDirectoryQuery sd = SessionDirectoryFactory.createSessionDirectoryQuery(con);
			SessionIterator iterator = sd.getSessionsByTime(begin, end);
			iterator.open();

			Session s;
			while ((s = iterator.next()) != null) {
				print(s);
			}

			iterator.close();

		} finally {
			if (recon != null && con.isConnected()) {
				// disconnect from pxGrid
				recon.stop();
			}
		}
	}

	private void print(Session session) {
		System.out.print("Session={");

		List<IPInterfaceIdentifier> intfIDs = session.getInterface().getIpIntfIDs();
		System.out.print("ip=[");
		for (int i = 0; i < intfIDs.size(); i++) {
			if (i > 0)
				System.out.print(",");
			System.out.print(intfIDs.get(i).getIpAddress());
		}
		System.out.print("]");

		System.out.print(", Audit Session Id=" + session.getGid());
		User user = session.getUser();
		if (user != null) {
			System.out.print(", User Name=" + user.getName());
			System.out.print(", AD User DNS Domain=" + user.getADUserDNSDomain());
			System.out.print(", AD Host DNS Domain=" + user.getADHostDNSDomain());
			System.out.print(", AD User NetBIOS Name=" + user.getADUserNetBIOSName());
			System.out.print(", AD Host NETBIOS Name=" + user.getADHostNetBIOSName());
		}

		List<String> macs = session.getInterface().getMacAddresses();
		if (macs != null && macs.size() > 0) {
			System.out.print(", Calling station id=" + macs.get(0));
		}

		System.out.print(", Session state=" + session.getState());
		//System.out.print(", Epsstatus=" + session.getEPSStatus());
		System.out.print(", ANCstatus=" + session.getANCStatus());
		System.out.print(", Security Group=" + session.getSecurityGroup());
		System.out.print(", Endpoint Profile=" + session.getEndpointProfile());

		// Port and NAS Ip information
		DevicePortIdentifier deviceAttachPt = session.getInterface().getDeviceAttachPt();
		if (deviceAttachPt != null) {
			IPInterfaceIdentifier deviceMgmtIntfID = deviceAttachPt.getDeviceMgmtIntfID();
			if (deviceMgmtIntfID != null) {
				System.out.print(", NAS IP=" + deviceAttachPt.getDeviceMgmtIntfID().getIpAddress());
			}
			Port port = deviceAttachPt.getPort();
			if (port != null) {
				System.out.print(", NAS Port=" + port.getPortId());
			}
		}

		List<RADIUSAVPair> radiusAVPairs = session.getRADIUSAttrs();
		if (radiusAVPairs != null && !radiusAVPairs.isEmpty()) {
			System.out.print(", RADIUSAVPairs=[");
			for (RADIUSAVPair p : radiusAVPairs) {
				System.out.print(" " + p.getAttrName() + "=" + p.getAttrValue());
			}
			System.out.print("]");
		}

		// Posture Info
		List<PostureAssessment> postures = session.getAssessedPostureEvents();
		if (postures != null && postures.size() > 0) {
			System.out.print(", Posture Status=" + postures.get(0).getStatus());

			Calendar cal = postures.get(0).getLastUpdateTime();
			System.out.print(", Posture Timestamp=" + ((cal != null) ? cal.getTime() : ""));

		}
		System.out.print(", Session Last Update Time=" + session.getLastUpdateTime().getTime());
		//Get Generic Attributes
		List<GenericAttribute> attributes = session.getExtraAttributes();
		for (GenericAttribute attrib : attributes) {

			System.out.print(", Session attributeName=" + attrib.getName());
			if (attrib.getType() == GenericAttributeValueType.STRING) {
				String attribValue = null;
				try {
					attribValue = new String(attrib.getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {

					e.printStackTrace();
				}
				System.out.print(", Session attributeValue=" + attribValue);
			}
		}
		System.out.println("}");
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
		if (jobDataMapExtension.isJobDataMapContainKey(map,"earliest") &&
				jobDataMapExtension.isJobDataMapContainKey(map,"latest") &&
				jobDataMapExtension.isJobDataMapContainKey(map,"type")){
			earliest = jobDataMapExtension.getJobDataMapStringValue(map, "earliest");
			latest = jobDataMapExtension.getJobDataMapStringValue(map, "latest");
			type = jobDataMapExtension.getJobDataMapStringValue(map, "type");
		}
		else{
			//calculate query run times from mongo in the case not provided as job params
			logger.info("No Time frame was specified as input param, continuing from the previous run ");
			getRunTimeFrameFromMongo(map);
		}
	}

	private void preparerFetchPageParams(){
		earliest = String.valueOf(TimestampUtils.convertToSeconds(earliestDate.getTime()));
		Date pageLatestDate = DateUtils.addSeconds(earliestDate, fetchIntervalInSeconds);
		pageLatestDate = pageLatestDate.before(latestDate) ? pageLatestDate : latestDate;
		latest = String.valueOf(TimestampUtils.convertToSeconds(pageLatestDate.getTime()));
		//set for next page
		earliestDate = pageLatestDate;
	}

	private void updateMongoWithCurrentFetchProgress(){
		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(type);
		latest = TimestampUtils.convertSplunkTimeToUnix(latest);
		if(fetchConfiguration == null){
			fetchConfiguration = new FetchConfiguration(type, latest);
		}
		else {
			fetchConfiguration.setLastFetchTime(latest);
		}
		fetchConfigurationRepository.save(fetchConfiguration);

		if (earliestDate != null && latestDate != null) {
			if (earliestDate.after(latestDate) || earliestDate.equals(latestDate)) {
				keepFetching = false;
			}
		}
	}

	private void getRunTimeFrameFromMongo(JobDataMap map) throws JobExecutionException{
		type = jobDataMapExtension.getJobDataMapStringValue(map, "type");
		//time back (default 1 hour)
		fetchIntervalInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchIntervalInSeconds", 3600);
		int ceilingTimePartInt = jobDataMapExtension.getJobDataMapIntValue(map, "ceilingTimePartInt", Calendar.HOUR);
		int fetchDiffInSeconds = jobDataMapExtension.getJobDataMapIntValue(map, "fetchDiffInSeconds", 0);
		//set fetch until the ceiling of now (according to the given interval
		latestDate = DateUtils.ceiling(new Date(), ceilingTimePartInt);
		//shift the date by the configured diff
		latestDate = DateUtils.addSeconds(latestDate,-1*fetchDiffInSeconds);
		keepFetching = true;

		FetchConfiguration fetchConfiguration = fetchConfigurationRepository.findByType(type);
		if (fetchConfiguration != null) {
			earliest = fetchConfiguration.getLastFetchTime();
			earliestDate = new Date(TimestampUtils.convertToMilliSeconds(Long.parseLong(earliest)));
		}
		else {
			earliestDate = DateUtils.addSeconds(latestDate,-1*fetchIntervalInSeconds);
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

	private class MyListener implements Listener {

		@Override public void beforeConnect() {
			System.out.println("Connecting...");
		}

		@Override public void onConnected() {
			System.out.println("Connected");
			synchronized (PxGridFetchJob.this) {
				connected = true;
				PxGridFetchJob.this.notify();
			}
		}

		@Override public void onDisconnected() {
			if (connected) {
				System.out.println("Connection closed");
				connected = false;
			}
		}

		@Override public void onDeleted() {
			System.out.println("Account deleted");
		}

		@Override public void onDisabled() {
			System.out.println("Account disabled");
		}

		@Override public void onEnabled() {
			System.out.println("Account enabled");
		}

		@Override public void onAuthorizationChanged() {
			System.out.println("Authorization changed");
		}
	}
}
