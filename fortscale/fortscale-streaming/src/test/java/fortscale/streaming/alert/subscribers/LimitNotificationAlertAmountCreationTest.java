package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.Alert;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.EvidenceType;
import fortscale.services.ApplicationConfigurationService;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.applicable.AlertTypesHisotryCache;
import fortscale.streaming.alert.subscribers.evidence.applicable.LimitNotificationAlertAmountCreation;
import fortscale.streaming.alert.subscribers.evidence.decider.DeciderConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by shays on 28/03/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:META-INF/spring/streaming-UnifiedAlertGenerator-test-context.xml")
public class LimitNotificationAlertAmountCreationTest {

    @Autowired
    ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    LimitNotificationAlertAmountCreation limitNotificationAlertAmountCreation;

    @Autowired
    private AlertTypesHisotryCache alertTypesHisotryCache;

    @Autowired
    DeciderConfiguration deciderConfiguration;

    private final static String TYPE1_ALERT_NAME = "type1";
    private final static String TYPE1_ANOMALY_NAME = "type1Anomaly"; //Notification evidence of type1Anomaly generate alert with name type1

    @Before
    public void setUp(){
        //Set maximum daily to 3
        ApplicationConfiguration maxAmountDaily = new ApplicationConfiguration(LimitNotificationAlertAmountCreation.MAX_AMOUNT_OF_NOTIFICATIONS_ALERT_IN_DAY_KEY, "3");
        Mockito.when(applicationConfigurationService.getApplicationConfigurationByKey
                (LimitNotificationAlertAmountCreation.MAX_AMOUNT_OF_NOTIFICATIONS_ALERT_IN_DAY_KEY)).thenReturn(maxAmountDaily);

        //Set maximum hourly to 3
        ApplicationConfiguration maxAmountHourly = new ApplicationConfiguration(LimitNotificationAlertAmountCreation.MAX_AMOUNT_OF_NOTIFICATIONS_ALERT_IN_HOUR_KEY, "3");
        Mockito.when(applicationConfigurationService.getApplicationConfigurationByKey
                (LimitNotificationAlertAmountCreation.MAX_AMOUNT_OF_NOTIFICATIONS_ALERT_IN_HOUR_KEY)).thenReturn(maxAmountHourly);


        deciderConfiguration.getAlertName().put(TYPE1_ANOMALY_NAME, TYPE1_ALERT_NAME);
    }


    /**
     * In this scenario we have one alert with the same type and time as the evidece,
     * so the evidence should not be filtered
     */
    @Test
    public void filter1Test(){

        Long startAlertTime = 1L;
        Long endAlertTime = 2L;

        Alert alert = new Alert();
        alert.setStartDate(startAlertTime);
        alert.setEndDate(endAlertTime);
        alert.setName(TYPE1_ALERT_NAME);

        alertTypesHisotryCache.updateCache(alert);


        //Evidence of type1Anomaly should generate alert with name type1
        EnrichedFortscaleEvent evidence = new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName(TYPE1_ANOMALY_NAME).buildObject();
        boolean shouldNotFilter = limitNotificationAlertAmountCreation.canCreateAlert(evidence, startAlertTime, endAlertTime);

        Assert.assertEquals("The evidence should be filtered, but it did filtered",true, shouldNotFilter);


    }

    /**
     * In this scenario we have one alert with the same type and time as the evidece,
     * so the evidence should not be filtered
     */
    @Test
    public void filter2Test(){

        Long startAlertTime = 1L;
        Long endAlertTime = 2L;

        Alert alert = new Alert();
        alert.setStartDate(startAlertTime);
        alert.setEndDate(endAlertTime);
        alert.setName(TYPE1_ALERT_NAME);

        alertTypesHisotryCache.updateCache(alert);
        alertTypesHisotryCache.updateCache(alert);
        alertTypesHisotryCache.updateCache(alert);


        //Evidence of type1Anomaly should generate alert with name type1
        EnrichedFortscaleEvent evidence = new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName(TYPE1_ANOMALY_NAME).buildObject();
        boolean shouldNotFilter = limitNotificationAlertAmountCreation.canCreateAlert(evidence, startAlertTime, endAlertTime);

        Assert.assertEquals("The evidence should be filtered, but it didn't",false, shouldNotFilter);


    }

    /**
     * In this scenario we have one alert with the same type and time as the evidece,
     * so the evidence should not be filtered
     */
    @Test
    public void filter3Test(){

        Long startAlertTime = 10L;
        Long endAlertTime = 20L;

        Long startAlertTime2 = 20L;
        Long endAlertTime2 = 30L;

        Long startAlertTime3 = 30L;
        Long endAlertTime3 = 40L;

        Alert alert = new Alert();
        alert.setStartDate(startAlertTime);
        alert.setEndDate(endAlertTime);
        alert.setName(TYPE1_ALERT_NAME);

        Alert alert2 = new Alert();
        alert2.setStartDate(startAlertTime2);
        alert2.setEndDate(endAlertTime2);
        alert2.setName(TYPE1_ALERT_NAME);

        Alert alert3 = new Alert();
        alert3.setStartDate(startAlertTime3);
        alert3.setEndDate(endAlertTime3);
        alert3.setName(TYPE1_ALERT_NAME);

        alertTypesHisotryCache.updateCache(alert);
        alertTypesHisotryCache.updateCache(alert2);
        alertTypesHisotryCache.updateCache(alert3);


        //Evidence of type1Anomaly should generate alert with name type1
        EnrichedFortscaleEvent evidence = new EnrichedFortscaleEventBuilder().setAnomalyTypeFieldName(TYPE1_ANOMALY_NAME).buildObject();
        boolean shouldNotFilter = limitNotificationAlertAmountCreation.canCreateAlert(evidence, startAlertTime, endAlertTime);

        Assert.assertEquals("The evidence should not be filtered, but it did filtered",true, shouldNotFilter);


    }


    @Test
    public void filterMatchTest(){
        boolean isMatch = limitNotificationAlertAmountCreation.filterMatch(null, EvidenceType.Notification);
        Assert.assertEquals(true, isMatch);
    }

    @Test
    public void filterNotMatchTest(){
        boolean isMatch = limitNotificationAlertAmountCreation.filterMatch(null, EvidenceType.AnomalyAggregatedEvent);
        Assert.assertEquals(false, isMatch);
    }


}
