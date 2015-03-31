package fortscale.collection.morphlines;

import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class AMTWithHistoricalParsingTest {
	private static ClassPathXmlApplicationContext testContextManager;

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String parsConfFile ="resources/conf-files/processAMTParsingHistoricalEvents.conf";
	private String logicConfFile = "resources/conf-files/readAMT_enrich.conf";



	@BeforeClass
	public static void setUpClass(){
		testContextManager = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test.xml");

	}

	@AfterClass
	public static void finalizeTestClass(){
		testContextManager.close();
		testContextManager = null;
	}


	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.amt.table.fields");
		List<String> amtOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[] {parsConfFile,logicConfFile}, amtOutputFields);
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


	@SuppressWarnings("unused")
	private Object[] parametersForTest() {
		return	$(



				$(
						"regular line - Ongoing events",
						$(
								"Mon Nov  3 00:13:37 2014,192.168.150.51,192.168.70.50,ip_angela1123,M5781,UNAUTHACCESS,Attempted unauth access,6ao697ha5ecol&b=3&s=60,,54573930"
						),
						$(
								"2014-11-03 00:13:37,1414973617,192.168.70.50,,UNKNOWN,192.168.150.51,,ip_angela1123,SUCCESS,,,false,false,false,false,M5781,UNAUTHACCESS,Attempted unauth access,6ao697ha5ecol&b=3&s=60,Reserved Range,Reserved Range,ip_angela1123"
						)
				),

				$(
						"MOBILELOOKUP Event",
						$(
								"Sun Aug 10 17:16:17 2014,192.168.150.51,192.168.70.50,guerro18,Mobile = 1-4438663599, yid = statefun48,MOBILELOOKUP,Success,7jhdshh9ua1r7&b=4&d=mG.m.x1pYEJ.syjM.GkFlZGsgwi.gM3OKzzyhA--&s=7t&i=ou8lkeQLbEQ.WkpA7FeN,,53e80b51"
						),
						$(
								"2014-08-10 17:16:17,1407690977,192.168.70.50,,UNKNOWN,192.168.150.51,,guerro18,SUCCESS,,,false,false,false,false,statefun48,MOBILELOOKUP,Success,7jhdshh9ua1r7&b=4&d=mG.m.x1pYEJ.syjM.GkFlZGsgwi.gM3OKzzyhA--&s=7t&i=ou8lkeQLbEQ.WkpA7FeN,Reserved Range,Reserved Range,guerro18"
						)
				),

				$(
						"MOBILELOOKUP Event with no yid",
						$(
								"Sun Aug 10 17:16:17 2014,192.168.150.51,192.168.70.50,guerro18,Mobile = 1-4438663599, yid = ,MOBILELOOKUP,Success,7jhdshh9ua1r7&b=4&d=mG.m.x1pYEJ.syjM.GkFlZGsgwi.gM3OKzzyhA--&s=7t&i=ou8lkeQLbEQ.WkpA7FeN,,53e80b51"
						),
						$(
								"2014-08-10 17:16:17,1407690977,192.168.70.50,,UNKNOWN,192.168.150.51,,guerro18,SUCCESS,,,false,false,false,false,,MOBILELOOKUP,Success,7jhdshh9ua1r7&b=4&d=mG.m.x1pYEJ.syjM.GkFlZGsgwi.gM3OKzzyhA--&s=7t&i=ou8lkeQLbEQ.WkpA7FeN,Reserved Range,Reserved Range,guerro18"
						)
				),

				$(
						"EMAILLOOKUP Event",
						$(
								"Sun Aug 10 17:18:29 2014,192.168.150.51,192.168.70.50,disseldorf,Comm = doyouknowwhoami, yid = doyouknowwhoami,EMAILLOOKUP,Success,9uh496t9sal39&b=4&d=embzJt9rYH1_CEyzNVM8jUl8elF5TwXiZxqrSt67qlG9JQ--&s=5h,,53e80bd5"
						),
						$(
								"2014-08-10 17:18:29,1407691109,192.168.70.50,,UNKNOWN,192.168.150.51,,disseldorf,SUCCESS,,,false,false,false,false,doyouknowwhoami,EMAILLOOKUP,Success,9uh496t9sal39&b=4&d=embzJt9rYH1_CEyzNVM8jUl8elF5TwXiZxqrSt67qlG9JQ--&s=5h,Reserved Range,Reserved Range,disseldorf"
						)
				),

				$(
						"EMAILLOOKUP Event with no yid",
						$(
								"Sun Aug 10 17:18:29 2014,192.168.150.51,192.168.70.50,disseldorf,Comm = doyouknowwhoami, yid = ,EMAILLOOKUP,Success,9uh496t9sal39&b=4&d=embzJt9rYH1_CEyzNVM8jUl8elF5TwXiZxqrSt67qlG9JQ--&s=5h,,53e80bd5"
						),
						$(
								"2014-08-10 17:18:29,1407691109,192.168.70.50,,UNKNOWN,192.168.150.51,,disseldorf,SUCCESS,,,false,false,false,false,,EMAILLOOKUP,Success,9uh496t9sal39&b=4&d=embzJt9rYH1_CEyzNVM8jUl8elF5TwXiZxqrSt67qlG9JQ--&s=5h,Reserved Range,Reserved Range,disseldorf"
						)
				),

				$(
						"GUIDLOOKUP Event",
						$(
								"Sun Aug 10 18:06:46 2014,192.168.150.51,192.168.70.50,byp_quelindio,GUID = 6ZRF5HL2UGX4HKANJGNUFXT27E, yid = rollingsun18,GUIDLOOKUP,Success,f00k4399ud6no&b=3&s=hd,,53e81726"
						),
						$(
								"2014-08-10 18:06:46,1407694006,192.168.70.50,,UNKNOWN,192.168.150.51,,byp_quelindio,SUCCESS,,,false,false,false,false,rollingsun18,GUIDLOOKUP,Success,f00k4399ud6no&b=3&s=hd,Reserved Range,Reserved Range,byp_quelindio"
						)
				),

				$(
						"drop record without username - Ongoing events",
						$(
								"Mon Nov  3 00:13:37 2014,192.168.150.51,192.168.70.50,,M5781,UNAUTHACCESS,Attempted unauth access,6ao697ha5ecol&b=3&s=60,,54573930"
						),
						$(
								(String)null
						)
				)



		);


	}

}
