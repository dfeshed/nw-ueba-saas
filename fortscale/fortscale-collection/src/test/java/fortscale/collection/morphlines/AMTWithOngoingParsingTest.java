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

/**
 * Created by idanp on 11/23/2014.
 */

@RunWith(JUnitParamsRunner.class)
public class AMTWithOngoingParsingTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String parsConfFile ="resources/conf-files/processAMTParsingOngoingEvents.conf";
	private String logicConfFile = "resources/conf-files/enrichment/readAMT_enrich.conf";



	@SuppressWarnings("resource")
	@BeforeClass
	public static void setUpClass(){
		new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");

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
						"Test second type of lines - mobile,email,comm - Ongoing events",
						$(
								"2014-10-01T05:00:30.980+00:00|10.0.0.1|10.0.0.2|nicholle|Comm = cailsg1, yid = cailsg1|EMAILLOOKUP|EMAILLOOKUP|19oqra9a2309f&b=4&d=vU6UaP1pYEKqIGqMUbyy5SwboOh-&s=4e&i=ZwYi5Z71ltrVGw4r39rR"
						),
						$(
								"2014-10-01 05:00:30,1412139630,10.0.0.2,,Unknown,,10.0.0.1,,nicholle,SUCCESS,,,false,false,false,false,cailsg1,EMAILLOOKUP,EMAILLOOKUP,19oqra9a2309f&b=4&d=vU6UaP1pYEKqIGqMUbyy5SwboOh-&s=4e&i=ZwYi5Z71ltrVGw4r39rR,,,"
						)
				),

				$(
						"Test second type of lines - mobile,email,comm - 2 - Ongoing events ",
						$(
								"2014-10-01T05:00:30.980+00:00|10.0.0.1|10.0.0.2|nicholle| yid = cailsg1, Comm = cailsg1|EMAILLOOKUP|EMAILLOOKUP|19oqra9a2309f&b=4&d=vU6UaP1pYEKqIGqMUbyy5SwboOh-&s=4e&i=ZwYi5Z71ltrVGw4r39rR"
						),
						$(
								"2014-10-01 05:00:30,1412139630,10.0.0.2,,Unknown,,10.0.0.1,,nicholle,SUCCESS,,,false,false,false,false,cailsg1,EMAILLOOKUP,EMAILLOOKUP,19oqra9a2309f&b=4&d=vU6UaP1pYEKqIGqMUbyy5SwboOh-&s=4e&i=ZwYi5Z71ltrVGw4r39rR,,,"
						)
				),


				$(
						"regular line - Ongoing events",
						$(
								"2014-10-10T07:41:48.000+00:00|10.0.0.1|10.0.0.2|rashid|nanu|VIEW|145342234|1ser2ef&b=234234%s=6v"
						),
						$(
								"2014-10-10 07:41:48,1412926908,10.0.0.2,,Unknown,,10.0.0.1,,rashid,SUCCESS,,,false,false,false,false,nanu,VIEW,145342234,1ser2ef&b=234234%s=6v,,,"
						)
				),
				$(
						"drop record without username - Ongoing events",
						$(
								"2014-08-25T00:28:54.000+00:00|10.0.0.1|10.0.0.2|||||"
						),
						$(
								(String)null
						)
				)



		);


	}
}
