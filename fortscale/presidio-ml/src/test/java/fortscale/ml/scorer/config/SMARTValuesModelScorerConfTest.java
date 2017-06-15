package fortscale.ml.scorer.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;


@RunWith(JUnit4.class)
public class SMARTValuesModelScorerConfTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNotGivenAdditionalModelInfo() {
        new SMARTValuesModelScorerConf("name", new ModelInfo("name"), Collections.emptyList(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNegativeGlobalInfluence() {
        new SMARTValuesModelScorerConf("name", new ModelInfo("name"), Collections.singletonList(new ModelInfo("name")), -1);
    }
}
