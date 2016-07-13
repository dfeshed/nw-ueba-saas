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

/**
 * Created by idanp on 8/25/2014.
 */

@RunWith(JUnitParamsRunner.class)
public class VpnCiscoASATest {

    private MorphlinesTester morphlineTester = new MorphlinesTester();
    private String confFile = "resources/conf-files/vpn/readVPN_ASA_Cisco.conf";
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
        morphlineTester.init(new String[] {confFile, confEnrichmentFile}, vpnOutputFields);
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
						"apple user name parsing test",
						$(
								"Sep 03 2014 05:12:22 rtp5-vpn-cluster-2 : %ASA-7-722051: Group <apple_short> User <tomerl-test-CA57DA549C121B25A5A5038A58C81E6B7B1C954F-iPhone> IP <174.46.152.7> IPv4 Address <10.82.239.225> IPv6 address <::> assigned to session",
								"Oct 01 2014 00:05:44 fff-vpn-cluster-2 : %ASA-4-113019: Group = apple_short, Username = idan-test-1DBFA4192D8E90FD6C9B7620562B3AD1978BBFFE-iPhone, IP = 44.188.239.218, Session disconnected. Session Type: SSL, Duration: 1d 19h:41m:14s, Bytes xmt: 187346964, Bytes rcv: 28559584, Reason: User Requested\"",
								"Oct 01 2014 00:05:44 fff-vpn-cluster-2 : %ASA-4-113019: Group = apple_short, Username = -1DBFA4192D8E90FD6C9B7620562B3AD1978BBFFE-iPhone, IP = 44.188.239.218, Session disconnected. Session Type: SSL, Duration: 1d 19h:41m:14s, Bytes xmt: 187346964, Bytes rcv: 28559584, Reason: User Requested\""

						),
						$(
								"2014-09-03 05:12:22,1409721142,tomerl-test,174.46.152.7,10.82.239.225,SUCCESS,,,,,,,,,,,,,,,",
								"2014-10-01 00:05:44,1412121944,idan-test,44.188.239.218,,CLOSED,,,,,,,,215906548,187346964,28559584,157274,,,,",
								(String) null
						)
				),
				$(
						"fail no group sign with iPhone user",
						$(
								"Feb 01 2016 00:00:32 sjc12-vpn-cluster-4 : %ASA-6-113005: AAA user authorization Rejected : reason = Unspecified : server = 171.71.198.38 : user = migood-AFFD995FA93AB585402348B64FD5BBC1A464A5E8-iPhone : user IP = None"
						),

						$(
								"2016-02-01 00:00:32,1454284832,migood,,,FAIL,,,,,,,,,,,,,,,"
						)
				),
				$(
						"fail no group sign with cisco\\user",
						$(
								"Feb 01 2016 00:00:32 sjc12-vpn-cluster-4 : %ASA-6-113005: AAA user authorization Rejected : reason = Unspecified : server = 171.71.198.38 : user = cisco\\tomerl : user IP = None"
						),
						$(
								"2016-02-01 00:00:32,1454284832,tomerl,,,FAIL,,,,,,,,,,,,,,,"
						)
				),
				$(
						"fail no group sign with @cisco.com",
						$(
								"Feb 01 2016 00:00:32 sjc12-vpn-cluster-4 : %ASA-6-113005: AAA user authorization Rejected : reason = Unspecified : server = 171.71.198.38 : user = tomerl@cisco.com : user IP = None"
						),
						$(
								"2016-02-01 00:00:32,1454284832,tomerl,,,FAIL,,,,,,,,,,,,,,,"
						)
				),
				$(
						"apple_short - fail",
						$(
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-3 : %ASA-4-722037: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC closing connection: DPD failure."
						),
						$(
								(String) null
						)
				),
				$(
						"apple_short - start",
						$("Sep 03 2014 05:12:22 rtp5-vpn-cluster-2 : %ASA-7-722051: Group <apple_short> User <tomerl-CA57DA549C121B25A5A5038A58C81E6B7B1C954F-iPhone> IP <174.46.152.7> IPv4 Address <10.82.239.225> IPv6 address <::> assigned to session",
								"Ma 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session",
								"Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session",
								"Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session",
								"Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <> IPv6 address <::> assigned to session"
						),
						$("2014-09-03 05:12:22,1409721142,tomerl,174.46.152.7,10.82.239.225,SUCCESS,,,,,,,,,,,,,,,",
								(String) null,
								(String) null,
								(String) null,
								(String) null
						)
				),
				$(
						"apple_short - fakse start - resume",
						$(
								"Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-6-716059: Group <apple_short> User <kebarrowFAKE-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <108.202.178.181> apple_short session resumed connection from IP <10.21.77.114>>.",
								"Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrowFAKE-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <108.202.178.181> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session"
						),
						$(
								(String) null,
								(String) null
						)
				),
				$(
						"apple_short - fake start - replace",
						$(
								"Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-5-722032: Group <apple_short> User <kebarrowFAKE-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <108.202.178.181> New UDP SVC connection replacing old connection.",
								"Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrowFAKE-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <108.202.178.181> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session"
						),
						$(
								(String) null,
								(String) null
						)
				),
				$(
						"apple_short - end",
						$(
								"Oct 01 2014 00:05:44 fff-vpn-cluster-2 : %ASA-4-113019: Group = apple_short, Username = idantest-1DBFA4192D8E90FD6C9B7620562B3AD1978BBFFE-iPhone, IP = 44.188.239.218, Session disconnected. Session Type: SSL, Duration: 1d 19h:41m:14s, Bytes xmt: 187346964, Bytes rcv: 28559584, Reason: User Requested",
								"Oct 01 2014 00:05:44 fff-vpn-cluster-2 : %ASA-4-113019: Group = apple_short, Username = idantest-1DBFA4192D8E90FD6C9B7620562B3AD1978BBFFE-iPhone, IP , Session disconnected. Session Type: SSL, Duration: 1d 19h:41m:14s, Bytes xmt: 187346964, Bytes rcv: 28559584, Reason: User Requested"
						),
						$(
								"2014-10-01 00:05:44,1412121944,idantest,44.188.239.218,,CLOSED,,,,,,,,215906548,187346964,28559584,157274,,,,",
								(String) null
						)
				),
				$(
						"apple_long - end",
						$(
								"Jul 01 2014 06:49:49 ddd-vpn-cluster-2 : %ASA-4-113019: Group = apple_long, Username = idantest-B6EFFBF04B0D9D2B5B67A0CF5AAB4FBAF6CBC1E0-iPhone, IP = 188.76.199.235, Session disconnected. Session Type: SSL, Duration: 10d 1h:09m:53s, Bytes xmt: 69672950, Bytes rcv: 8570067, Reason: Idle Timeout",
								"Jul 01 2014 06:49:49 ddd-vpn-cluster-2 : %ASA-4-113019: Group = apple_long, Username = idantest-B6EFFBF04B0D9D2B5B67A0CF5AAB4FBAF6CBC1E0-iPhone, IP , Session disconnected. Session Type: SSL, Duration: 1d 1h:09m:53s, Bytes xmt: 69672950, Bytes rcv: 8570067, Reason: Idle Timeout"
						),
						$(
								"2014-07-01 06:49:49,1404197389,idantest,188.76.199.235,,CLOSED,,,,,,,,78243017,69672950,8570067,868193,,,,",
								(String) null
						)
				),
				$(
						"Everyone - Fail",
						$(
								"Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-6-713905: Group = Everyone, Username = bpotugan, IP = 102.78.186.30, Login authentication failed due to max simultaneous-login restriction."
						),
						$(
								"2014-03-21 23:03:49,1395443029,bpotugan,102.78.186.30,,FAIL,,,,,,,,,,,,,,,"
						)
				),
				$(
						"Everyone - start",
						$(
								"Mar 21 2014 23:03:49 sjc12-gem-ubvpn-gw1a : %ASA-4-713228: Group = Everyone, Username = latom, IP = 122.169.234.49, Assigned private IP address 172.30.223.157 to remote user",
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-713228: Group = Everyone Username  IP = 108.202.178.181, Assigned private IP address 172.30.223.157 to remote user",
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-713228: Group = Everyone Username = latom, IP = , Assigned private IP address 172.30.223.157 to remote user"
						),
						$(
								"2014-03-21 23:03:49,1395443029,latom,122.169.234.49,172.30.223.157,SUCCESS,,,,,,,,,,,,,,,",
								(String) null,
								(String) null
						)
				),
				$(
						"Everyone - start - With WAN enrichment",
						$(
								"Mar 21 2014 23:03:49 sjc12-gem-ubvpn-gw1a : %ASA-4-713228: Group = Everyone, Username = latom, IP = 122.169.234.49, Assigned private IP address 172.30.223.157 to remote user Flume enrichment timezone UTC",
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-713228: Group = Everyone Username  IP = 108.202.178.181, Assigned private IP address 172.30.223.157 to remote user Flume enrichment timezone UTC",
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-713228: Group = Everyone Username = latom, IP = , Assigned private IP address 172.30.223.157 to remote user Flume enrichment timezone UTC"
						),
						$(
								"2014-03-21 23:03:49,1395443029,latom,122.169.234.49,172.30.223.157,SUCCESS,,,,,,,,,,,,,,,",
								(String) null,
								(String) null
						)
				),
				$(
						"AnyConnect_policy - fakse start - resume",
						$(
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-3 : %ASA-6-716059: Group <AnyConnect_profile> User <latomfake> IP <108.202.178.181> AnyConnect session resumed connection from IP <10.21.77.114>>.",
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722051: Group <AnyConnect_policy> User <latomfake> IP <108.202.178.181> IPv4 Address <10.21.77.114> IPv6 address <2001:420:c0c8:1004::315> assigned to session"
						),
						$(
								(String) null,
								(String) null
						)
				),
				$(
						"AnyConnect_policy - fake start - replace",
						$(
								"Mar 21 2014 23:03:49 sjck-vpn-cluster-4 : %ASA-5-722032: Group <AnyConnect_policy> User <chdemontfake> IP <24.7.121.71> New UDP SVC connection replacing old connection.",
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722051: Group <AnyConnect_policy> User <chdemontfake> IP <24.7.121.71> IPv4 Address <10.21.77.114> IPv6 address <2001:420:c0c8:1004::315> assigned to session"

						),
						$(
								(String) null,
								(String) null
						)
				),
				$(
						"event with no datetime or type code",
						$(
								"M 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-7-722031: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC Session Termination: Out: 632880 (+2252) bytes, 32427 (+105) packets, 46 drops.",
								"Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : 1: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC Session Termination: Out: 632880 (+2252) bytes, 32427 (+105) packets, 46 drops."
						),
						$(
								(String) null,
								(String) null
						)
				),
				$(
						"Fail session without group sign",
						$(
								"Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-6-113013: AAA unable to complete the request Error : reason = Simultaneous logins exceeded for user : user = kebarrow",
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-6-113005: AAA user authentication Rejected : reason = AAA failure : server = 173.38.203.42 : user = kebarrow"
						),
						$(
								"2014-03-21 23:03:49,1395443029,kebarrow,,,FAIL,,,,,,,,,,,,,,,",
								"2014-03-21 23:03:49,1395443029,kebarrow,,,FAIL,,,,,,,,,,,,,,,"
						)
				),
				$(
						"AnyConnect_policy - start",
						$(
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722051: Group <AnyConnect_policy> User <latom> IP <108.202.178.181> IPv4 Address <10.21.77.114> IPv6 address <2001:420:c0c8:1004::315> assigned to session",
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722051: Group <AnyConnect_policy> User <> IP <108.202.178.181> IPv4 Address <10.21.77.114> IPv6 address <2001:420:c0c8:1004::315> assigned to session",
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722051: Group <AnyConnect_policy> User <latom> IP <> IPv4 Address <10.21.77.114> IPv6 address <2001:420:c0c8:1004::315> assigned to session"
						),
						$(
								"2014-03-21 23:03:49,1395443029,latom,108.202.178.181,10.21.77.114,SUCCESS,,,,,,,,,,,,,,,",
								(String) null,
								(String) null
						)
				),
				$(
						"AnyConnect_policy - End",
						$(
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP = 108.99.114.225, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt: 123772941, Bytes rcv: 86407232, Reason: User Requested",
								"M 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP = 108.99.114.225, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt: 123772941, Bytes rcv: 86407232, Reason: User Requested",
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP =, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt: 123772941, Bytes rcv: 86407232, Reason: User Requested",
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP = 108.99.114.225, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt: , Bytes rcv: 86407232, Reason: User Requested",
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP = 108.99.114.225, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt:123772941 , Bytes rcv: , Reason: User Requested"
						),
						$(
								"2014-03-21 23:03:49,1395443029,latom,108.99.114.225,,CLOSED,,,,,,,,210180173,123772941,86407232,26591,,,,",
								(String) null,
								(String) null,
								(String) null,
								(String) null
						)
				),
				$(
						"AnyConnect_policy - Fail",
						$(
								"Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722037: Group <AnyConnect_policy> User <bagiri> IP <98.207.110.144> SVC closing connection: DPD failure."
						),
						$(
								(String) null
						)
				),
				$(
						"apple_short - start",
						$("Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session",
								"Ma 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session",
								"Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session",
								"Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session",
								"Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <> IPv6 address <::> assigned to session"
						),
						$("2014-03-21 23:03:49,1395443029,kebarrow,75.138.81.207,10.89.4.165,SUCCESS,,,,,,,,,,,,,,,",
								(String) null,
								(String) null,
								(String) null,
								(String) null
						)
				),
				$(
						"Everyone - end",
						$(
								"Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP = 102.76.169.108, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: 5179731, Bytes rcv: 940724, Reason: User Requested",
								"Ma 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP = 102.76.169.108, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: 5179731, Bytes rcv: 940724, Reason: User Requested",
								"Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP =, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: 5179731, Bytes rcv: 940724, Reason: User Requested",
								"Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP = 102.76.169.108, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: , Bytes rcv: 940724, Reason: User Requested",
								"Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP = 102.76.169.108, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: 5179731, Bytes rcv:, Reason: User Requested"
						),
						$(
								"2014-03-21 23:03:49,1395443029,rkukunur,102.76.169.108,,CLOSED,,,,,,,,6120455,5179731,940724,2078,,,,",
								(String) null,
								(String) null,
								(String) null,
								(String) null
						)
				),
				$(
						"ciscovpn4cluster / ciscovpncluster / crdc_webex_employee / CRDC_users - start",
						$(
								"Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-713228: Group = ciscovpn4cluster, Username = kebarrow, IP = 75.138.81.207, Assigned private IP address 10.82.210.107",
								"ar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-713228: Group = ciscovpn4cluster, Username = kebarrow, IP = 75.138.81.207, Assigned private IP address 10.82.210.107",
								"Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-713228: Group = ciscovpn4cluster, Username = kebarrow, IP =, Assigned private IP address 10.82.210.107",
								"Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-713228: Group = ciscovpn4cluster, Username = kebarrow, IP = 75.138.81.207, Assigned private IP address "
						),
						$(
								"2014-03-21 23:03:49,1395443029,kebarrow,75.138.81.207,10.82.210.107,SUCCESS,,,,,,,,,,,,,,,",
								(String) null,
								(String) null,
								(String) null
						)
				),
				$(
						"ciscovpn4cluster / ciscovpncluster / crdc_webex_employee / CRDC_users - end",
						$(
								"Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP = 102.253.118.222, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt: 14038276, Bytes rcv: 5944581, Reason: Lost Service",
								"Ma 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP = 102.253.118.222, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt: 14038276, Bytes rcv: 5944581, Reason: Lost Service",
								"Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP =, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt:14038276, Bytes rcv: 5944581, Reason: Lost Service",
								"Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP = 102.253.118.222, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt:, Bytes rcv: 5944581, Reason: Lost Service",
								"Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP = 102.253.118.222, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt: 14038276, Bytes rcv: , Reason: Lost Service"
						),
						$(
								"2014-03-21 23:03:49,1395443029,kebarrow,102.253.118.222,,CLOSED,,,,,,,,19982857,14038276,5944581,3175,,,,",
								(String) null,
								(String) null,
								(String) null,
								(String) null
						)
				),
				$(
						"un supported group ",
						$(
								"Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-7-722031: Group <bla bla test> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC Session Termination: Out: 632880 (+2252) bytes, 32427 (+105) packets, 46 drops."
						),
						$(
								(String) null
						)
				),
				$(
						"VALIDATE PARSING OF \" ",
						$(
								"\" Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-6-113005: AAA user authentication Rejected : reason = AAA failure : server = 173.38.203.42 : user = kebarrow : user IP = 101.63.204.196\","
						),
						$(
								"2014-03-21 23:03:49,1395443029,kebarrow,101.63.204.196,,FAIL,,,,,,,,,,,,,,,"
						)
				),
				$(
						"New ASA code ASA-5-751025",
						$("Jul 08 2016 15:00:39 shnidc-vpn-cluster-2 : %ASA-5-751025: Local:72.163.248.230:4500 Remote:123.118.90.197:6107 Username:huitzhan IKEv2 Group:AnyConnect_IPSec_policy IPv4 Address=10.79.103.60 IPv6 address=:: assigned to session"),
						$("2016-07-08 15:00:39,1467990039,huitzhan,123.118.90.197,10.79.103.60,SUCCESS,,,,,,,,,,,,,,,")
				),
				$(
						"New ASA code ASA-5-751025 include ipv6",
						$("Jul 08 2016 15:00:39 shnidc-vpn-cluster-2 : %ASA-5-751025: Local:72.163.248.230:4500 Remote:123.118.90.197:6107 Username:huitzhan IKEv2 Group:AnyConnect_IPSec_policy IPv4 Address=10.79.103.60 IPv6 address=:: assigned to session"),
						$("2016-07-08 15:00:39,1467990039,huitzhan,123.118.90.197,10.79.103.60,SUCCESS,,,,,,,,,,,,,,,")
				)


		);
        }

}
