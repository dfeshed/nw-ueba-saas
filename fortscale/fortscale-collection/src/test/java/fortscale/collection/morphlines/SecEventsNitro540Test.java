package fortscale.collection.morphlines;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class SecEventsNitro540Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readWin2003SecEvt_nitro.conf";
	private String conf540File = "resources/conf-files/processSecEvt540.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";


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
						"144116287956583680,10830961599,ELM,1437870767,,,11.176.91.253||Security||1489839827||Security||540||52||1437865378||4||STAP0024||NT AUTHORITY\\SYSTEM||Logon/Logoff||15||STAP0024$||RBSRES07||(0x4,0xEAF9B9CF)||3||Kerberos||Kerberos||||{1204f6a7-306b-a34f-27f9-639a055534e6}||-||-||-||-||-||11.176.91.245||44280||Successful Network Logon:%0D %0D %09User Name:%09ROND-PC$%0D %0D %09Domain:%09%09RBSRES07%0D %0D %09Logon ID:%09%09(0x4,0xEAF9B9CF)%0D %0D %09Logon Type:%093%0D %0D %09Logon Process:%09Kerberos%0D %0D %09Authentication Package:%09Kerberos%0D %0D %09Workstation Name:%09%0D %0D %09Logon GUID:%09{1204f6a7-306b-a34f-27f9-639a055534e6}%0D %0D %09Caller User Name:%09-%0D %0D %09Caller Domain:%09-%0D %0D %09Caller Logon ID:%09-%0D %0D %09Caller Process ID: -%0D %0D %09Transited Services: -%0D %0D %09Source Network Address:%0911.176.91.245%0D %0D %09Source Port:%0944280%0D %0D",
						"1437865378,11.176.91.245,ROND-PC$,RBSRES07,STAP0024"
				)

		);
	}


}