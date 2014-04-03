package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

import java.util.Arrays;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class DhcpDhcpdTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/read_DHCPD.conf";
	private String[] dhcpOutputFields = new String[] {"timestampepoch","ipaddress","hostname","macAddress"};

	
	@Before
	public void setUp() throws Exception {
		morphlineTester.init(new String[] { confFile }, Arrays.asList(dhcpOutputFields));
	}
	
	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
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
        		"Regular dhcpack #1",
				"Nov 19 23:59:54 server01 dhcpd: DHCPACK on 10.28.136.112 to 00:0d:0d:e8:72:c6 (APAC803F6) via eth0",
				"1416434394,10.28.136.112,APAC803F6,00:0d:0d:e8:72:c6"
				),
        		$ (
        		"Regular dhcpack #2",
				"Nov 19 23:59:56 server01 dhcpd: DHCPACK on 172.16.30.160 to e0:1d:41:04:7c:c0 (ML-retro-3cf-045dd0) via 10.136.76.250",
				"1416434396,172.16.30.160,ML-retro-3cf-045dd0,e0:1d:41:04:7c:c0"
				),
        		$ (
        		"Regular dhcpack with no hostname. Drop the record",
				"Nov 19 23:59:54 server01 dhcpd: DHCPACK on 10.28.192.236 to e0:cd:1d:17:7f:51 via eth0",
				null
				)
        		);
    }

}