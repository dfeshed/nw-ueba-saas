package fortscale.collection.jobs;

import com.cisco.pxgrid.GridConnection;
import com.cisco.pxgrid.GridConnection.Listener;
import com.cisco.pxgrid.ReconnectionManager;
import com.cisco.pxgrid.TLSConfiguration;
import com.cisco.pxgrid.model.ise.metadata.EndpointProfile;
import com.cisco.pxgrid.stub.isemetadata.EndpointProfileClientStub;
import com.cisco.pxgrid.stub.isemetadata.EndpointProfileQuery;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Iterator;
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

		EndpointProfileClientStub stub = new EndpointProfileClientStub(con);
		EndpointProfileQuery query = stub.createEndpointProfileQuery();

		List<EndpointProfile> dps = query.getEndpointProfiles();
		if (dps != null) {
			EndpointProfile dp;
			for (Iterator<EndpointProfile> it = dps.iterator(); it.hasNext(); ) {
				dp = it.next();
				System.out.println("Endpoint Profile : id=" + dp.getId() + ", name=" + dp.getName() + ", fqname " + dp.getFqname());
			}
		}

		// disconnect from pxGrid
		recon.stop();
	}

	protected void getJobParameters(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getMergedJobDataMap();

		filenameFormat = jobDataMapExtension.getJobDataMapStringValue(map, "filenameFormat");
		hosts = jobDataMapExtension.getJobDataMapStringValue(map, "hosts");
		userName = jobDataMapExtension.getJobDataMapStringValue(map, "userName");
		group = jobDataMapExtension.getJobDataMapStringValue(map, "group");
		//keystorePath = jobDataMapExtension.getJobDataMapStringValue(map, "keystorePath");
		keystorePath = "resources/certificates/pxGrid/clientSample2.jks";
		keystorePassphrase = jobDataMapExtension.getJobDataMapStringValue(map, "keystorePassphrase");
		//truststorePath = jobDataMapExtension.getJobDataMapStringValue(map, "truststorePath");
		truststorePath = "resources/certificates/pxGrid/rootSample.jks";
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
