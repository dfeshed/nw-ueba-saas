package fortscale.streaming.task;

import fortscale.utils.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by rans on 24/12/15.
 */
public class AlertGeneratorTaskTest extends AbstractTaskTest{
    protected static final String STREAMING_CONFIG_FILE = "alert-generator-task.properties";
    private static Logger logger = Logger.getLogger(AlertGeneratorTaskTest.class);

    private String event1 = "{\"severity\":\"High\",\"entityTypeFieldName\":\"context.normalized_username\",\"last_state\":\"EvidenceCreationTask\",\"endDate\":1450828799000,\"entityType\":\"User\",\"anomalyType\":null,\"anomalyValue\":\"6.0\",\"numOfEvents\":6,\"supportingInformation\":null,\"top3events\":null,\"retentionDate\":1450742400000,\"dataEntitiesIds\":[\"kerberos_logins\"],\"score\":90,\"timeframe\":\"Daily\",\"anomalyTypeFieldName\":\"number_of_failed_kerberos_logins_daily\",\"entityName\":\"sausr15fs@somebigcompany.com\",\"evidenceType\":\"AnomalyAggregatedEvent\",\"top3eventsJsonStr\":null,\"name\":null,\"id\":\"83aa3ecd-d480-4266-a2fa-7aab5bec8b15\",\"startDate\":1450742400000}";
    private String event2 = "{\"severity\":\"Critical\",\"entityTypeFieldName\":\"context.normalized_username\",\"last_state\":\"EvidenceCreationTask\",\"endDate\":1450807199000,\"entityType\":\"User\",\"anomalyType\":null,\"anomalyValue\":\"1.0\",\"numOfEvents\":1,\"supportingInformation\":null,\"top3events\":null,\"retentionDate\":1450803600000,\"dataEntitiesIds\":[\"kerberos_logins\"],\"score\":100,\"timeframe\":\"Hourly\",\"anomalyTypeFieldName\":\"number_of_failed_kerberos_logins_hourly\",\"entityName\":\"alrusr10@somebigcompany.com\",\"evidenceType\":\"AnomalyAggregatedEvent\",\"top3eventsJsonStr\":null,\"name\":null,\"id\":\"118f9e63-3009-4bbf-ace5-5d00a1430441\",\"startDate\":1450803600000}";


    @Before
    public void setup() throws IOException {
        //set topic names
        inputTopic = "fortscale-evidences";
        outputTopic = "fortscale-evidences";
        String propertiesPath = System.getenv("HOME") + STREAMING_CONFIG_PATH + STREAMING_CONFIG_FILE;

        super.setupBefore(propertiesPath, null);
    }

    @After
    public void cleanup(){
        super.cleanupAfter();
    }





    @Test
    public void testSamza() throws InterruptedException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        startJob();

        // Validate that restored is empty.
//        assertEquals(0, task.initFinished().getCount());
//        assertEquals(0, task.restored().size());
//        assertEquals(0, task.received().size());

        // Send some messages to input stream.
//        Thread.sleep(120000);
        send(event1);
        send(event2);

        // Validate that messages appear in store stream.
//        List<String> messages = readAll(outputTopic, 5, "testShouldStartTaskForFirstTime");

//        assertEquals(event1, messages.get(0));
//        assertEquals(event2, messages.get(1));
        Thread.sleep(120000);
        stopJob();

    }
}
