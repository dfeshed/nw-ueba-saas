package fortscale.monitoring.external.stats.mongo.collector;

import fortscale.monitoring.external.stats.collector.impl.mongo.collection.MongoCollectionCollectorImpl;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { FakeMongoConfig.class })

public class MongoCollectorTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Before
    public void setup() throws IOException, JSONException {
        mongoTemplate.createCollection("test1");
        mongoTemplate.createCollection("test2");
        mongoTemplate.createCollection("test3");
        String originalMessage = "{\"element\":\"value\"}";

        JSONObject message= new JSONObject(originalMessage);
        mongoTemplate.insert(message,"test1");

    }
    @Test
    public void mongoCollectionCollectorTest()
    {
        MongoCollectionCollectorImpl collector = new MongoCollectionCollectorImpl(mongoTemplate,null);
        collector.collect(0);
        collector.getCollectionMetricsMap();
    }

}
