package fortscale.collection.jobs;

import com.cisco.pxgrid.GridConnection;
import com.cisco.pxgrid.ReconnectionManager;
import com.cisco.pxgrid.TLSConfiguration;
import com.cisco.pxgrid.model.net.Session;
import com.cisco.pxgrid.stub.identity.SessionDirectoryFactory;
import com.cisco.pxgrid.stub.identity.SessionDirectoryQuery;
import com.cisco.pxgrid.stub.identity.SessionIterator;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Calendar;

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

	@Override
	protected int getTotalNumOfSteps() {
		return 3;
	}

	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}

	@Override
	protected void runSteps() throws Exception {

		// configure the connection properties
		TLSConfiguration config = createConfigObject();

		// establishing a connection with the pxGrid controller
		GridConnection con = new GridConnection(config);
		ReconnectionManager recon = new ReconnectionManager(con);
		recon.setRetryMillisecond(connectionRetryMillisecond);
		recon.start();

		// construct a date range and request those sessions
		Calendar begin = Calendar.getInstance();
		begin.set(Calendar.HOUR, begin.get(Calendar.HOUR) - 1);
		Calendar end = Calendar.getInstance();

		SessionDirectoryQuery query = SessionDirectoryFactory.createSessionDirectoryQuery(con);
		SessionIterator iterator = query.getSessionsByTime(begin, end);
		iterator.open();

		Session session = iterator.next();
		while (session != null) {
			System.out.println("received session: " + session.getGid());
			session = iterator.next();
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

	private TLSConfiguration createConfigObject(){
		TLSConfiguration config = new TLSConfiguration();
		config.setHosts(new String[]{hosts});
		config.setUserName(userName);
		config.setGroup(group);
		config.setKeystorePath(keystorePath);
		config.setKeystorePassphrase(keystorePassphrase);
		config.setTruststorePath(truststorePath);
		config.setTruststorePassphrase(truststorePassphrase);

		return  config;
	}
}
