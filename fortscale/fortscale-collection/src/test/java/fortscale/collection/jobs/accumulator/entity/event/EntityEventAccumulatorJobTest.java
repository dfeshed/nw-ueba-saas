package fortscale.collection.jobs.accumulator.entity.event;

import com.github.fakemongo.Fongo;
import fortscale.accumulator.entityEvent.EntityEventAccumulatorManagerImpl;
import fortscale.accumulator.manager.AccumulatorManagerParams;
import fortscale.collection.jobs.accumulator.FortscaleJobMockedTestSpringConfig;
import fortscale.collection.jobs.accumulator.entity.event.config.EntityEventAccumulatorJobConfig;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.util.Properties;

/**
 * Created by barak_schuster on 10/18/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@ActiveProfiles("test")
public class EntityEventAccumulatorJobTest {

    private static final String ACCUMULATION_FROM_DATE = "2016-10-01T00:00:00.000Z";
    private static final Instant ACCUMULATION_FROM_DATE_INSTANT = Instant.parse(ACCUMULATION_FROM_DATE);
    private static final String ACCUMULATION_TO_DATE = "2016-10-07T03:00:00.000Z";
    private static final Instant ACCUMULATION_TO_DATE_INSTANT = Instant.parse(ACCUMULATION_TO_DATE);

    @Configuration
    @Import({
            NullStatsServiceConfig.class,
            EntityEventAccumulatorJobConfig.class,
            FortscaleJobMockedTestSpringConfig.class
    })
    @Profile("test")
    public static class springConfig {
        private static final String FORTSCALE_TEST_DB = "fortscaleTestDb";

        private MongoDbFactory mongoDbFactory() {
            Fongo fongo = new Fongo(FORTSCALE_TEST_DB);

            return new SimpleMongoDbFactory(fongo.getMongo(), FORTSCALE_TEST_DB);
        }

        @Bean
        public EntityEventAccumulatorJob entityEventAccumulatorJob() {
            return new EntityEventAccumulatorJob();
        }

        @Bean
        public MongoTemplate mongoTemplate() {
            return new MongoTemplate(mongoDbFactory());
        }

        @Bean
        public static TestPropertiesPlaceholderConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("streaming.event.field.type.aggr_event", "aggr_event");
            properties.put("streaming.event.field.type.entity_event", "entity_event");
            properties.put("streaming.event.field.type.entity_event", "entity_event");

            properties.put("fortscale.entity.event.definitions.json.file.path", "classpath:config/asl/entity_events.json");
            properties.put("fortscale.entity.event.definitions.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/entity_events/overriding/entity_events*.json");
            properties.put("fortscale.entity.event.global.params.json.file.path", "classpath:config/asl/entity_events_global_params.json");
            properties.put("fortscale.entity.event.global.params.conf.json.overriding.files.path", "file:home/cloudera/config/asl/entity_events/overriding/global_params*.json");

            properties.put("fortscale.scored.entity.event.store.page.size", 1);

            properties.put("fortscale.accumulator.param.from.days.ago", 30);
            properties.put("fortscale.accumulator.param.from", "from");
            properties.put("fortscale.accumulator.param.to", "to");
            properties.put("fortscale.accumulator.param.featureNames", "featureNames");
            properties.put("fortscale.accumulator.param.featureNames.delimiter", ",");

            properties.put("fortscale.accumulator.entity.event.retention.daily","P3M");
            properties.put("fortscale.accumulator.entity.event.retention.hourly","P1M");

            return new TestPropertiesPlaceholderConfigurer(properties);
        }

    }

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    EntityEventAccumulatorManagerImpl entityEventAccumulatorManager;

    @Autowired
    private EntityEventAccumulatorJob job;

    @Test
    public void contextText() {
        AccumulatorManagerParams accumulatorManagerParams = new AccumulatorManagerParams();
        accumulatorManagerParams.setFrom(ACCUMULATION_FROM_DATE_INSTANT);
        accumulatorManagerParams.setTo(ACCUMULATION_TO_DATE_INSTANT);
        job.setAccumulatorManagerParams(accumulatorManagerParams);
        job.runAccumulation();
    }

}