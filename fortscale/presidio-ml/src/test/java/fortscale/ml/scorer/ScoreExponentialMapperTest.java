package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import presidio.ade.domain.record.AdeRecordReader;

public class ScoreExponentialMapperTest {
    private Scorer baseScorer;
    private AdeRecordReader adeRecordReader;

    @Before
    public void setup() {
        baseScorer = Mockito.mock(Scorer.class);
        adeRecordReader = Mockito.mock(AdeRecordReader.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsName() {
        ScoreExponentialMapping.ScoreExponentialMappingConf scoreMappingConf = new ScoreExponentialMapping.ScoreExponentialMappingConf();
        new ScoreExponentialMapper(null, baseScorer, scoreMappingConf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsBaseScorer() {
        ScoreExponentialMapping.ScoreExponentialMappingConf scoreMappingConf = new ScoreExponentialMapping.ScoreExponentialMappingConf();
        new ScoreExponentialMapper("name", null, scoreMappingConf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsScorerMappingConf() {
        new ScoreExponentialMapper("name", baseScorer, null);
    }

    @Test
    public void test_score_with_exponential_mapping_default_configuration() throws Exception {
        ScoreExponentialMapping.ScoreExponentialMappingConf scoreMappingConf = new ScoreExponentialMapping.ScoreExponentialMappingConf();

        FeatureScore featureScore = calcFeatureScore(0,scoreMappingConf);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(95,scoreMappingConf);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(98,scoreMappingConf);
        Assert.assertEquals(25, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(99,scoreMappingConf);
        Assert.assertEquals(50, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(99.5,scoreMappingConf);
        Assert.assertEquals(70.71, featureScore.getScore(), 0.01);

        featureScore = calcFeatureScore(99.9,scoreMappingConf);
        Assert.assertEquals(93.3, featureScore.getScore(), 0.01);

        featureScore = calcFeatureScore(99.99,scoreMappingConf);
        Assert.assertEquals(99.3, featureScore.getScore(), 0.01);
    }

    @Test
    public void test_score_with_different_min_score_to_map() throws Exception {
        ScoreExponentialMapping.ScoreExponentialMappingConf scoreMappingConf = new ScoreExponentialMapping.ScoreExponentialMappingConf();
        scoreMappingConf.setMinScoreToMap(97);

        FeatureScore featureScore = calcFeatureScore(0,scoreMappingConf);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(97,scoreMappingConf);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(98,scoreMappingConf);
        Assert.assertEquals(25, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(99,scoreMappingConf);
        Assert.assertEquals(50, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(99.5,scoreMappingConf);
        Assert.assertEquals(70.71, featureScore.getScore(), 0.01);

        featureScore = calcFeatureScore(99.9,scoreMappingConf);
        Assert.assertEquals(93.3, featureScore.getScore(), 0.01);

        featureScore = calcFeatureScore(99.99,scoreMappingConf);
        Assert.assertEquals(99.3, featureScore.getScore(), 0.01);
    }

    @Test
    public void test_score_with_different_min_score_to_map_and_different_base() throws Exception {
        ScoreExponentialMapping.ScoreExponentialMappingConf scoreMappingConf = new ScoreExponentialMapping.ScoreExponentialMappingConf();
        scoreMappingConf.setMinScoreToMap(97);
        scoreMappingConf.setBase(3);

        FeatureScore featureScore = calcFeatureScore(0,scoreMappingConf);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(97,scoreMappingConf);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(98,scoreMappingConf);
        Assert.assertEquals(11.11, featureScore.getScore(), 0.01);

        featureScore = calcFeatureScore(99,scoreMappingConf);
        Assert.assertEquals(33.33, featureScore.getScore(), 0.01);

        featureScore = calcFeatureScore(99.5,scoreMappingConf);
        Assert.assertEquals(57.735, featureScore.getScore(), 0.001);

        featureScore = calcFeatureScore(99.9,scoreMappingConf);
        Assert.assertEquals(89.596, featureScore.getScore(), 0.001);

        featureScore = calcFeatureScore(99.99,scoreMappingConf);
        Assert.assertEquals(98.9, featureScore.getScore(), 0.01);
    }

    @Test
    public void test_score_with_different_min_score_to_map_and_different_max_score_to_map() throws Exception {
        ScoreExponentialMapping.ScoreExponentialMappingConf scoreMappingConf = new ScoreExponentialMapping.ScoreExponentialMappingConf();
        scoreMappingConf.setMinScoreToMap(97);
        scoreMappingConf.setMaxScoreToMap(99.9);

        FeatureScore featureScore = calcFeatureScore(0,scoreMappingConf);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(97,scoreMappingConf);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(98,scoreMappingConf);
        Assert.assertEquals(26.77, featureScore.getScore(), 0.01);

        featureScore = calcFeatureScore(99,scoreMappingConf);
        Assert.assertEquals(53.535, featureScore.getScore(), 0.001);

        featureScore = calcFeatureScore(99.5,scoreMappingConf);
        Assert.assertEquals(75.71, featureScore.getScore(), 0.01);

        featureScore = calcFeatureScore(99.9,scoreMappingConf);
        Assert.assertEquals(99.9, featureScore.getScore(), 0.0);

        featureScore = calcFeatureScore(99.99,scoreMappingConf);
        Assert.assertEquals(99.99, featureScore.getScore(), 0.0);
    }

    private FeatureScore calcFeatureScore(double scoreToMap, ScoreExponentialMapping.ScoreExponentialMappingConf scoreMappingConf){
        FeatureScore baseScore = new FeatureScore("base score", scoreToMap);
        Mockito.when(baseScorer.calculateScore(adeRecordReader)).thenReturn(baseScore);
        String featureScoreName = "mapped score";
        ScoreExponentialMapper scorer = new ScoreExponentialMapper(featureScoreName, baseScorer, scoreMappingConf);
        return scorer.calculateScore(adeRecordReader);
    }
}
