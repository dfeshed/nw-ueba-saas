package fortscale.ml.scorer.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ModelBasedScoreMapperConfTest {

    private ModelInfo modelInfo = new ModelInfo("model-name");

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
        new ModelBasedScoreMapperConf("name", modelInfo, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNotGivenModelInfo() {
        new ModelBasedScoreMapperConf("name", null, baseScorerConf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenEmptyModelInfoName() {
        new ModelBasedScoreMapperConf("name", new ModelInfo(""), baseScorerConf);
    }

    @Test
    public void shouldGetStuff() {
        ModelBasedScoreMapperConf conf = new ModelBasedScoreMapperConf(
                "name", modelInfo, baseScorerConf);
        Assert.assertEquals("model-based-score-mapper", conf.getFactoryName());
        Assert.assertEquals(baseScorerConf, conf.getBaseScorerConf());
        Assert.assertEquals(modelInfo, conf.getModelInfo());
    }
}
