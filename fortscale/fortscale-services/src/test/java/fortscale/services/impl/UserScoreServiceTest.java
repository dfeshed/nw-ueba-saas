package fortscale.services.impl;

import com.google.common.collect.Lists;
import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.dto.AlertWithUserScore;
import fortscale.services.AlertsService;
import fortscale.services.UserUpdateScoreService;
import fortscale.services.domain.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shays on 26/05/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserScoreServiceTest {

    @Mock
    public AlertsService alertsService;

    @InjectMocks
    public UserUpdateScoreServiceImpl userUpdateScoreService;

    @InjectMocks
    public UserScoreServiceImpl userScoreService;

    @Before
    public void setUp(){
        userScoreService.setDaysRelevantForUnresolvedAlerts(90);
        Map alertSeverityToUserScoreContribution = alertSeverityToUserScoreContribution=new HashMap<>();
        alertSeverityToUserScoreContribution.put(Severity.Low,(double)10);
        alertSeverityToUserScoreContribution.put(Severity.Low,(double)20);
        alertSeverityToUserScoreContribution.put(Severity.Low,(double)30);
        alertSeverityToUserScoreContribution.put(Severity.Low,(double)40);
        userScoreService.setAlertSeverityToUserScoreContribution(alertSeverityToUserScoreContribution);
    }

    @Test
    public void alertWithUserScoreTest(){


        Mockito.when(alertsService.getAlertsByUsername("shay")).thenReturn(Arrays.asList(
                        new Alert())
        );

        List<AlertWithUserScore> alerts = userScoreService.getAlertsWithUserScore("shay");


    }

}
