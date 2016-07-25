package fortscale.ml.scorer.algorithm;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.scorer.algorithms.ContinuousValuesModelScorerAlgorithm;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by amira on 17/03/2016.
 */
public class ContinuousValuesModelScorerAlgorithmTest {
    @Test
    public void testZeroSd() {
        double score = ContinuousValuesModelScorerAlgorithm.calculate(new ContinuousDataModel().setParameters(100, 0, 0, 0), 50);
        Assert.assertEquals(1, score, 0.0);
    }
}
