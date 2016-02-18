package fortscale.collection.morphlines.vpn;

import fortscale.collection.FsParametrizedMultiLineTest;
import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.junit.SpringAware;
import fortscale.utils.properties.PropertiesResolver;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

/**
 * Created by galiar on 09/11/2015.
 */
@RunWith(Parameterized.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class,
		locations = {"classpath*:META-INF/spring/collection-context-test-mocks.xml"})
//used to clean spring context for next class:
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class VpnMessageTest extends FsParametrizedMultiLineTest {
	//rules used to set JUnit parameters in SpringAware
	@ClassRule
	public static final SpringAware SPRING_AWARE = SpringAware.forClass(VpnCiscoTest.class);
	@Rule
	public TestRule springAwareMethod = SPRING_AWARE.forInstance(this);


	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/vpn/readVPN_Cisco.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readVPN_enrich.conf";

	public VpnMessageTest(String testCase, Object[] lines, Object[] outputs) {
		super(testCase, lines, outputs);
	}

	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String kafkaMessageFields = propertiesResolver.getProperty("kafka.vpn.message.record.fields");
		List<String> vpnOutputFields = ImpalaParser.getTableFieldNames(kafkaMessageFields);
		morphlineTester.init(new String[] {confFile, confEnrichmentFile}, vpnOutputFields);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}

	@Test
	@Parameterized.Parameters(name = "{index} {1}: Run test for conf file: ({3})")
	public void test() {

		List<String> events = new ArrayList<String>(lines.length);
		for (Object line : lines)
			events.add((String)line);

		List<String> expected = new ArrayList<String>(outputs.length);
		for (Object output : outputs)
			expected.add((String)output);

		morphlineTester.testMultipleLines(testCase, events , expected);
	}


	@Parameterized.Parameters()
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ "Auth success example",
				$("111367413: 2014 Mar 22 00:12:15.980 +0100 +1:00 %AUTH-6-4: RPT=33398: 80.36.103.199: Authentication successful: handle = 77, server = 172.16.19.110, user = bosch"), $((String) null) }, { "Session start test", $("111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: 37.11.25.29: User [mduran] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724)", "111350320:  %AUTH-6-92: RPT=22376: 212.59.220.45: User [mduran] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724)", "111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: 212.59.220.45: User [] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724)", "111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: 212.59.220.45: User [mduran] Sending ACCT-START for assigned IP  (Session ID=9305F724)", "111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: : User [mduran] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724)"), $("2014-03-21 23:03:49,1395443029,mduran,37.11.25.29,172.16.25.22,SUCCESS,,,,,,,,,,,,,,,,,vpn,etl", (String) null, (String) null, (String) null, (String) null) },
				{ "Session start test - With WAN enrichment",
				$("111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: 37.11.25.29: User [mduran] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724) Flume enrichment timezone UTC", "111350320:  %AUTH-6-92: RPT=22376: 212.59.220.45: User [mduran] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724) Flume enrichment timezone UTC", "111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: 212.59.220.45: User [] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724) Flume enrichment timezone UTC", "111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: 212.59.220.45: User [mduran] Sending ACCT-START for assigned IP  (Session ID=9305F724) Flume enrichment timezone UTC", "111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: : User [mduran] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724) Flume enrichment timezone UTC"),

				$("2014-03-21 23:03:49,1395443029,mduran,37.11.25.29,172.16.25.22,SUCCESS,,,,,,,,,,,,,,,,,vpn,etl", (String) null, (String) null, (String) null, (String) null) }, { "Session disconnected", $("111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv: 2649665  Reason: User Requested", "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv: 2649665  Reason: User Requested", "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: : User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration:  Bytes xmt: 632880  Bytes rcv:   Reason: User Requested", "111412517: 2 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv: 2649665  Reason: User Requested", "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt:   Bytes rcv: 2649665  Reason: User Requested", "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv:   Reason: User Requested", "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration:  Bytes xmt: 632880  Bytes rcv:   Reason: User Requested"

		),

				$("2014-03-22 04:07:25,1395461245,pmoreno,37.11.25.29,,CLOSED,,,,,,,,3282545,632880,2649665,,,,,,,vpn,etl", (String) null, (String) null, (String) null, (String) null, (String) null, (String) null) }, { "Session disconnected - with WAN enrichment", $("111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv: 2649665  Reason: User Requested Flume enrichment timezone UTC", "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv: 2649665  Reason: User Requested Flume enrichment timezone UTC", "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: : User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration:  Bytes xmt: 632880  Bytes rcv:   Reason: User Requested Flume enrichment timezone UTC", "111412517: 2 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv: 2649665  Reason: User Requested Flume enrichment timezone UTC", "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt:   Bytes rcv: 2649665  Reason: User Requested Flume enrichment timezone UTC", "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv:   Reason: User Requested Flume enrichment timezone UTC", "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration:  Bytes xmt: 632880  Bytes rcv:   Reason: User Requested Flume enrichment timezone UTC"

		),

				$("2014-03-22 04:07:25,1395461245,pmoreno,37.11.25.29,,CLOSED,,,,,,,,3282545,632880,2649665,,,,,,,vpn,etl", (String) null, (String) null, (String) null, (String) null, (String) null, (String) null) }, { "Auth fail", $("111502127: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-4-5: RPT=333150: 37.11.25.29: Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = monkey, domain = <not specified>", "111502127: 00 +1:00 %AUTH-4-5: RPT=333150: 206.201.227.92: Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = monkey, domain = <not specified>", "111502127: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-4-5: RPT=333150: : Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = monkey, domain = <not specified>", "111502127: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-4-5: RPT=333150: 206.201.227.92: Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = , domain = <not specified>"),

				$("2014-03-22 04:07:25,1395461245,monkey,37.11.25.29,,FAIL,,,,,,,,,,,,,,,,,vpn,etl", (String) null, (String) null, (String) null

				) }, { "Auth fail - with WAN enrichment", $("111502127: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-4-5: RPT=333150: 37.11.25.29: Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = monkey, domain = <not specified> Flume enrichment timezone UTC", "111502127: 00 +1:00 %AUTH-4-5: RPT=333150: 206.201.227.92: Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = monkey, domain = <not specified> Flume enrichment timezone UTC", "111502127: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-4-5: RPT=333150: : Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = monkey, domain = <not specified> Flume enrichment timezone UTC", "111502127: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-4-5: RPT=333150: 206.201.227.92: Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = , domain = <not specified> Flume enrichment timezone UTC"),

				$("2014-03-22 04:07:25,1395461245,monkey,37.11.25.29,,FAIL,,,,,,,,,,,,,,,,,vpn,etl", (String) null, (String) null, (String) null

				) } });
	}
}
