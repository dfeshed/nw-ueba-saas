package fortscale.collection.jobs;

import com.cisco.pxgrid.GridConnection;
import com.cisco.pxgrid.GridConnection.Listener;
import com.cisco.pxgrid.ReconnectionManager;
import com.cisco.pxgrid.TLSConfiguration;
import com.cisco.pxgrid.model.core.GenericAttribute;
import com.cisco.pxgrid.model.core.GenericAttributeValueType;
import com.cisco.pxgrid.model.core.IPInterfaceIdentifier;
import com.cisco.pxgrid.model.ise.metadata.EndpointProfile;
import com.cisco.pxgrid.model.net.*;
import com.cisco.pxgrid.stub.identity.*;
import com.cisco.pxgrid.stub.isemetadata.EndpointProfileClientStub;
import com.cisco.pxgrid.stub.isemetadata.EndpointProfileQuery;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Calendar;
import java.util.List;


/**
 * Created by tomerd on 27/11/2015.
 */
public class PxGridFetchJob extends FortscaleJob {

	private String filenameFormat;
	private String hosts;
	private String userName;
	private String group;
	private String keystorePath;
	private String keystorePassphrase;
	private String truststorePath;
	private String truststorePassphrase;
	private int connectionRetryMillisecond;

	private boolean connected;

	@Override protected int getTotalNumOfSteps() {
		return 3;
	}

	@Override protected boolean shouldReportDataReceived() {
		return true;
	}

	@Override protected void runSteps() throws Exception {

		// configure the connection properties
		TLSConfiguration config = createConfigObject();

		// establishing a connection with the pxGrid controller
		GridConnection con = new GridConnection(config);
		con.addListener(new MyListener());

		ReconnectionManager recon = new ReconnectionManager(con);
		recon.setRetryMillisecond(connectionRetryMillisecond);
		recon.start();

		// Wait for the connection to establish
		while (!con.isConnected()) {
			Thread.sleep(100);
		}

		// create query we'll use to make call

		/*
		EndpointProfileClientStub stub = new EndpointProfileClientStub(con);
		EndpointProfileQuery query = stub.createEndpointProfileQuery();

		List<EndpointProfile> dps = query.getEndpointProfiles();
		if (dps != null) {
			EndpointProfile dp;
			for (Iterator<EndpointProfile> it = dps.iterator(); it.hasNext();) {
				dp = it.next();
				System.out.println("Endpoint Profile : id=" + dp.getId() + ", name=" +  dp.getName() + ", fqname " + dp.getFqname());
			}
		}*/




		IdentityGroupQuery id = SessionDirectoryFactory.createIdentityGroupQuery(con);
		Iterator<User> iterator = id.getIdentityGroups();
		iterator.open();

		int count = 0;
		User u;
		while ((u = iterator.next()) != null) {
			System.out.println("user=" + u.getName() + " groups=" + u.getGroupList().getObjects().get(0).getName());
			count++;
		}
		iterator.close();


		Calendar begin = Calendar.getInstance();
		begin.set(Calendar.YEAR, begin.get(Calendar.YEAR) - 1);
		Calendar end = Calendar.getInstance();
		SessionDirectoryQuery sd = SessionDirectoryFactory.createSessionDirectoryQuery(con);
		SessionIterator iterator2 = sd.getSessionsByTime(begin, end);
		iterator2.open();

		Session s;
		while ((s = iterator2.next()) != null) {
			print(s);
		}
		iterator.close();

		// disconnect from pxGrid
		recon.stop();
	}

	private void print(Session session) {
		System.out.print("Session={");

		List<IPInterfaceIdentifier> intfIDs = session.getInterface().getIpIntfIDs();
		System.out.print("ip=[");
		for (int i = 0; i < intfIDs.size(); i++) {
			if (i > 0) System.out.print(",");
			System.out.print(intfIDs.get(i).getIpAddress());
		}
		System.out.print("]");

		System.out.print(", Audit Session Id=" +session.getGid());
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
		System.out.print(", Security Group=" +  session.getSecurityGroup());
		System.out.print(", Endpoint Profile=" +  session.getEndpointProfile());

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
			for(RADIUSAVPair p : radiusAVPairs) {
				System.out.print(" " + p.getAttrName() + "=" + p.getAttrValue() );
			}
			System.out.print("]");
		}

		// Posture Info
		List<PostureAssessment> postures = session.getAssessedPostureEvents();
		if (postures != null && postures.size() > 0) {
			System.out.print(", Posture Status=" + postures.get(0).getStatus());

			Calendar cal  = postures.get(0).getLastUpdateTime();
			System.out.print(", Posture Timestamp=" + ((cal != null) ? cal.getTime(): ""));

		}
		System.out.print(", Session Last Update Time=" + session.getLastUpdateTime().getTime());
		//Get Generic Attributes
		List<GenericAttribute> attributes= session.getExtraAttributes();
		for(GenericAttribute attrib: attributes) {

			System.out.print(", Session attributeName=" + attrib.getName());
			if(attrib.getType()== GenericAttributeValueType.STRING) {
				String attribValue = null;
				try {
					attribValue = new String(attrib.getValue(),"UTF-8");
				} catch (UnsupportedEncodingException e) {

					e.printStackTrace();
				}
				System.out.print(", Session attributeValue=" + attribValue);
			}
		}
		System.out.println("}");
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
		}
		catch (Exception e){
			throw new JobExecutionException("Error loading keys; Error: " + e.getMessage());
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

		@Override
		public void beforeConnect() {
			System.out.println("Connecting...");
		}

		@Override
		public void onConnected() {
			System.out.println("Connected");
			synchronized (PxGridFetchJob.this) {
				connected = true;
				PxGridFetchJob.this.notify();
			}
		}

		@Override
		public void onDisconnected() {
			if (connected) {
				System.out.println("Connection closed");
				connected = false;
			}
		}

		@Override
		public void onDeleted() {
			System.out.println("Account deleted");
		}

		@Override
		public void onDisabled() {
			System.out.println("Account disabled");
		}

		@Override
		public void onEnabled() {
			System.out.println("Account enabled");
		}

		@Override
		public void onAuthorizationChanged() {
			System.out.println("Authorization changed");
		}
	}
}
