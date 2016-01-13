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
	private String confFile = "resources/conf-files/parseGWAME.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readGWAME_enrich.conf";



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
								"\"2015-12-07T10:27:20.000+0200\",4728,FORTSCALE,amirf,\"FORTSCALE\\avil\",\"Fs-DC-01.Fortscale.dom\",\"CN=Avi Legman.|OU=Fortscale-Users|DC=Fortscale|DC=dom\",\"Wifi-Users\",FORTSCALE,,"

						),
						$(
								"2015-12-07 08:27:20,1449476840,amirf,Fortscale.dom,avil,Fortscale.dom,Addition To A Security Global Group,SUCCESS,Wifi-Users,FORTSCALE"

						)
				),
				$(
						"target SID as sireal Event",
						$(
								"\"2015-10-02T14:52:00.000+0300\",4728,FORTSCALE,amirf,\"S-1-5-21-2944713389-4249601353-3095880226-0\",\"Fs-DC-01.Fortscale.dom\",\"cn=Amir Kerenvpn|OU=Fortscale-Users|DC=Fortscale|DC=dom\",\"VPN-Users\",FORTSCALE,,"

						),
						$(
								"2015-10-02 11:52:00,1443786720,amirf,Fortscale.dom,S-1-5-21-2944713389-4249601353-3095880226-0,Fortscale.dom,Addition To A Security Global Group,SUCCESS,VPN-Users,FORTSCALE"

								)
				),
				$(
						"Group info from the group account fields ",
						$(
								"\"2015-04-01T17:23:34.000+0300\",4756,FORTSCALE,victorb,\"FORTSCALE\\manager\",\"Fs-DC-01.Fortscale.dom\",\"CN=manager|CN=Users|DC=Fortscale|DC=dom\",,,\"Recipient Management\",FORTSCALE"

						),
						$(
								"2015-04-01 14:23:34,1427898214,victorb,Fortscale.dom,manager,Fortscale.dom,Addition To A Security Universal Group,SUCCESS,Recipient Management,FORTSCALE"

						)
				)

		);
	}

}
