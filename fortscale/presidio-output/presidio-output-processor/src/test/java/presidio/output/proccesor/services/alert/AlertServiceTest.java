package presidio.output.proccesor.services.alert;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.elasticsearch.config.ElasticsearchTestConfig;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.aggregated.*;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.record.enriched.activedirectory.AdeScoredActiveDirectoryRecord;
import presidio.ade.domain.record.enriched.activedirectory.EnrichedActiveDirectoryRecord;
import presidio.ade.domain.record.enriched.authentication.AdeScoredAuthenticationRecord;
import presidio.ade.domain.record.enriched.authentication.EnrichedAuthenticationRecord;
import presidio.ade.domain.record.enriched.file.AdeScoredFileRecord;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.domain.store.ScoredDataReader;
import presidio.ade.domain.store.enriched.EnrichedDataAdeToCollectionNameTranslator;
import presidio.ade.domain.store.scored.AdeScoredEnrichedRecordToCollectionNameTranslator;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.smart.SmartRecordsMetadata;
import presidio.monitoring.services.MetricCollectingService;
import presidio.monitoring.services.export.MetricsExporter;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.Bucket;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.events.ActiveDirectoryEnrichedEvent;
import presidio.output.domain.records.events.AuthenticationEnrichedEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.FileEnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;
import presidio.output.proccesor.spring.TestConfig;
import presidio.output.processor.services.alert.AlertServiceImpl;
import presidio.output.processor.spring.AlertServiceElasticConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

