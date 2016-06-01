package fortscale.collection.morphlines.securityevents.syslog;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.collection.morphlines.PropertyMockingLocalTimezoneApplicationContextInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class,
		initializers = PropertyMockingLocalTimezoneApplicationContextInitializer.class,
		locations = {"classpath*:META-INF/spring/collection-context-test-light-local-timezone.xml"})
//used to clean spring context for next class:
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SecEventsSyslog4768OnlyLocalTimezoneCompTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/syslog/readSecEvtOnlyComp.conf";
	private String conf4768File = "resources/conf-files/securityevents/syslog/read4768SecEvtOnlyComp.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";

	
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		List<String> fieldsToCheck = Arrays.asList("date_time_unix","client_address","account_name","account_domain","reporting_server");
		morphlineTester.init(new String[] { confFile, conf4768File,confSecEnrich }, fieldsToCheck);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}

	@Test
	public void test_winter() {
		String testCase = "4768 Event in the winter";
		String inputLine = "Jan 31 20:18:43 il-dc1 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:IL-TMUROTDB$ Supplied Realm Name:IL.PLAYTECH.CORP User ID: S-1-5-21-2289726844-590661003-2420928919-6529  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.87 Client Port:54179  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.";
		String expectedOutput = "1454264323,192.168.7.87,IL-TMUROTDB$,IL.PLAYTECH.CORP,il-dc1";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}
	@Test
	public void test_summer() {
		String testCase = "4768 Event in the summer";
		String inputLine = "May 31 20:18:43 il-dc1 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:IL-TMUROTDB$ Supplied Realm Name:IL.PLAYTECH.CORP User ID: S-1-5-21-2289726844-590661003-2420928919-6529  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.87 Client Port:54179  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.";
		String expectedOutput = "1464715123,192.168.7.87,IL-TMUROTDB$,IL.PLAYTECH.CORP,il-dc1";

		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

}
