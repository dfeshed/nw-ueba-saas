package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.common.feature.*;
import fortscale.utils.AggrFeatureFunctionUtils;
import fortscale.utils.data.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AggrFeatureMultiKeyHistogramFuncTest {

    @Test
    public void testUpdateAggregatedFeature() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        Map<String, String> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("feature1", "open");
        featureNameToValue1.put("feature2", "SUCCESS");
        double val1 = 9.0;
        MultiKeyFeature multiKeyFeature1 = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue1);
        multiKeyHistogram.set(multiKeyFeature1, val1);

        Map<String, String> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put("feature1", "move");
        featureNameToValue2.put("feature2", "SUCCESS");
        double val2 = 5.0;
        MultiKeyFeature multiKeyFeature2 = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue2);
        multiKeyHistogram.set(multiKeyFeature2, val2);

        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<String, Object>("feature1", new FeatureStringValue("open")),
                new ImmutablePair<String, Object>("feature2", new FeatureStringValue("SUCCESS"))
        );

        Feature aggrFeature = new Feature("MyAggrFeature", multiKeyHistogram);
        AggregatedFeatureConf aggrFuncConf = AggrFeatureTestUtils.createAggrFeatureConf(2);
        IAggrFeatureFunction func = new AggrFeatureMultiKeyHistogramFunc(null);

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);

        Assert.assertEquals(value.getClass(), MultiKeyHistogram.class);
        MultiKeyHistogram aggrFeatureValue = (MultiKeyHistogram) aggrFeature.getValue();
        Double expectedFeatureValue1 = val1 + 1;
        Double expectedFeatureValue2 = val2;

        Assert.assertEquals(expectedFeatureValue1, aggrFeatureValue.getHistogram().get(multiKeyFeature1));
        Assert.assertEquals(expectedFeatureValue2, aggrFeatureValue.getHistogram().get(multiKeyFeature2));
    }

    @Test
    public void testUpdateAggregatedFeatureWithGroupByValues() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();
        Feature aggrFeature = new Feature("MyAggrFeature", multiKeyHistogram);

        List<String> values = new ArrayList<>();
        values.add("listFeatureValue1");
        values.add("listFeatureValue2");

        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<String, Object>("stringFeatureName", new FeatureStringValue("stringFeatureValue")),
                new ImmutablePair<String, Object>("listFeatureName", new FeatureListValue(values))
        );

        List<String> featureNames = new ArrayList<>();
        featureNames.add("listFeatureName");
        featureNames.add("stringFeatureName");
        Map<String, List<String>> featureNamesMap = new HashMap<>();
        featureNamesMap.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, featureNames);
        AggregatedFeatureConf aggrFuncConf = new AggregatedFeatureConf("MyAggrFeature", featureNamesMap, Mockito.mock(IAggrFeatureFunction.class));

        Map<String, List<String>> groupByValues = new HashMap<>();
        List<String> allowedValues = new ArrayList<>();
        allowedValues.add("listFeatureValue1");
        groupByValues.put("listFeatureName", allowedValues);

        IAggrFeatureFunction func = new AggrFeatureMultiKeyHistogramFunc(groupByValues);

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);

        Assert.assertEquals(value.getClass(), MultiKeyHistogram.class);
        MultiKeyHistogram aggrFeatureValue = (MultiKeyHistogram) aggrFeature.getValue();

        MultiKeyFeature expected1 = new MultiKeyFeature();
        expected1.add("listFeatureName", "listFeatureValue1");
        expected1.add("stringFeatureName", "stringFeatureValue");

        MultiKeyFeature expected2 = new MultiKeyFeature();
        expected2.add("listFeatureName", AggrFeatureFunctionUtils.OTHER_FIELD_NAME);
        expected2.add("stringFeatureName", "stringFeatureValue");


        Map<MultiKeyFeature, Double> histogram = aggrFeatureValue.getHistogram();
        Assert.assertEquals(2, histogram.size());
        Assert.assertNotNull(aggrFeatureValue.getHistogram().get(expected1));
        Assert.assertNotNull(aggrFeatureValue.getHistogram().get(expected2));
    }

    @Test
    public void testUpdateAggregatedFeatureWithAllowedValuesThatNotExist() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();
        Feature aggrFeature = new Feature("MyAggrFeature", multiKeyHistogram);

        List<String> values = new ArrayList<>();
        values.add("listFeatureValue1");
        values.add("listFeatureValue2");

        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<String, Object>("stringFeatureName", new FeatureStringValue("stringFeatureValue")),
                new ImmutablePair<String, Object>("listFeatureName", new FeatureListValue(values))
        );

        List<String> featureNames = new ArrayList<>();
        featureNames.add("listFeatureName");
        featureNames.add("stringFeatureName");
        Map<String, List<String>> featureNamesMap = new HashMap<>();
        featureNamesMap.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, featureNames);
        AggregatedFeatureConf aggrFuncConf = new AggregatedFeatureConf("MyAggrFeature", featureNamesMap, Mockito.mock(IAggrFeatureFunction.class));

        Map<String, List<String>> groupByValues = new HashMap<>();
        List<String> allowedValues = new ArrayList<>();
        allowedValues.add("listFeatureValue3");
        groupByValues.put("listFeatureName", allowedValues);

        IAggrFeatureFunction func = new AggrFeatureMultiKeyHistogramFunc(groupByValues);

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);

        Assert.assertEquals(value.getClass(), MultiKeyHistogram.class);
        MultiKeyHistogram aggrFeatureValue = (MultiKeyHistogram) aggrFeature.getValue();

        MultiKeyFeature expected = new MultiKeyFeature();
        expected.add("listFeatureName",  AggrFeatureFunctionUtils.OTHER_FIELD_NAME);
        expected.add("stringFeatureName", "stringFeatureValue");


        Map<MultiKeyFeature, Double> histogram = aggrFeatureValue.getHistogram();
        Assert.assertEquals(1, histogram.size());
        Assert.assertNotNull(aggrFeatureValue.getHistogram().get(expected));
    }


    @Test
    public void testCalculateAggrFeature() {
        String confName = "testCalculateAggrFeature";

        List<Pair<Double, Double>> pairs = new ArrayList<>();
        double keyPair1 = 9.0;
        double valuePair1 = 5.0;
        double ketPair2 = 7.0;
        double valuePair2 = 20.0;
        pairs.add(new Pair<>(keyPair1, valuePair1));
        pairs.add(new Pair<>(ketPair2, valuePair2));

        Map<String, String> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("feature1", "open");
        featureNameToValue1.put("feature2", "SUCCESS");
        MultiKeyFeature multiKeyFeature1 = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue1);

        Map<String, String> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put("feature1", "move");
        featureNameToValue2.put("feature2", "SUCCESS");
        MultiKeyFeature multiKeyFeature2 = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue2);

        List<Map<String, Feature>> listOfMaps = new ArrayList<>();
        for (Pair<Double, Double> pair : pairs) {
            MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();
            multiKeyHistogram.set(multiKeyFeature1, pair.getKey());
            multiKeyHistogram.set(multiKeyFeature2, pair.getValue());
            Map<String, Feature> map = AggrFeatureTestUtils.createFeatureMap(
                    new ImmutablePair<String, Object>("feature1", multiKeyHistogram)
            );
            listOfMaps.add(map);
        }

        IAggrFeatureEventFunction function = new AggrFeatureMultiKeyHistogramFunc(null);
        Feature actual = function.calculateAggrFeature(AggrFeatureTestUtils.createAggregatedFeatureEventConf(confName, 1), listOfMaps);

        Double resultFeature1 = ((MultiKeyHistogram) actual.getValue()).getHistogram().get(multiKeyFeature1);
        Double resultFeature12 = ((MultiKeyHistogram) actual.getValue()).getHistogram().get(multiKeyFeature2);
        Assert.assertEquals(resultFeature1, (Double) (keyPair1 + ketPair2));
        Assert.assertEquals(resultFeature12, (Double) (valuePair1 + valuePair2));
    }
}
