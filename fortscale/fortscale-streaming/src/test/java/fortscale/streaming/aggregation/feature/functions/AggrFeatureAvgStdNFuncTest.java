package fortscale.streaming.aggregation.feature.functions;

import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.util.ContinuousValueAvgStdN;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;
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

     private  AggregatedFeatureConf createAggrFeatureConf3() {
        List<String> featureNames = new ArrayList<>();
        featureNames.add("feature1");
        featureNames.add("feature2");
        featureNames.add("feature3");

        AggregatedFeatureConf aggrFuncConf = new AggregatedFeatureConf(
                "MyAggrFeature",
                featureNames,
                new JSONObject());


        return aggrFuncConf;
    }

    private  AggregatedFeatureConf createAggrFeatureConf12() {
        List<String> featureNames = new ArrayList<>();
        featureNames.add("feature1");
        featureNames.add("feature2");
        featureNames.add("feature3");
        featureNames.add("feature4");
        featureNames.add("feature5");
        featureNames.add("feature6");
        featureNames.add("feature7");
        featureNames.add("feature8");
        featureNames.add("feature9");
        featureNames.add("feature10");
        featureNames.add("feature11");
        featureNames.add("feature12");


        return new AggregatedFeatureConf(
                "MyAggrFeature",
                featureNames,
                new JSONObject());
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
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf3();
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
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf3();
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

        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf3();
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

    @Test
    public void testUpdateAggrFeatureWithWrongAggrFeatureValueType() {

        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1", new Feature("feature1", 0.5));

        Feature aggrFeature = new Feature("MyAggrFeature", "wrong value type");
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf3();
        AggrFeatureFunction func = new AggrFeatureAvgStdNFunc();

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap, aggrFeature);

        Assert.assertNull(value);
        Assert.assertEquals("wrong value type", aggrFeature.getValue());
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
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf12();
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
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf3();
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

}
