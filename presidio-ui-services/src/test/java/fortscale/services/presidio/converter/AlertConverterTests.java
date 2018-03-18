package fortscale.services.presidio.converter;

import fortscale.domain.core.*;
import fortscale.domain.dto.DateRange;
import fortscale.domain.rest.AlertRestFilter;
import fortscale.services.presidio.core.converters.AlertConverterHelper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import presidio.output.client.model.AlertQuery;
import presidio.output.client.model.Indicator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shays on 11/09/2017.
 */
public class AlertConverterTests {

    AlertConverterHelper alertConverterHelper = new AlertConverterHelper();

    @Test
    public void testConvertAlertFromResponseToUiAlert(){
        Alert uiAlerts;
        presidio.output.client.model.Alert alertFromResponse = new presidio.output.client.model.Alert();
        alertFromResponse.setScore(30);
        alertFromResponse.setStartDate(new BigDecimal(1499817600));
        alertFromResponse.setEndDate(new BigDecimal(1429817600));
        alertFromResponse.setClassifiation(Arrays.asList("name1","name2"));
        alertFromResponse.setFeedback(presidio.output.client.model.Alert.FeedbackEnum.RISK);
        alertFromResponse.setId("blaIdId");
        alertFromResponse.setTimeframe(presidio.output.client.model.Alert.TimeframeEnum.HOURLY);
        alertFromResponse.setIndicatorsNum(3);
        alertFromResponse.setUserScoreContribution(new BigDecimal(50));
        alertFromResponse.setSeverity(presidio.output.client.model.Alert.SeverityEnum.LOW);
        alertFromResponse.setUsername("userName");
        alertFromResponse.setUserId("userId");

        uiAlerts=alertConverterHelper.convertResponseToUiDto(alertFromResponse);

        Assert.assertEquals(30,uiAlerts.getScore().intValue());
        Assert.assertEquals(1499817600000L,uiAlerts.getStartDate());
        Assert.assertEquals(1429817600000L,uiAlerts.getEndDate());
        Assert.assertEquals("name1",uiAlerts.getName());
        Assert.assertEquals(AlertFeedback.Approved,uiAlerts.getFeedback());
        Assert.assertEquals(AlertStatus.Closed,uiAlerts.getStatus());
        Assert.assertEquals("blaIdId",uiAlerts.getId());
        Assert.assertEquals(AlertTimeframe.Hourly,uiAlerts.getTimeframe());
        Assert.assertEquals(3,uiAlerts.getEvidenceSize().intValue());
        Assert.assertEquals(50D,uiAlerts.getUserScoreContribution(),0.0001);
        Assert.assertEquals(Severity.Low,uiAlerts.getSeverity());
        Assert.assertEquals("userId",uiAlerts.getEntityId());


    }

    @Test
    public void testConvertAlertFromResponseToUiAlertEmpties(){

        Alert uiAlerts;
        presidio.output.client.model.Alert alertFromResponse = new presidio.output.client.model.Alert();
        alertFromResponse.setScore(30);
        alertFromResponse.setStartDate(new BigDecimal(1499817600));
        alertFromResponse.setEndDate(new BigDecimal(1429817600));
        alertFromResponse.setClassifiation(Arrays.asList("name1","name2"));
        alertFromResponse.setFeedback(null);
        alertFromResponse.setId("blaIdId");
        alertFromResponse.setTimeframe(presidio.output.client.model.Alert.TimeframeEnum.HOURLY);
        alertFromResponse.setIndicators(Arrays.asList(getTestIndicator(),getTestIndicator()));
        alertFromResponse.setIndicatorsNum(2);

        alertFromResponse.setSeverity(null);
        alertFromResponse.setUsername(null);
        alertFromResponse.setUserId("userId");

        uiAlerts=alertConverterHelper.convertResponseToUiDto(alertFromResponse);

        Assert.assertEquals(30,uiAlerts.getScore().intValue());
        Assert.assertEquals(1499817600000L,uiAlerts.getStartDate());
        Assert.assertEquals(1429817600000L,uiAlerts.getEndDate());
        Assert.assertEquals("name1",uiAlerts.getName());
        Assert.assertEquals(AlertFeedback.None,uiAlerts.getFeedback());
        Assert.assertEquals(AlertStatus.Open,uiAlerts.getStatus());
        Assert.assertEquals("blaIdId",uiAlerts.getId());
        Assert.assertEquals(AlertTimeframe.Hourly,uiAlerts.getTimeframe());
        Assert.assertEquals(2,uiAlerts.getEvidenceSize().intValue());

        Assert.assertEquals(null,uiAlerts.getSeverity());
        Assert.assertEquals("userId",uiAlerts.getEntityId());


    }

