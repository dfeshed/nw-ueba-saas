package fortscale.collection.morphlines.securityevents.syslog;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.collection.morphlines.TestUtils;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class SecEventsSyslog4624Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/syslog/readSecEvt.conf";
	private String conf4624File = "resources/conf-files/securityevents/syslog/processSecEvt4624.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";


	final static String Jun_1_00_00_49 = "Jun  1 00:00:49";
	static String Jun_1_00_00_49_OUT;
	static String year_case1;



	static {
		prepareDates();
	}

	@SuppressWarnings("deprecation") private static void prepareDates() {
		TestUtils.init("yyyy MMM dd HH:mm:ss", "UTC");
		Date date = TestUtils.constuctDate(Jun_1_00_00_49);
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		year_case1 = df.format(date);
		Jun_1_00_00_49_OUT = TestUtils.getOutputDate(date);

	}

	
	
	@SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }

	@Before
	public void setUp() throws Exception {
		List<String> fieldsToCheck = Arrays.asList("date_time_unix","source_ip","account_name","account_domain","reporting_server");
		morphlineTester.init(new String[] { confFile, conf4624File , confSecEnrich}, fieldsToCheck);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}

	@Test
	@Parameters
	public void test(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}


	@SuppressWarnings("unused")
	private Object[] parametersForTest() {

		long runtime_Jun_1_00_00_49 = ((new DateTime(new Integer(year_case1), 6, 1, 0, 0, 49, DateTimeZone.UTC).getMillis()) / 1000L);
		return	$(
				$ (
						"Successfull 4624 Event",
						Jun_1_00_00_49+" il-dc-dr microsoft-windows-security-auditing[success] 4624 An account was successfully logged on.  Subject: Security ID:S-1-0-0 Account Name:- Account Domain:- Logon ID:0x0  Logon Type:3  Impersonation Level:%1833  New Logon: Security ID: S-1-5-21-2289726844-590661003-2420928919-3984 Account Name:IL-DC2$ Account Domain:IL Logon ID:0x1a21039a Logon GUID: {FEDDD500-E343-BC3C-57AB-F8FEC4B3B968}  Process Information: Process ID:0x0 Process Name:-  Network Information: Workstation Name: Source Network Address:192.168.7.41 Source Port:53660  Detailed Authentication Information: Logon Process:Kerberos Authentication Package:Kerberos Transited Services:- Package Name (NTLM only):- Key Length:0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Service",
						runtime_Jun_1_00_00_49+",192.168.7.41,IL-DC2$,IL,il-dc-dr"
				),
				$ (
						"4624 Event with single date digit",
						Jun_1_00_00_49+" il-dc-dr microsoft-windows-security-auditing[success] 4624 An account was successfully logged on.  Subject: Security ID:S-1-0-0 Account Name:- Account Domain:- Logon ID:0x0  Logon Type:3  Impersonation Level:%1833  New Logon: Security ID: S-1-5-21-2289726844-590661003-2420928919-3984 Account Name:IL-DC2$ Account Domain:IL Logon ID:0x1a21039a Logon GUID: {FEDDD500-E343-BC3C-57AB-F8FEC4B3B968}  Process Information: Process ID:0x0 Process Name:-  Network Information: Workstation Name: Source Network Address:192.168.7.41 Source Port:53660  Detailed Authentication Information: Logon Process:Kerberos Authentication Package:Kerberos Transited Services:- Package Name (NTLM only):- Key Length:0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Service",
						runtime_Jun_1_00_00_49+",192.168.7.41,IL-DC2$,IL,il-dc-dr"
				),
				$ (
						"Account name value is \"-\", should be dropped",
						Jun_1_00_00_49+" il-dc-dr microsoft-windows-security-auditing[success] 4624 An account was successfully logged on.  Subject: Security ID:S-1-0-0 Account Name:- Account Domain:- Logon ID:0x0  Logon Type:3  Impersonation Level:%1833  New Logon: Security ID: S-1-5-21-2289726844-590661003-2420928919-3984 Account Name:- Account Domain:- Logon ID:0x1a21039a Logon GUID: {FEDDD500-E343-BC3C-57AB-F8FEC4B3B968}  Process Information: Process ID:0x0 Process Name:-  Network Information: Workstation Name: Source Network Address:192.168.7.41 Source Port:53660  Detailed Authentication Information: Logon Process:Kerberos Authentication Package:Kerberos Transited Services:- Package Name (NTLM only):- Key Length:0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Service",
						null
				)

		);
	}


}
