package presidio.output.domain.services;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import fortscale.utils.elasticsearch.PresidioElasticsearchTemplate;
import presidio.output.domain.records.Alert;
import presidio.output.domain.records.AlertQuery;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static presidio.output.domain.records.AlertEnums.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
@Import(fortscale.utils.elasticsearch.config.ElasticsearchConfig.class)
public class AlertServiceTest {

    @Autowired
    private AlertService alertService;

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

        Alert alert =
                new Alert("1010","user1", AlertType.DATA_EXFILTRATION, "23-FEB-2017", "23-FEB-2017", 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH);
        Alert testAlert = alertService.save(alert);

        assertNotNull(testAlert.getId());
        assertEquals(testAlert.getId(), alert.getId());
        assertEquals(testAlert.getUserName(), alert.getUserName());
        //assertEquals(testAlert.getStartDate(), alert.getStartDate());
    }

    @Test
    public void testSaveBulk() {

        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("1010","user1", AlertType.DATA_EXFILTRATION, "23-FEB-2017", "23-FEB-2017", 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("1020","user1", AlertType.ANOMALOUS_ADMIN_ACTIVITY, "23-FEB-2017", "23-FEB-2017", 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        Iterable<Alert> testAlert = alertService.save(alertList);

        assertThat(Lists.newArrayList(testAlert).size(), is(2));

    }

    @Test
    public void testFindOne() {

        Alert alert =
                new Alert("1010","user1", AlertType.DATA_EXFILTRATION, "23-FEB-2017", "23-FEB-2017", 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH);

        alertService.save(alert);

        Alert testAlert = alertService.findOne(alert.getId());

        assertNotNull(testAlert.getId());
        assertEquals(testAlert.getId(), alert.getId());
        assertEquals(testAlert.getUserName(), alert.getUserName());
       // assertEquals(testAlert.getStartDate(), alert.getStartDate());

    }

    @Test
    public void testFindAll() {
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("1010","user1", AlertType.DATA_EXFILTRATION, "23-FEB-2017", "23-FEB-2017", 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("1020","user1", AlertType.ANOMALOUS_ADMIN_ACTIVITY, "23-FEB-2017", "23-FEB-2017", 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        for (Alert alert : alertList) {
            alertService.save(alert);
        }

        Iterable<Alert> testAlert = alertService.findAll();
        assertThat(Lists.newArrayList(testAlert).size(), is(2));


    }

    @Test
    public void testFindByUserName() {
        List<Alert> alertList = new ArrayList<>();
        alertList.add(
                new Alert("1010","user1", AlertType.DATA_EXFILTRATION, "23-FEB-2017", "23-FEB-2017", 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        alertList.add(
                new Alert("1020","user1", AlertType.ANOMALOUS_ADMIN_ACTIVITY, "23-FEB-2017", "23-FEB-2017", 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH));
        for (Alert alert : alertList) {
            alertService.save(alert);
        }

        Page<Alert> byName1 = alertService.findByUserName("user1",new PageRequest(0, 10));
        assertThat(byName1.getTotalElements(), is(2L));

        Page<Alert> byName2 = alertService.findByUserName("user2",new PageRequest(0, 10));
        assertThat(byName2.getTotalElements(), is(0L));
    }


    @Test
    public void testDelete() {

        Alert alert =
                new Alert("1010","user1", AlertType.DATA_EXFILTRATION, "23-FEB-2017", "23-FEB-2017", 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH);
        alertService.save(alert);
        alertService.delete(alert);
        Alert testAlert = alertService.findOne(alert.getId());
        assertNull(testAlert);
    }

    @Test
    public void testFindByQuery() {

        Alert alert =
                new Alert("1010","user1", AlertType.DATA_EXFILTRATION, "23-FEB-2017", "23-FEB-2017", 95.0d,3,AlertTimeframe.HOURLY, AlertSeverity.HIGH);
        alertService.save(alert);

        AlertQuery alertQuery =
                new AlertQuery.AlertQueryBuilder()
                        .filterByUserName("user1")
                        .sortField(Alert.SCORE, true)
                        .aggregateBySeverity(false)
                        .build();

        Page<Alert> testAlert = alertService.find(alertQuery);
        assertThat(testAlert.getTotalElements(), is(1L));
    }

}