package fortscale.collection.morphlines;

import fortscale.domain.events.dao.VpnSessionRepository;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
//@ContextConfiguration(locations = {"classpath*:META-INF/spring/collection-context.xml"})
public class VpnCiscoASANoEventCodesTest {

	private static ClassPathXmlApplicationContext testContextManager;

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readVPN_ASA_Cisco_NoEventCodes.conf";

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
						"Start of Session - 'First TCP Session' followed by 'assigned to session' at the same unix time",
						$(
								"2014-12-14T00:17:26.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> First TCP SVC connection established for SVC session.",
								"2014-12-14T00:17:26.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
								"2014-12-14T00:17:56.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
								"2014-12-14T01:17:26.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session"
						),
						$(
								(String)null,
								"2014-12-14 02:17:26,1418516246,poorman,73.189.60.63,10.72.116.99,SUCCESS,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,,false,false",
								(String)null,
								(String)null
						)
				),



				$(
						"Start of Session - 'First TCP Session' followed by 'assigned to session' after a few seconds",
						$(
								"2014-12-14T00:17:20.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> First TCP SVC connection established for SVC session.",
								"2014-12-14T00:17:26.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session"
						),
						$(
								(String)null,
								"2014-12-14 02:17:26,1418516246,poorman,73.189.60.63,10.72.116.99,SUCCESS,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,,false,false"
						)
				),



				$(
						"Start of Session - 'assigned to session' with no preceding 'First TCP Session'",
						$(
								"2014-12-14T00:17:26.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.fortscale.com Group <GeneralHybrid> User <poorman> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session"
						),
						$(
								(String)null
						)
				),



				$(
						"Failed Session - 'User authentication failed'",
						$(
								"2014-12-14T23:59:46.000+00:00|device-id=gi-0-1.rav2-1-gci.sydney.corp.fortscale.com User authentication failed: Uname: admin",
								"2014-12-14T23:59:46.000+00:00|device-id=gi-0-1.rav2-1-gci.sydney.corp.fortscale.com User authentication failed: Uname: "
						),
						$(
								"2014-12-15 01:59:46,1418601586,admin,,,FAIL,,,,,,,,,,,,,false,false",
								(String)null
						)
				),




				$(
						"Failed Session - 'user authentication Rejected'",
						$(
								"2014-12-14T23:59:43.000+00:00|device-id=gi-0-1.rav2-1-gci.eglbp.corp.fortscale.com AAA user authentication Rejected : reason = Invalid password : local database : user = admin",
								"2014-12-14T23:59:44.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.tw1.fortscale.com AAA user authentication Rejected : reason = AAA failure : server = 67.195.88.201 : user = niniyni",
								"2014-12-14T23:59:44.000+00:00|device-id=gi-0-1.rav1-1-gci.corp.tw1.fortscale.com AAA user authentication Rejected : reason = AAA failure : server = 67.195.88.201 : user = "
						),
						$(
								"2014-12-15 01:59:43,1418601583,admin,,,FAIL,,,,,,,,,,,,,false,false",
								"2014-12-15 01:59:44,1418601584,niniyni,,,FAIL,,,,,,,,,,,,,false,false",
								(String)null
						)
				),




				$(
						"Session Ended - 'Session disconnected'",
						$(
								"2014-12-14T00:00:28.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> First TCP SVC connection established for SVC session.",
								"2014-12-14T00:00:28.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
								"2014-12-14T01:01:29.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group = GeneralHybrid, Username = vferreira, IP = 220.36.32.118, Session disconnected. Session Type: SSL, Duration: 1h:01m:01s, Bytes xmt: 233909584, Bytes rcv: 29339131, Reason: User Requested"
						),
						$(
								(String)null,
								"2014-12-14 02:00:28,1418515228,vferreira,73.189.60.63,10.72.116.99,SUCCESS,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,,false,false",
								"2014-12-14 03:01:29,1418518889,vferreira,73.189.60.63,10.72.116.99,CLOSED,United States,US,Not_supported,Not_supported,Not_supported,isp,,,233909584,29339131,3661,48119,false,false"
						)
				),


				$(
						"Session Ended - 'Session disconnected' with no bytes - drop record",
						$(
								"2014-12-14T00:01:29.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group = GeneralHybrid, Username = vferreira, IP = 220.36.32.118, Session disconnected. Session Type: SSL, Duration: 7h:33m:35s, Bytes xmt: 0, Bytes rcv: 0, Reason: User Requested"
						),
						$(
								(String)null
						)
				),


				$(
						"Usernames with trailing whitespaces should be trimmed",
						$(
								"2014-12-14T00:17:26.000+00:00|device-id=gi-0-1.rav2-1-gci.corp.fortscale.com Group <MobileHybrid> User <moav > IP <73.189.60.63> First TCP SVC connection established for SVC session",
								"2014-12-14T00:17:26.000+00:00|device-id=gi-0-1.rav2-1-gci.corp.fortscale.com Group <MobileHybrid> User <moav > IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7805::101a> assigned to session"
						),
						$(
								(String)null,
								"2014-12-14 02:17:26,1418516246,moav,73.189.60.63,10.72.116.99,SUCCESS,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,,false,false"
						)
				),
				$(
						"Session end with multiple Start session start from multiple IP's",
						$(
								"2014-12-14T00:01:20.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> First TCP SVC connection established for SVC session.",
								"2014-12-14T00:01:20.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
								"2014-12-14T00:01:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.65> First TCP SVC connection established for SVC session.",
								"2014-12-14T00:01:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.65> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
								"2014-12-14T00:02:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.67> First TCP SVC connection established for SVC session.",
								"2014-12-14T00:02:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.67> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
								"2014-12-14T01:02:29.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group = GeneralHybrid, Username = vferreira, IP = 220.36.32.118, Session disconnected. Session Type: SSL, Duration: 1h:01m:01s, Bytes xmt: 233909584, Bytes rcv: 29339131, Reason: User Requested"
						),
						$(
								(String)null,
								"2014-12-14 02:01:20,1418515280,vferreira,73.189.60.63,10.72.116.99,SUCCESS,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,,false,false",
								(String)null,
								"2014-12-14 02:01:27,1418515287,vferreira,73.189.60.65,10.72.116.99,SUCCESS,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,,false,false",
								(String)null,
								"2014-12-14 02:02:27,1418515347,vferreira,73.189.60.67,10.72.116.99,SUCCESS,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,,false,false",
								"2014-12-14 03:02:29,1418518949,vferreira,73.189.60.65,10.72.116.99,CLOSED,United States,US,Not_supported,Not_supported,Not_supported,isp,,,233909584,29339131,3661,48119,false,false"
						)
				),

				$(
						"Session end with multiple Start session But none in the duration +- 30 seconds",
						$(
								"2014-12-13T00:01:20.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> First TCP SVC connection established for SVC session.",
								"2014-12-13T00:01:20.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
								"2014-12-14T00:01:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.65> First TCP SVC connection established for SVC session.",
								"2014-12-14T00:01:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.65> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
								"2014-12-14T00:02:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.67> First TCP SVC connection established for SVC session.",
								"2014-12-14T00:02:27.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.67> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
								"2014-12-14T01:02:29.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group = GeneralHybrid, Username = vferreira, IP = 220.36.32.118, Session disconnected. Session Type: SSL, Duration: 10h:01m:01s, Bytes xmt: 233909584, Bytes rcv: 29339131, Reason: User Requested"
						),
						$(
								(String)null,
								"2014-12-13 02:01:20,1418428880,vferreira,73.189.60.63,10.72.116.99,SUCCESS,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,,false,false",
								(String)null,
								"2014-12-14 02:01:27,1418515287,vferreira,73.189.60.65,10.72.116.99,SUCCESS,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,,false,false",
								(String)null,
								"2014-12-14 02:02:27,1418515347,vferreira,73.189.60.67,10.72.116.99,SUCCESS,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,,false,false",
								"2014-12-14 03:02:29,1418518949,vferreira,220.36.32.118,,CLOSED,Japan,JP,Not_supported,Not_supported,Not_supported,isp,,,233909584,29339131,36061,6277,false,false"
						)
				),

				$(
						"Session end with zero (0) duration",
						$(
								"2014-12-13T00:01:20.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> First TCP SVC connection established for SVC session.",
								"2014-12-13T00:01:20.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group <GeneralHybrid> User <vferreira> IP <73.189.60.63> IPv4 Address <10.72.116.99> IPv6 address <2001:4998:effd:7801::10a8> assigned to session",
								"2014-12-14T01:02:29.000+00:00|device-id=rav2-1-gci.corp.bf1.fortscale.com Group = GeneralHybrid, Username = vferreira, IP = 220.36.32.118, Session disconnected. Session Type: SSL, Duration: 0h:00m:00s, Bytes xmt: 233909584, Bytes rcv: 29339131, Reason: User Requested"
						),
						$(
								(String)null,
								"2014-12-13 02:01:20,1418428880,vferreira,73.189.60.63,10.72.116.99,SUCCESS,United States,US,Not_supported,Not_supported,Not_supported,isp,,,,,,,false,false",
								"2014-12-14 03:02:29,1418518949,vferreira,220.36.32.118,,CLOSED,Japan,JP,Not_supported,Not_supported,Not_supported,isp,,,233909584,29339131,0,,false,false"
						)
				)


		);
	}

}
