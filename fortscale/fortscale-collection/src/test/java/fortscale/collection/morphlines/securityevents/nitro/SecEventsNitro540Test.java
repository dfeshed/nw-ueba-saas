package fortscale.collection.morphlines.securityevents.nitro;

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
public class SecEventsNitro540Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/nitro/readWin2003SecEvtRouter.conf";
	private String conf540File = "resources/conf-files/securityevents/nitro/processSecEvt540.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";

	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpClass() {
		new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-mocks.xml");
	}

	@Before
	public void setUp() throws Exception {
		List<String> fieldsToCheck = Arrays.asList("date_time_unix","source_ip","account_name","account_domain","reporting_server");
		morphlineTester.init(new String[] { confFile, conf540File , confSecEnrich}, fieldsToCheck);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}

	@Test @Parameters
	public void test(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}


	@SuppressWarnings("unused")
	private Object[] parametersForTest() {
		return	$(
				$ (
						"Successfull 540 Event",
						"144116287956582912,11013702165,ELM,1438389171,,,11.176.92.244||Security||104918286||Security||540||52||1438256108||4||STAPSQL0015||FM\\LONMS06531$||Logon/Logoff||15||LONMS06531$||FM||(0x2,0x3B53D42A)||3||Kerberos||Kerberos||||{73d73cf1-c745-056f-3080-d324772308f7}||-||-||-||-||-||11.160.90.46||4740||Successful Network Logon:%0D %0D %09User Name:%09LONMS06531$%0D %0D %09Domain:%09%09FM%0D %0D %09Logon ID:%09%09(0x2,0x3B53D42A)%0D %0D %09Logon Type:%093%0D %0D %09Logon Process:%09Kerberos%0D %0D %09Authentication Package:%09Kerberos%0D %0D %09Workstation Name:%09%0D %0D %09Logon GUID:%09{73d73cf1-c745-056f-3080-d324772308f7}%0D %0D %09Caller User Name:%09-%0D %0D %09Caller Domain:%09-%0D %0D %09Caller Logon ID:%09-%0D %0D %09Caller Process ID: -%0D %0D %09Transited Services: -%0D %0D %09Source Network Address:%0911.160.90.46%0D %0D %09Source Port:%094740%0D %0D",
						"1438256108,11.160.90.46,LONMS06531$,FM,STAPSQL0015"
				)

		);
	}


}