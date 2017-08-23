package presidio.output.domain.services;

import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static presidio.output.domain.records.alerts.AlertEnums.AlertSeverity;
import static presidio.output.domain.records.alerts.AlertEnums.AlertTimeframe;
import static presidio.output.domain.records.alerts.AlertEnums.AlertType;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = presidio.output.domain.spring.PresidioOutputPersistencyServiceConfig.class)
public class AlertPersistencyServiceTest {

    @Autowired
    private AlertPersistencyService alertPersistencyService;

    @Autowired
    private PresidioElasticsearchTemplate esTemplate;
    List<String> classifications1;
    List<String> classifications2;
    List<String> classifications3;
    List<String> classifications4;
    List<String> classifications5;

    @Before
    public void before() {
        esTemplate.deleteIndex(Alert.class);
        esTemplate.createIndex(Alert.class);
        esTemplate.putMapping(Alert.class);
        esTemplate.refresh(Alert.class);
        classifications1 = new ArrayList<>(Arrays.asList("a", "b", "c"));
        classifications2 = new ArrayList<>(Arrays.asList("b"));
        classifications3 = new ArrayList<>(Arrays.asList("a"));
        classifications4 = new ArrayList<>(Arrays.asList("d"));
        classifications5 = null;
    }

    @Test
    public void testSave() {
        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();
        Alert alert =
                new Alert("userId", classifications1, "user1", AlertType.DATA_EXFILTRATION, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH);
        Alert testAlert = alertPersistencyService.save(alert);

        assertNotNull(testAlert.getId());
        assertEquals(testAlert.getId(), alert.getId());
        assertEquals(testAlert.getUserName(), alert.getUserName());
        //assertEquals(testAlert.getStartDate(), alert.getStartDate());
    }

    @Test
    public void testSaveBulk() {
        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", classifications1, "user1", AlertType.DATA_EXFILTRATION, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications1, "user1", AlertType.ANOMALOUS_ADMIN_ACTIVITY, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        Iterable<Alert> testAlert = alertPersistencyService.save(alertList);

        assertThat(Lists.newArrayList(testAlert).size(), is(2));

    }

    @Test
    public void testFindOne() {
        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();
        Alert alert =
                new Alert("userId", classifications1, "user1", AlertType.DATA_EXFILTRATION, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH);

        alertPersistencyService.save(alert);

        Alert testAlert = alertPersistencyService.findOne(alert.getId());

        assertNotNull(testAlert.getId());
        assertEquals(testAlert.getId(), alert.getId());
        assertEquals(testAlert.getUserName(), alert.getUserName());
        // assertEquals(testAlert.getStartDate(), alert.getStartDate());

    }

    @Test
    public void testFindAll() {
        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", classifications1, "user1", AlertType.DATA_EXFILTRATION, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications1, "user1", AlertType.ANOMALOUS_ADMIN_ACTIVITY, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        Iterable<Alert> testAlert = alertPersistencyService.findAll();
        assertThat(Lists.newArrayList(testAlert).size(), is(2));


    }

    @Test
    public void testFindByUserName() {
        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", classifications1, "user1", AlertType.DATA_EXFILTRATION, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications1, "user1", AlertType.ANOMALOUS_ADMIN_ACTIVITY, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        Page<Alert> byName1 = alertPersistencyService.findByUserName("user1", new PageRequest(0, 10));
        assertThat(byName1.getTotalElements(), is(2L));

        Page<Alert> byName2 = alertPersistencyService.findByUserName("user2", new PageRequest(0, 10));
        assertThat(byName2.getTotalElements(), is(0L));
    }


    @Test
    public void testDelete() {
        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();
        Alert alert =
                new Alert("userId", classifications1, "user1", AlertType.DATA_EXFILTRATION, startDate, endDate, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH);
        alertPersistencyService.save(alert);
        alertPersistencyService.delete(alert);
        Alert testAlert = alertPersistencyService.findOne(alert.getId());
        assertNull(testAlert);
    }

    @Test
    public void testFindByQuery() {

        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", classifications1, "normalized_username_ipusr3@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate - 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications1, "normalized_username_ipusr3@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications1, "normalized_username_ipusr3@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate + 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications1, "normalized_username_ipusr3@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate + 2, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications1, "normalized_username_ipusr4@somebigcompany.com", AlertType.ANOMALOUS_ADMIN_ACTIVITY, startDate, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications1, "normalized_username_ipusr3@somebigcompany.com", AlertType.ANOMALOUS_ADMIN_ACTIVITY, startDate, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.MEDIUM));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .filterByUserName("normalized_username_ipusr3")
                        .filterBySeverity(AlertSeverity.HIGH.name())
                        .filterByStartDate(startDate)
                        .filterByEndDate(endDate + 1)
                        .sortField(Alert.SCORE, true)
                        .aggregateBySeverity(false)
                        .filterByClassification(classifications2)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(2L));
    }

    @Test
    public void testFindByQueryWhiteClassification1() {

        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", classifications3, "normalized_username_ipusr3@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate - 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications2, "normalized_username_ipusr4@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate - 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications4, "normalized_username_ipusr5@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate - 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .sortField(Alert.SCORE, true)
                        .aggregateBySeverity(false)
                        .filterByClassification(classifications1)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(2L));
    }

    @Test
    public void testFindByQueryWhiteClassification2() {

        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", classifications1, "normalized_username_ipusr3@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate - 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications2, "normalized_username_ipusr4@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate - 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications4, "normalized_username_ipusr5@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate - 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .sortField(Alert.SCORE, true)
                        .aggregateBySeverity(false)
                        .filterByClassification(classifications3)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(1L));
    }

    @Test
    public void testFindByQueryWhiteClassification3() {

        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", classifications1, "normalized_username_ipusr3@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate - 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications2, "normalized_username_ipusr4@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate - 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .sortField(Alert.SCORE, true)
                        .aggregateBySeverity(false)
                        .filterByClassification(classifications4)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(0L));
    }


    @Test
    public void testFindByQueryWhiteClassificationEmptyFilter() {

        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("userId", classifications1, "normalized_username_ipusr3@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate - 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications2, "normalized_username_ipusr4@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate - 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("userId", classifications4, "normalized_username_ipusr5@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate - 1, endDate + 5, 95.0d, 3, AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        for (Alert alert : alertList) {
            alertPersistencyService.save(alert);
        }

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .sortField(Alert.SCORE, true)
                        .aggregateBySeverity(false)
                        .filterByClassification(classifications5)
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(3L));
    }
}