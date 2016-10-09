package fortscale.accumulator.entityEvent;

import com.github.fakemongo.Fongo;
import fortscale.accumulator.entityEvent.config.EntityEventAccumulatorConfig;
import fortscale.entity.event.EntityEventMongoStore;
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

import java.util.Properties;

/**
 * Created by barak_schuster on 10/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EntityEventAccumulatorTest {
    @Configuration
    @Import({
            NullStatsServiceConfig.class,
            EntityEventAccumulatorConfig.class
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
            properties.put("streaming.event.field.type.entity_event","entity_event");

            properties.put("fortscale.entity.event.definitions.json.file.path", "classpath:config/asl/entity_events.json");
            properties.put("fortscale.entity.event.definitions.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/entity_events/overriding/entity_events*.json");
            properties.put("fortscale.entity.event.global.params.json.file.path", "classpath:config/asl/entity_events_global_params.json");
            properties.put("fortscale.entity.event.global.params.conf.json.overriding.files.path", "file:home/cloudera/config/asl/entity_events/overriding/global_params*.json");

            properties.put("fortscale.scored.entity.event.store.page.size",1);

            return new TestPropertiesPlaceholderConfigurer(properties);
        }

    }

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    EntityEventAccumulator accumulator;
    @Autowired
    EntityEventMongoStore entityEventMongoStore;

    @Test
    public void shouldAccumulateEvents()
    {
//        EntityEvent entityEvent = new EntityEvent();
//        entityEventMongoStore.save();
//        accumulator.run();

    }

}