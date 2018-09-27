package fortscale.aggregation.feature.functions;

import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

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
                new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue1), max1.intValue()),
                new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue2), max2.intValue())));

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
                new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue1), max1),
                new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue2), max2)));

        String aggregatedFeatureName = "sum_of_highest_scores_over_src_machines_vpn_hourly";
        Feature res = new AggrFeatureEventMultiKeyValuesMaxSumFunc().calculateAggrFeature(
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureName, "not_existing_feature"),
                listOfMaps);
        Assert.assertNull(res);
    }

    @Test
    public void testCalculateAggrFeatureWithOperationTypeMultiKeys() {
        Double max1 = 10.0;
        Double max2 = 20.0;
        Double max3 = 20.0;
        String pickFeatureName = "source_machine_and_operation_type_to_highest_score_map";
        List<Map<String, Feature>> listOfMaps = new ArrayList<>();
        Map<String,String> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("machine","host_123");
        featureNameToValue1.put("operationType","open");

        Map<String,String> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put("machine","host_456");
        featureNameToValue2.put("operationType","open");

        Map<String,String> featureNameToValue3 = new HashMap<>();
        featureNameToValue3.put("machine","host_456");
        featureNameToValue3.put("operationType","close");

        listOfMaps.add(AggrFeatureFeatureToMaxRelatedFuncTestUtils.createBucketAggrFeaturesMap(
                pickFeatureName,
                new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue1), max1.intValue()),
                new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue2), max2.intValue()),
                new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue3), max3.intValue())));

        String aggregatedFeatureName = "sum_of_highest_scores_over_src_machines_vpn_hourly";
        Set<Map<String, String>> keys = new HashSet<>();
        Map<String, String> featureNameToValue = new HashMap<>();
        featureNameToValue.put("operationType","open");
        keys.add(featureNameToValue);

        AggrFeatureEventMultiKeyValuesMaxSumFunc func = new AggrFeatureEventMultiKeyValuesMaxSumFunc();
        func.setKeys(keys);

        Feature res = func.calculateAggrFeature(
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureName, pickFeatureName),
                listOfMaps);
        Assert.assertNotNull(res);
        Assert.assertEquals(((AggrFeatureValue)res.getValue()).getValue(),max1 + max2);
    }

    @Test
    public void testCalculateAggrFeatureWithOpenAndCloseOfSpecificMachineMultiKeys() {
        Double max1 = 10.0;
        Double max2 = 20.0;
        Double max3 = 20.0;
        String pickFeatureName = "source_machine_and_operation_type_to_highest_score_map";
        List<Map<String, Feature>> listOfMaps = new ArrayList<>();
        Map<String,String> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("machine","host_123");
        featureNameToValue1.put("operationType","open");

        Map<String,String> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put("machine","host_456");
        featureNameToValue2.put("operationType","open");

        Map<String,String> featureNameToValue3 = new HashMap<>();
        featureNameToValue3.put("machine","host_456");
        featureNameToValue3.put("operationType","close");

        listOfMaps.add(AggrFeatureFeatureToMaxRelatedFuncTestUtils.createBucketAggrFeaturesMap(
                pickFeatureName,
                new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue1), max1.intValue()),
                new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue2), max2.intValue()),
                new ImmutablePair<>(AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue3), max3.intValue())));

        String aggregatedFeatureName = "sum_of_highest_scores_over_src_machines_vpn_hourly";
        Set<Map<String, String>> keys = new HashSet<>();
        Map<String, String> key1 = new HashMap<>();
        key1.put("operationType","open");
        key1.put("machine","host_456");

        Map<String, String> key2 = new HashMap<>();
        key2.put("operationType","close");
        key2.put("machine","host_456");

        keys.add(key1);
        keys.add(key2);

        AggrFeatureEventMultiKeyValuesMaxSumFunc func = new AggrFeatureEventMultiKeyValuesMaxSumFunc();
        func.setKeys(keys);

        Feature res = func.calculateAggrFeature(
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureName, pickFeatureName),
                listOfMaps);
        Assert.assertNotNull(res);
        Assert.assertEquals(((AggrFeatureValue)res.getValue()).getValue(),max3 + max2);
    }


}
