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
public class SecEventsNitro4768RouterTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvtRouter_nitro.conf";

	
	
	@SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }
	
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
		        "Successful 4768 computer Event",
		    	"144117387552096256,142469366,ELM,1446336136,,,10.8.48.101||Security||<BookmarkList>%0D   <Bookmark Channel='Security' RecordId='225175943' IsCurrent='true'/>%0D </BookmarkList>||Microsoft-Windows-Security-Auditing||4768||262||1446335487||0||SNCH2DCS01.sicpa-net.ads||||||14||SAPServiceLVM$||sicpa-net||S-1-5-21-1324571244-530250876-991709287-45630||krbtgt||S-1-5-21-1324571244-530250876-991709287-502||0x40810010||0x0||0x12||2||::ffff:10.8.48.74||50087||||||||A Kerberos authentication ticket (TGT) was requested.%0D %0D Account Information:%0D %09Account Name:%09%09%251%0D %09Supplied Realm Name:%09%252%0D %09User ID:%09%09%09%253%0D %0D Service Information:%0D %09Service Name:%09%09%254%0D %09Service ID:%09%09%255%0D %0D Network Information:%0D %09Client Address:%09%09%2510%0D %09Client Port:%09%09%2511%0D %0D Additional Information:%0D %09Ticket Options:%09%09%256%0D %09Result Code:%09%09%257%0D %09Ticket Encryption Type:%09%258%0D %09Pre-Authentication Type:%09%259%0D %0D Certificate Information:%0D %09Certificate Issuer Name:%09%09%2512%0D %09Certificate Serial Number:%09%2513%0D %09Certificate Thumbprint:%09%09%2514%0D %0D Certificate information is only provided if a certificate was used for pre-authentication.%0D %0D Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
		    	"4768,true"
        		),
				$ (
				"Successful 4768 non computer Event",
				"144117387552096256,142469366,ELM,1446336136,,,10.8.48.101||Security||<BookmarkList>%0D   <Bookmark Channel='Security' RecordId='225175943' IsCurrent='true'/>%0D </BookmarkList>||Microsoft-Windows-Security-Auditing||4768||262||1446335487||0||SNCH2DCS01.sicpa-net.ads||||||14||SAPServiceLVM||sicpa-net||S-1-5-21-1324571244-530250876-991709287-45630||krbtgt||S-1-5-21-1324571244-530250876-991709287-502||0x40810010||0x0||0x12||2||::ffff:10.8.48.74||50087||||||||A Kerberos authentication ticket (TGT) was requested.%0D %0D Account Information:%0D %09Account Name:%09%09%251%0D %09Supplied Realm Name:%09%252%0D %09User ID:%09%09%09%253%0D %0D Service Information:%0D %09Service Name:%09%09%254%0D %09Service ID:%09%09%255%0D %0D Network Information:%0D %09Client Address:%09%09%2510%0D %09Client Port:%09%09%2511%0D %0D Additional Information:%0D %09Ticket Options:%09%09%256%0D %09Result Code:%09%09%257%0D %09Ticket Encryption Type:%09%258%0D %09Pre-Authentication Type:%09%259%0D %0D Certificate Information:%0D %09Certificate Issuer Name:%09%09%2512%0D %09Certificate Serial Number:%09%2513%0D %09Certificate Thumbprint:%09%09%2514%0D %0D Certificate information is only provided if a certificate was used for pre-authentication.%0D %0D Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
				"4768,false"
				)
        		   		
        );
 	}


}
