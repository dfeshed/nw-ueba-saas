package presidio.output.proccesor.services;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.FongoTestConfig;
import fortscale.utils.time.TimeRange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;
import presidio.ade.domain.store.smart.SmartDataReader;
import presidio.ade.domain.store.smart.SmartRecordsMetadata;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.processor.services.alert.AlertEnumsSeverityService;
import presidio.output.processor.services.alert.AlertServiceImpl;
import presidio.output.processor.spring.AlertEnumsConfig;
import presidio.output.processor.spring.AlertServiceElasticConfig;

import java.time.Instant;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

/**
 * Created by efratn on 24/07/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = {AlertServiceElasticConfig.class, AlertEnumsConfig.class, AlertServiceTest.SpringConfig.class, FongoTestConfig.class})
public class AlertServiceTest {

    @MockBean
    private AlertPersistencyService alertPersistencyService;

    @MockBean
    private SmartDataReader smartDataReader;

    @Autowired
    private AlertServiceImpl alertService;

    @Autowired
    private AlertEnumsSeverityService alertEnumsSeverityService;

    @Configuration
    @EnableSpringConfigured
    public static class SpringConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer testPropertiesPlaceholderConfigurer() {
            Properties properties = new Properties();
            properties.put("severity.critical", 95);
            properties.put("severity.high", 85);
            properties.put("severity.mid", 70);
            properties.put("severity.low", 50);
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }


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
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL);
        Alert alert = alertService.generateAlert(generateSingleSmart(30), userEntity);
        assertTrue(alert == null);
    }


    @Test
    public void generateAlertTest() {
        User userEntity = new User("userId", "userName", "displayName", 0d, new ArrayList<String>(), new ArrayList<String>(), null, UserSeverity.CRITICAL);
        SmartRecord smart = generateSingleSmart(60);
        Alert alert = alertService.generateAlert(smart, userEntity);
        assertEquals(alert.getUserId(), userEntity.getUserId());
        assertEquals(alert.getUserName(), userEntity.getUserName());
//        assertEquals(alert.getAlertType() //TODO- test here if the classification is correct once classification is implemented
        assertTrue(alert.getScore() == smart.getScore());
    }

    @Test
    public void severityTest() {
        assertEquals(alertEnumsSeverityService.severity(51), AlertEnums.AlertSeverity.LOW);
        assertEquals(alertEnumsSeverityService.severity(71), AlertEnums.AlertSeverity.MEDIUM);
        assertEquals(alertEnumsSeverityService.severity(86), AlertEnums.AlertSeverity.HIGH);
        assertEquals(alertEnumsSeverityService.severity(97), AlertEnums.AlertSeverity.CRITICAL);
    }

    private SmartRecord generateSingleSmart(int score) {
        List<FeatureScore> feature_scores = new ArrayList<FeatureScore>();
        Map<String, String> context = new HashMap<String, String>();
        List<AdeAggregationRecord> aggregated_feature_events = new ArrayList<>();
        TimeRange timeRange = new TimeRange(Instant.now(), Instant.now());
        SmartRecord smart = new SmartRecord(timeRange, contextId, featureName, FixedDurationStrategy.HOURLY,
                5.0, score, feature_scores, aggregated_feature_events);

        return smart;
    }
}
