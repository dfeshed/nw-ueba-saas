package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.AlertFeedback;
import fortscale.domain.core.Severity;
import fortscale.domain.core.User;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by shays on 26/05/2016.
 */

public class UserScoreServiceRecalculateAlertContributionTest extends UserScoreServiceTestAbstract {


    @Test
    public void approvedAlertOnlyConfigurationChanged() {

        //Alert Too old - Alert was not too old before and not too old know
        //Configuration of high severity changed from 20 to HIGH_ALERT_INFLUANCE(30)

        Alert alert = getAlert(Severity.High, AlertFeedback.Approved, false);
        alert.setUserScoreContribution(20.0);
        alert.setUserScoreContributionFlag(true);


        executeRecalculateUserScoreOnAlert(alert, HIGH_ALERT_INFLUANCE, true);

    }

    @Test
    public void unresolvedAlertOnlyConfigurationChanged() {

        //Alert Too old - Alert was not too old before and not too old know
        //Configuration of high severity changed from 20 to HIGH_ALERT_INFLUANCE(30)

        Alert alert = getAlert(Severity.High, AlertFeedback.None, false);
        alert.setUserScoreContribution(20.0);
        alert.setUserScoreContributionFlag(true);

        executeRecalculateUserScoreOnAlert(alert, HIGH_ALERT_INFLUANCE, true);
    }

    @Test
    public void rejectedAlertOnlyConfigurationChanged() {

        //Alert Too old - Alert was not too old before and not too old know
        //Configuration of high severity changed from 20 to HIGH_ALERT_INFLUANCE(30)

        Alert alert = getAlert(Severity.High, AlertFeedback.Rejected, false);
        alert.setUserScoreContribution(0);
        alert.setUserScoreContributionFlag(true);

        executeRecalculateUserScoreOnAlert(alert, 0, false);
    }


    @Test
    public void approvedAlertConfigurationChangedaAndAlertBecameTooOld() {

        //Alert Too old - Alert was not too old before and not too old know
        //Configuration of high severity changed from 20 to HIGH_ALERT_INFLUANCE(30)

        Alert alert = getAlert(Severity.High, AlertFeedback.Approved, true);
        alert.setUserScoreContribution(20.0);
        alert.setUserScoreContributionFlag(true);


        executeRecalculateUserScoreOnAlert(alert, HIGH_ALERT_INFLUANCE, true);

    }

    @Test
    public void unresolvedAlertOnlyConfigurationChangedAndAlertBecameTooOld() {

        //Alert Too old - Alert was not too old before and not too old know
        //Configuration of high severity changed from 20 to HIGH_ALERT_INFLUANCE(30)

        Alert alert = getAlert(Severity.High, AlertFeedback.None, true);
        alert.setUserScoreContribution(20.0);
        alert.setUserScoreContributionFlag(true);

        executeRecalculateUserScoreOnAlert(alert, 20.0, false);
    }


    @Test
    public void rejectedAlertOnlyConfigurationChangedAndAlertBecameTooOld() {

        //Alert Too old - Alert was not too old before and not too old know
        //Configuration of high severity changed from 20 to HIGH_ALERT_INFLUANCE(30)

        Alert alert = getAlert(Severity.High, AlertFeedback.Rejected, true);
        alert.setUserScoreContribution(0);
        alert.setUserScoreContributionFlag(true);

        executeRecalculateUserScoreOnAlert(alert, 0, false);
    }





    private void executeRecalculateUserScoreOnAlert(Alert alert, double expectedUserScoreContribution, boolean expectedFlag) {
        Mockito.when(alertsService.getAlertsRelevantToUserScore(USER_ID)).thenReturn(new HashSet(Arrays.asList(alert)));


        User u = new User();
        u.setUsername(USER_ID);
        Mockito.when(userService.getUserById(USER_ID)).thenReturn(u);

        double score = userScoreService.recalculateUserScore(USER_ID);

        Assert.assertEquals(expectedUserScoreContribution, alert.getUserScoreContribution(), 0);
        Assert.assertEquals(expectedFlag, alert.isUserScoreContributionFlag());
    }


}
