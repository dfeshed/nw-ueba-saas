package fortscale.ml.scorer.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ScoreAndCertaintyMultiplierScorerConfTest {
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
        new ScoreAndCertaintyMultiplierScorerConf("name", null);
    }

    @Test
    public void shouldGetStuff() {
        ScoreAndCertaintyMultiplierScorerConf conf = new ScoreAndCertaintyMultiplierScorerConf("name", baseScorerConf);
        Assert.assertEquals(baseScorerConf, conf.getBaseScorerConf());
        Assert.assertEquals("score-and-certainty-multiplier-scorer", conf.getFactoryName());
    }
}
