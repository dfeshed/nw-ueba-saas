package fortscale.collection.morphlines.securityevents.syslog;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.collection.morphlines.PropertyMockingLocalTimezoneApplicationContextInitializer;
import fortscale.collection.morphlines.TestUtils;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.junit.SpringAware;
import fortscale.utils.properties.PropertiesResolver;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(Parameterized.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class,
		initializers = PropertyMockingLocalTimezoneApplicationContextInitializer.class,
		locations = {"classpath*:META-INF/spring/collection-context-test-light-local-timezone.xml"})
//used to clean spring context for next class:
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SecEventsSyslog4768LocalTimezoneTest {
	//rules used to set JUnit parameters in SpringAware
	@ClassRule
	public static final SpringAware SPRING_AWARE = SpringAware.forClass(SecEventsSyslog4768LocalTimezoneTest.class);
	@Rule
	public TestRule springAwareMethod = SPRING_AWARE.forInstance(this);
	@Rule
	public TestName testName = new TestName();

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/syslog/readSecEvt.conf";
	private String conf4768File = "resources/conf-files/securityevents/syslog/processSecEvt4768.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";

	final static String Jun_1_00_00_49 = "Jun  1 00:00:49";
	static String Jun_1_00_00_49_OUT;
	static String year_case1;

	final static String May_31_20_18_44 = "May 31 20:18:44";
	static String May_31_20_18_44_OUT;
	static String year_case2;



	static {
		prepareDates();
	}

	@SuppressWarnings("deprecation") private static void prepareDates() {
		TestUtils.init("yyyy MMM dd HH:mm:ss", "Asia/Jerusalem");
		Date date = TestUtils.constuctDate(Jun_1_00_00_49);
		SimpleDateFormat df = new SimpleDateFormat("yyyy");
		year_case1 = df.format(date);
		Jun_1_00_00_49_OUT = TestUtils.getOutputDate(date);
		date = TestUtils.constuctDate(May_31_20_18_44);
		year_case2 = df.format(date);
		May_31_20_18_44_OUT = TestUtils.getOutputDate(date);
	}





	String testCase;
	String line;
	String output;
	public SecEventsSyslog4768LocalTimezoneTest(String testCase, String line, String output){
		this.testCase = testCase;
		this.line = line;
		this.output = output;
	}

	@AfterClass
	public static void finalizeTestClass(){
	}



	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.security.events.login.table.morphline.fields");
		List<String> splunkSecEventsOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		List<String> splunkSecEventsOutputFieldsExcludingEnrichment = new ArrayList<>();
		for(String field: splunkSecEventsOutputFields){
			if(!field.equals("machine_name")){
				splunkSecEventsOutputFieldsExcludingEnrichment.add(field);
			}
		}
		morphlineTester.init(new String[] { confFile, conf4768File , confSecEnrich }, splunkSecEventsOutputFieldsExcludingEnrichment);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}

	@Test
	@Parameters(name = "{index} {1}")
	public void test() {
		morphlineTester.testSingleLine(testCase, line, output);
	}

	@Parameters()
	public static Iterable<Object[]> data() {


		long runtime_Jun_1_00_00_49 = ((new DateTime(new Integer(year_case1), 5, 31, 21, 0, 49, DateTimeZone.UTC).getMillis()) / 1000L);
		long runtime_May_31_20_18_44 = ((new DateTime(new Integer(year_case2), 5, 31, 17, 18, 44, DateTimeZone.UTC).getMillis()) / 1000L);

		return Arrays.asList(new Object[][]
						{

								{

										"4768 Event",
										Jun_1_00_00_49+" IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:IL-SPLEGAL1$ Supplied Realm Name:IL.PLAYTECH.CORP User ID: S-1-5-21-2289726844-590661003-2420928919-3013  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.62 Client Port:63521  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										Jun_1_00_00_49+","+Jun_1_00_00_49_OUT+","+runtime_Jun_1_00_00_49+",IL-SPLEGAL1$,IL.PLAYTECH.CORP,S-1-5-21-2289726844-590661003-2420928919-3013,4768,192.168.7.62,,,0x0,2,0x40810010,True,False,False,False,False,False,false,,,,,,,"
								},
								{
										"Successfull 4768 Event",
										May_31_20_18_44+" IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:asher_y Supplied Realm Name:il User ID: S-1-5-21-2289726844-590661003-2420928919-8387  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47357  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										May_31_20_18_44+","+May_31_20_18_44_OUT+","+runtime_May_31_20_18_44+",asher_y,il,S-1-5-21-2289726844-590661003-2420928919-8387,4768,192.168.7.34,,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,,,,,,"
								},
								{
										"Failure 4768 Event",
										May_31_20_18_44+" il-dc1 microsoft-windows-security-auditing[failure] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:Galit@ptts.com Supplied Realm Name:IL.PLAYTECH.CORP User ID:S-1-0-0  Service Information: Service Name:krbtgt/IL.PLAYTECH.CORP Service ID:S-1-0-0  Network Information: Client Address: ::ffff:10.197.67.19 Client Port:54544  Additional Information: Ticket Options:0x40810010 Result Code:0x6 Ticket Encryption Type:0xffffffff Pre-Authentication Type:-  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										May_31_20_18_44+","+May_31_20_18_44_OUT+","+runtime_May_31_20_18_44+",Galit@ptts.com,IL.PLAYTECH.CORP,S-1-0-0,4768,10.197.67.19,,FAILURE,0x6,-,0x40810010,True,False,False,False,False,False,false,,,,,,,"
								},
								{
										"Event 4768 with no user name (Should be dropped)",
										May_31_20_18_44+" IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Supplied Realm Name:il User ID: S-1-5-21-2289726844-590661003-2420928919-8387  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47357  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										null
								},
								{
										"Regular 4768 Event",
										May_31_20_18_44+" IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:besadmin Supplied Realm Name:IL User ID: S-1-5-21-2289726844-590661003-2420928919-1726  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.161 Client Port:40950  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										May_31_20_18_44+","+May_31_20_18_44_OUT+","+runtime_May_31_20_18_44+",besadmin,IL,S-1-5-21-2289726844-590661003-2420928919-1726,4768,192.168.7.161,,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,,,,,,"
								},
								{
										"4768 Event with computer as account name",
										May_31_20_18_44+" il-dc1 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:IL-TMUROTDB$ Supplied Realm Name:IL.PLAYTECH.CORP User ID: S-1-5-21-2289726844-590661003-2420928919-6529  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.87 Client Port:54179  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										May_31_20_18_44+","+May_31_20_18_44_OUT+","+runtime_May_31_20_18_44+",IL-TMUROTDB$,IL.PLAYTECH.CORP,S-1-5-21-2289726844-590661003-2420928919-6529,4768,192.168.7.87,,,0x0,2,0x40810010,True,False,False,False,False,False,false,,,,,,,"
								},
								{
										"Successfull 4768 Event with ' in the Account Name",
										May_31_20_18_44+" IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:amo's_s Supplied Realm Name:il User ID: S-1-5-21-2289726844-590661003-2420928919-10374  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47362  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										May_31_20_18_44+","+May_31_20_18_44_OUT+","+runtime_May_31_20_18_44+",amos_s,il,S-1-5-21-2289726844-590661003-2420928919-10374,4768,192.168.7.34,,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,,,,,,"
								}
						}
		);
	}

}
