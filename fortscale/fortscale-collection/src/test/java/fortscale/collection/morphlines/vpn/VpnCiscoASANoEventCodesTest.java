package fortscale.collection.morphlines.vpn;

import fortscale.collection.FsParametrizedMultiLineTest;
import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(Parameterized.class)
//@ContextConfiguration(loader = SpringockitoContextLoader.class,
//		locations = {"classpath*:META-INF/spring/collection-context-test-mocks.xml"})
//used to clean spring context for next class:
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class VpnCiscoASANoEventCodesTest extends FsParametrizedMultiLineTest {

	private static ClassPathXmlApplicationContext testContextManager;

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/vpn/readVPN_ASA_Cisco_NoEventCodes.conf";

	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpClass() {
		new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-mocks.xml");
	}

	public VpnCiscoASANoEventCodesTest(String testCase, Object[] lines, Object[] outputs) {
		super(testCase, lines, outputs);
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
	}

	@Test
	@Parameters(name = "{index} {1}")
	public void test() {

		List<String> events = new ArrayList<String>(lines.length);
		for (Object line : lines)
			events.add((String)line);

		List<String> expected = new ArrayList<String>(outputs.length);
		for (Object output : outputs)
			expected.add((String)output);

		morphlineTester.testMultipleLines(testCase, events , expected);
	}


	@Parameters()
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {

				{ "Start of Session - 'First TCP Session' followed by 'assigned to session' at the same unix time",
					$("2014-12-14T00:17:26.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> First TCP SVC connection established for SVC session.",
					"2014-12-14T00:17:26.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
					"2014-12-14T00:17:56.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
					"2014-12-14T01:17:26.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session"),

					$((String) null, "2014-12-14 00:17:26,1418516246,poorman,73.189.60.63,10.72.116.99,SUCCESS,,,,,,,,,,,,,,,",
					(String) null,
					(String) null)
				},

				{ "Start of Session - 'First TCP Session' followed by 'assigned to session' after a few seconds",
				$("2014-12-14T00:17:20.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> First TCP SVC connection established for SVC session.",

				"2014-12-14T00:17:26.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session"),
				$((String) null, "2014-12-14 00:17:26,1418516246,poorman,73.189.60.63,10.72.116.99,SUCCESS,,,,,,,,,,,,,,,") },

				{ "Start of Session - 'assigned to session' with no preceding 'First TCP Session'", $("2014-12-14T00:17:26.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session"),
				$((String) null) },

				{ "Failed Session - 'User authentication failed'",
				$("2014-12-14T23:59:46.000+00:00|device-id=gi-0-1.rav2-1-gci.sydney.corp.fortscale.com User authentication failed: Uname: admin", "2014-12-14T23:59:46.000+00:00|device-id=gi-0-1.rav2-1-gci.sydney.corp.fortscale.com User authentication failed: Uname: "),
				$("2014-12-14 23:59:46,1418601586,admin,,,FAIL,,,,,,,,,,,,,,,", (String) null) },

				{ "Failed Session - 'user authentication Rejected'",
				$("2014-12-14T23:59:43.000+00:00|device-id=gi-0-1.rav2-1-gci.eglbp.corp.fortscale.com AAA user authentication Rejected : reason = Invalid password : local database : user = admin",
				"2014-12-14T23:59:44.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.tw1.fortscale.com AAA user authentication Rejected : reason = AAA failure : server = 67.195.88.201 : user = niniyni",
				"2014-12-14T23:59:44.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.tw1.fortscale.com AAA user authentication Rejected : reason = AAA failure : server = 67.195.88.201 : user = "),
				$("2014-12-14 23:59:43,1418601583,admin,,,FAIL,,,,,,,,,,,,,,,",
				"2014-12-14 23:59:44,1418601584,niniyni,,,FAIL,,,,,,,,,,,,,,,", (String) null) },

				{ "Session Ended - 'Session disconnected'",
				$("2014-12-14T00:00:28.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> First TCP SVC connection established for SVC session.", "2014-12-14T00:00:28.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
				"2014-12-14T01:01:29.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group = GeneralHybrid, Username = vferreira, IP = 220.36.32.118, Session disconnected. Session Type: SSL, Duration: 1h:01m:01s, Bytes xmt: 233909584, Bytes rcv: 29339131, Reason: User Requested"),
				$((String) null, "2014-12-14 00:00:28,1418515228,vferreira,73.189.60.63,10.72.116.99,SUCCESS,,,,,,,,,,,,,,,",
				"2014-12-14 01:01:29,1418518889,vferreira,220.36.32.118,,CLOSED,,,,,,,,,233909584,29339131,3661,,,,") },

				{ "Session Ended - 'Session disconnected' with no bytes - drop record",
				$("2014-12-14T00:01:29.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group = GeneralHybrid, Username = vferreira, IP = 220.36.32.118, Session disconnected. Session Type: SSL, Duration: 7h:33m:35s, Bytes xmt: 0, Bytes rcv: 0, Reason: User Requested"),
				$((String) null) },
				{ "Usernames with trailing whitespaces should be trimmed",
				$("2014-12-14T00:17:26.000+00:00|device-id=gi-0-1.rav2-1-gci.corp.fortscale.com Group <MobileHybrid> User <moav > IP <73.189.60.63> First TCP SVC connection established for SVC session",
				"2014-12-14T00:17:26.000+00:00|device-id=gi-0-1.rav2-1-gci.corp.fortscale.com Group <MobileHybrid> User <moav > IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7805::101a> assigned to session"),
				$((String) null,
				"2014-12-14 00:17:26,1418516246,moav,73.189.60.63,10.72.116.99,SUCCESS,,,,,,,,,,,,,,,") },
				{ "Session end with multiple Start session start from multiple IP's",

				$("2014-12-14T00:01:20.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> First TCP SVC connection established for SVC session.", "2014-12-14T00:01:20.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
				"2014-12-14T00:01:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.65> First TCP SVC connection established for SVC session.", "2014-12-14T00:01:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.65> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
				"2014-12-14T00:02:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.67> First TCP SVC connection established for SVC session.", "2014-12-14T00:02:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.67> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
				"2014-12-14T01:02:29.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group = GeneralHybrid, Username = vferreira, IP = 220.36.32.118, Session disconnected. Session Type: SSL, Duration: 1h:01m:01s, Bytes xmt: 233909584, Bytes rcv: 29339131, Reason: User Requested"),

				$((String) null, "2014-12-14 00:01:20,1418515280,vferreira,73.189.60.63,10.72.116.99,SUCCESS,,,,,,,,,,,,,,,",
				(String) null,
				"2014-12-14 00:01:27,1418515287,vferreira,73.189.60.65,10.72.116.99,SUCCESS,,,,,,,,,,,,,,,",
				(String) null,
				"2014-12-14 00:02:27,1418515347,vferreira,73.189.60.67,10.72.116.99,SUCCESS,,,,,,,,,,,,,,,",
					"2014-12-14 01:02:29,1418518949,vferreira,220.36.32.118,,CLOSED,,,,,,,,,233909584,29339131,3661,,,,") },

				{ "Session end with multiple Start session But none in the duration +- 30 seconds",
				$("2014-12-13T00:01:20.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> First TCP SVC connection established for SVC session.", "2014-12-13T00:01:20.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
				"2014-12-14T00:01:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.65> First TCP SVC connection established for SVC session.", "2014-12-14T00:01:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.65> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
				"2014-12-14T00:02:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.67> First TCP SVC connection established for SVC session.", "2014-12-14T00:02:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.67> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
				"2014-12-14T01:02:29.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group = GeneralHybrid, Username = vferreira, IP = 220.36.32.118, Session disconnected. Session Type: SSL, Duration: 10h:01m:01s, Bytes xmt: 233909584, Bytes rcv: 29339131, Reason: User Requested"),
				$((String) null, "2014-12-13 00:01:20,1418428880,vferreira,73.189.60.63,10.72.116.99,SUCCESS,,,,,,,,,,,,,,,",
				(String) null,
				"2014-12-14 00:01:27,1418515287,vferreira,73.189.60.65,10.72.116.99,SUCCESS,,,,,,,,,,,,,,,",
				(String) null, "2014-12-14 00:02:27,1418515347,vferreira,73.189.60.67,10.72.116.99,SUCCESS,,,,,,,,,,,,,,,",
				"2014-12-14 01:02:29,1418518949,vferreira,220.36.32.118,,CLOSED,,,,,,,,,233909584,29339131,36061,,,,") },

				{ "Session end with zero (0) duration",
				$("2014-12-13T00:01:20.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> First TCP SVC connection established for SVC session.",
				"2014-12-13T00:01:20.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
				"2014-12-14T01:02:29.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group = GeneralHybrid, Username = vferreira, IP = 220.36.32.118, Session disconnected. Session Type: SSL, Duration: 0h:00m:00s, Bytes xmt: 233909584, Bytes rcv: 29339131, Reason: User Requested"),
				$((String) null,
				"2014-12-13 00:01:20,1418428880,vferreira,73.189.60.63,10.72.116.99,SUCCESS,,,,,,,,,,,,,,,",
				"2014-12-14 01:02:29,1418518949,vferreira,220.36.32.118,,CLOSED,,,,,,,,,233909584,29339131,,,,,") } });
	}
}
