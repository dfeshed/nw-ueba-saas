package fortscale.collection.morphlines.ssh;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.collection.morphlines.TestUtils;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class SshSymantecTest {


	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/ssh/readSSH_symantec.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readSSH_enrich.conf";

	final static String Nov_19_14_58_32 = "Nov 19 14:58:32";
	static String Nov_19_14_58_32_OUT;

	final static String Nov_19_14_58_32_WAN = "Nov 19 12:58:32";
	static String Nov_19_14_58_32_OUT_WAN;

	static {
		prepareDates();
	}

	private static void prepareDates() {
		TestUtils.init("yyyy MMM dd HH:mm:ss", "UTC");
		Date date = TestUtils.constuctDate(Nov_19_14_58_32);
		Nov_19_14_58_32_OUT = TestUtils.getOutputDate(date);
		date = TestUtils.constuctDate(Nov_19_14_58_32_WAN);
		Nov_19_14_58_32_OUT_WAN = TestUtils.getOutputDate(date);
	}
	
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


		int year = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);



		long runtime = ((new DateTime(year, 11, 19, 14, 58, 32, DateTimeZone.UTC).getMillis()) / 1000L);

        return	$(
        		$ (
        		"Successful Authentication",
						"Dec 01 06:00:02 1.1.1.1 Dec  1 06:00:02 server sshd[6150]: Accepted publickey for root from 1.1.1.1 port 62460 ssh2\" (service map: <eventmap version=\"2\"><field name=\"vendor_severity\">info</field><field name=\"TimeOffset\">0</field><field name=\"facility\">security</field><field name=\"event_dt\">1448942402321</field><field name=\"reporting_sensor\">sensor</field><field name=\"proxy_machine_ip\">1.1.1.1</field><field name=\"proxy_machine\">proxy_machine</field></eventmap>)",
						"2015-12-01 06:00:02,1448949602,1.1.1.1,proxy_machine,root,Accepted,,,,,,,,,,"
				),
        		$ (
        		"Failed Authentication",
						"</field><field name=\"TimeOffset\">0</field><field name=\"facility\">security</field><field name=\"event_dt\">1446708582725</field><field name=\"reporting_sensor\">sensor</field><field name=\"proxy_machine_ip\">3.3.3.3</field><field name=\"proxy_machine\">hostname</field></eventmap>)Nov 11 10:44:09 1.1.1.1 sshd[4855]: Failed password for root from 1.1.1.1 port 42450 ssh2\" (service map: <eventmap version=\"2\"><field name=\"vendor_severity\">info</field><field name=\"TimeOffset\">0</field><field name=\"facility\">security</field><field name=\"event_dt\">1447231449132</field><field name=\"reporting_sensor\">sensor</field><field name=\"proxy_machine_ip\">1.1.1.1</field><field name=\"proxy_machine\">proxy_machine</field></eventmap>)",
						"2015-11-11 10:44:09,1447238649,3.3.3.3,hostname,root,Failed,,,,,,,,,,")
        );
    }

}
