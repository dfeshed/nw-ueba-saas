package fortscale.collection.jobs.accumulator.aggregation;

import com.github.fakemongo.Fongo;
import fortscale.accumulator.aggregation.AggregatedFeatureEventsAccumulatorManagerImpl;
import fortscale.accumulator.aggregation.event.AccumulatedAggregatedFeatureEvent;
import fortscale.accumulator.aggregation.store.AccumulatedAggregatedFeatureEventStore;
import fortscale.accumulator.aggregation.translator.AccumulatedAggregatedFeatureEventTranslator;
import fortscale.accumulator.manager.AccumulatorManagerParams;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsMongoStore;
import fortscale.collection.jobs.accumulator.FortscaleJobMockedTestSpringConfig;
import fortscale.collection.jobs.accumulator.aggregation.config.AggrEventAccumulatorJobConfig;
import fortscale.domain.core.FeatureScore;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Before;
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

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by barak_schuster on 10/18/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@ActiveProfiles("test")
public class AggrEventAccumulatorJobTest {

    private static final String FIRST_AGGR_EVENT_CREATION_TIME = "2016-10-01T00:00:00.000Z";
    private static final String FIRST_AGGR_EVENT_START_TIME = "2016-10-01T00:00:00.000Z";
    private static final Instant FIRST_AGGR_EVENT_START_TIME_INSTANT = Instant.parse(FIRST_AGGR_EVENT_START_TIME);
    private static final String LAST_AGGR_EVENT_START_TIME = "2016-10-10T13:00:00.000Z";
    private static final Instant LAST_AGGR_EVENT_START_TIME_INSTANT = Instant.parse(LAST_AGGR_EVENT_START_TIME);
    private static final Duration DURATION_BETWEEN_FIRST_AND_LAST_AGGR_EVENT = Duration.between(FIRST_AGGR_EVENT_START_TIME_INSTANT, LAST_AGGR_EVENT_START_TIME_INSTANT);
    private static final String FIRST_AGGR_EVENT_END_TIME = "2016-10-01T00:59:59.000Z";
    private static final String FEATURE_NAME = "distinct_number_of_normalized_src_machine_kerberos_logins_hourly";
    private static final String ACCUMULATION_FROM_DATE = "2016-10-01T00:00:00.000Z";
    private static final Instant ACCUMULATION_FROM_DATE_INSTANT = Instant.parse(ACCUMULATION_FROM_DATE);
    private static final String ACCUMULATION_TO_DATE = "2016-10-07T03:00:00.000Z";
    private static final Instant ACCUMULATION_TO_DATE_INSTANT = Instant.parse(ACCUMULATION_TO_DATE);
    private static final Duration DURATION_BETWEEN_ACCUMUALTION_FROM_AND_TO = Duration.between(ACCUMULATION_FROM_DATE_INSTANT, ACCUMULATION_TO_DATE_INSTANT);
    private static final int HOURS_PER_DAY = 24;

    @Configuration
    @Import({
            NullStatsServiceConfig.class,
            AggrEventAccumulatorJobConfig.class,
            FortscaleJobMockedTestSpringConfig.class
    })
    @Profile("test")
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
        public AggrEventAccumulatorJob aggrEventAccumulatorJob() {
            return new AggrEventAccumulatorJob();
        }

        @Bean
        public static TestPropertiesPlaceholderConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("streaming.event.field.type.aggr_event", "aggr_event");
            properties.put("streaming.event.field.type.entity_event", "entity_event");

            properties.put("fortscale.aggregation.bucket.conf.json.file.name", "classpath:config/asl/buckets.json");
            properties.put("fortscale.aggregation.bucket.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/buckets/overriding/*.json");
            properties.put("fortscale.aggregation.bucket.conf.json.additional.files.path", "file:home/cloudera/fortscale/config/asl/buckets/additional/*.json");

