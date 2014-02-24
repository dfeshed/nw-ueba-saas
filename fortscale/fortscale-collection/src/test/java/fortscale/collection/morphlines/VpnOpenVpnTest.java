package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

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
		morphlineTester.init(confFile, vpnOutputFields);
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
			"2023-11-06 07:55:40,1699242940,ross,79.122.200.58,10.110.120.168,SUCCESS,Russia,"
			),
			$ (
			"Regular (FS) Failed VPN Authentication",
			"Feb  8 19:12:47 vpnserver vpnserver: [-] OVPN 3 OUT: \"Sat Feb  8 17:12:47 2014 84.94.26.113:50976 SENT CONTROL [morgans]: 'AUTH_FAILED' (status=1)\"",
			null
//			TODO: When VPN failures are being processed, replace the expected null with the commented-out line			
//			"2014-02-08 17:12:47,1391879567,morgans,84.94.26.113,,FAIL,Feb  8 19:12:47 vpnserver vpnserver: [-] OVPN 3 OUT: \"Sat Feb  8 17:12:47 2014 84.94.26.113:50976 SENT CONTROL [morgans]: 'AUTH_FAILED' (status=1)\",Israel,"
			)
        );
    }	
	

}
