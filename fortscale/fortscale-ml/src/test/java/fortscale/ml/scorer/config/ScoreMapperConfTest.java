package fortscale.ml.scorer.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ScoreMapperConfTest {
    private IScorerConf baseScorerConf = new IScorerConf() {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getFactoryName() {
            return null;
        }
    };

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNotGivenBaseScorerConf() {
        new ScoreMapperConf("name", null, new ScoreMappingConf());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNotGivenScoreMappingConf() {
        new ScoreMapperConf("name", baseScorerConf, null);
    }

    @Test
    public void shouldGetStuff() {
        ScoreMappingConf scoreMappingConf = new ScoreMappingConf();
        ScoreMapperConf conf = new ScoreMapperConf("name", baseScorerConf, scoreMappingConf);
        Assert.assertEquals(scoreMappingConf, conf.getScoreMappingConf());
        Assert.assertEquals(baseScorerConf, conf.getBaseScorerConf());
        Assert.assertEquals("score-mapper", conf.getFactoryName());
    }
}
