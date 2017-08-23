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
import presidio.webapp.restquery.RestAlertQuery;
import presidio.webapp.spring.OutputWebappConfigurationTest;

import java.time.Instant;
import java.util.ArrayList;
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
    AlertPersistencyService alertService;

    @Test
    public void getAlertByIdSuccess() {
        Alert alert = createAlert();
        alert.setId("id");
        when(alertService.findOne(eq(alert.getId()))).thenReturn(alert);

        presidio.webapp.dto.Alert alertById = restAlertService.getAlertById("id");
        Assert.assertEquals(alert.getUserName(), alertById.getUsername());
    }

    @Test
    public void getAlertById_getNull() {
        when(alertService.findOne(anyString())).thenReturn(null);
        presidio.webapp.dto.Alert alertById = restAlertService.getAlertById("id");
        Assert.assertNull(alertById);
    }

    @Test
    public void getAlertsSuccessNoFiltering() {
        Alert alert = createAlert();
        List<Alert> resultList = new ArrayList<>();
        resultList.add(alert);
        Page<Alert> page = new PageImpl<>(resultList);
        when(alertService.find(anyObject())).thenReturn(page);

        RestAlertQuery restAlertQuery = new RestAlertQuery();
        Page<Alert> alerts = restAlertService.getAlerts(restAlertQuery);
        Assert.assertEquals(1, alerts.getNumberOfElements());
    }

    @Test
    public void getAlertsSuccess_filterBuUsername() {
        Alert firstAlert = createAlert();
        List<Alert> resultList = new ArrayList<>();
        resultList.add(firstAlert);
        Page<Alert> page = new PageImpl<>(resultList);
        when(alertService.find(anyObject())).thenReturn(page);

        RestAlertQuery restAlertQuery = new RestAlertQuery();
        restAlertQuery.setUserName(firstAlert.getUserName());
        Page<Alert> alerts = restAlertService.getAlerts(restAlertQuery);
        Assert.assertEquals(1, alerts.getNumberOfElements());
    }

    @Test
    public void getAlertsNoAlert() {
        List<Alert> resultList = new ArrayList<>();
        Page<Alert> page = new PageImpl<>(resultList);
        when(alertService.find(anyObject())).thenReturn(page);

        RestAlertQuery restAlertQuery = new RestAlertQuery();
        restAlertQuery.setUserName("someUserName");
        Page<Alert> alerts = restAlertService.getAlerts(restAlertQuery);
        Assert.assertEquals(0, alerts.getNumberOfElements());
    }

    private Alert createAlert() {
        return new Alert("username", AlertEnums.AlertType.SNOOPING,
                Instant.parse("2017-01-01T00:00:00Z").toEpochMilli(), Instant.parse("2017-01-01T11:00:00Z").toEpochMilli(),
                10, 10, AlertEnums.AlertTimeframe.DAILY, AlertEnums.AlertSeverity.CRITICAL);
    }
}
