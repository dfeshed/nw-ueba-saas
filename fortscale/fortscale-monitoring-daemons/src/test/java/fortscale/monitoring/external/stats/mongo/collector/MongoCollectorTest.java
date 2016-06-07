package fortscale.monitoring.external.stats.mongo.collector;

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import fortscale.monitoring.external.stats.collector.impl.mongo.collection.MongoCollectionCollectorImpl;
import fortscale.monitoring.external.stats.collector.impl.mongo.collection.MongoCollectionImplMetrics;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;


public class MongoCollectorTest {


    @Test
    public void mongoCollectionCollectorTest()
    {
        MongoTemplate mongoTemplate= Mockito.mock(MongoTemplate.class);
        Mockito.when(mongoTemplate.getDb()).thenReturn(Mockito.mock(DB.class));
        Mockito.when(mongoTemplate.getDb().getName()).thenReturn("fortscale");

        Set<String> collections =  new HashSet<String>(Arrays.asList("a", "b"));
        Mockito.when(mongoTemplate.getCollectionNames()).thenReturn(collections);
        Mockito.when(mongoTemplate.getCollection("a")).thenReturn(Mockito.mock(DBCollection.class));
        Mockito.when(mongoTemplate.getCollection("b")).thenReturn(Mockito.mock(DBCollection.class));

        HashMap stats = new HashMap();
        stats.put("size",1);
        stats.put("storageSize",2);
        stats.put("avgObjSize",3);
        stats.put("count",3);
        stats.put("totalIndexSize",3);
        HashMap wiredTigerStats = new HashMap();
        HashMap cacheStats = new HashMap();

        cacheStats.put("bytes read into cache",3);
        cacheStats.put("bytes written from cache",3);
        wiredTigerStats.put("cache",cacheStats);
        stats.put("wiredTiger",wiredTigerStats);
        Mockito.when(mongoTemplate.getCollection("a").getStats()).thenReturn((CommandResult) stats);


        MongoCollectionCollectorImpl collector = new MongoCollectionCollectorImpl(mongoTemplate,null);
        collector.collect(0);
        Map<String, MongoCollectionImplMetrics> metrics = collector.getCollectionMetricsMap();
        for (Field field: MongoCollectionCollectorImpl.class.getDeclaredFields()) {
        }
    }

}
