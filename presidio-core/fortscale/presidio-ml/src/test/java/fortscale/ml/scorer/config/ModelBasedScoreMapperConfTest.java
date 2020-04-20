package fortscale.ml.scorer.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class ModelBasedScoreMapperConfTest {

    private ModelInfo modelInfo = new ModelInfo("model-name");

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNotGivenBaseScorerConf() {
        new ModelBasedScoreMapperConf("name", modelInfo, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNotGivenModelInfo() {
        new ModelBasedScoreMapperConf("name", null, Mockito.mock(IScorerConf.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenEmptyModelInfoName() {
        new ModelBasedScoreMapperConf("name", new ModelInfo(""), Mockito.mock(IScorerConf.class));
    }

    @Test
    public void shouldInitializeProperly() {
        IScorerConf baseScorerConf = Mockito.mock(IScorerConf.class);
        String name = "name";
        ModelBasedScoreMapperConf conf = new ModelBasedScoreMapperConf(
                name, modelInfo, baseScorerConf);
        Assert.assertEquals(name, conf.getName());
        Assert.assertEquals("model-based-score-mapper", conf.getFactoryName());
        Assert.assertEquals(baseScorerConf, conf.getBaseScorerConf());
        Assert.assertEquals(modelInfo, conf.getModelInfo());
    }
}
