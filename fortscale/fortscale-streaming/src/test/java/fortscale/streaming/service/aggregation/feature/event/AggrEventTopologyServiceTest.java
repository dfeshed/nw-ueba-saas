package fortscale.streaming.service.aggregation.feature.event;

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
@ContextConfiguration(locations = { "classpath*:META-INF/spring/streaming-aggr-context.xml" })
public class AggrEventTopologyServiceTest {

    @Autowired
    AggrKafkaEventTopologyService aggrEventTopologyService;

    @Test
    public void testLoadingJsonFile() {
        String topic = aggrEventTopologyService.getTopicForEventType("aggregated_feature_event");
        Assert.assertEquals("fortscale-aggregated-feature-event", topic);
    }
}
