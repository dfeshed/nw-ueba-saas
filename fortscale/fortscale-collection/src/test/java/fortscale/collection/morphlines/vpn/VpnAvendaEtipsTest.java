package fortscale.collection.morphlines.vpn;

import fortscale.collection.morphlines.MorphlinesTester;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.properties.PropertiesResolver;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class VpnAvendaEtipsTest {

	private MorphlinesTester morphlineTester = new MorphlinesTester();
	private String confFile = "resources/conf-files/vpn/readVPN_avendaEtips.conf";
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

	@Test
	@Parameters
	public void test(String testCase, String inputLine, String expectedOutput) {
		morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
	}
	
	
	
	@SuppressWarnings("unused")
	private Object[] parametersForTest() {
        return	$(
				$ (
				"Regular (AMD) Successful VPN Authentication #1",
				"2014-03-10 09:03:39	Local7.Debug	10.232.60.18	CEF:0|Avenda|eTipsDB|1.0||Start|Unknown| eventId=23982752342 end=1394434855000 mrt=1394434862962 customerID=S+cjNIw8BABCLZ6xMwRmzOw\\=\\= customerURI=/All Customers/Amdocs/Amdocs - Israel modelConfidence=4 severity=0 relevance=10 assetCriticality=0 priority=2 art=1394434860986 rt=1394434860986 src=10.120.91.190 sourceZoneID=ML8022AABABCDTFpYAT3UdQ\\=\\= sourceZoneURI=/All Zones/ArcSight System/Private Address Space Zones/RFC1918: 10.0.0.0-10.255.255.255 dst=10.121.2.30 destinationZoneID=Mvq5iHCoBABCbpx2nIuhb8g\\=\\= destinationZoneURI=/All Zones/Site Zones/AmdocsProduction/Champaign/WISM CMI  destinationAssetId=NxYeaHCoBABCqRh2nIuhb8g\\=\\= duser=a88808c7a1d7 destinationGeoCountryCode=US destinationGeoLocationInfo=Champaign  dlong=-88.0 dlat=40.0 destinationGeoPostalCode=61820 destinationGeoRegionCode=217 cs2=Remote locality=1 cs1Label=Service_Type cs2Label=Acct_Authentic cs3Label=Acct_Terminate_Cause ahost=RASIMMGR2SRV agt=10.232.62.23 av=5.1.7.6151.0 atz=Asia/Jerusalem aid=3hmtHiT4BABCAAYE0Soydvg\\=\\= at=superagent_ng dtz=Asia/Jerusalem eventAnnotationStageUpdateTime=1394434863005 eventAnnotationModificationTime=1394434863005 eventAnnotationAuditTrail=1,1393945852714,root,Queued,,,,\r\n eventAnnotationVersion=1 eventAnnotationFlags=0 eventAnnotationEndTime=1394434855000 eventAnnotationManagerReceiptTime=1394434862962 customerName=Amdocs - Israel originalAgentHostName=RASIMAGT4SRV originalAgentAddress=10.232.60.74 originalAgentZoneURI=/All Zones/Site Zones/AmdocsProduction/Raanana/Raanana - NT originalAgentAssetId=4sw8hmRIBABCpf+uHmdlLaA\\=\\= originalAgentVersion=5.1.3.5870.0 originalAgentId=3zGZnkzABABDjvo8uXZWyYQ\\=\\= originalAgentType=flexmulti_db _cefVer=0.1 ad.arcSightEventPath=3zGZnkzABABDjvo8uXZWyYQ\\=\\=",
				"2014-03-10 07:00:55,1394434855000,a88808c7a1d7,10.120.91.190,10.121.2.30,SUCCESS,,,,,,,,,,,,,,,"
				),
				$ (
				"Regular (AMD) Successful VPN Authentication #2", 
				"2014-03-10 09:41:01	Local7.Debug	10.232.60.18	CEF:0|Avenda|eTipsDB|1.0||Start|Unknown| eventId=23986765322 end=1394437065000 mrt=1394437073468 customerID=S+cjNIw8BABCLZ6xMwRmzOw\\=\\= customerURI=/All Customers/Amdocs/Amdocs - Israel modelConfidence=0 severity=0 relevance=10 assetCriticality=0 priority=2 art=1394437072314 rt=1394437072314 shost=valeriel01.corp.amdocs.com src=10.233.136.38 sourceZoneID=ML8022AABABCDTFpYAT3UdQ\\=\\= sourceZoneURI=/All Zones/ArcSight System/Private Address Space Zones/RFC1918: 10.0.0.0-10.255.255.255 dst=10.232.234.15 destinationZoneID=ML8022AABABCDTFpYAT3UdQ\\=\\= destinationZoneURI=/All Zones/ArcSight System/Private Address Space Zones/RFC1918: 10.0.0.0-10.255.255.255 duser=VALMIKK cs2=RADIUS locality=1 cs1Label=Service_Type cs2Label=Acct_Authentic cs3Label=Acct_Terminate_Cause ahost=RASIMMGR2SRV agt=10.232.62.23 av=5.1.7.6151.0 atz=Asia/Jerusalem aid=3hmtHiT4BABCAAYE0Soydvg\\=\\= at=superagent_ng dtz=Asia/Jerusalem eventAnnotationStageUpdateTime=1394437073812 eventAnnotationModificationTime=1394437073812 eventAnnotationAuditTrail=1,1393945852714,root,Queued,,,,\r\n eventAnnotationVersion=1 eventAnnotationFlags=0 eventAnnotationEndTime=1394437065000 eventAnnotationManagerReceiptTime=1394437073468 customerName=Amdocs - Israel originalAgentHostName=RASIMAGT4SRV originalAgentAddress=10.232.60.74 originalAgentZoneURI=/All Zones/Site Zones/AmdocsProduction/Raanana/Raanana - NT originalAgentAssetId=4sw8hmRIBABCpf+uHmdlLaA\\=\\= originalAgentVersion=5.1.3.5870.0 originalAgentId=3zGZnkzABABDjvo8uXZWyYQ\\=\\= originalAgentType=flexmulti_db _cefVer=0.1 ad.arcSightEventPath=3zGZnkzABABDjvo8uXZWyYQ\\=\\=",
				"2014-03-10 07:37:45,1394437065000,VALMIKK,10.233.136.38,10.232.234.15,SUCCESS,,,,,,,,,,,,,,,"
				),
				$ (
				"Regular (AMD) Successful VPN Authentication #3",
				"2014-03-10 09:05:35	Local7.Debug	10.232.60.18	CEF:0|Avenda|eTipsDB|1.0||Start|Unknown| eventId=23983068375 end=1394434990000 mrt=1394434998202 customerID=S+cjNIw8BABCLZ6xMwRmzOw\\=\\= customerURI=/All Customers/Amdocs/Amdocs - Israel modelConfidence=4 severity=0 relevance=10 assetCriticality=0 priority=2 art=1394434996517 rt=1394434996517 src=193.229.18.9 sourceZoneID=Mokee5CcBABCGKZ5Updd27g\\=\\= sourceZoneURI=/All Zones/ArcSight System/Public Address Space Zones/RIPE NCC/193.0.0.0-195.255.255.255 (RIPE NCC) sourceGeoCountryCode=FI slong=26.0 slat=64.0 dst=193.43.246.42 destinationZoneID=MgkUSmRIBABCcMOuHmdlLaA\\=\\= destinationZoneURI=/All Zones/Site Zones/AmdocsProduction/ArcSync/ArcSync_193.43.246.0 destinationAssetId=NAxo0FhgBABCAi2llvNTpUg\\=\\= duser=ALINADA destinationGeoCountryCode=IL destinationGeoLocationInfo=Raanana dlong=35.0 dlat=32.0 destinationGeoRegionCode=09 cs1=Framed-User cs2=RADIUS locality=1 cs1Label=Service_Type cs2Label=Acct_Authentic cs3Label=Acct_Terminate_Cause ahost=RASIMMGR2SRV agt=10.232.62.23 av=5.1.7.6151.0 atz=Asia/Jerusalem aid=3hmtHiT4BABCAAYE0Soydvg\\=\\= at=superagent_ng dtz=Asia/Jerusalem eventAnnotationStageUpdateTime=1394434998219 eventAnnotationModificationTime=1394434998219 eventAnnotationAuditTrail=1,1393945852714,root,Queued,,,,\r\n eventAnnotationVersion=1 eventAnnotationFlags=0 eventAnnotationEndTime=1394434990000 eventAnnotationManagerReceiptTime=1394434998202 customerName=Amdocs - Israel originalAgentHostName=RASIMAGT4SRV originalAgentAddress=10.232.60.74 originalAgentZoneURI=/All Zones/Site Zones/AmdocsProduction/Raanana/Raanana - NT originalAgentAssetId=4sw8hmRIBABCpf+uHmdlLaA\\=\\= originalAgentVersion=5.1.3.5870.0 originalAgentId=3zGZnkzABABDjvo8uXZWyYQ\\=\\= originalAgentType=flexmulti_db _cefVer=0.1 ad.arcSightEventPath=3zGZnkzABABDjvo8uXZWyYQ\\=\\=",
				"2014-03-10 07:03:10,1394434990000,ALINADA,193.229.18.9,193.43.246.42,SUCCESS,,,,,,,,,,,,,,,"
				),
				$ (
				"Regular (AMD) Successful VPN Authentication of Unknown User (Should be dropped)",
				"2014-03-10 09:11:20	Local7.Debug	10.232.60.18	CEF:0|Avenda|eTipsDB|1.0||Start|Unknown| eventId=23983855349 end=1394435382000 mrt=1394435388211 customerID=S+cjNIw8BABCLZ6xMwRmzOw\\=\\= customerURI=/All Customers/Amdocs/Amdocs - Israel modelConfidence=0 severity=0 relevance=10 assetCriticality=0 priority=2 art=1394435386861 rt=1394435386861 duser=unknown locality=1 cs1Label=Service_Type cs2Label=Acct_Authentic cs3Label=Acct_Terminate_Cause ahost=RASIMMGR2SRV agt=10.232.62.23 av=5.1.7.6151.0 atz=Asia/Jerusalem aid=3hmtHiT4BABCAAYE0Soydvg\\=\\= at=superagent_ng dtz=Asia/Jerusalem eventAnnotationStageUpdateTime=1394435388302 eventAnnotationModificationTime=1394435388302 eventAnnotationAuditTrail=1,1393945852714,root,Queued,,,,\r\n eventAnnotationVersion=1 eventAnnotationFlags=0 eventAnnotationEndTime=1394435382000 eventAnnotationManagerReceiptTime=1394435388211 customerName=Amdocs - Israel originalAgentHostName=RASIMAGT4SRV originalAgentAddress=10.232.60.74 originalAgentZoneURI=/All Zones/Site Zones/AmdocsProduction/Raanana/Raanana - NT originalAgentAssetId=4sw8hmRIBABCpf+uHmdlLaA\\=\\= originalAgentVersion=5.1.3.5870.0 originalAgentId=3zGZnkzABABDjvo8uXZWyYQ\\=\\= originalAgentType=flexmulti_db _cefVer=0.1 ad.arcSightEventPath=3zGZnkzABABDjvo8uXZWyYQ\\=\\=",
				null
				)
				,
				$ (
				"Regular (AMD) Successful VPN Authentication of empty string User (Should be dropped)",
				"2014-03-10 09:11:20	Local7.Debug	10.232.60.18	CEF:0|Avenda|eTipsDB|1.0||Start|  | eventId=23983855349 end=1394435382000 mrt=1394435388211 customerID=S+cjNIw8BABCLZ6xMwRmzOw\\=\\= customerURI=/All Customers/Amdocs/Amdocs - Israel modelConfidence=0 severity=0 relevance=10 assetCriticality=0 priority=2 art=1394435386861 rt=1394435386861 duser=    locality=1 cs1Label=Service_Type cs2Label=Acct_Authentic cs3Label=Acct_Terminate_Cause ahost=RASIMMGR2SRV agt=10.232.62.23 av=5.1.7.6151.0 atz=Asia/Jerusalem aid=3hmtHiT4BABCAAYE0Soydvg\\=\\= at=superagent_ng dtz=Asia/Jerusalem eventAnnotationStageUpdateTime=1394435388302 eventAnnotationModificationTime=1394435388302 eventAnnotationAuditTrail=1,1393945852714,root,Queued,,,,\r\n eventAnnotationVersion=1 eventAnnotationFlags=0 eventAnnotationEndTime=1394435382000 eventAnnotationManagerReceiptTime=1394435388211 customerName=Amdocs - Israel originalAgentHostName=RASIMAGT4SRV originalAgentAddress=10.232.60.74 originalAgentZoneURI=/All Zones/Site Zones/AmdocsProduction/Raanana/Raanana - NT originalAgentAssetId=4sw8hmRIBABCpf+uHmdlLaA\\=\\= originalAgentVersion=5.1.3.5870.0 originalAgentId=3zGZnkzABABDjvo8uXZWyYQ\\=\\= originalAgentType=flexmulti_db _cefVer=0.1 ad.arcSightEventPath=3zGZnkzABABDjvo8uXZWyYQ\\=\\=",
				null
				),
				$ (
				"Regular (AMD) Successful VPN Authentication #3 of empty string source ip",
				"2014-03-10 09:05:35	Local7.Debug	10.232.60.18	CEF:0|Avenda|eTipsDB|1.0||Start|Unknown| eventId=23983068375 end=1394434990000 mrt=1394434998202 customerID=S+cjNIw8BABCLZ6xMwRmzOw\\=\\= customerURI=/All Customers/Amdocs/Amdocs - Israel modelConfidence=4 severity=0 relevance=10 assetCriticality=0 priority=2 art=1394434996517 rt=1394434996517 src=   sourceZoneID=Mokee5CcBABCGKZ5Updd27g\\=\\= sourceZoneURI=/All Zones/ArcSight System/Public Address Space Zones/RIPE NCC/193.0.0.0-195.255.255.255 (RIPE NCC) sourceGeoCountryCode=FI slong=26.0 slat=64.0 dst=193.43.246.42 destinationZoneID=MgkUSmRIBABCcMOuHmdlLaA\\=\\= destinationZoneURI=/All Zones/Site Zones/AmdocsProduction/ArcSync/ArcSync_193.43.246.0 destinationAssetId=NAxo0FhgBABCAi2llvNTpUg\\=\\= duser=ALINADA destinationGeoCountryCode=IL destinationGeoLocationInfo=Raanana dlong=35.0 dlat=32.0 destinationGeoRegionCode=09 cs1=Framed-User cs2=RADIUS locality=1 cs1Label=Service_Type cs2Label=Acct_Authentic cs3Label=Acct_Terminate_Cause ahost=RASIMMGR2SRV agt=10.232.62.23 av=5.1.7.6151.0 atz=Asia/Jerusalem aid=3hmtHiT4BABCAAYE0Soydvg\\=\\= at=superagent_ng dtz=Asia/Jerusalem eventAnnotationStageUpdateTime=1394434998219 eventAnnotationModificationTime=1394434998219 eventAnnotationAuditTrail=1,1393945852714,root,Queued,,,,\r\n eventAnnotationVersion=1 eventAnnotationFlags=0 eventAnnotationEndTime=1394434990000 eventAnnotationManagerReceiptTime=1394434998202 customerName=Amdocs - Israel originalAgentHostName=RASIMAGT4SRV originalAgentAddress=10.232.60.74 originalAgentZoneURI=/All Zones/Site Zones/AmdocsProduction/Raanana/Raanana - NT originalAgentAssetId=4sw8hmRIBABCpf+uHmdlLaA\\=\\= originalAgentVersion=5.1.3.5870.0 originalAgentId=3zGZnkzABABDjvo8uXZWyYQ\\=\\= originalAgentType=flexmulti_db _cefVer=0.1 ad.arcSightEventPath=3zGZnkzABABDjvo8uXZWyYQ\\=\\=",
				null
				),
				$ (
				"Regular (AMD) Successful VPN Authentication #3 with no source ip",
				"2014-03-10 09:05:35	Local7.Debug	10.232.60.18	CEF:0|Avenda|eTipsDB|1.0||Start|Unknown| eventId=23983068375 end=1394434990000 mrt=1394434998202 customerID=S+cjNIw8BABCLZ6xMwRmzOw\\=\\= customerURI=/All Customers/Amdocs/Amdocs - Israel modelConfidence=4 severity=0 relevance=10 assetCriticality=0 priority=2 art=1394434996517 rt=1394434996517 src1=193.229.18.9 sourceZoneID=Mokee5CcBABCGKZ5Updd27g\\=\\= sourceZoneURI=/All Zones/ArcSight System/Public Address Space Zones/RIPE NCC/193.0.0.0-195.255.255.255 (RIPE NCC) sourceGeoCountryCode=FI slong=26.0 slat=64.0 dst=193.43.246.42 destinationZoneID=MgkUSmRIBABCcMOuHmdlLaA\\=\\= destinationZoneURI=/All Zones/Site Zones/AmdocsProduction/ArcSync/ArcSync_193.43.246.0 destinationAssetId=NAxo0FhgBABCAi2llvNTpUg\\=\\= duser=ALINADA destinationGeoCountryCode=IL destinationGeoLocationInfo=Raanana dlong=35.0 dlat=32.0 destinationGeoRegionCode=09 cs1=Framed-User cs2=RADIUS locality=1 cs1Label=Service_Type cs2Label=Acct_Authentic cs3Label=Acct_Terminate_Cause ahost=RASIMMGR2SRV agt=10.232.62.23 av=5.1.7.6151.0 atz=Asia/Jerusalem aid=3hmtHiT4BABCAAYE0Soydvg\\=\\= at=superagent_ng dtz=Asia/Jerusalem eventAnnotationStageUpdateTime=1394434998219 eventAnnotationModificationTime=1394434998219 eventAnnotationAuditTrail=1,1393945852714,root,Queued,,,,\r\n eventAnnotationVersion=1 eventAnnotationFlags=0 eventAnnotationEndTime=1394434990000 eventAnnotationManagerReceiptTime=1394434998202 customerName=Amdocs - Israel originalAgentHostName=RASIMAGT4SRV originalAgentAddress=10.232.60.74 originalAgentZoneURI=/All Zones/Site Zones/AmdocsProduction/Raanana/Raanana - NT originalAgentAssetId=4sw8hmRIBABCpf+uHmdlLaA\\=\\= originalAgentVersion=5.1.3.5870.0 originalAgentId=3zGZnkzABABDjvo8uXZWyYQ\\=\\= originalAgentType=flexmulti_db _cefVer=0.1 ad.arcSightEventPath=3zGZnkzABABDjvo8uXZWyYQ\\=\\=",
				null
				)
        		);
    }

}
