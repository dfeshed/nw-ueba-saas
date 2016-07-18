package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.builder.gaussian.prior.UniformSegmentCentersIterator;
import org.apache.commons.collections.IteratorUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UniformSegmentCentersIteratorTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsModels() {
        new UniformSegmentCentersIterator(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenZeroAsDistanceBetweenSegmentCenters() {
        new UniformSegmentCentersIterator(Collections.emptyList(), 0);
    }

    @Test
    public void shouldGenerateNothingIfGivenEmptyModels() {
        UniformSegmentCentersIterator it = new UniformSegmentCentersIterator(Collections.emptyList(), 1);

        List segmentCenters = IteratorUtils.toList(it);
        Assert.assertEquals(0, segmentCenters.size());
    }

    @Test
    public void shouldUniformlyGenerateNumbersUntilMaximalMean() {
        List<ContinuousDataModel> models = new ArrayList<>();
        double[] means = {2, 2.2};
        for (double mean : means) {
            ContinuousDataModel model = new ContinuousDataModel();
            model.setParameters(0, mean, 0, 0);
            models.add(model);
        }

        UniformSegmentCentersIterator it = new UniformSegmentCentersIterator(models, 1);

        List segmentCenters = IteratorUtils.toList(it);
        Assert.assertEquals(Arrays.asList(0.0, 1.0, 2.0, 3.0), segmentCenters);
    }
}
