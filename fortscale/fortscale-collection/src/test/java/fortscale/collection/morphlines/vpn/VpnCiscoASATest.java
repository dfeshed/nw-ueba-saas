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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;

/**
 * Created by idanp on 8/25/2014.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/collection-context-test-light.xml"})
public class VpnCiscoASATest {

    private MorphlinesTester morphlineTester = new MorphlinesTester();


    @SuppressWarnings("resource")
    @BeforeClass
    public static void setUpClass() {
    }


    @Before
    public void setUp() throws Exception {
        PropertiesResolver propertiesResolver = new PropertiesResolver("/META-INF/fortscale-config.properties");
        String impalaTableFields = propertiesResolver.getProperty("impala.data.vpn.table.morphline.fields");
        List<String> vpnOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
        String confFile = "resources/conf-files/vpn/readVPN_ASA_Cisco.conf";
        String confEnrichmentFile = "resources/conf-files/enrichment/readVPN_enrich.conf";
        morphlineTester.init(new String[]{confFile, confEnrichmentFile}, vpnOutputFields);
    }

    @After
    public void tearDown() throws Exception {
        morphlineTester.close();
    }


    @Test
    public void test_Apple_username_parsing() {
        String testCase = "apple user name parsing test";
        String inputLine = "Sep 03 2014 05:12:22 rtp5-vpn-cluster-2 : %ASA-7-722051: Group <apple_short> User <tomerl-test-CA57DA549C121B25A5A5038A58C81E6B7B1C954F-iPhone> IP <174.46.152.7> IPv4 Address <10.82.239.225> IPv6 address <::> assigned to session";
        String expectedOutput = "2014-09-03 05:12:22,1409721142,tomerl-test,174.46.152.7,10.82.239.225,SUCCESS,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);


        inputLine = "Oct 01 2014 00:05:44 fff-vpn-cluster-2 : %ASA-4-113019: Group = apple_short, Username = idan-test-1DBFA4192D8E90FD6C9B7620562B3AD1978BBFFE-iPhone, IP = 44.188.239.218, Session disconnected. Session Type: SSL, Duration: 1d 19h:41m:14s, Bytes xmt: 187346964, Bytes rcv: 28559584, Reason: User Requested\"";
        expectedOutput = "2014-10-01 00:05:44,1412121944,idan-test,44.188.239.218,,CLOSED,,,,,,,,215906548,187346964,28559584,157274,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_Apple_username_parsing_fail() {
        String testCase = "apple user name parsing test";
        String inputLine = "Oct 01 2014 00:05:44 fff-vpn-cluster-2 : %ASA-4-113019: Group = apple_short, Username = -1DBFA4192D8E90FD6C9B7620562B3AD1978BBFFE-iPhone, IP = 44.188.239.218, Session disconnected. Session Type: SSL, Duration: 1d 19h:41m:14s, Bytes xmt: 187346964, Bytes rcv: 28559584, Reason: User Requested\"";
        morphlineTester.testSingleLine(testCase, inputLine, null);
    }

    @Test
    public void test_fail_no_group_sign_with_iPhone_user() {
        String testCase = "fail no group sign with iPhone user";
        String inputLine = "Feb 01 2016 00:00:32 sjc12-vpn-cluster-4 : %ASA-6-113005: AAA user authorization Rejected : reason = Unspecified : server = 171.71.198.38 : user = migood-AFFD995FA93AB585402348B64FD5BBC1A464A5E8-iPhone : user IP = None";
        String expectedOutput = "2016-02-01 00:00:32,1454284832,migood,,,FAIL,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_fail_no_group_sign_with_cisco_user() {
        String testCase = "fail no group sign with cisco\\user";
        String inputLine = "Feb 01 2016 00:00:32 sjc12-vpn-cluster-4 : %ASA-6-113005: AAA user authorization Rejected : reason = Unspecified : server = 171.71.198.38 : user = cisco\\tomerl : user IP = None";
        String expectedOutput = "2016-02-01 00:00:32,1454284832,tomerl,,,FAIL,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_fail_no_group_sign_with_cisco_user_at() {
        String testCase = "fail no group sign with @cisco.com";
        String inputLine = "Feb 01 2016 00:00:32 sjc12-vpn-cluster-4 : %ASA-6-113005: AAA user authorization Rejected : reason = Unspecified : server = 171.71.198.38 : user = tomerl@cisco.com : user IP = None";
        String expectedOutput = "2016-02-01 00:00:32,1454284832,tomerl,,,FAIL,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_fail_apple_short() {
        String testCase = "apple_short - fail";
        String inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-3 : %ASA-4-722037: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC closing connection: DPD failure.";
        String expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_apple_short_start() {
        String testCase = "apple_short - start";
        String inputLine = "Sep 03 2014 05:12:22 rtp5-vpn-cluster-2 : %ASA-7-722051: Group <apple_short> User <tomerl-CA57DA549C121B25A5A5038A58C81E6B7B1C954F-iPhone> IP <174.46.152.7> IPv4 Address <10.82.239.225> IPv6 address <::> assigned to session";
        String expectedOutput = "2014-09-03 05:12:22,1409721142,tomerl,174.46.152.7,10.82.239.225,SUCCESS,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Ma 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <> IPv6 address <::> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_apple_short_fake_start_resume() {
        String testCase = "apple_short - fake start - resume";
        String inputLine = "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-6-716059: Group <apple_short> User <kebarrowFAKE-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <108.202.178.181> apple_short session resumed connection from IP <10.21.77.114>>.";
        String expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrowFAKE-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <108.202.178.181> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_apple_short_fake_start_replace() {
        String testCase = "apple_short - fake start - replace";
        String inputLine = "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-5-722032: Group <apple_short> User <kebarrowFAKE-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <108.202.178.181> New UDP SVC connection replacing old connection.";
        String expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrowFAKE-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <108.202.178.181> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_apple_short_end() {
        String testCase = "apple_short - end";
        String inputLine = "Oct 01 2014 00:05:44 fff-vpn-cluster-2 : %ASA-4-113019: Group = apple_short, Username = idantest-1DBFA4192D8E90FD6C9B7620562B3AD1978BBFFE-iPhone, IP = 44.188.239.218, Session disconnected. Session Type: SSL, Duration: 1d 19h:41m:14s, Bytes xmt: 187346964, Bytes rcv: 28559584, Reason: User Requested";
        String expectedOutput = "2014-10-01 00:05:44,1412121944,idantest,44.188.239.218,,CLOSED,,,,,,,,215906548,187346964,28559584,157274,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Oct 01 2014 00:05:44 fff-vpn-cluster-2 : %ASA-4-113019: Group = apple_short, Username = idantest-1DBFA4192D8E90FD6C9B7620562B3AD1978BBFFE-iPhone, IP , Session disconnected. Session Type: SSL, Duration: 1d 19h:41m:14s, Bytes xmt: 187346964, Bytes rcv: 28559584, Reason: User Requested";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_apple_long_end() {
        String testCase = "apple_long - end";
        String inputLine = "Jul 01 2014 06:49:49 ddd-vpn-cluster-2 : %ASA-4-113019: Group = apple_long, Username = idantest-B6EFFBF04B0D9D2B5B67A0CF5AAB4FBAF6CBC1E0-iPhone, IP = 188.76.199.235, Session disconnected. Session Type: SSL, Duration: 10d 1h:09m:53s, Bytes xmt: 69672950, Bytes rcv: 8570067, Reason: Idle Timeout";
        String expectedOutput = "2014-07-01 06:49:49,1404197389,idantest,188.76.199.235,,CLOSED,,,,,,,,78243017,69672950,8570067,868193,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Jul 01 2014 06:49:49 ddd-vpn-cluster-2 : %ASA-4-113019: Group = apple_long, Username = idantest-B6EFFBF04B0D9D2B5B67A0CF5AAB4FBAF6CBC1E0-iPhone, IP , Session disconnected. Session Type: SSL, Duration: 1d 1h:09m:53s, Bytes xmt: 69672950, Bytes rcv: 8570067, Reason: Idle Timeout";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_fail_everyone() {
        String testCase = "Everyone - Fail";
        String inputLine = "Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-6-713905: Group = Everyone, Username = bpotugan, IP = 102.78.186.30, Login authentication failed due to max simultaneous-login restriction.";
        String expectedOutput = "2014-03-21 23:03:49,1395443029,bpotugan,102.78.186.30,,FAIL,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_start_everyone() {
        String testCase = "Everyone - start";
        String inputLine = "Mar 21 2014 23:03:49 sjc12-gem-ubvpn-gw1a : %ASA-4-713228: Group = Everyone, Username = latom, IP = 122.169.234.49, Assigned private IP address 172.30.223.157 to remote user";
        String expectedOutput = "2014-03-21 23:03:49,1395443029,latom,122.169.234.49,172.30.223.157,SUCCESS,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-713228: Group = Everyone Username  IP = 108.202.178.181, Assigned private IP address 172.30.223.157 to remote user";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-713228: Group = Everyone Username = latom, IP = , Assigned private IP address 172.30.223.157 to remote user";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_start_everyone_wan() {
        String testCase = "Everyone - start";
        String inputLine = "Mar 21 2014 23:03:49 sjc12-gem-ubvpn-gw1a : %ASA-4-713228: Group = Everyone, Username = latom, IP = 122.169.234.49, Assigned private IP address 172.30.223.157 to remote user Flume enrichment timezone UTC";
        String expectedOutput = "2014-03-21 23:03:49,1395443029,latom,122.169.234.49,172.30.223.157,SUCCESS,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-713228: Group = Everyone Username  IP = 108.202.178.181, Assigned private IP address 172.30.223.157 to remote user Flume enrichment timezone UTC";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-713228: Group = Everyone Username = latom, IP = , Assigned private IP address 172.30.223.157 to remote user Flume enrichment timezone UTC";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_anyConnect_policy_fake_start_resume() {
        String testCase = "AnyConnect_policy - fake start - resume";
        String inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-3 : %ASA-6-716059: Group <AnyConnect_profile> User <latomfake> IP <108.202.178.181> AnyConnect session resumed connection from IP <10.21.77.114>>.";
        String expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722051: Group <AnyConnect_policy> User <latomfake> IP <108.202.178.181> IPv4 Address <10.21.77.114> IPv6 address <2001:420:c0c8:1004::315> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_anyConnect_policy_fake_start_replace() {
        String testCase = "AnyConnect_policy - fake start - replace";
        String inputLine = "Mar 21 2014 23:03:49 sjck-vpn-cluster-4 : %ASA-5-722032: Group <AnyConnect_policy> User <chdemontfake> IP <24.7.121.71> New UDP SVC connection replacing old connection.";
        String expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722051: Group <AnyConnect_policy> User <chdemontfake> IP <24.7.121.71> IPv4 Address <10.21.77.114> IPv6 address <2001:420:c0c8:1004::315> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_event_with_no_datetime_or_type_code() {
        String testCase = "event with no datetime or type code";
        String inputLine = "M 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-7-722031: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC Session Termination: Out: 632880 (+2252) bytes, 32427 (+105) packets, 46 drops.";
        String expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : 1: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC Session Termination: Out: 632880 (+2252) bytes, 32427 (+105) packets, 46 drops.";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_fail_session_without_group_sign() {
        String testCase = "Fail session without group sign";
        String inputLine = "Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-6-113013: AAA unable to complete the request Error : reason = Simultaneous logins exceeded for user : user = kebarrow";
        String expectedOutput = "2014-03-21 23:03:49,1395443029,kebarrow,,,FAIL,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-6-113005: AAA user authentication Rejected : reason = AAA failure : server = 173.38.203.42 : user = kebarrow";
        expectedOutput = "2014-03-21 23:03:49,1395443029,kebarrow,,,FAIL,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_anyConnect_policy_start() {
        String testCase = "AnyConnect_policy - start";
        String inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722051: Group <AnyConnect_policy> User <latom> IP <108.202.178.181> IPv4 Address <10.21.77.114> IPv6 address <2001:420:c0c8:1004::315> assigned to session";
        String expectedOutput = "2014-03-21 23:03:49,1395443029,latom,108.202.178.181,10.21.77.114,SUCCESS,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722051: Group <AnyConnect_policy> User <> IP <108.202.178.181> IPv4 Address <10.21.77.114> IPv6 address <2001:420:c0c8:1004::315> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722051: Group <AnyConnect_policy> User <latom> IP <> IPv4 Address <10.21.77.114> IPv6 address <2001:420:c0c8:1004::315> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_anyConnect_policy_end() {
        String testCase = "AnyConnect_policy - End";
        String inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP = 108.99.114.225, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt: 123772941, Bytes rcv: 86407232, Reason: User Requested";
        String expectedOutput = "2014-03-21 23:03:49,1395443029,latom,108.99.114.225,,CLOSED,,,,,,,,210180173,123772941,86407232,26591,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "M 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP = 108.99.114.225, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt: 123772941, Bytes rcv: 86407232, Reason: User Requested";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP =, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt: 123772941, Bytes rcv: 86407232, Reason: User Requested";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP = 108.99.114.225, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt: , Bytes rcv: 86407232, Reason: User Requested";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP = 108.99.114.225, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt:123772941 , Bytes rcv: , Reason: User Requested";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_anyConnect_policy_fail() {
        String testCase = "AnyConnect_policy - Fail";

        String inputLine = "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722037: Group <AnyConnect_policy> User <bagiri> IP <98.207.110.144> SVC closing connection: DPD failure.";
        String expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_apple_short_start2() {
        String testCase = "apple_short - start";
        String inputLine = "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session";
        String expectedOutput = "2014-03-21 23:03:49,1395443029,kebarrow,75.138.81.207,10.89.4.165,SUCCESS,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Ma 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <> IPv6 address <::> assigned to session";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_everyone_end() {
        String testCase = "Everyone - end";
        String inputLine = "Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP = 102.76.169.108, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: 5179731, Bytes rcv: 940724, Reason: User Requested";
        String expectedOutput = "2014-03-21 23:03:49,1395443029,rkukunur,102.76.169.108,,CLOSED,,,,,,,,6120455,5179731,940724,2078,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Ma 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP = 102.76.169.108, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: 5179731, Bytes rcv: 940724, Reason: User Requested";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP =, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: 5179731, Bytes rcv: 940724, Reason: User Requested";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP = 102.76.169.108, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: , Bytes rcv: 940724, Reason: User Requested";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP = 102.76.169.108, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: 5179731, Bytes rcv:, Reason: User Requested";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_ciscovpn4cluster_ciscovpncluster_crdc_webex_employee_CRDC_users_start() {
        String testCase = "ciscovpn4cluster / ciscovpncluster / crdc_webex_employee / CRDC_users - start";
        String inputLine = "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-713228: Group = ciscovpn4cluster, Username = kebarrow, IP = 75.138.81.207, Assigned private IP address 10.82.210.107";
        String expectedOutput = "2014-03-21 23:03:49,1395443029,kebarrow,75.138.81.207,10.82.210.107,SUCCESS,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "ar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-713228: Group = ciscovpn4cluster, Username = kebarrow, IP = 75.138.81.207, Assigned private IP address 10.82.210.107";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-713228: Group = ciscovpn4cluster, Username = kebarrow, IP =, Assigned private IP address 10.82.210.107";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-713228: Group = ciscovpn4cluster, Username = kebarrow, IP = 75.138.81.207, Assigned private IP address";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_ciscovpn4cluster_ciscovpncluster_crdc_webex_employee_CRDC_users_end() {
        String testCase = "ciscovpn4cluster / ciscovpncluster / crdc_webex_employee / CRDC_users - end";
        String inputLine = "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP = 102.253.118.222, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt: 14038276, Bytes rcv: 5944581, Reason: Lost Service";
        String expectedOutput = "2014-03-21 23:03:49,1395443029,kebarrow,102.253.118.222,,CLOSED,,,,,,,,19982857,14038276,5944581,3175,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Ma 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP = 102.253.118.222, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt: 14038276, Bytes rcv: 5944581, Reason: Lost Service";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP =, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt:14038276, Bytes rcv: 5944581, Reason: Lost Service";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP = 102.253.118.222, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt:, Bytes rcv: 5944581, Reason: Lost Service";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);

        inputLine = "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP = 102.253.118.222, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt: 14038276, Bytes rcv: , Reason: Lost Service";
        expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_unsupported_group() {
        String testCase = "unsupported group";
        String inputLine = "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-7-722031: Group <bla bla test> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC Session Termination: Out: 632880 (+2252) bytes, 32427 (+105) packets, 46 drops.";
        String expectedOutput = null;
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_special_char() {
        String testCase = "VALIDATE PARSING OF \\";
        String inputLine = "\\\" Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-6-113005: AAA user authentication Rejected : reason = AAA failure : server = 173.38.203.42 : user = kebarrow : user IP = 101.63.204.196\\\",";
        String expectedOutput = "2014-03-21 23:03:49,1395443029,kebarrow,101.63.204.196,,FAIL,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_apple_ios_group_open() {
        String testCase = "Open - AppleiOS group";
        String inputLine = "Nov 14 2016 14:27:19 rtp1-vpn-cluster-4 : %ASA-4-722051: Group <AppleiOS> User <kredmon_9EAB192CAA2AA02479AFDC7FEB7C36FB6FBEE184-iOS> IP <10.116.43.136> IPv4 Address <10.82.173.40> IPv6 address <::> assigned to session";
        String expectedOutput = "2016-11-14 14:27:19,1479133639,kredmon,10.116.43.136,10.82.173.40,SUCCESS,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_apple_ios_group_close() {
        String testCase = "Close - AppleiOS group";
        String inputLine = "Nov 14 2016 15:58:14 rcdn9-sdfb-vpn-cluster-4 : %ASA-4-113019: Group = AppleiOS, Username = chbechar_A6A73B29BC2CB1F95B3043F25AF4F10705AB76AA-iOS, IP = 78.95.61.46, Session disconnected. Session Type: SSL, Duration: 0h:06m:18s, Bytes xmt: 1088320, Bytes rcv: 976100, Reason: User Requested";
        String expectedOutput = "2016-11-14 15:58:14,1479139094,chbechar,78.95.61.46,,CLOSED,,,,,,,,2064420,1088320,976100,378,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_dx_group_open() {
        String testCase = "Open - DX group";
        String inputLine = "Nov 14 2016 14:20:11 asa-sjcace-011 : %ASA-4-722051: Group <DX> User <gscruggs> IP <107.3.193.12> IPv4 Address <10.42.154.132> IPv6 address <::> assigned to session";
        String expectedOutput = "2016-11-14 14:20:11,1479133211,gscruggs,107.3.193.12,10.42.154.132,SUCCESS,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_dx_group_close() {
        String testCase = "Close - dx group";
        String inputLine = "Nov 14 2016 15:12:34 asa-sjcace-011 : %ASA-4-113019: Group = DX, Username = iachick, IP = 76.219.188.178, Session disconnected. Session Type: SSL, Duration: 3d 0h:55m:21s, Bytes xmt: 143739139, Bytes rcv: 175048705, Reason: Idle Timeout";
        String expectedOutput = "2016-11-14 15:12:34,1479136354,iachick,76.219.188.178,,CLOSED,,,,,,,,318787844,143739139,175048705,262521,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_android_group_open() {
        String testCase = "Open - android group";
        String inputLine = "Nov 14 2016 15:32:21 sjc05-vpn-cluster-1 : %ASA-4-722051: Group <Android> User <jlugosal-e91db8c2-ab2e-314e-b272-026ca4ef92a4-android> IP <190.58.250.138> IPv4 Address <10.21.112.81> IPv6 address <::> assigned to session";
        String expectedOutput = "2016-11-14 15:32:21,1479137541,jlugosal-e91db8c2-ab2e-314e-b272,190.58.250.138,10.21.112.81,SUCCESS,,,,,,,,,,,,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_android_group_close() {
        String testCase = "Close - android group";
        String inputLine = "Nov 14 2016 15:52:28 sjc12-vpn-cluster-2 : %ASA-4-113019: Group = Android, Username = achavezs-b5d7666b-c103-3b20-a67b-b84de18ed81e-android, IP = 187.151.32.93, Session disconnected. Session Type: SSL, Duration: 11h:48m:00s, Bytes xmt: 1017122512, Bytes rcv: 70758439, Reason: Idle Timeout";
        String expectedOutput = "2016-11-14 15:52:28,1479138748,achavezs-b5d7666b-c103-3b20-a67b,187.151.32.93,,CLOSED,,,,,,,,1087880951,1017122512,70758439,42480,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }

    @Test
    public void test_bld23_group_close() {
        String testCase = "Close - BLD23 group";
        String inputLine = "Oct 31 2016 22:42:17 asa-sjcace-011 : %ASA-4-113019: Group = BLD23, Username = ctjebben, IP = 173.17.22.207, Session disconnected. Session Type: SSL, Duration: 2d 11h:09m:24s, Bytes xmt: 16871523, Bytes rcv: 17131045, Reason: Idle Timeout";
        String expectedOutput = "2016-10-31 22:42:17,1477953737,ctjebben,173.17.22.207,,CLOSED,,,,,,,,34002568,16871523,17131045,212964,,,,";
        morphlineTester.testSingleLine(testCase, inputLine, expectedOutput);
    }
}
