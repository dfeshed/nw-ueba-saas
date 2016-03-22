package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.Alert;
import fortscale.domain.core.AlertStatus;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.Severity;
import fortscale.streaming.alert.event.wrappers.EnrichedFortscaleEvent;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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

    /**
     * This test accept one smart entity event and create the relevant alert
     */
    @Test
    public void alertCreationSubscriberSmartOnlyTest(){


        /**
         * Init data with one smart
         */

        EnrichedFortscaleEventBuilder enrichedFortscaleEventBuilder = new EnrichedFortscaleEventBuilder()
                .setAnomalyTypeFieldName("smart")
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
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, "smart");
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, 50);

        //Verify alert creation save alert
        Alert expectedAlert = new Alert();
        expectedAlert.setScore(50);
        expectedAlert.setName("smart");
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
                .setAnomalyTypeFieldName("vpn_geo_hopping")
                .setEntityEventType("normalized_username_hourly")
                .setEntityEventName("normalized_username_hourly")
                .setScore(30)
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
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, 30);

        //Verify alert creation save alert

        Alert expectedAlert = new Alert();
        expectedAlert.setScore(30);
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
                .setAnomalyTypeFieldName("smart")
                .setEntityEventType("normalized_username_hourly")
                .setEntityEventName("normalized_username_hourly")
                .setAggregated_feature_events(unifiedAlertIntegrationTestHelper.getAggregatedFeatureEvents());

        EnrichedFortscaleEventBuilder evidenceBuilder = new EnrichedFortscaleEventBuilder()
                .setAnomalyTypeFieldName("BruteForce")
                .setEntityEventType("normalized_username_hourly")
                .setEntityEventName("normalized_username_hourly")
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
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, "BruteForce");
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, 50);

        //Verify alert creation save alert

        Alert expectedAlert = new Alert();
        expectedAlert.setScore(50);
        expectedAlert.setName("BruteForce");
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
                .setAnomalyTypeFieldName("smart")
                .setEntityEventType("normalized_username_hourly")
                .setEntityEventName("normalized_username_hourly")
                .setAggregated_feature_events(unifiedAlertIntegrationTestHelper.getAggregatedFeatureEvents());

        EnrichedFortscaleEventBuilder evidenceBuilder = new EnrichedFortscaleEventBuilder()
                .setAnomalyTypeFieldName("BruteForce")
                .setEntityEventType("normalized_username_hourly")
                .setEntityEventName("normalized_username_hourly")
                .setScore(70)
                .setAggregated_feature_events(unifiedAlertIntegrationTestHelper.getAggregatedFeatureEvents());
        EnrichedFortscaleEventBuilder geoHoppingBuilder = new EnrichedFortscaleEventBuilder()
                .setAnomalyTypeFieldName("vpn_geo_hopping")
                .setEntityEventType("normalized_username_hourly")
                .setEntityEventName("normalized_username_hourly")
                .setScore(30)
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
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, "BruteForce");
        unifiedAlertIntegrationTestHelper.assertScoreDecider(expected, 50);

        //Verify alert creation save alert

        Alert expectedAlert = new Alert();
        expectedAlert.setScore(50);
        expectedAlert.setName("BruteForce");
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
