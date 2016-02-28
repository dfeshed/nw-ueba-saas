package fortscale.streaming.task;

import fortscale.domain.core.EntityTags;
import fortscale.streaming.alert.subscribers.SmartAlertCreationSubscriber;
import fortscale.services.impl.SpringService;
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
 * Created by rans on 24/12/15.
 */
@Ignore
public class AlertGeneratorTaskTest extends AbstractTaskTest{


    //Inner class that extends the class of the task to be tested
    public static class AlertGeneratorTaskSubclass extends AlertGeneratorTask implements TestTask{
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
    private static final String TASK_CLASS_NAME = "fortscale.streaming.task.AlertGeneratorTaskTest$AlertGeneratorTaskSubclass";
    protected static final String STREAMING_CONFIG_FILE = "alert-generator-task.properties";
    protected final static String SPRING_CONTEXT_FIILE = "classpath*:META-INF/spring/streaming-AlertGeneratorTask-test-context.xml";
    private static final String KEY_VALUE_STORE_TABLE_NAME = "entity-tags-cache";

    private static final String eventTag1 = "[\"service\",\"admin\"]";

    private static final String tagKey1 = "EntityTags{entityName='1', entityType=User}";
    private static final String tagValue1 = "EntityTags{entityName='1', entityType=User}";


    @BeforeClass
    public static void beforeClass() throws IOException{
        propertiesPath = STREAMING_CONFIG_PATH + STREAMING_CONFIG_FILE;
        springContextFile = SPRING_CONTEXT_FIILE;
        //add the subclass as the name of the class to load in Samza container
        addInfo.put(TASK_CLASS, TASK_CLASS_NAME);
        setupBefore();
    }

    @Before
    public void setup() throws IOException {
        //set topic names
        inputTopic = "user-tag-service-cache-updates";
    }

    @After
    public void cleanup() throws IOException {
        super.cleanupAfter();
    }




    @Test
    public void testSamza() throws InterruptedException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, JSONException {

        startJob();

        send(eventTag1);
        //Wait for keyvalue store to get the data inserted
        int i = 0;
        while (
            !keyValueStore.all().hasNext() && i < 20)
        {
            TestTask.logger.warn("could not read keyValueStore yet, retrying " + i);
            Thread.sleep(2000L);
            i++;
        }

        SmartAlertCreationSubscriber smartAlertCreationSubscriber = (SmartAlertCreationSubscriber)SpringService.getInstance().resolve("smartAlertCreationSubscriber");
        Assert.assertEquals(tagValue1, keyValueStore.get(tagKey1).toString());
        Assert.assertEquals("service", ((EntityTags)keyValueStore.get("EntityTags{entityName='1', entityType=User}")).getTags().get(0));
        Assert.assertEquals("admin", ((EntityTags)keyValueStore.get("EntityTags{entityName='1', entityType=User}")).getTags().get(1));
        stopJob();

    }
}
