package fortscale.collection.morphlines.securityevents.optimal;

import fortscale.collection.FsParametrizedTest;
import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class,
		locations = {"classpath*:META-INF/spring/collection-context-test-mocks.xml"})
//used to clean spring context for next class:
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class SecEvents4768Test extends FsParametrizedTest{

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/default/readSecEvt.conf";
	private String conf4768File = "resources/conf-files/securityevents/default/processSecEvt4768.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";

	public SecEvents4768Test(String testCase, String line, String output) {
		super(testCase, line, output);
	}

	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.security.events.login.table.morphline.fields");
		List<String> splunkSecEventsOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		List<String> splunkSecEventsOutputFieldsExcludingEnrichment = new ArrayList<>();
		for(String field: splunkSecEventsOutputFields){
			if(!field.equals("machine_name")){
				splunkSecEventsOutputFieldsExcludingEnrichment.add(field);
			}
		}
		morphlineTester.init(new String[] { confFile, conf4768File , confSecEnrich }, splunkSecEventsOutputFieldsExcludingEnrichment);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}

	@Test
	@Parameters(name = "{index} {1}")
	public void test() {
		morphlineTester.testSingleLine(testCase, line, output);
	}

	@Parameters()
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][]
		{
				{
						"4768 Event",
						"4768,2016-01-30T10:00:00,ga23418$,0x408,10.33.48.78,password has expired,deltads.ent",
						"2016-01-30T10:00:00,2016-01-30T10:00:00,1454148000,ga23418$,0x408,deltads.ent,4768,10.33.48.78,,,10.33.48.78,password has expired,0x408,False,False,False,False,False,False,false,,,,,,"
				}
		}
		);
	}

}
