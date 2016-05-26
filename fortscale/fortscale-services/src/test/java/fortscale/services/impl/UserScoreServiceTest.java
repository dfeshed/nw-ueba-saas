package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.dto.AlertWithUserScore;
import fortscale.services.AlertsService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Mockito.verify;

/**
 * Created by shays on 26/05/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserScoreServiceTest {

    public static final double LOW_ALERT_INFLUANCE = (double) 10;
    public static final double MEDIUM_ALERT_INFLUANCE = (double) 20;
    public static final double HIGH_ALERT_INFLUANCE = (double) 30;
    public static final double CRITICAL_ALERT_INFLUANCE = (double) 40;

    public static final int DAYS_RELEVANT_FOR_UNRESOLVED = 90;
    public static final String USER_NAME = "shay";

    @Mock
    public AlertsService alertsService;

    @Mock
    public UserRepository userRepository;

    @InjectMocks
    public UserScoreServiceImpl userScoreService;

    @InjectMocks
    public UserUpdateScoreServiceImpl userUpdateScoreService;

    @Before
    public void setUp(){
        userScoreService.setDaysRelevantForUnresolvedAlerts(DAYS_RELEVANT_FOR_UNRESOLVED);
        Map alertSeverityToUserScoreContribution = alertSeverityToUserScoreContribution=new HashMap<>();
        alertSeverityToUserScoreContribution.put(Severity.Low, LOW_ALERT_INFLUANCE);
        alertSeverityToUserScoreContribution.put(Severity.Medium, MEDIUM_ALERT_INFLUANCE);
        alertSeverityToUserScoreContribution.put(Severity.High, HIGH_ALERT_INFLUANCE);
        alertSeverityToUserScoreContribution.put(Severity.Critical, CRITICAL_ALERT_INFLUANCE);
        userScoreService.setAlertSeverityToUserScoreContribution(alertSeverityToUserScoreContribution);
        userUpdateScoreService.setUserScoreService(userScoreService);
    }


    @Test
    public void alertWithUserScoreApprovedTest(){


        Mockito.when(alertsService.getAlertsByUsername(USER_NAME)).thenReturn(Arrays.asList(
                        getAlert(Severity.High, AlertFeedback.Approved,true),
                        getAlert(Severity.Critical,AlertFeedback.Approved,true)
                )
        );

        List<AlertWithUserScore> alerts = userScoreService.getAlertsWithUserScore(USER_NAME);
        Assert.assertEquals(2,alerts.size());


        Assert.assertEquals(HIGH_ALERT_INFLUANCE, alerts.get(0).getScore(),0);
        Assert.assertEquals(CRITICAL_ALERT_INFLUANCE, alerts.get(1).getScore(),0);


    }

    @Test
    public void alertWithUserScoreRejectedTest(){


        Mockito.when(alertsService.getAlertsByUsername(USER_NAME)).thenReturn(Arrays.asList(
                        getAlert(Severity.High, AlertFeedback.Rejected,true),
                        getAlert(Severity.Critical,AlertFeedback.Rejected,true)
                )
        );

        List<AlertWithUserScore> alerts = userScoreService.getAlertsWithUserScore(USER_NAME);
        Assert.assertEquals(2,alerts.size());

        //Rejected always return 0;
        Assert.assertEquals(0, alerts.get(0).getScore(),0);
        Assert.assertEquals(0, alerts.get(1).getScore(),0);


    }

    @Test
    public void alertWithUserScoreUnresolvedTest(){


        Mockito.when(alertsService.getAlertsByUsername(USER_NAME)).thenReturn(Arrays.asList(
                        getAlert(Severity.High, AlertFeedback.None,true),
                        getAlert(Severity.Critical,AlertFeedback.None,false)
                )
        );

        List<AlertWithUserScore> alerts = userScoreService.getAlertsWithUserScore(USER_NAME);
        Assert.assertEquals(2,alerts.size());

        //Rejected always return 0;
        Assert.assertEquals(0, alerts.get(0).getScore(),0);
        Assert.assertEquals(CRITICAL_ALERT_INFLUANCE, alerts.get(1).getScore(),0);


    }

    @Test
    public void recalculateUserScoreTest(){
        Mockito.when(alertsService.getAlertsByUsername(USER_NAME)).thenReturn(Arrays.asList(
                        getAlert(Severity.High, AlertFeedback.Approved, true), // Contribute 30
                        getAlert(Severity.High, AlertFeedback.Approved, true), // Contribute 30
                        getAlert(Severity.Critical, AlertFeedback.Approved, true), // Contribute 40
                        getAlert(Severity.Critical, AlertFeedback.Approved, true), // Contribute 40
                        getAlert(Severity.Critical, AlertFeedback.Rejected, true), // Contribute 0
                        getAlert(Severity.Low, AlertFeedback.Rejected, true), // Contribute 0
                        getAlert(Severity.Medium, AlertFeedback.Approved, true), // Contribute 10
                        getAlert(Severity.Low, AlertFeedback.Approved, true) // Contribute 20
                )
        );
        double expectedScore = HIGH_ALERT_INFLUANCE * 2 + CRITICAL_ALERT_INFLUANCE * 2+ LOW_ALERT_INFLUANCE + MEDIUM_ALERT_INFLUANCE;

        User u = new User();
        u.setUsername(USER_NAME);
        Mockito.when(userRepository.findByUsername(USER_NAME)).thenReturn(u);

        double score = userUpdateScoreService.recalculateUserScore(USER_NAME);
        Assert.assertEquals(expectedScore, score,0);
        //Check that the user updated
        Assert.assertEquals(expectedScore, u.getScore(),0);

        //Verify that the user saved with the new score
        ArgumentCaptor<User> capture = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository,Mockito.times(1)).save(capture.capture());
        Assert.assertEquals(expectedScore, capture.getValue().getScore(),0);
        Assert.assertEquals(USER_NAME, capture.getValue().getUsername());




    }

    private Alert getAlert(Severity s, AlertFeedback feedback, boolean tooOldUnresolved){

        //Calculate Date
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); //Set date to today
        if (tooOldUnresolved) { // If true, we need to generate date before today - DAYS_RELEVANT_FOR_UNRESOLVED
            int daysBeforeToday = DAYS_RELEVANT_FOR_UNRESOLVED + 2;
            c.add(Calendar.DATE, -1 * daysBeforeToday);
        }
        Alert a = new Alert();
        a.setFeedback(feedback);
        a.setSeverity(s);
        a.setStartDate(c.getTime().getTime());
        return a;

    }
}
