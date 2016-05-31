package fortscale.services.impl;

import fortscale.domain.core.*;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.dto.AlertWithUserScore;
import fortscale.services.AlertsService;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
import java.util.Collection;
import java.util.List;

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

        double score = userScoreService.recalculateUserScore(USER_NAME);
        Assert.assertEquals(expectedScore, score,0);
        //Check that the user updated
        Assert.assertEquals(expectedScore, u.getScore(),0);

        //Verify that the user saved with the new score
        ArgumentCaptor<User> capture = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository,Mockito.times(1)).save(capture.capture());
        Assert.assertEquals(expectedScore, capture.getValue().getScore(),0);
        Assert.assertEquals(USER_NAME, capture.getValue().getUsername());




    }

    @Test
    public void simplePercentileCalculationTest(){
        List<Pair<Double, Integer>> scoresToUsersCount= new java.util.ArrayList<>();
        scoresToUsersCount.add(new ImmutablePair<>(5.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(10.0, 100));

        scoresToUsersCount.add(new ImmutablePair<>(15.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(20.0, 100));

        scoresToUsersCount.add(new ImmutablePair<>(25.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(30.0, 100));

        scoresToUsersCount.add(new ImmutablePair<>(35.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(40.0, 100));

        scoresToUsersCount.add(new ImmutablePair<>(45.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(50.0, 100));

        scoresToUsersCount.add(new ImmutablePair<>(55.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(60.0, 100));

        scoresToUsersCount.add(new ImmutablePair<>(70.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(65.0, 100));


        scoresToUsersCount.add(new ImmutablePair<>(80.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(75.0, 100));


        scoresToUsersCount.add(new ImmutablePair<>(85.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(90.0, 100));


        scoresToUsersCount.add(new ImmutablePair<>(95.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(100.0, 100));

        //Expect that each decile will contain 200 users 0-10, 11-20, 21-30... 91..100
        List<UserSingleScorePercentile> m = userScoreService.getOrderdPercentiles(scoresToUsersCount, 10);
        Assert.assertEquals(10, m.size());

        for (int i=1; i<=10; i++){
            UserSingleScorePercentile u = m.get(i-1);
            Assert.assertEquals(i, u.getPercentile());
            Assert.assertEquals((i-1)*10, u.getMinScoreInPerecentile());
            Assert.assertEquals(i*10, u.getMaxScoreInPercentile());

        }



    }

    @Test
    public void advancedPercentileCalculationTest(){
        List<Pair<Double, Integer>> scoresToUsersCount= new java.util.ArrayList<>();
        //300 users between 0-50
        scoresToUsersCount.add(new ImmutablePair<>(0.0, 20));
        scoresToUsersCount.add(new ImmutablePair<>(10.0, 40));
        scoresToUsersCount.add(new ImmutablePair<>(32.0, 50));
        scoresToUsersCount.add(new ImmutablePair<>(38.0, 40));
        scoresToUsersCount.add(new ImmutablePair<>(40.0, 60));
        scoresToUsersCount.add(new ImmutablePair<>(50.0, 90));

        //300 users between 51-70
        scoresToUsersCount.add(new ImmutablePair<>(52.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(68.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(70.0, 100));

        //300 users between 71-80
        scoresToUsersCount.add(new ImmutablePair<>(72.0, 100));
        scoresToUsersCount.add(new ImmutablePair<>(80.0, 200));

        //300 users between 81-85
        scoresToUsersCount.add(new ImmutablePair<>(85-.0, 300));


        //300 users between 86-300
        scoresToUsersCount.add(new ImmutablePair<>(86.0, 20));
        scoresToUsersCount.add(new ImmutablePair<>(120.0, 180));
        scoresToUsersCount.add(new ImmutablePair<>(300.0, 100));



        //Expect that each HAMISHON will contain 300 users -- > 0-50, 51-70, 71-80, 81-85, 86-300
        List<UserSingleScorePercentile> m = userScoreService.getOrderdPercentiles(scoresToUsersCount, 5);
        Assert.assertEquals(5, m.size());

        //Hamishon 1

        Assert.assertEquals(1, m.get(0).getPercentile());
        Assert.assertEquals(0, m.get(0).getMinScoreInPerecentile());
        Assert.assertEquals(50, m.get(0).getMaxScoreInPercentile());

        //Hamishon 2
        Assert.assertEquals(2, m.get(1).getPercentile());
        Assert.assertEquals(50, m.get(1).getMinScoreInPerecentile());
        Assert.assertEquals(70, m.get(1).getMaxScoreInPercentile());

        //Hamishon 3
        Assert.assertEquals(3, m.get(2).getPercentile());
        Assert.assertEquals(70, m.get(2).getMinScoreInPerecentile());
        Assert.assertEquals(80, m.get(2).getMaxScoreInPercentile());

        //Hamishon 4
        Assert.assertEquals(4, m.get(3).getPercentile());
        Assert.assertEquals(80, m.get(3).getMinScoreInPerecentile());
        Assert.assertEquals(85, m.get(3).getMaxScoreInPercentile());

        //Hamishon 5
        Assert.assertEquals(5, m.get(4).getPercentile());
        Assert.assertEquals(85, m.get(4).getMinScoreInPerecentile());
        Assert.assertEquals(300, m.get(4).getMaxScoreInPercentile());


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
