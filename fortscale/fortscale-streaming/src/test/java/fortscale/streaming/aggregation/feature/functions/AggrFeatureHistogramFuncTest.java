package fortscale.streaming.aggregation.feature.functions;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.util.GenericHistogram;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;
import fortscale.streaming.service.aggregation.feature.event.AggregatedFeatureEventConf;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amira on 18/06/2015.
 */
public class AggrFeatureHistogramFuncTest {
    private static final double DELTA = 0.00001;

    private AggregatedFeatureConf createAggrFeatureConf(int num) {
        List<String> featureNames = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            featureNames.add(String.format("feature%d", i));
        }
        Map<String, List<String>> featureNamesMap = new HashMap<>();
        featureNamesMap.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, featureNames);
        return new AggregatedFeatureConf("MyAggrFeature", featureNamesMap, new JSONObject());
    }

    private AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, int num) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            list.add(String.format("feature%d", i));
        }
        Map<String, List<String>> map = new HashMap<>();
        map.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, list);
        return new AggregatedFeatureEventConf(name, "bucketConfName", 3, 1, 300, map, new JSONObject());
    }

    @Test
    public void testUpdateAggregatedFeature() {
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(2, 0.5);
        histogram.add(2, 2.0);
        histogram.add(2, 3.0);
        //histogram.add(2, 1.0); // will be added from features
        histogram.add(2, 3.5);

        histogram.add(2L, 0.5);
        //histogram.add(2L, 1.0); // will be added from features
        histogram.add(2L, 2.0);
        histogram.add(2L, 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);

        histogram.add("2", 30.0);

        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1", new Feature("feature1", 2));
        featureMap.put("feature2", new Feature("feature2", 2L));
        featureMap.put("not relevant", new Feature("not relevant", 2));

        Feature aggrFeature = new Feature("MyAggrFeature", histogram);
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(3);
        AggrFeatureFunction func = new AggrFeatureHistogramFunc();

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);

        Assert.assertEquals(value.getClass(), GenericHistogram.class);
        Assert.assertEquals(value, aggrFeature.getValue());

        GenericHistogram histValue = (GenericHistogram)value;

        Assert.assertEquals((Double)10.0, (Double)histValue.get(2) );
        Assert.assertEquals((Double)20.0, (Double)histValue.get(2L) );
        Assert.assertEquals((Double)30.0, (Double)histValue.get("2") );

        Double avg = 20d;
        Double one = Math.pow((10-avg),2);
        Double two = Math.pow((20-avg),2);
        Double three = Math.pow((30-avg),2);
        Double sum = one+two+three;
        Double std = Math.sqrt((sum)/3);
        Assert.assertEquals((Double) std, (Double) histValue.getPopulationStandardDeviation());

    }

    @Test
    public void testUpdateWithDifferentFeatureValueTypes() {
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(2, 0.5);
        histogram.add(2, 2.0);
        histogram.add(2, 3.0);
        //histogram.add(2, 1.0); // will be added from features
        histogram.add(2, 3.5);

        histogram.add(2L, 0.5);
        //histogram.add(2L, 1.0); // will be added from features
        histogram.add(2L, 2.0);
        histogram.add(2L, 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);

        histogram.add("2", 30.0);

        Map<String, Feature> featureMap1 = new HashMap<>();
        featureMap1.put("feature1", new Feature("feature1", 2));

        Map<String, Feature> featureMap2 = new HashMap<>();
        featureMap2.put("feature1", new Feature("feature1", 2L));
        featureMap2.put("not relevant", new Feature("not relevant", 2));

        Feature aggrFeature = new Feature("MyAggrFeature", histogram);
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(3);

        AggrFeatureFunction func = new AggrFeatureHistogramFunc();

        func.updateAggrFeature(aggrFuncConf, featureMap1, aggrFeature);
        Object value = func.updateAggrFeature(aggrFuncConf, featureMap2, aggrFeature);

        Assert.assertEquals(value.getClass(), GenericHistogram.class);
        Assert.assertEquals(value, aggrFeature.getValue());

        GenericHistogram histValue = (GenericHistogram)value;

        Assert.assertEquals((Double)10.0, (Double)histValue.get(2) );
        Assert.assertEquals((Double)20.0, (Double)histValue.get(2L) );
        Assert.assertEquals((Double)30.0, (Double)histValue.get("2") );

        Double avg = 20d;
        Double one = Math.pow((10-avg),2);
        Double two = Math.pow((20-avg),2);
        Double three = Math.pow((30-avg),2);
        Double sum = one+two+three;
        Double std = Math.sqrt((sum)/3);
        Assert.assertEquals((Double) std, (Double) histValue.getPopulationStandardDeviation());
    }

    @Test
    public void testUpdateWithNullAggrFeatureValue() {
        String s1 = "one", s2 = "two", s3 = "three";
        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1", new Feature("feature1",s1));
        featureMap.put("feature2", new Feature("feature2", s1));
        featureMap.put("feature3", new Feature("feature3", s1));
        featureMap.put("feature4", new Feature("feature4", s1)); // 4
        featureMap.put("feature5", new Feature("feature5", s2));
        featureMap.put("feature6", new Feature("feature6", s2));
        featureMap.put("feature7", new Feature("feature7", s2));
        featureMap.put("feature8", new Feature("feature8", s2));
        featureMap.put("feature9", new Feature("feature9", s2)); // 5
        featureMap.put("feature10", new Feature("feature10", s3));
        featureMap.put("feature11", new Feature("feature11", s3));
        featureMap.put("feature12", new Feature("feature12", s3)); // 3
        featureMap.put("not relevant", new Feature("not relevant", 2));

        Feature aggrFeature = new Feature("MyAggrFeature", null);
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(12);
        AggrFeatureFunction func = new AggrFeatureHistogramFunc();

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);

        GenericHistogram histValue = (GenericHistogram)value;

        Assert.assertEquals((Double)4.0, (Double)histValue.get(s1) );
        Assert.assertEquals((Double)5.0, (Double)histValue.get(s2) );
        Assert.assertEquals((Double)3.0, (Double)histValue.get(s3) );

        Double avg = 4.0;
        Assert.assertEquals((Double)avg, (Double)histValue.getAvg());

        Double one = Math.pow((4.0-avg),2);
        Double two = Math.pow((5.0-avg),2);
        Double three = Math.pow((3.0-avg),2);
        Double sum = one+two+three;
        Double std = Math.sqrt((sum)/3);
        Assert.assertEquals((Double) std, (Double) histValue.getPopulationStandardDeviation());
    }

    @Test
    public void testUpdateWithNullAggrFeatureConf() {
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(2, 0.5);
        histogram.add(2, 2.0);
        histogram.add(2, 3.0);
        histogram.add(2, 1.0);
        histogram.add(2, 3.5);

        histogram.add(2L, 0.5);
        histogram.add(2L, 1.0);
        histogram.add(2L, 2.0);
        histogram.add(2L, 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);

        histogram.add("2", 30.0);

        Map<String, Feature> featureMap1 = new HashMap<>();
        featureMap1.put("feature1", new Feature("feature1", 2));


        Feature aggrFeature = new Feature("MyAggrFeature", histogram);
        AggregatedFeatureConf aggrFuncConf = null;

        AggrFeatureFunction func = new AggrFeatureHistogramFunc();

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap1, aggrFeature);

        Assert.assertNull(value);

        // Validating that the histogram value was not changed
        GenericHistogram aggrValue = (GenericHistogram)aggrFeature.getValue();
        Assert.assertEquals(histogram, aggrValue);
        Assert.assertEquals((Double)10.0, (Double)aggrValue.get(2) );
        Assert.assertEquals((Double)20.0, (Double)aggrValue.get(2L) );
        Assert.assertEquals((Double)30.0, (Double)aggrValue.get("2") );

        Double avg = 20d;
        Double one = Math.pow((10-avg),2);
        Double two = Math.pow((20-avg),2);
        Double three = Math.pow((30-avg),2);
        Double sum = one+two+three;
        Double std = Math.sqrt((sum)/3);
        Assert.assertEquals((Double) std, (Double) aggrValue.getPopulationStandardDeviation());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithWrongAggrFeatureValueType() {
        Map<String, Feature> featureMap1 = new HashMap<>();
        featureMap1.put("feature1", new Feature("feature1", 2));
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(12);
        String str = "I'm a string, not histogram";
        Feature aggrFeature = new Feature("MyAggrFeature", str);
        AggrFeatureFunction func = new AggrFeatureHistogramFunc();
        func.updateAggrFeature(aggrFuncConf, featureMap1, aggrFeature);
    }

    @Test
    public void testUpdateWithNullAggrFeature() {
        Map<String, Feature> featureMap1 = new HashMap<>();
        featureMap1.put("feature1", new Feature("feature1", 2));
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(12);
        AggrFeatureFunction func = new AggrFeatureHistogramFunc();

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap1, null);

        Assert.assertNull(value);
    }

    @Test
    public void testUpdateWithNullFeatures() {
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(2, 0.5);
        histogram.add(2, 2.0);
        histogram.add(2, 3.0);
        histogram.add(2, 1.0);
        histogram.add(2, 3.5);

        histogram.add(2L, 0.5);
        histogram.add(2L, 1.0);
        histogram.add(2L, 2.0);
        histogram.add(2L, 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);

        histogram.add("2", 30.0);

        Feature aggrFeature = new Feature("MyAggrFeature", histogram);
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(12);

        AggrFeatureFunction func = new AggrFeatureHistogramFunc();

        Object value = func.updateAggrFeature(aggrFuncConf, null, aggrFeature);


        // Validating that the histogram value was not changed
        GenericHistogram aggrValue = (GenericHistogram)value;
        Assert.assertEquals(histogram, aggrValue);
        Assert.assertEquals((Double)10.0, aggrValue.get(2) );
        Assert.assertEquals((Double)20.0, aggrValue.get(2L) );
        Assert.assertEquals((Double)30.0, aggrValue.get("2") );

        Double avg = 20d;
        Double one = Math.pow((10-avg),2);
        Double two = Math.pow((20-avg),2);
        Double three = Math.pow((30-avg),2);
        Double sum = one+two+three;
        Double std = Math.sqrt((sum)/3);
        Assert.assertEquals(std, (Double) aggrValue.getPopulationStandardDeviation());
    }

    @Test
    public void testCalculateAggrFeature() {
        String confName = "testCalculateAggrFeature";

        GenericHistogram hist1 = new GenericHistogram();
        hist1.add(7, 10.0);
        hist1.add(7L, 20.0);
        hist1.add("7", 30.0);
        Map<String, Feature> map1 = new HashMap<>();
        map1.put("feature1", new Feature("feature1", hist1));

        GenericHistogram hist2 = new GenericHistogram();
        hist2.add(11, 1.0);
        hist2.add(13, 1.0);
        hist2.add(17, 1.0);
        Map<String, Feature> map2 = new HashMap<>();
        map2.put("feature1", new Feature("feature1", hist2));

        GenericHistogram hist3 = new GenericHistogram();
        hist3.add(7, 40.0);
        hist3.add(11, 9.0);
        hist3.add("7", 70.0);
        Map<String, Feature> map3 = new HashMap<>();
        map3.put("feature1", new Feature("feature1", hist3));

        List<Map<String, Feature>> listOfMaps = new ArrayList<>();
        listOfMaps.add(map1);
        listOfMaps.add(map2);
        listOfMaps.add(map3);

        AggrFeatureEventFunction function = new AggrFeatureHistogramFunc();
        Feature actual = function.calculateAggrFeature(createAggregatedFeatureEventConf(confName, 1), listOfMaps);

        Assert.assertNotNull(actual);
        Assert.assertEquals(confName, actual.getName());
        Assert.assertEquals(GenericHistogram.class, actual.getValue().getClass());

        GenericHistogram actualValue = (GenericHistogram)actual.getValue();
        Assert.assertEquals(6, actualValue.getN());
        Assert.assertEquals(30.33333, actualValue.getAvg(), DELTA);
        Assert.assertEquals(38.68161, actualValue.getStandardDeviation(), DELTA);
        Assert.assertEquals(35.31131, actualValue.getPopulationStandardDeviation(), DELTA);
        Assert.assertEquals(100.0, actualValue.getMaxCount(), 0);
        Assert.assertEquals("7", actualValue.getMaxCountObject());
        Assert.assertEquals(100.0 / 182.0, actualValue.getMaxCountFromTotalCount(), DELTA);

        Assert.assertEquals(50.0, actualValue.get(7), 0);
        Assert.assertEquals(20.0, actualValue.get(7L), 0);
        Assert.assertEquals(100.0, actualValue.get("7"), 0);
        Assert.assertEquals(10.0, actualValue.get(11), 0);
        Assert.assertEquals(1.0, actualValue.get(13), 0);
        Assert.assertEquals(1.0, actualValue.get(17), 0);
    }

    @Test
    public void testCalculateAggrFeatureWhenMappedFeaturesIncludeSomeThatAreNotListed() {
        String confName = "testCalculateAggrFeatureWhenMappedFeaturesIncludeSomeThatAreNotListed";

        GenericHistogram hist1 = new GenericHistogram();
        hist1.add(1, 10.0);
        hist1.add(2L, 20.0);
        hist1.add("3", 30.0);

        GenericHistogram notListedHist = new GenericHistogram();
        notListedHist.add(1, 100.0);
        notListedHist.add("3", 300.0);
        notListedHist.add(5.0, 500.0);

        Map<String, Feature> map1 = new HashMap<>();
        map1.put("feature1", new Feature("feature1", hist1));
        map1.put("feature2", new Feature("feature2", notListedHist));

        GenericHistogram hist2 = new GenericHistogram();
        hist2.add(2L, 1.0);
        hist2.add("test", 2.0);
        hist2.add("check", 3.0);

        Map<String, Feature> map2 = new HashMap<>();
        map2.put("feature1", new Feature("feature1", hist2));
        map2.put("feature2", new Feature("feature2", -1));

        List<Map<String, Feature>> listOfMaps = new ArrayList<>();
        listOfMaps.add(map1);
        listOfMaps.add(map2);

        AggrFeatureEventFunction function = new AggrFeatureHistogramFunc();
        Feature actual = function.calculateAggrFeature(createAggregatedFeatureEventConf(confName, 1), listOfMaps);

        Assert.assertNotNull(actual);
        Assert.assertEquals(confName, actual.getName());
        Assert.assertEquals(GenericHistogram.class, actual.getValue().getClass());

        GenericHistogram actualValue = (GenericHistogram)actual.getValue();
        Assert.assertEquals(5, actualValue.getN());
        Assert.assertEquals(13.2, actualValue.getAvg(), DELTA);
        Assert.assertEquals(12.07063, actualValue.getStandardDeviation(), DELTA);
        Assert.assertEquals(10.7963, actualValue.getPopulationStandardDeviation(), DELTA);
        Assert.assertEquals(30.0, actualValue.getMaxCount(), 0);
        Assert.assertEquals("3", actualValue.getMaxCountObject());
        Assert.assertEquals(30.0 / 66.0, actualValue.getMaxCountFromTotalCount(), DELTA);

        Assert.assertEquals(10.0, actualValue.get(1), 0);
        Assert.assertEquals(21.0, actualValue.get(2L), 0);
        Assert.assertEquals(30.0, actualValue.get("3"), 0);
        Assert.assertEquals(2.0, actualValue.get("test"), 0);
        Assert.assertEquals(3.0, actualValue.get("check"), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateAggrFeatureWithANullAggregatedFeatureValue() {
        String confName = "testCalculateAggrFeatureWithANullAggregatedFeatureValue";

        GenericHistogram hist = new GenericHistogram();
        hist.add("a", 1.0);
        hist.add("b", 1.0);
        hist.add("c", 1.0);
        Map<String, Feature> map1 = new HashMap<>();
        map1.put("feature1", new Feature("feature1", hist));

        Map<String, Feature> map2 = new HashMap<>();
        map2.put("feature1", new Feature("feature1", null));

        List<Map<String, Feature>> listOfMaps = new ArrayList<>();
        listOfMaps.add(map1);
        listOfMaps.add(map2);

        AggrFeatureEventFunction function = new AggrFeatureHistogramFunc();
        function.calculateAggrFeature(createAggregatedFeatureEventConf(confName, 1), listOfMaps);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateAggrFeatureWithAWrongAggregatedFeatureValueType() {
        String confName = "testCalculateAggrFeatureWithAWrongAggregatedFeatureValueType";

        GenericHistogram hist = new GenericHistogram();
        hist.add("x", 1.0);
        hist.add("y", 2.0);
        hist.add("z", 3.0);
        Map<String, Feature> map1 = new HashMap<>();
        map1.put("feature1", new Feature("feature1", hist));

        Map<String, Feature> map2 = new HashMap<>();
        map2.put("feature1", new Feature("feature1", "wrong value"));

        List<Map<String, Feature>> listOfMaps = new ArrayList<>();
        listOfMaps.add(map1);
        listOfMaps.add(map2);

        AggrFeatureEventFunction function = new AggrFeatureHistogramFunc();
        function.calculateAggrFeature(createAggregatedFeatureEventConf(confName, 1), listOfMaps);
    }

    @Test
    public void testCalculateAggrFeatureWithNullAggregatedFeatureEventConf() {
        GenericHistogram hist = new GenericHistogram();
        hist.add(1.0, 1.0);
        hist.add(2.0, 2.0);
        hist.add(3.0, 3.0);
        Map<String, Feature> map = new HashMap<>();
        map.put("feature1", new Feature("feature1", hist));

        List<Map<String, Feature>> listOfMaps = new ArrayList<>();
        listOfMaps.add(map);

        AggrFeatureEventFunction function = new AggrFeatureHistogramFunc();
        Assert.assertNull(function.calculateAggrFeature(null, listOfMaps));
    }

    @Test
    public void testCalculateAggrFeatureWithNullAggregatedFeaturesMapList() {
        String confName = "testCalculateAggrFeatureWithNullAggregatedFeaturesMapList";

        AggrFeatureEventFunction function = new AggrFeatureHistogramFunc();
        Assert.assertNull(function.calculateAggrFeature(createAggregatedFeatureEventConf(confName, 3), null));
    }
}
