package fortscale.ml.model.retriever;

import fortscale.ml.model.data.type.ContinuousDataHistogram;
import org.junit.Assert;
import org.junit.Test;

public class ExponentialDecayTest {
    @Test
    public void shouldReturnTheSameHistogramIfBaseIsOne() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram();
        histogram.add(1, 1);

        ContinuousDataHistogram res = new ExponentialDecay(1, 60).execute(histogram, 1000);

        Assert.assertEquals(histogram.getMap(), res.getMap());
    }

    @Test
    public void shouldReturnTheSameHistogramIfStartTimeRelativeToNowIsZero() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram();
        histogram.add(1, 1);

        ContinuousDataHistogram res = new ExponentialDecay(0.9f, 60).execute(histogram, 0);

        Assert.assertEquals(histogram.getMap(), res.getMap());
    }

    @Test
    public void shouldReturnEmptyHistogramIfBaseIsZero() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram();
        histogram.add(1, 1);

        ContinuousDataHistogram res = new ExponentialDecay(0, 60).execute(histogram, 1000);

        Assert.assertEquals(0, res.getMap().size());
    }

    @Test
    public void shouldMultiplyByBaseOnce() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram();
        int value = 1;
        int count = 10;
        histogram.add(value, count);

        float base = 0.9f;
        ContinuousDataHistogram res = new ExponentialDecay(base, 60).execute(histogram, 61);

        Assert.assertEquals(1, res.getMap().size());
        Assert.assertEquals(count * base, res.getCount(value), 0.001);
    }

    @Test
    public void shouldMultiplyByBaseTwice() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram();
        int value = 1;
        int count = 10;
        histogram.add(value, count);

        float base = 0.9f;
        ContinuousDataHistogram res = new ExponentialDecay(base, 60).execute(histogram, 121);

        Assert.assertEquals(1, res.getMap().size());
        Assert.assertEquals(count * base * base, res.getCount(value), 0.001);
    }
}
