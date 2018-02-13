package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ScoreExponentialStepsMapperTest {
    private Scorer baseScorer;

    @Test
    public void test_score_with_exponential_mapping_default_configuration() throws Exception {
        ScoreExponentialStepsMapping.ScoreExponentialStepsMappingConf scoreMappingConf = new ScoreExponentialStepsMapping.ScoreExponentialStepsMappingConf();
        double expectedScore = scoreMappingConf.getMinScore();
        double score = 0.9544;
        while (score < 0.9999999999999999) {
            double mappedScore = calcScore(score * 100, scoreMappingConf);
            score = 1 - ((1 - score) / 1.5);

            Assert.assertEquals(mappedScore, expectedScore, 0.0);
            if (expectedScore < 100) {
                expectedScore += scoreMappingConf.getScoreStep();
            }
        }
    }


    private double calcScore(double scoreToMap, ScoreExponentialStepsMapping.ScoreExponentialStepsMappingConf scoreMappingConf) {
        return ScoreExponentialStepsMapping.mapScore(scoreToMap, scoreMappingConf);
    }

    @Test
    public void test_score_with_different_sd() throws Exception {
        ScoreExponentialStepsMapping.ScoreExponentialStepsMappingConf scoreMappingConf = new ScoreExponentialStepsMapping.ScoreExponentialStepsMappingConf();

        //2sd-
        double mappedScore = calcScore(95.0, scoreMappingConf);
        Assert.assertEquals(0.0, mappedScore, 0.0);
        //2sd+
        mappedScore = calcScore(95.5, scoreMappingConf);
        Assert.assertEquals(3, mappedScore, 0.0);
        //3sd
        mappedScore = calcScore(99.73, scoreMappingConf);
        Assert.assertEquals(35, mappedScore, 0.0);
        //3.5sd
        mappedScore = calcScore(99.95, scoreMappingConf);
        Assert.assertEquals(60, mappedScore, 0.0);
        //4sd
        mappedScore = calcScore(99.99, scoreMappingConf);
        Assert.assertEquals(80, mappedScore, 0.0);
        //4.5sd
        mappedScore = calcScore(99.999, scoreMappingConf);
        Assert.assertEquals(100.0, mappedScore, 0.0);
        //5sd
        mappedScore = calcScore(99.9999, scoreMappingConf);
        Assert.assertEquals(100.0, mappedScore, 0.0);
    }
}
