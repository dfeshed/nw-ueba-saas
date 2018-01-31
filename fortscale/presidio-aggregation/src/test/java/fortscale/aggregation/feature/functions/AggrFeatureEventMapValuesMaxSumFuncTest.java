package fortscale.aggregation.feature.functions;

import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AggrFeatureEventMapValuesMaxSumFuncTest {

    @Test
    public void testCalculateAggrFeature() {
        int max1 = 10;
        int max2 = 20;
        String pickFeatureName = "source_machine_to_highest_score_map";
        List<Map<String, Feature>> listOfMaps = new ArrayList<>();
        listOfMaps.add(AggrFeatureFeatureToMaxRelatedFuncTestUtils.createBucketAggrFeaturesMap(
                pickFeatureName,
                new ImmutablePair<>(new String[]{"host_123"}, max1),
                new ImmutablePair<>(new String[]{"host_456"}, max2)));

        String aggregatedFeatureName = "sum_of_highest_scores_over_src_machines_vpn_hourly";
        Feature res = new AggrFeatureEventMapValuesMaxSumFunc().calculateAggrFeature(
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
        listOfMaps.add(AggrFeatureFeatureToMaxRelatedFuncTestUtils.createBucketAggrFeaturesMap(
                pickFeatureName,
                new ImmutablePair<>(new String[]{"host_123"}, max1),
                new ImmutablePair<>(new String[]{"host_456"}, max2)));

        String aggregatedFeatureName = "sum_of_highest_scores_over_src_machines_vpn_hourly";
        Feature res = new AggrFeatureEventMapValuesMaxSumFunc().calculateAggrFeature(
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureName, "not_existing_feature"),
                listOfMaps);
        Assert.assertNull(res);
    }
}
