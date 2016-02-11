package fortscale.streaming.service.topology;

import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by amira on 21/07/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/event-topology-context-test.xml" })
public class KafkaEventTopologyServiceTest {

    @Autowired
    KafkaEventTopologyService eventTopologyService;

    @Test
    public void testLoadingConfiguationFile() {

    }

    @Test
    public void testGetOutputTopicForEvent() throws Exception {
        JSONObject event = new JSONObject();
        event.put("data_source", "kerberos_login");
        eventTopologyService.setSendingJobName("raw-events-scoring-task");
        String outputTopic = eventTopologyService.getOutputTopicForEvent(event);
        Assert.assertEquals("fortscale-4769-event-score", outputTopic);
    }

    @Test
    public void testGetOutputTopicForEventWithNonMatchingFields() throws Exception {
        JSONObject event = new JSONObject();
        eventTopologyService.setSendingJobName("raw-events-scoring-task");
        String outputTopic = eventTopologyService.getOutputTopicForEvent(event);
        Assert.assertNull(outputTopic);
    }

    @Test(expected = Exception.class)
    public void testGetOutputTopicForEventWithoutSettingJobName() throws Exception {
        JSONObject event = new JSONObject();
        String outputTopic = eventTopologyService.getOutputTopicForEvent(event);
    }


}
