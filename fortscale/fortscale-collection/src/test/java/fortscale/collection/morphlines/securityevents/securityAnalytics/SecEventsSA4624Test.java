package fortscale.collection.morphlines.securityevents.securityAnalytics;

import fortscale.collection.morphlines.MorphlinesTester;
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
public class SecEventsSA4624Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	//empty morphline
	private String confFile = "resources/conf-files/enrichment/readSSH_enrich.conf";
	private String conf4624File = "resources/conf-files/securityevents/securityAnalytics/read4624SecEvtOnlyComp.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";

	
	
	@SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }
	
	@Before
	public void setUp() throws Exception {
		List<String> fieldsToCheck = Arrays.asList("date_time_unix","client_address","account_name","account_domain");
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
						"%NICWIN-4-Security_4624_Microsoft-Windows-Security-Auditing: Security,rn=-1158775465 cid=0x00003100 eid=0x00001210,Mon Nov 23 15:28:49 2015,4624,Microsoft-Windows-Security-Auditing,None,Success Audit,BLRKECMBX21.ad.infosys.com,Logon,,An account was successfully logged on.  Subject:  Security ID:  /NULL SID   Account Name:  -   Account Domain:  -   Logon ID:  0x0   Logon Type:   3   New Logon:  Security ID:  ITLINFOSYS/CHNSHLMBX12$   Account Name:  CHNSHLMBX12$   Account Domain:  ITLINFOSYS   Logon ID:  0x275588966   Logon GUID:  {4E5B9B95-41ED-7F32-8EC1-09A1A57ABEFC}   Process Information:  Process ID:  0x0   Process Name:  -   Network Information:  Workstation Name:    Source Network Address: 10.81.88.73   Source Port:  29212   Detailed Authentication Information:  Logon Process:  Kerberos   Authentication Package: Kerberos   Transited Services: -   Package Name (NTLM only): -   Key Length:  0   This event is generated when a logon session is created. It is generated on the computer that was accessed.  The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.  The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).  The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.  The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.  The authentication information fields provide detailed information about this specific logon request.  - Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.  - Transited services indicate which intermediate services have participated in this logon request.  - Package name i",
						"1448292529,10.81.88.73,CHNSHLMBX12$,ITLINFOSYS"
				)
        		   		
        );
 	}


}
