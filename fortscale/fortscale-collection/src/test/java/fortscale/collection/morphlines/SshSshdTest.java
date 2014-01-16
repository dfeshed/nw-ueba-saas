package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class SshSshdTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSSH_centos.conf";
	private String[] sshOutputFields = new String[] {"date_time","date_time_epoch","ip","source_machine","user","status","auth_method","message","client_hostname"};
													   

	
	@Before
	public void setUp() throws Exception {
		morphlineTester.init(confFile, sshOutputFields);
	}

	@Test
	@Parameters
	public void testSshSingleLines(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}
	
	
	
	@SuppressWarnings("unused")
	private Object[] parametersForTestSshSingleLines() {
        return	$(
        		$ (
        		"Successful Password Authentication",
        		"Nov 19 14:58:32 dev-gever sshd[30431]: Accepted password for root from 192.168.200.254 port 62257 ssh2",
        		"2014-11-19 14:58:32,1416409112,192.168.200.254,dev-gever,root,Accepted,password,Nov 19 14:58:32 dev-gever sshd[30431]: Accepted password for root from 192.168.200.254 port 62257 ssh2,192.168.200.254"
				),
        		$ (
        		"Successful Public Key Authentication",
        		"Nov 19 18:31:17 dev-gever sshd[2591]: Accepted publickey for root from 192.168.55.55 port 38681 ssh2",
        		"2014-11-19 18:31:17,1416421877,192.168.55.55,dev-gever,root,Accepted,publickey,Nov 19 18:31:17 dev-gever sshd[2591]: Accepted publickey for root from 192.168.55.55 port 38681 ssh2,192.168.55.55"),
        		$ (
        		"Password Failed Authentication",
        		"Dec 5 11:51:36 inter-psg-01 sshd[22525]: Failed password for root from 192.168.211.112 port 59420 ssh2",
        		"2014-12-05 11:51:36,1417780296,192.168.211.112,inter-psg-01,root,Failed,password,Dec 5 11:51:36 inter-psg-01 sshd[22525]: Failed password for root from 192.168.211.112 port 59420 ssh2,192.168.211.112"),
        		$ (
        		"Invalid User Failed Authentication",
        		"Jul 7 10:53:24 chaves sshd[12914]: Failed password for invalid user test-inv from spongebob.lab.ossec.net",
        		null)
        		);
    }

}