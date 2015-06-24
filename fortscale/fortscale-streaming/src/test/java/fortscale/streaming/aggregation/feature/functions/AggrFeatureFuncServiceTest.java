package fortscale.streaming.aggregation.feature.functions;

import com.mongodb.util.JSON;
import fortscale.streaming.aggregation.feature.Feature;
import fortscale.streaming.aggregation.feature.util.ContinuousValueAvgStdN;
import fortscale.streaming.aggregation.feature.util.GenericHistogram;
import fortscale.streaming.service.aggregation.AggregatedFeatureConf;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amira on 21/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/aggr-feature-service-context-test.xml" })
public class AggrFeatureFuncServiceTest {

    @Autowired
    AggrFeatureFuncService funcService;

    private  AggregatedFeatureConf createAggrFeatureConf3(String aggrFeatureName, String funcName) {
        List<String> featureNames = new ArrayList<>();
        featureNames.add("feature1"+aggrFeatureName);
        featureNames.add("feature2"+aggrFeatureName);
        featureNames.add("feature3"+aggrFeatureName);

        JSONObject funcConf = new JSONObject();
        funcConf.put("type", funcName);

        return new AggregatedFeatureConf(
                aggrFeatureName,
                featureNames,
                funcConf);

    }

    @Test
    public  void testUpdateWithTwoAggrFeatures() {
        //////////////////////////////////////////////////////
        // ContinuousValueAvgStdN
        /////////////////////////////////////////////////////
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

        String aggrFeatureName1 = "MyAggrFeature1";

        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1"+aggrFeatureName1, new Feature("feature1"+aggrFeatureName1, 3.5)); Double a10 = Math.pow(( 3.5- 5.0), 2);
        featureMap.put("feature2"+aggrFeatureName1, new Feature("feature2"+aggrFeatureName1, 10.0)); Double a11 = Math.pow(( 10.0- 5.0), 2);
        featureMap.put("feature3"+aggrFeatureName1, new Feature("feature3"+aggrFeatureName1, 30.0)); Double a12 = Math.pow(( 30.0- 5.0), 2);

        featureMap.put("not relevant", new Feature("not relevant", 30.0));

        Feature aggrFeature1 = new Feature(aggrFeatureName1, avgStdN);
        AggregatedFeatureConf aggrFuncConf1 = createAggrFeatureConf3(aggrFeatureName1, AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE);


        //////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////
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

        String aggrFeatureName2 = "MyAggrFeature2";

        featureMap.put("feature1"+aggrFeatureName2, new Feature("feature1"+aggrFeatureName2, 2));
        featureMap.put("feature2"+aggrFeatureName2, new Feature("feature2"+aggrFeatureName2, 2L));
        featureMap.put("not relevant", new Feature("not relevant", 2));


        Feature aggrFeature2 = new Feature(aggrFeatureName2, histogram);
        AggregatedFeatureConf aggrFuncConf2 = createAggrFeatureConf3(aggrFeatureName2, AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE);

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
        aggrFeatureConfs.add(aggrFuncConf1);
        aggrFeatureConfs.add(aggrFuncConf2);

        Map<String, Feature> aggrFeatures = new HashMap<>();
        aggrFeatures.put(aggrFeatureName1, aggrFeature1);
        aggrFeatures.put(aggrFeatureName2, aggrFeature2);

        //AggrFeatureFuncService funcService = new AggrFeatureFuncService();
        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(aggrFeatureConfs, aggrFeatures, featureMap);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = aggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = aggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNvalues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9+a10+a11+a12;
        Double std = Math.sqrt((sum)/12);

        Assert.assertEquals((Long) 12L, avgStdNvalues.getN());
        Assert.assertEquals((Double)5.0, avgStdNvalues.getAvg());
        Assert.assertEquals( std,  avgStdNvalues.getStd());
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);


        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals((Double)10.0, histValue.get(2) );
        Assert.assertEquals((Double)20.0, histValue.get(2L) );
        Assert.assertEquals((Double)30.0, histValue.get("2") );

