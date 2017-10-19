package presidio.output.proccesor.services.alert;


import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.test.mongodb.FongoTestConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.activedirectory.AdeScoredActiveDirectoryRecord;
import presidio.ade.domain.record.enriched.activedirectory.EnrichedActiveDirectoryRecord;
import presidio.ade.domain.record.enriched.file.AdeScoredFileRecord;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.domain.store.enriched.EnrichedDataAdeToCollectionNameTranslator;
import presidio.ade.domain.store.scored.AdeScoredEnrichedRecordToCollectionNameTranslator;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.smart.SmartRecordsMetadata;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.events.ActiveDirectoryEnrichedEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.FileEnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;
import presidio.output.proccesor.spring.TestConfig;
import presidio.output.processor.services.alert.AlertEnumsSeverityService;
import presidio.output.processor.services.alert.AlertServiceImpl;
import presidio.output.processor.spring.AlertEnumsConfig;
import presidio.output.processor.spring.AlertServiceElasticConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

/**
 * Created by efratn on 24/07/2017.
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = {AlertServiceElasticConfig.class, MongodbTestConfig.class, AlertEnumsConfig.class, TestConfig.class, FongoTestConfig.class})
public class AlertServiceTest {

    @MockBean
    private AlertPersistencyService alertPersistencyService;

    @MockBean
    private SmartDataReader smartDataReader;

    @Autowired
    private AlertServiceImpl alertService;

    @Autowired
    private AlertEnumsSeverityService alertEnumsSeverityService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Instant startTime = Instant.parse("2017-06-06T10:00:00Z");
    private Instant endTime = Instant.parse("2017-06-06T11:00:00Z");
    private TimeRange timeRange = new TimeRange(startTime, endTime);
    private static String contextId = "testUser";
    private static String configurationName = "testConfName";
    private static String featureName = "featureName";

    public void setup(int smartListSize, int numOfSmartsBelowScoreThreshold, int scoreThreshold) {
        List<SmartRecord> smarts = new ArrayList<SmartRecord>();
        for (int i = 0; i <= numOfSmartsBelowScoreThreshold - 1; i++) {
            smarts.add(generateSingleSmart(scoreThreshold - 1));
        }

        for (int i = numOfSmartsBelowScoreThreshold + 1; i <= smartListSize; i++) {
            smarts.add(generateSingleSmart(scoreThreshold + 1));
        }

        List<ContextIdToNumOfItems> contextIdToNumOfItems = Collections.singletonList(new ContextIdToNumOfItems(contextId, smartListSize));
        Mockito.when(smartDataReader.aggregateContextIdToNumOfEvents(any(SmartRecordsMetadata.class), eq(50))).thenReturn(contextIdToNumOfItems);
        Set<String> contextIds = new HashSet<>();
        contextIds.add(contextId);
        Mockito.when(smartDataReader.readRecords(any(SmartRecordsMetadata.class), eq(contextIds), eq(0), eq(1000), eq(50))).thenReturn(smarts);
    }

    @Test
    public void generateAlertWithLowSmartScore() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL, 0);
        Alert alert = alertService.generateAlert(generateSingleSmart(30), userEntity, 50);
        assertTrue(alert == null);
    }


    @Test
    public void generateAlertTest() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL, 0);
        SmartRecord smart = generateSingleSmart(60);
        Alert alert = alertService.generateAlert(smart, userEntity, 50);
        assertEquals(alert.getUserId(), userEntity.getId());
        assertEquals(alert.getUserName(), userEntity.getUserName());
        assertTrue(alert.getScore() == smart.getScore());
    }

    @Test
    public void generateAlertWithOnlyStaticIndicatorsTest() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL, 0);
        SmartRecord smart = generateSingleSmart(60);
        AdeAggregationRecord adeAggregationRecord = new AdeAggregationRecord(Instant.now(), Instant.now(), "userAccountTypeChangedScoreUserIdActiveDirectoryHourly",
                +10d, "userAccountTypeChangedScoreUserIdActiveDirectoryHourly", Collections.singletonMap("userId", "userId"), AggregatedFeatureType.SCORE_AGGREGATION);
        EnrichedEvent event = new FileEnrichedEvent(Instant.now(), Instant.now(), "eventId", Schema.FILE.toString(), "userId", "username", "userDisplayName", "dataSource", "oppType", new ArrayList<>(),
                EventResult.FAILURE, "resultCode", new HashMap<>(), "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);
        String fileEnrichedEventCollectionName = new OutputToCollectionNameTranslator().toCollectionName(Schema.FILE);
        mongoTemplate.save(event, fileEnrichedEventCollectionName);
        smart.setAggregationRecords(Arrays.asList(adeAggregationRecord));

        Alert alert = alertService.generateAlert(smart, userEntity, 50);

        assertNull(alert);
    }

    @Test
    public void generateAlertWithNotOnlyStaticIndicatorsTest() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL, 0);
        SmartRecord smart = generateSingleSmart(60);
        Instant eventTime = Instant.now();
        Instant startDate = eventTime.minus(10, ChronoUnit.MINUTES);
        Instant endDate = eventTime.plus(10, ChronoUnit.MINUTES);

        //  highestStartInstant entities

        //  indicator
        AdeAggregationRecord notStaticAggregationRecord = new AdeAggregationRecord(startDate, endDate, "highestStartInstantScoreUserIdFileHourly",
                +10d, "startInstantHistogramUserIdFileDaily", Collections.singletonMap("userId", "userId"), AggregatedFeatureType.SCORE_AGGREGATION);

        // raw event
        EnrichedEvent fileEvent = new FileEnrichedEvent(Instant.now(), eventTime, "eventId", Schema.FILE.toString(), "userId", "username", "userDisplayName", "dataSource", "USER_ACCOUNT_TYPE_CHANGED", new ArrayList<>(),
                EventResult.FAILURE, "resultCode", new HashMap<>(), "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);
        mongoTemplate.save(fileEvent, new OutputToCollectionNameTranslator().toCollectionName(Schema.FILE));

        // enriched event
        EnrichedRecord fileEnrichedRecord = new EnrichedFileRecord(eventTime);
        fileEnrichedRecord.setEventId("eventId");
        mongoTemplate.save(fileEnrichedRecord, new EnrichedDataAdeToCollectionNameTranslator().toCollectionName("file"));

        // scored enriched event
        AdeScoredEnrichedRecord fileScoredEnrichedEvent = new AdeScoredFileRecord(eventTime, "startInstant.userId.file.score", "file", 10.0d, new ArrayList<FeatureScore>(), fileEnrichedRecord);
        mongoTemplate.save(fileScoredEnrichedEvent, new AdeScoredEnrichedRecordToCollectionNameTranslator().toCollectionName("scored_enriched.file.startInstant.userId.file.score"));


        //  userAccountTypeChanged entities

        // indicator
        AdeAggregationRecord staticAggregationRecord = new AdeAggregationRecord(startDate, endDate, "userAccountTypeChangedScoreUserIdActiveDirectoryHourly",
                +10d, "userAccountTypeChangedScoreUserIdActiveDirectoryHourly", Collections.singletonMap("userId", "userId"), AggregatedFeatureType.SCORE_AGGREGATION);

        // event
        EnrichedEvent activeDirectoryEvent = new ActiveDirectoryEnrichedEvent(Instant.now(), eventTime, "eventId", Schema.ACTIVE_DIRECTORY.toString(), "userId", "username", "userDisplayName", "dataSource", "USER_ACCOUNT_TYPE_CHANGED", new ArrayList<String>(), EventResult.SUCCESS, "resultCode", new HashMap<String, String>(), Boolean.FALSE, "objectId");
        mongoTemplate.save(fileEvent, new OutputToCollectionNameTranslator().toCollectionName(Schema.ACTIVE_DIRECTORY));

        // enriched event
        EnrichedRecord activeDirectoryEnrichedRecord = new EnrichedActiveDirectoryRecord(eventTime);
        activeDirectoryEnrichedRecord.setEventId("eventId");
        mongoTemplate.save(activeDirectoryEnrichedRecord, new EnrichedDataAdeToCollectionNameTranslator().toCollectionName("active_directory"));

        // scored enriched event
        AdeScoredEnrichedRecord activeDirectoryScoredEnrichedEvent = new AdeScoredActiveDirectoryRecord(eventTime, "startInstant.userId.file.score", "file", 10.0d, new ArrayList<FeatureScore>(), activeDirectoryEnrichedRecord);
        mongoTemplate.save(activeDirectoryScoredEnrichedEvent, new AdeScoredEnrichedRecordToCollectionNameTranslator().toCollectionName("scored_enriched.active_directory.userAccountTypeChanged.userId.activeDirectory.score"));

        smart.setAggregationRecords(Arrays.asList(staticAggregationRecord, notStaticAggregationRecord));

        Alert alert = alertService.generateAlert(smart, userEntity, 50);

        assertNotNull(alert);
        assertEquals(2, alert.getIndicatorsNum());
    }

    @Test
    public void severityTest() {
        assertEquals(alertEnumsSeverityService.severity(51), AlertEnums.AlertSeverity.LOW);
        assertEquals(alertEnumsSeverityService.severity(71), AlertEnums.AlertSeverity.MEDIUM);
        assertEquals(alertEnumsSeverityService.severity(86), AlertEnums.AlertSeverity.HIGH);
        assertEquals(alertEnumsSeverityService.severity(97), AlertEnums.AlertSeverity.CRITICAL);
    }

    private SmartRecord generateSingleSmart(int score) {
        List<FeatureScore> feature_scores = new ArrayList<>();
        List<AdeAggregationRecord> aggregated_feature_events = new ArrayList<>();
        TimeRange timeRange = new TimeRange(Instant.now(), Instant.now());
        return new SmartRecord(
                timeRange, contextId, featureName, FixedDurationStrategy.HOURLY,
                5.0, score, feature_scores, aggregated_feature_events, null);
    }
}