    private Indicator getTestIndicator(){
        Indicator indicator = new Indicator();
        indicator.setName("name");
        indicator.setScore(50D);
        indicator.setStartDate(new BigDecimal(1505311882));
        indicator.setAnomalyValue("value");
        indicator.setEventsNum(5);
        indicator.setSchema("FILE");
        indicator.setEndDate(new BigDecimal(1505311882));
        indicator.setId("id");
        return  indicator;
    }


    @Test
    public void testAlertQueryPagination(){

        PageRequest pageRequest = new PageRequest(0,100);
        AlertQuery alertQuery = alertConverterHelper.convertUiFilterToQueryDto(pageRequest,null,null,null,null,null,null,null,null,true);
        Assert.assertEquals(100,alertQuery.getPageSize().intValue());
        Assert.assertEquals(0,alertQuery.getPageNumber().intValue());


    }

    @Test
    public void testAlertQuerySorting(){

    }

    @Test
    public void testAlertQueryDates(){
        DateRange dateRange = new DateRange();
        dateRange.setFromTime(1499817600000L);
        dateRange.setToTime(1505087999000L);
        AlertQuery alertQuery = alertConverterHelper.convertUiFilterToQueryDto(null,null,null,null,dateRange
                ,null,null,null,null,true);
        Assert.assertEquals(1499817600000L,alertQuery.getStartTimeFrom().longValue());
        Assert.assertEquals(1505087999000L,alertQuery.getStartTimeTo().longValue());

    }

    @Test
    public void testAlertQuerySeverityAndFeedback(){
        String severity = "Low,High,Medium";
        String status="Open";

        AlertQuery alertQuery = alertConverterHelper.convertUiFilterToQueryDto(null,severity,status,null,null
                ,null,null,null,null,false);
        Assert.assertEquals(3,alertQuery.getSeverity().size());
        Assert.assertTrue(alertQuery.getSeverity().contains(AlertQuery.SeverityEnum.LOW));
        Assert.assertTrue(alertQuery.getSeverity().contains(AlertQuery.SeverityEnum.HIGH));
        Assert.assertTrue(alertQuery.getSeverity().contains(AlertQuery.SeverityEnum.MEDIUM));

        Assert.assertEquals(1,alertQuery.getFeedback().size());
        Assert.assertEquals(AlertQuery.FeedbackEnum.NONE,alertQuery.getFeedback().get(0));

    }

    @Test
    public void testAlertQueryFeedback(){
        String feedback = "Approved,None";


        AlertQuery alertQuery = alertConverterHelper.convertUiFilterToQueryDto(null,null,null,feedback,null
                ,null,null,null,null,false);


        Assert.assertEquals(2,alertQuery.getFeedback().size());
        Assert.assertTrue(alertQuery.getFeedback().contains(AlertQuery.FeedbackEnum.NONE));
        Assert.assertTrue(alertQuery.getFeedback().contains(AlertQuery.FeedbackEnum.RISK));


    }

    @Test
    public void testAlertQueryFilterByUserDetails(){

        AlertQuery alertQuery = alertConverterHelper.convertUiFilterToQueryDto(null,null,null,null,null
                ,"user1,user2","any","id1,id2",null,false);

        Assert.assertEquals("admin",alertQuery.getTags().get(0));

        Assert.assertEquals(2,alertQuery.getUserName().size());
        Assert.assertTrue(alertQuery.getUserName().contains("user1"));
        Assert.assertTrue(alertQuery.getUserName().contains("user2"));

        Assert.assertEquals(2,alertQuery.getUsersId().size());
        Assert.assertTrue(alertQuery.getUsersId().contains("id1"));
        Assert.assertTrue(alertQuery.getUsersId().contains("id2"));

    }

    @Test
    public void testAlertQueryFilterIndicators(){
        Set<String> indicatorTypes = new HashSet<>();
        indicatorTypes.add("type");
        AlertQuery alertQuery = alertConverterHelper.convertUiFilterToQueryDto(null,null,null,null,null
                ,null,null,null,indicatorTypes,false);


        Assert.assertEquals(1,alertQuery.getIndicatorsName().size());
        Assert.assertTrue(alertQuery.getIndicatorsName().contains("type"));


    }


}
