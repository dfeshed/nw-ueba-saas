package fortscale.collection.morphlines;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static junitparams.JUnitParamsRunner.$;


@RunWith(JUnitParamsRunner.class)
public class SshSshdTest {


	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readSSH_centos.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readSSH_enrich.conf";

	final static String Nov_19_14_58_32 = "Nov 19 14:58:32";
	static String Nov_19_14_58_32_OUT;

	static {
		prepareDates();
	}

	private static void prepareDates() {
		TestUtils.init("yyyy MMM dd HH:mm:ss", "Asia/Jerusalem");
		Date date = TestUtils.constuctDate(Nov_19_14_58_32);
		Nov_19_14_58_32_OUT = TestUtils.getOutputDate(date);
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


        //10 Represent Nov Month at calendar (counting start from 0)
		if (currentMonth < 10 || (currentMonth == 10 && currentDay < 19 ))
			year--;

		long runtime = ((new DateTime(year, 11, 19, 14, 58, 32, DateTimeZone.UTC).getMillis()) / 1000L);

        return	$(
        		$ (
        		"Successful Password Authentication",
						Nov_19_14_58_32 + " dev-gever sshd[30431]: Accepted password for root from 192.168.200.254 port 62257 ssh2",
						Nov_19_14_58_32_OUT + "," + runtime + ",192.168.200.254,dev-gever,root,Accepted,password,,,,false,,false,false,false"
				),
        		$ (
        		"Successful Public Key Authentication",
						Nov_19_14_58_32 + " dev-gever sshd[2591]: Accepted publickey for root from 192.168.55.55 port 38681 ssh2",
						Nov_19_14_58_32_OUT + "," + runtime + ",192.168.55.55,dev-gever,root,Accepted,publickey,,,,false,,false,false,false"),
        		$ (
        		"Successful Public Key Authentication from NAT address",
						Nov_19_14_58_32 + " dev-gever sshd[2591]: Accepted publickey for root from 192.168.0.22 port 38681 ssh2",
						Nov_19_14_58_32_OUT + "," + runtime + ",192.168.0.22,dev-gever,root,Accepted,publickey,,,,true,,false,false,false"),
        		$ (
        		"Password Failed Authentication",
						Nov_19_14_58_32 + " inter-psg-01 sshd[22525]: Failed password for root from 192.168.211.112 port 59420 ssh2",
						Nov_19_14_58_32_OUT + "," + runtime + ",192.168.211.112,inter-psg-01,root,Failed,password,,,,false,,false,false,false"),
        		$ (

        		"Invalid User Failed Authentication",
        		"Jul 7 10:53:24 chaves sshd[12914]: Failed password for invalid user test-inv from spongebob.lab.ossec.net",
        		null)
                ,
                $(
                "Target Machine as IP",
						Nov_19_14_58_32 + " 192.168.0.30 sshd[30431]: Accepted password for root from 192.168.200.254 port 62257 ssh2",
                 null
                )



        );
    }

}
