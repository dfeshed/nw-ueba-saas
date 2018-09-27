package fortscale.common.util;

import fortscale.common.feature.FeatureStringValue;
import fortscale.common.feature.FeatureValue;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiKeyHistogramTest {

    @Test
    public void testSetMax() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        Map<String, FeatureValue> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("featureName1", new FeatureStringValue("value1"));
        double max1 = 90;
        MultiKeyFeature multiKeyFeature1 = new MultiKeyFeature(featureNameToValue1);
        multiKeyHistogram.setMax(multiKeyFeature1, 80.0);
        multiKeyHistogram.setMax(multiKeyFeature1, 50.0);
        multiKeyHistogram.setMax(multiKeyFeature1, max1);

        Map<String, FeatureValue> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put("featureName2", new FeatureStringValue("value2"));
        double max2 = 100;
        MultiKeyFeature multiKeyFeature2 = new MultiKeyFeature(featureNameToValue2);
        multiKeyHistogram.setMax(multiKeyFeature2, 80.0);
        multiKeyHistogram.setMax(multiKeyFeature2, 50.0);
        multiKeyHistogram.setMax(multiKeyFeature2, 70.0);
        multiKeyHistogram.setMax(multiKeyFeature2, max2);

        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature1), (Double) max1);
        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature2), (Double) max2);
        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) 7.0);
    }

    @Test
    public void testAdd() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        Map<String, FeatureValue> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("featureName1", new FeatureStringValue("value1"));
        MultiKeyFeature multiKeyFeature1 = new MultiKeyFeature(featureNameToValue1);
        double val1 = 1.0;
        double val2 = 5.0;
        multiKeyHistogram.add(multiKeyFeature1, val1);
        multiKeyHistogram.add(multiKeyFeature1, val2);

        Map<String, FeatureValue> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put("featureName2", new FeatureStringValue("value2"));
        MultiKeyFeature multiKeyFeature2 = new MultiKeyFeature(featureNameToValue2);
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

        Map<String, FeatureValue> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("featureName1", new FeatureStringValue("value1"));
        MultiKeyFeature multiKeyFeature1 = new MultiKeyFeature(featureNameToValue1);

        Map<String, FeatureValue> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put("featureName2", new FeatureStringValue("value2"));
        MultiKeyFeature multiKeyFeature2 = new MultiKeyFeature(featureNameToValue2);
        multiKeyHistogram.add(multiKeyFeature1, 10.0);
        multiKeyHistogram.add(multiKeyFeature2, 50.0);

        MultiKeyHistogram newMultiKeyHistogram = new MultiKeyHistogram();
        newMultiKeyHistogram.add(multiKeyHistogram);

        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) newMultiKeyHistogram.getTotal());

        Map<MultiKeyFeature, Double> featuresGroupToMax = multiKeyHistogram.getHistogram();
        Map<MultiKeyFeature, Double> newFeaturesGroupToMax = newMultiKeyHistogram.getHistogram();
        Assert.assertEquals(newFeaturesGroupToMax.size(), featuresGroupToMax.size());
        for (Map.Entry<MultiKeyFeature, Double> entry : featuresGroupToMax.entrySet()) {
            Assert.assertEquals(entry.getValue().intValue(), newFeaturesGroupToMax.get(entry.getKey()).intValue());
        }
    }


    @Test
    public void testRemoveKeyFromOneKeyHistogram() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        Map<String, FeatureValue> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("featureName1", new FeatureStringValue("value1"));
        MultiKeyFeature multiKeyFeature1 = new MultiKeyFeature(featureNameToValue1);
        double val1 = 1.0;
        double val2 = 5.0;
        multiKeyHistogram.add(multiKeyFeature1, val1);
        multiKeyHistogram.add(multiKeyFeature1, val2);

        Map<String, FeatureValue> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put("featureName2", new FeatureStringValue("value2"));
        MultiKeyFeature multiKeyFeature2 = new MultiKeyFeature(featureNameToValue2);
        double val3 = 4.0;
        multiKeyHistogram.add(multiKeyFeature2, val1);
        multiKeyHistogram.add(multiKeyFeature2, val2);
        multiKeyHistogram.add(multiKeyFeature2, val3);

        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature1), (Double) (val1 + val2));
        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature2), (Double) (val1 + val2 + val3));
        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) ((val1 + val2) + (val1 + val2 + val3)));
        Assert.assertEquals(multiKeyHistogram.getMaxObject(), multiKeyFeature2);

        String valToRemove = "value1";
        multiKeyHistogram.remove(new FeatureStringValue(valToRemove));

        Assert.assertNull(multiKeyHistogram.getHistogram().get(multiKeyFeature1));
        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature2), (Double) (val1 + val2 + val3));
        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) (val1 + val2 + val3));
        Assert.assertEquals(multiKeyHistogram.getMaxObject(), multiKeyFeature2);
    }


    @Test
    public void testRemoveKeyWhichIsMaxObject() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        Map<String, FeatureValue> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("featureName1", new FeatureStringValue("value1"));
        MultiKeyFeature multiKeyFeature1 = new MultiKeyFeature(featureNameToValue1);
        double val1 = 1.0;
        double val2 = 5.0;
        multiKeyHistogram.add(multiKeyFeature1, val1);
        multiKeyHistogram.add(multiKeyFeature1, val2);

        Map<String, FeatureValue> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put("featureName2", new FeatureStringValue("value2"));
        MultiKeyFeature multiKeyFeature2 = new MultiKeyFeature(featureNameToValue2);
        double val3 = 4.0;
        multiKeyHistogram.add(multiKeyFeature2, val1);
        multiKeyHistogram.add(multiKeyFeature2, val2);
        multiKeyHistogram.add(multiKeyFeature2, val3);

        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature1), (Double) (val1 + val2));
        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature2), (Double) (val1 + val2 + val3));
        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) ((val1 + val2) + (val1 + val2 + val3)));
        Assert.assertEquals(multiKeyHistogram.getMaxObject(), multiKeyFeature2);

        String valToRemove = "value2";
        multiKeyHistogram.remove(new FeatureStringValue(valToRemove));

        Assert.assertNull(multiKeyHistogram.getHistogram().get(multiKeyFeature2));
        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature1), (Double) (val1 + val2));
        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) (val1 + val2));
        Assert.assertEquals(multiKeyHistogram.getMaxObject(), multiKeyFeature1);
    }


    @Test
    public void testRemoveKeyWhichDoesNotExist() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        Map<String, FeatureValue> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("featureName1", new FeatureStringValue("value1"));
        MultiKeyFeature multiKeyFeature1 = new MultiKeyFeature(featureNameToValue1);
        double val1 = 1.0;
        double val2 = 5.0;
        multiKeyHistogram.add(multiKeyFeature1, val1);
        multiKeyHistogram.add(multiKeyFeature1, val2);

        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature1), (Double) (val1 + val2));
        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) (val1 + val2));
        Assert.assertEquals(multiKeyHistogram.getMaxObject(), multiKeyFeature1);

        String valToRemove = "value2";
        multiKeyHistogram.remove(new FeatureStringValue(valToRemove));

        Assert.assertEquals(multiKeyHistogram.getHistogram().get(multiKeyFeature1), (Double) (val1 + val2));
        Assert.assertEquals((Double) multiKeyHistogram.getTotal(), (Double) (val1 + val2));
        Assert.assertEquals(multiKeyHistogram.getMaxObject(), multiKeyFeature1);
    }
}
