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
public class PrinterLogTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/parsePRNLOG.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readPRNLOG_enrich.conf";



	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpClass() {
		new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
	}

	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-collection-test.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.prnlog.table.fields");
		List<String> prnlogFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[]{confFile, confEnrichmentFile}, prnlogFields);

	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();

	}

	@Test
	@Parameters
	public void test(String testCase, Object[] lines, Object[] outputs) {

		List<String> events = new ArrayList<String>(lines.length);
		for (Object line : lines) {
			events.add((String) line);
		}
		List<String> expected = new ArrayList<String>(outputs.length);
		for (Object output : outputs) {
			expected.add((String) output);
		}

		morphlineTester.testMultipleLines(testCase, events , expected);
	}


	private Object[] parametersForTest() {
		return	$(
					$(
							"Regular Event",
							$(
							  "\"2016-01-18T16:58:28.000+0200\",set,PrintJob,\"HP LaserJet Pro MFP M127-M128 PCLmS\",\"\\\\192.168.0.49\",\"ori_m\",\"Microsoft Word - suspicious.docx\",\"ori_m\",11,RAW,\"MS_XPS_PROC\",,\"HP LaserJet Pro MFP M127-M128 PCLmS\",printing,1,1,94241,\"01/18/2016 14:31:50.762\",0",
							  "\"2016-01-19T15:22:09.000+0200\",set,PrintJob,\"HP LaserJet Pro MFP M127-M128 PCLmS\",\"ORI-LAPTOP\",\"ori_m\",\"Microsoft Word - snowden.docx\",\"ori_m\",12,RAW,\"MS_XPS_PROC\",,\"HP LaserJet Pro MFP M127-M128 PCLmS\",spooling,1,1,1294,\"01/19/2016 13:22:08.967\",0"
							),
							$(
								"2016-01-18 16:58:28,1453136308,ori_m,,192.168.0.49,,,,,,HP LaserJet Pro MFP M127-M128 PCLmS,,,,,,,,,,,printing,,,,,,94241,Microsoft Word - suspicious.docx,1,false",
								 "2016-01-19 15:22:09,1453216929,ori_m,,,ORI-LAPTOP,,,,,HP LaserJet Pro MFP M127-M128 PCLmS,,,,,,,,,,,spooling,,,,,,1294,Microsoft Word - snowden.docx,1,"

							)
					),
					$(
							"Empty Fields",
							$(
								"\"2016-01-18T16:58:28.000+0200\",set,PrintJob,\"HP LaserJet Pro MFP M127-M128 PCLmS\",\"\\\\192.168.0.49\",,\"Microsoft Word - suspicious.docx\",\"ori_m\",11,RAW,\"MS_XPS_PROC\",,\"HP LaserJet Pro MFP M127-M128 PCLmS\",printing,1,1,94241,\"01/18/2016 14:31:50.762\",0",
                                ",set,PrintJob,\"HP LaserJet Pro MFP M127-M128 PCLmS\",\"\\\\192.168.0.49\",\"ori_m\",\"Microsoft Word - suspicious.docx\",\"ori_m\",11,RAW,\"MS_XPS_PROC\",,\"HP LaserJet Pro MFP M127-M128 PCLmS\",printing,1,1,94241,\"01/18/2016 14:31:50.762\",0",
								"\"2016-01-18T16:58:28.000+0200\",set,PrintJob,\"HP LaserJet Pro MFP M127-M128 PCLmS\",,\"ori_m\",\"Microsoft Word - suspicious.docx\",\"ori_m\",11,RAW,\"MS_XPS_PROC\",,\"HP LaserJet Pro MFP M127-M128 PCLmS\",printing,1,1,94241,\"01/18/2016 14:31:50.762\",0"
							),
							$(
									(String)null,
									(String)null,
									(String)null
							)
					)

		);
	}


}
