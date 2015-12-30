package fortscale.collection.morphlines.vpn;

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

import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class VpnCheckpointTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/vpn/readVPN_Checkpoint.conf";
	private String confEnrichmentFile = "resources/conf-files/enrichment/readVPN_enrich.conf";
	
	
	
	@SuppressWarnings("resource")
	@BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }
	
	@Before
	public void setUp() throws Exception {
		PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
		String impalaTableFields = propertiesResolver.getProperty("impala.data.vpn.table.morphline.fields");
		List<String> vpnOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
		morphlineTester.init(new String[] { confFile, confEnrichmentFile }, vpnOutputFields);
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
        return
        $(
			$ (
        	"Checkpoint Successful Connection",
			"Nov 30 2015 21:40:33: %CHKPNT-6-031085: decrypt,10.156.22.181,inbound,Lan3,10.110.74.43,60607,224.0.0.252,5355,5355,udp,29, , , , , , , , , , , , ,ESP: 3DES + SHA1,10.110.74.43,IKE, , , , , , , , ,30Nov2015 21:40:33,0,VPN-1 & FireWall-1, , , , ,priya_murugesan, , ,udp-high-ports, , , , , , , , , , , , , , , , , , , , , , , ,031085, , , , , , , , , , , , ,RemoteAccess, , ,{686BDE51-7603-4B98-B4B1-599776346986},Access to Intranet, , , ,",
			"2015-11-30 21:40:33,1448919633,priya_murugesan,10.110.74.43,224.0.0.252,SUCCESS,,,,,,,,,,,,,,"
			),
			$ (
			"Checkpoint Close Session No User",
			"Nov 19 2015 14:17:05: %CHKPNT-6-999999: time=19Nov2015 14:17:05,action=allow,orig=192.168.179.90,i/f_dir=outbound,i/f_name=eth0,has_accounting=0,product=URL Filtering,__policy_id_tag=product,src=122.98.111.8,s_port=61044,dst=148.171.146.25,service=TCP.8080,d_port=8080,proto=tcp,appi_name=*** Confidential ***,app_desc=*** Confidential ***,app_id=1825177755,app_category=*** Confidential ***,matched_category=*** Confidential ***,app_properties=*** Confidential ***,app_risk=*** Confidential ***,app_rule_id=*** Confidential ***,app_rule_name=*** Confidential ***,proxy_src_ip=122.98.111.8,resource=*** Confidential ***,bytes=93161,sent_bytes=59143,received_bytes=32764,Suppressed logs=79,Referrer_self_uid=*** Confidential ***",
			null
			),
			$ (
			"Checkpoint Failed Connection No User",
			"Nov 22 2015 07:09:31: %CHKPNT-6-031070: reject,122.98.54.80,inbound,daemon,63.91.129.97, ,122.98.54.80, , , , , , , , , , , , , , ,*** Confidential ***, , ,63.91.129.97,IKE,Quick Mode Failed to match proposal: Transform: 3DES  SHA1  Tunnel; Reason: Wrong value for: Authentication Algorithm, , , , , , , ,22Nov2015  7:09:31,0, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , ,031070, , , , , , , , , , , , ,Pune.Netscreen.VPN.community, , , , , , , ,\n",
			null
			)
        );
    }	
	

}
