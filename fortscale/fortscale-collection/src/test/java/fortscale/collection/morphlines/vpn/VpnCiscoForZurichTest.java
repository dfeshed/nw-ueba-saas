package fortscale.collection.morphlines.vpn;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.domain.events.dao.VpnSessionRepository;
import fortscale.collection.FsParametrizedMultiLineTest;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.junit.SpringAware;
import fortscale.utils.properties.PropertiesResolver;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;


@RunWith(Parameterized.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class,
		locations = {"classpath*:META-INF/spring/collection-context-test-mocks.xml"})
//used to clean spring context for next class:
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class VpnCiscoForZurichTest extends FsParametrizedMultiLineTest {

	//rules used to set JUnit parameters in SpringAware
	@ClassRule
	public static final SpringAware SPRING_AWARE = SpringAware.forClass(VpnCiscoForZurichTest.class);
	@Rule
	public TestRule springAwareMethod = SPRING_AWARE.forInstance(this);

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/vpn/readVPN_Cisco_forZurich.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readVPN_enrich.conf";

	public VpnCiscoForZurichTest(String testCase, Object[] lines, Object[] outputs) {
		super(testCase, lines, outputs);
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
	}

	@Test
	@Parameters(name = "{index} {1}")
	public void test() {

		List<String> events = new ArrayList<String>(lines.length);
		for (Object line : lines)
			events.add((String)line);

		List<String> expected = new ArrayList<String>(outputs.length);
		for (Object output : outputs)
			expected.add((String)output);

		morphlineTester.testMultipleLines(testCase, events , expected);
	}

	@Parameters()
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][]
						{


				/*
				Date,Time,User-Name,Group-Name,Calling-Station-Id,Acct-Status-Type,Acct-Session-Id,Acct-Session-Time,Service-Type,Framed-Protocol,Acct-Input-Octets,Acct-Output-Octets,Acct-Input-Packets,Acct-Output-Packets,Framed-IP-Address,NAS-Port,NAS-IP-AddressRADIUS1
				10/8/2014,12:28:18,uswh90d,Default Group,82.166.88.97,Stop,5245FDFC,86402,Framed,PPP,3604725223,92294191,2574220,2168428,10.128.0.214,212660224,10.148.1.71
				10/10/2014,9:18:50,uswh90d,Default Group,82.166.88.97,Start,C835EF40,,Framed,PPP,,,,,10.128.0.214,195923968,10.148.1.72
				 */


								{
										"session start",
										// original records
										$("10/13/2014,2:20:42,uswh90d,Default Group,82.166.88.97,Start,C825FF9E,,Framed,PPP,,,,,10.128.0.214,217886720,10.148.1.72", // valid record
												"10/13/2014,2:20:43,uswh90d,Default Group,82.166.88.97,Start,C825FF9E,,Framed,PPP,,,,,,217886720,10.148.1.72", // no local-IP - valid record
												"10/13/2014,2:20:42,uswh90d,Default Group,,Start,C825FF9E,,Framed,PPP,,,,,10.128.0.214,217886720,10.148.1.72" // no source-IP - dropping record
										),
										// response
										$("2014-10-13 02:20:42,1413166842,uswh90d,82.166.88.97,10.128.0.214,SUCCESS,,,,,,,,,,,,,,,",
												"2014-10-13 02:20:43,1413166843,uswh90d,82.166.88.97,,SUCCESS,,,,,,,,,,,,,,,",
												(String) null
										)
								},

								{
										"session start - with WAN enrichment",
										// original records
										$("10/13/2014,2:20:42,uswh90d,Default Group,82.166.88.97,Start,C825FF9E,,Framed,PPP,,,,,10.128.0.214,217886720,10.148.1.72 Flume enrichment timezone UTC", // valid record
												"10/13/2014,2:20:43,uswh90d,Default Group,82.166.88.97,Start,C825FF9E,,Framed,PPP,,,,,,217886720,10.148.1.72 Flume enrichment timezone UTC", // no local-IP - valid record
												"10/13/2014,2:20:42,uswh90d,Default Group,,Start,C825FF9E,,Framed,PPP,,,,,10.128.0.214,217886720,10.148.1.72 Flume enrichment timezone UTC" // no source-IP - dropping record
										),
										// response
										$("2014-10-13 02:20:42,1413166842,uswh90d,82.166.88.97,10.128.0.214,SUCCESS,,,,,,,,,,,,,,,",
												"2014-10-13 02:20:43,1413166843,uswh90d,82.166.88.97,,SUCCESS,,,,,,,,,,,,,,,",
												(String) null
										)
								},

								{
										"session end",
										// original records
										$("10/13/2014,8:09:45,uswh90d,Default Group,82.166.88.97,Stop,C825FF9E,20943,Framed,PPP,9199349,19097369,62424,61192,10.128.59.201,217886720,10.148.1.72", // valid record
												"10/13/2014,8:09:45,uswh90d,Default Group,82.166.88.97,Stop,C825FF9E,20943,Framed,PPP,9199349,19097369,62424,61192,,217886720,10.148.1.72", // no local-IP - valid record
												"10/13/2014,8:09:45,uswh90d,Default Group,,Stop,C825FF9E,20943,Framed,PPP,9199349,19097369,62424,61192,10.128.59.201,217886720,10.148.1.72" // no source-IP - dropping record
										),
										// response
										$("2014-10-13 08:09:45,1413187785,uswh90d,82.166.88.97,10.128.59.201,CLOSED,,,,,,,,28296718,9199349,19097369,20943,,,,",
												"2014-10-13 08:09:45,1413187785,uswh90d,82.166.88.97,,CLOSED,,,,,,,,28296718,9199349,19097369,20943,,,,",
												(String) null
										)

								}

						}
		);
	}

}
