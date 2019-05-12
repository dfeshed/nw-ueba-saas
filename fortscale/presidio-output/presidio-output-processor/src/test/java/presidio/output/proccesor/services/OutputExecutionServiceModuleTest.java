package presidio.output.proccesor.services;

import com.google.common.collect.Lists;
import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import fortscale.utils.data.Pair;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.SmartAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.domain.store.enriched.EnrichedDataAdeToCollectionNameTranslator;
import presidio.ade.domain.store.smart.SmartDataToCollectionNameTranslator;
import presidio.monitoring.elastic.services.PresidioMetricPersistencyService;
import presidio.monitoring.services.export.MetricsExporter;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntitySeverity;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.FileEnrichedEvent;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;
import presidio.output.proccesor.spring.OutputProcessorTestConfiguration;
import presidio.output.proccesor.spring.TestConfig;
import presidio.output.processor.services.OutputExecutionServiceImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static java.time.Instant.now;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {OutputProcessorTestConfiguration.class, MongodbTestConfig.class, TestConfig.class, ElasticsearchTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OutputExecutionServiceModuleTest {
    public static final String ENTITY_ID_TEST_ENTITY = "entityId#testEntity";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private OutputExecutionServiceImpl outputExecutionService;

    @Autowired
    EntityPersistencyService entityPersistencyService;

    @Autowired
    AlertPersistencyService alertPersistencyService;

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;

    @Autowired
    private PresidioMetricPersistencyService metricPersistencyService;

    @Autowired
    private MetricsExporter metricsExporter;

    private static String OUTPUT_DAILY_METRIC_NAME_PREFIX = "output-core.";

    @Before
    public void setup() {
        String smartUserIdHourlyCollectionName = SmartDataToCollectionNameTranslator.SMART_COLLECTION_PREFIX + "userId_hourly";
        String outputFileEnrichedEventCollectionName = new OutputToCollectionNameTranslator().toCollectionName(Schema.FILE);
        String adeFileEnrichedEventCollectionName = new EnrichedDataAdeToCollectionNameTranslator().toCollectionName(Schema.FILE.getName().toLowerCase());

        mongoTemplate.dropCollection(smartUserIdHourlyCollectionName);
        mongoTemplate.dropCollection(outputFileEnrichedEventCollectionName);
        mongoTemplate.dropCollection(adeFileEnrichedEventCollectionName);

        List<SmartRecord> smartRecords = new ArrayList<>();

        TimeRange timeRange = new TimeRange(Instant.now().minusSeconds(1800), Instant.now());
        TimeRange timeRange2 = new TimeRange(Instant.now().minus(Duration.ofDays(100)), Instant.now().minus(Duration.ofDays(95)));
        List<Pair<String, Double>> usersToScoreList = new ArrayList<>();
        usersToScoreList.add(new Pair<>("userTest1", 90.0));
        usersToScoreList.add(new Pair<>("userTest2", 50.0));
        usersToScoreList.add(new Pair<>("userTest3", 70.0));
        usersToScoreList.add(new Pair<>("userTest4", 30.0));
        usersToScoreList.add(new Pair<>("userTest5", 55.0));
        usersToScoreList.add(new Pair<>("userTest6", 65.0));
        usersToScoreList.add(new Pair<>("userTest7", 45.0));
        usersToScoreList.add(new Pair<>("userTest8", 85.0));

        AdeAggregationRecord aggregationRecord = new AdeAggregationRecord(Instant.now(), Instant.now(), "highestStartInstantScoreUserIdFileHourly",
                10d, "userAccountTypeChangedScoreUserIdActiveDirectoryHourly", Collections.singletonMap("userId", ENTITY_ID_TEST_ENTITY), AggregatedFeatureType.SCORE_AGGREGATION);
        FileEnrichedEvent event = new FileEnrichedEvent(Instant.now(), Instant.now(), "eventId", Schema.FILE.toString(),
                ENTITY_ID_TEST_ENTITY, "username", "userDisplayName", "dataSource", "oppType", new ArrayList<String>(),
                EventResult.FAILURE, "resultCode", new HashMap<>(), "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);
        FileEnrichedEvent event2 = new FileEnrichedEvent(Instant.now().minus(Duration.ofDays(5)), Instant.now().minus(Duration.ofDays(3)), "eventId", Schema.FILE.toString(),
                ENTITY_ID_TEST_ENTITY, "username", "userDisplayName", "dataSource", "oppType", new ArrayList<String>(),
                EventResult.FAILURE, "resultCode", new HashMap<>(), "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);


        EnrichedFileRecord enrichedFileRecord = new EnrichedFileRecord(event.getEventDate());
        enrichedFileRecord.setUserId("userId");
        enrichedFileRecord.setEventId(event.getEventId());
        enrichedFileRecord.setOperationType(event.getOperationType());
        enrichedFileRecord.setOperationTypeCategories(event.getOperationTypeCategories());
        enrichedFileRecord.setResult(event.getResult());


        Map<String, String> context = new HashMap<>();
        context.put("userId", ENTITY_ID_TEST_ENTITY);
        SmartAggregationRecord smartAggregationRecord = new SmartAggregationRecord(aggregationRecord);
        smartAggregationRecord.setContribution(0.3);
        for (Pair<String, Double> usersToScore : usersToScoreList) {
            SmartRecord smartRecord = new SmartRecord(timeRange, usersToScore.getKey(), "featureName", FixedDurationStrategy.HOURLY,
                    90, usersToScore.getValue(), Collections.emptyList(), Collections.singletonList(smartAggregationRecord), context);
            smartRecords.add(smartRecord);
        }

        smartRecords.add(new SmartRecord(timeRange2, "userTest9", "featureName", FixedDurationStrategy.HOURLY,
                90, 75, Collections.emptyList(), Collections.singletonList(smartAggregationRecord), context));
        smartRecords.add(new SmartRecord(timeRange2, "userTest10", "featureName", FixedDurationStrategy.HOURLY,
                90, 85, Collections.emptyList(), Collections.singletonList(smartAggregationRecord), context));

        mongoTemplate.insert(smartRecords, smartUserIdHourlyCollectionName);
        mongoTemplate.insert(event, outputFileEnrichedEventCollectionName);
        mongoTemplate.insert(event2, outputFileEnrichedEventCollectionName);
        mongoTemplate.insert(enrichedFileRecord, adeFileEnrichedEventCollectionName);
    }

    @After
    public void deleteTestData() {
        esTemplate.deleteIndex(Alert.class);
        esTemplate.createIndex(Alert.class);
        esTemplate.putMapping(Alert.class);
        esTemplate.refresh(Alert.class);

        esTemplate.deleteIndex(Indicator.class);
        esTemplate.createIndex(Indicator.class);
        esTemplate.putMapping(Indicator.class);
        esTemplate.refresh(Indicator.class);

        esTemplate.deleteIndex(IndicatorEvent.class);
        esTemplate.createIndex(IndicatorEvent.class);
        esTemplate.putMapping(IndicatorEvent.class);
        esTemplate.refresh(IndicatorEvent.class);

        esTemplate.deleteIndex(Entity.class);
        esTemplate.createIndex(Entity.class);
        esTemplate.putMapping(Entity.class);
        esTemplate.refresh(Entity.class);
    }

    @Test
    public void createAlertForNewEntity() {
        try {
            outputExecutionService.run(now().minus(Duration.ofDays(2)), now().plus(Duration.ofDays(2)), "userId_hourly");

            Assert.assertEquals(8, Lists.newArrayList(alertPersistencyService.findAll()).size());
            Assert.assertEquals(1, Lists.newArrayList(entityPersistencyService.findAll()).size());
            Page<Entity> entities = entityPersistencyService.findByEntityId(ENTITY_ID_TEST_ENTITY, new PageRequest(0, 9999));
            Assert.assertEquals(1, entities.getNumberOfElements());
            Entity entity = entities.iterator().next();
            Assert.assertEquals(8, entity.getAlertsCount());
            Assert.assertEquals(55, new Double(entity.getScore()).intValue());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void createAlertForExistingEntity() {
        Entity entity = new Entity(ENTITY_ID_TEST_ENTITY, "userName", 95d, Arrays.asList("existingClassification"), Arrays.asList("existingIndicator"), null, EntitySeverity.CRITICAL, 8, "userId");
        entityPersistencyService.save(entity);
        try {
            outputExecutionService.run(now().minus(Duration.ofDays(2)), now().plus(Duration.ofDays(2)), "userId_hourly");

            Assert.assertEquals(8, Lists.newArrayList(alertPersistencyService.findAll()).size());
            Assert.assertEquals(1, Lists.newArrayList(entityPersistencyService.findAll()).size());
            Page<Entity> entities = entityPersistencyService.findByEntityId(ENTITY_ID_TEST_ENTITY, new PageRequest(0, 9999));
            Assert.assertEquals(1, entities.getNumberOfElements());
            Entity entity1 = entities.iterator().next();
            Assert.assertEquals(16, entity1.getAlertsCount());
            Assert.assertEquals(1, entity1.getAlertClassifications().size());
            Assert.assertEquals(1, entity1.getIndicators().size());
            Assert.assertEquals(150, new Double(entity1.getScore()).intValue());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testCleanAlerts() {

        try {
            outputExecutionService.run(now().minus(Duration.ofDays(2)), now().plus(Duration.ofDays(2)), "userId_hourly");
            Assert.assertEquals(8, Lists.newArrayList(alertPersistencyService.findAll()).size());
            Assert.assertEquals(1, Lists.newArrayList(entityPersistencyService.findAll()).size());
            Page<Entity> entities = entityPersistencyService.findByEntityId(ENTITY_ID_TEST_ENTITY, new PageRequest(0, 9999));
            Assert.assertEquals(1, entities.getNumberOfElements());
            Entity entity = entities.iterator().next();
            Assert.assertEquals(8, entity.getAlertsCount());
            Assert.assertEquals(55, new Double(entity.getScore()).intValue());
            outputExecutionService.cleanAlerts(now().minus(Duration.ofDays(2)), now().plus(Duration.ofDays(2)), "userId");
            // test alerts cleanup
            Assert.assertEquals(0, Lists.newArrayList(alertPersistencyService.findAll()).size());
            entities = entityPersistencyService.findByEntityId(ENTITY_ID_TEST_ENTITY, new PageRequest(0, 9999));
            entity = entities.iterator().next();
            // test entity score re-calculation
            Assert.assertEquals(0, new Double(entity.getScore()).intValue());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
