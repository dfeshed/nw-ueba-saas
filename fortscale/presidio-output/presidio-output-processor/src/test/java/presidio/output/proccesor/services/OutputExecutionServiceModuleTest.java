package presidio.output.proccesor.services;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import fortscale.utils.elasticsearch.config.ElasticsearchTestUtils;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.test.mongodb.FongoTestConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import javafx.util.Pair;
import org.assertj.core.util.Lists;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.smart.SmartDataToCollectionNameTranslator;
import presidio.monitoring.aspect.metrics.PresidioCustomMetrics;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.records.events.FileEnrichedEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;
import presidio.output.proccesor.spring.OutputProcessorTestConfiguration;
import presidio.output.proccesor.spring.TestConfig;
import presidio.output.processor.services.OutputExecutionServiceImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = {OutputProcessorTestConfiguration.class, MongodbTestConfig.class, TestConfig.class, FongoTestConfig.class})
public class OutputExecutionServiceModuleTest {
    public static final String USER_ID_TEST_USER = "userId#testUser";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private OutputExecutionServiceImpl outputExecutionService;

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;

    @Autowired
    UserPersistencyService userPersistencyService;

    @Autowired
    AlertPersistencyService alertPersistencyService;

    private static ElasticsearchTestUtils embeddedElasticsearchUtils = new ElasticsearchTestUtils();

    @BeforeClass
    public static void setupElasticsearch() {
        try {
            embeddedElasticsearchUtils.setupLocalElasticsearch();
        } catch (Exception e) {
            Assert.fail("Failed to start elasticsearch");
            embeddedElasticsearchUtils.stopEmbeddedElasticsearch();
        }
    }

    @AfterClass
    public static void stopElasticsearch() throws Exception {
        embeddedElasticsearchUtils.stopEmbeddedElasticsearch();
    }

    @Before
    public void setup() {
        esTemplate.deleteIndex(User.class);
        esTemplate.createIndex(User.class);
        esTemplate.putMapping(User.class);
        esTemplate.refresh(User.class);
        esTemplate.deleteIndex(Alert.class);
        esTemplate.createIndex(Alert.class);
        esTemplate.putMapping(Alert.class);
        esTemplate.refresh(Alert.class);
        String smartUserIdHourlyCollectionName = SmartDataToCollectionNameTranslator.SMART_COLLECTION_PREFIX + "userId_hourly";
        String fileEnrichedEventCollectionName = new OutputToCollectionNameTranslator().toCollectionName(Schema.FILE);

        mongoTemplate.dropCollection(smartUserIdHourlyCollectionName);
        mongoTemplate.dropCollection(fileEnrichedEventCollectionName);

        List<SmartRecord> smartRecords = new ArrayList<>();

        TimeRange timeRange = new TimeRange(Instant.now().minus(Duration.ofDays(1)), Instant.now().plus(Duration.ofDays(1)));
        List<Pair<String, Double>> usersToScoreList = new ArrayList<>();
        usersToScoreList.add(new Pair<>("userTest1", 90.0));
        usersToScoreList.add(new Pair<>("userTest2", 50.0));
        usersToScoreList.add(new Pair<>("userTest3", 70.0));
        usersToScoreList.add(new Pair<>("userTest4", 30.0));
        usersToScoreList.add(new Pair<>("userTest5", 55.0));
        usersToScoreList.add(new Pair<>("userTest6", 65.0));
        usersToScoreList.add(new Pair<>("userTest7", 45.0));
        usersToScoreList.add(new Pair<>("userTest8", 85.0));

        AdeAggregationRecord adeAggregationRecord = new AdeAggregationRecord(Instant.now(), Instant.now(), "highestStartInstantScoreUserIdFileHourly",
                10d, "userAccountTypeChangedScoreUserIdActiveDirectoryHourly", Collections.singletonMap("userId", USER_ID_TEST_USER), AggregatedFeatureType.SCORE_AGGREGATION);
        EnrichedEvent event = new FileEnrichedEvent(Instant.now(), Instant.now(), "eventId", Schema.FILE.toString(),
                USER_ID_TEST_USER, "username", "userDisplayName", "dataSource", "oppType", new ArrayList<String>(),
                EventResult.FAILURE, "resultCode", new HashMap<>(), "absoluteSrcFilePath", "absoluteDstFilePath",
                "absoluteSrcFolderFilePath", "absoluteDstFolderFilePath", 20L, true, true);


        Map<String, String> context = new HashMap<>();
        context.put("userId", USER_ID_TEST_USER);
        for (Pair<String, Double> usersToScore : usersToScoreList) {
            SmartRecord smartRecord = new SmartRecord(timeRange, usersToScore.getKey(), "featureName", FixedDurationStrategy.HOURLY,
                    90, usersToScore.getValue(), Collections.emptyList(), Arrays.asList(adeAggregationRecord), context);
            smartRecords.add(smartRecord);
        }

        mongoTemplate.insert(smartRecords, smartUserIdHourlyCollectionName);
        mongoTemplate.insert(event, fileEnrichedEventCollectionName);
    }


    @Ignore
    @Test
    public void createAlertForNewUser() {
        try {
            outputExecutionService.run(Instant.now().minus(Duration.ofDays(2)), Instant.now().plus(Duration.ofDays(2)));

            Assert.assertEquals(8, Lists.newArrayList(alertPersistencyService.findAll()).size());
            Assert.assertEquals(1, Lists.newArrayList(userPersistencyService.findAll()).size());
            Page<User> users = userPersistencyService.findByUserId(USER_ID_TEST_USER, new PageRequest(0, 9999));
            Assert.assertEquals(1, users.getNumberOfElements());
            User user = users.iterator().next();
            Assert.assertEquals(8, user.getAlertsCount());
            Assert.assertEquals(1, user.getAlertClassifications().size());
            Assert.assertEquals(1, user.getIndicators().size());
            Assert.assertEquals(95, new Double(user.getScore()).intValue());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Ignore
    @Test
    public void createAlertForExistingUser() {
        User userEntity = new User(USER_ID_TEST_USER, "userName", "displayName", 95d, Arrays.asList("existingClassification"), Arrays.asList("existingIndicator"), null, UserSeverity.CRITICAL, 8);
        userPersistencyService.save(userEntity);
        try {
            outputExecutionService.run(Instant.now().minus(Duration.ofDays(2)), Instant.now().plus(Duration.ofDays(2)));

            Assert.assertEquals(8, Lists.newArrayList(alertPersistencyService.findAll()).size());
            Assert.assertEquals(1, Lists.newArrayList(userPersistencyService.findAll()).size());
            Page<User> users = userPersistencyService.findByUserId(USER_ID_TEST_USER, new PageRequest(0, 9999));
            Assert.assertEquals(1, users.getNumberOfElements());
            User user = users.iterator().next();
            Assert.assertEquals(16, user.getAlertsCount());
            Assert.assertEquals(2, user.getAlertClassifications().size());
            Assert.assertEquals(2, user.getIndicators().size());
            Assert.assertEquals(190, new Double(user.getScore()).intValue());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
