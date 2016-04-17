package fortscale.collection.morphlines.vpn;

import fortscale.collection.FsParametrizedMultiLineTest;
import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.collection.morphlines.TestUtils;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.junit.SpringAware;
import fortscale.utils.properties.PropertiesResolver;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(Parameterized.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class,
		locations = {"classpath*:META-INF/spring/collection-context-test-mocks.xml"})
//used to clean spring context for next class:
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class VpnF5Test extends FsParametrizedMultiLineTest {

	//rules used to set JUnit parameters in SpringAware
	@ClassRule
	public static final SpringAware SPRING_AWARE = SpringAware.forClass(VpnF5Test.class);
	@Rule
	public TestRule springAwareMethod = SPRING_AWARE.forInstance(this);



	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/vpn/readVPN_F5.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readVPN_enrich.conf";


	final static String Apr_14_01_50_26 = "Apr 14 01:50:26";
	static String Apr_14_01_50_26_OUT;
	static Long Apr_14_01_50_26_L;
	final static String Apr_14_00_17_42 = "Apr 14 00:17:42";
	static String Apr_14_00_17_42_OUT;
	static Long Apr_14_00_17_42_L;
	final static String Apr_14_00_23_29 = "Apr 14 00:23:29";
	static String Apr_14_00_23_29_OUT;
	static Long Apr_14_00_23_29_L;
	final static String Apr_14_01_50_05 = "Apr 14 01:50:05";
	static String Apr_14_01_50_05_OUT;
	static Long Apr_14_01_50_05_L;
	final static String Apr_14_01_50_42 = "Apr 14 01:50:42";
	static String Apr_14_01_50_42_OUT;
	static Long Apr_14_01_50_42_L;
	final static String Jan_2_19_08_35 = "Jan  2 19:08:35";
	static String Jan_2_19_08_35_OUT;
	static Long Jan_2_19_08_35_L;
	final static String Jan_2_19_08_28 = "Jan  2 19:08:28";
	static String Jan_2_19_08_28_OUT;
	static Long Jan_2_19_08_28_L;
	final static String Jan_4_19_08_28 = "Jan  4 19:08:28";
	static String Jan_4_19_08_28_OUT;
	static Long Jan_4_19_08_28_L;
	final static String Jan_2_19_11_09 = "Jan  2 19:11:09";
	static String Jan_2_19_11_09_OUT;
	static Long Jan_2_19_11_09_L;
	final static String Jan_2_19_06_14 = "Jan  2 19:06:14";
	static String Jan_2_19_06_14_OUT;
	static Long Jan_2_19_06_14_L;
	final static String Jan_2_19_06_26 = "Jan  2 19:06:26";
	static String Jan_2_19_06_26_OUT;
	static Long Jan_2_19_06_26_L;
	final static String Mar_2_22_32_16 = "Mar  2 22:32:16";
	static String Mar_2_22_32_16_OUT;
	static Long Mar_2_22_32_16_L;

	static {
		prepareDates();
	}

	public VpnF5Test(String testCase, Object[] lines, Object[] outputs) {
		super(testCase, lines, outputs);
	}

	private static void prepareDates() {

		TestUtils.init("yyyy MMM dd HH:mm:ss", "UTC");

		Date date = TestUtils.constuctDate(Jan_2_19_08_35);
		Jan_2_19_08_35_OUT = TestUtils.getOutputDate(date);
		Jan_2_19_08_35_L = TestUtils.getUnixDate(date);

		date = TestUtils.constuctDate(Jan_2_19_11_09);
		Jan_2_19_11_09_OUT = TestUtils.getOutputDate(date);
		Jan_2_19_11_09_L = TestUtils.getUnixDate(date);

		date = TestUtils.constuctDate(Apr_14_00_17_42);
		Apr_14_00_17_42_OUT = TestUtils.getOutputDate(date);
		Apr_14_00_17_42_L = TestUtils.getUnixDate(date);

		date = TestUtils.constuctDate(Apr_14_00_23_29);
		Apr_14_00_23_29_OUT = TestUtils.getOutputDate(date);
		Apr_14_00_23_29_L = TestUtils.getUnixDate(date);

		date = TestUtils.constuctDate(Apr_14_01_50_26);
		Apr_14_01_50_26_OUT = TestUtils.getOutputDate(date);
		Apr_14_01_50_26_L = TestUtils.getUnixDate(date);

		date = TestUtils.constuctDate(Apr_14_01_50_05);
		Apr_14_01_50_05_OUT = TestUtils.getOutputDate(date);
		Apr_14_01_50_05_L = TestUtils.getUnixDate(date);

		date = TestUtils.constuctDate(Apr_14_01_50_42);
		Apr_14_01_50_42_OUT = TestUtils.getOutputDate(date);
		Apr_14_01_50_42_L = TestUtils.getUnixDate(date);

		date = TestUtils.constuctDate(Jan_2_19_08_28);
		Jan_2_19_08_28_OUT = TestUtils.getOutputDate(date);
		Jan_2_19_08_28_L = TestUtils.getUnixDate(date);

		date = TestUtils.constuctDate(Jan_2_19_06_14);
		Jan_2_19_06_14_OUT = TestUtils.getOutputDate(date);
		Jan_2_19_06_14_L = TestUtils.getUnixDate(date);

		date = TestUtils.constuctDate(Jan_2_19_06_26);
		Jan_2_19_06_26_OUT = TestUtils.getOutputDate(date);
		Jan_2_19_06_26_L = TestUtils.getUnixDate(date);

	}


	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.vpn.table.morphline.fields");
		List<String> vpnOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[]{confFile, confEnrichmentFile}, vpnOutputFields);
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
		return Arrays.asList(new Object[][]
				{

						{

								"Several new session with authentication (BS) VPN",
								$(
										Apr_14_00_17_42 + " va60tb01lba01dmz.black.com Apr 14 00:18:03 va60tb01lba01dmz notice tmm[20226]: 01490500:5: 18648c83: New session from client IP 66.249.64.46 (ST=California/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown)",
										Apr_14_00_17_42 + " va60tb01lba01dmz.black.com Apr 14 00:18:03 va60tb01lba01dmz notice tmm[20226]: 01490500:5: 18648c83: New session from client IP 66.249.64.46 (ST=California/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown)",
										Apr_14_01_50_26 + " server Apr 14 01:50:47 server info apd[18544]: 01490500:5: 18648c83: AD agent: Auth (logon attempt:0): authenticate with 'kamali123' failed",
										Apr_14_00_23_29 + " va60tb01lba01dmz.black.com Apr 14 00:23:50 va60tb01lba01dmz notice tmm[20226]: 01490521:5: 18648c83: Session statistics - bytes in: 0, bytes out: 0"
								),
								$(
										(String) null,
										(String) null,
										Apr_14_01_50_26_OUT + "," + Apr_14_01_50_26_L + ",kamali123,66.249.64.46,,FAIL,,,,,,,,,,,,,,,",
										Apr_14_00_23_29_OUT + "," + Apr_14_00_23_29_L + ",,,,CLOSED,,,,,,,,0,0,0,,,,,"
								)
						},
						{
								"Several new session with authentication (BS) VPN - With WAN enrichment ",
								$(
										Apr_14_00_17_42 + " va60tb01lba01dmz.black.com Apr 14 00:18:03 va60tb01lba01dmz notice tmm[20226]: 01490500:5: 18648c83: New session from client IP 66.249.64.46 (ST=California/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown) Flume enrichment timezone UTC",
										Apr_14_00_17_42 + " va60tb01lba01dmz.black.com Apr 14 00:18:03 va60tb01lba01dmz notice tmm[20226]: 01490500:5: 18648c83: New session from client IP 66.249.64.46 (ST=California/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown) Flume enrichment timezone UTC",
										Apr_14_01_50_26 + " server Apr 14 01:50:47 server info apd[18544]: 01490500:5: 18648c83: AD agent: Auth (logon attempt:0): authenticate with 'kamali123' failed Flume enrichment timezone UTC",
										Apr_14_00_23_29 + " va60tb01lba01dmz.black.com Apr 14 00:23:50 va60tb01lba01dmz notice tmm[20226]: 01490521:5: 18648c83: Session statistics - bytes in: 0, bytes out: 0 Flume enrichment timezone UTC"
								),
								$(
										(String) null,
										(String) null,
										"2016-04-14 01:50:26,1460598626,kamali123,66.249.64.46,,FAIL,,,,,,,,,,,,,,,",
										"2016-04-14 00:23:29,1460593409,,,,CLOSED,,,,,,,,0,0,0,,,,,"
								)
						},
						{
								"Regular (BS) Successful VPN Authentication",
								$(
										Jan_2_19_08_28 + " server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8781: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)",
										Jan_2_19_08_35 + " server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8781: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful",
										Jan_2_19_11_09 + " server.bs.dom Jan  2 19:11:31 server notice tmm2[20226]: 01490521:5: 49dc8781: Session statistics - bytes in: 632880, bytes out: 2649665"
								),
								$(
										(String) null,
										Jan_2_19_08_35_OUT + "," + Jan_2_19_08_35_L + ",chavier,75.26.245.200,,SUCCESS,,,,,,,,,,,,,,,",
										Jan_2_19_11_09_OUT + "," + Jan_2_19_11_09_L + ",,,,CLOSED,,,,,,,,3282545,2649665,632880,,,,,"
								)
						},
						{
								"Regular (BS) Successful VPN Authentication in reverse order",
								$(
										Jan_2_19_08_35 + " server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8782: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful",
										Jan_2_19_08_28 + " server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8782: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)"
								),
								$(
										(String) null,
										Jan_2_19_08_35_OUT + "," + Jan_2_19_08_35_L + ",chavier,75.26.245.200,,SUCCESS,,,,,,,,,,,,,,,"
								)
						},
						{
								"Regular (BS) Successful VPN Authentication in reverse order more than day apart",
								$(
										Jan_2_19_08_35 + " server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8788: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful",
										Jan_4_19_08_28 + " server.bs.dom Jan  4 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8788: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)"
								),
								$(
										(String) null,
										(String) null
								)
						},
						{
								"Regular (BS) Successful VPN Authentication in reverse order with end session",
								$(
										Jan_2_19_08_35 + " server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8784: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful",
										Jan_2_19_08_28 + " server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8784: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)",
										Jan_2_19_11_09 + " server.bs.dom Jan  2 19:11:31 server notice tmm2[20226]: 01490521:5: 49dc8784: Session statistics - bytes in: 632880, bytes out: 2649665"
								),
								$(
										(String) null,
										Jan_2_19_08_35_OUT + "," + Jan_2_19_08_35_L + ",chavier,75.26.245.200,,SUCCESS,,,,,,,,,,,,,,,",
										Jan_2_19_11_09_OUT + "," + Jan_2_19_11_09_L + ",,,,CLOSED,,,,,,,,3282545,2649665,632880,,,,,"
								)
						},

						{
								"Regular (BS) Failed VPN Authentication",
								$(
										Jan_2_19_06_14 + " server.bs.dom Jan  2 19:07:42 server notice tmm2[20226]: 01490500:5: 8a38fa18: New session from client IP 69.141.27.100 (ST=New Jersey/CC=US/C=NA) at VIP 172.10.11.12 Listener /DETAILS/details (Reputation=Unknown)",
										Jan_2_19_06_26 + " server.bs.dom Jan  2 19:07:54 server info apd[18544]: 01490017:6: 8a38fa18: AD agent: Auth (logon attempt:0): authenticate with 'bartra' failed"
								),
								$(
										(String) null,
										Jan_2_19_06_26_OUT + "," + Jan_2_19_06_26_L + ",bartra,69.141.27.100,,FAIL,,,,,,,,,,,,,,,"
								)
						},

						{
								"Only First Event of Regular (BS) Successful VPN Authentication",
								$(
										Jan_2_19_08_28 + " server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8783: New session from client IP 75.26.245.200 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)"
								),
								$(
										(String) null
								)
						},

						{
								"Only Second Event of Regular (BS) Successful VPN Authentication",
								$(
										Jan_2_19_08_35 + " server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8798: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful"
								),
								$(
										(String) null
								)
						},

						{
								"Regular (BS) Successful VPN Authentication From Last Year",
								$(
										Jan_2_19_08_28 + " server.bs.dom Jan  2 19:09:56 server notice tmm2[20226]: 01490500:5: 49dc8785: New session from client IP 75.26.245.201 (ST=Illinois/CC=US/C=NA) at VIP 172.10.10.10 Listener /DETAILS/details_https-va (Reputation=Unknown)",
										Jan_2_19_08_35 + " server.bs.dom Jan  2 19:10:03 server info apd[18544]: 01490017:6: 49dc8785: AD agent: Auth (logon attempt:0): authenticate with 'chavier' successful"
								),
								$(
										(String) null,
										Jan_2_19_08_35_OUT + "," + Jan_2_19_08_35_L + ",chavier,75.26.245.201,,SUCCESS,,,,,,,,,,,,,,,"
								)
						},

						{
								"Regular (BS) VPN Session Statistics Event",
								$(
										Jan_2_19_11_09 + " server.bs.dom Feb 28 17:11:31 server notice tmm2[20226]: 01490521:5: 0a6c7b51: Session statistics - bytes in: 632880, bytes out: 2649665"
								),
								$(
										Jan_2_19_11_09_OUT + "," + Jan_2_19_11_09_L + ",,,,CLOSED,,,,,,,,3282545,2649665,632880,,,,,"
								)
						},

						{
								"HTTP Agent authentication event should be dropped", // As we are using the AD agent authentication event to recognize this action
								$(
										Mar_2_22_32_16 + " server.bs.dom Mar  2 22:32:16 server info apd[5904]: 01490139:6: 2275c32c: HTTP agent: authenticate with 'pinto' successful"
								),
								$(
										(String) null
								)
						},
						{
								"Failed and Success Regular (BS) VPN Authentications",
								$(
										Apr_14_01_50_05 + " server Apr 14 01:50:26 server notice tmm[20226]: 01490500:5: e83f3a7c: New session from client IP 71.125.52.63 (ST=New York/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown)",
										Apr_14_01_50_26 + " server Apr 14 01:50:47 server info apd[18544]: 01490017:6: e83f3a7c: AD agent: Auth (logon attempt:0): authenticate with 'kamali123' failed",
										Apr_14_01_50_42 + " server Apr 14 01:51:04 server info apd[18544]: 01490017:6: e83f3a7c: AD agent: Auth (logon attempt:1): authenticate with 'kamalij' successful"
								),
								$(
										(String) null,
										Apr_14_01_50_26_OUT + "," + Apr_14_01_50_26_L + ",kamali123,71.125.52.63,,FAIL,,,,,,,,,,,,,,,",
										Apr_14_01_50_42_OUT + "," + Apr_14_01_50_42_L + ",kamalij,71.125.52.63,,SUCCESS,,,,,,,,,,,,,,,"
								)
						},
						{
								"New session without authentication (BS) VPN",
								$(
										Apr_14_00_17_42 + " va60tb01lba01dmz.black.com Apr 14 00:18:03 va60tb01lba01dmz notice tmm[20226]: 01490500:5: 18648c73: New session from client IP 66.249.64.46 (ST=California/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown)",
										Apr_14_00_17_42 + " va60tb01lba01dmz.black.com Apr 14 00:18:03 va60tb01lba01dmz notice tmm[20226]: 01490500:5: 18648c73: New session from client IP 66.249.64.46 (ST=California/CC=US/C=NA) at VIP 172.17.135.10 Listener /DMZ_1_RAS_Prod/www.bx.com_web_vip_https-va (Reputation=Unknown)",
										Apr_14_00_23_29 + " va60tb01lba01dmz.black.com Apr 14 00:23:50 va60tb01lba01dmz notice tmm[20226]: 01490521:5: 18648c73: Session statistics - bytes in: 0, bytes out: 0"
								),
								$(
										(String) null,
										(String) null,
										Apr_14_00_23_29_OUT + "," + Apr_14_00_23_29_L + ",,,,CLOSED,,,,,,,,0,0,0,,,,,"
								)
						}
				}
		);
	}

}
