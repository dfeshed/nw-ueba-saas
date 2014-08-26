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

/**
 * Created by idanp on 8/25/2014.
 */

@RunWith(JUnitParamsRunner.class)
//@ContextConfiguration(locations = {"classpath*:META-INF/spring/collection-context.xml"})
public class VpnCiscoASATest {

    private static ClassPathXmlApplicationContext testContextManager;

    private MorphlinesTester morphlineTester = new MorphlinesTester();
    private String confFile = "resources/conf-files/readVPN_ASA_Cisco.conf";

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
        morphlineTester.init(new String[] {confFile}, vpnOutputFields);
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
                $(
                        "apple_short - start",
                        $("Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session",
                          "Ma 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session",
                          "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session",
                          "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <> IPv4 Address <10.89.4.165> IPv6 address <::> assigned to session",
                          "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> IPv4 Address <> IPv6 address <::> assigned to session"
                         ),
                        $("2014-03-21 23:03:49,1395435829,kebarrow,75.138.81.207,10.89.4.165,SUCCESS,,,,,,,,,,,,,false,false",
                          (String)null,
                          (String)null,
                          (String)null,
                          (String)null
                         )
                ),
                $(
                        "apple_short - end",
                        $(
                          "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-7-722031: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC Session Termination: Out: 632880 (+2252) bytes, 32427 (+105) packets, 46 drops.",
                          "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-7-722030: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC Session Termination: In: 2649665 (+2807) bytes, 25096 (+176) packets, 0 drops.",
                          "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-7-722030: Group <apple_short> User <idantest-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC Session Termination: In: 2649665 (+2807) bytes, 25096 (+176) packets, 0 drops.",
                          "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-7-722031: Group <apple_short> User <idantest-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC Session Termination: Out: 632880 (+2252) bytes, 32427 (+105) packets, 46 drops."
                          ),
                        $(
                          (String)null,
                          "2014-03-21 23:03:49,1395435829,kebarrow,75.138.81.207,,CLOSED,,,,,,,,3282545,632880,2649665,,50,false,false",
                          (String)null,
                          "2014-03-21 23:03:49,1395435829,idantest,75.138.81.207,,CLOSED,,,,,,,,3282545,632880,2649665,,50,false,false"
                        )

                ),
                $(
                        "apple_short - fail",
                        $(
                                "Mar 21 2014 23:03:49 rcdn9-sdfb-vpn-cluster-2 : %ASA-4-722051: Group <apple_short> User <kebarrow-1D49D102AEB1D20FBAB69AD8CD8A28F9BCB1FBCC-iPhone> IP <75.138.81.207> SVC closing connection: DPD failure."

                         ),
                        $(

                                "2014-03-21 23:03:49,1395435829,kebarrow,75.138.81.207,,FAIL,,,,,,,,,,,,,false,false"

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

                                "2014-03-21 23:03:49,1395435829,latom,108.202.178.181,10.21.77.114,SUCCESS,,,,,,,,,,,,,false,false",
                                (String)null,
                                (String)null


                        )

                ),
                $(
                        "AnyConnect_policy - End",
                        $(
                                "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP =108.99.114.225, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt: 123772941, Bytes rcv: 86407232, Reason: User Requested",
                                "M 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP =108.99.114.225, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt: 123772941, Bytes rcv: 86407232, Reason: User Requested",
                                "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP =, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt: 123772941, Bytes rcv: 86407232, Reason: User Requested",
                                "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP =108.99.114.225, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt: , Bytes rcv: 86407232, Reason: User Requested",
                                "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-113019: Group = AnyConnect_profile, Username = latom, IP =108.99.114.225, Session disconnected. Session Type: SSL, Duration: 7h:23m:11s, Bytes xmt:123772941 , Bytes rcv: , Reason: User Requested"


                        ),
                        $(

                                "2014-03-21 23:03:49,1395435829,latom,108.99.114.225,,CLOSED,,,,,,,,210180173,123772941,86407232,,200,false,false",
                                (String)null,
                                (String)null,
                                (String)null,
                                (String)null


                        )

                ),
                $(
                        "AnyConnect_policy - Fail",
                        $(
                                "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722037: Group <AnyConnect_policy> User <bagiri> IP <98.207.110.144> SVC closing connection: DPD failure."
                        ),
                        $(

                                "2014-03-21 23:03:49,1395435829,bagiri,98.207.110.144,,FAIL,,,,,,,,,,,,,false,false"
                        )

                ),
                $(
                        "Everyone - start",
                        $(
                                "Mar 21 2014 23:03:49 sjc12-gem-ubvpn-gw1a : %ASA-4-713228: Group = Everyone, Username = latom, IP =122.169.234.49, Assigned private IP address 172.30.223.157 to remote user",
                                "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722051: Group <AnyConnect_policy> User <> IP <108.202.178.181> IPv4 Address <10.21.77.114> IPv6 address <2001:420:c0c8:1004::315> assigned to session",
                                "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-4-722051: Group <AnyConnect_policy> User <latom> IP <> IPv4 Address <10.21.77.114> IPv6 address <2001:420:c0c8:1004::315> assigned to session"



                        ),
                        $(

                                "2014-03-21 23:03:49,1395435829,latom,122.169.234.49,172.30.223.157,SUCCESS,,,,,,,,,,,,,false,false",
                                (String)null,
                                (String)null


                        )

                ),
                $(
                        "Everyone - end",
                        $(
                                "Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP =102.76.169.108, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: 5179731, Bytes rcv:940724, Reason: User Requested",
                                "Ma 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP =102.76.169.108, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: 5179731, Bytes rcv:940724, Reason: User Requested",
                                "Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP =, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: 5179731, Bytes rcv:940724, Reason: User Requested",
                                "Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP =102.76.169.108, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: , Bytes rcv:940724, Reason: User Requested",
                                "Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-4-113019: Group = Everyone, Username = rkukunur, IP =102.76.169.108, Session disconnected. Session Type: IKEv1, Duration: 0h:34m:38s, Bytes xmt: 5179731, Bytes rcv:, Reason: User Requested"



                        ),
                        $(

                                "2014-03-21 23:03:49,1395435829,rkukunur,102.76.169.108,,CLOSED,,,,,,,,6120455,5179731,940724,,50,false,false",
                                (String)null,
                                (String)null,
                                (String)null,
                                (String)null


                        )

                ),
                $(
                        "Everyone - Fail",
                        $(
                                "Mar 21 2014 23:03:49 bgl11-gem-ubvpn-gw1a : %ASA-6-713905: Group = Everyone, Username = bpotugan, IP = 102.78.186.30, Login authentication failed due to max simultaneous-login restriction."



                        ),
                        $(

                                "2014-03-21 23:03:49,1395435829,bpotugan,102.78.186.30,,FAIL,,,,,,,,,,,,,false,false"

                        )

                ),
                $(
                        "ciscovpn4cluster / ciscovpncluster / crdc_webex_employee / CRDC_users - start",
                        $(
                                "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-713228: Group = ciscovpn4cluster, Username = kebarrow, IP =75.138.81.207, Assigned private IP address 10.82.210.107",
                                "ar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-713228: Group = ciscovpn4cluster, Username = kebarrow, IP =75.138.81.207, Assigned private IP address 10.82.210.107",
                                "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-713228: Group = ciscovpn4cluster, Username = kebarrow, IP =, Assigned private IP address 10.82.210.107",
                                "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-713228: Group = ciscovpn4cluster, Username = kebarrow, IP =75.138.81.207, Assigned private IP address "



                        ),
                        $(

                                "2014-03-21 23:03:49,1395435829,kebarrow,75.138.81.207,10.82.210.107,SUCCESS,,,,,,,,,,,,,false,false",
                                (String)null,
                                (String)null,
                                (String)null


                        )

                ),
                $(
                        "ciscovpn4cluster / ciscovpncluster / crdc_webex_employee / CRDC_users - end",
                        $(
                                "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP =102.253.118.222, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt:14038276, Bytes rcv: 5944581, Reason: Lost Service",
                                "Ma 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP =102.253.118.222, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt:14038276, Bytes rcv: 5944581, Reason: Lost Service",
                                "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP =, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt:14038276, Bytes rcv: 5944581, Reason: Lost Service",
                                "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP =102.253.118.222, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt:, Bytes rcv: 5944581, Reason: Lost Service",
                                "Mar 21 2014 23:03:49 rtp1-vpn-cluster-3 : %ASA-4-113019: Group = ciscovpn4cluster, Username = kebarrow, IP =102.253.118.222, Session disconnected. Session Type: IPsecOverNatT, Duration: 0h:52m:55s, Bytes xmt:14038276, Bytes rcv: , Reason: Lost Service"



                        ),
                        $(

                                "2014-03-21 23:03:49,1395435829,kebarrow,102.253.118.222,,CLOSED,,,,,,,,19982857,14038276,5944581,,50,false,false",
                                (String)null,
                                (String)null,
                                (String)null,
                                (String)null


                        )

                ),
                $(
                        "ciscovpn4cluster / ciscovpncluster / crdc_webex_employee / CRDC_users - Fail",
                        $(
                                "Mar 21 2014 23:03:49 sjce-vpn-cluster-4 : %ASA-6-113005: AAA user authentication Rejected : reason = AAA failure : server = 173.38.203.42 : user = akucher"



                        ),
                        $(

                                "2014-03-21 23:03:49,1395435829,akucher,,,FAIL,,,,,,,,,,,,,false,false"

                        )

                )








        );
    }

}
