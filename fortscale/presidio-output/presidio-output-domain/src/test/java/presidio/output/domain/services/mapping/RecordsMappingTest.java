package presidio.output.domain.services.mapping;


import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.users.User;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = fortscale.utils.elasticsearch.config.ElasticsearchConfig.class)
public class RecordsMappingTest {

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;

    @Test
    public void userTest() {
        Map map = esTemplate.getMapping(User.class);
        Map properties = (Map) map.get("properties");
        Assert.assertEquals(14, properties.size());
    }

    @Test
    public void alertTest() {
        Map map = esTemplate.getMapping(Alert.class);
        Map properties = (Map) map.get("properties");
        Assert.assertEquals(17, properties.size());
    }

    @Test
    public void indicatorTest() {
        Map map = esTemplate.getMapping(Indicator.class);
        Map properties = (Map) map.get("properties");
        Assert.assertEquals(11, properties.size());
    }
}
