package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;

@RunWith(JUnitParamsRunner.class)
public class VpnJuniperTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readVPN_juniper.conf";

	
	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.vpn.table.morphline.fields");
		List<String> vpnOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[] { confFile }, vpnOutputFields);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
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
        		"Regular (Poza) Failure VPN Authentication",
        		$(
        			"Mar 20 07:11:35 192.168.199.2 Juniper: 2014-03-20 07:11:36 - ive - [84.94.86.213] dcr(SecID Users)[] - Login failed using auth server Poza SecurID (ACE Server). Reason: Failed"   
        		),
        		$(
        			"2014-03-20 07:11:36,1395292296,dcr,84.94.86.213,,FAIL,,,,,,,,,,,,,false"
        		)
        	),
        	$(
    		"Regular (Poza) Close VPN",
    		$(
    			"Feb 12 11:56:32 10.1.150.10 Juniper: 2014-02-12 11:56:36 - ch-vpn-prilly - [85.132.48.198] cfankhause(Employees-OTP)[Employees_Common, Employees_Pulse] - Closed connection to 10.1.151.24 after 55 seconds, with 119991 bytes read and 110702 bytes written"   
    		),
    		$(
    			"2014-02-12 11:56:36,1392198996,cfankhause,85.132.48.198,10.1.151.24,CLOSED,,,,,,,,230693,119991,110702,55,50,false"
    		)
        	),
        	$(
            	"Regular (Poza) Failure VPN Authentication",
            	$(
            		"Nov  7 14:36:10 11.155.45.2 Juniper: 2013-11-07 14:36:10 - ive - [72.193.146.27] bdes(Users)[] - Login failed using auth server server.mypozza.com (ACE Server).  Reason: Failed"   
            	),
        		$(
        			"2013-11-07 14:36:10,1383827770,bdes,72.193.146.27,,FAIL,,,,,,,,,,,,,false"
        		)
        	),
    		$(
	    		"Regular (Poza) Successful VPN Authentication",
	    		$(
	    			"Mar 16 04:17:26 192.168.199.2 Juniper: 2014-03-16 04:17:26 - ive - [82.166.88.97] omendelso-contractor(SecurID Users)[Users, Poza Users] - Agent login succeeded for omendelso-contractor/SecurID Users from 82.166.88.97.",
	    			"Mar 16 04:17:26 192.168.199.2 Juniper: 2014-03-16 04:17:26 - ive - [82.166.88.97] omendelso-contractor(SecurID Users)[Users, Poza Users] - VPN Tunneling: Session started for user with IP 10.49.253.16, hostname ORI-PC"   
				),
	    		$(
    				(String)null,
    				"2014-03-16 04:17:26,1394936246,omendelso-contractor,82.166.88.97,10.49.253.16,SUCCESS,,,,,,,ORI-PC,,,,,,false"
	    		)
    		),
    		$(
				"Single login event (Poza) with no tunnel should have no output",
	    		$(
					"Mar 16 04:17:26 192.168.199.2 Juniper: 2014-03-16 04:17:26 - ive - [82.166.88.97] omendelso-contractor(SecurID Users)[Users, Poza Users] - Agent login succeeded for omendelso-contractor/SecurID Users from 82.166.88.97."
				),
				$(
					(String)null
				)
			),
			$(
				"Multiple Regular (Poza) Successful VPN Authentication should have output only on last event",
	    		$(
		    		"Mar 16 04:17:26 192.168.199.2 Juniper: 2014-03-16 04:17:26 - ive - [82.166.88.97] omendelso-contractor(SecurID Users)[Users, Poza Users] - Agent login succeeded for omendelso-contractor/SecurID Users from 82.166.88.97.",
		    		"Mar 16 04:17:26 192.168.199.2 Juniper: 2014-03-16 04:17:26 - ive - [82.166.88.97] omendelso-contractor(SecurID Users)[Users, Poza Users] - VPN Tunneling: Session started for user with IP 10.49.253.16, hostname ORI-PC",
		    		"Mar 16 04:17:26 192.168.199.2 Juniper: 2014-03-16 04:17:26 - ive - [82.166.88.97] omendelso-contractor(SecurID Users)[Users, Poza Users] - VPN Tunneling: Session started for user with IP 10.49.253.16, hostname ORI-PC"
    			),
				$(
					(String)null,
					"2014-03-16 04:17:26,1394936246,omendelso-contractor,82.166.88.97,10.49.253.16,SUCCESS,,,,,,,ORI-PC,,,,,,false",
					(String)null
				)
			),
            $(
        		"Successful Empty Username VPN Authentication (Should be dropped)",
        		$("Nov 7 14:41:28 11.155.45.2 Juniper: 2013-11-07 14:41:28 - ive - [208.91.35.4] ser:gio-contractor(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.100, hostname victory.local"),
        		$((String)null)
            ),
            
            $ (
            	"Regular (PT) Successful VPN Authentication",
        		$(
        			"info - [62.219.118.133] - baxishk(Company Users)[Group a,Group b] - 2011/06/10 09:57:18 - Agent login succeeded for baxishk/Company Users from 62.219.118.133.",        				
        			"info - [62.219.118.133] - baxishk(Company Users)[Group a,Group b] - 2011/06/10 09:57:18 - VPN Tunneling: Session started for user with IPv4 address 10.122.65.1, hostname SWAN"
        		),
        		$((String)null, "2011-06-10 09:57:18,1307689038,baxishk,62.219.118.133,10.122.65.1,SUCCESS,,,,,,,SWAN,,,,,,false")
            ),
            $ (
            	"Regular (Poza) Successful VPN Authentication",
            	$(
            		"Nov  7 14:37:04 11.155.45.2 Juniper: 2013-11-07 14:37:05 - ive - [72.193.146.27] bvaldes(Users)[Users] - Agent login succeeded for bvaldes/Users from 72.193.146.27.",
            		"Nov  7 14:37:04 11.155.45.2 Juniper: 2013-11-07 14:37:05 - ive - [72.193.146.27] bvaldes(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.113, hostname LAPTOP-20005507"
            	),
            	$((String)null, "2013-11-07 14:37:05,1383827825,bvaldes,72.193.146.27,11.155.46.113,SUCCESS,,,,,,,LAPTOP-20005507,,,,,,false")
            ),
             $ (
            	"Regular (Poza) Successful VPN Authentication for mac with dns suffix",
            	$(
            		"Nov  7 14:37:04 11.155.45.2 Juniper: 2013-11-07 14:37:05 - ive - [72.193.146.27] bvaldes(Users)[Users] - Agent login succeeded for bvaldes/Users from 72.193.146.27.",
            		"Nov  7 14:37:04 11.155.45.2 Juniper: 2013-11-07 14:37:05 - ive - [72.193.146.27] bvaldes(Users)[Users] - VPN Tunneling: Session started for user with IP 11.155.46.113, hostname LAPTOP-20005507.local"
            	),
            	$((String)null, "2013-11-07 14:37:05,1383827825,bvaldes,72.193.146.27,11.155.46.113,SUCCESS,,,,,,,LAPTOP-20005507,,,,,,false")
            ),
            $(
            		"Regular (Poza) Successful VPN Authentication",
            		$(
            				"Jun 25 02:30:44 192.168.199.2 Juniper: 2014-06-25 02:30:44 - ive - [127.0.0.1] System()[] - Agent login succeeded for scarletj/SecurID Users + Machine Cert Host Check from 87.247.232.212.",
            				"Jun 25 02:30:44 192.168.199.2 Juniper: 2014-06-25 02:30:45 - ive - [87.247.232.212] scarletj(SecurID Users + Machine Cert Host Check)[Users, Fortscale Users] - VPN Tunneling: Session started for user with IP 11.29.253.18, hostname L-25002436"
            		),
            		$(
            				(String)null,
            				"2014-06-25 02:30:44,1403652644,scarletj,87.247.232.212,11.29.253.18,SUCCESS,,,,,,,L-25002436,,,,,,false"
            		)
            )
		);
    }

}