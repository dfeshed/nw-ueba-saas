package fortscale.streaming.aggregation.feature.functions;


import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.util.GenericHistogram;
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
public class AggrFeatureHistogramFuncTest {
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

    private AggregatedFeatureConf createAggrFeatureConf12() {
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

        AggregatedFeatureConf aggrFuncConf = new AggregatedFeatureConf(
                "MyAggrFeature",
                featureNames,
                new JSONObject());


        return aggrFuncConf;
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
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf3();
        AggrFeatureFunction func = new AggrFeatureHistogramFunc(new AggrFilter(""));

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
    public void testFilterUpdateAggregatedFeature() {
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(2, 0.5);
        histogram.add(2, 2.0);
        histogram.add(2, 3.0);
        histogram.add(2, 3.5);
        histogram.add(2L, 0.5);
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
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf3();
        AggrFeatureFunction func = new AggrFeatureHistogramFunc(new AggrFilter("$.."));

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
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf3();

        AggrFeatureFunction func = new AggrFeatureHistogramFunc(new AggrFilter(""));

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
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf12();
        AggrFeatureFunction func = new AggrFeatureHistogramFunc(new AggrFilter(""));

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

        AggrFeatureFunction func = new AggrFeatureHistogramFunc(new AggrFilter(""));

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

    @Test
    public  void testUpdateWithWrongAggrFeatureValueType() {
        Map<String, Feature> featureMap1 = new HashMap<>();
        featureMap1.put("feature1", new Feature("feature1", 2));
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf12();
        String str = "I'm a string, not histogram";
        Feature aggrFeature = new Feature("MyAggrFeature",str);
        AggrFeatureFunction func = new AggrFeatureHistogramFunc(new AggrFilter(""));

        Object value = func.updateAggrFeature(aggrFuncConf, featureMap1, aggrFeature);

        Assert.assertNull(value);
        // Validating that the histogram value was not changed
        Assert.assertEquals(str, (String)aggrFeature.getValue());
    }

    @Test
    public void testUpdateWithNullAggrFeature() {
        Map<String, Feature> featureMap1 = new HashMap<>();
        featureMap1.put("feature1", new Feature("feature1", 2));
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf12();
        AggrFeatureFunction func = new AggrFeatureHistogramFunc(new AggrFilter(""));

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
        AggregatedFeatureConf aggrFuncConf = createAggrFeatureConf12();

        AggrFeatureFunction func = new AggrFeatureHistogramFunc(new AggrFilter(""));

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
}
