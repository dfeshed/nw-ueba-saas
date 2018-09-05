package fortscale.aggregation.feature.functions;

import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggrFeatureEventMultiKeyValuesMaxSumFuncTest {
    @Test
    public void testCalculateAggrFeature() {
        Double max1 = 10.0;
        Double max2 = 20.0;
        String pickFeatureName = "source_machine_to_highest_score_map";
        List<Map<String, Feature>> listOfMaps = new ArrayList<>();

        Map<String,String> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("machine","host_123");
        Map<String,String> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put("machine","host_456");
        listOfMaps.add(AggrFeatureFeatureToMaxRelatedFuncTestUtils.createBucketAggrFeaturesMap(
                pickFeatureName,
                new ImmutablePair<>(AggrFeatureFeatureToMaxRelatedFuncTestUtils.createMultiKeyFeature(featureNameToValue1), max1.intValue()),
                new ImmutablePair<>(AggrFeatureFeatureToMaxRelatedFuncTestUtils.createMultiKeyFeature(featureNameToValue2), max2.intValue())));

        String aggregatedFeatureName = "sum_of_highest_scores_over_src_machines_vpn_hourly";
        Feature res = new AggrFeatureEventMultiKeyValuesMaxSumFunc().calculateAggrFeature(
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureName, pickFeatureName),
                listOfMaps);

        Assert.assertEquals(max1 + max2, ((AggrFeatureValue)res.getValue()).getValue());
    }

    @Test
    public void testCalculateAggrFeatureWhenFeatureNotExist() {
        int max1 = 10;
        int max2 = 20;
        String pickFeatureName = "source_machine_to_highest_score_map";
        List<Map<String, Feature>> listOfMaps = new ArrayList<>();
        Map<String,String> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("machine","host_123");
        Map<String,String> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put("machine","host_456");
        listOfMaps.add(AggrFeatureFeatureToMaxRelatedFuncTestUtils.createBucketAggrFeaturesMap(
                pickFeatureName,
                new ImmutablePair<>(AggrFeatureFeatureToMaxRelatedFuncTestUtils.createMultiKeyFeature(featureNameToValue1), max1),
                new ImmutablePair<>(AggrFeatureFeatureToMaxRelatedFuncTestUtils.createMultiKeyFeature(featureNameToValue2), max2)));

        String aggregatedFeatureName = "sum_of_highest_scores_over_src_machines_vpn_hourly";
        Feature res = new AggrFeatureEventMultiKeyValuesMaxSumFunc().calculateAggrFeature(
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureName, "not_existing_feature"),
                listOfMaps);
        Assert.assertNull(res);
    }

}
