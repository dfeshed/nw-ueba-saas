package presidio.output.proccesor.services.entity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.commons.services.alert.AlertSeverityServiceImpl;
import presidio.output.commons.services.alert.AlertSeverityService;
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.alerts.AlertPersistencyServiceImpl;
import presidio.output.domain.services.entities.EntityPersistencyService;
import presidio.output.domain.services.entities.EntityPersistencyServiceImpl;
import presidio.output.processor.services.entity.EntityScoreServiceImpl;
import presidio.output.processor.services.entity.EntitiesAlertData;

import java.time.*;
import java.util.*;

/**
 * Created by shays on 27/08/2017.
 */

public class EntityScoreServiceImplRecalculateScoresTest {

    public static final int CRITICAL_SCORE = 95;
    public static final int HIGH_SCORE = 90;
    public static final int MEDIUM_SCORE = 80;
    public static final int ALERT_CONTRIBUTION_CRITICAL = 30;
    public static final int ALERT_CONTRIBUTION_HIGH = 25;
    public static final int ALERT_CONTRIBUTION_MEDIUM = 20;
    public static final int ALERT_CONTRIBUTION_LOW = 10;
    public static final int ALERT_EFFECTIVE_DURATION_IN_DAYS = 90;
    private EntityScoreServiceImpl entityScoreService;
    private EntityPersistencyService mockEntityPresistency;
    private AlertPersistencyService mockAlertPresistency;
    private AlertSeverityService mockAlertSeverityService;

    private Page<Alert> emptyAlertPage;


    @Before
    public void setup() {
        mockAlertSeverityService = new AlertSeverityServiceImpl(
                CRITICAL_SCORE,
                HIGH_SCORE,
                MEDIUM_SCORE,
                ALERT_CONTRIBUTION_CRITICAL,
                ALERT_CONTRIBUTION_HIGH,
                ALERT_CONTRIBUTION_MEDIUM,
                ALERT_CONTRIBUTION_LOW);
        mockEntityPresistency = Mockito.mock(EntityPersistencyServiceImpl.class);
        mockAlertPresistency = Mockito.mock(AlertPersistencyServiceImpl.class);

        entityScoreService = new EntityScoreServiceImpl(mockEntityPresistency,
                mockAlertPresistency,
                mockAlertSeverityService,
                1000,
                ALERT_EFFECTIVE_DURATION_IN_DAYS);


        emptyAlertPage = new PageImpl<Alert>(Collections.emptyList());
    }

    @Test
    public void testBasicScoreCalculation() throws Exception {
        Pageable pageable1 = new PageRequest(0, 10);
        List<Alert> mockAlerts;

        LocalDateTime weekAgo = LocalDate.now().minusDays(7).atStartOfDay().plusHours(3);

        Date startTimeAWeekAgo = Date.from(weekAgo.atZone(ZoneOffset.UTC).toInstant());

        mockAlerts = Arrays.asList(
                new Alert("entity1", "smartId", null, null, null,startTimeAWeekAgo, new Date(), 95, 0, null, AlertEnums.AlertSeverity.CRITICAL, null,(double)ALERT_CONTRIBUTION_CRITICAL, "entityType"),
                new Alert("entity1", "smartId", null, null,null, startTimeAWeekAgo, new Date(), 95, 0, null, AlertEnums.AlertSeverity.HIGH, null,(double)ALERT_CONTRIBUTION_HIGH, "entityType"),
                new Alert("entity1", "smartId", null, null, null,startTimeAWeekAgo, new Date(), 95, 0, null, AlertEnums.AlertSeverity.LOW, null,(double)ALERT_CONTRIBUTION_LOW, "entityType"),
                new Alert("entity2", "smartId", null, null,null, startTimeAWeekAgo, new Date(), 95, 0, null, AlertEnums.AlertSeverity.CRITICAL, null,(double)ALERT_CONTRIBUTION_CRITICAL, "entityType"),
                new Alert("entity2", "smartId", null, null,null, startTimeAWeekAgo, new Date(), 95, 0, null, AlertEnums.AlertSeverity.CRITICAL, null,(double)ALERT_CONTRIBUTION_CRITICAL, "entityType")
        );
        Page<Alert> alertPage1 = new PageImpl<Alert>(mockAlerts, pageable1, 5);
        Mockito.when(this.mockAlertPresistency.find(Mockito.any(AlertQuery.class))).thenAnswer(new Answer<Page>() {
            @Override
            public Page answer(InvocationOnMock invocation) throws Throwable {
                AlertQuery query = (AlertQuery) invocation.getArguments()[0];
                if (query.getFilterByStartDate() <= startTimeAWeekAgo.getTime() && query.getFilterByEndDate() >= startTimeAWeekAgo.getTime()) {
                    return alertPage1;
                } else {
                    return emptyAlertPage;
                }
            }
        });


        Map<String, EntitiesAlertData> aggregatedEntityScore = entityScoreService.calculateEntityScores(ALERT_EFFECTIVE_DURATION_IN_DAYS, Instant.now());
        Assert.assertEquals(2, aggregatedEntityScore.size());
        double entity1Expected = (ALERT_CONTRIBUTION_CRITICAL + ALERT_CONTRIBUTION_HIGH + ALERT_CONTRIBUTION_LOW) * 1D;
        double entity2Expected = (ALERT_CONTRIBUTION_CRITICAL * 2) * 1D;
        Assert.assertEquals(entity1Expected, aggregatedEntityScore.get("entity1").getEntityScore(), 0.1);
        Assert.assertEquals(entity2Expected, aggregatedEntityScore.get("entity2").getEntityScore(), 0.1);
    }

