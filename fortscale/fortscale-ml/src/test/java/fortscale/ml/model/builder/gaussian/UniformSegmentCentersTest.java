package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.builder.gaussian.prior.UniformSegmentCenters;
import org.apache.commons.collections.IteratorUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UniformSegmentCentersTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsModels() {
        new UniformSegmentCenters(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenZeroAsDistanceBetweenSegmentCenters() {
        new UniformSegmentCenters(Collections.emptyList(), 0);
    }

    @Test
    public void shouldGenerateNothingIfGivenEmptyModels() {
        UniformSegmentCenters segmentCenters = new UniformSegmentCenters(Collections.emptyList(), 1);

        Assert.assertEquals(0, IteratorUtils.toList(segmentCenters.iterator()).size());
    }

    @Test
    public void shouldUniformlyGenerateNumbersUntilMaximalMean() {
        List<ContinuousDataModel> models = new ArrayList<>();
        double[] means = {2, 2.2};
        for (double mean : means) {
            models.add(new ContinuousDataModel().setParameters(0, mean, 0, 0));
        }

        UniformSegmentCenters segmentCenters = new UniformSegmentCenters(models, 1);

        Assert.assertEquals(Arrays.asList(0.0, 1.0, 2.0, 3.0), IteratorUtils.toList(segmentCenters.iterator()));
    }
}
