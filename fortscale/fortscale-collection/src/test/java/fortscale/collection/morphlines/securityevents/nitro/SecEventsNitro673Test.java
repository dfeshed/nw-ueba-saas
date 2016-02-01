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
public class SecEventsNitro673Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/nitro/readWin2003SecEvt.conf";
	private String conf673File = "resources/conf-files/securityevents/nitro/processSecEvt673.conf";
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
		String impalaTableFields = propertiesResolver.getProperty("impala.data.kerberos_logins.table.morphline.fields");
		List<String> splunkSecEventsOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		List<String> splunkSecEventsOutputFieldsExcludingEnrichment = new ArrayList<>();
		for(String field: splunkSecEventsOutputFields){
			if(!field.equals("machine_name")){
				splunkSecEventsOutputFieldsExcludingEnrichment.add(field);
			}
		}
		morphlineTester.init(new String[] { confFile, conf673File , confSecEnrich }, splunkSecEventsOutputFieldsExcludingEnrichment);
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
						"673 Event",
						"11.178.255.45||Security||3261131673||Security||673||52||1437490334||4||TRUPDCS0003||NT AUTHORITY\\SYSTEM||Account mailto:Logon%7C%7C10%7C%7CTRUVESX0028$@GCM.COM||GCM.COM||TRUPDCS0003$||%25{S-1-5-21-2000478354-616249376-682003330-101399}||0x40810000||0x17||172.21.108.203||-||{5bc0e599-d238-ea65-a185-44845d6e4cd9}||-||Service Ticket Request:%0D %0D %09User Name:%09%09TRUVESX0028$@GCM.COM%0D %0D %09User Domain:%09%09GCM.COM%0D %0D %09Service Name:%09%09TRUPDCS0003$%0D %0D %09Service ID:%09%09GCM\\TRUPDCS0003$%0D %0D %09Ticket Options:%09%090x40810000%0D %0D %09Ticket Encryption Type:%090x17%0D %0D %09Client Address:%09%09172.21.108.203%0D %0D %09Failure Code:%09%09-%0D %0D %09Logon GUID:%09%09{5bc0e599-d238-ea65-a185-44845d6e4cd9}%0D %0D %09Transited Services:%09-%0D %0D",
						"2015-07-21T14:52:14.000,2015-07-21 14:52:14,Kerberos Service Ticket Operations,4769,Security,3261131673,Microsoft Windows security auditing.,TRUVESX0028$@GCM.COM,GCM.COM,TRUPDCS0003,GCM,172.21.108.203,0x40810000,-,,1437490334,false,,,,,,,,,"
				)
		);
	}

}
