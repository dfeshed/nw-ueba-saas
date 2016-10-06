package fortscale.aggregation.feature.event;

import com.github.fakemongo.Fongo;
import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AggregatedFeatureEventsMongoStoreTest {

    private static final String AGGR_EVENT_TYPE = "aggr_event";

    @Configuration
    @Import({
            NullStatsServiceConfig.class
    })
    @EnableSpringConfigured
    @EnableAnnotationConfiguration
    public static class springConfig {
        private static final String FORTSCALE_TEST_DB = "fortscaleTestDb";
        private AggregatedFeatureEventsConfService aggrFeatureEventsConfService;

        private MongoDbFactory mongoDbFactory() {
            Fongo fongo = new Fongo(FORTSCALE_TEST_DB);

            return new SimpleMongoDbFactory(fongo.getMongo(), FORTSCALE_TEST_DB);
        }

        @Bean
        public AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore() {
            return new AggregatedFeatureEventsMongoStore();
        }

        @Bean
        public AggregatedFeatureEventsConfUtilService aggregatedFeatureEventsConfUtilService() {
            return mock(AggregatedFeatureEventsConfUtilService.class);
        }

        @Bean
        RetentionStrategiesConfService retentionStrategiesConfService() {
            return mock(RetentionStrategiesConfService.class);
        }

        @Bean
        public BucketConfigurationService bucketConfigurationService() {
            return mock(BucketConfigurationService.class);
        }

        @Bean
        public AggregatedFeatureEventsConfService aggrFeatureEventsConfService() {
            AggregatedFeatureEventConf aggregatedFeatureEventConf = mock(AggregatedFeatureEventConf.class);
            String retentionStrategyName = "retentionStrategyName";
            when(aggregatedFeatureEventConf.getRetentionStrategyName()).thenReturn(retentionStrategyName);
            int retentionInSeconds = 10;
            AggrFeatureRetentionStrategy retentionStrategy = new AggrFeatureRetentionStrategy(retentionStrategyName, retentionInSeconds);

            aggrFeatureEventsConfService = mock(AggregatedFeatureEventsConfService.class);
            when(aggrFeatureEventsConfService.getAggregatedFeatureEventConf(anyString())).thenReturn(aggregatedFeatureEventConf);
            when(aggrFeatureEventsConfService.getAggrFeatureRetnetionStrategy(anyString())).thenReturn(retentionStrategy);
            return aggrFeatureEventsConfService;
        }

        @Bean
        public MongoTemplate mongoTemplate() {
            return new MongoTemplate(mongoDbFactory());
        }

        @Bean
        public static TestPropertiesPlaceholderConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("streaming.event.field.type.aggr_event", AGGR_EVENT_TYPE);

            return new TestPropertiesPlaceholderConfigurer(properties);
        }

    }

    @Autowired
    private AggregatedFeatureEventsMongoStore store;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void shouldStoreEvents() {
        String aggrFeatureName = "aggrFeatureName";
        AggrEvent aggrEvent =
                getNewAggrEvent(aggrFeatureName);
        String collectionName = getCollectionName(aggrFeatureName);
        long writesBefore = store.getCollectionMetrics(collectionName).writes;
        store.storeEvent(aggrEvent);
        long writesAfter = store.getCollectionMetrics(collectionName).writes;
        Assert.assertTrue(mongoTemplate.getCollectionNames().contains(collectionName));
        Assert.assertEquals(writesAfter ,writesBefore + 1);
    }

    @Test
    public void shouldStoreEventsInBulk() {
        String aggrFeatureName = "aggrFeatureName";
        String aggrFeatureName1 = aggrFeatureName + "1";
        String aggrFeatureName2 = aggrFeatureName + "2";
        List<AggrEvent> aggrEvents = new LinkedList<>();
        int generatedEventsCount = 10;
        for (int i = 0; i < generatedEventsCount; i++) {
            aggrEvents.add(getNewAggrEvent(aggrFeatureName1));
        }
        for (int i = 0; i < generatedEventsCount; i++) {
            aggrEvents.add(getNewAggrEvent(aggrFeatureName2));
        }

        String collectionName1 = getCollectionName(aggrFeatureName1);
        String collectionName2 = getCollectionName(aggrFeatureName2);

        long writesBefore1 = store.getCollectionMetrics(collectionName1).writes;
        long bulkWritesBefore1 = store.getCollectionMetrics(collectionName1).bulkWrites;
        long writesBefore2 = store.getCollectionMetrics(collectionName2).writes;
        long bulkWritesBefore2 = store.getCollectionMetrics(collectionName2).bulkWrites;
        store.storeEvent(aggrEvents);
        long writesAfter1 = store.getCollectionMetrics(collectionName1).writes;
        long bulkWritesAfter1 = store.getCollectionMetrics(collectionName1).bulkWrites;
        long writesAfter2 = store.getCollectionMetrics(collectionName2).writes;
        long bulkWritesAfter2 = store.getCollectionMetrics(collectionName2).bulkWrites;

        Assert.assertTrue(mongoTemplate.getCollectionNames().contains(collectionName1));
        Assert.assertEquals(writesAfter1 ,writesBefore1 + generatedEventsCount);
        Assert.assertEquals(writesAfter2 ,writesBefore2 + generatedEventsCount);
        Assert.assertEquals(bulkWritesBefore1 +1 ,bulkWritesAfter1 );
        Assert.assertEquals(bulkWritesBefore2 +1 ,bulkWritesAfter2 );
    }

    private String getCollectionName(String aggrFeatureName) {
        return String.format("%s%s%s%s%s",
                AggregatedFeatureEventsMongoStore.COLLECTION_NAME_PREFIX,
                AggregatedFeatureEventsMongoStore.COLLECTION_NAME_SEPARATOR,
                AGGR_EVENT_TYPE,
                AggregatedFeatureEventsMongoStore.COLLECTION_NAME_SEPARATOR,
                aggrFeatureName);
    }

    private AggrEvent getNewAggrEvent(String aggrFeatureName) {
        return new AggrEvent("dataSource", "featureType", aggrFeatureName, 0d, new HashMap<>(), "bucketConfName", new HashMap<>(), "contextId", 0L, 0L, 0L, new ArrayList<>(), 0d, new ArrayList<>());
    }
}
