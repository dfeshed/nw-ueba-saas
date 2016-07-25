package fortscale.ml.scorer.algorithm;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import fortscale.ml.scorer.algorithms.GaussianModelScorerAlgorithm;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.IntStream;


public class GaussianModelScorerAlgorithmTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenGivenNullAsPriorModel() {
        GaussianModelScorerAlgorithm.calculate(null, new GaussianPriorModel(), 0);
    }

    @Test
    public void shouldScore0WhenLessThanTwoSamples() {
        GaussianPriorModel priorModel = new GaussianPriorModel();
        IntStream.of(0, 1)
                .mapToObj(N -> new ContinuousDataModel().setParameters(N, 0, 0, 0))
                .mapToDouble(model -> GaussianModelScorerAlgorithm.calculate(model, priorModel, 0))
                .forEach(score -> Assert.assertEquals(0, score, 0.0000001));
        Assert.assertTrue(GaussianModelScorerAlgorithm.calculate(new ContinuousDataModel().setParameters(2, 0, 0, 0), priorModel, 0) > 0);
    }
}
