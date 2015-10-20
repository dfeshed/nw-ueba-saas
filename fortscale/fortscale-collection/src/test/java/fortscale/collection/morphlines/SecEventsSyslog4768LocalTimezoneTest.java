package fortscale.collection.morphlines;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.junit.SpringAware;
import fortscale.utils.properties.PropertiesResolver;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
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
	private String confFile = "resources/conf-files/readSecEvt_syslog.conf";
	private String conf4768File = "resources/conf-files/processSecEvt4768_syslog.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";


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
		return Arrays.asList(new Object[][]
						{

								{

										"4768 Event",
										"Jun  1 00:00:49 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:IL-SPLEGAL1$ Supplied Realm Name:IL.PLAYTECH.CORP User ID: S-1-5-21-2289726844-590661003-2420928919-3013  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.62 Client Port:63521  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										"Jun  1 00:00:49,2015-05-31 21:00:49,1433106049,IL-SPLEGAL1$,IL.PLAYTECH.CORP,S-1-5-21-2289726844-590661003-2420928919-3013,4768,192.168.7.62,,,0x0,2,0x40810010,True,False,False,False,False,False,false,,,"
								},
								{
										"Successfull 4768 Event",
										"May 31 20:18:43 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:asher_y Supplied Realm Name:il User ID: S-1-5-21-2289726844-590661003-2420928919-8387  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47357  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										"May 31 20:18:43,2015-05-31 17:18:43,1433092723,asher_y,il,S-1-5-21-2289726844-590661003-2420928919-8387,4768,192.168.7.34,,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,,"
								},
								{
										"Failure 4768 Event",
										"May 31 20:18:50 il-dc1 microsoft-windows-security-auditing[failure] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:Galit@ptts.com Supplied Realm Name:IL.PLAYTECH.CORP User ID:S-1-0-0  Service Information: Service Name:krbtgt/IL.PLAYTECH.CORP Service ID:S-1-0-0  Network Information: Client Address: ::ffff:10.197.67.19 Client Port:54544  Additional Information: Ticket Options:0x40810010 Result Code:0x6 Ticket Encryption Type:0xffffffff Pre-Authentication Type:-  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										"May 31 20:18:50,2015-05-31 17:18:50,1433092730,Galit@ptts.com,IL.PLAYTECH.CORP,S-1-0-0,4768,10.197.67.19,,FAILURE,0x6,-,0x40810010,True,False,False,False,False,False,false,,,"
								},
								{
										"Event 4768 with no user name (Should be dropped)",
										"May 31 20:18:43 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Supplied Realm Name:il User ID: S-1-5-21-2289726844-590661003-2420928919-8387  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47357  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										null
								},
								{
										"Regular 4768 Event",
										"May 31 20:18:48 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:besadmin Supplied Realm Name:IL User ID: S-1-5-21-2289726844-590661003-2420928919-1726  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.161 Client Port:40950  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										"May 31 20:18:48,2015-05-31 17:18:48,1433092728,besadmin,IL,S-1-5-21-2289726844-590661003-2420928919-1726,4768,192.168.7.161,,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,,"
								},
								{
										"4768 Event with computer as account name",
										"May 31 20:18:43 il-dc1 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:IL-TMUROTDB$ Supplied Realm Name:IL.PLAYTECH.CORP User ID: S-1-5-21-2289726844-590661003-2420928919-6529  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.87 Client Port:54179  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										"May 31 20:18:43,2015-05-31 17:18:43,1433092723,IL-TMUROTDB$,IL.PLAYTECH.CORP,S-1-5-21-2289726844-590661003-2420928919-6529,4768,192.168.7.87,,,0x0,2,0x40810010,True,False,False,False,False,False,false,,,"
								},
								{
										"Successfull 4768 Event with ' in the Account Name",
										"May 31 20:18:45 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:amo's_s Supplied Realm Name:il User ID: S-1-5-21-2289726844-590661003-2420928919-10374  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47362  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
										"May 31 20:18:45,2015-05-31 17:18:45,1433092725,amos_s,il,S-1-5-21-2289726844-590661003-2420928919-10374,4768,192.168.7.34,,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,,"
								}
						}
		);
	}

}
