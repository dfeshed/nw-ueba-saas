package fortscale.ml.scorer;

import fortscale.ml.scorer.config.ScoreExponentialStepsMappingConf;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;



public class ScoreExponentialStepsMapperTest {

    @Test
    public void test_score_with_exponential_mapping_default_configuration() throws Exception {
        ScoreExponentialStepsMappingConf scoreMappingConf = new ScoreExponentialStepsMappingConf();
        double expectedScore = 0;
        double score = 0.9544;
        double mappedScoreStep = scoreMappingConf.MAX_MAPPED_SCORE_DEFAULT / scoreMappingConf.getAmountOfSteps();
        while (score < 0.9999999999999999) {
            double mappedScore = calcScore(score * 100, scoreMappingConf);
            score = 1 - ((1 - score) / 1.5);
            Assert.assertEquals(mappedScore, expectedScore, 0.5);
            expectedScore = (expectedScore + mappedScoreStep) < 100 ? expectedScore + mappedScoreStep : 100;
        }
    }


    private double calcScore(double scoreToMap, ScoreExponentialStepsMappingConf scoreMappingConf) {
        ScoreExponentialStepsMapper scoreExponentialStepsMapper = new ScoreExponentialStepsMapper("test", Mockito.mock(Scorer.class), scoreMappingConf);
        return scoreExponentialStepsMapper.mapScore(scoreToMap);
    }

    @Test
    public void test_score_with_different_sd() throws Exception {
        ScoreExponentialStepsMappingConf scoreMappingConf = new ScoreExponentialStepsMappingConf();
        //2sd-
        double mappedScore = calcScore(95.0, scoreMappingConf);
        Assert.assertEquals(0.0, mappedScore, 0.0);
        //2sd+
        mappedScore = calcScore(95.5, scoreMappingConf);
        Assert.assertEquals(0.10906621426740208, mappedScore, 0.0);
        //3sd
        mappedScore = calcScore(99.73, scoreMappingConf);
        Assert.assertEquals(27.86396458159663, mappedScore, 0.0);
        //3.5sd
        mappedScore = calcScore(99.95, scoreMappingConf);
        Assert.assertEquals(44.500650894150425, mappedScore, 0.0);
        //4sd
        mappedScore = calcScore(99.99, scoreMappingConf);
        Assert.assertEquals(60.37810007780395, mappedScore, 0.0);
        //4.5sd
        mappedScore = calcScore(99.999, scoreMappingConf);
        Assert.assertEquals(83.09359442681946, mappedScore, 0.0);
        //5sd
        mappedScore = calcScore(99.9999, scoreMappingConf);
        Assert.assertEquals(100.0, mappedScore, 0.0);
        //5.5sd
        mappedScore = calcScore(99.99999, scoreMappingConf);
        Assert.assertEquals(100.0, mappedScore, 0.0);
    }
}
