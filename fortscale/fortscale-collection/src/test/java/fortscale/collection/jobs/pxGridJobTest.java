package fortscale.collection.jobs;

import com.cisco.pxgrid.GridConnection;
import com.cisco.pxgrid.ReconnectionManager;
import com.cisco.pxgrid.TLSConfiguration;
import com.cisco.pxgrid.model.core.GenericAttribute;
import com.cisco.pxgrid.model.core.GenericAttributeValueType;
import com.cisco.pxgrid.model.core.IPInterfaceIdentifier;
import com.cisco.pxgrid.model.net.*;
import com.cisco.pxgrid.stub.identity.SessionDirectoryFactory;
import com.cisco.pxgrid.stub.identity.SessionDirectoryQuery;
import com.cisco.pxgrid.stub.identity.SessionIterator;
import fortscale.utils.test.category.HadoopTestCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Calendar;
import java.util.List;

public class pxGridJobTest {

	private String keystorePath = "src/test/resources/certificates/pxGrid/self1.jks";

	private String keystorePassphrase = "P@ssw0rd";

	private String truststorePath = "src/test/resources/certificates/pxGrid/rootSample.jks";
	private String truststorePassphrase = "P@ssw0rd";
	private String hosts = "fs-ise-02.fortscale.dom";
	private String userName = "Fortscale";
	private String group = "ANC";
	private int connectionRetryMillisecond = 2000;

	GridConnection con;
	ReconnectionManager recon;

	@Test public void testKeys() throws InstantiationException, IllegalAccessException {
		try {
			keystoreLoadTest(keystorePath, keystorePassphrase);
			keystoreLoadTest(truststorePath, truststorePassphrase);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}
	}

	private void keystoreLoadTest(String filename, String password) throws GeneralSecurityException, IOException {
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream(filename), password.toCharArray());
	}


	// Unit test to test connection to pxGrid

	@Test public void testConnection() throws InstantiationException, IllegalAccessException {
		try {
			connectToGrid();
			Calendar begin = Calendar.getInstance();
			begin.set(Calendar.YEAR, begin.get(Calendar.YEAR) - 1);
			//begin.setTimeInMillis(Long.parseLong(earliest));
			Calendar end = Calendar.getInstance();
			SessionDirectoryQuery sd = SessionDirectoryFactory.createSessionDirectoryQuery(con);
			SessionIterator iterator = sd.getSessionsByTime(begin, end);
			iterator.open();

			Session s;
			while ((s = iterator.next()) != null) {
				print(s);
			}
		}
		catch (Exception e) {
			Assert.assertTrue(false);
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

	private void connectToGrid() throws Exception {

		// configure the connection properties
		TLSConfiguration config = new TLSConfiguration();
		config.setHosts(new String[] { hosts });
		config.setUserName(userName);
		config.setGroup(group);
		config.setKeystorePath(keystorePath);
		config.setKeystorePassphrase(keystorePassphrase);
		config.setTruststorePath(truststorePath);
		config.setTruststorePassphrase(truststorePassphrase);

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
}
