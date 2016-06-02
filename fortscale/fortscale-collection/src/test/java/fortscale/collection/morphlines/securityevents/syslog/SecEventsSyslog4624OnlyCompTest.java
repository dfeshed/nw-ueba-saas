package fortscale.collection.morphlines.securityevents.syslog;

import fortscale.collection.morphlines.MorphlinesTester;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class SecEventsSyslog4624OnlyCompTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/syslog/readSecEvtOnlyComp.conf";
	private String conf4624File = "resources/conf-files/securityevents/syslog/read4624SecEvtOnlyComp.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";


	
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
	@Ignore
	@Test
	@Parameters
	public void test(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}


	@SuppressWarnings("unused")
	private Object[] parametersForTest() {
		return	$(
				$ (
						"Successfull 4624 Event",
						"May 31 20:18:43 il-dc-dr microsoft-windows-security-auditing[success] 4624 An account was successfully logged on.  Subject: Security ID:S-1-0-0 Account Name:- Account Domain:- Logon ID:0x0  Logon Type:3  Impersonation Level:%1833  New Logon: Security ID: S-1-5-21-2289726844-590661003-2420928919-10110 Account Name:IL-DC1$ Account Domain:IL Logon ID:0x1a2103c0 Logon GUID: {ECC8AE54-0C05-3220-AD69-8E5F133D6550}  Process Information: Process ID:0x0 Process Name:-  Network Information: Workstation Name: Source Network Address:192.168.7.40 Source Port:55398  Detailed Authentication Information: Logon Process:Kerberos Authentication Package:Kerberos Transited Services:- Package Name (NTLM only):- Key Length:0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Servic",
						"1433103523,192.168.7.40,IL-DC1$,IL,il-dc-dr"
				)

		);
	}


}
