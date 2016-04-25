package fortscale.streaming.alert.subscribers;

import fortscale.aggregation.feature.event.AggrEvent;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.domain.core.*;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;


import fortscale.streaming.alert.subscribers.evidence.applicable.LimitGeoHoppingPreAlertCreation;
import fortscale.streaming.service.alert.EvidencesForAlertResolverService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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


    @InjectMocks
    @Autowired
    private AlertCreationSubscriber alertCreationSubscriber;

    @Autowired
    private UnifiedAlertIntegrationTestHelper unifiedAlertIntegrationTestHelper;

    @Autowired
    private EvidencesForAlertResolverService evidencesForAlertResolverService;

    @Autowired
    private LimitGeoHoppingPreAlertCreation limitGeoHoppingPreAlertCreation;

    @Before
    public void setUp() throws Exception {

        //Make sure that we have at least one evidence
        List<Evidence> evidences = Arrays.asList(Mockito.mock(Evidence.class));
        Mockito.when(evidencesForAlertResolverService.handleEntityEvents(Mockito.any())).thenReturn(evidences);
        //This test is not focus on the filters.

        Mockito.doReturn(true).when(limitGeoHoppingPreAlertCreation).canCreateAlert(Mockito.any(), Mockito.anyLong(), Mockito.anyLong());


    }


    /**
     * This test accept one smart entity event and create the relevant alert
     */
    @Test
    public void alertCreationSubscriberSmartOnlyTest(){


        /**
         * Init data with one smart
         */

        EnrichedFortscaleEventBuilder enrichedFortscaleEventBuilder = new EnrichedFortscaleEventBuilder()
                .setAnomalyTypeFieldName("normalized_username_hourly").setEvidenceType(EvidenceType.Smart)
                .setEntityEventType("normalized_username_hourly")
                .setEntityEventName("normalized_username_hourly")
                .setAggregated_feature_events(unifiedAlertIntegrationTestHelper.getAggregatedFeatureEvents());

        Map[] insertStream = new Map[1];
        insertStream[0] = unifiedAlertIntegrationTestHelper.createEvidenceWrapper(enrichedFortscaleEventBuilder);

        //Execute
        alertCreationSubscriber.update(insertStream, null);

        List<EnrichedFortscaleEvent> expected = new ArrayList<>();
        expected.add(enrichedFortscaleEventBuilder.buildObject());

        //Verify flow
        unifiedAlertIntegrationTestHelper.assertCreateIndicatorListApplicableForDecider(expected, expected);
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, "normalized_username_hourly");
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, 50);




        //Verify alert creation save alert
        Alert expectedAlert = new Alert();
        expectedAlert.setScore(50);
        expectedAlert.setName("normalized_username_hourly");
        expectedAlert.setSeverity(Severity.Low);
        expectedAlert.setStatus(AlertStatus.Open);
        expectedAlert.setEntityName("user@fortscale.com");
        expectedAlert.setEntityType(EntityType.User);
        unifiedAlertIntegrationTestHelper.assertAlertCreation(expectedAlert);


    }

/*
    @Test
    public void alertCreationSubscriberSmartSemanticTest(){

    }
*/
    @Test
    public void alertCreationSubscriberRegularSemanticTest(){
        /**
         * Init data with one smart and one semantic smart
         */

        EnrichedFortscaleEventBuilder geoHoppingBuilder = new EnrichedFortscaleEventBuilder()
                .setAnomalyTypeFieldName("vpn_geo_hopping").setEvidenceType(EvidenceType.Notification)
                .setEntityEventType("user")
                .setEntityEventName("fortscale")
                .setScore(60)
                .setAggregated_feature_events(unifiedAlertIntegrationTestHelper.getAggregatedFeatureEvents());

        Map[] insertStream = new Map[1];
        insertStream[0] = unifiedAlertIntegrationTestHelper.createEvidenceWrapper(geoHoppingBuilder);

        //Execute
        alertCreationSubscriber.update(insertStream, null);

        List<EnrichedFortscaleEvent> expected = new ArrayList<>();
        expected.add(geoHoppingBuilder.buildObject());

        //Verify flow
        unifiedAlertIntegrationTestHelper.assertCreateIndicatorListApplicableForDecider(expected, expected);
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, "vpn_geo_hopping");
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, 60);

        //Verify alert creation save alert

        Alert expectedAlert = new Alert();
        expectedAlert.setScore(60);
        expectedAlert.setName("vpn_geo_hopping");
        expectedAlert.setSeverity(Severity.Low);
        expectedAlert.setStatus(AlertStatus.Open);
        expectedAlert.setEntityName("user@fortscale.com");
        expectedAlert.setEntityType(EntityType.User);

        unifiedAlertIntegrationTestHelper.assertAlertCreation(expectedAlert);
    }

    /**
     *  Test following combination
     *  Smart
     *  SemanticSmart
     */
    @Test
    public void alertCreationSubscriberSmartWithSemanticSmart(){
        /**
         * Init data with one smart and one semantic smart
         */
        EnrichedFortscaleEventBuilder smartEventBuilder = new EnrichedFortscaleEventBuilder()
                .setAnomalyTypeFieldName("normalized_username_hourly").setEvidenceType(EvidenceType.Smart)
                .setEntityEventType("normalized_username_hourly")
                .setEntityEventName("normalized_username_hourly")
                .setAggregated_feature_events(unifiedAlertIntegrationTestHelper.getAggregatedFeatureEvents());

        EnrichedFortscaleEventBuilder evidenceBuilder = new EnrichedFortscaleEventBuilder()
                .setAnomalyTypeFieldName("brute_force_normalized_username_hourly").setEvidenceType(EvidenceType.Smart)
                .setEntityEventType("brute_force_normalized_username_hourly")
                .setEntityEventName("brute_force_normalized_username_hourly")
                .setScore(70)
                .setAggregated_feature_events(unifiedAlertIntegrationTestHelper.getAggregatedFeatureEvents());

        Map[] insertStream = new Map[1];
        insertStream[0] = unifiedAlertIntegrationTestHelper.createEvidenceWrapper(smartEventBuilder, evidenceBuilder);

        //Execute
        alertCreationSubscriber.update(insertStream, null);

        List<EnrichedFortscaleEvent> expected = new ArrayList<>();
        expected.add(smartEventBuilder.buildObject());
        expected.add(evidenceBuilder.buildObject());

        //Verify flow
        unifiedAlertIntegrationTestHelper.assertCreateIndicatorListApplicableForDecider(expected, expected);
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, "brute_force_normalized_username_hourly");
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, 50);

        //Verify alert creation save alert

        Alert expectedAlert = new Alert();
        expectedAlert.setScore(50);
        expectedAlert.setName("brute_force_normalized_username_hourly");
        expectedAlert.setSeverity(Severity.Low);
        expectedAlert.setStatus(AlertStatus.Open);
        expectedAlert.setEntityName("user@fortscale.com");
        expectedAlert.setEntityType(EntityType.User);

        unifiedAlertIntegrationTestHelper.assertAlertCreation(expectedAlert);

    }




