package fortscale.aggregation.feature.functions;


import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class AggrFeatureFeatureToMaxMapFuncTest {
    private AggregatedFeatureConf createAggrFeatureConf(String maximizeFeatureName, String... groupByFeatureNames) {
        Map<String, List<String>> featureNamesMap = new HashMap<>();
        if (groupByFeatureNames.length > 0) {
            featureNamesMap.put(AggrFeatureFeatureToMaxMapFunc.GROUP_BY_FIELD_NAME, Arrays.asList(groupByFeatureNames));
        }
        featureNamesMap.put(AggrFeatureFeatureToMaxMapFunc.MAXIMIZE_FIELD_NAME, Collections.singletonList(maximizeFeatureName));
        return new AggregatedFeatureConf("MyAggrFeature", featureNamesMap, new JSONObject());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithWrongAggrFeatureValueType() {
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf("maximizeFeatureName", "groupByFeatureName");
        Feature aggrFeature = new Feature("MyAggrFeature", "I'm a string, not a map");
        new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(aggrFuncConf, new HashMap<>(), aggrFeature);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnUnchangedMapIfGivenEmptyFeatures() {
        String maximizeFeatureName = "event_time_score";
        String groupByFeatureName = "dest_machine";

        int max = 10;
        final String featureGroupedByValue = "dest_machine#host_123";
        Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature("MyAggrFeature", new ImmutablePair<>(new String[]{featureGroupedByValue}, max));

        Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName),
                new HashMap<>(),
                aggrFeature);

        Assert.assertTrue(value instanceof AggrFeatureValue);
        Assert.assertEquals(value, aggrFeature.getValue());
        Map<String, Double> featuresGroupToMax = (Map<String, Double>)((AggrFeatureValue)value).getValue();
        Assert.assertEquals(1, featuresGroupToMax.size());
        Assert.assertEquals(max, featuresGroupToMax.get(featureGroupedByValue).intValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotUpdateIfGivenSmallerNumber() {
        final String maximizeFeatureName = "event_time_score";
        final String groupByFeatureName = "dest_machine";

        final int max = 10;
        final String featureGroupedByValue = "host_123";
        final String featureNameAndValue = groupByFeatureName + AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY + featureGroupedByValue;
        Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature("MyAggrFeature", new ImmutablePair<>(new String[]{featureNameAndValue}, max));
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(groupByFeatureName, featureGroupedByValue),
                new ImmutablePair<>(maximizeFeatureName, max - 1)
        );

        Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName),
                featureMap,
                aggrFeature);
        Map<String, Double> featuresGroupToMax = (Map<String, Double>)((AggrFeatureValue)value).getValue();

        Assert.assertEquals(1, featuresGroupToMax.size());
        Assert.assertEquals(max, featuresGroupToMax.get(featureNameAndValue).intValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateIfGivenBiggerNumber() {
        final String maximizeFeatureName = "event_time_score";
        final String groupByFeatureName = "dest_machine";

        final int max = 10;
        final String featureGroupedByValue = "host_123";
        final String featureNameAndValue = groupByFeatureName + AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY + featureGroupedByValue;
        Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature("MyAggrFeature", new ImmutablePair<>(new String[]{featureNameAndValue}, max - 1));
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(groupByFeatureName, featureGroupedByValue),
                new ImmutablePair<>(maximizeFeatureName, max)
        );

        Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName),
                featureMap,
                aggrFeature);
        Map<String, Double> featuresGroupToMax = (Map<String, Double>)((AggrFeatureValue)value).getValue();

        Assert.assertEquals(1, featuresGroupToMax.size());
        Assert.assertEquals(max, featuresGroupToMax.get(featureNameAndValue).intValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateIfGivenNewFeature() {
        final String maximizeFeatureName = "event_time_score";
        final String groupByFeatureName = "dest_machine";

        final int max = 10;
        final String featureGroupedByValue1 = "host_123";
        final String featureNameAndValue1 = groupByFeatureName + AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY + featureGroupedByValue1;
        final String featureGroupedByValue2 = "host_456";
        final String featureNameAndValue2 = groupByFeatureName + AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY + featureGroupedByValue2;
        Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature("MyAggrFeature", new ImmutablePair<>(new String[]{featureNameAndValue1}, max));
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(groupByFeatureName, featureGroupedByValue2),
                new ImmutablePair<>(maximizeFeatureName, max)
        );

        Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName),
                featureMap,
                aggrFeature);
        Map<String, Double> featuresGroupToMax = (Map<String, Double>)((AggrFeatureValue)value).getValue();

        Assert.assertEquals(2, featuresGroupToMax.size());
        Assert.assertEquals(max, featuresGroupToMax.get(featureNameAndValue2).intValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateWhenGroupingBy() {
        final String maximizeFeatureName = "event_time_score";

        final int max = 10;
        Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature("MyAggrFeature", new ImmutablePair<>(new String[]{}, max - 1));
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>("dest_machine", "host_456"),
                new ImmutablePair<>(maximizeFeatureName, max)
        );

        Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName),
                featureMap,
                aggrFeature);
        Map<String, Double> featuresGroupToMax = (Map<String, Double>)((AggrFeatureValue)value).getValue();

        Assert.assertEquals(1, featuresGroupToMax.size());
        Assert.assertEquals(max, featuresGroupToMax.get("").intValue());
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
        final String featureNameAndValue1A = groupByFeatureName1 + AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY + featureGroupedByValue1A;
        final String featureGroupedByValue2A = "src_host_456_A";
        final String featureNameAndValue2A = groupByFeatureName2 + AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY + featureGroupedByValue2A;
        final String featureNameAndValueA = featureNameAndValue1A + AggrFeatureFeatureToMaxMapFunc.FEATURE_GROUP_SEPARATOR_KEY + featureNameAndValue2A;
        final String featureGroupedByValue1B = "dest_host_123_B";
        final String featureNameAndValue1B = groupByFeatureName1 + AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY + featureGroupedByValue1B;
        final String featureGroupedByValue2B = "src_host_456_B";
        final String featureNameAndValue2B = groupByFeatureName2 + AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY + featureGroupedByValue2B;
        final String featureNameAndValueB = featureNameAndValue1B + AggrFeatureFeatureToMaxMapFunc.FEATURE_GROUP_SEPARATOR_KEY + featureNameAndValue2B;
        Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature(
                "MyAggrFeature",
                new ImmutablePair<>(new String[]{featureNameAndValue1A, featureNameAndValue2A}, maxA),
                new ImmutablePair<>(new String[]{featureNameAndValue1B, featureNameAndValue2B}, 0));
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(groupByFeatureName1, featureGroupedByValue1B),
                new ImmutablePair<>(groupByFeatureName2, featureGroupedByValue2B),
                new ImmutablePair<>(maximizeFeatureName, maxB)
        );

        Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName1, groupByFeatureName2),
                featureMap,
                aggrFeature);
        Map<String, Double> featuresGroupToMax = (Map<String, Double>)((AggrFeatureValue)value).getValue();

        Assert.assertEquals(2, featuresGroupToMax.size());
        Assert.assertEquals(maxA, featuresGroupToMax.get(featureNameAndValueA).intValue());
        Assert.assertEquals(maxB, featuresGroupToMax.get(featureNameAndValueB).intValue());
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
            final String featureNameAndValue1A = groupByFeatureName1 + AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY + featureGroupedByValue1A;
            final String featureGroupedByValue2A = "src_host_456_A";
            final String featureNameAndValue2A = groupByFeatureName2 + AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY + featureGroupedByValue2A;
            final String featureGroupedByValue1B = "dest_host_123_B";
            final String featureNameAndValue1B = groupByFeatureName1 + AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY + featureGroupedByValue1B;
            final String featureGroupedByValue2B = "src_host_456_B";
            final String featureNameAndValue2B = groupByFeatureName2 + AggrFeatureFeatureToMaxMapFunc.FEATURE_SEPARATOR_KEY + featureGroupedByValue2B;
            final String featureNameAndValueB = featureNameAndValue1B + AggrFeatureFeatureToMaxMapFunc.FEATURE_GROUP_SEPARATOR_KEY + featureNameAndValue2B;
            Feature aggrFeature = AggrFeatureFeatureToMaxRelatedFuncTestUtils.createAggrFeature(
                    "MyAggrFeature",
                    new ImmutablePair<>(new String[]{featureNameAndValue1A, featureNameAndValue2A}, max),
                    new ImmutablePair<>(new String[]{featureNameAndValue1B, featureNameAndValue2B}, 0));
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

            Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                    createAggrFeatureConf(maximizeFeatureName, groupByFeatureName1, groupByFeatureName2),
                    featureMap,
                    aggrFeature);
            Map<String, Double> featuresGroupToMax = (Map<String, Double>)((AggrFeatureValue)value).getValue();

            Assert.assertEquals(testVal, featuresGroupToMax.get(featureNameAndValueB).intValue());
        }
    }
}
