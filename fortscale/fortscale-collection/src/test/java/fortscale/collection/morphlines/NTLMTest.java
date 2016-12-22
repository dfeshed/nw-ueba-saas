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
 * tests NTLM's etl process of a single row - parse and enrichment
 * Created by galiar on 29/12/2015.
 */

@RunWith(JUnitParamsRunner.class)
public class NTLMTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/parseNTLM.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readNTLM_enrich.conf";

	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpClass() {
		new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
	}

	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-collection-test.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.ntlm.table.fields");
		List<String> ntlmOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[]{confFile, confEnrichmentFile}, ntlmOutputFields);
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
								"\"2015-12-10T05:43:11.000+0200\",esm,0x0,1.2.3.4,\"Fs-DC-01.Fortscale.dom\"",
								"\"2015-12-10T05:43:11.000+0200\",esm,0x0,MCAFEE,\"Fs-DC-01.Fortscale.dom\"",
								"\"2015-12-10T05:40:05.000+0200\",,\"splunk-ldap\",0x0,\"FS-DC-01\",\"Fs-DC-01.Fortscale.dom\"",
                                //To lower case test:
                                "\"2015-12-10T05:43:11.000+0200\",esm,0X6,MCAFEE,\"Fs-DC-01.Fortscale.dom\"",
								"2016-05-15T11:00:00.000+0000,TEST@test.com,0x0,\\\\TEST-PRD-03,TEST-TYO1-C1-6.test.com",
								"\"2015-12-10T05:43:11.000+0200\",esm,0x0,MCAFEE,\"192.168.1.26\""
						),
						$(
								(String)null,
								"2015-12-10 03:43:11,1449718991,esm,0x0,MCAFEE,Fs-DC-01.Fortscale.dom",
								(String)null,
                                "2015-12-10 03:43:11,1449718991,esm,0x6,MCAFEE,Fs-DC-01.Fortscale.dom",
								"2016-05-15 11:00:00,1463310000,TEST@test.com,0x0,TEST-PRD-03,TEST-TYO1-C1-6.test.com",
								"\"2015-12-10T05:43:11.000+0200\",esm,0x0,MCAFEE,\"192.168.1.26\""
						)
				)

		);
	}


}
