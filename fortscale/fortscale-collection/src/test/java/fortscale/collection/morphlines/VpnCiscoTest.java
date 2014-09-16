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
 * Created by idanp on 7/15/2014.
 */


@RunWith(JUnitParamsRunner.class)
//@ContextConfiguration(locations = {"classpath*:META-INF/spring/collection-context.xml"})
public class VpnCiscoTest {

    private static ClassPathXmlApplicationContext testContextManager;

    private MorphlinesTester morphlineTester = new MorphlinesTester();
    private String confFile = "resources/conf-files/readVPN_Cisco.conf";

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
                       "Auth success example",
                        $("111367413: 2014 Mar 22 00:12:15.980 +0100 +1:00 %AUTH-6-4: RPT=33398: 80.36.103.199: Authentication successful: handle = 77, server = 172.16.19.110, user = bosch"),
                        $((String)null)
                ),
                $(
                        "Session start test",
                        $("111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: 37.11.25.29: User [mduran] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724)",
                          "111350320:  %AUTH-6-92: RPT=22376: 212.59.220.45: User [mduran] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724)",
                          "111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: 212.59.220.45: User [] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724)",
                          "111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: 212.59.220.45: User [mduran] Sending ACCT-START for assigned IP  (Session ID=9305F724)",
                          "111350320: 2014 Mar 21 23:03:49.730 +0100 +1:00 %AUTH-6-92: RPT=22376: : User [mduran] Sending ACCT-START for assigned IP 172.16.25.22 (Session ID=9305F724)"
                        ),
                        $("2014-03-21 21:03:49,1395435829,mduran,37.11.25.29,172.16.25.22,SUCCESS,,,,,,,,,,,,,false,false",
                          (String)null,
                          (String)null,
                          (String)null,
                          (String)null
                        )
                ),
                $(
                        "Session disconnected",
                        $("111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv: 2649665  Reason: User Requested",
                          "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv: 2649665  Reason: User Requested",
                          "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: : User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration:  Bytes xmt: 632880  Bytes rcv:   Reason: User Requested",
                          "111412517: 2 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv: 2649665  Reason: User Requested",
                          "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt:   Bytes rcv: 2649665  Reason: User Requested",
                          "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration: 16:30:23  Bytes xmt: 632880  Bytes rcv:   Reason: User Requested",
                          "111412517: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-5-28: RPT=187418: 37.11.25.29: User [pmoreno] Group [EXODOHP] disconnected:  Session Type: IPSec/UDP  Duration:  Bytes xmt: 632880  Bytes rcv:   Reason: User Requested"

                        ),

                        $("2014-03-22 02:07:25,1395454045,pmoreno,37.11.25.29,,CLOSED,,,,,,,,3282545,632880,2649665,,50,false,false",
                          (String)null,
                          (String)null,
                          (String)null,
                          (String)null,
                          (String)null,
                          (String)null
                        )
                ),
                $(
                        "Auth fail",
                        $("111502127: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-4-5: RPT=333150: 37.11.25.29: Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = monkey, domain = <not specified>",
                          "111502127: 00 +1:00 %AUTH-4-5: RPT=333150: 206.201.227.92: Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = monkey, domain = <not specified>",
                          "111502127: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-4-5: RPT=333150: : Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = monkey, domain = <not specified>",
                          "111502127: 2014 Mar 22 04:07:25.120 +0100 +1:00 %AUTH-4-5: RPT=333150: 206.201.227.92: Authentication rejected: Reason = Simultaneous logins exceeded for user handle = 86, server = (none), user = , domain = <not specified>"),


                        $("2014-03-22 02:07:25,1395454045,monkey,37.11.25.29,,FAIL,,,,,,,,,,,,,false,false",
                          (String)null,
                          (String)null,
                          (String)null

                        )
                )



        );
    }


}
