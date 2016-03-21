package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.Alert;
import fortscale.domain.core.EntityType;
import fortscale.services.AlertsService;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;
import fortscale.streaming.alert.subscribers.evidence.applicable.EvidencesApplicableToAlertService;
import fortscale.streaming.alert.subscribers.evidence.decider.Decider;
import fortscale.streaming.alert.subscribers.evidence.decider.DeciderCommand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
    private Decider decider;

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


        EnrichedFortscaleEventBuilder enrichedFortscaleEventBuilder = new EnrichedFortscaleEventBuilder();
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

        //Test decider
        ArgumentCaptor<List> decideNameCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(priorityDecider, Mockito.times(1)).getName(Mockito.anyListOf(EnrichedFortscaleEvent.class), Mockito.anyListOf(DeciderCommand.class));

        //Test save alert
        ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        Mockito.verify(alertsService).saveAlertInRepository(alertCaptor.capture());
        Alert actualAlert = alertCaptor.getValue();

        Assert.assertEquals(new Integer(80),actualAlert.getScore());


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
