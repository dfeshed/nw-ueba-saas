
package fortscale.collection.morphlines;



import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junitparams.JUnitParamsRunner.$;


@RunWith(JUnitParamsRunner.class)
public class SshTelcomItaliaTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSSH_TelcomItalia.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readSSH_enrich.conf";


	@Before
	public void setUp() throws Exception {


		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.ssh.table.morphline.fields");

		List<String> sshOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[] { confFile, confEnrichmentFile }, sshOutputFields);
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
				$ (
						"Successful Public Key Authentication",
						"1409522849 2014-09-01T00:07:26+02:00 FAC=auth npatas1/npatas1 PROG=sshd PID=271913 LEV=[info] Accepted publickey for logarc from 10.41.152.66 port 63935 ssh2",
						"2014-09-01 00:07:26,1409530046,10.41.152.66,npatas1/npatas1,logarc,Accepted,publickey,,,,false,,,,"),

				$(
						"test from contain ::1 ",

						"1409522849 2014-09-01T00:07:26+02:00 FAC=auth npatas1/npatas1 PROG=sshd PID=271913 LEV=[info] Accepted publickey for logarc from ::1 port 63935 ssh2",
						"2014-09-01 00:07:26,1409530046,npatas1/npatas1,npatas1/npatas1,logarc,Accepted,publickey,,,,false,,,,"


				),
				$(
						"test from contain 127.0.0.1  ",
						"1409522849 2014-09-01T00:07:26+02:00 FAC=auth npatas1/npatas1 PROG=sshd PID=271913 LEV=[info] Accepted publickey for logarc from 127.0.0.1 port 63935 ssh2",
						"2014-09-01 00:07:26,1409530046,npatas1/npatas1,npatas1/npatas1,logarc,Accepted,publickey,,,,false,,,,"


				),
				$ (
						"Target Machine as IP",
						"1409522849 2014-09-01T00:07:26+02:00 FAC=auth 192.168.70.65 PROG=sshd PID=271913 LEV=[info] Accepted publickey for logarc from 10.41.152.66 port 63935 ssh2",
						null

				),
				$ (
						"gssapi-with-mic as auth method ",
						"1409556949 2014-09-01T09:35:44+02:00 FAC=auth npatas1/npatas1 PROG=sshd PID=416884 LEV=[info] Accepted gssapi-with-mic for UE016550 from 10.23.202.50 port 49303 ssh2",
						"2014-09-01 09:35:44,1409564144,10.23.202.50,npatas1/npatas1,UE016550,Accepted,gssapi-with-mic,,,,false,,,,"

				)



		);
	}

}