/*
    @Test
    public void alertCreationSubscriberSmartWithRegularSemantic(){

    }
*/
    @Test
    public void alertCreationSubscriberSmartWithRegularSemanticWithSemanticSmart(){

        /**
         * Init data with one smart and one semantic smart
         */
        EnrichedFortscaleEventBuilder smartEventBuilder = new EnrichedFortscaleEventBuilder()
                .setAnomalyTypeFieldName("normalized_username_hourly").setEvidenceType(EvidenceType.Smart)
                .setEntityEventType("normalized_username_hourly")
                .setEntityEventName("normalized_username_hourly")
                .setAggregated_feature_events(unifiedAlertIntegrationTestHelper.getAggregatedFeatureEvents());

        EnrichedFortscaleEventBuilder evidenceBuilder = new EnrichedFortscaleEventBuilder()
                .setAnomalyTypeFieldName("brute_force_normalized_username_hourly").setEvidenceType(EvidenceType.Smart)
                .setEntityEventType("normalized_username_hourly")
                .setEntityEventName("normalized_username_hourly")
                .setScore(70)
                .setAggregated_feature_events(unifiedAlertIntegrationTestHelper.getAggregatedFeatureEvents());
        EnrichedFortscaleEventBuilder geoHoppingBuilder = new EnrichedFortscaleEventBuilder()
                .setAnomalyTypeFieldName("vpn_geo_hopping").setEvidenceType(EvidenceType.Notification)
                .setEntityEventType("normalized_username_hourly")
                .setEntityEventName("normalized_username_hourly")
                .setScore(51)
                .setAggregated_feature_events(unifiedAlertIntegrationTestHelper.getAggregatedFeatureEvents());

        Map[] insertStream = new Map[1];
        insertStream[0] = unifiedAlertIntegrationTestHelper.createEvidenceWrapper(smartEventBuilder, evidenceBuilder,geoHoppingBuilder);

        //Execute
        alertCreationSubscriber.update(insertStream, null);

        List<EnrichedFortscaleEvent> expected = new ArrayList<>();
        expected.add(smartEventBuilder.buildObject());
        expected.add(evidenceBuilder.buildObject());
        expected.add(geoHoppingBuilder.buildObject());

        //Verify flow
        unifiedAlertIntegrationTestHelper.assertCreateIndicatorListApplicableForDecider(expected, expected);
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, "brute_force_normalized_username_hourly");
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, 50);

        //Verify alert creation save alert

        Alert expectedAlert = new Alert();
        expectedAlert.setScore(50);
        expectedAlert.setName("brute_force_normalized_username_hourly");
        expectedAlert.setSeverity(Severity.Low);
        expectedAlert.setStatus(AlertStatus.Open);
        expectedAlert.setEntityName("user@fortscale.com");
        expectedAlert.setEntityType(EntityType.User);

        unifiedAlertIntegrationTestHelper.assertAlertCreation(expectedAlert);
    }

/*
    @Test
    public void alertCreationSubscriber_TwoSemanticIndicators(){

    }

    @Test
    public void alertCreationSubscriber_TwoSmartSemanticIndicators(){

    }
*/


}
