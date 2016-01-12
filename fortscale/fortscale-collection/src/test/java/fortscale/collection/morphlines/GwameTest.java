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
 * Created by idanp on 1/12/2016.
 */

@RunWith(JUnitParamsRunner.class)
public class GwameTest {
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
		String impalaTableFields = propertiesResolver.getProperty("impala.data.gwame.table.fields");
		List<String> wameOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[]{confFile, confEnrichmentFile}, wameOutputFields);
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
								"\"2015-12-07T10:27:20.000+0200\",4728,FORTSCALE,amirf,\"FORTSCALE\\avil\",\"Fs-DC-01.Fortscale.dom\",\"CN=Avi Legman.,OU=Fortscale-Users,DC=Fortscale,DC=dom\",\"Wifi-Users\",FORTSCALE,,",
								"\"2015-10-02T14:52:00.000+0300\",4728,FORTSCALE,amirf,\"S-1-5-21-2944713389-4249601353-3095880226-0\",\"Fs-DC-01.Fortscale.dom\",\"cn=Amir Kerenvpn,OU=Fortscale-Users,DC=Fortscale,DC=dom\",\"VPN-Users\",FORTSCALE,,"

						),
						$(
								"2015-12-22 15:04:24,1450796664,Idan.Admin.B,,,,,,,,,,,,,,SUCCESS,,,,,Password Reset,FORSTCALE.DOM,usr1,FORSTCALE.DOM,",
								(String)null

						)
				)

		);
	}

}
