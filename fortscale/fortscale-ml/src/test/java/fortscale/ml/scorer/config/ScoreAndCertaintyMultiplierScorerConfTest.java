package fortscale.ml.scorer.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class ScoreAndCertaintyMultiplierScorerConfTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNotGivenName() {
        new ScoreAndCertaintyMultiplierScorerConf(null, Mockito.mock(IScorerConf.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNotGivenBaseScorerConf() {
        new ScoreAndCertaintyMultiplierScorerConf("name", null);
    }

    @Test
    public void shouldInitializeProperly() {
        IScorerConf baseScorerConf = Mockito.mock(IScorerConf.class);
        String name = "name";
        ScoreAndCertaintyMultiplierScorerConf conf = new ScoreAndCertaintyMultiplierScorerConf(name, baseScorerConf);
        Assert.assertEquals(name, conf.getName());
        Assert.assertEquals("score-and-certainty-multiplier-scorer", conf.getFactoryName());
        Assert.assertEquals(baseScorerConf, conf.getBaseScorerConf());
    }
}
