package fortscale.common.util;

import fortscale.common.feature.FeatureStringValue;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultiKeyHistogramTest {

    @Test
    public void testSet() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        MultiKeyFeature multiKeyFeature1 = new MultiKeyFeature();
        multiKeyFeature1.add("featureName1", new FeatureStringValue("value1"));
        double value1 = 90;
        multiKeyHistogram.set(multiKeyFeature1, 80.0);
        multiKeyHistogram.set(multiKeyFeature1, 50.0);
        multiKeyHistogram.set(multiKeyFeature1, value1);

        MultiKeyFeature multiKeyFeature2 = new MultiKeyFeature();
        multiKeyFeature2.add("featureName2", new FeatureStringValue("value2"));
        double value2 = 10;
        multiKeyHistogram.set(multiKeyFeature2, 80.0);
        multiKeyHistogram.set(multiKeyFeature2, 70.0);
        multiKeyHistogram.set(multiKeyFeature2, value2);

        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature1), (Double) value1);
        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature2), (Double) value2);
        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) 6.0);
    }

    @Test
    public void testAdd() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        MultiKeyFeature multiKeyFeature1 = new MultiKeyFeature();
        multiKeyFeature1.add("featureName1", new FeatureStringValue("value1"));
        double val1 = 1.0;
        double val2 = 5.0;
        multiKeyHistogram.add(multiKeyFeature1, val1);
        multiKeyHistogram.add(multiKeyFeature1, val2);

        MultiKeyFeature multiKeyFeature2 = new MultiKeyFeature();
        multiKeyFeature2.add("featureName2", new FeatureStringValue("value2"));
        double val3 = 4.0;
        multiKeyHistogram.add(multiKeyFeature2, val1);
        multiKeyHistogram.add(multiKeyFeature2, val2);
        multiKeyHistogram.add(multiKeyFeature2, val3);

        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature1), (Double) (val1 + val2));
        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature2), (Double) (val1 + val2 + val3));
        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) ((val1 + val2) + (val1 + val2 + val3)));
    }


    @Test
    public void testAddMultiKeyHistogram() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        MultiKeyFeature multiKeyFeature1 = new MultiKeyFeature();
        multiKeyFeature1.add("featureName1", new FeatureStringValue("value1"));

        MultiKeyFeature multiKeyFeature2 = new MultiKeyFeature();
        multiKeyFeature2.add("featureName2", new FeatureStringValue("value2"));
        multiKeyHistogram.add(multiKeyFeature1, 10.0);
        multiKeyHistogram.add(multiKeyFeature2, 50.0);

        MultiKeyHistogram newMultiKeyHistogram = new MultiKeyHistogram();
        newMultiKeyHistogram.add(multiKeyHistogram, new HashSet<>());

        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) newMultiKeyHistogram.getTotal());

        Map<MultiKeyFeature, Double> featuresGroupToMax = multiKeyHistogram.getHistogram();
        Map<MultiKeyFeature, Double> newFeaturesGroupToMax = newMultiKeyHistogram.getHistogram();
        Assert.assertEquals(newFeaturesGroupToMax.size(), featuresGroupToMax.size());
        for (Map.Entry<MultiKeyFeature, Double> entry : featuresGroupToMax.entrySet()) {
            Assert.assertEquals(entry.getValue().intValue(), newFeaturesGroupToMax.get(entry.getKey()).intValue());
        }
    }


    @Test
    public void testFilterKeyFromOneKeyHistogram() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        MultiKeyFeature multiKeyFeature1 = new MultiKeyFeature();
        multiKeyFeature1.add("featureName1", new FeatureStringValue("value1"));
        double val1 = 1.0;
        double val2 = 5.0;
        multiKeyHistogram.add(multiKeyFeature1, val1);
        multiKeyHistogram.add(multiKeyFeature1, val2);

        MultiKeyFeature multiKeyFeature2 = new MultiKeyFeature();
        multiKeyFeature2.add("featureName2", new FeatureStringValue("value2"));
        double val3 = 4.0;
        multiKeyHistogram.add(multiKeyFeature2, val1);
        multiKeyHistogram.add(multiKeyFeature2, val2);
        multiKeyHistogram.add(multiKeyFeature2, val3);

        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature1), (Double) (val1 + val2));
        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature2), (Double) (val1 + val2 + val3));
        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) ((val1 + val2) + (val1 + val2 + val3)));

        MultiKeyHistogram filteredMultiKeyHistogram = new MultiKeyHistogram();
        Set<FeatureStringValue> filter = new HashSet<>();
        filter.add(new FeatureStringValue("value1"));
        filteredMultiKeyHistogram.add(multiKeyHistogram,filter);

        Assert.assertNull(filteredMultiKeyHistogram.getHistogram().get(multiKeyFeature1));
        Assert.assertEquals(filteredMultiKeyHistogram.getHistogram().get(multiKeyFeature2), (Double) (val1 + val2 + val3));
        Assert.assertEquals((Double) filteredMultiKeyHistogram.getTotal(), (Double) (val1 + val2 + val3));
    }

    @Test
    public void testFilterKeyWhichDoesNotExist() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        MultiKeyFeature multiKeyFeature1 = new MultiKeyFeature();
        multiKeyFeature1.add("featureName1", new FeatureStringValue("value1"));
        double val1 = 1.0;
        double val2 = 5.0;
        multiKeyHistogram.add(multiKeyFeature1, val1);
        multiKeyHistogram.add(multiKeyFeature1, val2);

        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature1), (Double) (val1 + val2));
        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) (val1 + val2));

        MultiKeyHistogram filteredMultiKeyHistogram = new MultiKeyHistogram();
        Set<FeatureStringValue> filter = new HashSet<>();
        filter.add(new FeatureStringValue("value2"));
        filteredMultiKeyHistogram.add(multiKeyHistogram,filter);

        Assert.assertEquals(filteredMultiKeyHistogram.getHistogram().get(multiKeyFeature1), (Double) (val1 + val2));
        Assert.assertEquals((Double) filteredMultiKeyHistogram.getTotal(), (Double) (val1 + val2));
    }
}
