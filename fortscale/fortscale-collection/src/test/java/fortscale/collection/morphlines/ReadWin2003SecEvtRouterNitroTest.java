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
public class ReadWin2003SecEvtRouterNitroTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readWin2003SecEvtRouter_nitro.conf";


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
						"144116287956583680,10830961599,ELM,1437870767,,,11.176.91.253||Security||1489839827||Security||540||52||1437865378||4||STAP0024||NT AUTHORITY\\SYSTEM||Logon/Logoff||15||STAP0024$||RBSRES07||(0x4,0xEAF9B9CF)||3||Kerberos||Kerberos||||{1204f6a7-306b-a34f-27f9-639a055534e6}||-||-||-||-||-||11.176.91.245||44280||Successful Network Logon:%0D %0D %09User Name:%09ROND-PC$%0D %0D %09Domain:%09%09RBSRES07%0D %0D %09Logon ID:%09%09(0x4,0xEAF9B9CF)%0D %0D %09Logon Type:%093%0D %0D %09Logon Process:%09Kerberos%0D %0D %09Authentication Package:%09Kerberos%0D %0D %09Workstation Name:%09%0D %0D %09Logon GUID:%09{1204f6a7-306b-a34f-27f9-639a055534e6}%0D %0D %09Caller User Name:%09-%0D %0D %09Caller Domain:%09-%0D %0D %09Caller Logon ID:%09-%0D %0D %09Caller Process ID: -%0D %0D %09Transited Services: -%0D %0D %09Source Network Address:%0911.176.91.245%0D %0D %09Source Port:%0944280%0D %0D",
						"ROND-PC$,4624"
				),
				$ (
						"673 Event",
						"11.178.255.45||Security||3261131673||Security||673||52||1437490334||4||TRUPDCS0003||NT AUTHORITY\\SYSTEM||Account mailto:Logon%7C%7C10%7C%7CTRUVESX0028$@GCM.COM||GCM.COM||TRUPDCS0003$||%25{S-1-5-21-2000478354-616249376-682003330-101399}||0x40810000||0x17||172.21.108.203||-||{5bc0e599-d238-ea65-a185-44845d6e4cd9}||-||Service Ticket Request:%0D %0D %09User Name:%09%09TRUVESX0028$@GCM.COM%0D %0D %09User Domain:%09%09GCM.COM%0D %0D %09Service Name:%09%09TRUPDCS0003$%0D %0D %09Service ID:%09%09GCM\\TRUPDCS0003$%0D %0D %09Ticket Options:%09%090x40810000%0D %0D %09Ticket Encryption Type:%090x17%0D %0D %09Client Address:%09%09172.21.108.203%0D %0D %09Failure Code:%09%09-%0D %0D %09Logon GUID:%09%09{5bc0e599-d238-ea65-a185-44845d6e4cd9}%0D %0D %09Transited Services:%09-%0D %0D",
						"TRUVESX0028$@GCM.COM,4769"
				)

		);
	}
}