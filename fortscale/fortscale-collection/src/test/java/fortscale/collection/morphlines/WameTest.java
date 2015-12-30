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
 * Created by idanp on 12/22/2015.
 */

@RunWith(JUnitParamsRunner.class)
public class WameTest {


	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/parseWAME.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readWAME_enrich.conf";



	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpClass() {
		new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
	}

	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-collection-test.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.wame.table.fields");
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
								"\"2015-12-22T15:04:24.000+0000\",4724,\"IDAN-DEV\",\"Idan.Admin.B\",\"TEST-DEV\",usr1",
								"\"2015-12-22T15:04:24.000+0000\",4724,\"IDAN-DEV\",,\"TEST-DEV\",usr1",
								"\"2015-12-22T15:04:24.000+0000\",4724,\"IDAN-DEV\",\"Idan.Admin.B\",\"TEST-DEV\",usr1"
						),
						$(
								"2015-12-22 15:04:24,1450796664,Idan.Admin.B,IDAN-DEV,,,,,,,,,,,,,,,,,,,,,,,,Password Reset,SUCCESS,,,,,,TEST-DEV,,Password Reset",
								(String)null,
								"2015-12-22 15:04:24,1450796664,Idan.Admin.B,IDAN-DEV,,,,,,,,,,,,,,,,,,,,,,,,Password Reset,SUCCESS,,,,,,TEST-DEV,,Password Reset"
						)
				)

		);
	}

}
