package fortscale.aggregation.feature.functions;


import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.common.feature.Feature;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
public class AggrFeatureMultiKeyToMaxFuncTest {
    private AggregatedFeatureConf createAggrFeatureConf(String maximizeFeatureName, String... groupByFeatureNames) {
        Map<String, List<String>> featureNamesMap = new HashMap<>();
        if (groupByFeatureNames.length > 0) {
            featureNamesMap.put(AggrFeatureMultiKeyToMaxFunc.GROUP_BY_FIELD_NAME, Arrays.asList(groupByFeatureNames));
        }
        featureNamesMap.put(AggrFeatureMultiKeyToMaxFunc.MAXIMIZE_FIELD_NAME, Collections.singletonList(maximizeFeatureName));
        return new AggregatedFeatureConf("MyAggrFeature", featureNamesMap, new JSONObject());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithWrongAggrFeatureValueType() {
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf("maximizeFeatureName", "groupByFeatureName");
        Feature aggrFeature = new Feature("MyAggrFeature", "I'm a string, not a MultiKeyHistogram");
        new AggrFeatureMultiKeyToMaxFunc().updateAggrFeature(aggrFuncConf, new HashMap<>(), aggrFeature);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnUnchangedMapIfGivenEmptyFeatures() {
        String maximizeFeatureName = "event_time_score";
        String groupByFeatureName = "dest_machine";
        final String featureGroupedByValue = "host_123";
        Map<String,String> featureNameToValue = new HashMap<>();
        featureNameToValue.put(groupByFeatureName,featureGroupedByValue);

        int max = 10;
        MultiKeyFeature multiKeyFeature = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue);
        Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature("MyAggrFeature", new ImmutablePair<>(multiKeyFeature, max));
        Object value = new AggrFeatureMultiKeyToMaxFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName),
                new HashMap<>(),
                aggrFeature);

        Assert.assertTrue(value instanceof MultiKeyHistogram);
        Assert.assertEquals(value, aggrFeature.getValue());
        Map<MultiKeyFeature, Double> featuresGroupToMax = ((MultiKeyHistogram)value).getHistogram();
        Assert.assertEquals(1, featuresGroupToMax.size());
        Assert.assertEquals(max, featuresGroupToMax.get(multiKeyFeature).intValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotUpdateIfGivenSmallerNumber() {
        final String maximizeFeatureName = "event_time_score";
        final String groupByFeatureName = "dest_machine";
        final int max = 10;
        final String featureGroupedByValue = "host_123";
        Map<String,String> featureNameToValue = new HashMap<>();
        featureNameToValue.put(groupByFeatureName,featureGroupedByValue);

        MultiKeyFeature multiKeyFeature = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue);
        Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature("MyAggrFeature", new ImmutablePair<>(multiKeyFeature, max));
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(groupByFeatureName, featureGroupedByValue),
                new ImmutablePair<>(maximizeFeatureName, max - 1)
        );

