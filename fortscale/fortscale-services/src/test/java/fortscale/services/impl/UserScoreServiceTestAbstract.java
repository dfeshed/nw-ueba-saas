package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.AlertsRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.AlertsService;
import fortscale.services.configuration.Impl.UserScoreConfiguration;
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

    /**
     * Default values  of mapiing percentiles to user severity.
     * Users with user score between - MIN_PERCENTIL_USER_SEVERITY_LOW_DEFAULT - MIN_PERCENTIL_USER_SEVERITY_MEDIUM_DEFAULT will get low severity
     * Users with user score between - MIN_PERCENTIL_USER_SEVERITY_MEDIUM_DEFAULT - MIN_PERCENTIL_USER_SEVERITY_HIGH_DEFAULT will get medium severity
     * Users with user score between - MIN_PERCENTIL_USER_SEVERITY_HIGH_DEFAULT - MIN_PERCENTIL_USER_SEVERITY_CRITICAL_DEFAULT will get high severity
     * Users with user score between - MIN_PERCENTIL_USER_SEVERITY_CRITICAL_DEFAULT - 100 will get critical severity
     */
    public static final double MIN_PERCENTIL_USER_SEVERITY_LOW_DEFAULT = (double) 0;
    public static final double MIN_PERCENTIL_USER_SEVERITY_MEDIUM_DEFAULT = (double) 50;
    public static final double MIN_PERCENTIL_USER_SEVERITY_HIGH_DEFAULT = (double) 80;
    public static final double MIN_PERCENTIL_USER_SEVERITY_CRITICAL_DEFAULT = (double) 95;

    public static final int DAYS_RELEVANT_FOR_UNRESOLVED = 90;
    public static final String USER_NAME = "user123";

    @Mock
    public AlertsService alertsService;

    @Mock
    public UserRepository userRepository;

    @Mock
    public AlertsRepository alertsRepository;

    @InjectMocks
    public UserScoreServiceImpl userScoreService;

    @Before
    public void setUp(){
        UserScoreConfiguration userScoreConfiguration = new UserScoreConfiguration();
        userScoreConfiguration.setContributionOfCriticalSeverityAlert(CRITICAL_ALERT_INFLUANCE);
        userScoreConfiguration.setContributionOfHighSeverityAlert(HIGH_ALERT_INFLUANCE);
        userScoreConfiguration.setContributionOfMediumSeverityAlert(MEDIUM_ALERT_INFLUANCE);
        userScoreConfiguration.setContributionOfLowSeverityAlert(LOW_ALERT_INFLUANCE);
        userScoreConfiguration.setDaysRelevantForUnresolvedAlerts(DAYS_RELEVANT_FOR_UNRESOLVED);

        userScoreConfiguration.setMinPercentileForUserSeverityCritical(MIN_PERCENTIL_USER_SEVERITY_CRITICAL_DEFAULT);
        userScoreConfiguration.setMinPercentileForUserSeverityHigh(MIN_PERCENTIL_USER_SEVERITY_HIGH_DEFAULT);
        userScoreConfiguration.setMinPercentileForUserSeverityMedium(MIN_PERCENTIL_USER_SEVERITY_MEDIUM_DEFAULT);
        userScoreConfiguration.setMinPercentileForUserSeverityLow(MIN_PERCENTIL_USER_SEVERITY_LOW_DEFAULT);

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
