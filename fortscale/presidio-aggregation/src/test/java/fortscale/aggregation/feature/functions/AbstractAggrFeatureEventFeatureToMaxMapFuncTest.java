package fortscale.aggregation.feature.functions;


import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class AbstractAggrFeatureEventFeatureToMaxMapFuncTest {

    private void assertBucketsAggregatedCorrectly(final Map<String[], Integer> expectedFeaturesGroupToMax, Pair<String[], Integer>[]... featureValuesAndNumbersInBucketList) {
        String pickFeatureName = "source_machine_to_highest_score_map";

        final boolean[] calculateMapAggrFeatureValueWasCalled = {false};
        AbstractAggrFeatureEventFeatureToMaxMapFunc f = new AbstractAggrFeatureEventFeatureToMaxMapFunc() {
            @Override
            protected AggrFeatureValue calculateFeaturesGroupToMaxValue(AggrFeatureValue aggrFeatureValue) {
                Map<String, Double> featuresGroupToMax = (Map<String, Double>)aggrFeatureValue.getValue();
                Assert.assertEquals(expectedFeaturesGroupToMax.size(), featuresGroupToMax.size());
                for (Map.Entry<String[], Integer> entry : expectedFeaturesGroupToMax.entrySet()) {
                    String groupByFeatureValuesKey = generateGroupByFeatureValuesKey(entry.getKey());
                    Assert.assertEquals(entry.getValue().intValue(), featuresGroupToMax.get(groupByFeatureValuesKey).intValue());
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

    private String generateGroupByFeatureValuesKey(String[] features){
        StringBuilder builder = new StringBuilder();
        for(String feature: features){
            if(builder.length() > 0){
                builder.append(AggrFeatureFeatureToMaxMapFunc.FEATURE_GROUP_SEPARATOR_KEY);
            }
            builder.append(feature);
        }
        return builder.toString();
    }

    @Test
    public void shouldCreateTheSameMappingGivenOnlyOneBucket() {
        final String feature = "host_123";
        final int max = 10;
        Map<String[], Integer> expectedFeaturesGroupToMax = new HashMap<>();
        expectedFeaturesGroupToMax.put(new String[]{feature}, max);
        assertBucketsAggregatedCorrectly(
                expectedFeaturesGroupToMax,
                new Pair[]{new ImmutablePair<>(new String[]{feature}, max)});
    }

    @Test
    public void shouldCreateMappingContainingMaxValuesGivenMultipleBuckets() {
        final String feature1 = "host_123";
        final String feature2 = "host_456";
        final int max1 = 10;
        final int max2 = 20;
        Map<String[], Integer> expectedFeaturesGroupToMax = new HashMap<>();
        expectedFeaturesGroupToMax.put(new String[]{feature1}, max1);
        expectedFeaturesGroupToMax.put(new String[]{feature2}, max2);
        assertBucketsAggregatedCorrectly(
                expectedFeaturesGroupToMax,
                new Pair[]{
                        new ImmutablePair<>(new String[]{feature1}, max1),
                        new ImmutablePair<>(new String[]{feature2}, max2 - 1)
                },
                new Pair[]{
                        new ImmutablePair<>(new String[]{feature2}, max2)
                },
                new Pair[]{
                        new ImmutablePair<>(new String[]{feature1}, max1 - 1)
                });
    }
}