    @Test
    public void testScoreCalculationWithPaging() throws Exception {

        List<Alert> mockAlertsPage1;
        List<Alert> mockAlertsPage2;

        LocalDateTime weekAgo = LocalDate.now().minusDays(7).atStartOfDay().plusHours(3);

        Date startTimeAWeekAgo = Date.from(weekAgo.atZone(ZoneOffset.UTC).toInstant());
        Date oldStartTime = new Date(LocalDate.now().minusDays(ALERT_EFFECTIVE_DURATION_IN_DAYS * 2).toEpochDay());

        mockAlertsPage1 = Arrays.asList(

                //Page1-one entity which 3 alerts one entity with 2 alrrts
                new Alert("entity1", "smartId", null, null, null, startTimeAWeekAgo, new Date(), 95, 0, null, AlertEnums.AlertSeverity.CRITICAL, null,(double)ALERT_CONTRIBUTION_CRITICAL, "entityType"),
                new Alert("entity1", "smartId", null, null, null, startTimeAWeekAgo, new Date(), 95, 0, null, AlertEnums.AlertSeverity.HIGH, null,(double)ALERT_CONTRIBUTION_HIGH, "entityType"),
                new Alert("entity1", "smartId", null, null,null, oldStartTime, new Date(), 95, 0, null, AlertEnums.AlertSeverity.LOW, null,(double)ALERT_CONTRIBUTION_LOW, "entityType"),
                new Alert("entity2", "smartId", null, null,null, oldStartTime, new Date(), 95, 0, null, AlertEnums.AlertSeverity.CRITICAL, null,(double)ALERT_CONTRIBUTION_CRITICAL, "entityType"),
                new Alert("entity2", "smartId", null, null, null,startTimeAWeekAgo, new Date(), 95, 0, null, AlertEnums.AlertSeverity.CRITICAL, null,(double)ALERT_CONTRIBUTION_CRITICAL, "entityType")
        );

        mockAlertsPage2 = Arrays.asList(

                //Page2-3  alerts that should be counted, and 2 alerts which should not be counted
                new Alert("entity1", "smartId", null, null, null,startTimeAWeekAgo, new Date(), 95, 0, null, AlertEnums.AlertSeverity.LOW, null,(double)ALERT_CONTRIBUTION_LOW, "entityType"),
                new Alert("entity1", "smartId", null, null, null,startTimeAWeekAgo, new Date(), 95, 0, null, AlertEnums.AlertSeverity.LOW, null,(double)ALERT_CONTRIBUTION_LOW, "entityType"),
                new Alert("entity1", "smartId", null, null, null,oldStartTime, new Date(), 95, 0, null, AlertEnums.AlertSeverity.LOW, null,(double)ALERT_CONTRIBUTION_LOW, "entityType"),
                new Alert("entity2", "smartId", null, null, null,startTimeAWeekAgo, new Date(), 95, 0, null, AlertEnums.AlertSeverity.HIGH, null,(double)ALERT_CONTRIBUTION_HIGH, "entityType"),
                new Alert("entity3", "smartId", null, null, null,startTimeAWeekAgo, new Date(), 95, 0, null, AlertEnums.AlertSeverity.HIGH, null,(double)ALERT_CONTRIBUTION_HIGH, "entityType")
        );
        Pageable pageable1 = new PageRequest(0, 5);
        Page<Alert> alertPage1 = new PageImpl<Alert>(mockAlertsPage1, pageable1, 10);

        Pageable pageable2 = new PageRequest(1, 5);
        Page<Alert> alertPage2 = new PageImpl<Alert>(mockAlertsPage2, pageable2, 10);
        Mockito.when(this.mockAlertPresistency.find(Mockito.any(AlertQuery.class))).thenAnswer(new Answer<Page>() {
            @Override
            public Page answer(InvocationOnMock invocation) throws Throwable {
                AlertQuery query = (AlertQuery) invocation.getArguments()[0];
                if (query.getFilterByStartDate() <= startTimeAWeekAgo.getTime() && query.getFilterByEndDate() >= startTimeAWeekAgo.getTime()) {
                    if (query.getPageNumber() == 0) {
                        return alertPage1;
                    } else {
                        return alertPage2;
                    }
                } else {
                    return emptyAlertPage;
                }
            }
        });


        Map<String, EntitiesAlertData> aggregatedEntityScore = Whitebox.invokeMethod(entityScoreService, "calculateEntityScores", ALERT_EFFECTIVE_DURATION_IN_DAYS, Instant.now());
        Assert.assertEquals(3, aggregatedEntityScore.size());

        Assert.assertEquals(95D, aggregatedEntityScore.get("entity1").getEntityScore(), 0.1);
        Assert.assertEquals(6, aggregatedEntityScore.get("entity1").getAlertsCount());
        Assert.assertEquals(85D, aggregatedEntityScore.get("entity2").getEntityScore(), 0.1);
        Assert.assertEquals(3, aggregatedEntityScore.get("entity2").getAlertsCount());
        Assert.assertEquals(25D, aggregatedEntityScore.get("entity3").getEntityScore(), 0.1);
        Assert.assertEquals(1, aggregatedEntityScore.get("entity3").getAlertsCount());

    }
}
