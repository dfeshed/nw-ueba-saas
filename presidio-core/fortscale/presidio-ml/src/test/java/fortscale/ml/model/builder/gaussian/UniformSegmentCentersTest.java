package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.IContinuousDataModel;
import fortscale.ml.model.builder.gaussian.prior.UniformSegmentCenters;
import org.apache.commons.collections.IteratorUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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
    public void shouldUniformlyGenerateNumbersFromMinMeanToMaxMean() {
        UniformSegmentCenters segmentCenters = new UniformSegmentCenters(1);
        List<IContinuousDataModel> models = DoubleStream.of(2.0, 2.2, 5.1)
                .mapToObj(mean -> new ContinuousDataModel().setParameters(0, mean, 0, 0))
                .collect(Collectors.toList());

        Assert.assertEquals(Arrays.asList(2.0, 3.0, 4.0, 5.0, 6.0), IteratorUtils.toList(segmentCenters.iterate(models)));
    }

    @Test
    public void shouldStartFromTheMinMeanRoundedAccordingToDistanceBetweenSegmentCenters() {
        UniformSegmentCenters segmentCenters = new UniformSegmentCenters(0.5);

        ContinuousDataModel model = new ContinuousDataModel().setParameters(0, 2.6, 0, 0);
        Iterator<Double> centers = segmentCenters.iterate(Collections.singletonList(model));
        Assert.assertEquals(Arrays.asList(2.5, 3.0), IteratorUtils.toList(centers));
    }
}
