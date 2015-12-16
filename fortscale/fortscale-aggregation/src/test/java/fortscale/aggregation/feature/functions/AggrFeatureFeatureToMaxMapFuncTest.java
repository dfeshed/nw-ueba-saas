package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class AggrFeatureFeatureToMaxMapFuncTest {
    private AggregatedFeatureConf createAggrFeatureConf(String maximizeFeatureName, String... groupByFeatureNames) {
        List<String> groupByFeatureNamesList = new ArrayList<>();
        for (String featureName : groupByFeatureNames) {
            groupByFeatureNamesList.add(featureName);
        }
        List<String> maximizeFeatureNameList = new ArrayList<>();
        maximizeFeatureNameList.add(maximizeFeatureName);

        Map<String, List<String>> featureNamesMap = new HashMap<>();
        featureNamesMap.put(AggrFeatureFeatureToMaxMapFunc.GROUP_BY_FIELD_NAME, groupByFeatureNamesList);
        featureNamesMap.put(AggrFeatureFeatureToMaxMapFunc.MAXIMIZE_FIELD_NAME, maximizeFeatureNameList);
        return new AggregatedFeatureConf("MyAggrFeature", featureNamesMap, new JSONObject());
    }

    private Feature createAggrFeature(Pair<String[], Integer>... featureValuesAndNumbers) {
        Map<List<String>, Integer> featuresGroupToMax = new HashMap<>();
        for (Pair<String[], Integer> featureValuesAndNumber : featureValuesAndNumbers) {
            List<String> featureGroupedByValues = Arrays.asList(featureValuesAndNumber.getLeft());
            featuresGroupToMax.put(featureGroupedByValues, featureValuesAndNumber.getRight());
        }
        return new Feature("MyAggrFeature", new AggrFeatureValue(featuresGroupToMax, (long) featuresGroupToMax.size()));
    }

    private Map createFeatureMap(final ImmutablePair<String, Object>... featureValues) {
        return new HashMap() {{
            for (ImmutablePair<String, Object> featureValue : featureValues) {
                Object value = featureValue.getRight();
                if (value instanceof String) {
                    put(featureValue.getLeft(), new Feature(featureValue.getLeft(), (String) value));
                } else {
                    put(featureValue.getLeft(), new Feature(featureValue.getLeft(), (Integer) value));
                }
            }
        }};
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithWrongAggrFeatureValueType() {
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf("maximizeFeatureName", "groupByFeatureName");
        Feature aggrFeature = new Feature("MyAggrFeature", "I'm a string, not a map");
        new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(aggrFuncConf, new HashMap<String, Feature>(), aggrFeature);
    }

    @Test
    public void shouldReturnUnchangedMapIfGivenEmptyFeatures() {
        String maximizeFeatureName = "event_time_score";
        String groupByFeatureName = "dest_machine";

        int max = 10;
        final String featureGroupedByValue = "host_123";
        Feature aggrFeature = createAggrFeature(new ImmutablePair(new String[]{featureGroupedByValue}, max));

        Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName),
                new HashMap<String, Feature>(),
                aggrFeature);

        Assert.assertTrue(value instanceof AggrFeatureValue);
        Assert.assertEquals(value, aggrFeature.getValue());
        Map<List<String>, Integer> featuresGroupToMax = (Map<List<String>, Integer>) ((AggrFeatureValue) value).getValue();
        Assert.assertEquals(1, featuresGroupToMax.size());
        List<String> groupByFeatureValues = new ArrayList() {{
            add(featureGroupedByValue);
        }};
        Assert.assertEquals(max, featuresGroupToMax.get(groupByFeatureValues).intValue());
    }

    @Test
    public void shouldNotUpdateIfGivenSmallerNumber() {
        final String maximizeFeatureName = "event_time_score";
        final String groupByFeatureName = "dest_machine";

        final int max = 10;
        final String featureGroupedByValue = "host_123";
        Feature aggrFeature = createAggrFeature(new ImmutablePair(new String[]{featureGroupedByValue}, max));
        Map<String, Feature> featureMap = createFeatureMap(
                new ImmutablePair<String, Object>(groupByFeatureName, featureGroupedByValue),
                new ImmutablePair<String, Object>(maximizeFeatureName, max - 1)
        );

        Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName),
                featureMap,
                aggrFeature);
        Map<List<String>, Integer> featuresGroupToMax = (Map<List<String>, Integer>) ((AggrFeatureValue) value).getValue();

        Assert.assertEquals(1, featuresGroupToMax.size());
        List<String> groupByFeatureValues = new ArrayList() {{
            add(featureGroupedByValue);
        }};
        Assert.assertEquals(max, featuresGroupToMax.get(groupByFeatureValues).intValue());
    }

    @Test
    public void shouldUpdateIfGivenBiggerNumber() {
        final String maximizeFeatureName = "event_time_score";
        final String groupByFeatureName = "dest_machine";

        final int max = 10;
        final String featureGroupedByValue = "host_123";
        Feature aggrFeature = createAggrFeature(new ImmutablePair(new String[]{featureGroupedByValue}, max - 1));
        Map<String, Feature> featureMap = createFeatureMap(
                new ImmutablePair<String, Object>(groupByFeatureName, featureGroupedByValue),
                new ImmutablePair<String, Object>(maximizeFeatureName, max)
        );

        Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName),
                featureMap,
                aggrFeature);
        Map<List<String>, Integer> featuresGroupToMax = (Map<List<String>, Integer>) ((AggrFeatureValue) value).getValue();

        Assert.assertEquals(1, featuresGroupToMax.size());
        List<String> groupByFeatureValues = new ArrayList() {{
            add(featureGroupedByValue);
        }};
        Assert.assertEquals(max, featuresGroupToMax.get(groupByFeatureValues).intValue());
    }

    @Test
    public void shouldUpdateIfGivenNewFeature() {
        final String maximizeFeatureName = "event_time_score";
        final String groupByFeatureName = "dest_machine";

        final int max = 10;
        final String featureGroupedByValue1 = "host_123";
        final String featureGroupedByValue2 = "host_456";
        Feature aggrFeature = createAggrFeature(new ImmutablePair(new String[]{featureGroupedByValue1}, max));
        Map<String, Feature> featureMap = createFeatureMap(
                new ImmutablePair<String, Object>(groupByFeatureName, featureGroupedByValue2),
                new ImmutablePair<String, Object>(maximizeFeatureName, max)
        );

        Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName),
                featureMap,
                aggrFeature);
        Map<List<String>, Integer> featuresGroupToMax = (Map<List<String>, Integer>) ((AggrFeatureValue) value).getValue();

        Assert.assertEquals(2, featuresGroupToMax.size());
        List<String> groupByFeatureValues = new ArrayList() {{
            add(featureGroupedByValue2);
        }};
        Assert.assertEquals(max, featuresGroupToMax.get(groupByFeatureValues).intValue());
    }

    @Test
    public void shouldUpdateAfterGroupingByTwoFeatures() {
        final String maximizeFeatureName = "event_time_score";
        final String groupByFeatureName1 = "dest_machine";
        final String groupByFeatureName2 = "src_machine";

        final int maxA = 10;
        final int maxB = 20;
        final String featureGroupedByValue1A = "dest_host_123_A";
        final String featureGroupedByValue2A = "src_host_456_A";
        final String featureGroupedByValue1B = "dest_host_123_B";
        final String featureGroupedByValue2B = "src_host_456_B";
        Feature aggrFeature = createAggrFeature(
                new ImmutablePair(new String[]{featureGroupedByValue1A, featureGroupedByValue2A}, maxA),
                new ImmutablePair(new String[]{featureGroupedByValue1B, featureGroupedByValue2B}, 0));
        Map<String, Feature> featureMap = createFeatureMap(
                new ImmutablePair<String, Object>(groupByFeatureName1, featureGroupedByValue1B),
                new ImmutablePair<String, Object>(groupByFeatureName2, featureGroupedByValue2B),
                new ImmutablePair<String, Object>(maximizeFeatureName, maxB)
        );

        Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                createAggrFeatureConf(maximizeFeatureName, groupByFeatureName1, groupByFeatureName2),
                featureMap,
                aggrFeature);
        Map<List<String>, Integer> featuresGroupToMax = (Map<List<String>, Integer>) ((AggrFeatureValue) value).getValue();

        Assert.assertEquals(2, featuresGroupToMax.size());
        List<String> groupByFeatureValues = new ArrayList() {{
            add(featureGroupedByValue1A);
            add(featureGroupedByValue2A);
        }};
        Assert.assertEquals(maxA, featuresGroupToMax.get(groupByFeatureValues).intValue());
        groupByFeatureValues = new ArrayList() {{
            add(featureGroupedByValue1B);
            add(featureGroupedByValue2B);
        }};
        Assert.assertEquals(maxB, featuresGroupToMax.get(groupByFeatureValues).intValue());
    }

    @Test
    public void shouldNotUpdateIfFeatureIsMissing() {
        for (int missingFeatureType = 0; missingFeatureType < 2; missingFeatureType++) {
            final String maximizeFeatureName = "event_time_score";
            final String groupByFeatureName1 = "dest_machine";
            final String groupByFeatureName2 = "src_machine";

            final int max = 10;
            final String featureGroupedByValue1A = "dest_host_123_A";
            final String featureGroupedByValue2A = "src_host_456_A";
            final String featureGroupedByValue1B = "dest_host_123_B";
            final String featureGroupedByValue2B = "src_host_456_B";
            Feature aggrFeature = createAggrFeature(
                    new ImmutablePair(new String[]{featureGroupedByValue1A, featureGroupedByValue2A}, max),
                    new ImmutablePair(new String[]{featureGroupedByValue1B, featureGroupedByValue2B}, 0));
            ImmutablePair[] featureValues = new ImmutablePair[2];
            featureValues[0] = new ImmutablePair<String, Object>(groupByFeatureName1, featureGroupedByValue1B);
            if (missingFeatureType == 0) {
                // the feature to maximize over is missing
                featureValues[1] = new ImmutablePair<String, Object>(groupByFeatureName2, featureGroupedByValue2B);
            } else {
                // a group by feature is missing
                featureValues[1] = new ImmutablePair<String, Object>(maximizeFeatureName, max);
            }
            Map<String, Feature> featureMap = createFeatureMap(featureValues);

            Object value = new AggrFeatureFeatureToMaxMapFunc().updateAggrFeature(
                    createAggrFeatureConf(maximizeFeatureName, groupByFeatureName1, groupByFeatureName2),
                    featureMap,
                    aggrFeature);
            Map<List<String>, Integer> featuresGroupToMax = (Map<List<String>, Integer>) ((AggrFeatureValue) value).getValue();

            List<String> groupByFeatureValues = new ArrayList() {{
                add(featureGroupedByValue1B);
                add(featureGroupedByValue2B);
            }};
            Assert.assertEquals(0, featuresGroupToMax.get(groupByFeatureValues).intValue());
        }
    }
}
