package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.builder.gaussian.prior.UniformSegmentCenters;
import org.apache.commons.collections.IteratorUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class UniformSegmentCentersTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenZeroAsDistanceBetweenSegmentCenters() {
        new UniformSegmentCenters(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsModels() {
        new UniformSegmentCenters(1).iterate(null);
    }

    @Test
    public void shouldGenerateNothingIfGivenEmptyModels() {
        Iterator<Double> segmentCentersIter = new UniformSegmentCenters(1).iterate(Collections.emptyList());

        Assert.assertEquals(0, IteratorUtils.toList(segmentCentersIter).size());
    }

    @Test
    public void shouldUniformlyGenerateNumbersUntilMaximalMean() {
        List<ContinuousDataModel> models = new ArrayList<>();
        double[] means = {2, 2.2};
        for (double mean : means) {
            models.add(new ContinuousDataModel().setParameters(0, mean, 0, 0));
        }

        UniformSegmentCenters segmentCenters = new UniformSegmentCenters(1);

        Assert.assertEquals(Arrays.asList(0.0, 1.0, 2.0, 3.0), IteratorUtils.toList(segmentCenters.iterate(models)));
    }
}
