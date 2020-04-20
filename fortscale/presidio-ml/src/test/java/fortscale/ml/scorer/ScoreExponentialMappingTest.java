package fortscale.ml.scorer;

import org.junit.Assert;
import org.junit.Test;

public class ScoreExponentialMappingTest {

    @Test
    public void should_map_to_zero_score_below_min_score(){
        double scoreToMap = 94.99;
        double mappedScore = calcScore(scoreToMap);
        Assert.assertEquals(0.0, mappedScore, 0.0);
    }

    @Test
    public void should_map_to_zero_score_equal_to_min_score(){
        double scoreToMap = ScoreExponentialMapping.ScoreExponentialMappingConf.MIN_SCORE_TO_MAP_DEFAULT;
        double mappedScore = calcScore(scoreToMap);
        Assert.assertEquals(0.0, mappedScore, 0.0);
    }

    @Test
    public void test_score(){
        double scoreToMap = 99;
        double mappedScore = calcScore(scoreToMap);
        Assert.assertEquals(50.0, mappedScore, 0.0);
    }

    private double calcScore(double scoreToMap){
        ScoreExponentialMapping.ScoreExponentialMappingConf scoreMappingConf = new ScoreExponentialMapping.ScoreExponentialMappingConf();
        return ScoreExponentialMapping.mapScore(scoreToMap, scoreMappingConf);
    }
}
