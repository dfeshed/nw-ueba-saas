package fortscale.aggregation.feature.functions;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.util.ContinuousValueAvgStdN;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
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
public class AggrFeatureAvgStdNFuncTest {
    private static final double DELTA = 0.00001;

    private AggregatedFeatureConf createAggrFeatureConf(int num) {
        List<String> featureNames = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            featureNames.add(String.format("feature%d", i));
        }
        Map<String, List<String>> featureNamesMap = new HashMap<>();
        featureNamesMap.put(AggrFeatureAvgStdNFunc.COUNT_BY_FIELD_NAME, featureNames);
        return new AggregatedFeatureConf("MyAggrFeature", featureNamesMap, new JSONObject());
    }

    private AggregatedFeatureEventConf createAggregatedFeatureEventConf(String name, int num) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            list.add(String.format("feature%d", i));
        }
        Map<String, List<String>> map = new HashMap<>();
        map.put(AggrFeatureAvgStdNFunc.COUNT_BY_FIELD_NAME, list);
        return new AggregatedFeatureEventConf(name, "bucketConfName", 3, 1, 300, map, new JSONObject());
    }

    @Test
    public void testUpdateAggrFeature() {
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5); Double a1 = Math.pow(( 0.5 - 5.0), 2);
        avgStdN.add(2.0); Double a2 = Math.pow(( 2.0 - 5.0), 2);
        avgStdN.add(3.0); Double a3 = Math.pow(( 3.0 - 5.0), 2);
        avgStdN.add(1.0); Double a4 = Math.pow(( 1.0 - 5.0), 2);
        avgStdN.add(3.5); Double a5 = Math.pow((3.5 - 5.0), 2);
        avgStdN.add(0.5); Double a6 = Math.pow(( 0.5- 5.0), 2);
        avgStdN.add(1.0); Double a7 = Math.pow(( 1.0- 5.0), 2);
        avgStdN.add(2.0); Double a8 = Math.pow(( 2.0- 5.0), 2);
        avgStdN.add(3.0); Double a9 = Math.pow(( 3.0- 5.0), 2);

        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1", new Feature("feature1", 3.5)); Double a10 = Math.pow(( 3.5- 5.0), 2);
        featureMap.put("feature2", new Feature("feature2", 10.0)); Double a11 = Math.pow(( 10.0- 5.0), 2);
        featureMap.put("feature3", new Feature("feature3", 30.0)); Double a12 = Math.pow(( 30.0- 5.0), 2);

        //featureMap.put("feature3", new Feature("feature3", "wrong value type"));
        featureMap.put("not relevant", new Feature("not relevant", 30.0));

        Feature aggrFeature = new Feature("MyAggrFeature", avgStdN);
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(3);
        AggrFeatureFunction func = new AggrFeatureAvgStdNFunc();

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);

        Assert.assertEquals(value.getClass(), ContinuousValueAvgStdN.class);
        Assert.assertEquals(value, aggrFeature.getValue());

        ContinuousValueAvgStdN avgStdNvalues = (ContinuousValueAvgStdN)value;
        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9+a10+a11+a12;
        Double std = Math.sqrt((sum)/12);

        Assert.assertEquals((Long) 12L, avgStdNvalues.getN());
        Assert.assertEquals((Double)5.0, avgStdNvalues.getAvg());
        Assert.assertEquals( std,  avgStdNvalues.getStd());
    }

    @Test
    public void testUpdateWithWrongFeatureValueType() {
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5); Double a1 = Math.pow(( 0.5 - 5.0), 2);
        avgStdN.add(2.0); Double a2 = Math.pow(( 2.0 - 5.0), 2);
        avgStdN.add(3.0); Double a3 = Math.pow(( 3.0 - 5.0), 2);
        avgStdN.add(1.0); Double a4 = Math.pow(( 1.0 - 5.0), 2);
        avgStdN.add(3.5); Double a5 = Math.pow((3.5 - 5.0), 2);
        avgStdN.add(0.5); Double a6 = Math.pow(( 0.5- 5.0), 2);
        avgStdN.add(1.0); Double a7 = Math.pow(( 1.0- 5.0), 2);
        avgStdN.add(2.0); Double a8 = Math.pow(( 2.0- 5.0), 2);
        avgStdN.add(3.0); Double a9 = Math.pow(( 3.0- 5.0), 2);
        avgStdN.add(3.5); Double a10 = Math.pow(( 3.5- 5.0), 2);

        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature2", new Feature("feature2", 10.0)); Double a11 = Math.pow(( 10.0- 5.0), 2);
        featureMap.put("feature3", new Feature("feature3", 30.0)); Double a12 = Math.pow(( 30.0- 5.0), 2);

        featureMap.put("feature1", new Feature("feature1", "wrong value type"));

        Feature aggrFeature = new Feature("MyAggrFeature", avgStdN);
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(3);
        AggrFeatureFunction func = new AggrFeatureAvgStdNFunc();

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);

        Assert.assertEquals(value.getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNvalues = (ContinuousValueAvgStdN)value;
        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9+a10+a11+a12;
        Double std = Math.sqrt((sum)/12);

        Assert.assertEquals((Long) 12L,  avgStdNvalues.getN());
        Assert.assertEquals((Double)5.0, avgStdNvalues.getAvg());
        Assert.assertEquals( std,  avgStdNvalues.getStd());
    }

    @Test
    public void testUpdateAggrFeatureWithNullAggrFeature() {
        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1", new Feature("feature1", 0.5));

        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(3);
        AggrFeatureFunction func = new AggrFeatureAvgStdNFunc();

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap, null);

        Assert.assertNull(value);
    }

    @Test
    public void testUpdateAggrFeatureWithNullAggrFeatureConf() {
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5); Double a1 = Math.pow(( 0.5 - 5.0), 2);
        avgStdN.add(2.0); Double a2 = Math.pow(( 2.0 - 5.0), 2);
        avgStdN.add(3.0); Double a3 = Math.pow(( 3.0 - 5.0), 2);
        avgStdN.add(1.0); Double a4 = Math.pow(( 1.0 - 5.0), 2);
        avgStdN.add(3.5); Double a5 = Math.pow((3.5 - 5.0), 2);
        avgStdN.add(0.5); Double a6 = Math.pow(( 0.5- 5.0), 2);
        avgStdN.add(1.0); Double a7 = Math.pow(( 1.0- 5.0), 2);
        avgStdN.add(2.0); Double a8 = Math.pow(( 2.0- 5.0), 2);
        avgStdN.add(3.0); Double a9 = Math.pow(( 3.0- 5.0), 2);
        avgStdN.add(3.5); Double a10 = Math.pow(( 3.5- 5.0), 2);
        avgStdN.add(10.0); Double a11 = Math.pow(( 10.0- 5.0), 2);
        avgStdN.add(30.0); Double a12 = Math.pow(( 30.0- 5.0), 2);

        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1", new Feature("feature1", 0.5));
        featureMap.put("feature2", new Feature("feature2", 2.0));
        featureMap.put("feature3", new Feature("feature3", 3.0));

        Feature aggrFeature = new Feature("MyAggrFeature", avgStdN);

        AggrFeatureFunction func = new AggrFeatureAvgStdNFunc();

        Object value = func.updateAggrFeature(null, featureMap, aggrFeature);

        Assert.assertNull(value);

        ContinuousValueAvgStdN avgStdNvalues = (ContinuousValueAvgStdN)aggrFeature.getValue();
        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9+a10+a11+a12;
        Double std = Math.sqrt((sum)/12);

        Assert.assertEquals((Long) 12L, avgStdNvalues.getN());
        Assert.assertEquals((Double)5.0, avgStdNvalues.getAvg());
        Assert.assertEquals( std,  avgStdNvalues.getStd());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateAggrFeatureWithWrongAggrFeatureValueType() {
        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1", new Feature("feature1", 0.5));

        Feature aggrFeature = new Feature("MyAggrFeature", "wrong value type");
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(3);
        AggrFeatureFunction func = new AggrFeatureAvgStdNFunc();

        func.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);
    }

    @Test
    public void testUpdateAggrFeatureWithNullAggrFeatureValue() {
        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1", new Feature("feature1", 0.5)); Double a1 = Math.pow(( 0.5 - 5.0), 2);
        featureMap.put("feature2", new Feature("feature2", 2.0)); Double a2 = Math.pow(( 2.0 - 5.0), 2);
        featureMap.put("feature3", new Feature("feature3", 3.0)); Double a3 = Math.pow(( 3.0 - 5.0), 2);
        featureMap.put("feature4", new Feature("feature4", 1.0)); Double a4 = Math.pow(( 1.0 - 5.0), 2);
        featureMap.put("feature5", new Feature("feature5", 3.5)); Double a5 = Math.pow((3.5 - 5.0), 2);
        featureMap.put("feature6", new Feature("feature6", 0.5)); Double a6 = Math.pow(( 0.5- 5.0), 2);
        featureMap.put("feature7", new Feature("feature7", 1.0)); Double a7 = Math.pow(( 1.0- 5.0), 2);
        featureMap.put("feature8", new Feature("feature8", 2.0)); Double a8 = Math.pow(( 2.0- 5.0), 2);
        featureMap.put("feature9", new Feature("feature9", 3.0)); Double a9 = Math.pow(( 3.0- 5.0), 2);
        featureMap.put("feature10", new Feature("feature10", 3.5)); Double a10 = Math.pow(( 3.5- 5.0), 2);
        featureMap.put("feature11", new Feature("feature11", 10.0)); Double a11 = Math.pow(( 10.0- 5.0), 2);
        featureMap.put("feature12", new Feature("feature12", 30.0)); Double a12 = Math.pow(( 30.0- 5.0), 2);

        Feature aggrFeature = new Feature("MyAggrFeature", null);
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(12);
        AggrFeatureFunction func = new AggrFeatureAvgStdNFunc();

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);

        ContinuousValueAvgStdN avgStdNvalues = (ContinuousValueAvgStdN)value;
        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9+a10+a11+a12;
        Double std = Math.sqrt((sum)/12);

        Assert.assertEquals((Long) 12L, (
                Long) avgStdNvalues.getN());
        Assert.assertEquals((Double)5.0, (Double)avgStdNvalues.getAvg());
        Assert.assertEquals((Double) std, (Double) avgStdNvalues.getStd());
    }

    @Test
    public void testUpdateWithNullFeatures() {
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5); Double a1 = Math.pow(( 0.5 - 5.0), 2);
        avgStdN.add(2.0); Double a2 = Math.pow(( 2.0 - 5.0), 2);
        avgStdN.add(3.0); Double a3 = Math.pow(( 3.0 - 5.0), 2);
        avgStdN.add(1.0); Double a4 = Math.pow(( 1.0 - 5.0), 2);
        avgStdN.add(3.5); Double a5 = Math.pow((3.5 - 5.0), 2);
        avgStdN.add(0.5); Double a6 = Math.pow(( 0.5- 5.0), 2);
        avgStdN.add(1.0); Double a7 = Math.pow(( 1.0- 5.0), 2);
        avgStdN.add(2.0); Double a8 = Math.pow(( 2.0- 5.0), 2);
        avgStdN.add(3.0); Double a9 = Math.pow(( 3.0- 5.0), 2);
        avgStdN.add(3.5); Double a10 = Math.pow(( 3.5- 5.0), 2);
        avgStdN.add(10.0); Double a11 = Math.pow(( 10.0- 5.0), 2);
        avgStdN.add(30.0); Double a12 = Math.pow(( 30.0- 5.0), 2);

        Feature aggrFeature = new Feature("MyAggrFeature", avgStdN);
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf(3);
        AggrFeatureFunction func = new AggrFeatureAvgStdNFunc();

        Object value = func.updateAggrFeature(aggrFuncConf, null, aggrFeature);

        Assert.assertEquals(value.getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNvalues = (ContinuousValueAvgStdN)value;
        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9+a10+a11+a12;
        Double std = Math.sqrt((sum)/12);

        Assert.assertEquals((Long) 12L, (Long) avgStdNvalues.getN());
        Assert.assertEquals((Double)5.0, (Double)avgStdNvalues.getAvg());
        Assert.assertEquals((Double) std, (Double) avgStdNvalues.getStd());
    }

    @Test
    public void testCalculateAggrFeature() {
        String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";

        ContinuousValueAvgStdN continuous1 = new ContinuousValueAvgStdN();
        continuous1.add(1.0);
        continuous1.add(2.0);
        continuous1.add(3.0);
        Map<String, Feature> bucket1FeatureMap = new HashMap<>();
        bucket1FeatureMap.put("feature1", new Feature("feature1", continuous1));

        ContinuousValueAvgStdN continuous2 = new ContinuousValueAvgStdN();
        continuous2.add(1.0);
        continuous2.add(5.0);
        continuous2.add(10.0);
        Map<String, Feature> bucket2FeatureMap = new HashMap<>();
        bucket2FeatureMap.put("feature1", new Feature("feature1", continuous2));

        ContinuousValueAvgStdN continuous3 = new ContinuousValueAvgStdN();
        continuous3.add(11.0);
        continuous3.add(13.0);
        continuous3.add(17.0);
        Map<String, Feature> bucket3FeatureMap = new HashMap<>();
        bucket3FeatureMap.put("feature1", new Feature("feature1", continuous3));

        List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
        listOfFeatureMaps.add(bucket1FeatureMap);
        listOfFeatureMaps.add(bucket2FeatureMap);
        listOfFeatureMaps.add(bucket3FeatureMap);

        AggrFeatureEventFunction function = new AggrFeatureAvgStdNFunc();
        Feature actual = function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);

        Assert.assertNotNull(actual);
        Assert.assertEquals(aggregatedFeatureEventName, actual.getName());
        Assert.assertEquals(ContinuousValueAvgStdN.class, actual.getValue().getClass());

        ContinuousValueAvgStdN actualValue = (ContinuousValueAvgStdN)actual.getValue();
        Assert.assertEquals(new Long(9), actualValue.getN());
        Assert.assertEquals(7, actualValue.getAvg(), DELTA);
        Assert.assertEquals(5.55778, actualValue.getStd(), DELTA);
    }

    @Test
    public void testCalculateAggrFeatureWhenMappedFeaturesIncludeSomeThatAreNotListed() {
        String aggregatedFeatureEventName = "aggregatedFeatureEventTestName";

        ContinuousValueAvgStdN continuous1 = new ContinuousValueAvgStdN();
        continuous1.add(1.0);
        continuous1.add(2.0);
        continuous1.add(3.0);

        ContinuousValueAvgStdN notListedContinuous = new ContinuousValueAvgStdN();
        notListedContinuous.add(100.0);
        notListedContinuous.add(200.0);
        notListedContinuous.add(300.0);

        Map<String, Feature> bucket1FeatureMap = new HashMap<>();
        bucket1FeatureMap.put("feature1", new Feature("feature1", continuous1));
        bucket1FeatureMap.put("feature2", new Feature("feature2", notListedContinuous));

        ContinuousValueAvgStdN continuous2 = new ContinuousValueAvgStdN();
        continuous2.add(1.0);
        continuous2.add(5.0);
        continuous2.add(10.0);

        Map<String, Feature> bucket2FeatureMap = new HashMap<>();
        bucket2FeatureMap.put("feature1", new Feature("feature1", continuous2));
        bucket2FeatureMap.put("feature2", new Feature("feature2", 42));

        List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
        listOfFeatureMaps.add(bucket1FeatureMap);
        listOfFeatureMaps.add(bucket2FeatureMap);

        AggrFeatureEventFunction function = new AggrFeatureAvgStdNFunc();
        Feature actual = function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);

        Assert.assertNotNull(actual);
        Assert.assertEquals(aggregatedFeatureEventName, actual.getName());
        Assert.assertEquals(ContinuousValueAvgStdN.class, actual.getValue().getClass());

        ContinuousValueAvgStdN actualValue = (ContinuousValueAvgStdN)actual.getValue();
        Assert.assertEquals(new Long(6), actualValue.getN());
        Assert.assertEquals(3.66667, actualValue.getAvg(), DELTA);
        Assert.assertEquals(3.14466, actualValue.getStd(), DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateAggrFeatureWithANullAggregatedFeatureValue() {
        String aggregatedFeatureEventName = "testWithANullAggregatedFeatureValue";

        ContinuousValueAvgStdN continuous = new ContinuousValueAvgStdN();
        continuous.add(1.1);
        continuous.add(4.4);
        continuous.add(9.9);
        Map<String, Feature> bucket1FeatureMap = new HashMap<>();
        bucket1FeatureMap.put("feature1", new Feature("feature1", continuous));

        Map<String, Feature> bucket2FeatureMap = new HashMap<>();
        bucket2FeatureMap.put("feature1", new Feature("feature1", null));

        List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
        listOfFeatureMaps.add(bucket1FeatureMap);
        listOfFeatureMaps.add(bucket2FeatureMap);

        AggrFeatureEventFunction function = new AggrFeatureAvgStdNFunc();
        function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateAggrFeatureWithAWrongAggregatedFeatureValueType() {
        String aggregatedFeatureEventName = "testWithAWrongAggregatedFeatureValueType";

        ContinuousValueAvgStdN continuous = new ContinuousValueAvgStdN();
        continuous.add(11.0);
        continuous.add(13.0);
        continuous.add(17.0);
        Map<String, Feature> bucket1FeatureMap = new HashMap<>();
        bucket1FeatureMap.put("feature1", new Feature("feature1", continuous));

        Map<String, Feature> bucket2FeatureMap = new HashMap<>();
        bucket2FeatureMap.put("feature1", new Feature("feature1", 42));

        List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
        listOfFeatureMaps.add(bucket1FeatureMap);
        listOfFeatureMaps.add(bucket2FeatureMap);

        AggrFeatureEventFunction function = new AggrFeatureAvgStdNFunc();
        function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 1), listOfFeatureMaps);
    }

    @Test
    public void testCalculateAggrFeatureWithNullAggregatedFeatureEventConf() {
        ContinuousValueAvgStdN continuous = new ContinuousValueAvgStdN();
        continuous.add(1.0);
        continuous.add(2.0);
        continuous.add(3.0);
        Map<String, Feature> bucketFeatureMap = new HashMap<>();
        bucketFeatureMap.put("feature1", new Feature("feature1", continuous));

        List<Map<String, Feature>> listOfFeatureMaps = new ArrayList<>();
        listOfFeatureMaps.add(bucketFeatureMap);

        AggrFeatureEventFunction function = new AggrFeatureAvgStdNFunc();
        Assert.assertNull(function.calculateAggrFeature(null, listOfFeatureMaps));
    }

    @Test
    public void testCalculateAggrFeatureWithNullAggregatedFeaturesMapList() {
        String aggregatedFeatureEventName = "testWithNullAggregatedFeaturesMapList";

        AggrFeatureEventFunction function = new AggrFeatureAvgStdNFunc();
        Assert.assertNull(function.calculateAggrFeature(createAggregatedFeatureEventConf(aggregatedFeatureEventName, 3), null));
    }
}
