package fortscale.utils.splunk;

import org.junit.BeforeClass;
import org.junit.Test;

public class SplunkTest {
	
	private static final String SPLUNK_SERVER_HOST_NAME_PROPERTY = "splunkServer";
	private static final String SPLUNK_SERVER_PORT_PROPERTY = "splunkPort";
	private static final String SPLUNK_SERVER_USER_NAME_PROPERTY = "splunkUser";
	private static final String SPLUNK_SERVER_USER_PASSWORD_PROPERTY = "splunkPassword";
	
	private static String host = "192.168.0.135";
	private static int port = 8089;
	private static String user = "admin";
	private static String password = "P@ssw0rd";
	
	@BeforeClass
	public static void setUpBeforeClass(){
		String tmp = null;
		
		tmp = System.getProperty(SPLUNK_SERVER_HOST_NAME_PROPERTY);
		if(tmp != null){
			host = tmp;
		}
		
		tmp = System.getProperty(SPLUNK_SERVER_PORT_PROPERTY);
		if(tmp != null){
			port = Integer.parseInt(tmp);
		}
		
		tmp = System.getProperty(SPLUNK_SERVER_USER_NAME_PROPERTY);
		if(tmp != null){
			user = tmp;
		}
		
		tmp = System.getProperty(SPLUNK_SERVER_USER_PASSWORD_PROPERTY);
		if(tmp != null){
			password = tmp;
		}
	}

	@Test
	public void testSplunkConnect(){
		@SuppressWarnings("unused")
		SplunkApi splunkApi = new SplunkApi(host, port, user, password);
	}
	
}
