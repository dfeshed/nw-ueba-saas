package fortscale.ml.model.retriever;

import fortscale.ml.model.data.type.ContinuousDataHistogram;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ContinuousDataHistogramExponentialDecayTest {
    private Date startTime = new Date(1420070400000L);
    private Date endTime = new Date(1420156800000L);

    @Test
    public void shouldReturnTheSameHistogramIfBaseIsOne() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);
        histogram.add(1, 1);

        // Add to start time 1000 seconds
        ContinuousDataHistogram res = new ContinuousDataHistogramExponentialDecay(1, 60).execute(histogram, new Date(1420071400000L));

        Assert.assertEquals(histogram.getMap(), res.getMap());
    }

    @Test
    public void shouldReturnTheSameHistogramIfStartTimeRelativeToNowIsZero() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);
        histogram.add(1, 1);

        // Add to start time 0 seconds
        ContinuousDataHistogram res = new ContinuousDataHistogramExponentialDecay(0.9f, 60).execute(histogram, new Date(1420070400000L));

        Assert.assertEquals(histogram.getMap(), res.getMap());
    }

    @Test
    public void shouldReturnEmptyHistogramIfBaseIsZero() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);
        histogram.add(1, 1);

        // Add to start time 1000 seconds
        ContinuousDataHistogram res = new ContinuousDataHistogramExponentialDecay(0, 60).execute(histogram, new Date(1420071400000L));

        Assert.assertEquals(0, res.getMap().size());
    }

    @Test
    public void shouldMultiplyByBaseOnce() {
        ContinuousDataHistogram histogram = new ContinuousDataHistogram(startTime, endTime);
        int value = 1;
        int count = 10;
        histogram.add(value, count);

        float base = 0.9f;
        // Add to start time 61 seconds
        ContinuousDataHistogram res = new ContinuousDataHistogramExponentialDecay(base, 60).execute(histogram, new Date(1420070461000L));

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
        // Add to start time 121 seconds
        ContinuousDataHistogram res = new ContinuousDataHistogramExponentialDecay(base, 60).execute(histogram, new Date(1420070521000L));

        Assert.assertEquals(1, res.getMap().size());
        Assert.assertEquals(count * base * base, res.getCount(value), 0.001);
    }
}
