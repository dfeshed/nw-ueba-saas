package presidio.webapp.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.webapp.model.AlertQuery;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.spring.OutputWebappConfigurationTest;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = OutputWebappConfigurationTest.class)
public class RestAlertServiceTest {

    @Autowired
    RestAlertService restAlertService;

    @Autowired
    AlertPersistencyService alertPersistencyService;

    @Test
    public void getAlertByIdSuccess() {
        Alert alert = createAlert();
        alert.setId("id");
        when(alertPersistencyService.findOne(eq(alert.getId()))).thenReturn(alert);

        presidio.webapp.model.Alert alertById = restAlertService.getAlertById("id", false);
        Assert.assertEquals(alert.getUserName(), alertById.getUsername());
    }

    @Test
    public void getAlertById_getNull() {
        when(alertPersistencyService.findOne(anyString())).thenReturn(null);
        presidio.webapp.model.Alert alertById = restAlertService.getAlertById("id", false);
        Assert.assertNull(alertById);
    }

    @Test
    public void getAlertsSuccessNoFiltering() {
        Alert alert = createAlert();
        List<Alert> resultList = new ArrayList<>();
        resultList.add(alert);
        Page<Alert> page = new PageImpl<>(resultList, null, 1);
        when(alertPersistencyService.find(anyObject())).thenReturn(page);

        AlertQuery alertQuery = new AlertQuery();
        AlertsWrapper alertsWrapper = restAlertService.getAlerts(alertQuery);
        List<presidio.webapp.model.Alert> alerts = alertsWrapper.getAlerts();
        Assert.assertEquals(1, alerts.size());
        Assert.assertEquals(1, alertsWrapper.getTotal().intValue());
    }

    @Test
    public void getAlertsSuccess_filterByUsername() {
        Alert firstAlert = createAlert();
        List<Alert> resultList = new ArrayList<>();
        resultList.add(firstAlert);
        Page<Alert> page = new PageImpl<>(resultList);
        when(alertPersistencyService.find(anyObject())).thenReturn(page);

        AlertQuery alertQuery = new AlertQuery();

        alertQuery.setUsersId(new ArrayList<>(Arrays.asList(firstAlert.getUserName())));
        AlertsWrapper alertsWrapper = restAlertService.getAlerts(alertQuery);
        List<presidio.webapp.model.Alert> alerts = alertsWrapper.getAlerts();
        Assert.assertEquals(1, alerts.size());
    }

    @Test
    public void getAlertsNoAlert() {
        List<Alert> resultList = new ArrayList<>();
        Page<Alert> page = new PageImpl<>(resultList);
        when(alertPersistencyService.find(anyObject())).thenReturn(page);

        AlertQuery alertQuery = new AlertQuery();
        alertQuery.setUsersId(new ArrayList<>(Arrays.asList("someUserName")));
        AlertsWrapper alertsWrapper = restAlertService.getAlerts(alertQuery);
        Assert.assertEquals(0, alertsWrapper.getAlerts().size());
    }

    private Alert createAlert() {
        List<String> classifications = new ArrayList<>(Arrays.asList("Mass Changes to Critical Enterprise Groups"));
        return new Alert("userId", "smartId", classifications, "username",
                Date.from(Instant.parse("2017-01-01T00:00:00Z")), Date.from(Instant.parse("2017-01-01T11:00:00Z")),
                10, 10, AlertEnums.AlertTimeframe.DAILY, AlertEnums.AlertSeverity.CRITICAL, null);
    }
}
