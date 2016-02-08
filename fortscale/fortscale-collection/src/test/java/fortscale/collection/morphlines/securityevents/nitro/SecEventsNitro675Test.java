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
public class SecEventsNitro675Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/nitro/readWin2003SecEvt.conf";
	private String conf675File = "resources/conf-files/securityevents/nitro/processSecEvt675.conf";
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
		morphlineTester.init(new String[] { confFile, conf675File , confSecEnrich }, splunkSecEventsOutputFieldsExcludingEnrichment);
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
						"675 Event",
						"11.176.118.118||Security||4171500018||Security||675||52||1437491095||5||STAPVDC0001||NT AUTHORITY\\SYSTEM||Account Logon||6||chengj||%25{S-1-5-21-2000478354-616249376-682003330-3080}||krbtgt/GCM.COM||0x2||0x18||11.176.84.238||Pre-authentication failed:%0D %0D %09User Name:%09%09chengj%0D %0D %09User ID:%09%09GCM\\chengj%0D %0D %09Service Name:%09krbtgt/GCM.COM%0D %0D %09Pre-Authentication Type:%090x2%0D %0D %09Failure Code:%090x18%0D %0D %09Client Address:%09%0911.176.84.238%0D %0D",
						"2015-07-21T15:04:55.000,2015-07-21 15:04:55,1437491095,chengj,,,4771,11.176.84.238,,FAILURE,0x18,,,,,,,,,false,,,,,,"
				)
		);
	}

}