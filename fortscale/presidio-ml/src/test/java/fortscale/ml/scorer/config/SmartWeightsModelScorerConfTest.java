package fortscale.ml.scorer.config;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

/**
 * Created by YaronDL on 8/30/2017.
 */
public class SmartWeightsModelScorerConfTest {
    public void shouldSuccessfullyCreateWhenAllParamsAreValid() {
        new SmartWeightsModelScorerConf("name", new ModelInfo("name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenScorerNameIsNull() {
        new SmartWeightsModelScorerConf(null, new ModelInfo("name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenScorerNameIsEmpty() {
        new SmartWeightsModelScorerConf("  ", new ModelInfo("name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenModelInfoIsNull() {
        new SmartWeightsModelScorerConf("name", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfModelNameIsNull() {
        new SmartWeightsModelScorerConf("name", new ModelInfo(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfModelNameIsEmpty() {
        new SmartWeightsModelScorerConf("name", new ModelInfo("   "));
    }
}
