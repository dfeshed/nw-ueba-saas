package fortscale.ml.scorer;


import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.SmartWeightsModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import fortscale.smart.record.conf.ClusterConf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.time.Instant;
import java.util.ArrayList;
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
        new SmartWeightsModelScorer( "scorerName","",createSmartWeightsScorerAlgorithm(),eventModelsCacheService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenWhenAdeRecordIsNotInstanceOfSmartRecord() {
        prepareSmartWeightModel(Collections.emptyList());
        AdeRecord adeRecord = Mockito.mock(AdeRecord.class);
        calculateScore(adeRecord);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenWhenModelIsNotInstanceOfSmartWeightModel() {
        SmartRecord emptyMockedSmartRecord = createMockedSmartRecord(Collections.emptyList());
        prepareModel(new CategoryRarityModel());
        calculateScore(emptyMockedSmartRecord);
    }

    @Test
    public void testEmptySmartRecordGetZeroScore(){
        SmartRecord emptyMockedSmartRecord = createMockedSmartRecord(Collections.emptyList());
        prepareSmartWeightModel(Collections.emptyList());
        FeatureScore featureScore = calculateScore(emptyMockedSmartRecord);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
    }

    @Test
    public void shouldGiveZeroScoreWhenModelDoesNotExist() {
        String featureName = "dummyFeature";
        double score = 50.0;
        AdeAggregationRecord adeAggregationRecord = createMockedAdeAggregationRecordOfSCORE_AGGREGATIONType(featureName,score);
        SmartRecord smartRecord = createMockedSmartRecord(Collections.singletonList(adeAggregationRecord));

        FeatureScore featureScore = calculateScore(smartRecord);
        prepareModel(null);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
    }

    @Test
    public void scoreShouldNotIncludeFeaturesWhichDoNotBelongToAnyCluster() {
        String featureName1 = "dummyFeature1";
        double score1 = 50.0;
        AdeAggregationRecord adeAggregationRecord1 = createMockedAdeAggregationRecordOfSCORE_AGGREGATIONType(featureName1,score1);

        String featureName2 = "dummyFeature2";
        double score2 = 80.0;
        AdeAggregationRecord adeAggregationRecord2 = createMockedAdeAggregationRecordOfFEATURE_AGGREGATIONType(featureName2,score2);

        String featureName3 = "dummyFeature3";
        double score3 = 90.0;
        AdeAggregationRecord adeAggregationRecord3 = createMockedAdeAggregationRecordOfSCORE_AGGREGATIONType(featureName3,score3);

        List<AdeAggregationRecord> adeAggregationRecords = new ArrayList<>();
        adeAggregationRecords.add(adeAggregationRecord1);
        adeAggregationRecords.add(adeAggregationRecord2);
        adeAggregationRecords.add(adeAggregationRecord3);

        SmartRecord smartRecord = createMockedSmartRecord(adeAggregationRecords);
        double clusterWeight = 0.1;
        ClusterConf clusterConf1 = new ClusterConf(Collections.singletonList(adeAggregationRecord1.getFeatureName()), clusterWeight);
        ClusterConf clusterConf2 = new ClusterConf(Collections.singletonList(adeAggregationRecord2.getFeatureName()), clusterWeight);
        List<ClusterConf> clusterConfs = new ArrayList<>();
        Collections.addAll(clusterConfs,clusterConf1,clusterConf2);
        prepareSmartWeightModel(clusterConfs);

        FeatureScore featureScore = calculateScore(smartRecord);

        Assert.assertEquals(clusterWeight * (score1+score2) / 100, featureScore.getScore(), 0.0000001);

    }

    @Test
    public void shouldGiveScoreWhenSmartContainsSCORE_AGGREGATIONFeatureWhichBelongToAClusterWithNonZeroWeight() {
        String featureName = "dummyFeature";
        double score = 50.0;
        AdeAggregationRecord adeAggregationRecord = createMockedAdeAggregationRecordOfSCORE_AGGREGATIONType(featureName,score);

        shouldGiveScoreWhenSmartContainsFeatureWhichBelongToAClusterWithNonZeroWeight(adeAggregationRecord, score);
    }

    @Test
    public void shouldGiveScoreWhenSmartContainsFEATURE_AGGREGATIONFeatureWhichBelongToAClusterWithNonZeroWeight() {
        String featureName = "dummyFeature";
        double score = 90.0;
        AdeAggregationRecord adeAggregationRecord = createMockedAdeAggregationRecordOfFEATURE_AGGREGATIONType(featureName,score);

        shouldGiveScoreWhenSmartContainsFeatureWhichBelongToAClusterWithNonZeroWeight(adeAggregationRecord, score);
    }

    private void shouldGiveScoreWhenSmartContainsFeatureWhichBelongToAClusterWithNonZeroWeight(AdeAggregationRecord adeAggregationRecord, double score) {
        SmartRecord smartRecord = createMockedSmartRecord(Collections.singletonList(adeAggregationRecord));
        double clusterWeight = 0.1;
        ClusterConf clusterConf = new ClusterConf(Collections.singletonList(adeAggregationRecord.getFeatureName()), clusterWeight);
        prepareSmartWeightModel(Collections.singletonList(clusterConf));

        FeatureScore featureScore = calculateScore(smartRecord);

        Assert.assertEquals(clusterWeight * score / 100, featureScore.getScore(), 0.0000001);
    }

    private SmartRecord createMockedSmartRecord(List<AdeAggregationRecord> adeAggregationRecords){
        SmartRecord ret = Mockito.mock(SmartRecord.class);
        Mockito.when(ret.getAggregationRecords()).thenReturn(adeAggregationRecords);

        return ret;
    }

    private AdeAggregationRecord createMockedAdeAggregationRecordOfSCORE_AGGREGATIONType(String featureName, double score){
        AdeAggregationRecord ret = Mockito.mock(AdeAggregationRecord.class);
        Mockito.when(ret.getAggregatedFeatureType()).thenReturn(AggregatedFeatureType.SCORE_AGGREGATION);
        Mockito.when(ret.getFeatureValue()).thenReturn(score);
        Mockito.when(ret.getFeatureName()).thenReturn(featureName);

        return ret;
    }

    private ScoredFeatureAggregationRecord createMockedAdeAggregationRecordOfFEATURE_AGGREGATIONType(String featureName, double score){
        ScoredFeatureAggregationRecord ret = Mockito.mock(ScoredFeatureAggregationRecord.class);
        Mockito.when(ret.getAggregatedFeatureType()).thenReturn(AggregatedFeatureType.FEATURE_AGGREGATION);
        Mockito.when(ret.getScore()).thenReturn(score);
        Mockito.when(ret.getFeatureName()).thenReturn(featureName);
        return ret;
    }

    private void prepareSmartWeightModel(List<ClusterConf> clusterConfs){
        SmartWeightsModel model = new SmartWeightsModel();
        model.setClusterConfs(clusterConfs);
        prepareModel(model);
    }

    private void prepareModel(Model model) {
        Mockito.when(modelsCacheService.getLatestModelBeforeEventTime(Mockito.anyString(), Mockito.eq(Collections.emptyMap()), Mockito.any(Instant.class))).thenReturn(model);
    }

    private FeatureScore calculateScore(AdeRecord adeRecord) {
        SmartWeightsModelScorer smartWeightsModelScorer = new SmartWeightsModelScorer("scorerName",
                "modelName"
                ,createSmartWeightsScorerAlgorithm(),
                eventModelsCacheService);
        return smartWeightsModelScorer.calculateScore(new AdeRecordReader(adeRecord));
    }

    private SmartWeightsScorerAlgorithm createSmartWeightsScorerAlgorithm(){
        return new SmartWeightsScorerAlgorithm(0.5, 50);
    }



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
