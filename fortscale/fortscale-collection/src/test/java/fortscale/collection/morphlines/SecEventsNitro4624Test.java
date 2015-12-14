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
public class SecEventsNitro4624Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvt_nitro.conf";
	private String conf4624File = "resources/conf-files/processSecEvt4624_nitro.conf";
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
						"144117387552096256,142469339,ELM,1446336136,,,10.8.48.101||Security||<BookmarkList>%0D   <Bookmark Channel='Security' RecordId='225175916' IsCurrent='true'/>%0D </BookmarkList>||Microsoft-Windows-Security-Auditing||4624||262||1446335482||0||SNCH2DCS01.sicpa-net.ads||||||20||S-1-5-18||SNCH2DCS01$||SICPA-NET||0x3e7||S-1-5-21-1324571244-530250876-991709287-1141||SvcCitrix||SICPA-NET||0x1f9a99aa||3||Advapi  ||MICROSOFT_AUTHENTICATION_PACKAGE_V1_0||SNCH2DCS01||00000000-0000-0000-0000-000000000000||-||-||0||0x268||C:\\Windows\\System32\\lsass.exe||10.8.51.11||50930||An account was successfully logged on.%0D %0D Subject:%0D %09Security ID:%09%09%251%0D %09Account Name:%09%09%252%0D %09Account Domain:%09%09%253%0D %09Logon ID:%09%09%254%0D %0D Logon Type:%09%09%09%259%0D %0D New Logon:%0D %09Security ID:%09%09%255%0D %09Account Name:%09%09%256%0D %09Account Domain:%09%09%257%0D %09Logon ID:%09%09%258%0D %09Logon GUID:%09%09%2513%0D %0D Process Information:%0D %09Process ID:%09%09%2517%0D %09Process Name:%09%09%2518%0D %0D Network Information:%0D %09Workstation Name:%09%2512%0D %09Source Network Address:%09%2519%0D %09Source Port:%09%09%2520%0D %0D Detailed Authentication Information:%0D %09Logon Process:%09%09%2510%0D %09Authentication Package:%09%2511%0D %09Transited Services:%09%2514%0D %09Package Name (NTLM only):%09%2515%0D %09Key Length:%09%09%2516%0D %0D This event is generated when a logon session is created. It is generated on the computer that was accessed.%0D %0D The subject fields indicate the account on the local system which requested the logon. This is most commonly a service such as the Server service, or a local process such as Winlogon.exe or Services.exe.%0D %0D The logon type field indicates the kind of logon that occurred. The most common types are 2 (interactive) and 3 (network).%0D %0D The New Logon fields indicate the account for whom the new logon was created, i.e. the account that was logged on.%0D %0D The network fields indicate where a remote logon request originated. Workstation name is not always available and may be left blank in some cases.%0D %0D The authentication information fields provide detailed information about this specific logon request.%0D %09- Logon GUID is a unique identifier that can be used to correlate this event with a KDC event.%0D %09- Transited services indicate which intermediate services have participated in this logon request.%0D %09- Package name indicates which sub-protocol was used among the NTLM protocols.%0D %09- Key length indicates the length of the generated session key. This will be 0 if no session key was requested.",
						"1446335482,10.8.51.11,SNCH2DCS01$,SICPA-NET,SNCH2DCS01.sicpa-net.ads"
				)
        		   		
        );
 	}


}
