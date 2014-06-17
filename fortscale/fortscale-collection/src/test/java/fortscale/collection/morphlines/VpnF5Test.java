package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fortscale.domain.events.dao.VpnSessionRepository;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;

@RunWith(JUnitParamsRunner.class)
//@ContextConfiguration(locations = {"classpath*:META-INF/spring/collection-context.xml"})
public class VpnF5Test {

	private static ClassPathXmlApplicationContext testContextManager;
	
	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readVPN_F5.conf";
		
	@BeforeClass
	public static void setUpClass(){
		testContextManager = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test.xml");
		VpnSessionRepository vpnSessionRepository = testContextManager.getBean(VpnSessionRepository.class);
		vpnSessionRepository.deleteAll();
	}
	
	@AfterClass
	public static void finalizeTestClass(){
		testContextManager.close();
		testContextManager = null;
	}
	 
	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.vpn.table.morphline.fields");
		List<String> vpnOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[] {confFile}, vpnOutputFields);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
		VpnSessionRepository vpnSessionRepository = testContextManager.getBean(VpnSessionRepository.class);
		vpnSessionRepository.deleteAll();
	}
	
	@Test
	@Parameters
	public void test(String testCase, Object[] lines, Object[] outputs) {
		
		List<String> events = new ArrayList<String>(lines.length);
		for (Object line : lines)
			events.add((String)line);
		
		List<String> expected = new ArrayList<String>(outputs.length);
		for (Object output : outputs)
			expected.add((String)output);
		
		morphlineTester.testMultipleLines(testCase, events , expected);
	}
	
	
	@SuppressWarnings("unused")
	private Object[] parametersForTest() {
        return	$(
    		$(
    			"Several new session with authentication (BS) VPN",
    			$(
					"Apr 14 00:17:42 va60tb01lba01dmz.black.com Apr 14 00:18:03 va60tb01lba01dmz notice tmm[20226]: 01490500:5: 18648c83: New session from client IP 66.249.64.46 (ST=California/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown)",
					"Apr 14 00:17:42 va60tb01lba01dmz.black.com Apr 14 00:18:03 va60tb01lba01dmz notice tmm[20226]: 01490500:5: 18648c83: New session from client IP 66.249.64.46 (ST=California/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown)",
					"Apr 14 01:50:26 server Apr 14 01:50:47 server info apd[18544]: 01490500:5: 18648c83: AD agent: Auth (logon attempt:0): authenticate with 'kamali123' failed",
					"Apr 14 00:23:29 va60tb01lba01dmz.black.com Apr 14 00:23:50 va60tb01lba01dmz notice tmm[20226]: 01490521:5: 18648c83: Session statistics - bytes in: 0, bytes out: 0"
    			),
    			$(
    				(String)null,
    				(String)null,
    				"2014-04-14 01:50:26,1397429426,kamali123,66.249.64.46,172.17.135.10,FAIL,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,",
    				(String)null
    			)
    		),
    		$(
	    		"Regular (BS) Successful VPN Authentication",
	    		$(
	    			"Jan  2 19:08:28 server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8781: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)",
	    			"Jan  2 19:08:35 server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8781: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful",
	    			"Jan  2 19:11:09 server.bs.dom Jan  2 19:11:31 server notice tmm2[20226]: 01490521:5: 49dc8781: Session statistics - bytes in: 632880, bytes out: 2649665"
				),
	    		$(
    				(String)null,
	    			"2014-01-02 19:08:35,1388682515,chavier,75.26.245.200,172.10.10.10,SUCCESS,,,,,,,,,,,,",
	    			"2014-01-02 19:11:09,1388682669,chavier,75.26.245.200,172.10.10.10,CLOSED,,,,,,,,3282545,2649665,632880,,50"
	    		)
    		),
    		$(
	    		"Regular (BS) Successful VPN Authentication in reverse order",
	    		$(
	    			"Jan  2 19:08:35 server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8782: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful",
	    			"Jan  2 19:08:28 server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8782: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)"
				),
	    		$(
    				(String)null,
	    			"2014-01-02 19:08:35,1388682515,chavier,75.26.245.200,172.10.10.10,SUCCESS,,,,,,,,,,,,"
	    		)
    		),
    		$(
	    		"Regular (BS) Successful VPN Authentication in reverse order more than day apart",
	    		$(
	    			"Jan  2 19:08:35 server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8788: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful",
	    			"Jan  4 19:08:28 server.bs.dom Jan  4 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8788: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)"
				),
	    		$(
    				(String)null,
    				(String)null
	    		)
    		),
    		$(
	    		"Regular (BS) Successful VPN Authentication in reverse order with end session",
	    		$(
	    			"Jan  2 19:08:35 server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8784: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful",
	    			"Jan  2 19:08:28 server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8784: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)",
	    			"Jan  2 19:11:09 server.bs.dom Jan  2 19:11:31 server notice tmm2[20226]: 01490521:5: 49dc8784: Session statistics - bytes in: 632880, bytes out: 2649665"
				),
	    		$(
    				(String)null,
	    			"2014-01-02 19:08:35,1388682515,chavier,75.26.245.200,172.10.10.10,SUCCESS,,,,,,,,,,,,",
	    			"2014-01-02 19:11:09,1388682669,chavier,75.26.245.200,172.10.10.10,CLOSED,,,,,,,,3282545,2649665,632880,,50"
	    		)
    		),
    		
    		$(
    	    	"Regular (BS) Failed VPN Authentication",
    	    	$(
    	    		"Jan  2 19:06:14 server.bs.dom Jan  2 19:07:42 server notice tmm2[20226]: 01490500:5: 8a38fa18: New session from client IP 69.141.27.100 (ST=New Jersey/CC=US/C=NA) at VIP 172.10.11.12 Listener /DETAILS/details (Reputation=Unknown)",
    	    		"Jan  2 19:06:26 server.bs.dom Jan  2 19:07:54 server info apd[18544]: 01490017:6: 8a38fa18: AD agent: Auth (logon attempt:0): authenticate with 'bartra' failed"
    			),
    	    	$(
        			(String)null,
    	    		"2014-01-02 19:06:26,1388682386,bartra,69.141.27.100,172.10.11.12,FAIL,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,"
    	    	)
        	),
        	
    		$(
	    		"Only First Event of Regular (BS) Successful VPN Authentication",
	    		$(
	    			"Jan  2 19:08:28 server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8783: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)"
				),
	    		$(
    				(String)null
	    		)
    		),
    		
    		$(
	    		"Only Second Event of Regular (BS) Successful VPN Authentication",
	    		$(
	    			"Jan  2 19:08:35 server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8798: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful"
				),
	    		$(
    				(String)null
	    		)
    		),
    		
    		$(
	    		"Regular (BS) Successful VPN Authentication From Last Year",
	    		$(
	    			"Dec  2 19:08:28 server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8785: New session from client IP 75.26.245.201 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)",
	    			"Dec  2 19:08:35 server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8785: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful"
				),
	    		$(
    				(String)null,
	    			"2013-12-02 19:08:35,1386004115,chavier,75.26.245.201,172.10.10.10,SUCCESS,,,,,,,,,,,,"
	    		)
    		),

    		$(
	    		"Regular (BS) VPN Session Statistics Event",
	    		$(
	    			"Feb 28 17:11:09 server.bs.dom Feb 28 17:11:31 server notice tmm2[20226]: 01490521:5: 0a6c7b51: Session statistics - bytes in: 632880, bytes out: 2649665"
				),
	    		$(
	    			(String) null
	    		)
    		),
    		
    		$(
    			"HTTP Agent authentication event should be dropped", // As we are using the AD agent authentication event to recognize this action
    			$(
    				"Mar  2 22:32:16 server.bs.dom Mar  2 22:32:16 server info apd[5904]: 01490139:6: 2275c32c: HTTP agent: authenticate with 'pinto' successful"
    			),
    			$(
    				(String)null
    			)
    		),
    		$(
    			"Failed and Success Regular (BS) VPN Authentications",
    			$(
					"Apr 14 01:50:05 server Apr 14 01:50:26 server notice tmm[20226]: 01490500:5: e83f3a7c: New session from client IP 71.125.52.63 (ST=New York/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown)",
					"Apr 14 01:50:26 server Apr 14 01:50:47 server info apd[18544]: 01490017:6: e83f3a7c: AD agent: Auth (logon attempt:0): authenticate with 'kamali123' failed",
					"Apr 14 01:50:42 server Apr 14 01:51:04 server info apd[18544]: 01490017:6: e83f3a7c: AD agent: Auth (logon attempt:1): authenticate with 'kamalij' successful"
    			),
    			$(
    				(String)null,
    				"2014-04-14 01:50:26,1397429426,kamali123,71.125.52.63,172.17.135.10,FAIL,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,",
    				"2014-04-14 01:50:42,1397429442,kamalij,71.125.52.63,172.17.135.10,SUCCESS,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,"
    			)
    		),
    		$(
    			"New session without authentication (BS) VPN",
    			$(
					"Apr 14 00:17:42 va60tb01lba01dmz.black.com Apr 14 00:18:03 va60tb01lba01dmz notice tmm[20226]: 01490500:5: 18648c73: New session from client IP 66.249.64.46 (ST=California/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown)",
					"Apr 14 00:17:42 va60tb01lba01dmz.black.com Apr 14 00:18:03 va60tb01lba01dmz notice tmm[20226]: 01490500:5: 18648c73: New session from client IP 66.249.64.46 (ST=California/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown)",
					"Apr 14 00:23:29 va60tb01lba01dmz.black.com Apr 14 00:23:50 va60tb01lba01dmz notice tmm[20226]: 01490521:5: 18648c73: Session statistics - bytes in: 0, bytes out: 0"
    			),
    			$(
    				(String)null,
    				(String)null,
    				(String)null
    			)
    		)
		);
    }

}