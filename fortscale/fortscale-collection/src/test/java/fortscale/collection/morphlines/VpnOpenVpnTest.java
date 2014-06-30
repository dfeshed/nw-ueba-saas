package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

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
public class VpnOpenVpnTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readVPN_openVPN.conf";
	
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
	public void test(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}
	
	
	@SuppressWarnings("unused")
	private Object[] parametersForTest() {
        return
        $(
			$ (
        	"Regular (FS) Successful VPN Authentication",
			"Nov  6 07:55:40 vpnserver vpnserver: [-] OVPN 2 OUT: 'Wed Nov  6 05:55:40 2023 ross/79.122.200.58:53722 MULTI: primary virtual IP for ross/79.122.200.58:53722: 10.110.120.168'",
			"2023-11-06 05:55:40,1699242940,ross,79.122.200.58,10.110.120.168,SUCCESS,,,,,,,,,,,,,false,false"
			),
			$ (
			"Regular (FS) Failed VPN Authentication",
			"Feb  8 19:12:47 vpnserver vpnserver: [-] OVPN 3 OUT: \"Sat Feb  8 17:12:47 2014 84.94.26.113:50976 SENT CONTROL [morgans]: 'AUTH_FAILED' (status=1)\"",
			"2014-02-08 17:12:47,1391872367,morgans,84.94.26.113,,FAIL,,,,,,,,,,,,,false,false"
			),
			$ (
			"Regular (FS) Closed VPN",
			"<14>Apr  6 08:40:28 openvpnas openvpnas: [-] OVPN 3 OUT: 'Sun Apr  6 05:40:28 2014 YaronDL/77.126.216.55:64441 TLS: soft reset sec=-1 bytes=314561084/0 pkts=727457/0'",
			"2014-04-06 05:40:28,1396752028,YaronDL,77.126.216.55,,CLOSED,,,,,,,,314561084,314561084,,,400,false,false"
			),
			$ (
			"Closed VPN",
			"<14>Apr  8 12:14:39 openvpnas openvpnas: [-] OVPN 2 OUT: 'Tue Apr  8 09:14:39 2014 dotanp/84.94.86.213:51078 SIGTERM[soft,remote-exit] received, client-instance exiting'",
			"2014-04-08 09:14:39,1396937679,dotanp,84.94.86.213,,CLOSED,,,,,,,,,,,,50,false,false"
			)
        );
    }	
	

}