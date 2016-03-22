package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.Alert;
import fortscale.domain.core.AlertStatus;
import fortscale.domain.core.Severity;
import fortscale.services.AlertsService;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.applicable.EvidencesApplicableToAlertService;
import fortscale.streaming.alert.subscribers.evidence.decider.DeciderServiceImpl;
import fortscale.streaming.alert.subscribers.evidence.decider.DeciderCommand;
import net.minidev.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by shays on 20/03/2016.
 * This test tests the creation of alert per user from collection fo smart entities and semantic alerts
 * This test in an integration test which include all the flow, deciders, and filters from the Esper until
 * final Alert is created.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:META-INF/spring/streaming-UnifiedAlertGenerator-test-context.xml")
public class UnifiedAlertIntegrationTest {

    @Autowired
    AlertsService alertsService;

    @Autowired
    private EvidencesApplicableToAlertService evidencesApplicableToAlertService;

    @Autowired
    private DeciderServiceImpl decider;

    @InjectMocks
    @Autowired
    private AlertCreationSubscriber alertCreationSubscriber;

    @Autowired
    @Qualifier("priorityDecider")
    private DeciderCommand priorityDecider;

    @Before
    public void setUp(){
        //Set spy objects

//        evidencesApplicableToAlertService = Mockito.spy(evidencesApplicableToAlertService);
//        alertCreationSubscriber.setEvidencesApplicableToAlertService(evidencesApplicableToAlertService);

        decider = Mockito.spy(decider);
        alertCreationSubscriber.setDecider(decider);

    }


    /**
     * This test accept one smart entity event and create the relevant alert
     */
    @Test
    public void alertCreationSubscriberSmartOnlyTest(){


        /**
         * Init data with one smart
         */
        Map[] insertStream = new Map[1];
        Map[] removeStream = new Map[0];


        EnrichedFortscaleEventBuilder enrichedFortscaleEventBuilder = new EnrichedFortscaleEventBuilder()
                .setAnomalyTypeFieldName("smart")
                .setEntityEventType("normalized_username_hourly")
                .setEntityEventName("normalized_username_hourly")
                .setAggregated_feature_events(getAggregatedFeatureEvents());

        insertStream[0] = createEvidenceWrapper(enrichedFortscaleEventBuilder);

        //Execute
        alertCreationSubscriber.update(insertStream, removeStream);

        List<EnrichedFortscaleEvent> expected = new ArrayList<>();
        expected.add(enrichedFortscaleEventBuilder.buildObject());

        //Test that createIndicatorListApplicableForDecider called one with the right EnrichedFortscaleEvent object.

        ArgumentCaptor<List> applicableCandidatesCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(evidencesApplicableToAlertService, Mockito.times(1)).createIndicatorListApplicableForDecider(applicableCandidatesCaptor.capture());
        Assert.assertEquals(expected.size(), applicableCandidatesCaptor.getValue().size());
        Assert.assertEquals(expected.get(0), applicableCandidatesCaptor.getValue().get(0));

        //Execute createIndicatorListApplicableForDecider and test results
        List<EnrichedFortscaleEvent> newExpected =  evidencesApplicableToAlertService.createIndicatorListApplicableForDecider(expected);
        Assert.assertEquals(expected.size(), newExpected.size());
        Assert.assertEquals(expected.get(0), newExpected.get(0));

        //Test name decider executed
        ArgumentCaptor<List> enrichedFortscaleEventsToNameDecider = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> deciderCommandsCapture = ArgumentCaptor.forClass(List.class);
        Mockito.verify(priorityDecider, Mockito.times(1)).getName(enrichedFortscaleEventsToNameDecider.capture(),deciderCommandsCapture.capture());

        Assert.assertTrue(CollectionUtils.isEqualCollection(deciderCommandsCapture.getValue(), decider.getDecidersLinkedList()));
        Assert.assertEquals(1, enrichedFortscaleEventsToNameDecider.getValue().size());
        Assert.assertEquals(expected.get(0),enrichedFortscaleEventsToNameDecider.getValue().get(0));

        //Test name decider result
        String title = priorityDecider.getName(expected, decider.getDecidersLinkedList());
        Assert.assertEquals("smart",title);

        //Test score decider executed
        enrichedFortscaleEventsToNameDecider = ArgumentCaptor.forClass(List.class); //Init the capture
        deciderCommandsCapture = ArgumentCaptor.forClass(List.class);  //Init the capture
        Mockito.verify(priorityDecider, Mockito.times(1)).getScore(enrichedFortscaleEventsToNameDecider.capture(), deciderCommandsCapture.capture());


        Assert.assertTrue(CollectionUtils.isEqualCollection(deciderCommandsCapture.getValue(), decider.getDecidersLinkedList()));
        Assert.assertEquals(1, enrichedFortscaleEventsToNameDecider.getValue().size());
        Assert.assertEquals(expected.get(0),enrichedFortscaleEventsToNameDecider.getValue().get(0));

        //Test score decider result
        int score = priorityDecider.getScore(expected, decider.getDecidersLinkedList());
        Assert.assertEquals(50,score);

        //Test save alert
        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Mockito.verify(alertsService).saveAlertInRepository(alertCaptor.capture());
        Alert actualAlert = alertCaptor.getValue();


        Assert.assertEquals(new Integer(50),actualAlert.getScore());
        Assert.assertEquals("smart",actualAlert.getName());
        Assert.assertEquals(Severity.Low,actualAlert.getSeverity());
        Assert.assertEquals(AlertStatus.Open,actualAlert.getStatus());
        Assert.assertEquals(expected.get(0).getEntityName(),actualAlert.getEntityName());


    }

