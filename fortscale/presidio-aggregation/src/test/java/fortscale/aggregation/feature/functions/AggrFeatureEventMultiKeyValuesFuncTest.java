package fortscale.aggregation.feature.functions;

import fortscale.common.feature.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;


public class AggrFeatureEventMultiKeyValuesFuncTest {

    @Test
    public void testCalculateHistogramAggrFeatureValue() {
        double featureCount1 = 1.0;
        double featureCount2 = 2.0;
        double featureCount3 = 3.0;

        MultiKeyHistogram multiKeyHistogram = buildMultiKeyHistogram(featureCount1, featureCount2, featureCount3);

        Map<String, String> key = new HashMap<>();
        key.put("featureName1", "featureValue1");
        Set<Map<String, String>> keys = new HashSet<>();
        keys.add(key);

        AggrFeatureEventMultiKeyValuesFunc func = new AggrFeatureEventMultiKeyValuesFunc(keys);
        AggrFeatureValue aggrFeatureValue = func.calculateHistogramAggrFeatureValue(multiKeyHistogram);
        Assert.assertEquals(aggrFeatureValue.getValue(), featureCount1 + featureCount2);
    }

    /**
     * Test Zero AggrFeatureValue creation, where no key was met.
     */
    @Test
    public void testCalculateHistogramAggrFeatureValueWithoutAppropriateKey() {
        double featureCount1 = 1.0;
        double featureCount2 = 2.0;
        double featureCount3 = 3.0;

        MultiKeyHistogram multiKeyHistogram = buildMultiKeyHistogram(featureCount1, featureCount2, featureCount3);

        Map<String, String> key = new HashMap<>();
        key.put("featureName1", "noValue");
        Set<Map<String, String>> keys = new HashSet<>();
        keys.add(key);

        AggrFeatureEventMultiKeyValuesFunc func = new AggrFeatureEventMultiKeyValuesFunc(keys);
        AggrFeatureValue aggrFeatureValue = func.calculateHistogramAggrFeatureValue(multiKeyHistogram);

        Assert.assertEquals(aggrFeatureValue.getValue(), 0.0);
    }

    /**
     * Help function to build MultiKeyHistogram
     *
     * @param featureCount1
     * @param featureCount2
     * @param featureCount3
     * @return MultiKeyHistogram
     */
    private MultiKeyHistogram buildMultiKeyHistogram(double featureCount1, double featureCount2, double featureCount3) {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        Map<String, String> featureNameToValues1 = new HashMap<>();
        featureNameToValues1.put("featureName1", "featureValue1");
        featureNameToValues1.put("featureName2", "featureValue2");

        Map<String, String> featureNameToValues2 = new HashMap<>();
        featureNameToValues2.put("featureName1", "featureValue1");
        featureNameToValues2.put("featureName2", "featureValue3");

        Map<String, String> featureNameToValues3 = new HashMap<>();
        featureNameToValues3.put("featureName1", "featureValue4");
        featureNameToValues3.put("featureName2", "featureValue3");

        multiKeyHistogram.add(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValues1), featureCount1);
        multiKeyHistogram.add(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValues2), featureCount2);
        multiKeyHistogram.add(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValues3), featureCount3);

        return multiKeyHistogram;
    }

}
