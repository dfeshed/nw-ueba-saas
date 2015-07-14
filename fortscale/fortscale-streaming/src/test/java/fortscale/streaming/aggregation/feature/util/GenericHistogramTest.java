package fortscale.streaming.aggregation.feature.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by amira on 17/06/2015.
 */
public class GenericHistogramTest {


    @Test
    public void testAddString() {
        GenericHistogram histogram = new GenericHistogram();
        histogram.add("one", 0.5);
        histogram.add("one", 2.0);
        histogram.add("one", 3.0);
        histogram.add("one", 1.0);
        histogram.add("one", 3.5);

        histogram.add("two", 0.5);
        histogram.add("two", 1.0);
        histogram.add("two", 2.0);
        histogram.add("two", 3.0);
        histogram.add("two", 3.5);
        histogram.add("two", 10.0);

        histogram.add("three", 30.0);

        Assert.assertEquals((Double)10.0, (Double)histogram.get("one"));
        Assert.assertEquals((Double)20.0, (Double)histogram.get("two"));
        Assert.assertEquals((Double)30.0, (Double)histogram.get("three"));
        Assert.assertEquals((Long) 3L, (Long) histogram.getN());
        Assert.assertEquals((Double) 20.0, (Double) histogram.getAvg());

        Double one = Math.pow((10-20.0),2);
        Double two = Math.pow((20-20.0),2);
        Double three = Math.pow((30-20.0),2);
        Double sum = one+two+three;
        Double std = Math.sqrt((sum)/(3-1));
        Double popStd = Math.sqrt((sum)/(3));
        Assert.assertEquals((Double) popStd, (Double) histogram.getPopulationStandardDeviation());
        Assert.assertEquals((Double) std, (Double) histogram.getStandardDeviation());
        Assert.assertEquals((Double) 30.0, (Double) histogram.getMaxCount());
        Assert.assertEquals("three", (String) histogram.getMaxCountObject());
        Assert.assertEquals((Double) (30.0/60.0), (Double) histogram.getMaxCountFromTotalCount());

    }

    @Test
    public void testAddNumbers() {
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(1, 0.5);
        histogram.add(1, 2.0);
        histogram.add(1, 3.0);
        histogram.add(1, 1.0);
        histogram.add(1, 3.5);

        histogram.add(2L, 0.5);
        histogram.add(2L, 1.0);
        histogram.add(2L, 2.0);
        histogram.add(2L, 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);

        histogram.add(3.0, 30.0);

        Assert.assertEquals((Double) 10.0, (Double) histogram.get(1));
        Assert.assertEquals((Double) 20.0, (Double) histogram.get(2L));
        Assert.assertEquals((Double) 30.0, (Double) histogram.get(3.0));
        Assert.assertEquals((Long) 3L, (Long) histogram.getN());
        Assert.assertEquals((Double) 20.0, (Double) histogram.getAvg());

        Double one = Math.pow((10-20.0),2);
        Double two = Math.pow((20-20.0),2);
        Double three = Math.pow((30-20.0),2);
        Double sum = one+two+three;
        Double std = Math.sqrt((sum)/(3-1));
        Double popStd = Math.sqrt((sum)/(3));
        Assert.assertEquals((Double) popStd, (Double) histogram.getPopulationStandardDeviation());
        Assert.assertEquals((Double) std, (Double) histogram.getStandardDeviation());
        Assert.assertEquals((Double) 30.0, (Double) histogram.getMaxCount());
        Assert.assertEquals((Double)3.0, (Double) histogram.getMaxCountObject());
        Assert.assertEquals((Double) (30.0/60.0), (Double) histogram.getMaxCountFromTotalCount());

    }

    @Test
    public void testAddDifferentTypes() {
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(2, 0.5);
        histogram.add(2, 2.0);
        histogram.add(2, 3.0);
        histogram.add(2, 1.0);
        histogram.add(2, 3.5);

        histogram.add(2L, 0.5);
        histogram.add(2L, 1.0);
        histogram.add(2L, 2.0);
        histogram.add(2L, 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);

        histogram.add("2", 30.0);

        Assert.assertEquals((Double) 10.0, (Double) histogram.get(2));
        Assert.assertEquals((Double) 20.0, (Double) histogram.get(2L));
        Assert.assertEquals((Double) 30.0, (Double) histogram.get("2"));
        Assert.assertEquals((Long) 3L, (Long) histogram.getN());
        Assert.assertEquals((Double) 20.0, (Double) histogram.getAvg());

        Double one = Math.pow((10-20.0),2);
        Double two = Math.pow((20-20.0),2);
        Double three = Math.pow((30-20.0),2);
        Double sum = one+two+three;
        Double std = Math.sqrt((sum)/(3-1));
        Double popStd = Math.sqrt((sum)/(3));
        Assert.assertEquals((Double) popStd, (Double) histogram.getPopulationStandardDeviation());
        Assert.assertEquals((Double) std, (Double) histogram.getStandardDeviation());
        Assert.assertEquals((Double) 30.0, (Double) histogram.getMaxCount());
        Assert.assertEquals("2", (String) histogram.getMaxCountObject());
        Assert.assertEquals((Double) (30.0/60.0), (Double) histogram.getMaxCountFromTotalCount());

    }

    @Test
    public void testAddNull() {
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(2, 0.5);
        histogram.add(2, 2.0);
        histogram.add(2, 3.0);
        histogram.add(2, 1.0);
        histogram.add(2, 3.5);

        histogram.add(2L, 0.5);
        histogram.add(2L, 1.0);
        histogram.add(2L, 2.0);
        histogram.add(2L, 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);

        histogram.add("2", 30.0);


        Double one = Math.pow((10-20.0),2);
        Double two = Math.pow((20-20.0),2);
        Double three = Math.pow((30-20.0),2);
        Double sum = one+two+three;
        Double std = Math.sqrt((sum)/(3-1));
        Double popStd = Math.sqrt((sum)/(3));

        histogram.add(null, 2.0);
        histogram.add("2.0", null);
        histogram.add(null, null);

       Assert.assertEquals((Double) popStd, (Double) histogram.getPopulationStandardDeviation());
        Assert.assertEquals((Double) std, (Double) histogram.getStandardDeviation());
        Assert.assertEquals((Double) 30.0, (Double) histogram.getMaxCount());
        Assert.assertEquals("2", (String) histogram.getMaxCountObject());
        Assert.assertEquals((Double) (30.0/60.0), (Double) histogram.getMaxCountFromTotalCount());

    }

    @Test
    public void testAddingTwoHistograms() {
        GenericHistogram histogram1 = new GenericHistogram();
        GenericHistogram histogram2 = new GenericHistogram();
        GenericHistogram histogram3 = new GenericHistogram();
        GenericHistogram histogram4 = new GenericHistogram();

        histogram1.add("a", 1.0);
        histogram1.add("b", 2.0);
        histogram1.add("c", 3.0);

        histogram2.add("b", 2.0);
        histogram2.add("c", 3.0);
        histogram2.add("d", 4.0);

        histogram3.add("a", 1.0);
        histogram3.add("b", 4.0);
        histogram3.add("c", 6.0);
        histogram3.add("d", 4.0);

        histogram4.add(histogram1).add(histogram2);

        Assert.assertTrue(histogram4.equals(histogram3));
    }
}
