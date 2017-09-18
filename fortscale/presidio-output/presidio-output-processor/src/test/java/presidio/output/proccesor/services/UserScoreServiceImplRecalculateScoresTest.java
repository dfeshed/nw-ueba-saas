package presidio.output.proccesor.services;

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
import presidio.output.domain.records.alerts.Alert;
import presidio.output.domain.records.alerts.AlertEnums;
import presidio.output.domain.records.alerts.AlertQuery;
import presidio.output.domain.services.alerts.AlertPersistencyService;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.output.domain.services.users.UserPersistencyServiceImpl;
import presidio.output.processor.services.user.UserScoreServiceImpl;
import presidio.output.processor.services.user.UsersAlertData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by shays on 27/08/2017.
 */

public class UserScoreServiceImplRecalculateScoresTest {

    public static final int ALERT_CONTRIBUTION_CRITICAL = 30;
    public static final int ALERT_CONTRIBUTION_HIGH = 25;
    public static final int ALERT_CONTRIBUTION_MEDIUM = 20;
    public static final int ALERT_CONTRIBUTION_LOW = 10;
    public static final int ALERT_EFFECTIVE_DURATION_IN_DAYS = 90;
    private UserScoreServiceImpl userScoreService;
    private UserPersistencyService mockUserPresistency;
    private AlertPersistencyService mockAlertPresistency;

    private Page<Alert> emptyAlertPage;


    @Before
    public void setup() {
        mockUserPresistency = Mockito.mock(UserPersistencyServiceImpl.class);
        mockAlertPresistency = Mockito.mock(AlertPersistencyService.class);

        userScoreService = new UserScoreServiceImpl(mockUserPresistency,
                mockAlertPresistency,
                1000,
                ALERT_EFFECTIVE_DURATION_IN_DAYS,
                75,
                50,
                25,
                ALERT_CONTRIBUTION_CRITICAL,
                ALERT_CONTRIBUTION_HIGH,
                ALERT_CONTRIBUTION_MEDIUM,
                ALERT_CONTRIBUTION_LOW);


        emptyAlertPage = new PageImpl<Alert>(Collections.emptyList());
    }

    @Test
    public void testBasicScoreCalculation() throws Exception {
        Pageable pageable1 = new PageRequest(0, 10);
        List<Alert> mockAlerts;

        LocalDateTime weekAgo = LocalDate.now().minusDays(7).atStartOfDay().plusHours(3);

        long startTimeAWeekAgo = Date.from(weekAgo.atZone(ZoneId.systemDefault()).toInstant()).getTime();

        mockAlerts = Arrays.asList(
                new Alert("user1", "smartId", null, null, startTimeAWeekAgo, 0, 95, 0, null, AlertEnums.AlertSeverity.CRITICAL, null),
                new Alert("user1", "smartId", null, null, startTimeAWeekAgo, 0, 95, 0, null, AlertEnums.AlertSeverity.HIGH, null),
                new Alert("user1", "smartId", null, null, startTimeAWeekAgo, 0, 95, 0, null, AlertEnums.AlertSeverity.LOW, null),
                new Alert("user2", "smartId", null, null, startTimeAWeekAgo, 0, 95, 0, null, AlertEnums.AlertSeverity.CRITICAL, null),
                new Alert("user2", "smartId", null, null, startTimeAWeekAgo, 0, 95, 0, null, AlertEnums.AlertSeverity.CRITICAL, null)
        );
        Page<Alert> alertPage1 = new PageImpl<Alert>(mockAlerts, pageable1, 5);
        Mockito.when(this.mockAlertPresistency.find(Mockito.any(AlertQuery.class))).thenAnswer(new Answer<Page>() {
            @Override
            public Page answer(InvocationOnMock invocation) throws Throwable {
                AlertQuery query = (AlertQuery) invocation.getArguments()[0];
                if (query.getFilterByStartDate() <= startTimeAWeekAgo && query.getFilterByEndDate() >= startTimeAWeekAgo) {
                    return alertPage1;
                } else {
                    return emptyAlertPage;
                }
            }
        });


        Map<String, UsersAlertData> aggregatedUserScore = userScoreService.calculateUserScores(ALERT_EFFECTIVE_DURATION_IN_DAYS);
        Assert.assertEquals(2, aggregatedUserScore.size());
        double user1Expected = (ALERT_CONTRIBUTION_CRITICAL + ALERT_CONTRIBUTION_HIGH + ALERT_CONTRIBUTION_LOW) * 1D;
        double user2Expected = (ALERT_CONTRIBUTION_CRITICAL * 2) * 1D;
        Assert.assertEquals(user1Expected, aggregatedUserScore.get("user1").getUserScore(), 0.1);
        Assert.assertEquals(user2Expected, aggregatedUserScore.get("user2").getUserScore(), 0.1);
    }

