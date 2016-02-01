package fortscale.collection.morphlines.ssh;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static junitparams.JUnitParamsRunner.$;

/**
 * Created by galiar on 09/11/2015.
 */
@RunWith(JUnitParamsRunner.class)
public class SshMessageTest {


	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/ssh/readSSH_cisco.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readSSH_enrich.conf";



	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpClass() {
		new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
	}

	@Before
	public void setUp() throws Exception {


		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String kafkaMessageFields = propertiesResolver.getProperty("kafka.ssh.message.record.fields");

		List<String> sshMessageOutputFields = ImpalaParser.getTableFieldNames(kafkaMessageFields);
		morphlineTester.init(new String[] { confFile, confEnrichmentFile }, sshMessageOutputFields);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}

	@Test
	@Parameters
	public void testSshSingleLines(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}



	@SuppressWarnings("unused")
	private Object[] parametersForTestSshSingleLines() {
		return	$(
				$(
						"test from contain ::1 ",
						"2014-09-09T17:00:00+02:00 dev-gever sshd[2552]: Accepted publickey for root from ::1 port 47952 ssh2",
						"2014-09-09 15:00:00,1410274800,dev-gever,dev-gever,root,Accepted,publickey,,,,false,,,,,,,ssh,etl"



				),
				$(
						"test from contain ::1 - With WAN enrichment. Should take the timezone from the event",
						"2014-09-09T17:00:00+02:00 dev-gever sshd[2552]: Accepted publickey for root from ::1 port 47952 ssh2 Flume enrichment timezone Asia/Jerusalem",
						"2014-09-09 15:00:00,1410274800,dev-gever,dev-gever,root,Accepted,publickey,,,,false,,,,,,,ssh,etl"



				),
				$(
						"test from contain 127.0.0.1  ",
						"2014-09-09T17:00:00+02:00 dev-gever sshd[2552]: Accepted publickey for root from 127.0.0.1 port 47952 ssh2",
						"2014-09-09 15:00:00,1410274800,dev-gever,dev-gever,root,Accepted,publickey,,,,false,,,,,,,ssh,etl"


				),
				$ (
						"Successful Public Key Authentication",
						"2014-09-09T17:00:00+02:00 dev-gever sshd[2552]: Accepted publickey for root from 171.70.163.169 port 47952 ssh2",
						"2014-09-09 15:00:00,1410274800,171.70.163.169,dev-gever,root,Accepted,publickey,,,,false,,,,,,,ssh,etl"
				),
				$(
						"Target Machine as IP",
						"2014-09-09T17:00:00+02:00 192.168.0.30 sshd[30431]: Accepted password for root from 192.168.200.254 port 62257 ssh2",
						"2014-09-09 15:00:00,1410274800,192.168.200.254,192.168.0.30,root,Accepted,password,,,,false,,,,,,,ssh,etl"
				),
				$(
						"Successful Keyboard Interactive Authentication",
						"2014-09-09T17:00:00+02:00 dev-gever sshd[2552]: Accepted keyboard-interactive for root from 171.70.163.169 port 47952 ssh2",
						"2014-09-09 15:00:00,1410274800,171.70.163.169,dev-gever,root,Accepted,keyboard-interactive,,,,false,,,,,,,ssh,etl"
				),
				$(
						"Successful Keyboard Interactive / PAM Authentication",
						"2014-09-09T17:00:00+02:00 dev-gever sshd[2552]: Accepted keyboard-interactive/pam for root from 171.70.163.169 port 47952 ssh2",
						"2014-09-09 15:00:00,1410274800,171.70.163.169,dev-gever,root,Accepted,keyboard-interactive/pam,,,,false,,,,,,,ssh,etl"
				),
				$(
						"Sample Cisco event with qoutes and ID after port",
						"\"2014-10-06T00:00:00+02:00 alli-prd-app7 sshd[25652]: [ID 800047 auth.info] Accepted publickey for root from 72.163.46.51 port 33218 ssh2\"",
						"2014-10-05 22:00:00,1412546400,72.163.46.51,alli-prd-app7,root,Accepted,publickey,,,,false,,,,,,,ssh,etl"
				),
				$(
						"Sample Cisco event with [ID 800047 auth.info] type message in the middle",
						"2015-06-20T00:00:30+00:00 192.168.249.55 sshd[24359]: [ID 800047 auth.info] Accepted publickey for oracle from 192.168.249.56 port 35735 ssh2",
						"2015-06-20 00:00:30,1434758430,192.168.249.56,192.168.249.55,oracle,Accepted,publickey,,,,false,,,,,,,ssh,etl"
				)


		);
	}

}
