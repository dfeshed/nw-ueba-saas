package fortscale.collection.morphlines;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class SecEventsSymantec4624Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvt_symantec.conf";
	private String conf4624File = "resources/conf-files/processSecEvt4624_symantec.conf";
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
						"<Event xmlns=\"http://schemas.microsoft.com/win/2004/08/events/event\"><System><Provider Guid=\"{GUID}\" Name=\"Microsoft-Windows-Security-Auditing\"/><EventID>4624</EventID><Version>0</Version><Level>0</Level><Task>12544</Task><Opcode>0</Opcode><Keywords>0x8020000000000000</Keywords><TimeCreated SystemTime=\"2015-10-31T22:05:07.519281900Z\"/><EventRecordID>2503683220</EventRecordID><Correlation/><Execution ProcessID=\"792\" ThreadID=\"15316\"/><Channel>Security</Channel><Computer>hostname</Computer><Security/></System><EventData><Data Name=\"SubjectUserSid\">S-1-0-0</Data><Data Name=\"SubjectUserName\">-</Data><Data Name=\"SubjectDomainName\">-</Data><Data Name=\"SubjectLogonId\">0x0</Data><Data Name=\"TargetUserSid\">SID</Data><Data Name=\"TargetUserName\">username</Data><Data Name=\"TargetDomainName\">tdomainname</Data><Data Name=\"TargetLogonId\">0x81010e2cc</Data><Data Name=\"LogonType\">3</Data><Data Name=\"LogonProcessName\">Kerberos</Data><Data Name=\"AuthenticationPackageName\">Kerberos</Data><Data Name=\"WorkstationName\"/><Data Name=\"LogonGuid\">{GUID}</Data><Data Name=\"TransmittedServices\">-</Data><Data Name=\"LmPackageName\">-</Data><Data Name=\"KeyLength\">0</Data><Data Name=\"ProcessId\">0x0</Data><Data Name=\"ProcessName\">-</Data><Data Name=\"IpAddress\">1.1.1.1</Data><Data Name=\"IpPort\">2553</Data></EventData><RenderingInfo Culture=\"en-US\"><Message>An account was successfully logged on.  Subject:  Security ID:            S-1-0-0         Account Name:           -       Account Domain:        -       Logon ID:               0x0  Logon Type:                        3  New Logon:   Security ID:            SID     Account Name:           username$      Account Domain:         Domain  Logon ID:               0x00000000      Logon GUID:             {GUID}  Process Information:    Process ID:             0x0    Process Name:           -  Network Information:         Workstation Name:       Source Network Address: 1.1.1.1         Source Port:            2553  Detailed Authentication Information:      Logon Process:          Kerberos        Authentication Package: Kerberos        Transited Services:     -       Package Name (NTLM only):       -       Key Length:             0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.  The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).  The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.  The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.  The authentication information fields provide detailed information about this specific logon request.  - Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.  - Transited services indicate which intermediate services have participated in this logon request.      - Package name indicates which sub-protocol was used among the NTLM protocols.  - Key length indicates the length of the generated session key. This will be 0 if no session key was requested.</Message><Level>Information</Level><Task>Logon</Task><Opcode>Info</Opcode><Channel>Security</Channel><Provider>Microsoft Windows security auditing.</Provider><Keywords><Keyword>Audit Success</Keyword></Keywords></RenderingInfo></Event>\" (service map: <eventmap version=\"2\"><field name=\"TimeOffset\"></field><field name=\"reporting_sensor\">iaies1</field><field name=\"proxy_machine\">iaies1</field></eventmap>)",
						"1446848388,1.1.1.1,username$,Domain,hostname"
				)

		);
	}


}
