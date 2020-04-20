package fortscale.ml.scorer.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class SMARTMaxValuesModelScorerConfTest {
    public void shouldSuccessfullyCreateWhenAllParamsAreValid() {
        new SMARTMaxValuesModelScorerConf("name", new ModelInfo("name"), new ModelInfo("name"), Mockito.mock(IScorerConf.class), 0, 10, 5, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNotGivenAdditionalModelInfo() {
        new SMARTMaxValuesModelScorerConf("name", new ModelInfo("name"), null, Mockito.mock(IScorerConf.class), 0, 10, 5, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNegativeGlobalInfluence() {
        new SMARTMaxValuesModelScorerConf("name", new ModelInfo("name"), new ModelInfo("name"), Mockito.mock(IScorerConf.class), -1, 10, 5, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsBaseScorerConf() {
        new SMARTMaxValuesModelScorerConf("name", new ModelInfo("name"), new ModelInfo("name"), null, 0, 10, 5, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNegativeNumOfPartition() {
        new SMARTMaxValuesModelScorerConf("name", new ModelInfo("name"), new ModelInfo("name"), Mockito.mock(IScorerConf.class), 0, 10, -1, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfMinNumOfUserValuesGreaterThanMaxUserInfluence() {
        new SMARTMaxValuesModelScorerConf("name", new ModelInfo("name"), new ModelInfo("name"), Mockito.mock(IScorerConf.class), 0, 1, 10, 5);
    }

}