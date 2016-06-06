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
public class SecEventsSyslog4768OnlyCompTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/syslog/readSecEvtOnlyComp.conf";
	private String conf4768File = "resources/conf-files/securityevents/syslog/read4768SecEvtOnlyComp.conf";
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

		long runtime_Jun_1_00_00_49 = ((new DateTime(new Integer(year_case1), 6, 1, 0, 0, 49, DateTimeZone.UTC).getMillis()) / 1000L);
		return	$(
				$ (
						"4768 Event with computer as account name (Should be dropped)",
						Jun_1_00_00_49+" il-dc1 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:IL-TMUROTDB$ Supplied Realm Name:IL.PLAYTECH.CORP User ID: S-1-5-21-2289726844-590661003-2420928919-6529  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.87 Client Port:54179  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						runtime_Jun_1_00_00_49+",192.168.7.87,IL-TMUROTDB$,IL.PLAYTECH.CORP,il-dc1"
				)
		);
	}

}
