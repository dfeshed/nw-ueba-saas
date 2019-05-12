package presidio.output.manager.service;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import fortscale.utils.data.Pair;
import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.FileEnrichedEvent;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;
import presidio.output.manager.OutputManagerService;
import presidio.output.manager.spring.OutputManagerTestConfiguration;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static java.time.Instant.now;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {OutputManagerTestConfiguration.class, MongodbTestConfig.class, ElasticsearchTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OutputManagerServiceTest {
    public static final String ENTITY_ID_TEST_ENTITY = "entityId#testEntity";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private OutputManagerService outputManagerService;



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

    @Test
    public void testCleanDocuments() {
        try {
            String outputFileEnrichedEventCollectionName = new OutputToCollectionNameTranslator().toCollectionName(Schema.FILE);
            Assert.assertEquals(2, mongoTemplate.findAll(EnrichedEvent.class, outputFileEnrichedEventCollectionName).size());
            outputManagerService.cleanDocuments(now().plus(Duration.ofDays(1)), Schema.FILE);
            // 2 alerts and 1 enriched event should have been deleted by retention
            Assert.assertEquals(1, mongoTemplate.findAll(EnrichedEvent.class, outputFileEnrichedEventCollectionName).size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
    }
    }

    @Test
    public void testCleanDocumentsForNonExistingSchema() {
        try {
            String outputFileEnrichedEventCollectionName = new OutputToCollectionNameTranslator().toCollectionName(Schema.PRINT);
            Assert.assertEquals(0, mongoTemplate.findAll(EnrichedEvent.class, outputFileEnrichedEventCollectionName).size());
            outputManagerService.cleanDocuments(now().plus(Duration.ofDays(1)), Schema.PRINT);
            Assert.assertEquals(0, mongoTemplate.findAll(EnrichedEvent.class, outputFileEnrichedEventCollectionName).size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
