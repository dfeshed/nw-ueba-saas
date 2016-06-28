package fortscale.monitoring.external.stats.mongo.collector;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import fortscale.monitoring.external.stats.collector.impl.ExternalStatsCollectorMetrics;
import fortscale.monitoring.external.stats.collector.impl.mongo.collection.MongoCollectionCollectorImpl;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class MongoCollectorTest {


    @Test
    public void shouldCreateMetricForEachCollection() throws UnknownHostException {
        MongoTemplate mongoTemplate= Mockito.mock(MongoTemplate.class);
        Mockito.when(mongoTemplate.getDb()).thenReturn(Mockito.mock(DB.class));
        Mockito.when(mongoTemplate.getDb().getName()).thenReturn("fortscale");

        Set<String> collections =  new HashSet<String>(Arrays.asList("a", "b"));
        Mockito.when(mongoTemplate.getCollectionNames()).thenReturn(collections);
        Mockito.when(mongoTemplate.getCollection("a")).thenReturn(Mockito.mock(DBCollection.class));
        Mockito.when(mongoTemplate.getCollection("b")).thenReturn(Mockito.mock(DBCollection.class));
        ExternalStatsCollectorMetrics metrics = new ExternalStatsCollectorMetrics(null,"test");
        MongoCollectionCollectorImpl collector = new MongoCollectionCollectorImpl(mongoTemplate,null,metrics);
        collector.collect(0);
        Assert.assertTrue(collector.getCollectionMetricsMap().size()>1);
    }

}
