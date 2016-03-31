package fortscale.collection.morphlines.db.oracle;

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
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static junitparams.JUnitParamsRunner.$;

/**
 * Test class for oracle native audit morphline processing
 *
 * @author gils
 * 26/01/2016
 */
@RunWith(JUnitParamsRunner.class)
public class OracleAuditEventProcessTest {
    private MorphlinesTester morphlineTester = new MorphlinesTester();

    private static final String IMPALA_DATA_ORACLE_TABLE_FIELDS = "impala.data.oracle.table.fields";
    private final static String confFile = "resources/conf-files/db/oracle/parseORACLE.conf";
    private final static String confEnrichmentFile = "resources/conf-files/enrichment/readORACLE_enrich.conf";
    private static final String FORTSCALE_COLLECTION_TEST_PROPERTIES = "/META-INF/fortscale-collection-test.properties";

    @Before
    public void setUp() throws Exception {
        PropertiesResolver propertiesResolver = new PropertiesResolver(FORTSCALE_COLLECTION_TEST_PROPERTIES);
        String impalaTableFields = propertiesResolver.getProperty(IMPALA_DATA_ORACLE_TABLE_FIELDS);
        List<String> oracleOutputFields = ImpalaParser.getTableFieldNames(impalaTableFields);
        morphlineTester.init(new String[]{confFile, confEnrichmentFile}, oracleOutputFields);
    }

    @SuppressWarnings("resource")
    @BeforeClass
    public static void setUpClass() {
        new ClassPathXmlApplicationContext("classpath*:META-INF/spring/collection-context-test-light.xml");
    }

    @After
    public void tearDown() throws Exception {
        morphlineTester.close();
    }

    @Test
    @Parameters
    public void test(String testCase, Object[] lines, Object[] outputs) {
        morphlineTester.testMultipleLines(testCase, Arrays.stream(lines).map(a -> (String)a).collect(Collectors.toList()) ,
                Arrays.stream(outputs).map(a -> (String)a).collect(Collectors.toList()));
    }