        Double avg = 20d;
        Double one = Math.pow((10-avg),2);
        Double two = Math.pow((20-avg),2);
        Double three = Math.pow((30-avg),2);
        Double sum2 = one+two+three;
        Double std2 = Math.sqrt((sum2)/3);
        Assert.assertEquals(std2, (Double) histValue.getPopulationStandardDeviation());


    }

    @Test
    public void testUpdateWithMissingAggrFeature() {
        //////////////////////////////////////////////////////
        // ContinuousValueAvgStdN
        /////////////////////////////////////////////////////
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

        String aggrFeatureName1 = "MyAggrFeature1";

        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1"+aggrFeatureName1, new Feature("feature1"+aggrFeatureName1, 3.5)); Double a10 = Math.pow(( 3.5- 5.0), 2);
        featureMap.put("feature2"+aggrFeatureName1, new Feature("feature2"+aggrFeatureName1, 10.0)); Double a11 = Math.pow(( 10.0- 5.0), 2);
        featureMap.put("feature3"+aggrFeatureName1, new Feature("feature3"+aggrFeatureName1, 30.0)); Double a12 = Math.pow(( 30.0- 5.0), 2);

        featureMap.put("not relevant", new Feature("not relevant", 30.0));

        Feature aggrFeature1 = new Feature(aggrFeatureName1, avgStdN);
        AggregatedFeatureConf aggrFuncConf1 = createAggrFeatureConf3(aggrFeatureName1, AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE);


        //////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////


        String aggrFeatureName2 = "MyAggrFeature2";

        featureMap.put("feature1"+aggrFeatureName2, new Feature("feature1"+aggrFeatureName2, 2));
        featureMap.put("feature2"+aggrFeatureName2, new Feature("feature2"+aggrFeatureName2, 2L));
        featureMap.put("not relevant", new Feature("not relevant", 2));


        //Feature aggrFeature2 = new Feature(aggrFeatureName2, histogram);
        AggregatedFeatureConf aggrFuncConf2 = createAggrFeatureConf3(aggrFeatureName2, AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE);

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
        aggrFeatureConfs.add(aggrFuncConf1);
        aggrFeatureConfs.add(aggrFuncConf2);

        Map<String, Feature> aggrFeatures = new HashMap<>();
        aggrFeatures.put(aggrFeatureName1, aggrFeature1);
        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(aggrFeatureConfs, aggrFeatures, featureMap);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = updatedAggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = updatedAggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNvalues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9+a10+a11+a12;
        Double std = Math.sqrt((sum)/12);

        Assert.assertEquals((Long) 12L, avgStdNvalues.getN());
        Assert.assertEquals((Double)5.0, avgStdNvalues.getAvg());
        Assert.assertEquals( std,  avgStdNvalues.getStd());
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);


        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals(2L, histValue.getN());
        Assert.assertEquals((Double)1.0, histValue.get(2) );
        Assert.assertEquals((Double)1.0, histValue.get(2L) );

        Double avg = 1d;
        Double one = Math.pow((1-avg),2);
        Double two = Math.pow((1-avg),2);
        Double sum2 = one+two;
        Double std2 = Math.sqrt((sum2)/2);
        Assert.assertEquals(std2, (Double) histValue.getPopulationStandardDeviation());


    }

    @Test
    public void testUpdateWithEmptyAggrFeatureConfs() {
        //////////////////////////////////////////////////////
        // ContinuousValueAvgStdN
        /////////////////////////////////////////////////////
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5);
        avgStdN.add(2.0);
        avgStdN.add(3.0);
        avgStdN.add(1.0);
        avgStdN.add(3.5);
        avgStdN.add(0.5);
        avgStdN.add(1.0);
        avgStdN.add(2.0);
        avgStdN.add(3.0);

        String aggrFeatureName1 = "MyAggrFeature1";

        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1"+aggrFeatureName1, new Feature("feature1"+aggrFeatureName1, 3.5));
        featureMap.put("feature2"+aggrFeatureName1, new Feature("feature2"+aggrFeatureName1, 10.0));
        featureMap.put("feature3"+aggrFeatureName1, new Feature("feature3"+aggrFeatureName1, 30.0));

        featureMap.put("not relevant", new Feature("not relevant", 30.0));

        Feature aggrFeature1 = new Feature(aggrFeatureName1, avgStdN);


        //////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////
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

        String aggrFeatureName2 = "MyAggrFeature2";

        featureMap.put("feature1"+aggrFeatureName2, new Feature("feature1"+aggrFeatureName2, 2));
        featureMap.put("feature2"+aggrFeatureName2, new Feature("feature2"+aggrFeatureName2, 2L));
        featureMap.put("not relevant", new Feature("not relevant", 2));


        Feature aggrFeature2 = new Feature(aggrFeatureName2, histogram);

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();

        Map<String, Feature> aggrFeatures = new HashMap<>();
        aggrFeatures.put(aggrFeatureName1, aggrFeature1);
        aggrFeatures.put(aggrFeatureName2, aggrFeature2);

        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(aggrFeatureConfs, aggrFeatures, featureMap);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = aggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = aggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNvalues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double avg = (0.5+2.0+3.0+1.0+3.5+0.5+1.0+2.0+3.0)/9;
        Double a1 = Math.pow(( 0.5 - avg), 2);
        Double a2 = Math.pow(( 2.0 - avg), 2);
        Double a3 = Math.pow(( 3.0 - avg), 2);
        Double a4 = Math.pow(( 1.0 - avg), 2);
        Double a5 = Math.pow((3.5 - avg), 2);
        Double a6 = Math.pow(( 0.5- avg), 2);
        Double a7 = Math.pow(( 1.0- avg), 2);
        Double a8 = Math.pow(( 2.0- avg), 2);
        Double a9 = Math.pow(( 3.0- avg), 2);
        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9;
        Double std = Math.sqrt((sum)/9);

        Assert.assertEquals((Long) 9L, avgStdNvalues.getN());
        Assert.assertEquals(avg, avgStdNvalues.getAvg());

        Assert.assertTrue(Math.abs(std-avgStdNvalues.getStd())<0.000001);
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);


        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals((Double)9.0, histValue.get(2) );
        Assert.assertEquals((Double)19.0, histValue.get(2L) );
        Assert.assertEquals((Double)30.0, histValue.get("2") );

        Double avg2 = (9+19+30)/3d;
        Double one = Math.pow((9-avg2),2);
        Double two = Math.pow((19-avg2),2);
        Double three = Math.pow((30-avg2),2);
        Double sum2 = one+two+three;
        Double std2 = Math.sqrt((sum2)/3);
        Assert.assertEquals(std2, (Double) histValue.getPopulationStandardDeviation());

    }

    @Test
    public void testUpdateWithEmptyFeatures() {
        //////////////////////////////////////////////////////
        // ContinuousValueAvgStdN
        /////////////////////////////////////////////////////
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5);
        avgStdN.add(2.0);
        avgStdN.add(3.0);
        avgStdN.add(1.0);
        avgStdN.add(3.5);
        avgStdN.add(0.5);
        avgStdN.add(1.0);
        avgStdN.add(2.0);
        avgStdN.add(3.0);

        String aggrFeatureName1 = "MyAggrFeature1";
        Feature aggrFeature1 = new Feature(aggrFeatureName1, avgStdN);

        //////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////
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

        String aggrFeatureName2 = "MyAggrFeature2";
        Feature aggrFeature2 = new Feature(aggrFeatureName2, histogram);

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
        AggregatedFeatureConf aggrFuncConf1 = createAggrFeatureConf3(aggrFeatureName1, AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE);
        AggregatedFeatureConf aggrFuncConf2 = createAggrFeatureConf3(aggrFeatureName2, AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE);
        aggrFeatureConfs.add(aggrFuncConf1);
        aggrFeatureConfs.add(aggrFuncConf2);

        Map<String, Feature> aggrFeatures = new HashMap<>();
        aggrFeatures.put(aggrFeatureName1, aggrFeature1);
        aggrFeatures.put(aggrFeatureName2, aggrFeature2);

        Map<String, Feature> featureMap = new HashMap<>();

        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(aggrFeatureConfs, aggrFeatures, featureMap);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = aggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = aggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNvalues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double avg = (0.5+2.0+3.0+1.0+3.5+0.5+1.0+2.0+3.0)/9;
        Double a1 = Math.pow(( 0.5 - avg), 2);
        Double a2 = Math.pow(( 2.0 - avg), 2);
        Double a3 = Math.pow(( 3.0 - avg), 2);
        Double a4 = Math.pow(( 1.0 - avg), 2);
        Double a5 = Math.pow((3.5 - avg), 2);
        Double a6 = Math.pow(( 0.5- avg), 2);
        Double a7 = Math.pow(( 1.0- avg), 2);
        Double a8 = Math.pow(( 2.0- avg), 2);
        Double a9 = Math.pow(( 3.0- avg), 2);
        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9;
        Double std = Math.sqrt((sum)/9);

        Assert.assertEquals((Long) 9L, avgStdNvalues.getN());
        Assert.assertEquals(avg, avgStdNvalues.getAvg());

        Assert.assertTrue(Math.abs(std-avgStdNvalues.getStd())<0.000001);
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);


        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals((Double)9.0, histValue.get(2) );
        Assert.assertEquals((Double)19.0, histValue.get(2L) );
        Assert.assertEquals((Double)30.0, histValue.get("2") );

        Double avg2 = (9+19+30)/3d;
        Double one = Math.pow((9-avg2),2);
        Double two = Math.pow((19-avg2),2);
        Double three = Math.pow((30-avg2),2);
        Double sum2 = one+two+three;
        Double std2 = Math.sqrt((sum2)/3);
        Assert.assertEquals(std2, (Double) histValue.getPopulationStandardDeviation());

    }


    @Test
    public void testUpdateWithNullConfs() {
        //////////////////////////////////////////////////////
        // ContinuousValueAvgStdN
        /////////////////////////////////////////////////////
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5);
        avgStdN.add(2.0);
        avgStdN.add(3.0);
        avgStdN.add(1.0);
        avgStdN.add(3.5);
        avgStdN.add(0.5);
        avgStdN.add(1.0);
        avgStdN.add(2.0);
        avgStdN.add(3.0);

        String aggrFeatureName1 = "MyAggrFeature1";

        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1"+aggrFeatureName1, new Feature("feature1"+aggrFeatureName1, 3.5));
        featureMap.put("feature2"+aggrFeatureName1, new Feature("feature2"+aggrFeatureName1, 10.0));
        featureMap.put("feature3"+aggrFeatureName1, new Feature("feature3"+aggrFeatureName1, 30.0));

        featureMap.put("not relevant", new Feature("not relevant", 30.0));

        Feature aggrFeature1 = new Feature(aggrFeatureName1, avgStdN);


        //////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////
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

        String aggrFeatureName2 = "MyAggrFeature2";

        featureMap.put("feature1"+aggrFeatureName2, new Feature("feature1"+aggrFeatureName2, 2));
        featureMap.put("feature2"+aggrFeatureName2, new Feature("feature2"+aggrFeatureName2, 2L));
        featureMap.put("not relevant", new Feature("not relevant", 2));


        Feature aggrFeature2 = new Feature(aggrFeatureName2, histogram);

        /////////////////////////////////////////////////////
        Map<String, Feature> aggrFeatures = new HashMap<>();
        aggrFeatures.put(aggrFeatureName1, aggrFeature1);
        aggrFeatures.put(aggrFeatureName2, aggrFeature2);

         Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(null, aggrFeatures, featureMap);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = aggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = aggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNvalues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double avg = (0.5+2.0+3.0+1.0+3.5+0.5+1.0+2.0+3.0)/9;
        Double a1 = Math.pow(( 0.5 - avg), 2);
        Double a2 = Math.pow(( 2.0 - avg), 2);
        Double a3 = Math.pow(( 3.0 - avg), 2);
        Double a4 = Math.pow(( 1.0 - avg), 2);
        Double a5 = Math.pow((3.5 - avg), 2);
        Double a6 = Math.pow(( 0.5- avg), 2);
        Double a7 = Math.pow(( 1.0- avg), 2);
        Double a8 = Math.pow(( 2.0- avg), 2);
        Double a9 = Math.pow(( 3.0- avg), 2);
        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9;
        Double std = Math.sqrt((sum)/9);

        Assert.assertEquals((Long) 9L, avgStdNvalues.getN());
        Assert.assertEquals(avg, avgStdNvalues.getAvg());

        Assert.assertTrue(Math.abs(std-avgStdNvalues.getStd())<0.000001);
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);


        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals((Double)9.0, histValue.get(2) );
        Assert.assertEquals((Double)19.0, histValue.get(2L) );
        Assert.assertEquals((Double)30.0, histValue.get("2") );

        Double avg2 = (9+19+30)/3d;
        Double one = Math.pow((9-avg2),2);
        Double two = Math.pow((19-avg2),2);
        Double three = Math.pow((30-avg2),2);
        Double sum2 = one+two+three;
        Double std2 = Math.sqrt((sum2)/3);
        Assert.assertEquals(std2, (Double) histValue.getPopulationStandardDeviation());


    }

    @Test
    public void testUpdateWithNullAggrFeatures() {
        //////////////////////////////////////////////////////
        // ContinuousValueAvgStdN
        /////////////////////////////////////////////////////
        String aggrFeatureName1 = "MyAggrFeature1";

        Map<String, Feature> featureMap = new HashMap<>();
        featureMap.put("feature1"+aggrFeatureName1, new Feature("feature1"+aggrFeatureName1, 3.5));
        featureMap.put("feature2"+aggrFeatureName1, new Feature("feature2"+aggrFeatureName1, 10.0));
        featureMap.put("feature3"+aggrFeatureName1, new Feature("feature3"+aggrFeatureName1, 30.0));
        featureMap.put("not relevant", new Feature("not relevant", 30.0));


        AggregatedFeatureConf aggrFuncConf1 = createAggrFeatureConf3(aggrFeatureName1, AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE);


        //////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////


        String aggrFeatureName2 = "MyAggrFeature2";

        featureMap.put("feature1"+aggrFeatureName2, new Feature("feature1"+aggrFeatureName2, 2));
        featureMap.put("feature2"+aggrFeatureName2, new Feature("feature2"+aggrFeatureName2, 2L));
        featureMap.put("not relevant", new Feature("not relevant", 2));


        //Feature aggrFeature2 = new Feature(aggrFeatureName2, histogram);
        AggregatedFeatureConf aggrFuncConf2 = createAggrFeatureConf3(aggrFeatureName2, AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE);

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
        aggrFeatureConfs.add(aggrFuncConf1);
        aggrFeatureConfs.add(aggrFuncConf2);

        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(aggrFeatureConfs, null, featureMap);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = updatedAggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = updatedAggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNvalues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double avg = (43.5)/3.0;
        Double a10 = Math.pow(( 3.5- avg), 2);
        Double a11 = Math.pow(( 10.0- avg), 2);
        Double a12 = Math.pow(( 30.0- avg), 2);
        Double sum = a10+a11+a12;
        Double std = Math.sqrt((sum)/3);

        Assert.assertEquals((Long) 3L, avgStdNvalues.getN());
        Assert.assertEquals(avg, avgStdNvalues.getAvg());
        Assert.assertEquals( std,  avgStdNvalues.getStd());
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);


        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals(2L, histValue.getN());
        Assert.assertEquals((Double)1.0, histValue.get(2) );
        Assert.assertEquals((Double)1.0, histValue.get(2L) );

        avg = 1d;
        Double one = Math.pow((1-avg),2);
        Double two = Math.pow((1-avg),2);
        Double sum2 = one+two;
        Double std2 = Math.sqrt((sum2)/2);
        Assert.assertEquals(std2, (Double) histValue.getPopulationStandardDeviation());


    }

    @Test
    public void testUpdateWithNullFeatures() {
        //////////////////////////////////////////////////////
        // ContinuousValueAvgStdN
        /////////////////////////////////////////////////////
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5);
        avgStdN.add(2.0);
        avgStdN.add(3.0);
        avgStdN.add(1.0);
        avgStdN.add(3.5);
        avgStdN.add(0.5);
        avgStdN.add(1.0);
        avgStdN.add(2.0);
        avgStdN.add(3.0);

        String aggrFeatureName1 = "MyAggrFeature1";
        Feature aggrFeature1 = new Feature(aggrFeatureName1, avgStdN);

        //////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////
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

        String aggrFeatureName2 = "MyAggrFeature2";
        Feature aggrFeature2 = new Feature(aggrFeatureName2, histogram);

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
        AggregatedFeatureConf aggrFuncConf1 = createAggrFeatureConf3(aggrFeatureName1, AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE);
        AggregatedFeatureConf aggrFuncConf2 = createAggrFeatureConf3(aggrFeatureName2, AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE);
        aggrFeatureConfs.add(aggrFuncConf1);
        aggrFeatureConfs.add(aggrFuncConf2);

        Map<String, Feature> aggrFeatures = new HashMap<>();
        aggrFeatures.put(aggrFeatureName1, aggrFeature1);
        aggrFeatures.put(aggrFeatureName2, aggrFeature2);

        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(aggrFeatureConfs, aggrFeatures, null);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = aggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = aggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNvalues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double avg = (0.5+2.0+3.0+1.0+3.5+0.5+1.0+2.0+3.0)/9;
        Double a1 = Math.pow(( 0.5 - avg), 2);
        Double a2 = Math.pow(( 2.0 - avg), 2);
        Double a3 = Math.pow(( 3.0 - avg), 2);
        Double a4 = Math.pow(( 1.0 - avg), 2);
        Double a5 = Math.pow((3.5 - avg), 2);
        Double a6 = Math.pow(( 0.5- avg), 2);
        Double a7 = Math.pow(( 1.0- avg), 2);
        Double a8 = Math.pow(( 2.0- avg), 2);
        Double a9 = Math.pow(( 3.0- avg), 2);
        Double sum = a1+a2+a3+a4+a5+a6+a7+a8+a9;
        Double std = Math.sqrt((sum)/9);

        Assert.assertEquals((Long) 9L, avgStdNvalues.getN());
        Assert.assertEquals(avg, avgStdNvalues.getAvg());

        Assert.assertTrue(Math.abs(std-avgStdNvalues.getStd())<0.000001);
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);


        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals((Double)9.0, histValue.get(2) );
        Assert.assertEquals((Double)19.0, histValue.get(2L) );
        Assert.assertEquals((Double)30.0, histValue.get("2") );

        Double avg2 = (9+19+30)/3d;
        Double one = Math.pow((9-avg2),2);
        Double two = Math.pow((19-avg2),2);
        Double three = Math.pow((30-avg2),2);
        Double sum2 = one+two+three;
        Double std2 = Math.sqrt((sum2)/3);
        Assert.assertEquals(std2, (Double) histValue.getPopulationStandardDeviation());

    }


}
