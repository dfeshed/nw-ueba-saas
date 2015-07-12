package fortscale.collection.morphlines;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class SecEventsSyslog4624RouterTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvtRouter_syslog.conf";


	@Before
	public void setUp() throws Exception {
		List<String> fieldsToCheck = Arrays.asList("eventCode","isComputer");
		morphlineTester.init(new String[] { confFile}, fieldsToCheck);
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
						"Successfull 4624 computer Event",
						"Jun 1 20:07:10|Jun 1 20:07:10 il-dc-dr microsoft-windows-security-auditing[success] 4624 An account was successfully logged on.  Subject: Security ID:S-1-5-18 Account Name:IL-DC-DR$ Account Domain:IL Logon ID:0x3e7  Logon Type:3  Impersonation Level:%1832  New Logon: Security ID: S-1-5-21-2289726844-590661003-2420928919-2994 Account Name:aandrea_g Account Domain:IL Logon ID:0x1f4dc949 Logon GUID: {8400F19B-5D2F-51E4-852B-4A8BB2FD2A19}  Process Information: Process ID:0x240 Process Name: C:\\Windows\\System32\\lsass.exe  Network Information: Workstation Name:IL-DC-DR Source Network Address:- Source Port:-  Detailed Authentication Information: Logon Process:Authz    Authentication Package:Kerberos Transited Services:- Package Name (NTLM only):- Key Length:0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local p",
						"4624,true"
				),
				$ (
						"Successfull 4624 user Event",
						"Jun 1 20:07:11|Jun 1 20:07:11 il-dc-dr microsoft-windows-security-auditing[success] 4624 An account was successfully logged on.  Subject: Security ID:S-1-5-18 Account Name:IL-DC-DR Account Domain:IL Logon ID:0x3e7  Logon Type:3  Impersonation Level:%1832  New Logon: Security ID: S-1-5-21-2289726844-590661003-2420928919-2994 Account Name:aandrea_g Account Domain:IL Logon ID:0x1f4dcc33 Logon GUID: {8400F19B-5D2F-51E4-852B-4A8BB2FD2A19}  Process Information: Process ID:0x240 Process Name: C:\\Windows\\System32\\lsass.exe  Network Information: Workstation Name:IL-DC-DR Source Network Address:- Source Port:-  Detailed Authentication Information: Logon Process:Authz    Authentication Package:Kerberos Transited Services:- Package Name (NTLM only):- Key Length:0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local p",
						"4624,false"
				),
				$ (
						"Account name value is \"-\", should be dropped",
						"May 31 20:18:43|May 31 20:18:43 il-dc-dr microsoft-windows-security-auditing[success] 4624 An account was successfully logged on.  Subject: Security ID:S-1-0-0 Account Name:- Account Domain:- Logon ID:0x0  Logon Type:3  Impersonation Level:%1833  New Logon: Security ID: S-1-5-21-2289726844-590661003-2420928919-10110 Account Name:- Account Domain:IL Logon ID:0x1a2103c0 Logon GUID: {ECC8AE54-0C05-3220-AD69-8E5F133D6550}  Process Information: Process ID:0x0 Process Name:-  Network Information: Workstation Name: Source Network Address:192.168.7.40 Source Port:55398  Detailed Authentication Information: Logon Process:Kerberos Authentication Package:Kerberos Transited Services:- Package Name (NTLM only):- Key Length:0  This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Servic",
						null
				)

		);
	}


}
