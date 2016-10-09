package fortscale.accumulator.aggregation;

import com.github.fakemongo.Fongo;
import fortscale.accumulator.accumulator.AccumulationParams;
import fortscale.accumulator.aggregation.config.AggregatedFeatureEventsAccumulatorConfig;
import fortscale.accumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;
import fortscale.accumulator.translator.AccumulatedFeatureTranslator;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.utils.monitoring.stats.StatsService;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AggregatedFeatureEventsAccumulatorTest {
    @Configuration
    @Import({
            NullStatsServiceConfig.class,
            AggregatedFeatureEventsAccumulatorConfig.class
    })
    @EnableSpringConfigured
    @EnableAnnotationConfiguration
    public static class springConfig {
        private static final String FORTSCALE_TEST_DB = "fortscaleTestDb";
        @Autowired
        private StatsService statsService;

        private MongoDbFactory mongoDbFactory() {
            Fongo fongo = new Fongo(FORTSCALE_TEST_DB);

            return new SimpleMongoDbFactory(fongo.getMongo(), FORTSCALE_TEST_DB);
        }

        @Bean
        public MongoTemplate mongoTemplate() {
            return new MongoTemplate(mongoDbFactory());
        }


        @Bean
        public static TestPropertiesPlaceholderConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("streaming.event.field.type.aggr_event", "aggr_event");
            properties.put("streaming.event.field.type.entity_event","entity_event");

            properties.put("fortscale.aggregation.bucket.conf.json.file.name", "classpath:config/asl/buckets.json");
            properties.put("fortscale.aggregation.bucket.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/buckets/overriding/*.json");
            properties.put("fortscale.aggregation.bucket.conf.json.additional.files.path", "file:home/cloudera/fortscale/config/asl/buckets/additional/*.json");

            properties.put("fortscale.aggregation.retention.strategy.conf.json.file.name", "classpath:config/asl/retention_strategies.json");
            properties.put("fortscale.aggregation.retention.strategy.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/retention_strategy/overriding/*.json");
            properties.put("fortscale.aggregation.retention.strategy.conf.json.additional.files.path", "file:home/cloudera/fortscale/config/asl/retention_strategy/additional/*.json");

            properties.put("fortscale.aggregation.feature.event.conf.json.file.name", "classpath:config/asl/aggregated_feature_events.json");
            properties.put("fortscale.aggregation.feature.event.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/aggregation_events/overriding/*.json");
            properties.put("fortscale.aggregation.feature.event.conf.json.additional.files.path", "file:home/cloudera/fortscale/config/asl/aggregation_events/additional/*.json");

            return new TestPropertiesPlaceholderConfigurer(properties);
        }

    }

    @Autowired
    AccumulatedFeatureTranslator translator;
    @Autowired
    private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
    @Autowired
    private AggregatedFeatureEventsAccumulator accumulator;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void shouldAccumulateAggrEvents() throws Exception {
        String featureName = "distinct_number_of_normalized_src_machine_kerberos_logins_daily";
        long creationEpochTimeSeconds = Instant.parse("2016-10-09T01:00:00Z").getEpochSecond();
        long startTimeUnixSeconds = Instant.parse("2016-10-09T01:00:00Z").getEpochSecond();
        long endTimeUnixSeconds = Instant.parse("2016-10-09T02:00:00Z").getEpochSecond();
        double score = 0d;
        Double aggregatedFeatureValue = 0d;
        String dataSource = "dataSource";
        String featureType = "featureType";
        String bucketConfName = "bucketConfName";
        String contextId = "contextId";
        AggrEvent aggrEvent = new AggrEvent(dataSource, featureType, featureName, aggregatedFeatureValue, new HashMap<>(), bucketConfName, new HashMap<>(), contextId, creationEpochTimeSeconds, startTimeUnixSeconds, endTimeUnixSeconds, new ArrayList<>(), score, new ArrayList<>());
        aggregatedFeatureEventsMongoStore.storeEvent(aggrEvent);
        startTimeUnixSeconds = Instant.parse("2016-10-09T02:00:00Z").getEpochSecond();
        endTimeUnixSeconds = Instant.parse("2016-10-09T03:00:00Z").getEpochSecond();
        Double aggregatedFeatureValue1 = 10d;
        aggrEvent = new AggrEvent(dataSource, featureType, featureName, aggregatedFeatureValue1, new HashMap<>(), bucketConfName, new HashMap<>(), contextId, creationEpochTimeSeconds, startTimeUnixSeconds, endTimeUnixSeconds, new ArrayList<>(), score, new ArrayList<>());
        aggregatedFeatureEventsMongoStore.storeEvent(aggrEvent);

        Instant from = Instant.parse("2016-10-09T00:00:00Z");
        Instant to = Instant.parse("2016-10-10T00:00:00Z");
        AccumulationParams params = new AccumulationParams(featureName, AccumulationParams.TimeFrame.DAILY, from, to);
        accumulator.run(params);

        String accumulatedCollectionName = translator.toAcmAggrCollection(featureName);
        Assert.assertTrue(mongoTemplate.getCollectionNames().contains(accumulatedCollectionName));
        List<AccumulatedAggregatedFeatureEvent> accumulatedEvents = mongoTemplate.findAll(AccumulatedAggregatedFeatureEvent.class, accumulatedCollectionName);
        Assert.assertEquals(accumulatedEvents.size(), 1);
        List<Double> expectedAccumulatedValues = new ArrayList<>();
        expectedAccumulatedValues.add(aggregatedFeatureValue);
        expectedAccumulatedValues.add(aggregatedFeatureValue1);
        AccumulatedAggregatedFeatureEvent accumulatedAggregatedFeatureEvent = accumulatedEvents.get(0);
        Assert.assertEquals(accumulatedAggregatedFeatureEvent.getAggregatedFeatureValues(), expectedAccumulatedValues);
        Assert.assertEquals(accumulatedAggregatedFeatureEvent.getContextId(), contextId);
        Assert.assertEquals(accumulatedAggregatedFeatureEvent.getStartTime(), from);
    }

}