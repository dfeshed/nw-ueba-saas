package fortscale.ml.scorer.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.Collections;


@RunWith(JUnit4.class)
public class SMARTValuesModelScorerConfTest {
    public void shouldSuccessfullyCreateWhenAllParamsAreValid() {
        new SMARTValuesModelScorerConf("name", new ModelInfo("name"), Collections.singletonList(new ModelInfo("name")), Mockito.mock(IScorerConf.class), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNotGivenAdditionalModelInfo() {
        new SMARTValuesModelScorerConf("name", new ModelInfo("name"), Collections.emptyList(), Mockito.mock(IScorerConf.class), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNegativeGlobalInfluence() {
        new SMARTValuesModelScorerConf("name", new ModelInfo("name"), Collections.singletonList(new ModelInfo("name")), Mockito.mock(IScorerConf.class), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsBaseScorerConf() {
        new SMARTValuesModelScorerConf("name", new ModelInfo("name"), Collections.singletonList(new ModelInfo("name")), null, 0);
    }
}