package fortscale.ml.scorer.config;

import fortscale.ml.scorer.ScoreMapping;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class ScoreMapperConfTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNotGivenBaseScorerConf() {
        new ScoreMapperConf("name", null, new ScoreMapping.ScoreMappingConf());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNotGivenScoreMappingConf() {
        new ScoreMapperConf("name", Mockito.mock(IScorerConf.class), null);
    }

    @Test
    public void shouldInitializeProperly() {
        ScoreMapping.ScoreMappingConf scoreMappingConf = new ScoreMapping.ScoreMappingConf();
        IScorerConf baseScorerConf = Mockito.mock(IScorerConf.class);
        String name = "name";
        ScoreMapperConf conf = new ScoreMapperConf(name, baseScorerConf, scoreMappingConf);
        Assert.assertEquals(name, conf.getName());
        Assert.assertEquals("score-mapper", conf.getFactoryName());
        Assert.assertEquals(scoreMappingConf, conf.getScoreMappingConf());
        Assert.assertEquals(baseScorerConf, conf.getBaseScorerConf());
    }
}
