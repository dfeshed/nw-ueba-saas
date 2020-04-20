package fortscale.aggregation.feature.functions;

import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class AggrFeatureMultiKeyValuesToMaxMaxFuncTest {

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

        String aggregatedFeatureName = "max_of_highest_scores_over_src_machines_vpn_hourly";
        Feature res = new AggrFeatureMultiKeyValuesToMaxMaxFunc(null).calculateAggrFeature(
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureName, pickFeatureName),
                listOfMaps);

        Assert.assertEquals(max2, ((AggrFeatureValue)res.getValue()).getValue());
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
        Feature res = new AggrFeatureMultiKeyValuesToMaxMaxFunc(null).calculateAggrFeature(
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureName, "not_existing_feature"),
                listOfMaps);

        Assert.assertNotNull(res);
        Assert.assertEquals(0D,  ((AggrFeatureValue) res.getValue()).getValue());
    }

    @Test
    public void testCalculateAggrFeatureWithOperationTypeMultiKeys() {
        Double max1 = 10.0;
        Double max2 = 20.0;
        Double max3 = 30.0;
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
        List<Map<String, String>> contextsToFilterIn = Collections.singletonList(Collections.singletonMap("operationType", "open"));
        AggrFeatureMultiKeyValuesToMaxMaxFunc func = new AggrFeatureMultiKeyValuesToMaxMaxFunc(contextsToFilterIn);

        Feature res = func.calculateAggrFeature(
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureName, pickFeatureName),
                listOfMaps);
        Assert.assertNotNull(res);
        Assert.assertEquals(((AggrFeatureValue)res.getValue()).getValue(),max2);
    }

    @Test
    public void testCalculateAggrFeatureWithOpenAndCloseOfSpecificMachineMultiKeys() {
        Double max1 = 10.0;
        Double max2 = 20.0;
        Double max3 = 30.0;
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
        Map<String, String> firstContextToFilterIn = new HashMap<>();
        firstContextToFilterIn.put("operationType", "open");
        firstContextToFilterIn.put("machine", "host_456");
        Map<String, String> secondContextToFilterIn = new HashMap<>();
        secondContextToFilterIn.put("operationType", "close");
        secondContextToFilterIn.put("machine", "host_456");
        List<Map<String, String>> contextsToFilterIn = Arrays.asList(firstContextToFilterIn, secondContextToFilterIn);
        AggrFeatureMultiKeyValuesToMaxMaxFunc func = new AggrFeatureMultiKeyValuesToMaxMaxFunc(contextsToFilterIn);

        Feature res = func.calculateAggrFeature(
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureName, pickFeatureName),
                listOfMaps);
        Assert.assertNotNull(res);
        Assert.assertEquals(((AggrFeatureValue)res.getValue()).getValue(),max3);
    }


    /**
     * Test zero AggrFeatureValue creation, where no key was met
     */
    @Test
    public void testCalculateAggrFeatureWithNoAppropriateKey() {
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
        List<Map<String, String>> contextsToFilterIn = Collections.singletonList(Collections.singletonMap("operationType", "noValue"));
        AggrFeatureMultiKeyValuesToMaxMaxFunc func = new AggrFeatureMultiKeyValuesToMaxMaxFunc(contextsToFilterIn);

        Feature res = func.calculateAggrFeature(
                AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggregatedFeatureEventConf(aggregatedFeatureName, pickFeatureName),
                listOfMaps);
        Assert.assertNotNull(res);
        Assert.assertEquals(((AggrFeatureValue)res.getValue()).getValue(),0D);
    }
}
