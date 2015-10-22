package fortscale.collection.morphlines;

import fortscale.utils.junit.SpringAware;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Date;

import static junitparams.JUnitParamsRunner.$;

@RunWith(Parameterized.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class,
		initializers = PropertyMockingLocalTimezoneApplicationContextInitializer.class,
		locations = {"classpath*:META-INF/spring/collection-context-test-light-local-timezone.xml"})
//used to clean spring context for next class:
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DhcpDhcpdLocalTimezoneTest {

	//rules used to set JUnit parameters in SpringAware
	@ClassRule
	public static final SpringAware SPRING_AWARE = SpringAware.forClass(DhcpDhcpdLocalTimezoneTest.class);
	@Rule
	public TestRule springAwareMethod = SPRING_AWARE.forInstance(this);
	@Rule
	public TestName testName = new TestName();

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/read_DHCPD.conf";
	private String[] dhcpOutputFields = new String[] {"timestampepoch","ipaddress","hostname","macAddress"};

	String testCase;
	String line;
	String output;
	public DhcpDhcpdLocalTimezoneTest(String testCase, String line, String output){
		this.testCase = testCase;
		this.line = line;
		this.output = output;
	}


	final static String Nov_19_23_59_54 = "Nov 19 23:59:54";
	static Long Nov_19_23_59_54_L;
	final static String Nov_19_23_59_56 = "Nov 19 23:59:56";
	static Long Nov_19_23_59_56_L;

	static {
		prepareDates();
	}

	private static void prepareDates() {
		//this test works in Jerusalem timezone
		TestUtils.init("yyyy MMM dd HH:mm:ss", "Asia/Jerusalem");

		Date date = TestUtils.constuctDate(Nov_19_23_59_54);
		Nov_19_23_59_54_L = TestUtils.getUnixDate(date);

		date = TestUtils.constuctDate(Nov_19_23_59_56);
		Nov_19_23_59_56_L = TestUtils.getUnixDate(date);

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
	@Parameters(name = "{index} {1}")
	public void testDhcpSingleLines() {
		morphlineTester.testSingleLine(testCase, line, output);
	}



	@Parameters()
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][]
						{
								{
										"Regular dhcpack #1",
										Nov_19_23_59_54 + " server01 dhcpd: DHCPACK on 10.28.136.112 to 00:0d:0d:e8:72:c6 (APAC803F6) via eth0",
										Nov_19_23_59_54_L + ",10.28.136.112,APAC803F6,00:0d:0d:e8:72:c6"
								},

								{
										"Regular dhcpack #1 - with WAN enrichment",
										Nov_19_23_59_54 + " server01 dhcpd: DHCPACK on 10.28.136.112 to 00:0d:0d:e8:72:c6 (APAC803F6) via eth0 Flume enrichment timezone Asia/Jerusalem",
										Nov_19_23_59_54_L + ",10.28.136.112,APAC803F6,00:0d:0d:e8:72:c6"
								},
								{
										"Regular dhcpack #2",
										Nov_19_23_59_56 + " server01 dhcpd: DHCPACK on 172.16.30.160 to e0:1d:41:04:7c:c0 (ML-retro-3cf-045dd0) via 10.136.76.250",
										Nov_19_23_59_56_L + ",172.16.30.160,ML-retro-3cf-045dd0,e0:1d:41:04:7c:c0"
								},
								{
										"Regular dhcpack with no hostname. Drop the record",
										Nov_19_23_59_54 + " server01 dhcpd: DHCPACK on 10.28.192.236 to e0:cd:1d:17:7f:51 via eth0",
										null
								}
						}
        		);
    }

}