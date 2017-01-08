package fortscale.streaming.task;

import fortscale.streaming.task.message.ProcessMessageContext;
import fortscale.streaming.task.message.StreamingProcessMessageContext;
import org.apache.samza.config.Config;
import org.apache.samza.task.TaskContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by rans on 24/12/15.
 */
@Ignore
public class EvidenceCreationTaskTest extends AbstractTaskTest{

    //Inner class that extends the class of the task to be tested
    public static class EvidenceCreationTaskSubclass extends EvidenceCreationTask implements TestTask{
        @Override
        protected void processInit(Config config, TaskContext context) throws Exception {
            //1. call the init function of the tested task
            super.processInit(config, context);
            //2. Optional: retrieve the keyValueStore to be tested later
            //keyValueStore = (KeyValueStore<String, String>)context.getStore(KEY_VALUE_STORE_TABLE_NAME);
            //3. init the test to register the task
            initTest(config, context);
        }
        @Override
        protected void processMessage(ProcessMessageContext messageContext) throws Exception {
            //1. call the process function of the tested task
            super.processMessage(messageContext);
            StreamingProcessMessageContext streamingProcessMessageContext = (StreamingProcessMessageContext) messageContext;
            //2. run teh test process function
            processTest(messageContext, streamingProcessMessageContext.getCollector(), streamingProcessMessageContext.getCoordinator());
        }
    }
    //define constants
    //class name should be of pattern: <THIS_CLASS_NAME>$<INNER_CLASS_NAME>
    private static final String TASK_CLASS_NAME = "fortscale.streaming.task.EvidenceCreationTaskTest$EvidenceCreationTaskSubclass";
    protected static final String STREAMING_CONFIG_FILE = "evidence-creation-task.properties";
    protected final static String SPRING_CONTEXT_FIILE = "classpath*:META-INF/spring/samza-task-test-context.xml";

    //messages for input topic
    private String event1 = "{\"categoryString\":\"Kerberos Service Ticket Operations\",\"LR\":false,\"eventscore\":0.0,\"normalized_src_machine_score\":0.0,\"normalized_dst_machine_score\":0.0,\"dst_class\":\"Server\",\"nat_src_machine\":\"CLEAN4_PC\",\"machine_name\":\"CLEAN4_PC\",\"date_time_score\":60.0,\"isUserAdministrator\":false,\"src_class\":\"Desktop\",\"date_time\":\"2015-12-26 15:34:43\",\"normalized_username\":\"demouser3@somebigcompany.com\",\"account_name\":\"demouser3@somebigcompany.com\",\"service_id\":\"FORTSCALE\\\\FS-DC-01$\",\"normalized_src_machine\":\"CLEAN4_PC\",\"client_address\":\"7.0.0.4\",\"date_time_unix\":\"1451144083\",\"isUserExecutive\":false,\"recordNumber\":\"924637017\",\"failure_code\":\"0x0\",\"is_nat\":\"false\",\"last_state\":\"HDFSWriterStreamTask\",\"account_domain\":\"FORTSCALE\",\"logfile\":\"Security\",\"service_name\":\"demouser1_SRV\",\"normalized_dst_machine\":\"DEMOUSER1_SRV\",\"isUserServiceAccount\":false,\"data_source\":\"kerberos_logins\",\"eventCode\":\"4769\",\"timeGeneratedRaw\":\"2015-12-26T15:34:43.000+03:00\",\"failure_codescore\":0.0,\"ticket_options\":\"0x40810010\",\"sourceName\":\"Microsoft Windows security auditing.\",\"is_sensitive_machine\":false}";
    private String event2 = "{\"categoryString\":\"Kerberos Service Ticket Operations\",\"LR\":false,\"eventscore\":0.0,\"normalized_src_machine_score\":0.0,\"normalized_dst_machine_score\":0.0,\"dst_class\":\"Server\",\"nat_src_machine\":\"CLEAN4_PC\",\"machine_name\":\"CLEAN4_PC\",\"date_time_score\":70.0,\"isUserAdministrator\":false,\"src_class\":\"Desktop\",\"date_time\":\"2015-12-26 16:18:22\",\"normalized_username\":\"demouser3@somebigcompany.com\",\"account_name\":\"demouser3@somebigcompany.com\",\"service_id\":\"FORTSCALE\\\\FS-DC-01$\",\"normalized_src_machine\":\"CLEAN4_PC\",\"client_address\":\"7.0.0.4\",\"date_time_unix\":\"1451146702\",\"isUserExecutive\":false,\"recordNumber\":\"924637017\",\"failure_code\":\"0x0\",\"is_nat\":\"false\",\"last_state\":\"HDFSWriterStreamTask\",\"account_domain\":\"FORTSCALE\",\"logfile\":\"Security\",\"service_name\":\"demouser1_SRV\",\"normalized_dst_machine\":\"DEMOUSER1_SRV\",\"isUserServiceAccount\":false,\"data_source\":\"kerberos_logins\",\"eventCode\":\"4769\",\"timeGeneratedRaw\":\"2015-12-26T16:18:22.000+03:00\",\"failure_codescore\":0.0,\"ticket_options\":\"0x40810010\",\"sourceName\":\"Microsoft Windows security auditing.\",\"is_sensitive_machine\":false}";

