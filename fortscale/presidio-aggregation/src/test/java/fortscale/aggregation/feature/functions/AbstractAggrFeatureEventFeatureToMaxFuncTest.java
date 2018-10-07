package fortscale.aggregation.feature.functions;


import fortscale.common.feature.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class AbstractAggrFeatureEventFeatureToMaxFuncTest {

    private void assertBucketsAggregatedCorrectly(final Map<MultiKeyFeature, Integer> expectedFeaturesGroupToMax, Pair<MultiKeyFeature, Integer>[]... featureValuesAndNumbersInBucketList) {
        String pickFeatureName = "source_machine_to_highest_score_map";

        final boolean[] calculateMapAggrFeatureValueWasCalled = {false};
        AbstractAggrFeatureEventFeatureToMaxFunc f = new AbstractAggrFeatureEventFeatureToMaxFunc() {
            @Override
            protected AggrFeatureValue calculateFeaturesGroupToMaxValue(MultiKeyHistogram multiKeyHistogram) {
                Map<MultiKeyFeature, Double> featuresGroupToMax = multiKeyHistogram.getHistogram();
                Assert.assertEquals(expectedFeaturesGroupToMax.size(), featuresGroupToMax.size());
                for (Map.Entry<MultiKeyFeature, Integer> entry : expectedFeaturesGroupToMax.entrySet()) {
                    Assert.assertEquals(entry.getValue().intValue(), featuresGroupToMax.get(entry.getKey()).intValue());
                }

                calculateMapAggrFeatureValueWasCalled[0] = true;
                return null;
            }
        };

        List<Map<String, Feature>> multipleBucketsAggrFeaturesMapList =
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createMultipleBucketsAggrFeaturesMapList(pickFeatureName, featureValuesAndNumbersInBucketList);
        f.calculateAggrFeature(
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggregatedFeatureEventConf("sum_of_highest_scores_over_src_machines_vpn_hourly", pickFeatureName),
                multipleBucketsAggrFeaturesMapList);

        Assert.assertTrue(calculateMapAggrFeatureValueWasCalled[0]);
    }

    private MultiKeyFeature generateGroupByFeatureValuesKey(String[] features) {
        MultiKeyFeature multiKeyFeature = new MultiKeyFeature();
        for (String feature : features) {

            multiKeyFeature.add("", new FeatureStringValue(feature));
        }
        return multiKeyFeature;
    }

    @Test
    public void shouldCreateTheSameMappingGivenOnlyOneBucket() {
        final String featureName = "host";
        final String featureValue = "123";
        Map<String,String> featureNameToValue = new HashMap<>();
        featureNameToValue.put(featureName,featureValue);
        final int max = 10;
        Map<MultiKeyFeature, Integer> expectedFeaturesGroupToMax = new HashMap<>();
        expectedFeaturesGroupToMax.put(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue), max);
        assertBucketsAggregatedCorrectly(
                expectedFeaturesGroupToMax,
                new Pair[]{new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue), max)});
    }

    @Test
    public void shouldCreateMappingContainingMaxValuesGivenMultipleBuckets() {
        final String featureName1 = "host";
        final String featureValue1 = "123";
        final String featureName2 = "host";
        final String featureValue2 = "456";
        Map<String,String> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put(featureName1,featureValue1);
        Map<String,String> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put(featureName2,featureValue2);
        final int max1 = 10;
        final int max2 = 20;
        Map<MultiKeyFeature, Integer> expectedFeaturesGroupToMax = new HashMap<>();
        expectedFeaturesGroupToMax.put(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue1), max1);
        expectedFeaturesGroupToMax.put(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue2), max2);
        assertBucketsAggregatedCorrectly(
                expectedFeaturesGroupToMax,
                new Pair[]{
                        new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue1), max1),
                        new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue2), max2 - 1)
                },
                new Pair[]{
                        new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue2), max2)
                },
                new Pair[]{
                        new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue1), max1 - 1)
                });
    }
}
