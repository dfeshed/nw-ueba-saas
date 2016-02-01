package fortscale.collection.morphlines.securityevents.optimal;

import fortscale.collection.morphlines.MorphlinesTester;
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

@RunWith(JUnitParamsRunner.class)
public class SecEvents4769Test {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/securityevents/default/readSecEvt.conf";
	private String conf4769File = "resources/conf-files/securityevents/default/processSecEvt4769.conf";
	private String confSecEnrich = "resources/conf-files/enrichment/readSEC_enrich.conf";

	
	@SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }

	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.kerberos_logins.table.morphline.fields");
		List<String> splunkSecEventsOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		List<String> splunkSecEventsOutputFieldsExcludingEnrichment = new ArrayList<>();
		for(String field: splunkSecEventsOutputFields){
			//if(!field.equals("machine_name")){
			splunkSecEventsOutputFieldsExcludingEnrichment.add(field);
			//}
		}
		morphlineTester.init(new String[] { confFile, conf4769File,confSecEnrich }, splunkSecEventsOutputFieldsExcludingEnrichment);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
	}

	@Test
	@Parameters
	public void test(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}


	@SuppressWarnings("unused")
	private Object[] parametersForTest() {
		return	$(
				$ (
						"Successfull 4769 Event",
						"4769,2016-01-30T10:00:00,sacads401.deltads.ent,0x408,10.33.48.78,deltads.ent,sacmsgcht102$,service_id,0x0,123456",
						"2016-01-30T10:00:00,2016-01-30T10:00:00,Kerberos Service Ticket Operations,4769,Security,123456,Microsoft Windows security auditing.,sacads401.deltads.ent,deltads.ent,sacmsgcht102,service_id,10.33.48.78,0x408,0x0,,1454148000,,false,,,,,,,,,"
				)
		);
	}


}
