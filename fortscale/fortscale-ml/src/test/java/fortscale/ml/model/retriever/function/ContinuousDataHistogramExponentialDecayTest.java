package fortscale.ml.model.retriever.function;

import fortscale.common.datastructures.GenericHistogram;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ContinuousDataHistogramExponentialDecayTest {
    private Date startTime = new Date(1420070400000L);

    @Test
    public void shouldReturnTheSameHistogramIfBaseIsOne() {
        GenericHistogram expected = new GenericHistogram();
        expected.add(1, 1.0);

        // Add to start time 1000 seconds
        GenericHistogram actual = (GenericHistogram)new ContinuousDataHistogramExponentialDecay(1, 60)
                .execute(expected, startTime, new Date(1420071400000L));

        Assert.assertEquals(expected.getHistogramMap(), actual.getHistogramMap());
    }

    @Test
    public void shouldReturnTheSameHistogramIfStartTimeRelativeToNowIsZero() {
        GenericHistogram expected = new GenericHistogram();
        expected.add(1, 1.0);

        // Add to start time 0 seconds
        GenericHistogram actual = (GenericHistogram)new ContinuousDataHistogramExponentialDecay(0.9f, 60)
                .execute(expected, startTime, new Date(1420070400000L));

        Assert.assertEquals(expected.getHistogramMap(), actual.getHistogramMap());
    }

    @Test
    public void shouldReturnEmptyHistogramIfBaseIsZero() {
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(1, 1.0);

        // Add to start time 1000 seconds
        GenericHistogram actual = (GenericHistogram)new ContinuousDataHistogramExponentialDecay(0, 60)
                .execute(histogram, startTime, new Date(1420071400000L));

        Double expectedTotalCount = 0d;
        Double actualTotalCount = actual.getTotalCount();
        Assert.assertEquals(expectedTotalCount, actualTotalCount);
    }

    @Test
    public void shouldMultiplyByBaseOnce() {
        GenericHistogram histogram = new GenericHistogram();
        double value = 1;
        double count = 10;
        histogram.add(value, count);

        float base = 0.9f;
        // Add to start time 61 seconds
        GenericHistogram actual = (GenericHistogram)new ContinuousDataHistogramExponentialDecay(base, 60)
                .execute(histogram, startTime, new Date(1420070461000L));

        Assert.assertEquals(1, actual.getN());
        Assert.assertEquals(count * base, actual.get(value), 0.001);
    }

    @Test
    public void shouldMultiplyByBaseTwice() {
        GenericHistogram histogram = new GenericHistogram();
        double value = 1;
        double count = 10;
        histogram.add(value, count);

        float base = 0.9f;
        // Add to start time 121 seconds
        GenericHistogram actual = (GenericHistogram)new ContinuousDataHistogramExponentialDecay(base, 60)
                .execute(histogram, startTime, new Date(1420070521000L));

        Assert.assertEquals(1, actual.getN());
        Assert.assertEquals(count * base * base, actual.get(value), 0.001);
    }
}
