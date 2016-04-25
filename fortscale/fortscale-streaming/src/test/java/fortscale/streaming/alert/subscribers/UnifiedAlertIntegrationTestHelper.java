package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.Alert;
import fortscale.services.AlertsService;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.applicable.AlertFilterApplicableEvidencesService;
import fortscale.streaming.alert.subscribers.evidence.decider.AlertDeciderService;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shays on 20/03/2016.

 */


public class UnifiedAlertIntegrationTestHelper {


    @Autowired
    private AlertFilterApplicableEvidencesService evidencesApplicableToAlertService;

    @Autowired
    private AlertDeciderService decider;



    @Autowired
    AlertsService alertsService;


    /**
     * This method assert that createIndicatorListApplicableForDecider has been called with the original list of EnrichedFortscaleEvent
     * which arrived to the subscriber.
     * After that, this method test that the method return EnrichedFortscaleEvent as provided in expectedToReturnFromMethod
     * @param expectedToSendToMethod
     * @param expectedToReturnFromMethod
     */
    public void assertCreateIndicatorListApplicableForDecider(List<EnrichedFortscaleEvent> expectedToSendToMethod, List<EnrichedFortscaleEvent> expectedToReturnFromMethod){

        //Test that createIndicatorListApplicableForDecider called one with the right EnrichedFortscaleEvent object.
        ArgumentCaptor<List> applicableCandidatesCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(evidencesApplicableToAlertService, Mockito.atLeast(1)).createIndicatorListApplicableForDecider(
                applicableCandidatesCaptor.capture(), Mockito.anyLong(),Mockito.anyLong());
        Assert.assertEquals("Wrong number of EnrichedFortscaleEvent sent to createIndicatorListApplicableForDecider",expectedToSendToMethod.size(), applicableCandidatesCaptor.getValue().size());
        for (int i=0; i<expectedToSendToMethod.size();i++){
            Assert.assertEquals("EnrichedFortscaleEvent - "+i+" was different that expected",expectedToSendToMethod.get(i), applicableCandidatesCaptor.getValue().get(i));
        }


        //Execute createIndicatorListApplicableForDecider and test results
        List<EnrichedFortscaleEvent> newExpected =  evidencesApplicableToAlertService.createIndicatorListApplicableForDecider
                                                (expectedToSendToMethod,0L, 0L);
        Assert.assertEquals("Wrong number of EnrichedFortscaleEvent returned from to createIndicatorListApplicableForDecider",expectedToReturnFromMethod.size(), newExpected.size());
        for (int i=0; i<expectedToReturnFromMethod.size();i++){
            Assert.assertEquals("EnrichedFortscaleEvent- "+i+" returned from createIndicatorListApplicableForDecider was different that expected",expectedToReturnFromMethod.get(i), applicableCandidatesCaptor.getValue().get(i));
        }

    }

    /**
     *
     * This method test that the nameDecider called with decider.getDecidersLinkedList()
     * and expectedToSendToMethod.
     * After that it test that the decider return the appropiate alert name as defined in expectedAlertName
     *
     * @param expectedToSendToMethod
     * @param expectedAlertName
     */
    public void assertScoreDecider(List<EnrichedFortscaleEvent> expectedToSendToMethod, String expectedAlertName){
        //Test name decider executed
        ArgumentCaptor<List> enrichedFortscaleEventsToNameDecider = ArgumentCaptor.forClass(List.class);
        Mockito.verify(decider, Mockito.atLeast(1)).decideName(enrichedFortscaleEventsToNameDecider.capture());


        Assert.assertEquals(expectedToSendToMethod.size(), enrichedFortscaleEventsToNameDecider.getValue().size());
        for (int i=0; i<expectedToSendToMethod.size();i++) {
            Assert.assertEquals("EnrichedFortscaleEvent - "+i+" was different that expected",expectedToSendToMethod.get(i), enrichedFortscaleEventsToNameDecider.getValue().get(i));
        }

        //Test name decider result
        String title = decider.decideName(expectedToSendToMethod);
        Assert.assertEquals("Title name was different then expected",expectedAlertName,title);
    }

    /**
     *
     * This method test that the scoreDecider called with decider.getDecidersLinkedList()
     * and expectedToSendToMethod.
     * After that it test that the decider return the appropiate score as defined in expectedScore
     *
     * @param expectedToSendToMethod
     * @param expectedScore
     */
    public void assertScoreDecider(List<EnrichedFortscaleEvent> expectedToSendToMethod, int expectedScore){
        //Test score decider executed
        ArgumentCaptor<List> enrichedFortscaleEventsToNameDecider = ArgumentCaptor.forClass(List.class);
        Mockito.verify(decider, Mockito.atLeastOnce()).decideScore(enrichedFortscaleEventsToNameDecider.capture());


        Assert.assertEquals(expectedToSendToMethod.size(), enrichedFortscaleEventsToNameDecider.getValue().size());

        for (int i=0; i<expectedToSendToMethod.size();i++) {
            Assert.assertEquals(expectedToSendToMethod.get(i), enrichedFortscaleEventsToNameDecider.getValue().get(i));
        }
        //Test score decider result
        int score = decider.decideScore(expectedToSendToMethod);
        Assert.assertEquals(expectedScore,score);
    }

    public Map<String, Object> createEvidenceWrapper(EnrichedFortscaleEventBuilder... enrichedFortscaleEventBuilder){

        Map<String, Object> evidenceWrapper = new HashMap<>();
        EnrichedFortscaleEvent regularSmartEvent = enrichedFortscaleEventBuilder[0].buildObject();
        evidenceWrapper.put("title", "Suspicious hourly activity");
        evidenceWrapper.put("entityType", regularSmartEvent.getEntityType());
        evidenceWrapper.put("entityName", regularSmartEvent.getEntityName());
        evidenceWrapper.put("startDate", regularSmartEvent.getStartTimeUnix());
        evidenceWrapper.put("endDate", regularSmartEvent.getEndTimeUnix());


        Map[] enrichedFortscaleEvents = new HashMap[enrichedFortscaleEventBuilder.length];
        for (int i=0; i<enrichedFortscaleEventBuilder.length;i++) {

            Map<String, Object> regularSmartEventMap = enrichedFortscaleEventBuilder[i].buildMap();
            enrichedFortscaleEvents[i]=regularSmartEventMap;

        }
        evidenceWrapper.put("eventList", enrichedFortscaleEvents);
        return  evidenceWrapper;
    }

    public List<JSONObject> getAggregatedFeatureEvents(){
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

    /**
     * This method test that saveAlertInRepository has been called with expected alert
     * @param expectedAlert
     */
    public void assertAlertCreation(Alert expectedAlert) {

        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Mockito.verify(alertsService,Mockito.atLeastOnce()).saveAlertInRepository(alertCaptor.capture());
        Alert actualAlert = alertCaptor.getValue();


        Assert.assertEquals(expectedAlert.getScore(), actualAlert.getScore());
        Assert.assertEquals(expectedAlert.getName(),actualAlert.getName());
        Assert.assertEquals(expectedAlert.getSeverity(),actualAlert.getSeverity());
        Assert.assertEquals(expectedAlert.getStatus(),actualAlert.getStatus());
        Assert.assertEquals(expectedAlert.getEntityName(), actualAlert.getEntityName());
        Assert.assertEquals(expectedAlert.getEntityType(), actualAlert.getEntityType());
    }

}
