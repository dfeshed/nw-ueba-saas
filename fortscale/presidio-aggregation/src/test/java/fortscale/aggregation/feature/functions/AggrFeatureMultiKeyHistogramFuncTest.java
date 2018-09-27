package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.common.feature.*;
import fortscale.common.util.GenericHistogram;
import fortscale.utils.data.Pair;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by amira on 18/06/2015.
 */
public class AggrFeatureMultiKeyHistogramFuncTest {

    @Test
    public void testUpdateAggregatedFeature() {
        MultiKeyHistogram multiKeyHistogram = new MultiKeyHistogram();

        Map<String, String> featureNameToValue1 = new HashMap<>();
        featureNameToValue1.put("feature1", "open");
        featureNameToValue1.put("feature2", "SUCCESS");
        double val1 = 9.0;
        MultiKeyFeature multiKeyFeature1 = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue1);
        multiKeyHistogram.add(multiKeyFeature1, val1);

        Map<String, String> featureNameToValue2 = new HashMap<>();
        featureNameToValue2.put("feature1", "move");
        featureNameToValue2.put("feature2", "SUCCESS");
        double val2 = 5.0;
        MultiKeyFeature multiKeyFeature2 = AggrFeatureTestUtils.createMultiKeyFeature(featureNameToValue2);
        multiKeyHistogram.add(multiKeyFeature2, val2);

        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<String, Object>("feature1", new FeatureStringValue("open")),
                new ImmutablePair<String, Object>("feature2", new FeatureStringValue("SUCCESS"))
        );

        Feature aggrFeature = new Feature("MyAggrFeature", multiKeyHistogram);
        AggregatedFeatureConf aggrFuncConf = AggrFeatureTestUtils.createAggrFeatureConf(2);
        IAggrFeatureFunction func = new AggrFeatureMultiKeyHistogramFunc();

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);

        Assert.assertEquals(value.getClass(), MultiKeyHistogram.class);
        MultiKeyHistogram aggrFeatureValue = (MultiKeyHistogram) aggrFeature.getValue();
        Double expectedTotal = val1 + val2 + 1;
        Double expectedFeatureValue1 = val1 + 1;
        Double expectedFeatureValue2 = val2;

        Assert.assertEquals(expectedTotal, (Double) aggrFeatureValue.getTotal());
        Assert.assertEquals(expectedFeatureValue1, aggrFeatureValue.getHistogram().get(multiKeyFeature1));
        Assert.assertEquals(expectedFeatureValue2, aggrFeatureValue.getHistogram().get(multiKeyFeature2));
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
            multiKeyHistogram.add(multiKeyFeature1, pair.getKey());
            multiKeyHistogram.add(multiKeyFeature2, pair.getValue());
            Map<String, Feature> map = AggrFeatureTestUtils.createFeatureMap(
                    new ImmutablePair<String, Object>("feature1", multiKeyHistogram)
            );
            listOfMaps.add(map);
        }

        IAggrFeatureEventFunction function = new AggrFeatureMultiKeyHistogramFunc();
        Feature actual = function.calculateAggrFeature(AggrFeatureTestUtils.createAggregatedFeatureEventConf(confName, 1), listOfMaps);

        Double resultFeature1 = ((MultiKeyHistogram) actual.getValue()).getHistogram().get(multiKeyFeature1);
        Double resultFeature12 = ((MultiKeyHistogram) actual.getValue()).getHistogram().get(multiKeyFeature2);
        Assert.assertEquals(resultFeature1, (Double) (keyPair1 + ketPair2));
        Assert.assertEquals(resultFeature12, (Double) (valuePair1 + valuePair2));
        Assert.assertEquals((Double) ((MultiKeyHistogram) actual.getValue()).getTotal(), (Double) (keyPair1 + ketPair2 + valuePair1 + valuePair2));
    }
}
