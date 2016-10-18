package fortscale.accumulator.entityEvent;

import fortscale.accumulator.accumulator.AccumulationParams;
import fortscale.accumulator.entityEvent.config.EntityEventAccumulatorConfig;
import fortscale.accumulator.entityEvent.event.AccumulatedEntityEvent;
import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.domain.core.EntityEvent;
import fortscale.entity.event.EntityEventMongoStore;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by barak_schuster on 10/9/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration

@ActiveProfiles("test")
public class EntityEventAccumulatorTest {

    private static final int HOUR_IN_SECONDS = 3600;

    @Configuration
    @Import({
            NullStatsServiceConfig.class,
            EntityEventAccumulatorConfig.class,
            TestMongoConfig.class
    })
    @EnableSpringConfigured
    @EnableAnnotationConfiguration
    @Profile("test")
    public static class springConfig {
        @Autowired
        private StatsService statsService;

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

    private final static String ENTITY_EVENT_JSON = "{\"start_time_unix\":1435176000,\"contextId\":\"normalized_username_normalized_username_14060866\",\"baseScore\":0.0,\"entity_event_value\":0.0,\"event_type\":\"entity_event\",\"score\":50.0,\"unreduced_score\":90.0,\"entity_event_name\": \"normalized_username_daily\",\"context\":{\"normalized_username\":\"normalized_username_14060866\"},\"end_time_unix\":1435179599,\"creation_epochtime\":1444297230,\"entity_event_type\":\"normalized_username_hourly\",\"date_time_unix\":1435179599,\"aggregated_feature_events\":[{\"creation_date_time\":\"2015-10-08 09:33:53\",\"aggregated_feature_value\":55.0,\"event_type\":\"aggr_event\",\"data_source\":\"aggr_event.normalized_username_vpn_session_hourly.sum_of_scores_rate_vpn_session_hourly\",\"score\":null,\"aggregated_feature_type\":\"P\",\"data_sources\":[\"vpn_session\"],\"creation_epochtime\":1444296833,\"date_time_unix\":1435179599,\"start_time_unix\":1435176000,\"end_time\":\"2015-06-24 20:59:59\",\"bucket_conf_name\":\"normalized_username_vpn_session_hourly\",\"context\":{\"normalized_username\":\"normalized_username_14060866\"},\"start_time\":\"2015-06-24 20:00:00\",\"aggregated_feature_name\":\"sum_of_scores_rate_vpn_session_hourly\",\"end_time_unix\":1435179599,\"aggregated_feature_info\":{\"total\":0}}]}";
    private final static String ENTITY_EVENT_JSON_2 = "{\"start_time_unix\":1435176000,\"contextId\":\"normalized_username_normalized_username_14060866\",\"baseScore\":1.0,\"entity_event_value\":3.0,\"event_type\":\"entity_event\",\"score\":50.0,\"unreduced_score\":90.0,\"entity_event_name\": \"normalized_username_daily\",\"context\":{\"normalized_username\":\"normalized_username_14060866\"},\"end_time_unix\":1435179599,\"creation_epochtime\":1444297230,\"entity_event_type\":\"normalized_username_hourly\",\"date_time_unix\":1435179599,\"aggregated_feature_events\":[{\"creation_date_time\":\"2015-10-08 09:33:53\",\"aggregated_feature_value\":66.0,\"event_type\":\"aggr_event\",\"data_source\":\"aggr_event.normalized_username_vpn_session_hourly.sum_of_scores_rate_vpn_session_hourly\",\"score\":null,\"aggregated_feature_type\":\"P\",\"data_sources\":[\"vpn_session\"],\"creation_epochtime\":1444296833,\"date_time_unix\":1435179599,\"start_time_unix\":1435179600,\"end_time\":\"2015-06-24 20:59:59\",\"bucket_conf_name\":\"normalized_username_vpn_session_hourly\",\"context\":{\"normalized_username\":\"normalized_username_14060866\"},\"start_time\":\"2015-06-24 20:00:00\",\"aggregated_feature_name\":\"sum_of_scores_rate_vpn_session_hourly\",\"end_time_unix\":1435179599,\"aggregated_feature_info\":{\"total\":0}}]}";
    private final static String ENTITY_EVENT_JSON_3 = "{\"start_time_unix\":1435176000,\"contextId\":\"normalized_username_normalized_username_14060866\",\"baseScore\":2.0,\"entity_event_value\":4.0,\"event_type\":\"entity_event\",\"score\":50.0,\"unreduced_score\":90.0,\"entity_event_name\": \"normalized_username_daily\",\"context\":{\"normalized_username\":\"normalized_username_14060866\"},\"end_time_unix\":1435179599,\"creation_epochtime\":1444297230,\"entity_event_type\":\"normalized_username_hourly\",\"date_time_unix\":1435179599,\"aggregated_feature_events\":[{\"creation_date_time\":\"2015-10-08 09:33:53\",\"aggregated_feature_value\":77.0,\"event_type\":\"aggr_event\",\"data_source\":\"aggr_event.normalized_username_vpn_session_hourly.sum_of_scores_rate_vpn_session_hourly\",\"score\":null,\"aggregated_feature_type\":\"P\",\"data_sources\":[\"vpn_session\"],\"creation_epochtime\":1444296833,\"date_time_unix\":1435179599,\"start_time_unix\":1435183200,\"end_time\":\"2015-06-24 20:59:59\",\"bucket_conf_name\":\"normalized_username_vpn_session_hourly\",\"context\":{\"normalized_username\":\"normalized_username_14060866\"},\"start_time\":\"2015-06-24 20:00:00\",\"aggregated_feature_name\":\"sum_of_scores_rate_vpn_session_hourly\",\"end_time_unix\":1435179599,\"aggregated_feature_info\":{\"total\":0}}]}";

