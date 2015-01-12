package fortscale.collection.morphlines;

import fortscale.utils.junit.SpringAware;
import fortscale.utils.impala.ImpalaParser;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;

import static junitparams.JUnitParamsRunner.$;

@RunWith(Parameterized.class)
@SuppressWarnings("InstanceMethodNamingConvention")
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:META-INF/spring/morphline-test-context.xml")
//used to clean spring context for next class:
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class VpnMorphlineParsingTest {
	//rules used to set JUnit parameters in SpringAware
	@ClassRule
	public static final SpringAware SPRING_AWARE = SpringAware.forClass(VpnMorphlineParsingTest.class);
	@Rule
	public TestRule springAwareMethod = SPRING_AWARE.forInstance(this);
	@Rule
	public TestName testName = new TestName();

	String testCase;
	static Map expectedFieldsMap = new HashMap<String, Boolean>();
	String confFile;
	Object[] lines;
	Object[] outputs;
	public VpnMorphlineParsingTest(String testCase, Map expectedFieldsMap, String confFile, Object[] lines, Object[] outputs){
		this.testCase = testCase;
		VpnMorphlineParsingTest.expectedFieldsMap = expectedFieldsMap;
		this.confFile = confFile;
		this.lines = lines;
		this.outputs = outputs;
	}

	private MorphlinesParseVpnTester morphlineTester = new MorphlinesParseVpnTester();

	@Value("${impala.data.vpn.table.morphline.fields}")
	private String impalaTableFields;
	@Value("${morphline.timezone}")
	private String timeZone;
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}

	@Test
	@Parameters(name = "{index} {1}: Run test for conf file: ({3})")
	public void test() {
		morphlineTester.init(new String[] {confFile}, null);
		List<String> events = new ArrayList<String>(lines.length);
		for (Object line : lines)
			events.add((String)line);

		List<String> expected = new ArrayList<String>(outputs.length);
		for (Object output : outputs)
			expected.add((String)output);
		morphlineTester.testMultipleLines(testCase, events, expected);
	}

	@Parameters()
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][]
						{
								{
										"VPN F5 input events",
										expectedFieldsMap,
										"resources/conf-files/readVPN_F5.conf",
										$(
												"Jan  2 19:08:28 server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8781: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)",
												"Jan  2 19:08:35 server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8781: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful",
												"Jan  2 19:11:09 server.bs.dom Jan  2 19:11:31 server notice tmm2[20226]: 01490521:5: 49dc8781: Session statistics - bytes in: 632880, bytes out: 2649665",
												"Apr 14 00:17:42 va60tb01lba01dmz.black.com Apr 14 00:18:03 va60tb01lba01dmz notice tmm[20226]: 01490500:5: 18648c83: New session from client IP 66.249.64.46 (ST=California/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown)",
												"Apr 14 01:50:26 server Apr 14 01:50:47 server info apd[18544]: 01490500:5: 18648c83: AD agent: Auth (logon attempt:0): authenticate with 'kamali123' failed"

										),
										$(
												(String) null,
												"SUCCESS",
												"CLOSED",
												(String) null,
												"FAIL"

										)

								},

								{
										"VPN Avenda Etips input events",
										expectedFieldsMap,
										"resources/conf-files/readVPN_avendaEtips.conf",
										$(
												"2014-03-10 09:03:39	Local7.Debug	10.232.60.18	CEF:0|Avenda|eTipsDB|1.0||Start|Unknown| eventId=23982752342 end=1394434855000 mrt=1394434862962 customerID=S+cjNIw8BABCLZ6xMwRmzOw\\=\\= customerURI=/All Customers/Amdocs/Amdocs - Israel modelConfidence=4 severity=0 relevance=10 assetCriticality=0 priority=2 art=1394434860986 rt=1394434860986 src=10.120.91.190 sourceZoneID=ML8022AABABCDTFpYAT3UdQ\\=\\= sourceZoneURI=/All Zones/ArcSight System/Private Address Space Zones/RFC1918: 10.0.0.0-10.255.255.255 dst=10.121.2.30 destinationZoneID=Mvq5iHCoBABCbpx2nIuhb8g\\=\\= destinationZoneURI=/All Zones/Site Zones/AmdocsProduction/Champaign/WISM CMI  destinationAssetId=NxYeaHCoBABCqRh2nIuhb8g\\=\\= duser=a88808c7a1d7 destinationGeoCountryCode=US destinationGeoLocationInfo=Champaign  dlong=-88.0 dlat=40.0 destinationGeoPostalCode=61820 destinationGeoRegionCode=217 cs2=Remote locality=1 cs1Label=Service_Type cs2Label=Acct_Authentic cs3Label=Acct_Terminate_Cause ahost=RASIMMGR2SRV agt=10.232.62.23 av=5.1.7.6151.0 atz=Asia/Jerusalem aid=3hmtHiT4BABCAAYE0Soydvg\\=\\= at=superagent_ng dtz=Asia/Jerusalem eventAnnotationStageUpdateTime=1394434863005 eventAnnotationModificationTime=1394434863005 eventAnnotationAuditTrail=1,1393945852714,root,Queued,,,,\r\n eventAnnotationVersion=1 eventAnnotationFlags=0 eventAnnotationEndTime=1394434855000 eventAnnotationManagerReceiptTime=1394434862962 customerName=Amdocs - Israel originalAgentHostName=RASIMAGT4SRV originalAgentAddress=10.232.60.74 originalAgentZoneURI=/All Zones/Site Zones/AmdocsProduction/Raanana/Raanana - NT originalAgentAssetId=4sw8hmRIBABCpf+uHmdlLaA\\=\\= originalAgentVersion=5.1.3.5870.0 originalAgentId=3zGZnkzABABDjvo8uXZWyYQ\\=\\= originalAgentType=flexmulti_db _cefVer=0.1 ad.arcSightEventPath=3zGZnkzABABDjvo8uXZWyYQ\\=\\=",
												"2014-03-10 09:41:01	Local7.Debug	10.232.60.18	CEF:0|Avenda|eTipsDB|1.0||Start|Unknown| eventId=23986765322 end=1394437065000 mrt=1394437073468 customerID=S+cjNIw8BABCLZ6xMwRmzOw\\=\\= customerURI=/All Customers/Amdocs/Amdocs - Israel modelConfidence=0 severity=0 relevance=10 assetCriticality=0 priority=2 art=1394437072314 rt=1394437072314 shost=valeriel01.corp.amdocs.com src=10.233.136.38 sourceZoneID=ML8022AABABCDTFpYAT3UdQ\\=\\= sourceZoneURI=/All Zones/ArcSight System/Private Address Space Zones/RFC1918: 10.0.0.0-10.255.255.255 dst=10.232.234.15 destinationZoneID=ML8022AABABCDTFpYAT3UdQ\\=\\= destinationZoneURI=/All Zones/ArcSight System/Private Address Space Zones/RFC1918: 10.0.0.0-10.255.255.255 duser=VALMIKK cs2=RADIUS locality=1 cs1Label=Service_Type cs2Label=Acct_Authentic cs3Label=Acct_Terminate_Cause ahost=RASIMMGR2SRV agt=10.232.62.23 av=5.1.7.6151.0 atz=Asia/Jerusalem aid=3hmtHiT4BABCAAYE0Soydvg\\=\\= at=superagent_ng dtz=Asia/Jerusalem eventAnnotationStageUpdateTime=1394437073812 eventAnnotationModificationTime=1394437073812 eventAnnotationAuditTrail=1,1393945852714,root,Queued,,,,\r\n eventAnnotationVersion=1 eventAnnotationFlags=0 eventAnnotationEndTime=1394437065000 eventAnnotationManagerReceiptTime=1394437073468 customerName=Amdocs - Israel originalAgentHostName=RASIMAGT4SRV originalAgentAddress=10.232.60.74 originalAgentZoneURI=/All Zones/Site Zones/AmdocsProduction/Raanana/Raanana - NT originalAgentAssetId=4sw8hmRIBABCpf+uHmdlLaA\\=\\= originalAgentVersion=5.1.3.5870.0 originalAgentId=3zGZnkzABABDjvo8uXZWyYQ\\=\\= originalAgentType=flexmulti_db _cefVer=0.1 ad.arcSightEventPath=3zGZnkzABABDjvo8uXZWyYQ\\=\\=",
												"2014-03-10 09:05:35	Local7.Debug	10.232.60.18	CEF:0|Avenda|eTipsDB|1.0||Start|Unknown| eventId=23983068375 end=1394434990000 mrt=1394434998202 customerID=S+cjNIw8BABCLZ6xMwRmzOw\\=\\= customerURI=/All Customers/Amdocs/Amdocs - Israel modelConfidence=4 severity=0 relevance=10 assetCriticality=0 priority=2 art=1394434996517 rt=1394434996517 src=193.229.18.9 sourceZoneID=Mokee5CcBABCGKZ5Updd27g\\=\\= sourceZoneURI=/All Zones/ArcSight System/Public Address Space Zones/RIPE NCC/193.0.0.0-195.255.255.255 (RIPE NCC) sourceGeoCountryCode=FI slong=26.0 slat=64.0 dst=193.43.246.42 destinationZoneID=MgkUSmRIBABCcMOuHmdlLaA\\=\\= destinationZoneURI=/All Zones/Site Zones/AmdocsProduction/ArcSync/ArcSync_193.43.246.0 destinationAssetId=NAxo0FhgBABCAi2llvNTpUg\\=\\= duser=ALINADA destinationGeoCountryCode=IL destinationGeoLocationInfo=Raanana dlong=35.0 dlat=32.0 destinationGeoRegionCode=09 cs1=Framed-User cs2=RADIUS locality=1 cs1Label=Service_Type cs2Label=Acct_Authentic cs3Label=Acct_Terminate_Cause ahost=RASIMMGR2SRV agt=10.232.62.23 av=5.1.7.6151.0 atz=Asia/Jerusalem aid=3hmtHiT4BABCAAYE0Soydvg\\=\\= at=superagent_ng dtz=Asia/Jerusalem eventAnnotationStageUpdateTime=1394434998219 eventAnnotationModificationTime=1394434998219 eventAnnotationAuditTrail=1,1393945852714,root,Queued,,,,\r\n eventAnnotationVersion=1 eventAnnotationFlags=0 eventAnnotationEndTime=1394434990000 eventAnnotationManagerReceiptTime=1394434998202 customerName=Amdocs - Israel originalAgentHostName=RASIMAGT4SRV originalAgentAddress=10.232.60.74 originalAgentZoneURI=/All Zones/Site Zones/AmdocsProduction/Raanana/Raanana - NT originalAgentAssetId=4sw8hmRIBABCpf+uHmdlLaA\\=\\= originalAgentVersion=5.1.3.5870.0 originalAgentId=3zGZnkzABABDjvo8uXZWyYQ\\=\\= originalAgentType=flexmulti_db _cefVer=0.1 ad.arcSightEventPath=3zGZnkzABABDjvo8uXZWyYQ\\=\\="
										),
										$(
												"SUCCESS",
												"SUCCESS",
												"SUCCESS"
										)
								},
								{
										"VPN Cisco ASA input events",
										expectedFieldsMap,
										"resources/conf-files/readVPN_ASA_Cisco.conf",
										$(
												"Sep 03 2014 05:12:22 rtp5-vpn-cluster-2 : %ASA-7-722051: Group <apple_short> User <tomerl-test-CA57DA549C121B25A5A5038A58C81E6B7B1C954F-iPhone> IP <174.46.152.7> IPv4 Address <10.82.239.225> IPv6 address <::> assigned to session",
												"Oct 01 2014 00:05:44 fff-vpn-cluster-2 : %ASA-4-113019: Group = apple_short, Username = idan-test-1DBFA4192D8E90FD6C9B7620562B3AD1978BBFFE-iPhone, IP = 44.188.239.218, Session disconnected. Session Type: SSL, Duration: 1d 19h:41m:14s, Bytes xmt: 187346964, Bytes rcv: 28559584, Reason: User Requested\"",
												"Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-6-713905: Group = Everyone, Username = bpotugan, IP = 102.78.186.30, Login authentication failed due to max simultaneous-login restriction."

										),
										$(
												"SUCCESS",
												"CLOSED",
												"FAIL"
										)
								},
								{
										"VPN Cisco for Zurich input events",
										expectedFieldsMap,
										"resources/conf-files/readVPN_Cisco_forZurich.conf",
										$(
												"10/13/2014,2:20:43,uswh90d,Default Group,82.166.88.97,Start,C825FF9E,,Framed,PPP,,,,,,217886720,10.148.1.72",
												"10/13/2014,8:09:45,uswh90d,Default Group,82.166.88.97,Stop,C825FF9E,20943,Framed,PPP,9199349,19097369,62424,61192,10.128.59.201,217886720,10.148.1.72"

										),
										$(
												"SUCCESS",
												"CLOSED"
										)
								},
								{
										"VPN Cisco input events",
										expectedFieldsMap,
										"resources/conf-files/readVPN_Cisco.conf",
										$(
												"111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: 37.11.25.29: User [mduran] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724)",
												"111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv: 2649665  Reason: User Requested",
												"111502127: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-4-5: RPT=333150: 37.11.25.29: Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = monkey, domain = <not specified>"

										),
										$(
												"SUCCESS",
												"CLOSED",
												"FAIL"

										)
								},
								{
										"VPN Juniper input events",
										expectedFieldsMap,
										"resources/conf-files/readVPN_juniper.conf",
										$(
												"Mar 16 04:17:26 192.168.199.2 Juniper: 2014-03-16 04:17:26 - ive - [82.166.88.97] omendelso-contractor(SecurID Users)[Users, Poza Users] - Agent login succeeded for omendelso-contractor/SecurID Users from 82.166.88.97.",
												"Mar 16 04:17:26 192.168.199.2 Juniper: 2014-03-16 04:17:26 - ive - [82.166.88.97] omendelso-contractor(SecurID Users)[Users, Poza Users] - VPN Tunneling: Session started for user with IP 10.49.253.16, hostname ORI-PC",
												"Feb 12 11:56:32 10.1.150.10 Juniper: 2014-02-12 11:56:36 - ch-vpn-prilly - [85.132.48.198] cfankhause(Employees-OTP)[Employees_Common, Employees_Pulse] - Closed connection to 10.1.151.24 after 55 seconds, with 119991 bytes read and 110702 bytes written",
												"Nov  7 14:36:10 11.155.45.2 Juniper: 2013-11-07 14:36:10 - ive - [72.193.146.27] bdes(Users)[] - Login failed using auth server server.mypozza.com (ACE Server).  Reason: Failed"
										),
										$(
												(String) null,
												"SUCCESS",
												"CLOSED",
												"FAIL"

										)
								}
						}
				);
	}

}