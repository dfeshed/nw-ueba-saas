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


@RunWith(JUnitParamsRunner.class)
public class SshCiscoTest {

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
                $(
                        "Successful Public Key Authentication",
                        "\"2016-03-03T13:08:14+00:00\",\"c7-a1-ow2-xdr-10\",\"Accepted\",\"publickey\",\"oamuser\",\"173.37.137.37\"",
                        "2016-03-03 13:08:14,1457010494,173.37.137.37,c7-a1-ow2-xdr-10,oamuser,Accepted,publickey,,,,false,,,,,"



                ),
                $(
                        "test from contain 127.0.0.1  ",
                        "\"2016-03-03T13:08:14+00:00\",\"caisisapp-prf1-07\",\"Accepted\",\"publickey\",\"root\",\"127.0.0.1\"",
                        "2016-03-03 13:08:14,1457010494,caisisapp-prf1-07,caisisapp-prf1-07,root,Accepted,publickey,,,,false,,,,,"


                ),
                $(
                        "Target Machine as IP",
                        "\"2016-03-03T13:08:14+00:00\",\"134.12.55.15\",\"Accepted\",\"publickey\",\"root\",\"72.163.46.51\"",
                        "2016-03-03 13:08:14,1457010494,72.163.46.51,134.12.55.15,root,Accepted,publickey,,,,false,,,,,"
                ),
                $(
                        "Successful Keyboard Interactive Authentication",
                        "\"2016-03-03T13:08:14+00:00\",\"c7-a1-ow2-xdr-10\",\"Accepted\",\"keyboard-interactive\",\"oamuser\",\"173.37.137.37\"",
                        "2016-03-03 13:08:14,1457010494,173.37.137.37,c7-a1-ow2-xdr-10,oamuser,Accepted,keyboard-interactive,,,,false,,,,,"
                ),
                $(
                        "Successful Keyboard Interactive / PAM Authentication",
                        "\"2016-03-03T13:08:14+00:00\",\"c7-a1-ow2-xdr-10\",\"Accepted\",\"keyboard-interactive/pam\",\"oamuser\",\"173.37.137.37\"",
                        "2016-03-03 13:08:14,1457010494,173.37.137.37,c7-a1-ow2-xdr-10,oamuser,Accepted,keyboard-interactive/pam,,,,false,,,,,"
                )
        );
    }

}
