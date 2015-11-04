package fortscale.collection.morphlines;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class SecEventsSyslog4768Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSecEvt_syslog.conf";
	private String conf4768File = "resources/conf-files/processSecEvt4768_syslog.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";



	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpClass() {
		new ClassPathXmlApplicationContext("classpath*:META-INF/spring/morphline-test-context-light.xml");
	}

	@AfterClass
	public static void finalizeTestClass(){
//		testContextManager.close();
//		testContextManager = null;
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
	@Parameters
	public void test(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}

	@SuppressWarnings("unused")
	private Object[] parametersForTest() {
		return	$(

				$ (
						"4768 Event",
						"Jun  1 00:00:49 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:IL-SPLEGAL1$ Supplied Realm Name:IL.PLAYTECH.CORP User ID: S-1-5-21-2289726844-590661003-2420928919-3013  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.62 Client Port:63521  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						"Jun  1 00:00:49,2015-06-01 00:00:49,1433116849,IL-SPLEGAL1$,IL.PLAYTECH.CORP,S-1-5-21-2289726844-590661003-2420928919-3013,4768,192.168.7.62,,,0x0,2,0x40810010,True,False,False,False,False,False,false,,,,sec"
				),
				$ (
						"Successfull 4768 Event",
						"May 31 20:18:43 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:asher_y Supplied Realm Name:il User ID: S-1-5-21-2289726844-590661003-2420928919-8387  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47357  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						"May 31 20:18:43,2015-05-31 20:18:43,1433103523,asher_y,il,S-1-5-21-2289726844-590661003-2420928919-8387,4768,192.168.7.34,,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,,,sec"
				),
				$ (
						"Failure 4768 Event",
						"May 31 20:18:50 il-dc1 microsoft-windows-security-auditing[failure] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:Galit@ptts.com Supplied Realm Name:IL.PLAYTECH.CORP User ID:S-1-0-0  Service Information: Service Name:krbtgt/IL.PLAYTECH.CORP Service ID:S-1-0-0  Network Information: Client Address: ::ffff:10.197.67.19 Client Port:54544  Additional Information: Ticket Options:0x40810010 Result Code:0x6 Ticket Encryption Type:0xffffffff Pre-Authentication Type:-  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						"May 31 20:18:50,2015-05-31 20:18:50,1433103530,Galit@ptts.com,IL.PLAYTECH.CORP,S-1-0-0,4768,10.197.67.19,,FAILURE,0x6,-,0x40810010,True,False,False,False,False,False,false,,,,sec"
				),
				$ (
						"Event 4768 with no user name (Should be dropped)",
						"May 31 20:18:43 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Supplied Realm Name:il User ID: S-1-5-21-2289726844-590661003-2420928919-8387  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47357  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						null
				),
				$ (
						"Regular 4768 Event",
						"May 31 20:18:48 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:besadmin Supplied Realm Name:IL User ID: S-1-5-21-2289726844-590661003-2420928919-1726  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.161 Client Port:40950  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						"May 31 20:18:48,2015-05-31 20:18:48,1433103528,besadmin,IL,S-1-5-21-2289726844-590661003-2420928919-1726,4768,192.168.7.161,,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,,,sec"
				),
				$ (
						"4768 Event with computer as account name",
						"May 31 20:18:43 il-dc1 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:IL-TMUROTDB$ Supplied Realm Name:IL.PLAYTECH.CORP User ID: S-1-5-21-2289726844-590661003-2420928919-6529  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.87 Client Port:54179  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						"May 31 20:18:43,2015-05-31 20:18:43,1433103523,IL-TMUROTDB$,IL.PLAYTECH.CORP,S-1-5-21-2289726844-590661003-2420928919-6529,4768,192.168.7.87,,,0x0,2,0x40810010,True,False,False,False,False,False,false,,,,sec"
				),
				$ (
						"Successfull 4768 Event with ' in the Account Name",
						"May 31 20:18:45 IL-DC2 microsoft-windows-security-auditing[success] 4768 A Kerberos authentication ticket (TGT) was requested.  Account Information: Account Name:amo's_s Supplied Realm Name:il User ID: S-1-5-21-2289726844-590661003-2420928919-10374  Service Information: Service Name:krbtgt Service ID: S-1-5-21-2289726844-590661003-2420928919-502  Network Information: Client Address: ::ffff:192.168.7.34 Client Port:47362  Additional Information: Ticket Options:0x40810010 Result Code:0x0 Ticket Encryption Type:0x12 Pre-Authentication Type:2  Certificate Information: Certificate Issuer Name: Certificate Serial Number: Certificate Thumbprint:  Certificate information is only provided if a certificate was used for pre-authentication.  Pre-authentication types, ticket options, encryption types and result codes are defined in RFC 4120.",
						"May 31 20:18:45,2015-05-31 20:18:45,1433103525,amos_s,il,S-1-5-21-2289726844-590661003-2420928919-10374,4768,192.168.7.34,,SUCCESS,0x0,2,0x40810010,True,False,False,False,False,False,false,,,,sec"
				)

		);
	}

}
