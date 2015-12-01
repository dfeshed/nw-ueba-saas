package fortscale.collection.jobs;

import com.cisco.pxgrid.GridConnection;
import com.cisco.pxgrid.ReconnectionManager;
import com.cisco.pxgrid.TLSConfiguration;
import com.cisco.pxgrid.model.ise.metadata.EndpointProfile;
import com.cisco.pxgrid.stub.isemetadata.EndpointProfileClientStub;
import com.cisco.pxgrid.stub.isemetadata.EndpointProfileQuery;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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
		keystorePath = jobDataMapExtension.getJobDataMapStringValue(map, "keystorePath");
		keystorePassphrase = jobDataMapExtension.getJobDataMapStringValue(map, "keystorePassphrase");
		truststorePath = jobDataMapExtension.getJobDataMapStringValue(map, "truststorePath");
		truststorePassphrase = jobDataMapExtension.getJobDataMapStringValue(map, "truststorePassphrase");
		connectionRetryMillisecond = jobDataMapExtension.getJobDataMapIntValue(map, "connectionRetryMillisecond");
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
