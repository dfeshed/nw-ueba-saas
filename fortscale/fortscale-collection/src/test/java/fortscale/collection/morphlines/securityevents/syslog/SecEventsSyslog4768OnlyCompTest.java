package fortscale.collection.morphlines.securityevents.syslog;

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
public class SecEventsSyslog4768OnlyCompTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/syslog/readSecEvtOnlyComp.conf";
	private String conf4768File = "resources/conf-files/securityevents/syslog/read4768SecEvtOnlyComp.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";

	
	
	
	@SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }
	
	
	@Before
	public void setUp() throws Exception {
		List<String> fieldsToCheck = Arrays.asList("date_time_unix","client_address","account_name","account_domain","reporting_server");
		morphlineTester.init(new String[] { confFile, conf4768File,confSecEnrich }, fieldsToCheck);
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
						"4768 Event with computer as account name (Should be dropped)",
						"May 31 20:18:43 il-dc1 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:IL-TMUROTDB$ Supplied Realm Name:IL.PLAYTECH.CORP User ID: S-1-5-21-2289726844-590661003-2420928919-6529  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.87 Client Port:54179  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						"1464725923,192.168.7.87,IL-TMUROTDB$,IL.PLAYTECH.CORP,il-dc1"
				)
		);
	}

}
