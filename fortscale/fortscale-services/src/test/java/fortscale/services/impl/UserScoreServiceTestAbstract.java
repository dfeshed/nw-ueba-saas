package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.AlertsService;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by shays on 02/06/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract  class UserScoreServiceTestAbstract {

    public static final double LOW_ALERT_INFLUANCE = (double) 10;
    public static final double MEDIUM_ALERT_INFLUANCE = (double) 20;
    public static final double HIGH_ALERT_INFLUANCE = (double) 30;
    public static final double CRITICAL_ALERT_INFLUANCE = (double) 40;

    public static final int DAYS_RELEVANT_FOR_UNRESOLVED = 90;
    public static final String USER_NAME = "user123";

    @Mock
    public AlertsService alertsService;

    @Mock
    public UserRepository userRepository;

    @InjectMocks
    public UserScoreServiceImpl userScoreService;

    @Before
    public void setUp(){
        UserScoreServiceImpl.UserScoreConfiguration userScoreConfiguration = new UserScoreServiceImpl.UserScoreConfiguration();
        userScoreConfiguration.setContributionOfCriticalSeverityAlert(CRITICAL_ALERT_INFLUANCE);
        userScoreConfiguration.setContributionOfHighSeverityAlert(HIGH_ALERT_INFLUANCE);
        userScoreConfiguration.setContributionOfMediumSeverityAlert(MEDIUM_ALERT_INFLUANCE);
        userScoreConfiguration.setContributionOfLowSeverityAlert(LOW_ALERT_INFLUANCE);
        userScoreConfiguration.setDaysRelevantForUnresolvedAlerts(DAYS_RELEVANT_FOR_UNRESOLVED);

        userScoreService.setUserScoreConfiguration(userScoreConfiguration);



    }

    protected Alert getAlert(Severity s, AlertFeedback feedback, boolean tooOldUnresolved){

        //Calculate Date
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); //Set date to today
        if (tooOldUnresolved) { // If true, we need to generate date before today - DAYS_RELEVANT_FOR_UNRESOLVED
            int daysBeforeToday = DAYS_RELEVANT_FOR_UNRESOLVED + 4;
            c.add(Calendar.DATE, -1 * daysBeforeToday);
        }
        Alert a = new Alert();
        a.setFeedback(feedback);
        a.setSeverity(s);
        a.setStartDate(c.getTime().getTime());

        a.setEntityType(EntityType.User);
        a.setStatus(AlertStatus.Open);
        return a;

    }

}
