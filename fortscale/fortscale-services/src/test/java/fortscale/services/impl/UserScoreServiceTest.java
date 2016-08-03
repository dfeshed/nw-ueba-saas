package fortscale.services.impl;


import fortscale.domain.core.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Mockito.verify;

/**
 * Created by shays on 26/05/2016.
 */

public class UserScoreServiceTest extends UserScoreServiceTestAbstract {

    @Test
    public void alertWithUserScoreApprovedTest(){
        Alert a1 =  getAlert(Severity.High, AlertFeedback.Approved,true);
        double a1Contribution = userScoreService.getUserScoreContributionForAlertSeverity(a1.getSeverity(), a1.getFeedback(),a1.getStartDate());
        Alert a2 =  getAlert(Severity.Critical,AlertFeedback.Approved,true);
        double a2Contribution = userScoreService.getUserScoreContributionForAlertSeverity(a2.getSeverity(), a2.getFeedback(),a2.getStartDate());

        Assert.assertEquals(HIGH_ALERT_INFLUANCE, a1Contribution,0);
        Assert.assertEquals(CRITICAL_ALERT_INFLUANCE, a2Contribution, 0);
    }

    @Test
    public void alertWithUserScoreRejectedTest(){
        Alert a1 =  getAlert(Severity.High, AlertFeedback.Rejected,true);
        double a1Contribution = userScoreService.getUserScoreContributionForAlertSeverity(a1.getSeverity(), a1.getFeedback(),a1.getStartDate());
        Alert a2 =   getAlert(Severity.Critical,AlertFeedback.Rejected,true);
        double a2Contribution = userScoreService.getUserScoreContributionForAlertSeverity(a2.getSeverity(), a2.getFeedback(),a2.getStartDate());

        //Rejected always return 0;
        Assert.assertEquals(0, a1Contribution,0);
        Assert.assertEquals(0, a2Contribution,0);
    }

    @Test
    public void alertWithUserScoreUnresolvedTest(){
        Alert a1 =  getAlert(Severity.High, AlertFeedback.None,true);
        double a1Contribution = userScoreService.getUserScoreContributionForAlertSeverity(a1.getSeverity(), a1.getFeedback(),a1.getStartDate());
        Alert a2 =   getAlert(Severity.Critical,AlertFeedback.None,false);
        double a2Contribution = userScoreService.getUserScoreContributionForAlertSeverity(a2.getSeverity(), a2.getFeedback(),a2.getStartDate());

        //Rejected always return 0;
        Assert.assertEquals(0, a1Contribution,0);
        Assert.assertEquals(CRITICAL_ALERT_INFLUANCE, a2Contribution,0);
    }

    @Test
    public void recalculateUserScoreTest() {
        HashSet alertsSet = new HashSet(Arrays.asList(getAlert(Severity.High, AlertFeedback.Approved, true), // Contribute 30
                getAlert(Severity.High, AlertFeedback.Approved, true), // Contribute 30
                getAlert(Severity.Critical, AlertFeedback.Approved, true), // Contribute 40
                getAlert(Severity.Critical, AlertFeedback.Approved, true), // Contribute 40
                getAlert(Severity.Critical, AlertFeedback.Rejected, true), // Contribute 0
                getAlert(Severity.Low, AlertFeedback.Rejected, true), // Contribute 0
                getAlert(Severity.Medium, AlertFeedback.Approved, true), // Contribute 10
                getAlert(Severity.Low, AlertFeedback.Approved, true) // Contribute 20
        ));
        Mockito.when(alertsService.getAlertsRelevantToUserScore(USER_NAME)).thenReturn(alertsSet);
        double expectedScore = HIGH_ALERT_INFLUANCE * 2 + CRITICAL_ALERT_INFLUANCE * 2 + LOW_ALERT_INFLUANCE + MEDIUM_ALERT_INFLUANCE;

        User u = new User();
        u.setUsername(USER_NAME);
        Mockito.when(userRepository.findByUsername(USER_NAME)).thenReturn(u);

        double score = userScoreService.recalculateUserScore(USER_NAME);
        Assert.assertEquals(expectedScore, score, 0);
        //Check that the user updated
        Assert.assertEquals(expectedScore, u.getScore(), 0);

        //Verify that the user saved with the new score
        ArgumentCaptor<User> capture = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1)).save(capture.capture());
        Assert.assertEquals(expectedScore, capture.getValue().getScore(), 0);
        Assert.assertEquals(USER_NAME, capture.getValue().getUsername());

    }



//    protected Alert getAlert(Severity s, AlertFeedback feedback, boolean tooOldUnresolved){
//
//        //Calculate Date
//        Calendar c = Calendar.getInstance();
//        c.setTime(new Date()); //Set date to today
//        if (tooOldUnresolved) { // If true, we need to generate date before today - DAYS_RELEVANT_FOR_UNRESOLVED
//            int daysBeforeToday = DAYS_RELEVANT_FOR_UNRESOLVED + 4;
//            c.add(Calendar.DATE, -1 * daysBeforeToday);
//        }
//        Alert a = new Alert();
//        a.setFeedback(feedback);
//        a.setSeverity(s);
//        a.setStartDate(c.getTime().getTime());
//
//        a.setEntityType(EntityType.User);
//        a.setStatus(AlertStatus.Open);
//        return a;
//
//    }
}
