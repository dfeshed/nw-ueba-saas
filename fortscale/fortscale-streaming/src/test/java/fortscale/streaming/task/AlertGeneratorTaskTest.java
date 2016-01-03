package fortscale.streaming.task;

import org.apache.samza.storage.kv.KeyValueStore;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by rans on 24/12/15.
 */
public class AlertGeneratorTaskTest extends AbstractTaskTest{
    protected static final String STREAMING_CONFIG_FILE = "alert-generator-task.properties";
    protected final static String SPRING_CONTEXT_FIILE = "classpath*:META-INF/spring/samza-task-test-context.xml";
    private static final String KEY_VALUE_STORE_TABLE_NAME = "entity-tags-cache";



    private static final String event1 = "[\"service\"]";
    private static final String event2 = "[\"admin\"]";
    private static final String event3 = "[\"executive\"]";

    private static final String tagKey1 = "EntityTags{entityName='1', entityType=User}";
    private static final String tagValue1 = "EntityTags{entityName='1', entityType=User}";

    private KeyValueStore<String, String> tagsCacheStore;

    @Before
    public void setup() throws IOException {
        //set topic names
        inputTopic = "user-tag-service-cache-updates";
        String propertiesPath = System.getenv("HOME") + STREAMING_CONFIG_PATH + STREAMING_CONFIG_FILE;

        super.setupBefore(propertiesPath, null);
    }

    @After
    public void cleanup(){
        super.cleanupAfter();
    }





    @Test
    public void testSamza() throws InterruptedException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, JSONException {

        startJob();
//        this.tagsCacheStore = (KeyValueStore<String, String>) context.getStore(ENTITY_TAGS_CACHE);



        send(event1);
        send(event2);
        send(event3);

        //retrieve KeyValueStore
        Class c = Class.forName(jobConfig.get("task.class"));
        Method m = c.getMethod("getStore", String.class);
        Object o;
        o = m.invoke(null, KEY_VALUE_STORE_TABLE_NAME);
        keyValueStore = (KeyValueStore)o;

        // Validate that messages appear in store stream.
//        List<String> messages = readAll(outputTopic, 5, "testShouldStartTaskForFirstTime");

//        assertEquals(event1, messages.get(0));
//        assertEquals(event2, messages.get(1));
        Thread.sleep(20000);
        Thread.sleep(20000);

        JSONAssert.assertEquals(keyValueStore.get(tagKey1).toString(), tagValue1, false);
        stopJob();

    }
}
