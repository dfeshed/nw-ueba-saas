package presidio.webapp.service;

import com.google.common.collect.ImmutableMap;
import fortscale.common.general.Schema;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.IndicatorEvent;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.webapp.model.*;
import presidio.webapp.spring.RestServiceTestConfig;

import java.sql.Date;
import java.time.Instant;
import java.util.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RestServiceTestConfig.class)
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

    @Test
    public void getEvent() {
        List<IndicatorEvent> resultList = new ArrayList<>();
        IndicatorEvent indicatorEvent = createEvent();
        resultList.add(indicatorEvent);
        Page<IndicatorEvent> page = new PageImpl<>(resultList);
        when(alertPersistencyService.findIndicatorEventsByIndicatorId(anyObject(), anyObject())).thenReturn(page);

        EventQuery eventQuery = new EventQuery();
        EventsWrapper eventsWrapper = restAlertService.getIndicatorEventsByIndicatorId("indicatorId",eventQuery);
        Assert.assertEquals(1, eventsWrapper.getEvents().size());
        Event event = eventsWrapper.getEvents().get(0);
        Assert.assertEquals(indicatorEvent.getEventTime().toInstant().getEpochSecond(), event.getTime().longValue());
        Assert.assertEquals(1,event.getScores().size());
        Assert.assertEquals(indicatorEvent.getFeatures().size(),event.keySet().size());

    }

    @Test
    public void getEventWithoutScore() {
        List<IndicatorEvent> resultList = new ArrayList<>();
        IndicatorEvent event = createEvent();
        event.setScores(null);
        resultList.add(event);
        Page<IndicatorEvent> page = new PageImpl<>(resultList);
        when(alertPersistencyService.findIndicatorEventsByIndicatorId(anyObject(), anyObject())).thenReturn(page);

        EventQuery eventQuery = new EventQuery();
        EventsWrapper eventsWrapper = restAlertService.getIndicatorEventsByIndicatorId("indicatorId",eventQuery);
        Assert.assertEquals(1, eventsWrapper.getEvents().size());
        Assert.assertEquals(null,eventsWrapper.getEvents().get(0).getScores());

    }

    @Test
    public void getAlertsWrongPageNumber() {
        List<Alert> resultList = new ArrayList<>();
        long total = 10;
        Pageable pageable = new PageRequest(3, 10);
        Page<Alert> page = new PageImpl<>(resultList, pageable, total);

        when(alertPersistencyService.find(anyObject())).thenReturn(page);

        AlertQuery alertQuery = new AlertQuery();
        alertQuery.setPageNumber(3);
        alertQuery.setPageSize(10);
        alertQuery.setUsersId(new ArrayList<>(Arrays.asList("someUserName")));
        AlertsWrapper alertsWrapper = restAlertService.getAlerts(alertQuery);
        Assert.assertEquals(0, alertsWrapper.getAlerts().size());
        Assert.assertEquals(Math.toIntExact(total), alertsWrapper.getTotal().intValue());
        Assert.assertEquals(Integer.valueOf(3), alertsWrapper.getPage());
    }

    private Alert createAlert() {
        List<String> classifications = new ArrayList<>(Arrays.asList("Mass Changes to Critical Enterprise Groups"));
        return new Alert("userId", "smartId", classifications, "username","username",
                Date.from(Instant.parse("2017-01-01T00:00:00Z")), Date.from(Instant.parse("2017-01-01T11:00:00Z")),
                10, 10, AlertEnums.AlertTimeframe.DAILY, AlertEnums.AlertSeverity.CRITICAL, null,0D);
    }

    private IndicatorEvent createEvent() {
        IndicatorEvent event = new IndicatorEvent();
        event.setIndicatorId("indicatorId");
        event.setEventTime(Date.from(Instant.parse("2017-01-01T11:00:00Z")));
        event.setSchema(Schema.FILE);
        Map<String, Object> features = new HashMap<String, Object>();
        features.put("id", "59fda03eb77dbd60bb1bef1a");
        features.put("eventDate", Instant.parse("2017-01-01T11:00:00Z"));
        features.put("eventId", "0x000000440001745200000220");
        features.put("userId", "S-1-5-21-636461855-2365528612-2953867313-96946");
        features.put("userName", "userName");
        features.put("userDisplayName", "userDisplayName");
        features.put("dataSource", "File System");
        features.put("operationType", "FOLDER_CREATED");
        features.put("operationTypeCategories", new String[] {"FILE_ACTION"});
        features.put("result", "SUCCESS");
        features.put("resultCode", null);
        features.put("scores", "");
        Map<String, String> additionalInfo = new HashMap<String, String>();
        additionalInfo.put("originIPv4","10.154.12.165");
        additionalInfo.put("description", "New folder c:tmp created on PRODAAA.");
        additionalInfo.put("oSVersion", "Windows Server 2012 R2 Standard");
        additionalInfo.put("isUserAdmin", "false");
        additionalInfo.put("operationType", "FOLDER_CREATED");
        features.put("additionalInfo", additionalInfo);
        event.setFeatures(features);
        Map<String, Double> scores = ImmutableMap.of("operationType",59d);
        event.setScores(scores);
        return event;
    }
}
