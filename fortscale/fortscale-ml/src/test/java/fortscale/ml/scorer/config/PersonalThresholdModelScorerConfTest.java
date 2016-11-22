package fortscale.ml.scorer.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class PersonalThresholdModelScorerConfTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsModelInfo() {
        new PersonalThresholdModelScorerConf("name", null, Mockito.mock(IScorerConf.class), 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsBaseScorerConf() {
        new PersonalThresholdModelScorerConf("name", new ModelInfo("name"), null, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenZeroAsMaxRatioFromUniformThreshold() {
        new PersonalThresholdModelScorerConf("name", new ModelInfo("name"), Mockito.mock(IScorerConf.class), 0);
    }

    @Test
    public void shouldGetStuffProperly() {
        IScorerConf baseScorerConf = Mockito.mock(IScorerConf.class);
        ModelInfo modelInfo = new ModelInfo("name");
        double maxRatioFromUniformThreshold = 10;
        PersonalThresholdModelScorerConf conf = new PersonalThresholdModelScorerConf("name", modelInfo, baseScorerConf, maxRatioFromUniformThreshold);
        Assert.assertEquals("personal-threshold-model-scorer", conf.getFactoryName());
        Assert.assertEquals(baseScorerConf, conf.getBaseScorerConf());
        Assert.assertEquals(modelInfo, conf.getModelInfo());
        Assert.assertEquals(maxRatioFromUniformThreshold, conf.getMaxRatioFromUniformThreshold(), 0.0000001);
    }
}
