package fortscale.collection.morphlines.securityevents.nitro;

import fortscale.collection.morphlines.MorphlinesTester;
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
public class ReadWin2003SecEvtRouterNitroTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/nitro/readWin2003SecEvtRouter.conf";


	@Before
	public void setUp() throws Exception {
		List<String> fieldsToCheck = Arrays.asList("account_name","eventCode");
		morphlineTester.init(new String[] { confFile}, fieldsToCheck);
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
						"144116287956582912,11013904892,ELM,1438389171,,,11.176.92.244||Security||104918383||Security||540||52||1438256514||4||STAPSQL0014||NT AUTHORITY\\SYSTEM||Logon/Logoff||15||STAPSQL0014$||RBSRES07||(0x9,0xB4E8F6B2)||3||Kerberos||Kerberos||||{49565cdc-f504-0b10-f759-01eeaf7c2c54}||-||-||-||-||-||-||-||Successful Network Logon:%0D %0D %09User Name:%09STAPSQL0014$%0D %0D %09Domain:%09%09RBSRES07%0D %0D %09Logon ID:%09%09(0x9,0xB4E8F6B2)%0D %0D %09Logon Type:%093%0D %0D %09Logon Process:%09Kerberos%0D %0D %09Authentication Package:%09Kerberos%0D %0D %09Workstation Name:%09%0D %0D %09Logon GUID:%09{49565cdc-f504-0b10-f759-01eeaf7c2c54}%0D %0D %09Caller User Name:%09-%0D %0D %09Caller Domain:%09-%0D %0D %09Caller Logon ID:%09-%0D %0D %09Caller Process ID: -%0D %0D %09Transited Services: -%0D %0D %09Source Network Address:%09-%0D %0D %09Source Port:%09-%0D %0D",
						"STAPSQL0014$,4624"
				),
				$ (
						"673 Event",
						"11.178.255.45||Security||3261131673||Security||673||52||1437490334||4||TRUPDCS0003||NT AUTHORITY\\SYSTEM||Account mailto:Logon%7C%7C10%7C%7CTRUVESX0028$@GCM.COM||GCM.COM||TRUPDCS0003$||%25{S-1-5-21-2000478354-616249376-682003330-101399}||0x40810000||0x17||172.21.108.203||-||{5bc0e599-d238-ea65-a185-44845d6e4cd9}||-||Service Ticket Request:%0D %0D %09User Name:%09%09TRUVESX0028$@GCM.COM%0D %0D %09User Domain:%09%09GCM.COM%0D %0D %09Service Name:%09%09TRUPDCS0003$%0D %0D %09Service ID:%09%09GCM\\TRUPDCS0003$%0D %0D %09Ticket Options:%09%090x40810000%0D %0D %09Ticket Encryption Type:%090x17%0D %0D %09Client Address:%09%09172.21.108.203%0D %0D %09Failure Code:%09%09-%0D %0D %09Logon GUID:%09%09{5bc0e599-d238-ea65-a185-44845d6e4cd9}%0D %0D %09Transited Services:%09-%0D %0D",
						"TRUVESX0028$@GCM.COM,4769"
				)

		);
	}
}