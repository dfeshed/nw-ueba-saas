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

    @Before
    public void before() {
        esTemplate.deleteIndex(Alert.class);
        esTemplate.createIndex(Alert.class);
        esTemplate.putMapping(Alert.class);
        esTemplate.refresh(Alert.class);
    }

    @Test
    public void testSave() {
        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();
        Alert alert =
                new Alert("user1", AlertType.DATA_EXFILTRATION, startDate, endDate, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH);
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
                new Alert("user1", AlertType.DATA_EXFILTRATION, startDate, endDate, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("user1", AlertType.ANOMALOUS_ADMIN_ACTIVITY, startDate, endDate, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        Iterable<Alert> testAlert = alertPersistencyService.save(alertList);

        assertThat(Lists.newArrayList(testAlert).size(), is(2));

    }

    @Test
    public void testFindOne() {
        long startDate = Instant.now().toEpochMilli();
        long endDate = Instant.now().toEpochMilli();
        Alert alert =
                new Alert("user1", AlertType.DATA_EXFILTRATION, startDate, endDate, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH);

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
                new Alert("user1", AlertType.DATA_EXFILTRATION, startDate, endDate, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("user1", AlertType.ANOMALOUS_ADMIN_ACTIVITY, startDate, endDate, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
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
                new Alert("user1", AlertType.DATA_EXFILTRATION, startDate, endDate, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("user1", AlertType.ANOMALOUS_ADMIN_ACTIVITY, startDate, endDate, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
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
                new Alert("user1", AlertType.DATA_EXFILTRATION, startDate, endDate, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH);
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
                new Alert("normalized_username_ipusr3@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate-1, endDate+5, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("normalized_username_ipusr3@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate, endDate+5, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("normalized_username_ipusr3@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate+1, endDate+5, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("normalized_username_ipusr3@somebigcompany.com", AlertType.DATA_EXFILTRATION, startDate+2, endDate+5, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("normalized_username_ipusr4@somebigcompany.com", AlertType.ANOMALOUS_ADMIN_ACTIVITY, startDate, endDate+5, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("normalized_username_ipusr3@somebigcompany.com", AlertType.ANOMALOUS_ADMIN_ACTIVITY, startDate, endDate+5, 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.MEDIUM));
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
                        .build();

        Page<Alert> testAlert = alertPersistencyService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(2L));
    }

}