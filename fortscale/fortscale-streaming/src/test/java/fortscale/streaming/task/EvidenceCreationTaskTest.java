package fortscale.streaming.task;

import org.json.JSONException;
import org.junit.*;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by rans on 24/12/15.
 */
public class EvidenceCreationTaskTest extends AbstractTaskTest{
    protected static final String STREAMING_CONFIG_FILE = "evidence-creation-task.properties";
    protected final static String SPRING_CONTEXT_FIILE = "classpath*:META-INF/spring/samza-task-test-context.xml";

    //messages for input topic
    private String event1 = "{\"categoryString\":\"Kerberos Service Ticket Operations\",\"LR\":false,\"eventscore\":0.0,\"normalized_src_machine_score\":0.0,\"normalized_dst_machine_score\":0.0,\"dst_class\":\"Server\",\"nat_src_machine\":\"CLEAN4_PC\",\"machine_name\":\"CLEAN4_PC\",\"date_time_score\":60.0,\"isUserAdministrator\":false,\"src_class\":\"Desktop\",\"date_time\":\"2015-12-26 15:34:43\",\"normalized_username\":\"demouser3@somebigcompany.com\",\"account_name\":\"demouser3@somebigcompany.com\",\"service_id\":\"FORTSCALE\\\\FS-DC-01$\",\"normalized_src_machine\":\"CLEAN4_PC\",\"client_address\":\"7.0.0.4\",\"date_time_unix\":\"1451144083\",\"isUserExecutive\":false,\"recordNumber\":\"924637017\",\"failure_code\":\"0x0\",\"is_nat\":\"false\",\"last_state\":\"HDFSWriterStreamTask\",\"account_domain\":\"FORTSCALE\",\"logfile\":\"Security\",\"service_name\":\"demouser1_SRV\",\"normalized_dst_machine\":\"DEMOUSER1_SRV\",\"isUserServiceAccount\":false,\"data_source\":\"kerberos_logins\",\"eventCode\":\"4769\",\"timeGeneratedRaw\":\"2015-12-26T15:34:43.000+03:00\",\"failure_codescore\":0.0,\"ticket_options\":\"0x40810010\",\"sourceName\":\"Microsoft Windows security auditing.\",\"is_sensitive_machine\":false}";
    private String event2 = "{\"categoryString\":\"Kerberos Service Ticket Operations\",\"LR\":false,\"eventscore\":0.0,\"normalized_src_machine_score\":0.0,\"normalized_dst_machine_score\":0.0,\"dst_class\":\"Server\",\"nat_src_machine\":\"CLEAN4_PC\",\"machine_name\":\"CLEAN4_PC\",\"date_time_score\":70.0,\"isUserAdministrator\":false,\"src_class\":\"Desktop\",\"date_time\":\"2015-12-26 16:18:22\",\"normalized_username\":\"demouser3@somebigcompany.com\",\"account_name\":\"demouser3@somebigcompany.com\",\"service_id\":\"FORTSCALE\\\\FS-DC-01$\",\"normalized_src_machine\":\"CLEAN4_PC\",\"client_address\":\"7.0.0.4\",\"date_time_unix\":\"1451146702\",\"isUserExecutive\":false,\"recordNumber\":\"924637017\",\"failure_code\":\"0x0\",\"is_nat\":\"false\",\"last_state\":\"HDFSWriterStreamTask\",\"account_domain\":\"FORTSCALE\",\"logfile\":\"Security\",\"service_name\":\"demouser1_SRV\",\"normalized_dst_machine\":\"DEMOUSER1_SRV\",\"isUserServiceAccount\":false,\"data_source\":\"kerberos_logins\",\"eventCode\":\"4769\",\"timeGeneratedRaw\":\"2015-12-26T16:18:22.000+03:00\",\"failure_codescore\":0.0,\"ticket_options\":\"0x40810010\",\"sourceName\":\"Microsoft Windows security auditing.\",\"is_sensitive_machine\":false}";

    //expected messages for output topic
    private String outEvent1 = "{\"severity\":\"Low\",\"entityTypeFieldName\":\"normalized_username\",\"last_state\":\"EvidenceCreationTask\",\"endDate\":1451144083000,\"entityType\":\"User\",\"anomalyType\":null,\"anomalyValue\":\"2015-12-26 15:34:43\",\"numOfEvents\":1,\"supportingInformation\":null,\"top3events\":null,\"retentionDate\":1451144083000,\"dataEntitiesIds\":[\"kerberos_logins\"],\"score\":60,\"timeframe\":null,\"anomalyTypeFieldName\":\"event_time\",\"entityName\":\"demouser3@somebigcompany.com\",\"evidenceType\":\"AnomalySingleEvent\",\"top3eventsJsonStr\":null,\"name\":null,\"startDate\":1451144083000}";
    private String outEvent2 = "{\"severity\":\"Low\",\"entityTypeFieldName\":\"normalized_username\",\"last_state\":\"EvidenceCreationTask\",\"endDate\":1451146702000,\"entityType\":\"User\",\"anomalyType\":null,\"anomalyValue\":\"2015-12-26 16:18:22\",\"numOfEvents\":1,\"supportingInformation\":null,\"top3events\":null,\"retentionDate\":1451146702000,\"dataEntitiesIds\":[\"kerberos_logins\"],\"score\":70,\"timeframe\":null,\"anomalyTypeFieldName\":\"event_time\",\"entityName\":\"demouser3@somebigcompany.com\",\"evidenceType\":\"AnomalySingleEvent\",\"top3eventsJsonStr\":null,\"name\":null,\"startDate\":1451146702000}";

    @BeforeClass
    public static void beforeClass() throws IOException{
        propertiesPath = STREAMING_CONFIG_PATH + STREAMING_CONFIG_FILE;
        springContextFile = SPRING_CONTEXT_FIILE;
        addInfo = null;
        setupBefore();
    }
    @Before
    public void setup() throws IOException {
        //set topic names
        inputTopic = "fortscale-4769-event-score-after-write";
        outputTopic = "fortscale-evidences";
    }

    @After
    public void cleanup() throws IOException {
        super.cleanupAfter();
    }

    @Ignore
    @Test
    public void testSamza() throws InterruptedException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, JSONException {

        startJob();


        // Send some messages to input stream.
        send(event1);
        send(event2);

        // Validate that messages appear in store stream.
        List<String> messages = readMessages(2L, outputTopic);

        org.json.JSONObject jsonEvent1 = new org.json.JSONObject(messages.get(0));
        //remove element "id" as it is generated on the fly
        jsonEvent1.remove("id");
        org.json.JSONObject jsonExpectedEvent1 = new org.json.JSONObject(outEvent1);
        org.json.JSONObject jsonEvent2 = new org.json.JSONObject(messages.get(1));
        //remove element "id" as it is generated on the fly
        jsonEvent2.remove("id");
        org.json.JSONObject jsonExpectedEvent2 = new org.json.JSONObject(outEvent2);
        JSONAssert.assertEquals(jsonEvent1, jsonExpectedEvent1, false);
        JSONAssert.assertEquals(jsonEvent2, jsonExpectedEvent2, false);

        //stop the job
        stopJob();

    }
}
