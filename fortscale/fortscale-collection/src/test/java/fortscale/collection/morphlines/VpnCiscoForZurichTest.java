package fortscale.collection.morphlines;

import fortscale.domain.events.dao.VpnSessionRepository;
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
public class VpnCiscoForZurichTest {

	private static ClassPathXmlApplicationContext testContextManager;

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/readVPN_Cisco_forZurich.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readVPN_enrich.conf";

	@BeforeClass
	public static void setUpClass(){
		testContextManager = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test.xml");
		VpnSessionRepository vpnSessionRepository = testContextManager.getBean(VpnSessionRepository.class);
		vpnSessionRepository.deleteAll();
	}

	@AfterClass
	public static void finalizeTestClass(){
		testContextManager.close();
		testContextManager = null;
	}

	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.vpn.table.morphline.fields");
		List<String> vpnOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[] {confFile, confEnrichmentFile}, vpnOutputFields);
	}

	@After
	public void tearDown() throws Exception {
		morphlineTester.close();
		VpnSessionRepository vpnSessionRepository = testContextManager.getBean(VpnSessionRepository.class);
		vpnSessionRepository.deleteAll();
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

				/*
				Date,Time,User-Name,Group-Name,Calling-Station-Id,Acct-Status-Type,Acct-Session-Id,Acct-Session-Time,Service-Type,Framed-Protocol,Acct-Input-Octets,Acct-Output-Octets,Acct-Input-Packets,Acct-Output-Packets,Framed-IP-Address,NAS-Port,NAS-IP-AddressRADIUS1
				10/8/2014,12:28:18,uswh90d,Default Group,82.166.88.97,Stop,5245FDFC,86402,Framed,PPP,3604725223,92294191,2574220,2168428,10.128.0.214,212660224,10.148.1.71
				10/10/2014,9:18:50,uswh90d,Default Group,82.166.88.97,Start,C835EF40,,Framed,PPP,,,,,10.128.0.214,195923968,10.148.1.72
				 */


				$(
						"session start",
						// original records
						$(	"10/13/2014,2:20:42,uswh90d,Default Group,82.166.88.97,Start,C825FF9E,,Framed,PPP,,,,,10.128.0.214,217886720,10.148.1.72", // valid record
							"10/13/2014,2:20:43,uswh90d,Default Group,82.166.88.97,Start,C825FF9E,,Framed,PPP,,,,,,217886720,10.148.1.72", // no local-IP - valid record
							"10/13/2014,2:20:42,uswh90d,Default Group,,Start,C825FF9E,,Framed,PPP,,,,,10.128.0.214,217886720,10.148.1.72" // no source-IP - dropping record
						),
						// response
						$(	"2014-10-13 02:20:42,1413166842,uswh90d,82.166.88.97,10.128.0.214,SUCCESS,,,,,,,,,,,,,,",
							"2014-10-13 02:20:43,1413166843,uswh90d,82.166.88.97,,SUCCESS,,,,,,,,,,,,,,",
								(String)null
						)
				),

				$(
						"session start - with WAN enrichment",
						// original records
						$(	"10/13/2014,2:20:42,uswh90d,Default Group,82.166.88.97,Start,C825FF9E,,Framed,PPP,,,,,10.128.0.214,217886720,10.148.1.72 Flume enrichment timezone UTC", // valid record
								"10/13/2014,2:20:43,uswh90d,Default Group,82.166.88.97,Start,C825FF9E,,Framed,PPP,,,,,,217886720,10.148.1.72 Flume enrichment timezone UTC", // no local-IP - valid record
								"10/13/2014,2:20:42,uswh90d,Default Group,,Start,C825FF9E,,Framed,PPP,,,,,10.128.0.214,217886720,10.148.1.72 Flume enrichment timezone UTC" // no source-IP - dropping record
						),
						// response
						$(	"2014-10-13 02:20:42,1413166842,uswh90d,82.166.88.97,10.128.0.214,SUCCESS,,,,,,,,,,,,,,",
								"2014-10-13 02:20:43,1413166843,uswh90d,82.166.88.97,,SUCCESS,,,,,,,,,,,,,,",
								(String)null
						)
				),

				$(
						"session end",
						// original records
						$(	"10/13/2014,8:09:45,uswh90d,Default Group,82.166.88.97,Stop,C825FF9E,20943,Framed,PPP,9199349,19097369,62424,61192,10.128.59.201,217886720,10.148.1.72", // valid record
							"10/13/2014,8:09:45,uswh90d,Default Group,82.166.88.97,Stop,C825FF9E,20943,Framed,PPP,9199349,19097369,62424,61192,,217886720,10.148.1.72", // no local-IP - valid record
							"10/13/2014,8:09:45,uswh90d,Default Group,,Stop,C825FF9E,20943,Framed,PPP,9199349,19097369,62424,61192,10.128.59.201,217886720,10.148.1.72" // no source-IP - dropping record
						),
						// response
						$(	"2014-10-13 08:09:45,1413187785,uswh90d,82.166.88.97,10.128.59.201,CLOSED,,,,,,,,28296718,9199349,19097369,20943,,,",
							"2014-10-13 08:09:45,1413187785,uswh90d,82.166.88.97,,CLOSED,,,,,,,,28296718,9199349,19097369,20943,,,",
								(String)null
						)
				)

				);
	}


}
