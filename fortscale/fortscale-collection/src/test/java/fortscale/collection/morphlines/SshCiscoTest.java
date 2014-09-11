package fortscale.collection.morphlines;

import static junitparams.JUnitParamsRunner.$;

import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;


@RunWith(JUnitParamsRunner.class)
public class SshCiscoTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSSH_cisco.conf";


	@Before
	public void setUp() throws Exception {


        PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.ssh.table.morphline.fields");

		List<String> sshOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[] { confFile }, sshOutputFields);
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
        		"2014-09-09T17:00:00+00:00 dev-gever sshd[2552]: Accepted publickey for root from 171.70.163.169 port 47952 ssh2",
        		"2014-09-09 17:00:00,1410271200,171.70.163.169,dev-gever,root,Accepted,publickey,,,,false,false,false,false,false")	
        );
    }

}
