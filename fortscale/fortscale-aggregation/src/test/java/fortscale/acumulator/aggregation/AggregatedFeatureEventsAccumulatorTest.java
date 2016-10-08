package fortscale.acumulator.aggregation;

import com.github.fakemongo.Fongo;
import fortscale.acumulator.AccumulationParams;
import fortscale.acumulator.aggregation.config.AggregatedFeatureEventsAccumulatorConfig;
import fortscale.acumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
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
import java.util.Properties;

/**
 * Created by barak_schuster on 10/8/16.
 */
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
        @Autowired
        private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
        @Autowired
        private AccumulatedAggregatedFeatureEventStore accumulatedFeatureEventStore;

        private MongoDbFactory mongoDbFactory() {
            Fongo fongo = new Fongo(FORTSCALE_TEST_DB);

            return new SimpleMongoDbFactory(fongo.getMongo(), FORTSCALE_TEST_DB);
        }

        @Bean
        public MongoTemplate mongoTemplate() {
            return new MongoTemplate(mongoDbFactory());
        }

        @Bean
        public AggregatedFeatureEventsAccumulator aggregatedFeatureEventsAccumulator() {
            return new AggregatedFeatureEventsAccumulator(aggregatedFeatureEventsMongoStore, accumulatedFeatureEventStore, statsService);
        }

        @Bean
        public static TestPropertiesPlaceholderConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("streaming.event.field.type.aggr_event", "aggr_event");

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
    private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
    @Autowired
    private AggregatedFeatureEventsAccumulator accumulator;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void run() throws Exception {
        String featureName = "distinct_number_of_normalized_src_machine_kerberos_logins_daily";
        AggrEvent aggrEvent = new AggrEvent("dataSource", "featureType", featureName, 0d, new HashMap<>(), "bucketConfName", new HashMap<>(), "contextId", 0l, 0l, 0l, new ArrayList<>(), 0d, new ArrayList<>());
        aggregatedFeatureEventsMongoStore.storeEvent(aggrEvent);
        Instant from = Instant.ofEpochMilli(0);
        Instant to = Instant.ofEpochMilli(2);
        AccumulationParams params = new AccumulationParams(featureName, AccumulationParams.TimeFrame.DAILY, from, to);
        accumulator.run(params);
        System.out.println(mongoTemplate.getCollectionNames());
    }

}