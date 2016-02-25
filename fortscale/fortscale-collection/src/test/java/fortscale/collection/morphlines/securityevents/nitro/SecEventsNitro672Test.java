package fortscale.collection.morphlines.securityevents.nitro;

import fortscale.collection.morphlines.MorphlinesTester;
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
public class SecEventsNitro672Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/nitro/readWin2003SecEvt.conf";
	private String conf672File = "resources/conf-files/securityevents/nitro/processSecEvt672.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";


	private static ClassPathXmlApplicationContext testContextManager;

	@BeforeClass
	public static void setUpClass() {
		testContextManager = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
	}

	@AfterClass
	public static void finalizeTestClass(){
		testContextManager.close();
		testContextManager = null;
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
		morphlineTester.init(new String[] { confFile, conf672File , confSecEnrich }, splunkSecEventsOutputFieldsExcludingEnrichment);
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
						"672 Event",
						"11.178.255.45||Security||3261090758||Security||672||52||1437490065||4||TRUPDCS0003||NT AUTHORITY\\SYSTEM||Account Logon||13||TRUVESX0040$||GCM.COM||%25{S-1-5-21-2000478354-616249376-682003330-137231}||krbtgt||%25{S-1-5-21-2000478354-616249376-682003330-502}||0x40000010||-||0x17||2||11.179.192.40||||||||Authentication Ticket Request:%0D %0D %09User Name:%09%09TRUVESX0040$%0D %0D %09Supplied Realm Name:%09GCM.COM%0D %0D %09User ID:%09%09%09GCM\\TRUVESX0040$%0D %0D %09Service Name:%09%09krbtgt%0D %0D %09Service ID:%09%09GCM\\krbtgt%0D %0D %09Ticket Options:%09%090x40000010%0D %0D %09Result Code:%09%09-%0D %0D %09Ticket Encryption Type:%090x17%0D %0D %09Pre-Authentication Type:%092%0D %0D %09Client Address:%09%0911.179.192.40%0D %0D %09Certificate Issuer Name:%09%0D %0D %09Certificate Serial Number:%09%0D %0D %09Certificate Thumbprint:%09%0D %0D",
						"2015-07-21T14:47:45.000,2015-07-21 14:47:45,1437490065,TRUVESX0040$,GCM.COM,GCM,4768,11.179.192.40,,SUCCESS,-,2,0x40000010,True,False,False,False,False,False,false,,,,,,,"
				)
		);
	}

}
