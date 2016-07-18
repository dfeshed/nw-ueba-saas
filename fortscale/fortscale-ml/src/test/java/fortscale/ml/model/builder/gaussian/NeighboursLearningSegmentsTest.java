package fortscale.ml.model.builder.gaussian;

import fortscale.ml.model.builder.gaussian.prior.NeighboursLearningSegments;
import org.junit.Test;

import java.util.Collections;

public class NeighboursLearningSegmentsTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsModels() {
        new NeighboursLearningSegments(null, 100, Collections.emptyIterator(), 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenZeroAsNumberOfNeighbours() {
        new NeighboursLearningSegments(Collections.emptyList(), 0, Collections.emptyIterator(), 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenNullAsSegmentCentersIterator() {
        new NeighboursLearningSegments(Collections.emptyList(), 100, null, 0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfGivenZeroAsValidRatio() {
        new NeighboursLearningSegments(Collections.emptyList(), 100, Collections.emptyIterator(), 0);
    }
}