            properties.put("fortscale.aggregation.retention.strategy.conf.json.file.name", "classpath:config/asl/retention_strategies.json");
            properties.put("fortscale.aggregation.retention.strategy.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/retention_strategy/overriding/*.json");
            properties.put("fortscale.aggregation.retention.strategy.conf.json.additional.files.path", "file:home/cloudera/fortscale/config/asl/retention_strategy/additional/*.json");

            properties.put("fortscale.aggregation.feature.event.conf.json.file.name", "classpath:config/asl/aggregated_feature_events.json");
            properties.put("fortscale.aggregation.feature.event.conf.json.overriding.files.path", "file:home/cloudera/fortscale/config/asl/aggregation_events/overriding/*.json");
            properties.put("fortscale.aggregation.feature.event.conf.json.additional.files.path", "file:home/cloudera/fortscale/config/asl/aggregation_events/additional/*.json");

            properties.put("fortscale.accumulator.param.from.days.ago", 30);
            properties.put("fortscale.accumulator.param.from", "from");
            properties.put("fortscale.accumulator.param.to", "to");
            properties.put("fortscale.accumulator.param.featureNames", "featureNames");
            properties.put("fortscale.accumulator.param.featureNames.delimiter", ",");

            properties.put("fortscale.accumulator.aggr.feature.event.retention.daily","P3M");
            properties.put("fortscale.accumulator.aggr.feature.event.retention.hourly","P1M");
            properties.put("fortscale.accumulator.aggr.feature.event.from.period.ago.daily","P3M");
            properties.put("fortscale.accumulator.aggr.feature.event.from.period.ago.hourly","P1M");

            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AggregatedFeatureEventsMongoStore aggregatedFeatureEventsMongoStore;
    @Autowired
    private AccumulatedAggregatedFeatureEventStore accumulatedAggregatedFeatureEventStore;
    @Autowired
    private AccumulatedAggregatedFeatureEventTranslator accumulatedAggregatedFeatureEventTranslator;
    @Autowired
    private AggregatedFeatureEventsAccumulatorManagerImpl aggregatedFeatureEventsAccumulatorManager;

    @Autowired
    private AggrEventAccumulatorJob job;

    @Before
    public void setup() {

        fillDb();
    }

    private void fillDb() {
        String featureName = FEATURE_NAME;
        long creationEpochTimeSeconds = Instant.parse(FIRST_AGGR_EVENT_CREATION_TIME).getEpochSecond();

        long startTimeUnixSeconds = FIRST_AGGR_EVENT_START_TIME_INSTANT.getEpochSecond();
        long endTimeUnixSeconds = Instant.parse(FIRST_AGGR_EVENT_END_TIME).getEpochSecond();

        long amountOfHoursToGenerate = DURATION_BETWEEN_FIRST_AND_LAST_AGGR_EVENT.toHours();
        // Store generated hourly aggr_events in mongo
        for (int i = 0; i < amountOfHoursToGenerate; i++) {
            AggrEvent aggrEvent = getGenerateAggrEvent(featureName, creationEpochTimeSeconds, startTimeUnixSeconds, endTimeUnixSeconds);
            aggregatedFeatureEventsMongoStore.storeEvent(aggrEvent);
            creationEpochTimeSeconds = Instant.ofEpochSecond(creationEpochTimeSeconds).plus(1, ChronoUnit.HOURS).getEpochSecond();
            startTimeUnixSeconds = Instant.ofEpochSecond(startTimeUnixSeconds).plus(1, ChronoUnit.HOURS).getEpochSecond();
            endTimeUnixSeconds = Instant.ofEpochSecond(endTimeUnixSeconds).plus(1, ChronoUnit.HOURS).getEpochSecond();
        }
    }

