package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class DhcpDhcpdTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "src/main/resources/conf-files/read_DHCPD.conf";
	private String[] dhcpOutputFields = new String[] {"date_time","date_time_epoch","ip","hostname","mac_address"};

	
	@Before
	public void setUp() throws Exception {
		morphlineTester.init(confFile, dhcpOutputFields);
	}

	@Test
	@Parameters
	public void testDhcpSingleLines(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}
	
	
	
	@SuppressWarnings("unused")
	private Object[] parametersForTestDhcpSingleLines() {
        return	$(
        		$ (
        		"Regular dhcpack",
				"Nov 19 23:59:54 server01 dhcpd: DHCPACK on 10.28.136.112 to 00:0d:0d:e8:72:c6 (APAC803F6) via eth0",
				"2014-11-19 23:59:54,1416441594,10.28.136.112,APAC803F6,00:0d:0d:e8:72:c6"
				),
        		$ (
        		"Regular dhcpack",
				"Nov 19 23:59:56 server01 dhcpd: DHCPACK on 172.16.30.160 to e0:1d:41:04:7c:c0 (ML-retro-3cf-045dd0) via eth0",
				"2014-11-19 23:59:56,1416441596,172.16.30.160,ML-retro-3cf-045dd0,e0:1d:41:04:7c:c0"
				),
        		$ (
        		"Regular dhcpack with no hostname",
				"Nov 19 23:59:54 server01 dhcpd: DHCPACK on 10.28.192.236 to e0:cd:1d:17:7f:51 via eth0",
				"2014-11-19 23:59:54,1416441594,10.28.192.236,,e0:cd:1d:17:7f:51"
				)
        		);
    }

}