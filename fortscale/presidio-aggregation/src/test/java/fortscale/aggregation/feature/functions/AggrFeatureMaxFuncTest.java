package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureValue;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.common.feature.MultiKeyHistogram;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AggrFeatureMaxFuncTest {
    @Test
    public void should_return_original_aggregated_feature_if_configuration_is_null() {
        Map.Entry<MultiKeyFeature, Double> actual = getEntry(new AggrFeatureMaxFunc().updateAggrFeature(
                null,
                getNameToFeatureMap("score", 100.0, "userId", "alice@rsa.com", "machineId", "ALICE-PC1"),
                getAggregatedFeature(90.0, "userId", "bob@rsa.com", "machineId", "BOB-PC1")
        ));
        assertEquals("bob@rsa.com", actual.getKey().getFeatureNameToValue().get("userId"));
        assertEquals("BOB-PC1", actual.getKey().getFeatureNameToValue().get("machineId"));
        assertEquals(90.0, actual.getValue(), 0.0);
    }

    @Test
    public void should_return_original_aggregated_feature_if_map_of_record_fields_is_null() {
        Map.Entry<MultiKeyFeature, Double> actual = getEntry(new AggrFeatureMaxFunc().updateAggrFeature(
                getAggregatedFeatureConf(singletonList("score"), singletonList("startInstant")),
                null,
                getAggregatedFeature(80.0, "userId", "cat@rsa.com", "machineId", "CAT-PC1")
        ));
        assertEquals("cat@rsa.com", actual.getKey().getFeatureNameToValue().get("userId"));
        assertEquals("CAT-PC1", actual.getKey().getFeatureNameToValue().get("machineId"));
        assertEquals(80.0, actual.getValue(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_if_aggregated_feature_conf_is_missing_maximize_parameter() {
        new AggrFeatureMaxFunc().updateAggrFeature(
                getAggregatedFeatureConf(null, singletonList("startInstant")),
                getNameToFeatureMap("score", 70.0, "userId", "dave@rsa.com", "machineId", "DAVE-PC1"),
                getAggregatedFeature(60.0, "userId", "emily@rsa.com", "machineId", "EMILY-PC1")
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_if_aggregated_feature_conf_contains_more_than_one_maximize_parameter() {
        new AggrFeatureMaxFunc().updateAggrFeature(
                getAggregatedFeatureConf(asList("myScore", "yourScore"), singletonList("startInstant")),
                getNameToFeatureMap("score", 50.0, "userId", "fred@rsa.com", "machineId", "FRED-PC1"),
                getAggregatedFeature(40.0, "userId", "george@rsa.com", "machineId", "GEORGE-PC1")
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_if_maximize_field_is_not_numeric() {
        new AggrFeatureMaxFunc().updateAggrFeature(
                getAggregatedFeatureConf(singletonList("score"), singletonList("startInstant")),
                getNameToFeatureMap("score", "NaN", "userId", "helen@rsa.com", "machineId", "HELEN-PC1"),
                getAggregatedFeature(30.0, "userId", "isaac@rsa.com", "machineId", "ISAAC-PC1")
        );
    }

    @Test
    public void should_return_original_aggregated_feature_if_maximize_field_value_is_null() {
        Map.Entry<MultiKeyFeature, Double> actual = getEntry(new AggrFeatureMaxFunc().updateAggrFeature(
                getAggregatedFeatureConf(singletonList("score"), singletonList("startInstant")),
                getNameToFeatureMap("score", null, "userId", "jane@rsa.com", "machineId", "JANE-PC1"),
                getAggregatedFeature(20.0, "userId", "kyle@rsa.com", "machineId", "KYLE-PC1")
        ));
        assertEquals("kyle@rsa.com", actual.getKey().getFeatureNameToValue().get("userId"));
        assertEquals("KYLE-PC1", actual.getKey().getFeatureNameToValue().get("machineId"));
        assertEquals(20.0, actual.getValue(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_if_original_aggregated_feature_is_not_histogram() {
        new AggrFeatureMaxFunc().updateAggrFeature(
                getAggregatedFeatureConf(singletonList("score"), singletonList("startInstant")),
                getNameToFeatureMap("score", 10.0, "userId", "leo@rsa.com", "machineId", "LEO-PC1"),
                new Feature("myAggregatedFeature", 0.0)
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_if_original_aggregated_feature_has_more_than_one_entry_in_histogram() {
        MultiKeyFeature firstContext = new MultiKeyFeature();
        firstContext.add("userId", "mia@rsa.com");
        firstContext.add("machineId", "MIA-PC1");
        MultiKeyFeature secondContext = new MultiKeyFeature();
        secondContext.add("userId", "nick@rsa.com");
        secondContext.add("machineId", "NICK-PC1");
        MultiKeyHistogram histogram = new MultiKeyHistogram();
        histogram.set(firstContext, 95.0);
        histogram.set(secondContext, 85.0);
        new AggrFeatureMaxFunc().updateAggrFeature(
                getAggregatedFeatureConf(singletonList("score"), singletonList("startInstant")),
                getNameToFeatureMap("score", 75.0, "userId", "oscar@rsa.com", "machineId", "OSCAR-PC1"),
                new Feature("my AggregatedFeature", histogram)
        );
    }

    @Test
    public void should_return_new_aggregated_feature_with_all_contexts() {
        Map.Entry<MultiKeyFeature, Double> actual = getEntry(new AggrFeatureMaxFunc().updateAggrFeature(
                getAggregatedFeatureConf(singletonList("score"), asList("userId", "machineId", "startInstant")),
                getNameToFeatureMap("score", 65.0, "userId", "paul@rsa.com", "machineId", "PAUL-PC1", "startInstant", 1514764800),
                getAggregatedFeature(55.0, "userId", "queen@rsa.com", "machineId", "QUEEN-PC1", "startInstant", "1514764800")
        ));
        assertEquals("paul@rsa.com", actual.getKey().getFeatureNameToValue().get("userId"));
        assertEquals("PAUL-PC1", actual.getKey().getFeatureNameToValue().get("machineId"));
        assertEquals("1514764800", actual.getKey().getFeatureNameToValue().get("startInstant"));
        assertEquals(65.0, actual.getValue(), 0.0);
    }

    @Test
    public void should_return_new_aggregated_feature_with_missing_contexts() {
        Map.Entry<MultiKeyFeature, Double> actual = getEntry(new AggrFeatureMaxFunc().updateAggrFeature(
                getAggregatedFeatureConf(singletonList("score"), asList("userId", "machineId", "startInstant")),
                getNameToFeatureMap("score", 45.0, "userId", "rita@rsa.com", "machineId", "RITA-PC1"),
                getAggregatedFeature(35.0, "userId", "sarah@rsa.com", "machineId", "SARAH-PC1", "startInstant", "1514764800")
        ));
        assertEquals("rita@rsa.com", actual.getKey().getFeatureNameToValue().get("userId"));
        assertEquals("RITA-PC1", actual.getKey().getFeatureNameToValue().get("machineId"));
        assertNull(actual.getKey().getFeatureNameToValue().get("startInstant"));
        assertEquals(45.0, actual.getValue(), 0.0);
    }

    @Test
    public void should_return_original_aggregated_feature() {
        Map.Entry<MultiKeyFeature, Double> actual = getEntry(new AggrFeatureMaxFunc().updateAggrFeature(
                getAggregatedFeatureConf(singletonList("score"), asList("userId", "machineId", "startInstant")),
                getNameToFeatureMap("score", 15.0, "userId", "tim@rsa.com", "machineIs", "TIM-PC1", "startInstant", 1514764800),
                getAggregatedFeature(25.0, "userId", "uma@rsa.com", "machineId", "UMA-PC1", "startInstant", "1514764800")
        ));
        assertEquals("uma@rsa.com", actual.getKey().getFeatureNameToValue().get("userId"));
        assertEquals("UMA-PC1", actual.getKey().getFeatureNameToValue().get("machineId"));
        assertEquals("1514764800", actual.getKey().getFeatureNameToValue().get("startInstant"));
        assertEquals(25.0, actual.getValue(), 0.0);
    }

    private static AggregatedFeatureConf getAggregatedFeatureConf(
            List<String> maximizeFieldNames, List<String> contextFieldNames) {

        AggregatedFeatureConf aggregatedFeatureConf = mock(AggregatedFeatureConf.class);
        Map<String, List<String>> featureNamesMap = new HashMap<>();
        if (maximizeFieldNames != null) featureNamesMap.put("maximize", maximizeFieldNames);
        if (contextFieldNames != null) featureNamesMap.put("context", contextFieldNames);
        when(aggregatedFeatureConf.getFeatureNamesMap()).thenReturn(featureNamesMap);
        return aggregatedFeatureConf;
    }

    private static Map<String, Feature> getNameToFeatureMap(Object... namesAndValuesArray) {
        if (namesAndValuesArray == null || namesAndValuesArray.length % 2 != 0) throw new TestException();
        Map<String, Feature> nameToFeatureMap = new HashMap<>(namesAndValuesArray.length / 2);

        for (int i = 0; i < namesAndValuesArray.length; i += 2) {
            String name = (String)namesAndValuesArray[i];
            Object value = namesAndValuesArray[i + 1];
            if (value == null) nameToFeatureMap.put(name, null);
            else if (value instanceof String) nameToFeatureMap.put(name, new Feature(name, (String)value));
            else if (value instanceof Number) nameToFeatureMap.put(name, new Feature(name, (Number)value));
            else throw new TestException();
        }

        return nameToFeatureMap;
    }

    private static Feature getAggregatedFeature(Double maximumValue, String... context) {
        if (context == null || context.length % 2 != 0) throw new TestException();
        MultiKeyFeature multiKeyFeature = new MultiKeyFeature();
        for (int i = 0; i < context.length; i += 2) multiKeyFeature.add(context[i], context[i + 1]);
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();
        multiKeyHistogram.set(multiKeyFeature, maximumValue);
        return new Feature("myAggregatedFeature", multiKeyHistogram);
    }

    private static Map.Entry<MultiKeyFeature, Double> getEntry(FeatureValue featureValue) {
        Set<Map.Entry<MultiKeyFeature, Double>> entries = ((MultiKeyHistogram)featureValue).getHistogram().entrySet();
        if (entries.size() != 1) throw new TestException();
        return entries.iterator().next();
    }

    private static final class TestException extends RuntimeException {}
}
