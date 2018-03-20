package presidio.output.forwarder.shell;

import fortscale.common.general.Schema;
import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.records.users.User;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.forwarder.spring.OutputForwarderTestConfigBeans;
import presidio.output.forwarder.MemoryStrategy;
import presidio.output.forwarder.spring.OutputForwarderTestConfig;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {OutputForwarderTestConfigBeans.class, OutputForwarderTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OutputForwarderExecutionServiceModuleTest {


    @Autowired
    private OutputForwarderExecutionService outputForwarderExecutionService;

    @Autowired
    UserPersistencyService userPersistencyService;

    @Autowired
    AlertPersistencyService alertPersistencyService;

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;

    @Autowired
    MemoryStrategy memoryStrategy;

    @Before
    public void setup() {

        memoryStrategy.cleanAll();

        Date fiveDaysAgo = Date.from(Instant.now().minus(5, ChronoUnit.DAYS));
        Date now = new Date();

        User user1 = new User("test", "test1", "test3", 90.0d, new ArrayList<>(), new ArrayList<>(), null, UserSeverity.CRITICAL, 0);
        user1.setUpdatedByLogicalStartDate(now);
        user1.setUpdatedByLogicalEndDate(now);
        userPersistencyService.save(user1);

        User user2 = new User("test", "test1", "test3", 90.0d, new ArrayList<>(), new ArrayList<>(), null, UserSeverity.CRITICAL, 0);
        user2.setUpdatedByLogicalStartDate(fiveDaysAgo);
        user2.setUpdatedByLogicalEndDate(fiveDaysAgo);
        userPersistencyService.save(user2);

        Alert alert1 =
                new Alert("userId", "smartId", new ArrayList<>(), "user1", "user1", now, now, 95.0d, 3, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 5D);
        alertPersistencyService.save(alert1);

        Alert alert2 =
                new Alert("userId", "smartId", new ArrayList<>(), "user1", "user1", fiveDaysAgo, fiveDaysAgo, 95.0d, 3, AlertEnums.AlertTimeframe.HOURLY, AlertEnums.AlertSeverity.HIGH, null, 5D);
        alertPersistencyService.save(alert2);

        Indicator indicator1 = new Indicator();
        indicator1.setAlertId("c678bb28-f795-402c-8d64-09f26e82807d");
        indicator1.setName("high_number_of_distinct_src_computer_clusters_print");
        indicator1.setStartDate(now);
        indicator1.setEndDate(now);
        indicator1.setAnomalyValue("60.0");
        indicator1.setSchema(Schema.PRINT);
        indicator1.setType(AlertEnums.IndicatorTypes.FEATURE_AGGREGATION);
        indicator1.setScoreContribution(0.19593662136570342);
        alertPersistencyService.save(indicator1);

        Indicator indicator2 = new Indicator();
        indicator2.setAlertId("c678bb28-f795-402c-8d64-09f26e82807d");
        indicator2.setName("high_number_of_distinct_src_computer_clusters_print");
        indicator2.setStartDate(fiveDaysAgo);
        indicator2.setEndDate(fiveDaysAgo);
        indicator2.setAnomalyValue("60.0");
        indicator2.setSchema(Schema.PRINT);
        indicator2.setType(AlertEnums.IndicatorTypes.FEATURE_AGGREGATION);
        indicator2.setScoreContribution(0.19593662136570342);
        alertPersistencyService.save(indicator2);

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

        esTemplate.deleteIndex(User.class);
        esTemplate.createIndex(User.class);
        esTemplate.putMapping(User.class);
        esTemplate.refresh(User.class);
    }


    @Test
    public void testRun() {

        try {
            outputForwarderExecutionService.doRun(Instant.now().minus(Duration.ofDays(2)), Instant.now().plus(Duration.ofDays(2)));
            Assert.assertEquals(3, memoryStrategy.getAllMessages().size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }


    @Test
    public void testCleanup() {

        try {
            outputForwarderExecutionService.doClean(Instant.now().minus(Duration.ofDays(2)), Instant.now().plus(Duration.ofDays(2)));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }




}
