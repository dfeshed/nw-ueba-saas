package fortscale.collection.morphlines.ipresolving;

import static junitparams.JUnitParamsRunner.$;

import java.util.Arrays;

import fortscale.collection.morphlines.MorphlinesTester;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fortscale.domain.events.DhcpEvent;

@RunWith(JUnitParamsRunner.class)
public class CiscoDHCPTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/ipresolving/parseCiscoDHCP.conf";
	private String[] dhcpOutputFields = new String[] {DhcpEvent.TIMESTAMP_EPOCH_FIELD_NAME,DhcpEvent.ACTION_FIELD_NAME,DhcpEvent.IP_ADDRESS_FIELD_NAME
			,DhcpEvent.HOSTNAME_FIELD_NAME,DhcpEvent.MAC_ADDRESS_FIELD_NAME,DhcpEvent.EXPIRATION_FIELD_NAME};
	

	@SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }
	
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
        		"ip assignment event",
        		"2014-11-08T00:00:00.000+00:00,,171.68.24.166,router-ubuntu02,,11/10/2014 21:31:41 UTC",
				"1415404800,ASSIGN,171.68.24.166,router-ubuntu02,,1415655101"
				),
				$ (
				"ip assignment event - with WAN enrichment ",
				"2014-11-08T00:00:00.000+00:00,,171.68.24.166,router-ubuntu02,,11/10/2014 21:31:41 UTC Flume enrichment timezone Asia/Jerusalem",
				"1415404800,ASSIGN,171.68.24.166,router-ubuntu02,,1415655101"
				),
				$ (
        		"RELEASED ip event",
        		"2014-11-08T00:00:00.000+00:00,RELEASED,171.68.24.166,router-ubuntu02,e8:b7:48:2c:61:2b,",
				"1415404800,RELEASED,171.68.24.166,router-ubuntu02,e8:b7:48:2c:61:2b,"
				),
				$(
				"EXPIRED ip event",
				"2014-11-08T00:00:00.000+00:00,EXPIRED,171.68.24.166,router-ubuntu02,3c:a9:f4:64:eb:84,",
				"1415404800,EXPIRED,171.68.24.166,router-ubuntu02,3c:a9:f4:64:eb:84,"
				),
				$(
				"time should be parsed according to input field timezone (expiration and assignment) (FV-6251)",
				"2015-04-12T12:00:00.000+03:00,,192.168.170.1,dhcpusr0_PC,3c:a9:f4:64:eb:84,04/14/2015 7:00:00 IDT",
				"1428829200,ASSIGN,192.168.170.1,dhcpusr0_PC,3c:a9:f4:64:eb:84,1428984000"
				),
				$(
						"time should be parsed according to input field timezone (expiration and assignment) (FV-6251)",
						"2015-04-12T12:00:00.000+03:00,ASSIGN,192.168.170.1,dhcpusr0_PC,3c:a9:f4:64:eb:84,04/14/2015 7:00:00 IDT",
						"1428829200,ASSIGN,192.168.170.1,dhcpusr0_PC,3c:a9:f4:64:eb:84,1428984000"
				),
				$(
						"time should be parsed according to input field timezone (expiration and assignment) (FV-6251)",
						"2015-04-12T12:00:00.000+03:00,assign,192.168.170.1,dhcpusr0_PC,3c:a9:f4:64:eb:84,04/14/2015 7:00:00 IDT",
						"1428829200,assign,192.168.170.1,dhcpusr0_PC,3c:a9:f4:64:eb:84,1428984000"
				)
        		);
    }

}