    @SuppressWarnings("unused")
    private Object[] parametersForTest() {
        return	$(
                $(
                        "Logon Events",
                        $(
                                "{\"format\":1,\"raw_log\":\"Dec  7 16:05:45 tar1310.crossperf.ptec Oracle Audit[11974]: [ID 748625 local7.warning] LENGTH: \\\"357\\\" SESSIONID:[8] \\\"20924831\\\" ENTRYID:[1] \\\"1\\\" STATEMENT:[1] \\\"1\\\" USERID:[8] \\\"MAILQAPP\\\" USERHOST:[38] \\\"cppums1-mailq-privil-01.crossperf.ptec\\\" ACTION:[3] \\\"100\\\" RETURNCODE:[1] \\\"0\\\" COMMENT$TEXT:[100] \\\"Authenticated by: DATABASE; Client address: (ADDRESS=(PROTOCOL=tcp)(HOST=10.143.70.216)(PORT=43679))\\\" OS$USERID:[6] \\\"apache\\\" DBID:[9] \\\"131437128\\\" PRIV$USED:[1] \\\"5\\\"",
                                "{\"format\":1,\"raw_log\":\"Dec  7 16:05:22 casino2-ums-db-01.pokerperf.ptec Oracle Audit[10367]: [ID 748625 local7.warning] LENGTH: \\\"332\\\" SESSIONID:[9] \\\"464285491\\\" ENTRYID:[1] \\\"1\\\" STATEMENT:[1] \\\"1\\\" USERID:[7] \\\"POTOOLS\\\" USERHOST:[12] \\\"poms.po.ptec\\\" ACTION:[3] \\\"100\\\" RETURNCODE:[1] \\\"0\\\" COMMENT$TEXT:[100] \\\"Authenticated by: DATABASE; Client address: (ADDRESS=(PROTOCOL=tcp)(HOST=192.168.8.168)(PORT=49215))\\\" OS$USERID:[5] \\\"nginx\\\" DBID:[10] \\\"3599862714\\\" PRIV$USED:[1] \\\"5\\\"",
                                "{\"format\":1,\"raw_log\":\"Dec  7 18:05:06 extdev-extdev2-db.extdev.ptec Oracle Audit[26217]: [ID 748625 local7.warning] LENGTH: \\\"349\\\" SESSIONID:[9] \\\"141101902\\\" ENTRYID:[1] \\\"1\\\" STATEMENT:[1] \\\"1\\\" USERID:[4] \\\"CRON\\\" USERHOST:[32] \\\"extdev-extdev2-admin.extdev.ptec\\\" ACTION:[3] \\\"100\\\" RETURNCODE:[1] \\\"0\\\" COMMENT$TEXT:[98] \\\"Authenticated by: DATABASE; Client address: (ADDRESS=(PROTOCOL=tcp)(HOST=10.27.3.141)(PORT=41165))\\\" OS$USERID:[8] \\\"playtech\\\" DBID:[10] \\\"3559652912\\\" PRIV$USED:[1] \\\"5\\\""
                        ),
                        $(
                                "2015-12-07 16:05:45,1449504345,apache,MAILQAPP,cppums1-mailq-privil-01.crossperf.ptec,tar1310.crossperf.ptec,10.143.70.216,131437128,,Login,0,5",
                                "2015-12-07 16:05:22,1449504322,nginx,POTOOLS,poms.po.ptec,casino2-ums-db-01.pokerperf.ptec,192.168.8.168,3599862714,,Login,0,5",
                                "2015-12-07 18:05:06,1449511506,playtech,CRON,extdev-extdev2-admin.extdev.ptec,extdev-extdev2-db.extdev.ptec,10.27.3.141,3559652912,,Login,0,5"
                        )
                ),
                $(
                        "Select Events",
                        $(
                                "\"{\"format\":1,\"raw_log\":\"Dec  7 18:05:20 umsdev1-ums-db-01.ums.ptec Oracle Audit[28265]: [ID 748625 local7.warning] LENGTH: \\\"296\\\" SESSIONID:[8] \\\"13052207\\\" ENTRYID:[5] \\\"74916\\\" STATEMENT:[6] \\\"299652\\\" USERID:[4] \\\"JAVA\\\" USERHOST:[34] \\\"umsdev1-cas-priva-game-01.ums.ptec\\\" TERMINAL:[7] \\\"unknown\\\" ACTION:[1] \\\"3\\\" RETURNCODE:[3] \\\"942\\\" OBJ$CREATOR:[6] \\\"GAMING\\\" OBJ$NAME:[12] \\\"MPGAMETABLES\\\" OS$USERID:[4] \\\"apps\\\" DBID:[10] \\\"3678712847\\\"",
                                "\"{\"format\":1,\"raw_log\":\"Dec  6 18:05:03 umsdev1-ums-db-01.ums.ptec Oracle Audit[28856]: [ID 748625 local7.warning] LENGTH: \\\"290\\\" SESSIONID:[8] \\\"13052307\\\" ENTRYID:[5] \\\"24044\\\" STATEMENT:[5] \\\"50540\\\" USERID:[4] \\\"JAVA\\\" USERHOST:[34] \\\"umsdev1-cas-pub-server-01.ums.ptec\\\" TERMINAL:[7] \\\"unknown\\\" ACTION:[1] \\\"3\\\" RETURNCODE:[3] \\\"942\\\" OBJ$CREATOR:[6] \\\"GAMING\\\" OBJ$NAME:[8] \\\"JACKPOTS\\\" OS$USERID:[4] \\\"apps\\\" DBID:[10] \\\"3678712847\\\"\\\"",
                                "\"{\"format\":1,\"raw_log\":\"Dec  7 18:05:09 umsdev1-ums-db-01.ums.ptec Oracle Audit[28742]: [ID 748625 local7.warning] LENGTH: \\\"297\\\" SESSIONID:[8] \\\"13052271\\\" ENTRYID:[5] \\\"23931\\\" STATEMENT:[5] \\\"50428\\\" USERID:[4] \\\"JAVA\\\" USERHOST:[34] \\\"umsdev1-cas-pub-server-01.ums.ptec\\\" TERMINAL:[7] \\\"unknown\\\" ACTION:[1] \\\"3\\\" RETURNCODE:[3] \\\"942\\\" OBJ$CREATOR:[6] \\\"GAMING\\\" OBJ$NAME:[14] \\\"CASINOJACKPOTS\\\" OS$USERID:[4] \\\"apps\\\" DBID:[10] \\\"3678712847\\\"\\\""

                        ),
                        $(
                                "2015-12-07 18:05:20,1449511520,apps,JAVA,umsdev1-cas-priva-game-01.ums.ptec,umsdev1-ums-db-01.ums.ptec,,3678712847,MPGAMETABLES,Select,942,",
                                "2015-12-06 18:05:03,1449425103,apps,JAVA,umsdev1-cas-pub-server-01.ums.ptec,umsdev1-ums-db-01.ums.ptec,,3678712847,JACKPOTS,Select,942,",
                                "2015-12-07 18:05:09,1449511509,apps,JAVA,umsdev1-cas-pub-server-01.ums.ptec,umsdev1-ums-db-01.ums.ptec,,3678712847,CASINOJACKPOTS,Select,942,"
                        )
                ),
                $(
                        "Session record Events (Not supported)",
                        $(
                                "\"{\"format\":1,\"raw_log\":\"Dec  7 16:05:37 tar1405.staging.ptec Oracle Audit[27063]: [ID 748625 local7.warning] LENGTH: \\\"350\\\" SESSIONID:[9] \\\"185780811\\\" ENTRYID:[6] \\\"603654\\\" STATEMENT:[6] \\\"219539\\\" USERID:[6] \\\"ALLARL\\\" USERHOST:[17] \\\"EE\\\\\\PLAYTECH-A1866\\\" TERMINAL:[14] \\\"PLAYTECH-A1866\\\" ACTION:[3] \\\"103\\\" RETURNCODE:[1] \\\"0\\\" OBJ$CREATOR:[3] \\\"SYS\\\" OBJ$NAME:[10] \\\"X$KSUSESTA\\\" SES$ACTIONS:[16] \\\"---------S------\\\" SES$TID:[10] \\\"4294951009\\\" OS$USERID:[6] \\\"allarl\\\" DBID:[10] \\\"3329355035\\\""
                        ),
                        $(
                                (String)null
                        )
                ),
                $(
                        "Events with missing fields",
                        $(
                                "\"{\"format\":1,\"raw_log\":\"Dec  7 16:05:45 tar1310.crossperf.ptec Oracle Audit[11974]: [ID 748625 local7.warning] LENGTH: \\\"357\\\" SESSIONID:[8] \\\"20924831\\\" ENTRYID:[1] \\\"1\\\" STATEMENT:[1] \\\"1\\\" USERID:[8] \\\"MAILQAPP\\\" USERHOST:[38] \\\"cppums1-mailq-privil-01.crossperf.ptec\\\" ACTION:[3] \\\"100\\\" RETURNCODE:[1] \\\"0\\\" COMMENT$TEXT:[100] \\\"Authenticated by: DATABASE; Client address: (ADDRESS=(PROTOCOL=tcp)(HOST=10.143.70.216)(PORT=43679))\\\" DBID:[9] \\\"131437128\\\" PRIV$USED:[1] \\\"5\\\"",
                                "\"{\"format\":1,\"raw_log\":\"Dec  7 18:05:20 umsdev1-ums-db-01.ums.ptec Oracle Audit[28265]: [ID 748625 local7.warning] LENGTH: \\\"296\\\" SESSIONID:[8] \\\"13052207\\\" ENTRYID:[5] \\\"74916\\\" STATEMENT:[6] \\\"299652\\\" USERID:[4] \\\"JAVA\\\" USERHOST:[34] \\\"umsdev1-cas-priva-game-01.ums.ptec\\\" TERMINAL:[7] \\\"unknown\\\" ACTION:[1] \\\"3\\\" OBJ$CREATOR:[6] \\\"GAMING\\\" OBJ$NAME:[12] \\\"MPGAMETABLES\\\" OS$USERID:[4] \\\"apps\\\" DBID:[10] \\\"3678712847\\\"",
                                "umsdev1-ums-db-01.ums.ptec Oracle Audit[28265]: [ID 748625 local7.warning] LENGTH: \\\"296\\\" SESSIONID:[8] \\\"13052207\\\" ENTRYID:[5] \\\"74916\\\" STATEMENT:[6] \\\"299652\\\" USERID:[4] \\\"JAVA\\\" USERHOST:[34] \\\"umsdev1-cas-priva-game-01.ums.ptec\\\" TERMINAL:[7] \\\"unknown\\\" ACTION:[1] \\\"3\\\" RETURNCODE:[3] \\\"942\\\" OBJ$CREATOR:[6] \\\"GAMING\\\" OBJ$NAME:[12] \\\"MPGAMETABLES\\\" OS$USERID:[4] \\\"apps\\\" DBID:[10] \\\"3678712847\\\""
                        ),
                        $(
                                (String)null,
                                (String)null,
                                (String)null
                        )
                ),
                $(
                        "Any other event (Not supported)",
                        $(
                                "{\"format\":1,\"raw_log\":\"%WINDNS-4:       TC        0\",\"unique_id\":\"4BA5812890DBAD022500651B00005639\",\"packetid\":162597479755}"
                        ),
                        $(
                                (String)null
                        )
                )
        );
    }
}