    @Test
    public void testScoreCalculationWithPaging() throws Exception {

        List<Alert> mockAlertsPage1;
        List<Alert> mockAlertsPage2;

        LocalDateTime weekAgo = LocalDate.now().minusDays(7).atStartOfDay().plusHours(3);

        long startTimeAWeekAgo = Date.from(weekAgo.atZone(ZoneId.systemDefault()).toInstant()).getTime();
        long oldStartTime = LocalDate.now().minusDays(ALERT_EFFECTIVE_DURATION_IN_DAYS * 2).toEpochDay();

        mockAlertsPage1 = Arrays.asList(

                //Page1-one user which 3 alerts one user with 2 alrrts
                new Alert("user1", "smartId", null, null, startTimeAWeekAgo, 0, 95, 0, null, AlertEnums.AlertSeverity.CRITICAL, null),
                new Alert("user1", "smartId", null, null, startTimeAWeekAgo, 0, 95, 0, null, AlertEnums.AlertSeverity.HIGH, null),
                new Alert("user1", "smartId", null, null, oldStartTime, 0, 95, 0, null, AlertEnums.AlertSeverity.LOW, null),
                new Alert("user2", "smartId", null, null, oldStartTime, 0, 95, 0, null, AlertEnums.AlertSeverity.CRITICAL, null),
                new Alert("user2", "smartId", null, null, startTimeAWeekAgo, 0, 95, 0, null, AlertEnums.AlertSeverity.CRITICAL, null)
        );

        mockAlertsPage2 = Arrays.asList(

                //Page2-3  alerts that should be counted, and 2 alerts which should not be counted
                new Alert("user1", "smartId", null, null, startTimeAWeekAgo, 0, 95, 0, null, AlertEnums.AlertSeverity.LOW, null),
                new Alert("user1", "smartId", null, null, startTimeAWeekAgo, 0, 95, 0, null, AlertEnums.AlertSeverity.LOW, null),
                new Alert("user1", "smartId", null, null, oldStartTime, 0, 95, 0, null, AlertEnums.AlertSeverity.LOW, null),
                new Alert("user2", "smartId", null, null, startTimeAWeekAgo, 0, 95, 0, null, AlertEnums.AlertSeverity.HIGH, null),
                new Alert("user3", "smartId", null, null, startTimeAWeekAgo, 0, 95, 0, null, AlertEnums.AlertSeverity.HIGH, null)
        );
        Pageable pageable1 = new PageRequest(0, 5);
        Page<Alert> alertPage1 = new PageImpl<Alert>(mockAlertsPage1, pageable1, 10);

        Pageable pageable2 = new PageRequest(1, 5);
        Page<Alert> alertPage2 = new PageImpl<Alert>(mockAlertsPage2, pageable2, 10);
        Mockito.when(this.mockAlertPresistency.find(Mockito.any(AlertQuery.class))).thenAnswer(new Answer<Page>() {
            @Override
            public Page answer(InvocationOnMock invocation) throws Throwable {
                AlertQuery query = (AlertQuery) invocation.getArguments()[0];
                if (query.getFilterByStartDate() <= startTimeAWeekAgo && query.getFilterByEndDate() >= startTimeAWeekAgo) {
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


        Map<String, UsersAlertData> aggregatedUserScore = Whitebox.invokeMethod(userScoreService, "calculateUserScores", ALERT_EFFECTIVE_DURATION_IN_DAYS);
        Assert.assertEquals(3, aggregatedUserScore.size());

        Assert.assertEquals(95D, aggregatedUserScore.get("user1").getUserScore(), 0.1);
        Assert.assertEquals(6, aggregatedUserScore.get("user1").getAlertsCount());
        Assert.assertEquals(85D, aggregatedUserScore.get("user2").getUserScore(), 0.1);
        Assert.assertEquals(3, aggregatedUserScore.get("user2").getAlertsCount());
        Assert.assertEquals(25D, aggregatedUserScore.get("user3").getUserScore(), 0.1);
        Assert.assertEquals(1, aggregatedUserScore.get("user3").getAlertsCount());

    }
}
