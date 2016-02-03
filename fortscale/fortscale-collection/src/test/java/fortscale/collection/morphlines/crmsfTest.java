package fortscale.collection.morphlines;

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

import java.util.ArrayList;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

/**
 * Created by idanp on 12/17/2015.
 */

@RunWith(JUnitParamsRunner.class)
public class crmsfTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/parseCRMSF.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readCRMSF_enrich.conf";



	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpClass() {
		new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
	}

	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-collection-test.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.crmsf.table.fields");
		List<String> crmsfOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[]{confFile, confEnrichmentFile}, crmsfOutputFields);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();

	}

	@Test
	@Parameters
	public void test(String testCase, Object[] lines, Object[] outputs) {

		List<String> events = new ArrayList<String>(lines.length);
		for (Object line : lines)
			events.add((String)line);

		List<String> expected = new ArrayList<String>(outputs.length);
		for (Object output : outputs)
			expected.add((String)output);

		morphlineTester.testMultipleLines(testCase, events , expected);
	}


	private Object[] parametersForTest() {
		return	$(
				$(
						"Regular Event",
						$(
						  "idan@fortscale.com,\"12/6/2015 3:31 AM\",204.14.234.8,Remote Access 2.0,Success,Chrome 47,Windows 7,Salesforce Help & Training,N/A,N/A,N/A,login.salesforce.com,US,United States,California,San Francisco,94105,37.7898,-122.3942",
					      "idan@fortscale.com,\"12/6/2015 3:31 AM\",,Remote Access 2.0,Success,Unknown,Safari 23,Salesforce Help & Training,N/A,N/A,N/A,login.salesforce.com,US,United States,California,San Francisco,94105,37.7898,-122.3942",
						  "idan@fortscale.com,12/6/2015 3:31 AM,204.14.234.8,Remote Access 2.0,Success,IE 5,Mac OSX,Salesforce Help & Training,N/A,N/A,N/A,login.salesforce.com,US,United States,California,San Francisco,94105,37.7898,-122.3942",
						  "user109f,12/5/2015 02:26,67.228.168.140,Remote Access 2.0,Success,Unknown,Unknown,Pardot + Salesforce Connector,login.salesforce.com"
						),
						$(
						   "2015-12-06 03:31:00,1449372660,idan@fortscale.com,,204.14.234.8,,,,,,,,,,,,,,,,,,,,,,,,Login,Success,,,,,Remote Access 2.0,Chrome 47,Windows 7,Salesforce Help & Training,login.salesforce.com,false,",
							(String)null,
						   "2015-12-06 03:31:00,1449372660,idan@fortscale.com,,204.14.234.8,,,,,,,,,,,,,,,,,,,,,,,,Login,Success,,,,,Remote Access 2.0,IE 5,Mac OSX,Salesforce Help & Training,login.salesforce.com,false,",
						   "2015-12-05 02:26:00,1449282360,user109f,,67.228.168.140,,,,,,,,,,,,,,,,,,,,,,,,Login,Success,,,,,Remote Access 2.0,Unknown,Unknown,Pardot + Salesforce Connector,,false,"
						)
				)

		);
	}


}