        Object value = new AggrFeatureMultiKeyToMaxFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName),
                featureMap,
                aggrFeature);
        Map<MultiKeyFeature, Double> featuresGroupToMax = ((MultiKeyHistogram)value).getHistogram();

        Assert.assertEquals(1, featuresGroupToMax.size());
        Assert.assertEquals(max, featuresGroupToMax.get(multiKeyFeature).intValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateIfGivenBiggerNumber() {
        final String maximizeFeatureName = "event_time_score";
        final String groupByFeatureName = "dest_machine";

        final int max = 10;
        final String featureGroupedByValue = "host_123";
        Map<String,String> featureNameToValue = new HashMap<>();
        featureNameToValue.put(groupByFeatureName,featureGroupedByValue);

        MultiKeyFeature multiKeyFeature = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue);
        Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature("MyAggrFeature", new ImmutablePair<>(multiKeyFeature, max - 1));
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(groupByFeatureName, featureGroupedByValue),
                new ImmutablePair<>(maximizeFeatureName, max)
        );

        Object value = new AggrFeatureMultiKeyToMaxFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName),
                featureMap,
                aggrFeature);
        Map<MultiKeyFeature, Double> featuresGroupToMax = ((MultiKeyHistogram)value).getHistogram();

        Assert.assertEquals(1, featuresGroupToMax.size());
        Assert.assertEquals(max, featuresGroupToMax.get(multiKeyFeature).intValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateIfGivenNewFeature() {
        final String maximizeFeatureName = "event_time_score";
        final String groupByFeatureName = "dest_machine";

        final int max = 10;
        final String featureGroupedByValue1 = "host_123";
        Map<String,String> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put(groupByFeatureName,featureGroupedByValue1);
        MultiKeyFeature multiKeyFeature1 = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue1);

        final String featureGroupedByValue2 = "host_456";
        Map<String,String> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put(groupByFeatureName,featureGroupedByValue2);
        MultiKeyFeature multiKeyFeature2 = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue2);
        Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature("MyAggrFeature", new ImmutablePair<>(multiKeyFeature1, max));
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(groupByFeatureName, featureGroupedByValue2),
                new ImmutablePair<>(maximizeFeatureName, max)
        );

        Object value = new AggrFeatureMultiKeyToMaxFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName),
                featureMap,
                aggrFeature);
        Map<MultiKeyFeature, Double> featuresGroupToMax = ((MultiKeyHistogram)value).getHistogram();

        Assert.assertEquals(2, featuresGroupToMax.size());
        Assert.assertEquals(max, featuresGroupToMax.get(multiKeyFeature2).intValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateWhenGroupingBy() {
        final String maximizeFeatureName = "event_time_score";

        final int max = 10;
        MultiKeyFeature multiKeyFeature = new MultiKeyFeature(new HashMap<>());
        Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature("MyAggrFeature", new ImmutablePair<>(multiKeyFeature, max - 1));
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>("dest_machine", "host_456"),
                new ImmutablePair<>(maximizeFeatureName, max)
        );

        Object value = new AggrFeatureMultiKeyToMaxFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName),
                featureMap,
                aggrFeature);
        Map<MultiKeyFeature, Double> featuresGroupToMax = ((MultiKeyHistogram)value).getHistogram();

        Assert.assertEquals(1, featuresGroupToMax.size());
        Assert.assertEquals(max, featuresGroupToMax.get(multiKeyFeature).intValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateWhenGroupingByTwoFeatures() {
        final String maximizeFeatureName = "event_time_score";
        final String groupByFeatureName1 = "dest_machine";
        final String groupByFeatureName2 = "src_machine";

        final int maxA = 10;
        final int maxB = 20;
        final String featureGroupedByValue1A = "dest_host_123_A";
        final String featureGroupedByValue2A = "src_host_456_A";

        Map<String,String> featureNameToValueA = new HashMap<>();
        featureNameToValueA.put(groupByFeatureName1,featureGroupedByValue1A);
        featureNameToValueA.put(groupByFeatureName2,featureGroupedByValue2A);
        MultiKeyFeature multiKeyFeatureA = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValueA);
        final String featureGroupedByValue1B = "dest_host_123_B";
        final String featureGroupedByValue2B = "src_host_456_B";
        Map<String,String> featureNameToValueB = new HashMap<>();
        featureNameToValueB.put(groupByFeatureName1,featureGroupedByValue1B);
        featureNameToValueB.put(groupByFeatureName2,featureGroupedByValue2B);
        MultiKeyFeature multiKeyFeatureB = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValueB);
        Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature(
                "MyAggrFeature",
                new ImmutablePair<>(multiKeyFeatureA, maxA),
                new ImmutablePair<>(multiKeyFeatureB, 0));
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(groupByFeatureName1, featureGroupedByValue1B),
                new ImmutablePair<>(groupByFeatureName2, featureGroupedByValue2B),
                new ImmutablePair<>(maximizeFeatureName, maxB)
        );

        Object value = new AggrFeatureMultiKeyToMaxFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName1, groupByFeatureName2),
                featureMap,
                aggrFeature);
        Map<MultiKeyFeature, Double> featuresGroupToMax = ((MultiKeyHistogram)value).getHistogram();

        Assert.assertEquals(2, featuresGroupToMax.size());
        Assert.assertEquals(maxA, featuresGroupToMax.get(multiKeyFeatureA).intValue());
        Assert.assertEquals(maxB, featuresGroupToMax.get(multiKeyFeatureB).intValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotUpdateIfFeatureIsMissing() {
        for (int missingFeatureType = 0; missingFeatureType < 3; missingFeatureType++) {
            final String maximizeFeatureName = "event_time_score";
            final String groupByFeatureName1 = "dest_machine";
            final String groupByFeatureName2 = "src_machine";

            final int max = 10;
            final String featureGroupedByValue1A = "dest_host_123_A";
            final String featureGroupedByValue2A = "src_host_456_A";
            final String featureGroupedByValue1B = "dest_host_123_B";
            final String featureGroupedByValue2B = "src_host_456_B";
            Map<String,String> featureNameToValueA = new HashMap<>();
            featureNameToValueA.put(groupByFeatureName1,featureGroupedByValue1A);
            featureNameToValueA.put(groupByFeatureName2,featureGroupedByValue2A);
            MultiKeyFeature multiKeyFeatureA = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValueA);
            Map<String,String> featureNameToValueB = new HashMap<>();
            featureNameToValueB.put(groupByFeatureName1,featureGroupedByValue1B);
            featureNameToValueB.put(groupByFeatureName2,featureGroupedByValue2B);
            MultiKeyFeature multiKeyFeatureB = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValueB);
            Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature(
                    "MyAggrFeature",
                    new ImmutablePair<>(multiKeyFeatureA, max),
                    new ImmutablePair<>(multiKeyFeatureB, 0));
            ImmutablePair<String, Object>[] featureValues;
            int testVal = 0;
            if (missingFeatureType == 0) {
                // the feature to maximize over is missing
                featureValues = new ImmutablePair[2];
                featureValues[1] = new ImmutablePair<>(groupByFeatureName2, featureGroupedByValue2B);
            } else {
                featureValues = new ImmutablePair[3];
                featureValues[1] = new ImmutablePair<>(groupByFeatureName2, featureGroupedByValue2B);
                featureValues[2] = new ImmutablePair<>(maximizeFeatureName, max);
                testVal = max;
            }
            featureValues[0] = new ImmutablePair<>(groupByFeatureName1, featureGroupedByValue1B);

            Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(featureValues);

            Object value = new AggrFeatureMultiKeyToMaxFunc().updateAggrFeature(
                    createAggrFeatureConf(maximizeFeatureName, groupByFeatureName1, groupByFeatureName2),
                    featureMap,
                    aggrFeature);
            Map<MultiKeyFeature, Double> featuresGroupToMax = ((MultiKeyHistogram)value).getHistogram();

            Assert.assertEquals(testVal, featuresGroupToMax.get(multiKeyFeatureB).intValue());
        }
    }
}
