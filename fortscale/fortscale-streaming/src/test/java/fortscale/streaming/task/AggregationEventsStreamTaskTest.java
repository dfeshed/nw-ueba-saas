package fortscale.streaming.task;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.json.JSONException;
import org.junit.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by rans on 21/01/16.
 */
@Ignore
public class AggregationEventsStreamTaskTest extends AbstractTaskTest{
    //Inner class that extends the class of the task to be tested
    public static class AggregationEventsStreamTaskSubclass extends AggregationEventsStreamTask implements TestTask{
        @Override
        protected void wrappedInit(Config config, TaskContext context) throws Exception {
            //1. call the init function of the tested task
            super.wrappedInit(config, context);
            //2. Optional: retrieve the keyValueStore to be tested later
            keyValueStore = (KeyValueStore<String, String>)context.getStore(KEY_VALUE_STORE_TABLE_NAME);
            //3. init the test to register the task
            initTest(config, context);
        }
        @Override
        protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
                                      TaskCoordinator coordinator) throws Exception {
            //1. call the process function of the tested task
            super.wrappedProcess(envelope, collector, coordinator);
            //2. run teh test process function
            processTest(envelope, collector, coordinator);
        }
    }

    //define constants
    //class name should be of pattern: <THIS_CLASS_NAME>$<INNER_CLASS_NAME>
    private static final String TASK_CLASS_NAME = "fortscale.streaming.task.AggregationEventsStreamTaskTest$AggregationEventsStreamTaskSubclass";
    protected static final String STREAMING_CONFIG_FILE = "aggregation-events-streaming.properties";
    private static final String KEY_VALUE_STORE_TABLE_NAME = "feature_buckets_store";

    private static final String eventSsh1 = "{\"LR\":false,\"eventscore\":90.0,\"normalized_dst_machine_score\":90.0,\"normalized_src_machine_score\":90.0,\"dst_class\":\"Server\",\"source_ip\":\"192.168.224.14\",\"date_time_score\":90.0,\"hostname\":\"ISEUSR14_PC\",\"isUserAdministrator\":false,\"src_class\":\"Desktop\",\"date_time\":\"2016-01-21 10:30:30.0\",\"normalized_username\":\"iseusr2@somebigcompany.com\",\"target_machine\":\"iseusr2_SRV\",\"normalized_src_machine\":\"ISEUSR14_PC\",\"date_time_unix\":1453386238,\"isUserExecutive\":false,\"is_nat\":false,\"last_state\":\"HDFSWriterStreamTask\",\"normalized_dst_machine\":\"ISEUSR2_SRV\",\"target_machine_temp\":\"iseusr2_SRV\",\"isUserServiceAccount\":false,\"data_source\":\"ssh\",\"auth_method\":\"password\",\"auth_method_score\":90.0,\"is_sensitive_machine\":false,\"username\":\"iseusr2\",\"status\":\"Accepted\"}";
    private static final String eventSsh2 = "{\"LR\":false,\"eventscore\":90.0,\"normalized_dst_machine_score\":90.0,\"normalized_src_machine_score\":90.0,\"dst_class\":\"Server\",\"source_ip\":\"192.168.224.14\",\"date_time_score\":90.0,\"hostname\":\"ISEUSR14_PC\",\"isUserAdministrator\":false,\"src_class\":\"Desktop\",\"date_time\":\"2016-01-21 12:30:30.0\",\"normalized_username\":\"iseusr2@somebigcompany.com\",\"target_machine\":\"iseusr2_SRV\",\"normalized_src_machine\":\"ISEUSR14_PC\",\"date_time_unix\":1453389838,\"isUserExecutive\":false,\"is_nat\":false,\"last_state\":\"HDFSWriterStreamTask\",\"normalized_dst_machine\":\"ISEUSR2_SRV\",\"target_machine_temp\":\"iseusr2_SRV\",\"isUserServiceAccount\":false,\"data_source\":\"ssh\",\"auth_method\":\"password\",\"auth_method_score\":90.0,\"is_sensitive_machine\":false,\"username\":\"iseusr2\",\"status\":\"Accepted\"}";

    private static final String keyUsrDaily1 = "normalized_username_ssh_daily.fixed_duration_daily_1453334400###normalized_username###iseusr2@somebigcompany.com";
    private static final String keyUsrHourly1 = "normalized_username_ssh_hourly.fixed_duration_hourly_1453384800###normalized_username###iseusr2@somebigcompany.com";
    private static final String valueUsrDaily1 = "FeatureBucket{startTime=21-01-2016 02:00:00, endTime=22-01-2016 01:59:59, id='null', bucketId='fixed_duration_daily_1453334400###normalized_username###iseusr2@somebigcompany.com'}";
    private static final String valueUsrHourly1 = "FeatureBucket{startTime=21-01-2016 16:00:00, endTime=21-01-2016 16:59:59, id='null', bucketId='fixed_duration_hourly_1453384800###normalized_username###iseusr2@somebigcompany.com'}";


    @BeforeClass
    public static void beforeClass() throws IOException {
        propertiesPath = STREAMING_CONFIG_PATH + STREAMING_CONFIG_FILE;
        //add the subclass as the name of the class to load in Samza container
        addInfo.put(TASK_CLASS, TASK_CLASS_NAME);
        addInfo.put("task.window.ms", "-1");
        setupBefore();
    }

    @Before
    public void setup() throws IOException {
        //set topic names
        inputTopic = "fortscale-ssh-event-score";
    }

    @After
    public void cleanup() throws IOException {
        super.cleanupAfter();
    }




    @Test
    public void testSamza() throws InterruptedException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, JSONException {

        startJob();

        send(eventSsh1);
        send(eventSsh2);
        //Wait for keyvalue store to get the data inserted
        int i = 0;
        Thread.sleep(10000L);
        while (
                !keyValueStore.all().hasNext() && i < 20)
        {
            TestTask.logger.warn("could not read keyValueStore yet, retrying " + i);
            Thread.sleep(2000L);
            i++;
        }

        Assert.assertEquals(valueUsrHourly1, keyValueStore.get(keyUsrHourly1).toString());
        Assert.assertEquals(valueUsrDaily1, keyValueStore.get(keyUsrDaily1).toString());

        stopJob();

    }
}
