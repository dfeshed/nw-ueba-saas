package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.core.dao.AlertsRepository;
import fortscale.domain.dto.DailySeveiryConuntDTO;
import fortscale.domain.dto.DateRange;
import fortscale.domain.dto.SeveritiesCountDTO;
import fortscale.services.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by shays on 24/05/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class AlertServiceTest {

    /**
     * Mongo repository for alerts
     */
    @Mock
    private AlertsRepository alertsRepository;

    /**
     * Mongo repository for users
     */
    @Mock
    private UserService userService;

    @InjectMocks
    AlertsServiceImpl alertsService;

    @Before
    public void setUp(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void testGetAlertsCountByDayAndSeverityEmptyResults(){
        //Set from April 07 2016 to April 10 2016
        DateRange range = new DateRange(1459988200L,1460332799L);
        //Mockito.when(alertsService.getAlertsCountByDayAndSeverity(range)).thenReturn(new ArrayList<DailySeveiryConuntDTO>());
        List<Alert> fakeAlerts = new ArrayList<>();
        Mockito.when(alertsRepository.getAlertsByTimeRange(Mockito.any(DateRange.class), Mockito.anyList())).
                thenReturn(fakeAlerts);

        List<DailySeveiryConuntDTO> response = alertsService.getAlertsCountByDayAndSeverity(range);
        Assert.assertEquals(4, response.size()); //4 days, one item in array list for each day

        Assert.assertEquals(1459987200000L,response.get(0).getDay());
        Assert.assertEquals(1460073600000L,response.get(1).getDay());
        Assert.assertEquals(1460160000000L,response.get(2).getDay());
        Assert.assertEquals(1460246400000L,response.get(3).getDay());

        for (DailySeveiryConuntDTO  dailySeveiryConunt:response){
            Assert.assertEquals(0,dailySeveiryConunt.getSeverities().get(0).getCount());
            Assert.assertEquals(Severity.Critical,dailySeveiryConunt.getSeverities().get(0).getSeverity());

            Assert.assertEquals(0,dailySeveiryConunt.getSeverities().get(1).getCount());
            Assert.assertEquals(Severity.High,dailySeveiryConunt.getSeverities().get(1).getSeverity());

            Assert.assertEquals(0,dailySeveiryConunt.getSeverities().get(2).getCount());
            Assert.assertEquals(Severity.Medium,dailySeveiryConunt.getSeverities().get(2).getSeverity());

            Assert.assertEquals(0,dailySeveiryConunt.getSeverities().get(3).getCount());
            Assert.assertEquals(Severity.Low,dailySeveiryConunt.getSeverities().get(3).getSeverity());


        }


    }


    @Test
    public void testGetAlertsCountByDayAndSeverityAlerts(){
        //Set from April 07 2016 to April 10 2016
        DateRange range = new DateRange(1459988200L,1460332799L);
        //Mockito.when(alertsService.getAlertsCountByDayAndSeverity(range)).thenReturn(new ArrayList<DailySeveiryConuntDTO>());
        List<Alert> fakeAlerts = Arrays.asList(
                //First day has 4 alerts
                getAlert(1459987210000L, Severity.Low),
                getAlert(1459987220000L, Severity.High),
                getAlert(1459987230000L, Severity.High),
                getAlert(1459987240000L, Severity.High),

                //Last day has 1 alert
                getAlert(1460246600000L, Severity.Critical)

        );
        Mockito.when(alertsRepository.getAlertsByTimeRange(Mockito.any(DateRange.class), Mockito.anyList())).
                thenReturn(fakeAlerts);

        List<DailySeveiryConuntDTO> response = alertsService.getAlertsCountByDayAndSeverity(range);
        Assert.assertEquals(4, response.size()); //4 days, one item in array list for each day



        //Test that first day has 1 low severity and 3 high severities
        DailySeveiryConuntDTO firstDaySeveritiesCount = response.get(0);
        Assert.assertEquals(1459987200000L,firstDaySeveritiesCount.getDay());
        Assert.assertEquals(0,firstDaySeveritiesCount.getSeverities().get(0).getCount()); //Critical count
        Assert.assertEquals(3,firstDaySeveritiesCount.getSeverities().get(1).getCount()); //High Count
        Assert.assertEquals(0,firstDaySeveritiesCount.getSeverities().get(2).getCount()); //Medium Count
        Assert.assertEquals(1,firstDaySeveritiesCount.getSeverities().get(3).getCount()); //Low Count

        //Test that second day has count=0 for each severity
        DailySeveiryConuntDTO secondDaySeveritiesCount = response.get(1);
        Assert.assertEquals(1460073600000L,secondDaySeveritiesCount.getDay());
        Assert.assertEquals(0,secondDaySeveritiesCount.getSeverities().get(0).getCount()); //Critical count
        Assert.assertEquals(0,secondDaySeveritiesCount.getSeverities().get(1).getCount()); //High Count
        Assert.assertEquals(0,secondDaySeveritiesCount.getSeverities().get(2).getCount()); //Medium Count
        Assert.assertEquals(0,secondDaySeveritiesCount.getSeverities().get(3).getCount()); //Low Count


        //Test that second day has count=0 for each severity
        DailySeveiryConuntDTO thirdDaySeveritiesCount = response.get(2);
        Assert.assertEquals(1460160000000L,thirdDaySeveritiesCount.getDay());
        Assert.assertEquals(0,thirdDaySeveritiesCount.getSeverities().get(0).getCount()); //Critical count
        Assert.assertEquals(0,thirdDaySeveritiesCount.getSeverities().get(1).getCount()); //High Count
        Assert.assertEquals(0,thirdDaySeveritiesCount.getSeverities().get(2).getCount()); //Medium Count
        Assert.assertEquals(0,thirdDaySeveritiesCount.getSeverities().get(3).getCount()); //Low Count


        //Test that first day has 1 critical severity
        DailySeveiryConuntDTO lastDaySeveritiesCount = response.get(3);
        Assert.assertEquals(1460246400000L,lastDaySeveritiesCount.getDay());
        Assert.assertEquals(1,lastDaySeveritiesCount.getSeverities().get(0).getCount()); //Critical count
        Assert.assertEquals(0,lastDaySeveritiesCount.getSeverities().get(1).getCount()); //High Count
        Assert.assertEquals(0,lastDaySeveritiesCount.getSeverities().get(2).getCount()); //Medium Count
        Assert.assertEquals(0,lastDaySeveritiesCount.getSeverities().get(3).getCount()); //Low Count




    }

    private Alert getAlert(long startTime, Severity severity){
        Alert a = new Alert();
        a.setStartDate(startTime);
        a.setSeverity(severity);
        return  a;
    }

}
