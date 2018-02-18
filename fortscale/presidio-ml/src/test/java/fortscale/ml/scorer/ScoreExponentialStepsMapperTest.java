package fortscale.ml.scorer;

import fortscale.ml.scorer.config.ScoreExponentialStepsMappingConf;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ScoreExponentialStepsMapperTest {

    @Test
    public void test_score_with_exponential_mapping_default_configuration() throws Exception {
        ScoreExponentialStepsMappingConf scoreMappingConf = new ScoreExponentialStepsMappingConf();
        double expectedScore = scoreMappingConf.getMinScore();
        double score = 0.9544;
        while (score < 0.9999999999999999) {
            double mappedScore = calcScore(score * 100, scoreMappingConf);
            score = 1 - ((1 - score) / 1.5);
            Assert.assertEquals(mappedScore, expectedScore, 0.0);
            expectedScore = (expectedScore + scoreMappingConf.getScoreStep())<100 ? expectedScore + scoreMappingConf.getScoreStep() : 100;

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
        Assert.assertEquals(0.0, mappedScore, 0.0);
        //3sd
        mappedScore = calcScore(99.73, scoreMappingConf);
        Assert.assertEquals(21, mappedScore, 0.0);
        //3.5sd
        mappedScore = calcScore(99.95, scoreMappingConf);
        Assert.assertEquals(33, mappedScore, 0.0);
        //4sd
        mappedScore = calcScore(99.99, scoreMappingConf);
        Assert.assertEquals(45, mappedScore, 0.0);
        //4.5sd
        mappedScore = calcScore(99.999, scoreMappingConf);
        Assert.assertEquals(62.0, mappedScore, 0.0);
        //5sd
        mappedScore = calcScore(99.9999, scoreMappingConf);
        Assert.assertEquals(80.0, mappedScore, 0.0);
        //5.5sd
        mappedScore = calcScore(99.99999, scoreMappingConf);
        Assert.assertEquals(96.0, mappedScore, 0.0);
        //6sd
        mappedScore = calcScore(99.999999, scoreMappingConf);
        Assert.assertEquals(100.0, mappedScore, 0.0);
    }
}