/**
 * Created by efratn on 24/07/2017.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AlertServiceElasticConfig.class, MongodbTestConfig.class, TestConfig.class, ElasticsearchTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AlertServiceTest {

    @MockBean
    private SmartDataReader smartDataReader;

    @MockBean
    private MetricCollectingService metricCollectingService;
    @MockBean
    private MetricsExporter metricsExporter;
    @MockBean
    private ScoredDataReader<ScoredFeatureAggregationRecord> scoredFeatureAggregationDataReader;

    @Autowired
    private AlertServiceImpl alertService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static String contextId = "testUser";
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

        Instant eventTime = Instant.now();
        Instant startDate = eventTime.minus(10, ChronoUnit.MINUTES);
        Instant endDate = eventTime.plus(10, ChronoUnit.MINUTES);

        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL, 0);
        SmartRecord smart = generateSingleSmart(60);
        AdeAggregationRecord aggregationRecord = new AdeAggregationRecord(startDate, endDate, "userAccountTypeChangedScoreUserIdActiveDirectoryHourly",
                +10d, "userAccountTypeChangedScoreUserIdActiveDirectoryHourly", Collections.singletonMap("userId", "userId"), AggregatedFeatureType.SCORE_AGGREGATION);

        EnrichedEvent activeDirectoryEvent = new ActiveDirectoryEnrichedEvent(Instant.now(), eventTime, "eventId" + "_only_static", Schema.ACTIVE_DIRECTORY.toString(), "userId", "username", "userDisplayName", "dataSource", "USER_ACCOUNT_TYPE_CHANGED", new ArrayList<String>(), EventResult.SUCCESS, "resultCode", new HashMap<String, String>(), "objectId");
        mongoTemplate.save(activeDirectoryEvent, new OutputToCollectionNameTranslator().toCollectionName(Schema.ACTIVE_DIRECTORY));

        EnrichedRecord activeDirectoryEnrichedRecord = new EnrichedActiveDirectoryRecord(activeDirectoryEvent.getEventDate());
        activeDirectoryEnrichedRecord.setEventId(activeDirectoryEvent.getEventId());
        mongoTemplate.save(activeDirectoryEnrichedRecord, new EnrichedDataAdeToCollectionNameTranslator().toCollectionName("active_directory"));

        // scored enriched event
        AdeScoredEnrichedRecord activeDirectoryScoredEnrichedEvent = new AdeScoredActiveDirectoryRecord(activeDirectoryEvent.getEventDate(), "startInstant.userId.file.score", "file", 10.0d, new ArrayList<FeatureScore>(), activeDirectoryEnrichedRecord);
        mongoTemplate.save(activeDirectoryScoredEnrichedEvent, new AdeScoredEnrichedRecordToCollectionNameTranslator().toCollectionName("scored_enriched.active_directory.userAccountTypeChanged.userId.activeDirectory.score"));

        SmartAggregationRecord smartAggregationRecord = new SmartAggregationRecord(aggregationRecord);
        smartAggregationRecord.setContribution(0.3);
        smart.setSmartAggregationRecords(Collections.singletonList(smartAggregationRecord));
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
        generateActiveDirectoryEvents(2, eventTime, "scored_enriched.active_directory.userAccountTypeChanged.userId.activeDirectory.score", "USER_ACCOUNT_TYPE_CHANGED", Arrays.asList("USER_MANAGEMENT", "SECURITY_SENSITIVE_OPERATION"));
        generateActiveDirectoryEvents(1, eventTime, "scored_enriched.active_directory.userAccountTypeChanged.userId.activeDirectory.score", "USER_ACCOUNT_LOCKED", Arrays.asList("USER_MANAGEMENT", "SECURITY_SENSITIVE_OPERATION"));

        SmartAggregationRecord smartAggregationRecord = new SmartAggregationRecord(staticAggregationRecord);
        SmartAggregationRecord smartAggregationRecord2 = new SmartAggregationRecord(notStaticAggregationRecord);
        smartAggregationRecord.setContribution(0.3);
        smartAggregationRecord2.setContribution(0.3);

        smart.setSmartAggregationRecords(Arrays.asList(
                smartAggregationRecord,
                smartAggregationRecord2
        ));

        Alert alert = alertService.generateAlert(smart, userEntity, 50);

        assertNotNull(alert);
        assertEquals(2, alert.getIndicatorsNum());
    }

    @Test
    public void testSingleIndicatorsWithMultipleEventsTest() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL, 0);
        SmartRecord smart = generateSingleSmart(60);
        Instant eventTime = Instant.now();
        Instant startDate = eventTime.minus(10, ChronoUnit.MINUTES);
        Instant endDate = eventTime.plus(10, ChronoUnit.MINUTES);


        // indicator
        AdeAggregationRecord aggregationRecord = new AdeAggregationRecord(startDate, endDate, "sumOfHighestSrcMachineNameRegexClusterScoresUserIdAuthenticationHourly",
                +10d, "srcMachineNameRegexClusterHistogramUserIdAuthenticationDaily", Collections.singletonMap("userId", "userId"), AggregatedFeatureType.SCORE_AGGREGATION);
        SmartAggregationRecord smartAggregationRecord = new SmartAggregationRecord(aggregationRecord);
        smartAggregationRecord.setContribution(0.3);
        smart.setSmartAggregationRecords(Collections.singletonList(smartAggregationRecord));

        generateAuthenticationEvents(2, eventTime, "scored_enriched.authentication.srcMachine.userId.authentication.score");

        Alert alert = alertService.generateAlert(smart, userEntity, 50);

        assertNotNull(alert);
        assertEquals(1, alert.getIndicatorsNum());
        assertTrue(alert.getIndicators().get(0).getStartDate().toInstant().isAfter(startDate));
        assertTrue(alert.getIndicators().get(0).getStartDate().toInstant().isBefore(endDate));
    }


    @Test
    public void testIndicatorWithMultipleAnomalyFilters() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL, 0);
        SmartRecord smart = generateSingleSmart(60);
        Instant eventTime = Instant.now();
        Instant startDate = eventTime.minus(10, ChronoUnit.MINUTES);
        Instant endDate = eventTime.plus(10, ChronoUnit.MINUTES);


        // indicator
        AdeAggregationRecord aggregationRecord = new AdeAggregationRecord(startDate, endDate, "sumOfHighestOperationTypeScoresUserIdGroupMembershipSecuritySensitiveActiveDirectoryHourly",
                +10d, "srcMachineNameRegexClusterHistogramUserIdAuthenticationDaily", Collections.singletonMap("userId", "userId"), AggregatedFeatureType.SCORE_AGGREGATION);
        SmartAggregationRecord smartAggregationRecord = new SmartAggregationRecord(aggregationRecord);
        smartAggregationRecord.setContribution(0.3);
        smart.setSmartAggregationRecords(Collections.singletonList(smartAggregationRecord));

        List<String> categories = Arrays.asList("GROUP_MEMBERSHIP_OPERATION", "SECURITY_SENSITIVE_OPERATION");
        generateActiveDirectoryEvents(2, eventTime, "scored_enriched.active_directory.operationType.userIdGroupMembershipSecuritySensitive.activeDirectory.score", "PASSWORD_CHANGED", categories);

        Alert alert = alertService.generateAlert(smart, userEntity, 50);

        assertNotNull(alert);
        assertEquals(1, alert.getIndicatorsNum());
        assertEquals(2, alert.getIndicators().get(0).getEventsNum());
    }

    @Test
    public void testAlertWithFailureStatusEvent() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL, 0);
        SmartRecord smart = generateSingleSmart(60);
        Instant startDate = Instant.parse("2017-10-20T15:00:00.000Z");
        Instant endDate = Instant.parse("2017-10-20T16:00:00.000Z");

        // indicator
        AdeAggregationRecord aggregationRecord = new ScoredFeatureAggregationRecord(90.0, new ArrayList<>(), startDate, endDate, "numberOfFailedFilePermissionChangesUserIdFileHourly",
                +10d, "numberOfFailedFilePermissionChangesUserIdFileHourly", Collections.singletonMap("userId", "userId"), AggregatedFeatureType.FEATURE_AGGREGATION);

        // raw event
        EnrichedEvent fileEvent1 = new FileEnrichedEvent(Instant.now(), startDate.plus(5, ChronoUnit.MINUTES), "eventId1", Schema.FILE.toString(), "userId", "username", "userDisplayName", "dataSource", "FOLDER_OWNERSHIP_CHANGED", Arrays.asList("FILE_PERMISSION_CHANGE"),
                EventResult.FAILURE, "FAILURE", new HashMap<>(), "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);

        EnrichedEvent fileEvent2 = new FileEnrichedEvent(Instant.now(), startDate.plus(10, ChronoUnit.MINUTES), "eventId2", Schema.FILE.toString(), "userId", "username", "userDisplayName", "dataSource", "FOLDER_OWNERSHIP_CHANGED", Arrays.asList("FILE_PERMISSION_CHANGE"),
                EventResult.SUCCESS, "SUCCESS", new HashMap<>(), "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);
        mongoTemplate.save(fileEvent1, new OutputToCollectionNameTranslator().toCollectionName(Schema.FILE));
        mongoTemplate.save(fileEvent2, new OutputToCollectionNameTranslator().toCollectionName(Schema.FILE));
        SmartAggregationRecord smartAggregationRecord = new SmartAggregationRecord(aggregationRecord);
        smartAggregationRecord.setContribution(0.3);
        smart.setSmartAggregationRecords(Collections.singletonList(smartAggregationRecord));
        Alert alert = alertService.generateAlert(smart, userEntity, 50);

        assertNotNull(alert);
        assertEquals(1, alert.getIndicators().size());
        assertEquals(1, alert.getIndicators().get(0).getEventsNum());
    }


    @Test
    public void testIndicatorWithTwoCategories() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<>(), new ArrayList<>(), null, UserSeverity.CRITICAL, 0);
        SmartRecord smart = generateSingleSmart(60);
        Instant startDate = Instant.parse("2017-11-20T15:00:00.000Z");
        Instant endDate = Instant.parse("2017-11-20T16:00:00.000Z");

        // indicator
        AdeAggregationRecord aggregationRecord = new ScoredFeatureAggregationRecord(90.0, new ArrayList<>(), startDate,
                endDate, "numberOfSensitiveGroupMembershipOperationUserIdActiveDirectoryHourly", +10d,
                "numberOfSensitiveGroupMembershipOperationUserIdActiveDirectoryHourly",
                Collections.singletonMap("userId", "userId"), AggregatedFeatureType.FEATURE_AGGREGATION);

        // event
        EnrichedEvent activeDirectoryEvent1 = new ActiveDirectoryEnrichedEvent(Instant.now(),
                startDate.plus(10, ChronoUnit.MINUTES), "eventId1", Schema.ACTIVE_DIRECTORY.toString(), "userId",
                "username", "userDisplayName", "dataSource", "USER_ACCOUNT_ENABLED",
                Arrays.asList("USER_MANAGEMENT", "SECURITY_SENSITIVE_OPERATION"), EventResult.SUCCESS, "resultCode",
                new HashMap<>(), "objectId");
        mongoTemplate.save(activeDirectoryEvent1, new OutputToCollectionNameTranslator().toCollectionName(Schema.ACTIVE_DIRECTORY));

        // event
        EnrichedEvent activeDirectoryEvent2 = new ActiveDirectoryEnrichedEvent(Instant.now(),
                startDate.plus(20, ChronoUnit.MINUTES), "eventId2", Schema.ACTIVE_DIRECTORY.toString(), "userId",
                "username", "userDisplayName", "dataSource", "USER_ACCOUNT_RE_ENABLED",
                Arrays.asList("USER_MANAGEMENT", "SECURITY_SENSITIVE_OPERATION"), EventResult.SUCCESS, "resultCode",
                new HashMap<>(), "objectId");
        mongoTemplate.save(activeDirectoryEvent2, new OutputToCollectionNameTranslator().toCollectionName(Schema.ACTIVE_DIRECTORY));

        // event
        EnrichedEvent activeDirectoryEvent3 = new ActiveDirectoryEnrichedEvent(Instant.now(),
                startDate.plus(30, ChronoUnit.MINUTES), "eventId3", Schema.ACTIVE_DIRECTORY.toString(), "userId",
                "username", "userDisplayName", "dataSource", "MEMBER_REMOVED_FROM_SECURITY_ENABLED_LOCAL_GROUP",
                Arrays.asList("GROUP_MEMBERSHIP_OPERATION", "SECURITY_SENSITIVE_OPERATION"), EventResult.SUCCESS, "resultCode",
                new HashMap<>(), "objectId");
        mongoTemplate.save(activeDirectoryEvent3, new OutputToCollectionNameTranslator().toCollectionName(Schema.ACTIVE_DIRECTORY));

        SmartAggregationRecord smartAggregationRecord = new SmartAggregationRecord(aggregationRecord);
        smartAggregationRecord.setContribution(0.3);
        smart.setSmartAggregationRecords(Collections.singletonList(smartAggregationRecord));
        Alert alert = alertService.generateAlert(smart, userEntity, 50);
        assertNotNull(alert);
        assertEquals(1, alert.getIndicators().get(0).getEventsNum());
    }


    @Test
    public void testAlertWithLargeNumberOfEvents() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL, 0);
        SmartRecord smart = generateSingleSmart(60);
        Instant startDate = Instant.parse("2017-10-23T15:00:00.000Z");
        Instant endDate = Instant.parse("2017-10-23T16:00:00.000Z");

        // indicator
        AdeAggregationRecord aggregationRecord = new ScoredFeatureAggregationRecord(90.0, new ArrayList<>(), startDate, endDate, "numberOfFailedFilePermissionChangesUserIdFileHourly",
                +2000d, "numberOfFailedFilePermissionChangesUserIdFileHourly", Collections.singletonMap("userId", "userId"), AggregatedFeatureType.FEATURE_AGGREGATION);

        // raw event
        generateFileEvents(2000, aggregationRecord.getStartInstant(), "scored_enriched.file.operationType.userIdFileAction.file.score", "FOLDER_OWNERSHIP_CHANGED", Arrays.asList("FILE_PERMISSION_CHANGE"));
        SmartAggregationRecord smartAggregationRecord = new SmartAggregationRecord(aggregationRecord);
        smartAggregationRecord.setContribution(0.3);
        smart.setSmartAggregationRecords(Collections.singletonList(smartAggregationRecord));

        Alert alert = alertService.generateAlert(smart, userEntity, 50);
        assertNotNull(alert);
        assertEquals(1, alert.getIndicators().size());
        assertEquals(2000d, ((Bucket) alert.getIndicators().get(0).getHistoricalData().getAggregation().getBuckets().get(0)).getValue());
        assertEquals(true, ((Bucket) alert.getIndicators().get(0).getHistoricalData().getAggregation().getBuckets().get(0)).isAnomaly());
    }


    @Test
    public void testAlertWithSourceMachineTransformer() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL, 0);
        SmartRecord smart = generateSingleSmart(60);
        Instant startDate = Instant.parse("2017-05-23T15:00:00.000Z");
        Instant endDate = Instant.parse("2017-05-23T16:00:00.000Z");

        // indicator
        AdeAggregationRecord aggregationRecord = new ScoredFeatureAggregationRecord(90.0, new ArrayList<>(), startDate, endDate, "sumOfHighestSrcMachineNameRegexClusterScoresUserIdAuthenticationHourly",
                100.0, "srcMachineNameRegexClusterHistogramUserIdAuthenticationHourly", Collections.singletonMap("userId", "userId"), AggregatedFeatureType.SCORE_AGGREGATION);

        // raw event
        generateAuthenticationEvents(1, aggregationRecord.getStartInstant(), "scored_enriched.authentication.srcMachine.userId.authentication.score");
        SmartAggregationRecord smartAggregationRecord = new SmartAggregationRecord(aggregationRecord);
        smartAggregationRecord.setContribution(0.3);
        smart.setSmartAggregationRecords(Collections.singletonList(smartAggregationRecord));

        Alert alert = alertService.generateAlert(smart, userEntity, 50);
        assertNotNull(alert);
        Bucket bucket = (Bucket) alert.getIndicators().get(0).getHistoricalData().getAggregation().getBuckets().get(0);
        assertEquals("Unresolved", bucket.getKey());
    }

    @Test
    public void testScoreIndicatorsContribution() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL, 0);
        SmartRecord smart = generateSingleSmart(60);
        Instant startDate = Instant.parse("2016-10-24T15:00:00.000Z");
        Instant endDate = Instant.parse("2016-10-24T16:00:00.000Z");

        // indicator1 - score aggr
        AdeAggregationRecord scoreAggregationRecord = new ScoredFeatureAggregationRecord(90.0, new ArrayList<>(), startDate, endDate, "sumOfHighestOperationTypeScoresUserIdFileActionFileHourly",
                100.0, "operationTypeHistogramUserIdFileDaily", Collections.singletonMap("userId", "userId"), AggregatedFeatureType.SCORE_AGGREGATION);
        generateFileEvents(1, scoreAggregationRecord.getStartInstant(), "scored_enriched.file.operationType.userIdFileAction.file.score", "FOLDER_OWNERSHIP_CHANGED", Arrays.asList("FILE_PERMISSION_CHANGE"));
        generateFileEvents(1, scoreAggregationRecord.getStartInstant(), "scored_enriched.file.operationType.userIdFileAction.file.score", "FOLDER_AUDITING_CHANGED", Arrays.asList("FILE_PERMISSION_CHANGE"));

        // indicator2 - feature aggr
        AdeAggregationRecord featureAggregationRecord = new ScoredFeatureAggregationRecord(90.0, new ArrayList<>(), startDate, endDate, "numberOfFailedFilePermissionChangesUserIdFileHourly",
                +2000d, "numberOfFailedFilePermissionChangesUserIdFileHourly", Collections.singletonMap("userId", "userId"), AggregatedFeatureType.FEATURE_AGGREGATION);
        generateFileEvents(1, featureAggregationRecord.getStartInstant(), "scored_enriched.file.operationType.userIdFileAction.file.score", "FOLDER_OWNERSHIP_CHANGED", Arrays.asList("FILE_PERMISSION_CHANGE"));


        SmartAggregationRecord smartScoreAggregationRecord = new SmartAggregationRecord(scoreAggregationRecord);
        smartScoreAggregationRecord.setContribution(0.5);
        SmartAggregationRecord smartFeatureAggregationRecord = new SmartAggregationRecord(featureAggregationRecord);
        smartFeatureAggregationRecord.setContribution(0.5);
        smart.setSmartAggregationRecords(Arrays.asList(smartFeatureAggregationRecord, smartScoreAggregationRecord));

        //generate alerts:
        Alert alert = alertService.generateAlert(smart, userEntity, 50);

        assertEquals(0.5d, alert.getIndicators().get(0).getScoreContribution(), 0);
        assertEquals(0.25d, alert.getIndicators().get(1).getScoreContribution(), 0);
        assertEquals(0.25d, alert.getIndicators().get(2).getScoreContribution(), 0);

    }

    @Test
    public void testExceedEventsLimit() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL, 0);
        SmartRecord smart = generateSingleSmart(60);
        Instant startDate = Instant.parse("2017-10-24T15:00:00.000Z");
        Instant endDate = Instant.parse("2017-10-24T16:00:00.000Z");

        // indicator
        AdeAggregationRecord aggregationRecord = new ScoredFeatureAggregationRecord(90.0, new ArrayList<>(), startDate, endDate, "numberOfFailedFilePermissionChangesUserIdFileHourly",
                +10d, "numberOfFailedFilePermissionChangesUserIdFileHourly", Collections.singletonMap("userId", "userId"), AggregatedFeatureType.FEATURE_AGGREGATION);

        // raw event
        generateFileEvents(102, aggregationRecord.getStartInstant(), "scored_enriched.file.operationType.userIdFileAction.file.score", "FOLDER_OWNERSHIP_CHANGED", Arrays.asList("FILE_PERMISSION_CHANGE")); //generating 2 events more than the limit (=100)
        SmartAggregationRecord smartAggregationRecord = new SmartAggregationRecord(aggregationRecord);
        smartAggregationRecord.setContribution(0.3);
        smart.setSmartAggregationRecords(Collections.singletonList(smartAggregationRecord));

        //generate alerts:
        Alert alert = alertService.generateAlert(smart, userEntity, 50);

        //check that only indicators events is not exceeding the limit
        Indicator indicator = alert.getIndicators().get(0);
        assertEquals(100, indicator.getEvents().size());
        assertEquals(100, indicator.getEventsNum());
    }

    private void generateFileEvents(int eventsNum, Instant startEventTime, String adeEventType, String opertaionType, List<String> operationTypeCategories) {
        Instant now = Instant.now();
        String schema = Schema.FILE.toString();
        HashMap<String, String> additionalInfo = new HashMap<>();

        for (int i = 1; i <= eventsNum; i++) {

            // generate output events
            FileEnrichedEvent fileEvent = new FileEnrichedEvent(now, startEventTime.plus(new Random().nextInt(50), ChronoUnit.MINUTES), "eventId1" + i, schema, "userId", "username", "userDisplayName", "dataSource", opertaionType, operationTypeCategories,
                    EventResult.FAILURE, "FAILURE", additionalInfo, "absoluteSrcFilePath", "absoluteDstFilePath",
                    "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);
            mongoTemplate.save(fileEvent, new OutputToCollectionNameTranslator().toCollectionName(Schema.FILE));

            // generate ade events
            EnrichedFileRecord enrichedFileRecord = new EnrichedFileRecord(fileEvent.getEventDate());
            enrichedFileRecord.setUserId("userId");
            enrichedFileRecord.setEventId(fileEvent.getEventId());
            enrichedFileRecord.setOperationType(fileEvent.getOperationType());
            enrichedFileRecord.setOperationTypeCategories(fileEvent.getOperationTypeCategories());
            enrichedFileRecord.setResult(fileEvent.getResult());
            mongoTemplate.save(enrichedFileRecord, new EnrichedDataAdeToCollectionNameTranslator().toCollectionName(Schema.FILE.getName().toLowerCase()));

            // generate scored ade events
            AdeScoredEnrichedRecord fileScoredEnrichedEvent = new AdeScoredFileRecord(enrichedFileRecord.getStartInstant(), adeEventType, "authentication", 10.0d, new ArrayList<FeatureScore>(), enrichedFileRecord);
            mongoTemplate.save(fileScoredEnrichedEvent, new AdeScoredEnrichedRecordToCollectionNameTranslator().toCollectionName(adeEventType));
        }
    }


    private void generateAuthenticationEvents(int eventsNum, Instant startEventTime, String adeEventType) {
        Instant now = Instant.now();
        String schema = Schema.AUTHENTICATION.toString();

        for (int i = 1; i <= eventsNum; i++) {

            // generate output events
            AuthenticationEnrichedEvent authenticationEvent = new AuthenticationEnrichedEvent(now, startEventTime.plus(new Random().nextInt(5), ChronoUnit.MINUTES), "eventId1" + i, schema, "userId", "username", "userDisplayName", "dataSource", "User authenticated through Kerberos", new ArrayList<String>(), EventResult.SUCCESS, "SUCCESS", new HashMap<>());
            authenticationEvent.setSrcMachineNameRegexCluster("N/A");
            mongoTemplate.save(authenticationEvent, new OutputToCollectionNameTranslator().toCollectionName(Schema.AUTHENTICATION));

            // generate ade events
            EnrichedAuthenticationRecord enrichedAuthenticationEventRecord = new EnrichedAuthenticationRecord(authenticationEvent.getEventDate());
            enrichedAuthenticationEventRecord.setUserId("userId");
            enrichedAuthenticationEventRecord.setEventId(authenticationEvent.getEventId());
            enrichedAuthenticationEventRecord.setOperationType(authenticationEvent.getOperationType());
            enrichedAuthenticationEventRecord.setOperationTypeCategories(authenticationEvent.getOperationTypeCategories());
            enrichedAuthenticationEventRecord.setResult(authenticationEvent.getResult());
            mongoTemplate.save(enrichedAuthenticationEventRecord, new EnrichedDataAdeToCollectionNameTranslator().toCollectionName(Schema.AUTHENTICATION.getName().toLowerCase()));

            // generate scored ade events
            AdeScoredEnrichedRecord authenticationScoredEnrichedEvent = new AdeScoredAuthenticationRecord(enrichedAuthenticationEventRecord.getStartInstant(), adeEventType, "authentication", 10.0d, new ArrayList<FeatureScore>(), enrichedAuthenticationEventRecord);
            mongoTemplate.save(authenticationScoredEnrichedEvent, new AdeScoredEnrichedRecordToCollectionNameTranslator().toCollectionName(adeEventType));
        }
    }

    private void generateActiveDirectoryEvents(int eventsNum, Instant startEventTime, String adeEventType, String opertaionType, List<String> operationTypeCategories) {
        Instant now = Instant.now();
        String schema = Schema.ACTIVE_DIRECTORY.toString();

        for (int i = 1; i <= eventsNum; i++) {

            // generate output events
            ActiveDirectoryEnrichedEvent activeDirectoryEvent = new ActiveDirectoryEnrichedEvent(now, startEventTime.plus(new Random().nextInt(5), ChronoUnit.MINUTES), "eventId" + UUID.randomUUID(), schema, "userId", "username", "userDisplayName", "dataSource", opertaionType, operationTypeCategories, EventResult.SUCCESS, "SUCCESS", new HashMap<>(), "");
            mongoTemplate.save(activeDirectoryEvent, new OutputToCollectionNameTranslator().toCollectionName(Schema.ACTIVE_DIRECTORY));

            // generate ade events
            EnrichedActiveDirectoryRecord enrichedActiveDirectoryRecord = new EnrichedActiveDirectoryRecord(activeDirectoryEvent.getEventDate());
            enrichedActiveDirectoryRecord.setUserId("userId");
            enrichedActiveDirectoryRecord.setEventId(activeDirectoryEvent.getEventId());
            enrichedActiveDirectoryRecord.setOperationType(activeDirectoryEvent.getOperationType());
            enrichedActiveDirectoryRecord.setOperationTypeCategories(activeDirectoryEvent.getOperationTypeCategories());
            enrichedActiveDirectoryRecord.setResult(activeDirectoryEvent.getResult());
            mongoTemplate.save(enrichedActiveDirectoryRecord, new EnrichedDataAdeToCollectionNameTranslator().toCollectionName(Schema.ACTIVE_DIRECTORY.getName().toLowerCase()));

            // generate scored ade events
            AdeScoredEnrichedRecord activeDirectoryScoredEnrichedEvent = new AdeScoredActiveDirectoryRecord(enrichedActiveDirectoryRecord.getStartInstant(), adeEventType, "active_directory", 10.0d, new ArrayList<FeatureScore>(), enrichedActiveDirectoryRecord);
            mongoTemplate.save(activeDirectoryScoredEnrichedEvent, new AdeScoredEnrichedRecordToCollectionNameTranslator().toCollectionName(adeEventType));
        }
    }


    private SmartRecord generateSingleSmart(int score) {
        List<FeatureScore> featureScores = new ArrayList<>();
        List<SmartAggregationRecord> smartAggregationRecords = new ArrayList<>();
        TimeRange timeRange = new TimeRange(Instant.now(), Instant.now());
        return new SmartRecord(
                timeRange, contextId, featureName, FixedDurationStrategy.HOURLY,
                5.0, score, featureScores, smartAggregationRecords, null);
    }
}