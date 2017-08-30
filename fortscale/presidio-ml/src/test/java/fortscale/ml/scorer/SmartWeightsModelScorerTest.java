package fortscale.ml.scorer;


import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.SmartWeightsModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.smart.record.conf.ClusterConf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
public class SmartWeightsModelScorerTest {
    @Autowired
    private EventModelsCacheService eventModelsCacheService;

    @Autowired
    private ModelsCacheService modelsCacheService;



    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenEmptyModelName() {
        new SmartWeightsModelScorer( "scorerName","",eventModelsCacheService);
    }

    @Test
    public void testEmptySmartRecordGetZeroScore(){
        SmartRecord emptyMockedSmartRecord = createMockedSmartRecord(Collections.emptyList());
        prepareSmartWeightModel(Collections.emptyList());
        FeatureScore featureScore = calculateScore(emptyMockedSmartRecord);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
    }

//    @Test(expected = IllegalArgumentException.class)
//    public void shouldFailToScoreIfGivenWrongModelType() {
//        String featureName = "F1";
//        prepareFeature(featureName, new JSONArray());
//        prepareModel(new CategoryRarityModel());
//        calculateScore(featureName);
//    }

    private SmartRecord createMockedSmartRecord(List<AdeAggregationRecord> adeAggregationRecords){
        SmartRecord ret = Mockito.mock(SmartRecord.class);
        Mockito.when(ret.getAggregationRecords()).thenReturn(adeAggregationRecords);

        return ret;
    }

    private AdeAggregationRecord createMockedAdeAggregationRecordOfSCORE_AGGREGATIONType(double score){
        AdeAggregationRecord ret = Mockito.mock(AdeAggregationRecord.class);
        Mockito.when(ret.getAggregatedFeatureType()).thenReturn(AggregatedFeatureType.SCORE_AGGREGATION);
        Mockito.when(ret.getFeatureValue()).thenReturn(score);

        return ret;
    }

    private ScoredFeatureAggregationRecord createMockedAdeAggregationRecordOfFEATURE_AGGREGATIONType(double score){
        ScoredFeatureAggregationRecord ret = Mockito.mock(ScoredFeatureAggregationRecord.class);
        Mockito.when(ret.getAggregatedFeatureType()).thenReturn(AggregatedFeatureType.FEATURE_AGGREGATION);
        Mockito.when(ret.getFeatureValue()).thenReturn(score);
        return ret;
    }

    private void prepareSmartWeightModel(List<ClusterConf> clusterConfs){
        SmartWeightsModel model = new SmartWeightsModel();
        model.init(clusterConfs);
        prepareModel(model);
    }

    private void prepareModel(Model model) {
        Mockito.when(modelsCacheService.getModel(Mockito.anyString(), Mockito.eq(Collections.emptyMap()), Mockito.any(Instant.class))).thenReturn(model);
    }

    private FeatureScore calculateScore(SmartRecord smartRecord) {
        return new SmartWeightsModelScorer("scorerName","modelName",eventModelsCacheService).calculateScore(new AdeRecordReader(smartRecord));
    }

//    private JokerSpecs prepareAggrFeatureEventsJsonFeature(String featureName, double score, double clusterWeight) {
//        JSONArray featureValue = new JSONArray();
//        JSONObject aggrFeatureEvent = new JSONObject();
//        featureValue.add(aggrFeatureEvent);
//        aggrFeatureEvent.put("event_type", "aggr_event");
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_FEATURE_TYPE, AggrEvent.AGGREGATED_FEATURE_TYPE_F_VALUE);
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_AGGREGATED_FEATURE_INFO, new JSONObject(Collections.singletonMap("total", 1)));
//        aggrFeatureEvent.put("date_time_unix", 1472540399);
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_START_TIME, "2016-08-30 06:00:00");
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_START_TIME_UNIX, 1472536800);
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_END_TIME, "2016-08-30 06:59:59");
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_END_TIME_UNIX, 1472540399);
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_CREATION_EPOCHTIME, 1481980116);
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_CREATION_DATE_TIME, "2016-12-17 13:08:36");
//        aggrFeatureEvent.put(AggrEvent.EVENT_FILED_DATA_SOURCE, "aggr_event.normalized_username_prnlog_hourly.sum_of_pages_prnlog_hourly");
//        JSONArray dataSources = new JSONArray();
//        dataSources.add("prnlog");
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_DATA_SOURCES, dataSources);
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_CONTEXT, new JSONObject(Collections.singletonMap("normalized_username", "prnlogusr3@somebigcompany.com")));
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_AGGREGATED_FEATURE_VALUE, 1.0);
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_SCORE, score);
//        String bucketConfName = "bucketConfName";
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_BUCKET_CONF_NAME, bucketConfName);
//        aggrFeatureEvent.put(AggrEvent.EVENT_FIELD_AGGREGATED_FEATURE_NAME, featureName);
//        prepareFeature(featureName, featureValue);
//
//        return new JokerSpecs(
//                Collections.singletonList(new ClusterSpecs(Collections.singletonList(
//                        AggregatedFeatureEventsConfUtilService.buildFullAggregatedFeatureEventName(
//                                bucketConfName,
//                                featureName
//                        )
//                ), clusterWeight))
//        );
//    }


//    private void prepareFeature(String featureName, Object featureValue) {
//        Mockito.when(featureExtractService.extract(Mockito.eq(featureName), Mockito.any(EventMessage.class)))
//                .thenReturn(new Feature(featureName, featureValue));
//    }






    @Configuration
    public static class ContextConfiguration {
        @Bean
        public EventModelsCacheService eventModelsCacheService() {
            return new EventModelsCacheService();
        }

        @Bean
        public ModelsCacheService modelsCacheService() {
            return Mockito.mock(ModelsCacheService.class);
        }
    }
}