    //expected messages for output topic
    private String outEvent1 = "{\"severity\":\"Low\",\"entityTypeFieldName\":\"normalized_username\",\"last_state\":\"EvidenceCreationTaskSubclass\",\"endDate\":1451144083000,\"entityType\":\"User\",\"anomalyType\":null,\"anomalyValue\":\"2015-12-26 15:34:43\",\"numOfEvents\":1,\"supportingInformation\":null,\"top3events\":null,\"retentionDate\":1451144083000,\"dataEntitiesIds\":[\"kerberos_logins\"],\"score\":60,\"timeframe\":null,\"anomalyTypeFieldName\":\"event_time\",\"entityName\":\"demouser3@somebigcompany.com\",\"evidenceType\":\"AnomalySingleEvent\",\"top3eventsJsonStr\":null,\"name\":null,\"startDate\":1451144083000}";
    private String outEvent2 = "{\"severity\":\"Low\",\"entityTypeFieldName\":\"normalized_username\",\"last_state\":\"EvidenceCreationTaskSubclass\",\"endDate\":1451146702000,\"entityType\":\"User\",\"anomalyType\":null,\"anomalyValue\":\"2015-12-26 16:18:22\",\"numOfEvents\":1,\"supportingInformation\":null,\"top3events\":null,\"retentionDate\":1451146702000,\"dataEntitiesIds\":[\"kerberos_logins\"],\"score\":70,\"timeframe\":null,\"anomalyTypeFieldName\":\"event_time\",\"entityName\":\"demouser3@somebigcompany.com\",\"evidenceType\":\"AnomalySingleEvent\",\"top3eventsJsonStr\":null,\"name\":null,\"startDate\":1451146702000}";

    @BeforeClass
    public static void beforeClass() throws IOException{
        propertiesPath = STREAMING_CONFIG_PATH + STREAMING_CONFIG_FILE;
        springContextFile = SPRING_CONTEXT_FIILE;
        //add the subclass as the name of the class to load in Samza container
        addInfo.put("task.class", TASK_CLASS_NAME);
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

    @Test
    public void testSamza() throws InterruptedException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, JSONException {

        startJob();


        // Send some messages to input stream.
        send(event1);
        send(event2);

        // Validate that messages appear in store stream.
        List<String> messages = readMessages(2L, outputTopic);

        JSONObject jsonEvent1 = new JSONObject(messages.get(0));
        JSONObject jsonExpectedEvent1 = new JSONObject(outEvent1);
        JSONObject jsonEvent2 = new JSONObject(messages.get(1));
        JSONObject jsonExpectedEvent2 = new JSONObject(outEvent2);
        JSONAssert.assertEquals(jsonExpectedEvent1, jsonEvent1, false);
        JSONAssert.assertEquals(jsonExpectedEvent2, jsonEvent2, false);

        //stop the job
        stopJob();

    }
}