    private Map<String, Object> createEvidenceWrapper(EnrichedFortscaleEventBuilder... enrichedFortscaleEventBuilder){

        Map<String, Object> evidenceWrapper = new HashMap<>();
        EnrichedFortscaleEvent regularSmartEvent = enrichedFortscaleEventBuilder[0].buildObject();
        evidenceWrapper.put("title", "Suspicious hourly activity");
        evidenceWrapper.put("entityType", regularSmartEvent.getEntityType());
        evidenceWrapper.put("entityName", regularSmartEvent.getEntityEventName());
        evidenceWrapper.put("startDate", regularSmartEvent.getStartTimeUnix());
        evidenceWrapper.put("endDate", regularSmartEvent.getEndTimeUnix());


        Map[] enrichedFortscaleEvents = new HashMap[enrichedFortscaleEventBuilder.length];
        for (int i=0; i<enrichedFortscaleEventBuilder.length;i++) {

            Map<String, Object> regularSmartEventMap = enrichedFortscaleEventBuilder[i].buildMap();
            enrichedFortscaleEvents[i]=regularSmartEventMap;

        }
        evidenceWrapper.put("idList", enrichedFortscaleEvents);
        return  evidenceWrapper;
    }

    private List<JSONObject> getAggregatedFeatureEvents(){
        List<JSONObject> objects = new ArrayList<>();
        Map<String,Object>  sample = new HashMap<>();

        sample.put("aggregated_feature_type" , "F");
        sample.put("end_time" , "2015-12-13 12:59:59");
        sample.put("creation_epochtime" , 1458523809);
        sample.put("creation_date_time" , "2016-03-21 01:30:09");
        sample.put("data_source" , "aggr_event.normalized_username_kerberos_logins_hourly.distinct_number_of_dst_machines_kerberos_logins_hourly");

        sample.put("data_sources" , new String[]{"kerberos_logins"});

        sample.put("event_type" , "aggr_event");
        sample.put("bucket_conf_name" , "normalized_username_kerberos_logins_hourly");
        sample.put("aggregated_feature_name" , "distinct_number_of_dst_machines_kerberos_logins_hourly");

        sample.put("end_time_unix" , 1450011599);
        sample.put("date_time_unix" , 1450011599);
        sample.put("aggregated_feature_value" , 1.0);
        sample.put("start_time_unix" , 1450008000);


        JSONObject object = new JSONObject();
        object.putAll(sample);

        objects.add(object);
        return  objects;
    }
/*
    @Test
    public void alertCreationSubscriberSmartSemanticTest(){

    }

    @Test
    public void alertCreationSubscriberRegularSemanticTest(){

    }

    @Test
    public void alertCreationSubscriberSmartWithSemanticSmart(){

    }


    @Test
    public void alertCreationSubscriberSmartWithRegularSemantic(){

    }

    @Test
    public void alertCreationSubscriberSmartWithRegularSemanticWithSemanticSmart(){

    }


    @Test
    public void alertCreationSubscriber_TwoSemanticIndicators(){

    }

    @Test
    public void alertCreationSubscriber_TwoSmartSemanticIndicators(){

    }

*/
}
