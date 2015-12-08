package fortscale.collection.morphlines;

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
	private String confFile = "resources/conf-files/readSSH_symantec.conf";
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
						"Dec 01 00:38:07 3.3.3.3 vsftpd: pam_unix(vsftpd:session): session opened for user username by (uid=0)\" (service map: <eventmap version=\"2\"><field name=\"vendor_severity\">info</field><field name=\"TimeOffset\">0</field><field name=\"facility\">security</field><field name=\"event_dt\">1448923087452</field><field name=\"reporting_sensor\">sensorname</field><field name=\"proxy_machine_ip\">3.3.3.3</field><field name=\"proxy_machine\">hostname</field></eventmap>)",
						Nov_19_14_58_32_OUT + "," + runtime + ",192.168.200.254,dev-gever,root,Accepted,password,,,,false,,,,"
				),
        		$ (
        		"Failed Authentication",
						"Dec 01 01:50:43 3.3.3.3 vsftpd: pam_unix(vsftpd:auth): authentication failure; logname= uid=0 euid=0 tty=ftp ruser=username rhost=1.1.1.1\" (service map: <eventmap version=\"2\"><field name=\"vendor_severity\">notice</field><field name=\"TimeOffset\">0</field><field name=\"facility\">security</field><field name=\"event_dt\">1448927443453</field><field name=\"reporting_sensor\">sensorname</field><field name=\"proxy_machine_ip\">3.3.3.3</field><field name=\"proxy_machine\">hostname</field></eventmap>)",
						Nov_19_14_58_32_OUT + "," + runtime + ",192.168.211.112,inter-psg-01,root,Failed,password,,,,false,,,,")
        );
    }

}
