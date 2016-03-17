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

import java.util.ArrayList;
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
	public void test(String testCase, Object[] lines, Object[] outputs) {
		List<String> events = new ArrayList<String>(lines.length);
		for (Object line : lines)
			events.add((String)line);

		List<String> expected = new ArrayList<String>(outputs.length);
		for (Object output : outputs)
			expected.add((String)output);

		morphlineTester.testMultipleLines(testCase, events, expected);
	}


	@SuppressWarnings("unused")
	private Object[] parametersForTest() {
		return
				$(
						$(
								"Checkpoint Successful Connection",
								$ (
										"11/30/2015:17:40:17 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context sankesh_kv@71.231.181.17 - SessionId: 7558- User sankesh_kv - Client_ip 71.231.181.17 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:17 GMT\" - End_time \"11/30/2015:17:40:17 GMT\" - Duration 00:00:00  - Total_bytes_send 0 - Total_bytes_recv 1658 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\""
								),
								$ (
										"2015-11-30 17:40:17,1448905217,sankesh_kv,71.231.181.17,10.68.248.8,SUCCESS,,,,,,,,,,,,,,,"
								)
						),
						$(
								"Checkpoint New Connection Format",
								$(
										"02/03/2016:06:30:53 GMT VBLRB6XICONNECT02 0-PPE-4 : SSLVPN ICASTART 8607 0 :  Source 159.245.48.100:26838 - Destination 10.67.251.30:2598 - username:domainname barun_bari:ad.infosys.com - applicationName Lync2013-XNet-B6 - startTime \"02/03/2016:06:30:52 GMT\" - connectionId 35a1@D33A5C295AED9A725200B18D00005696"
								),
								$ (
										"2016-02-03 06:30:53,1454481053,barun_bari,159.245.48.100,10.67.251.30,SUCCESS,,,,,,,,,,,,,,,"
								)
						),
						$(
								"Checkpoint no username",
								$ (
										"11/30/2015:17:40:17 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context sankesh_kv@71.231.181.17 - SessionId: 7558- User  - Client_ip 71.231.181.17 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:17 GMT\" - End_time \"11/30/2015:17:40:17 GMT\" - Duration 00:00:00  - Total_bytes_send 0 - Total_bytes_recv 1658 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\""
								),
								$ (
										(String)null
								)
						),
						$ (
								"Checkpoint Close Session with No Open Session (no local ip)",
								$ (
										"11/30/2015:18:28:58 GMT VBLRB6XICONNECT01 0-PPE-3 : SSLVPN LOGOUT 2492757 0 : Context rupesh_kumar09@202.7.39.57 - SessionId: 7260- User rupesh_kumar09 - Client_ip 202.7.39.57 - Nat_ip \"Mapped Ip\" - Vserver 125.16.230.70:443 - Start_time \"11/30/2015:17:38:57 GMT\" - End_time \"11/30/2015:18:28:58 GMT\" - Duration 00:50:01  - Http_resources_accessed 82 - NonHttp_services_accessed 0 - Total_TCP_connections 168 - Total_UDP_flows 0 - Total_policies_allowed 168 - Total_policies_denied 0 - Total_bytes_send 0 - Total_bytes_recv 739250 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - LogoutMethod \"TimedOut\" - Group(s) \"N/A\""
								),
								$ (
										"2015-11-30 18:28:58,1448908138,rupesh_kumar09,202.7.39.57,,SUCCESS,,,,,,,,,739250,0,00:50:01,,,,"
								)
						),
						$(
								"1 start, 1 end",
								$("11/30/2015:17:40:00 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context sankesh_kv@71.231.181.17 - SessionId: 7558- User sankesh_kv - Client_ip 71.231.181.17 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:40:00 GMT\" - Duration 00:00:00  - Total_bytes_send 0 - Total_bytes_recv 1658 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\"",
										"11/30/2015:18:48:00 GMT VBLRB6XICONNECT01 0-PPE-3 : SSLVPN LOGOUT 2492757 0 : Context sankesh_kv@202.7.39.57 - SessionId: 7558- User sankesh_kv - Client_ip 71.231.181.17 - Nat_ip \"Mapped Ip\" - Vserver 125.16.230.70:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:48:00 GMT\" - Duration 00:08:00  - Http_resources_accessed 82 - NonHttp_services_accessed 0 - Total_TCP_connections 168 - Total_UDP_flows 0 - Total_policies_allowed 168 - Total_policies_denied 0 - Total_bytes_send 505 - Total_bytes_recv 739250 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - LogoutMethod \"TimedOut\" - Group(s) \"N/A\""
								),
								$("2015-11-30 17:40:00,1448905200,sankesh_kv,71.231.181.17,10.68.248.8,SUCCESS,,,,,,,,,,,,,,,",
										"2015-11-30 18:48:00,1448909280,sankesh_kv,71.231.181.17,10.68.248.8,SUCCESS,,,,,,,,,739250,505,00:08:00,,,,"
								)
						),
						$(
								"2 start, 1 end",
								$("11/30/2015:17:40:00 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context sankesh_kv@71.231.181.17 - SessionId: 7558- User sankesh_kv - Client_ip 71.231.181.17 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:40:00 GMT\" - Duration 00:00:00  - Total_bytes_send 0 - Total_bytes_recv 1658 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\"",
										"11/30/2015:17:45:00 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context sankesh_kv@71.231.181.17 - SessionId: 7558- User sankesh_kv - Client_ip 71.231.181.17 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:45:00 GMT\" - Duration 00:05:00  - Total_bytes_send 0 - Total_bytes_recv 1989 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\"",
										"11/30/2015:18:48:00 GMT VBLRB6XICONNECT01 0-PPE-3 : SSLVPN LOGOUT 2492757 0 : Context sankesh_kv@202.7.39.57 - SessionId: 7558- User sankesh_kv - Client_ip 71.231.181.17 - Nat_ip \"Mapped Ip\" - Vserver 125.16.230.70:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:48:00 GMT\" - Duration 00:08:00  - Http_resources_accessed 82 - NonHttp_services_accessed 0 - Total_TCP_connections 168 - Total_UDP_flows 0 - Total_policies_allowed 168 - Total_policies_denied 0 - Total_bytes_send 505 - Total_bytes_recv 739250 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - LogoutMethod \"TimedOut\" - Group(s) \"N/A\""
								),
								$("2015-11-30 17:40:00,1448905200,sankesh_kv,71.231.181.17,10.68.248.8,SUCCESS,,,,,,,,,,,,,,,",
										(String)null,
										"2015-11-30 18:48:00,1448909280,sankesh_kv,71.231.181.17,10.68.248.8,SUCCESS,,,,,,,,,739250,505,00:08:00,,,,"
								)
						),
						$(
								"4 start with 2 different users, same session id, 2 ends (2 starts should be dropped):",
								$("11/30/2015:17:40:00 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context sankesh_kv@71.231.181.17 - SessionId: 7558- User sankesh_kv - Client_ip 71.231.181.17 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:40:00 GMT\" - Duration 00:00:00  - Total_bytes_send 0 - Total_bytes_recv 1658 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\"",
										"11/30/2015:17:45:00 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context sankesh_kv@71.231.181.17 - SessionId: 7558- User sankesh_kv - Client_ip 71.231.181.17 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:45:00 GMT\" - Duration 00:05:00  - Total_bytes_send 0 - Total_bytes_recv 1989 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\"",
										"11/30/2015:17:41:00 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context rupesh_kumar09@71.231.181.17 - SessionId: 7558- User rupesh_kumar09 - Client_ip 202.7.39.57 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:40:00 GMT\" - Duration 00:00:00  - Total_bytes_send 0 - Total_bytes_recv 1658 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\"",
										"11/30/2015:17:46:00 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context rupesh_kumar09@71.231.181.17 - SessionId: 7558- User rupesh_kumar09 - Client_ip 202.7.39.57 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:45:00 GMT\" - Duration 00:05:00  - Total_bytes_send 0 - Total_bytes_recv 1989 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\"",
										"11/30/2015:18:48:00 GMT VBLRB6XICONNECT01 0-PPE-3 : SSLVPN LOGOUT 2492757 0 : Context sankesh_kv@202.7.39.57 - SessionId: 7558- User sankesh_kv - Client_ip 71.231.181.17 - Nat_ip \"Mapped Ip\" - Vserver 125.16.230.70:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:48:00 GMT\" - Duration 00:08:00  - Http_resources_accessed 82 - NonHttp_services_accessed 0 - Total_TCP_connections 168 - Total_UDP_flows 0 - Total_policies_allowed 168 - Total_policies_denied 0 - Total_bytes_send 505 - Total_bytes_recv 739250 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - LogoutMethod \"TimedOut\" - Group(s) \"N/A\"",
										"11/30/2015:18:49:00 GMT VBLRB6XICONNECT01 0-PPE-3 : SSLVPN LOGOUT 2492757 0 : Context rupesh_kumar09@202.7.39.57 - SessionId: 7558- User rupesh_kumar09 - Client_ip 202.7.39.57 - Nat_ip \"Mapped Ip\" - Vserver 125.16.230.70:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:48:00 GMT\" - Duration 00:08:00  - Http_resources_accessed 82 - NonHttp_services_accessed 0 - Total_TCP_connections 168 - Total_UDP_flows 0 - Total_policies_allowed 168 - Total_policies_denied 0 - Total_bytes_send 505 - Total_bytes_recv 739250 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - LogoutMethod \"TimedOut\" - Group(s) \"N/A\""
								),
								$("2015-11-30 17:40:00,1448905200,sankesh_kv,71.231.181.17,10.68.248.8,SUCCESS,,,,,,,,,,,,,,,",
										(String)null,
										"2015-11-30 17:41:00,1448905260,rupesh_kumar09,202.7.39.57,10.68.248.8,SUCCESS,,,,,,,,,,,,,,,",
										(String)null,
										"2015-11-30 18:48:00,1448909280,sankesh_kv,71.231.181.17,10.68.248.8,SUCCESS,,,,,,,,,739250,505,00:08:00,,,,",
										"2015-11-30 18:49:00,1448909340,rupesh_kumar09,202.7.39.57,10.68.248.8,SUCCESS,,,,,,,,,739250,505,00:08:00,,,,"
								)
						),
						$(
								"4 start with 2 different users, 2 different session id per user, 4 ends (none should be dropped):",
								$("11/30/2015:17:40:00 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context sankesh_kv@71.231.181.17 - SessionId: 7558- User sankesh_kv - Client_ip 71.231.181.17 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:40:00 GMT\" - Duration 00:00:00  - Total_bytes_send 0 - Total_bytes_recv 1658 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\"",
										"11/30/2015:17:45:00 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context sankesh_kv@71.231.181.17 - SessionId: 7559- User sankesh_kv - Client_ip 79.231.181.18 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:45:00 GMT\" - Duration 00:05:00  - Total_bytes_send 0 - Total_bytes_recv 1989 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\"",
										"11/30/2015:17:41:00 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context rupesh_kumar09@71.231.181.17 - SessionId: 7568- User rupesh_kumar09 - Client_ip 202.7.39.57 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:40:00 GMT\" - Duration 00:00:00  - Total_bytes_send 0 - Total_bytes_recv 1658 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\"",
										"11/30/2015:17:46:00 GMT VBLRB6XICONNECT01 0-PPE-1 : SSLVPN TCPCONNSTAT 2583604 0 : Context rupesh_kumar09@71.231.181.17 - SessionId: 7569- User rupesh_kumar09 - Client_ip 202.7.39.58 - Nat_ip 10.68.248.8 - Vserver 125.16.230.70:443 - Source 71.231.181.17:50659 - Destination 10.67.252.6:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:45:00 GMT\" - Duration 00:05:00  - Total_bytes_send 0 - Total_bytes_recv 1989 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - Access Allowed - Group(s) \"N/A\"",
										"11/30/2015:18:48:00 GMT VBLRB6XICONNECT01 0-PPE-3 : SSLVPN LOGOUT 2492757 0 : Context sankesh_kv@202.7.39.57 - SessionId: 7558- User sankesh_kv - Client_ip 71.231.181.17 - Nat_ip \"Mapped Ip\" - Vserver 125.16.230.70:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:48:00 GMT\" - Duration 00:08:00  - Http_resources_accessed 82 - NonHttp_services_accessed 0 - Total_TCP_connections 168 - Total_UDP_flows 0 - Total_policies_allowed 168 - Total_policies_denied 0 - Total_bytes_send 505 - Total_bytes_recv 739250 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - LogoutMethod \"TimedOut\" - Group(s) \"N/A\"",
										"11/30/2015:18:49:00 GMT VBLRB6XICONNECT01 0-PPE-3 : SSLVPN LOGOUT 2492757 0 : Context rupesh_kumar09@202.7.39.57 - SessionId: 7568- User rupesh_kumar09 - Client_ip 202.7.39.57 - Nat_ip \"Mapped Ip\" - Vserver 125.16.230.70:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:48:00 GMT\" - Duration 00:08:00  - Http_resources_accessed 82 - NonHttp_services_accessed 0 - Total_TCP_connections 168 - Total_UDP_flows 0 - Total_policies_allowed 168 - Total_policies_denied 0 - Total_bytes_send 505 - Total_bytes_recv 739250 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - LogoutMethod \"TimedOut\" - Group(s) \"N/A\"",
										"11/30/2015:18:48:00 GMT VBLRB6XICONNECT01 0-PPE-3 : SSLVPN LOGOUT 2492757 0 : Context sankesh_kv@202.7.39.57 - SessionId: 7559- User sankesh_kv - Client_ip 71.231.181.17 - Nat_ip \"Mapped Ip\" - Vserver 125.16.230.70:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:48:00 GMT\" - Duration 00:08:00  - Http_resources_accessed 82 - NonHttp_services_accessed 0 - Total_TCP_connections 168 - Total_UDP_flows 0 - Total_policies_allowed 168 - Total_policies_denied 0 - Total_bytes_send 505 - Total_bytes_recv 739250 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - LogoutMethod \"TimedOut\" - Group(s) \"N/A\"",
										"11/30/2015:18:49:00 GMT VBLRB6XICONNECT01 0-PPE-3 : SSLVPN LOGOUT 2492757 0 : Context rupesh_kumar09@202.7.39.57 - SessionId: 7569- User rupesh_kumar09 - Client_ip 202.7.39.57 - Nat_ip \"Mapped Ip\" - Vserver 125.16.230.70:443 - Start_time \"11/30/2015:17:40:00 GMT\" - End_time \"11/30/2015:17:48:00 GMT\" - Duration 00:08:00  - Http_resources_accessed 82 - NonHttp_services_accessed 0 - Total_TCP_connections 168 - Total_UDP_flows 0 - Total_policies_allowed 168 - Total_policies_denied 0 - Total_bytes_send 505 - Total_bytes_recv 739250 - Total_compressedbytes_send 0 - Total_compressedbytes_recv 0 - Compression_ratio_send 0.00% - Compression_ratio_recv 0.00% - LogoutMethod \"TimedOut\" - Group(s) \"N/A\""
								),
								$("2015-11-30 17:40:00,1448905200,sankesh_kv,71.231.181.17,10.68.248.8,SUCCESS,,,,,,,,,,,,,,,",
										"2015-11-30 17:45:00,1448905500,sankesh_kv,79.231.181.18,10.68.248.8,SUCCESS,,,,,,,,,,,,,,,",
										"2015-11-30 17:41:00,1448905260,rupesh_kumar09,202.7.39.57,10.68.248.8,SUCCESS,,,,,,,,,,,,,,,",
										"2015-11-30 17:46:00,1448905560,rupesh_kumar09,202.7.39.58,10.68.248.8,SUCCESS,,,,,,,,,,,,,,,",
										"2015-11-30 18:48:00,1448909280,sankesh_kv,71.231.181.17,10.68.248.8,SUCCESS,,,,,,,,,739250,505,00:08:00,,,,",
										"2015-11-30 18:49:00,1448909340,rupesh_kumar09,202.7.39.57,10.68.248.8,SUCCESS,,,,,,,,,739250,505,00:08:00,,,,",
										"2015-11-30 18:48:00,1448909280,sankesh_kv,71.231.181.17,10.68.248.8,SUCCESS,,,,,,,,,739250,505,00:08:00,,,,",
										"2015-11-30 18:49:00,1448909340,rupesh_kumar09,202.7.39.57,10.68.248.8,SUCCESS,,,,,,,,,739250,505,00:08:00,,,,"
								)
						)
				);
	}


}