    @Test
    public void shouldAccumulateEvents() throws IOException, ParseException {
        JSONObject entityEventJsonObj = (JSONObject) JSONValue.parse(ENTITY_EVENT_JSON);
        EntityEvent entityEvent = EntityEvent.buildEntityEvent(entityEventJsonObj);
        entityEventMongoStore.save(entityEvent);
        JSONObject entityEventJsonObj2 = (JSONObject) JSONValue.parse(ENTITY_EVENT_JSON_2);
        EntityEvent entityEvent2 = EntityEvent.buildEntityEvent(entityEventJsonObj2);
        entityEventMongoStore.save(entityEvent2);
        JSONObject entityEventJsonObj3 = (JSONObject) JSONValue.parse(ENTITY_EVENT_JSON_3);
        EntityEvent entityEvent3 = EntityEvent.buildEntityEvent(entityEventJsonObj3);
        entityEventMongoStore.save(entityEvent3);

        String entity_event_type = entityEvent.getEntity_event_type();
        Instant firstStartTime = Instant.ofEpochSecond(entityEvent.getStart_time_unix());
        Instant lastStartTime = Instant.ofEpochSecond(entityEvent3.getAggregated_feature_events().get(0).getAsNumber(AggrEvent.EVENT_FIELD_START_TIME_UNIX).longValue());
        AccumulationParams accumulationParams = new AccumulationParams(entity_event_type, AccumulationParams.TimeFrame.DAILY, firstStartTime, lastStartTime);
        accumulator.run(accumulationParams);

        List<AccumulatedEntityEvent> accumulationResult = mongoTemplate.findAll(AccumulatedEntityEvent.class, "scored___entity_event__normalized_username_h_acm");
        Assert.assertEquals(1, accumulationResult.size());
        AccumulatedEntityEvent accumulatedEntityEvent = accumulationResult.get(0);
        Map<String, Double[]> aggregated_feature_events_values_map = accumulatedEntityEvent.getAggregated_feature_events_values_map();
        Double[] accumulatedEventHoursArray = aggregated_feature_events_values_map.get("normalized_username_vpn_session_hourly.sum_of_scores_rate_vpn_session_hourly");
        for (int i=0 ; i< 20; i++)
        {
            Assert.assertNull(accumulatedEventHoursArray[i]);
        }
        Assert.assertNotNull(accumulatedEventHoursArray[20]);
        Assert.assertNotNull(accumulatedEventHoursArray[21]);
        Assert.assertNotNull(accumulatedEventHoursArray[22]);

    }

}