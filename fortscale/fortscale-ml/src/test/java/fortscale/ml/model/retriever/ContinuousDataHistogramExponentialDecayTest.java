package fortscale.ml.model.retriever;

import fortscale.ml.model.data.type.ContinuousDataHistogram;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class ContinuousDataHistogramExponentialDecayTest {
    private DateTime startTime = new DateTime(1420070400000L);
    private DateTime endTime = startTime.plusDays(1);

    @Test
    public void shouldReturnTheSameHistogramIfBaseIsOne() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);
        histogram.add(1, 1);

        ContinuousDataHistogram res = new ContinuousDataHistogramExponentialDecay(1, 60).execute(histogram, startTime.plusSeconds(1000));

        Assert.assertEquals(histogram.getMap(), res.getMap());
    }

    @Test
    public void shouldReturnTheSameHistogramIfStartTimeRelativeToNowIsZero() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);
        histogram.add(1, 1);

        ContinuousDataHistogram res = new ContinuousDataHistogramExponentialDecay(0.9f, 60).execute(histogram, startTime.plusSeconds(0));

        Assert.assertEquals(histogram.getMap(), res.getMap());
    }

    @Test
    public void shouldReturnEmptyHistogramIfBaseIsZero() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);
        histogram.add(1, 1);

        ContinuousDataHistogram res = new ContinuousDataHistogramExponentialDecay(0, 60).execute(histogram, startTime.plusSeconds(1000));

        Assert.assertEquals(0, res.getMap().size());
    }

    @Test
    public void shouldMultiplyByBaseOnce() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);
        int value = 1;
        int count = 10;
        histogram.add(value, count);

        float base = 0.9f;
        ContinuousDataHistogram res = new ContinuousDataHistogramExponentialDecay(base, 60).execute(histogram, startTime.plusSeconds(61));

        Assert.assertEquals(1, res.getMap().size());
        Assert.assertEquals(count * base, res.getCount(value), 0.001);
    }

    @Test
    public void shouldMultiplyByBaseTwice() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);
        int value = 1;
        int count = 10;
        histogram.add(value, count);

        float base = 0.9f;
        ContinuousDataHistogram res = new ContinuousDataHistogramExponentialDecay(base, 60).execute(histogram, startTime.plusSeconds(121));

        Assert.assertEquals(1, res.getMap().size());
        Assert.assertEquals(count * base * base, res.getCount(value), 0.001);
    }
}
