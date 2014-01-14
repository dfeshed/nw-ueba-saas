package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class VpnOpenVpnTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "src/main/non-packaged-resources/conf-files/readVPN_openvpn.conf";
	private String[] vpnOutputFields = new String[] {"date_time","date_time_unixTime","username","source_ip","local_ip","status","message","country_name","host_name"};

	
	@Before
	public void setUp() throws Exception {
		morphlineTester.init(confFile, vpnOutputFields);
	}

	@Test
	@Parameters
	public void testVpnSingleLines(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}
	
	
	
	@SuppressWarnings("unused")
	private Object[] parametersForTestVpnSingleLines() {
        return
        $(
			$ (
        	"Regular Successful VPN Authentication",
			"Nov  6 07:55:40 openvpnas openvpnas: [-] OVPN 2 OUT: 'Wed Nov  6 05:55:40 2023 ross/79.122.200.58:53722 MULTI: primary virtual IP for ross/79.122.200.58:53722: 10.110.120.168'",
			"2023-11-06 05:55:40,1699250140,ross,79.122.200.58,10.110.120.168,SUCCESS,Nov  6 07:55:40 openvpnas openvpnas: [-] OVPN 2 OUT: 'Wed Nov  6 05:55:40 2023 ross/79.122.200.58:53722 MULTI: primary virtual IP for ross/79.122.200.58:53722: 10.110.120.168',Israel,"
			)
        );
    }

}