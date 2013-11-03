package fortscale.utils.splunk;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.splunk.HttpException;

public class SplunkTest {
	
	private static final String SPLUNK_SERVER_HOST_NAME_PROPERTY = "splunkServer";
	private static final String SPLUNK_SERVER_PORT_PROPERTY = "splunkPort";
	private static final String SPLUNK_SERVER_USER_NAME_PROPERTY = "splunkUser";
	private static final String SPLUNK_SERVER_USER_PASSWORD_PROPERTY = "splunkPassword";
	
	private String host = "mine.fortscale.dom";
	private int port = 8089;
	private String user = "admin";
	private String password = "P@ssw0rd";
	
	@Before
	public void setUpBeforeClass(){
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
	
	@Test
	public void testWrongSplunkLoginParameters(){
		try{
			@SuppressWarnings("unused")
			SplunkApi splunkApi = new SplunkApi(host, port, user, "bala");
			Assert.fail("SocketException has not been thrown.");
		} catch(HttpException e){
			if(!"HTTP 401 -- Login failed".equals(e.getMessage())){
				Assert.fail("got the right exception but the wrong message: " + e.toString());
			}
		} catch(Exception e){
			e.printStackTrace();
			Assert.fail("got the wrong exception: " + e.toString());
		}
	}
}