    private AggrEvent getGenerateAggrEvent(String featureName, long creationEpochTimeSeconds, long startTimeUnixSeconds, long endTimeUnixSeconds) {
        double score = 10d;
        Double aggregatedFeatureValue = 20d;
        String dataSource = "dataSource";
        String featureType = "featureType";
        String bucketConfName = "bucketConfName";
        String contextId = "contextId";
        HashMap<String, Object> aggregatedFeatureInfo = new HashMap<>();
        HashMap<String, String> context = new HashMap<>();
        ArrayList<String> dataSources = new ArrayList<>();
        ArrayList<FeatureScore> featureScores = new ArrayList<>();
        return new AggrEvent(dataSource, featureType, featureName, aggregatedFeatureValue, aggregatedFeatureInfo, bucketConfName, context, contextId, creationEpochTimeSeconds, startTimeUnixSeconds, endTimeUnixSeconds, dataSources, score, featureScores);
    }

    @Test
    public void shouldAccumulateNothingForNoneExistingFeature() {
        AccumulatorManagerParams accumulatorManagerParams = new AccumulatorManagerParams();
        Set<String> features = new HashSet<>();
        String nonExistingFeatureName = "nonExistingFeature";
        features.add(nonExistingFeatureName);
        accumulatorManagerParams.setFeatures(features);
        accumulatorManagerParams.setFrom(Instant.parse(ACCUMULATION_FROM_DATE));
        accumulatorManagerParams.setTo(Instant.parse(ACCUMULATION_TO_DATE));
        job.setAccumulatorManagerParams(accumulatorManagerParams);
        job.runAccumulation();
        mongoTemplate.getCollectionNames().forEach(collectionName ->
                Assert.assertTrue(!collectionName.contains(nonExistingFeatureName)));
    }

    @Test
    public void shouldAccumulatedSeveralHourlyAggrEvents() throws Exception {
        // accumulate phase 1 of events
        AccumulatorManagerParams accumulatorManagerParams = new AccumulatorManagerParams();
        accumulatorManagerParams.setFrom(Instant.parse(ACCUMULATION_FROM_DATE));
        accumulatorManagerParams.setTo(Instant.parse(ACCUMULATION_TO_DATE));
        Set<String> features = new HashSet<>();
        features.add(FEATURE_NAME);

        accumulatorManagerParams.setFeatures(features);
        job.setAccumulatorManagerParams(accumulatorManagerParams);
        job.runAccumulation();
        String acmCollectionName =
                accumulatedAggregatedFeatureEventTranslator.toAcmCollectionName(FEATURE_NAME);
        List<AccumulatedAggregatedFeatureEvent> accumulatedEvents =
                mongoTemplate.findAll(AccumulatedAggregatedFeatureEvent.class, acmCollectionName);
        long expectedAmountOfAccumulatedEvents = DURATION_BETWEEN_ACCUMUALTION_FROM_AND_TO.toDays();
        validateAccumulatedEvents(accumulatedEvents, expectedAmountOfAccumulatedEvents);

        // accumulate phase 2 of events
        accumulatorManagerParams.setFrom(ACCUMULATION_TO_DATE_INSTANT);
        accumulatorManagerParams.setTo(null);
        job.runAccumulation();
        accumulatedEvents =
                mongoTemplate.findAll(AccumulatedAggregatedFeatureEvent.class, acmCollectionName);
        expectedAmountOfAccumulatedEvents =
                Duration.between(ACCUMULATION_FROM_DATE_INSTANT, LAST_AGGR_EVENT_START_TIME_INSTANT).toDays();
        validateAccumulatedEvents(accumulatedEvents, expectedAmountOfAccumulatedEvents);
    }

    private void validateAccumulatedEvents(List<AccumulatedAggregatedFeatureEvent> accumulatedEvents, long expectedAmountOfAccumulatedEvents) {
        Assert.assertEquals(expectedAmountOfAccumulatedEvents, accumulatedEvents.size());
        accumulatedEvents.stream().forEach(accumulatedEvent -> {
            int amountOfAggrFeatures = accumulatedEvent.getAggregatedFeatureValues().size();
            Assert.assertEquals(HOURS_PER_DAY, amountOfAggrFeatures);
        });
    }

}