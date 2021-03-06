package fortscale.aggregation.feature.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.bucket.AggregatedFeatureConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.filter.JsonFilter;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.common.util.ContinuousValueAvgStdN;
import fortscale.common.util.GenericHistogram;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.*;

public class AggrFeatureFuncServiceTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private AggrFeatureFuncService funcService;

    @Before
    public void initTest() {
        funcService = new AggrFeatureFuncService();
    }

    private AggregatedFeatureConf createAggrFeatureConf3(String aggrFeatureName, String funcName, String funcParam, JsonFilter filter) {
        List<String> featureNames = new ArrayList<>();
        featureNames.add("feature1" + aggrFeatureName);
        featureNames.add("feature2" + aggrFeatureName);
        featureNames.add("feature3" + aggrFeatureName);
        Map<String, List<String>> featureNamesMap = new HashMap<>();
        featureNamesMap.put(funcParam, featureNames);
        JSONObject funcConf = new JSONObject();
        funcConf.put("type", funcName);
        AggregatedFeatureConf ret = new AggregatedFeatureConf(aggrFeatureName, featureNamesMap, deserializeAggrFeatureFuncConf(funcConf));
        if (filter != null) ret.setFilter(filter);
        return ret;
    }

    @Test
    public void testUpdateWithTwoAggrFeatures() {
        /////////////////////////////////////////////////////
        // ContinuousValueAvgStdN
        /////////////////////////////////////////////////////
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5);
        Double a1 = Math.pow((0.5 - 5.0), 2);
        avgStdN.add(2.0);
        Double a2 = Math.pow((2.0 - 5.0), 2);
        avgStdN.add(3.0);
        Double a3 = Math.pow((3.0 - 5.0), 2);
        avgStdN.add(1.0);
        Double a4 = Math.pow((1.0 - 5.0), 2);
        avgStdN.add(3.5);
        Double a5 = Math.pow((3.5 - 5.0), 2);
        avgStdN.add(0.5);
        Double a6 = Math.pow((0.5 - 5.0), 2);
        avgStdN.add(1.0);
        Double a7 = Math.pow((1.0 - 5.0), 2);
        avgStdN.add(2.0);
        Double a8 = Math.pow((2.0 - 5.0), 2);
        avgStdN.add(3.0);
        Double a9 = Math.pow((3.0 - 5.0), 2);

        String aggrFeatureName1 = "MyAggrFeature1";

        @SuppressWarnings("unchecked")
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>("feature1" + aggrFeatureName1, 3.5),
                new ImmutablePair<>("feature2" + aggrFeatureName1, 10.0),
                new ImmutablePair<>("feature3" + aggrFeatureName1, 30.0),
                new ImmutablePair<>("not relevant", 30.0));
        Double a10 = Math.pow((3.5 - 5.0), 2);
        Double a11 = Math.pow((10.0 - 5.0), 2);
        Double a12 = Math.pow((30.0 - 5.0), 2);

        AggregatedFeatureConf aggrFuncConf1 = createAggrFeatureConf3(aggrFeatureName1, AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureAvgStdNFunc.COUNT_BY_FIELD_NAME, null);

        /////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(22, 0.5);
        histogram.add(22, 2.0);
        histogram.add("22", 3.0);
        histogram.add(22L, 3.5);
        histogram.add(2L, 0.5);
        histogram.add(2L, 2.0);
        histogram.add(2L, 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);
        histogram.add("2.0", 30.0);

        String aggrFeatureName2 = "MyAggrFeature2";

        featureMap.put("feature1" + aggrFeatureName2, new Feature("feature1" + aggrFeatureName2, new FeatureNumericValue(22)));
        featureMap.put("feature2" + aggrFeatureName2, new Feature("feature2" + aggrFeatureName2, new FeatureNumericValue(2L)));
        featureMap.put("not relevant", new Feature("not relevant", new FeatureNumericValue(22)));

        AggregatedFeatureConf aggrFuncConf2 = createAggrFeatureConf3(aggrFeatureName2, AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, null);

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
        aggrFeatureConfs.add(aggrFuncConf1);
        aggrFeatureConfs.add(aggrFuncConf2);

        @SuppressWarnings("unchecked")
        Map<String, Feature> aggrFeatures = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(aggrFeatureName1, avgStdN),
                new ImmutablePair<>(aggrFeatureName2, histogram));

        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(null, aggrFeatureConfs, aggrFeatures, featureMap);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = aggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = aggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNValues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double sum = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9 + a10 + a11 + a12;
        Double std = Math.sqrt((sum) / 12);

        Assert.assertEquals((Long)12L, avgStdNValues.getN());
        Assert.assertEquals((Double)5.0, avgStdNValues.getAvg());
        Assert.assertEquals(std, avgStdNValues.getStd());
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);

        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals((Double)10.0, histValue.get(22));
        Assert.assertEquals((Double)20.0, histValue.get(2L));
        Assert.assertEquals((Double)30.0, histValue.get("2.0"));

        Double avg = 20d;
        Double one = Math.pow((10 - avg), 2);
        Double two = Math.pow((20 - avg), 2);
        Double three = Math.pow((30 - avg), 2);
        Double sum2 = one + two + three;
        Double std2 = Math.sqrt((sum2) / 3);
        Assert.assertEquals(std2, (Double)histValue.getPopulationStandardDeviation());
    }

    @Test
    public void testUpdateWithTwoAggrFeaturesWithFilterNotPassHistogramPassContinuous() {
        String testFieldName = "test";
        double testFieldValue = 3.0;
        /////////////////////////////////////////////////////
        // ContinuousValueAvgStdN
        /////////////////////////////////////////////////////
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5);
        Double a1 = Math.pow((0.5 - 5.0), 2);
        avgStdN.add(2.0);
        Double a2 = Math.pow((2.0 - 5.0), 2);
        avgStdN.add(3.0);
        Double a3 = Math.pow((3.0 - 5.0), 2);
        avgStdN.add(1.0);
        Double a4 = Math.pow((1.0 - 5.0), 2);
        avgStdN.add(3.5);
        Double a5 = Math.pow((3.5 - 5.0), 2);
        avgStdN.add(0.5);
        Double a6 = Math.pow((0.5 - 5.0), 2);
        avgStdN.add(1.0);
        Double a7 = Math.pow((1.0 - 5.0), 2);
        avgStdN.add(2.0);
        Double a8 = Math.pow((2.0 - 5.0), 2);
        avgStdN.add(3.0);
        Double a9 = Math.pow((3.0 - 5.0), 2);

        String aggrFeatureName1 = "MyAggrFeature1";

        @SuppressWarnings("unchecked")
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>("feature1" + aggrFeatureName1, 3.5),
                new ImmutablePair<>("feature2" + aggrFeatureName1, 10.0),
                new ImmutablePair<>("feature3" + aggrFeatureName1, 30.0),
                new ImmutablePair<>("not relevant", 30.0));
        Double a10 = Math.pow((3.5 - 5.0), 2);
        Double a11 = Math.pow((10.0 - 5.0), 2);
        Double a12 = Math.pow((30.0 - 5.0), 2);

        String filterJsonPath = String.format("[?(@.%s<%f)]", testFieldName, testFieldValue);
        AggregatedFeatureConf aggrFuncConf1 = createAggrFeatureConf3(aggrFeatureName1, AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureAvgStdNFunc.COUNT_BY_FIELD_NAME, new JsonFilter(filterJsonPath));

        /////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////
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

        String aggrFeatureName2 = "MyAggrFeature2";

        featureMap.put("feature1" + aggrFeatureName2, new Feature("feature1" + aggrFeatureName2, new FeatureNumericValue(2)));
        featureMap.put("feature2" + aggrFeatureName2, new Feature("feature2" + aggrFeatureName2, new FeatureNumericValue(2L)));
        featureMap.put("not relevant", new Feature("not relevant", new FeatureNumericValue(2)));

        filterJsonPath = String.format("[?(@.%s>%f)]", testFieldName, testFieldValue);
        AggregatedFeatureConf aggrFuncConf2 = createAggrFeatureConf3(aggrFeatureName2, AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, new JsonFilter(filterJsonPath));

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
        aggrFeatureConfs.add(aggrFuncConf1);
        aggrFeatureConfs.add(aggrFuncConf2);

        @SuppressWarnings("unchecked")
        Map<String, Feature> aggrFeatures = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(aggrFeatureName1, avgStdN),
                new ImmutablePair<>(aggrFeatureName2, histogram));

        TestAdeRecord record = new TestAdeRecord();
        record.test = testFieldValue - 1;
        AdeRecordReader recordReader = new AdeRecordReader(record);
        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(recordReader, aggrFeatureConfs, aggrFeatures, featureMap);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = aggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = aggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNValues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double sum = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9 + a10 + a11 + a12;
        Double std = Math.sqrt((sum) / 12);

        Assert.assertEquals((Long)12L, avgStdNValues.getN());
        Assert.assertEquals((Double)5.0, avgStdNValues.getAvg());
        Assert.assertEquals(std, avgStdNValues.getStd());
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);

        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals(histogram.get(2), histValue.get(2));
        Assert.assertEquals(histogram.get(2L), histValue.get(2L));
        Assert.assertEquals(histogram.get("2"), histValue.get("2"));

        Assert.assertEquals((Double)histogram.getPopulationStandardDeviation(), (Double)histValue.getPopulationStandardDeviation());
    }

    @Test
    public void testUpdateWithTwoAggrFeaturesWithFilterPassHistogramNotPassContinuous() {
        String testFieldName = "test";
        double testFieldValue = 3.0;
        /////////////////////////////////////////////////////
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

        @SuppressWarnings("unchecked")
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>("feature1" + aggrFeatureName1, 3.5),
                new ImmutablePair<>("feature2" + aggrFeatureName1, 10.0),
                new ImmutablePair<>("feature3" + aggrFeatureName1, 30.0),
                new ImmutablePair<>("not relevant", 30.0));

        String filterJsonPath = String.format("[?(@.%s>%f)]", testFieldName, testFieldValue);
        AggregatedFeatureConf aggrFuncConf1 = createAggrFeatureConf3(aggrFeatureName1, AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureAvgStdNFunc.COUNT_BY_FIELD_NAME, new JsonFilter(filterJsonPath));

        /////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(22, 0.5);
        histogram.add(22, 2.0);
        histogram.add(22, 3.0);
        histogram.add(22, 3.5);
        histogram.add(2L, 0.5);
        histogram.add(2L, 2.0);
        histogram.add(2L, 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);
        histogram.add("2.0", 30.0);

        String aggrFeatureName2 = "MyAggrFeature2";

        featureMap.put("feature1" + aggrFeatureName2, new Feature("feature1" + aggrFeatureName2, new FeatureNumericValue(22)));
        featureMap.put("feature2" + aggrFeatureName2, new Feature("feature2" + aggrFeatureName2, new FeatureNumericValue(2L)));
        featureMap.put("not relevant", new Feature("not relevant", new FeatureNumericValue(22)));

        filterJsonPath = String.format("[?(@.%s<%f)]", testFieldName, testFieldValue);
        AggregatedFeatureConf aggrFuncConf2 = createAggrFeatureConf3(aggrFeatureName2, AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, new JsonFilter(filterJsonPath));

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
        aggrFeatureConfs.add(aggrFuncConf1);
        aggrFeatureConfs.add(aggrFuncConf2);

        @SuppressWarnings("unchecked")
        Map<String, Feature> aggrFeatures = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(aggrFeatureName1, avgStdN),
                new ImmutablePair<>(aggrFeatureName2, histogram));

        TestAdeRecord record = new TestAdeRecord();
        record.test = testFieldValue - 1;
        AdeRecordReader recordReader = new AdeRecordReader(record);
        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(recordReader, aggrFeatureConfs, aggrFeatures, featureMap);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = aggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = aggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNValues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();

        Assert.assertEquals(avgStdN.getN(), avgStdNValues.getN());
        Assert.assertEquals(avgStdN.getAvg(), avgStdNValues.getAvg());
        Assert.assertEquals(avgStdN.getStd(), avgStdNValues.getStd());
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);

        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals((Double)10.0, histValue.get(22));
        Assert.assertEquals((Double)20.0, histValue.get(2L));
        Assert.assertEquals((Double)30.0, histValue.get("2.0"));

        Double avg = 20d;
        Double one = Math.pow((10 - avg), 2);
        Double two = Math.pow((20 - avg), 2);
        Double three = Math.pow((30 - avg), 2);
        Double sum2 = one + two + three;
        Double std2 = Math.sqrt((sum2) / 3);
        Assert.assertEquals(std2, (Double)histValue.getPopulationStandardDeviation());
    }

    @Test
    public void testUpdateWithMissingAggrFeature() {
        /////////////////////////////////////////////////////
        // ContinuousValueAvgStdN
        /////////////////////////////////////////////////////
        ContinuousValueAvgStdN avgStdN = new ContinuousValueAvgStdN();
        avgStdN.add(0.5);
        Double a1 = Math.pow((0.5 - 5.0), 2);
        avgStdN.add(2.0);
        Double a2 = Math.pow((2.0 - 5.0), 2);
        avgStdN.add(3.0);
        Double a3 = Math.pow((3.0 - 5.0), 2);
        avgStdN.add(1.0);
        Double a4 = Math.pow((1.0 - 5.0), 2);
        avgStdN.add(3.5);
        Double a5 = Math.pow((3.5 - 5.0), 2);
        avgStdN.add(0.5);
        Double a6 = Math.pow((0.5 - 5.0), 2);
        avgStdN.add(1.0);
        Double a7 = Math.pow((1.0 - 5.0), 2);
        avgStdN.add(2.0);
        Double a8 = Math.pow((2.0 - 5.0), 2);
        avgStdN.add(3.0);
        Double a9 = Math.pow((3.0 - 5.0), 2);

        String aggrFeatureName1 = "MyAggrFeature1";

        @SuppressWarnings("unchecked")
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>("feature1" + aggrFeatureName1, 3.5),
                new ImmutablePair<>("feature2" + aggrFeatureName1, 10.0),
                new ImmutablePair<>("feature3" + aggrFeatureName1, 30.0),
                new ImmutablePair<>("not relevant", 30.0));
        Double a10 = Math.pow((3.5 - 5.0), 2);
        Double a11 = Math.pow((10.0 - 5.0), 2);
        Double a12 = Math.pow((30.0 - 5.0), 2);

        AggregatedFeatureConf aggrFuncConf1 = createAggrFeatureConf3(aggrFeatureName1, AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureAvgStdNFunc.COUNT_BY_FIELD_NAME, null);

        /////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////

        String aggrFeatureName2 = "MyAggrFeature2";

        featureMap.put("feature1" + aggrFeatureName2, new Feature("feature1" + aggrFeatureName2, new FeatureNumericValue(2)));
        featureMap.put("feature2" + aggrFeatureName2, new Feature("feature2" + aggrFeatureName2, new FeatureNumericValue(2.0)));
        featureMap.put("not relevant", new Feature("not relevant", new FeatureNumericValue(2)));

        AggregatedFeatureConf aggrFuncConf2 = createAggrFeatureConf3(aggrFeatureName2, AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, null);

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
        aggrFeatureConfs.add(aggrFuncConf1);
        aggrFeatureConfs.add(aggrFuncConf2);

        @SuppressWarnings("unchecked")
        Map<String, Feature> aggrFeatures = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(aggrFeatureName1, avgStdN));
        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(null, aggrFeatureConfs, aggrFeatures, featureMap);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = updatedAggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = updatedAggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNValues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double sum = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9 + a10 + a11 + a12;
        Double std = Math.sqrt((sum) / 12);

        Assert.assertEquals((Long)12L, avgStdNValues.getN());
        Assert.assertEquals((Double)5.0, avgStdNValues.getAvg());
        Assert.assertEquals(std, avgStdNValues.getStd());
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);

        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals(2L, histValue.getN());
        Assert.assertEquals((Double)1.0, histValue.get(2));
        Assert.assertEquals((Double)1.0, histValue.get(2.0));

        Double avg = 1d;
        Double one = Math.pow((1 - avg), 2);
        Double two = Math.pow((1 - avg), 2);
        Double sum2 = one + two;
        Double std2 = Math.sqrt((sum2) / 2);
        Assert.assertEquals(std2, (Double)histValue.getPopulationStandardDeviation());
    }

    @Test
    public void testUpdateWithEmptyAggrFeatureConfs() {
        /////////////////////////////////////////////////////
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

        @SuppressWarnings("unchecked")
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>("feature1" + aggrFeatureName1, 3.5),
                new ImmutablePair<>("feature2" + aggrFeatureName1, 10.0),
                new ImmutablePair<>("feature3" + aggrFeatureName1, 30.0),
                new ImmutablePair<>("not relevant" + aggrFeatureName1, 30.0));

        /////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(22, 0.5);
        histogram.add(22, 2.0);
        histogram.add(22, 3.0);
        histogram.add(22, 3.5);
        histogram.add(2, 0.5);
        histogram.add(2, 2.0);
        histogram.add("2", 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);
        histogram.add(2.0, 30.0);

        String aggrFeatureName2 = "MyAggrFeature2";

        featureMap.put("feature1" + aggrFeatureName2, new Feature("feature1" + aggrFeatureName2, new FeatureNumericValue(22)));
        featureMap.put("feature2" + aggrFeatureName2, new Feature("feature2" + aggrFeatureName2, new FeatureNumericValue(2L)));
        featureMap.put("not relevant", new Feature("not relevant", new FeatureNumericValue(22)));

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();

        @SuppressWarnings("unchecked")
        Map<String, Feature> aggrFeatures = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(aggrFeatureName1, avgStdN),
                new ImmutablePair<>(aggrFeatureName2, histogram));

        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(null, aggrFeatureConfs, aggrFeatures, featureMap);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = aggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = aggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNValues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double avg = (0.5 + 2.0 + 3.0 + 1.0 + 3.5 + 0.5 + 1.0 + 2.0 + 3.0) / 9;
        Double a1 = Math.pow((0.5 - avg), 2);
        Double a2 = Math.pow((2.0 - avg), 2);
        Double a3 = Math.pow((3.0 - avg), 2);
        Double a4 = Math.pow((1.0 - avg), 2);
        Double a5 = Math.pow((3.5 - avg), 2);
        Double a6 = Math.pow((0.5 - avg), 2);
        Double a7 = Math.pow((1.0 - avg), 2);
        Double a8 = Math.pow((2.0 - avg), 2);
        Double a9 = Math.pow((3.0 - avg), 2);
        Double sum = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9;
        Double std = Math.sqrt((sum) / 9);

        Assert.assertEquals((Long)9L, avgStdNValues.getN());
        Assert.assertEquals(avg, avgStdNValues.getAvg());

        Assert.assertTrue(Math.abs(std - avgStdNValues.getStd()) < 0.000001);
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);

        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals((Double)9.0, histValue.get(22));
        Assert.assertEquals((Double)19.0, histValue.get(2L));
        Assert.assertEquals((Double)30.0, histValue.get(2.0));

        Double avg2 = (9 + 19 + 30) / 3d;
        Double one = Math.pow((9 - avg2), 2);
        Double two = Math.pow((19 - avg2), 2);
        Double three = Math.pow((30 - avg2), 2);
        Double sum2 = one + two + three;
        Double std2 = Math.sqrt((sum2) / 3);
        Assert.assertEquals(std2, (Double)histValue.getPopulationStandardDeviation());
    }

    @Test
    public void testUpdateWithEmptyFeatures() {
        /////////////////////////////////////////////////////
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

        /////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(22, 0.5);
        histogram.add(22L, 2.0);
        histogram.add("22", 3.0);
        histogram.add(22, 3.5);
        histogram.add(2, 0.5);
        histogram.add("2", 2.0);
        histogram.add(2L, 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);
        histogram.add("2.0", 20.0);
        histogram.add(2.0, 10.0);

        String aggrFeatureName2 = "MyAggrFeature2";

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
        AggregatedFeatureConf aggrFuncConf1 = createAggrFeatureConf3(aggrFeatureName1, AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureAvgStdNFunc.COUNT_BY_FIELD_NAME, null);
        AggregatedFeatureConf aggrFuncConf2 = createAggrFeatureConf3(aggrFeatureName2, AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, null);
        aggrFeatureConfs.add(aggrFuncConf1);
        aggrFeatureConfs.add(aggrFuncConf2);

        @SuppressWarnings("unchecked")
        Map<String, Feature> aggrFeatures = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(aggrFeatureName1, avgStdN),
                new ImmutablePair<>(aggrFeatureName2, histogram));

        Map<String, Feature> featureMap = new HashMap<>();
        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(null, aggrFeatureConfs, aggrFeatures, featureMap);

        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = aggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = aggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNValues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double avg = (0.5 + 2.0 + 3.0 + 1.0 + 3.5 + 0.5 + 1.0 + 2.0 + 3.0) / 9;
        Double a1 = Math.pow((0.5 - avg), 2);
        Double a2 = Math.pow((2.0 - avg), 2);
        Double a3 = Math.pow((3.0 - avg), 2);
        Double a4 = Math.pow((1.0 - avg), 2);
        Double a5 = Math.pow((3.5 - avg), 2);
        Double a6 = Math.pow((0.5 - avg), 2);
        Double a7 = Math.pow((1.0 - avg), 2);
        Double a8 = Math.pow((2.0 - avg), 2);
        Double a9 = Math.pow((3.0 - avg), 2);
        Double sum = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9;
        Double std = Math.sqrt((sum) / 9);

        Assert.assertEquals((Long)9L, avgStdNValues.getN());
        Assert.assertEquals(avg, avgStdNValues.getAvg());

        Assert.assertTrue(Math.abs(std - avgStdNValues.getStd()) < 0.000001);
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);

        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals((Double)9.0, histValue.get(22));
        Assert.assertEquals((Double)19.0, histValue.get(2L));
        Assert.assertEquals((Double)30.0, histValue.get(2.0));

        Double avg2 = (9 + 19 + 30) / 3d;
        Double one = Math.pow((9 - avg2), 2);
        Double two = Math.pow((19 - avg2), 2);
        Double three = Math.pow((30 - avg2), 2);
        Double sum2 = one + two + three;
        Double std2 = Math.sqrt((sum2) / 3);
        Assert.assertEquals(std2, (Double)histValue.getPopulationStandardDeviation());
    }

    @Test
    public void testUpdateWithNullConfs() {
        /////////////////////////////////////////////////////
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

        @SuppressWarnings("unchecked")
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>("feature1" + aggrFeatureName1, 3.5),
                new ImmutablePair<>("feature2" + aggrFeatureName1, 10.0),
                new ImmutablePair<>("feature3" + aggrFeatureName1, 30.0),
                new ImmutablePair<>("not relevant", 30.0));

        /////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(22, 0.5);
        histogram.add(22L, 2.0);
        histogram.add("22", 3.0);
        histogram.add(22, 3.5);
        histogram.add(2, 0.5);
        histogram.add("2", 2.0);
        histogram.add(2L, 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);
        histogram.add("2.0", 30.0);

        String aggrFeatureName2 = "MyAggrFeature2";

        featureMap.put("feature1" + aggrFeatureName2, new Feature("feature1" + aggrFeatureName2, new FeatureNumericValue(22)));
        featureMap.put("feature2" + aggrFeatureName2, new Feature("feature2" + aggrFeatureName2, new FeatureNumericValue(2L)));
        featureMap.put("not relevant", new Feature("not relevant", new FeatureNumericValue(2)));

        /////////////////////////////////////////////////////
        @SuppressWarnings("unchecked")
        Map<String, Feature> aggrFeatures = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(aggrFeatureName1, avgStdN),
                new ImmutablePair<>(aggrFeatureName2, histogram));

        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(null, null, aggrFeatures, featureMap);
        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = aggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = aggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNValues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double avg = (0.5 + 2.0 + 3.0 + 1.0 + 3.5 + 0.5 + 1.0 + 2.0 + 3.0) / 9;
        Double a1 = Math.pow((0.5 - avg), 2);
        Double a2 = Math.pow((2.0 - avg), 2);
        Double a3 = Math.pow((3.0 - avg), 2);
        Double a4 = Math.pow((1.0 - avg), 2);
        Double a5 = Math.pow((3.5 - avg), 2);
        Double a6 = Math.pow((0.5 - avg), 2);
        Double a7 = Math.pow((1.0 - avg), 2);
        Double a8 = Math.pow((2.0 - avg), 2);
        Double a9 = Math.pow((3.0 - avg), 2);
        Double sum = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9;
        Double std = Math.sqrt((sum) / 9);

        Assert.assertEquals((Long)9L, avgStdNValues.getN());
        Assert.assertEquals(avg, avgStdNValues.getAvg());

        Assert.assertTrue(Math.abs(std - avgStdNValues.getStd()) < 0.000001);
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);

        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals((Double)9.0, histValue.get(22));
        Assert.assertEquals((Double)19.0, histValue.get(2L));
        Assert.assertEquals((Double)30.0, histValue.get("2.0"));

        Double avg2 = (9 + 19 + 30) / 3d;
        Double one = Math.pow((9 - avg2), 2);
        Double two = Math.pow((19 - avg2), 2);
        Double three = Math.pow((30 - avg2), 2);
        Double sum2 = one + two + three;
        Double std2 = Math.sqrt((sum2) / 3);
        Assert.assertEquals(std2, (Double)histValue.getPopulationStandardDeviation());
    }

    @Test
    public void testUpdateWithNullAggrFeatures() {
        /////////////////////////////////////////////////////
        // ContinuousValueAvgStdN
        /////////////////////////////////////////////////////
        String aggrFeatureName1 = "MyAggrFeature1";

        @SuppressWarnings("unchecked")
        Map<String, Feature> featureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>("feature1" + aggrFeatureName1, 3.5),
                new ImmutablePair<>("feature2" + aggrFeatureName1, 10.0),
                new ImmutablePair<>("feature3" + aggrFeatureName1, 30.0),
                new ImmutablePair<>("not relevant", 30.0));

        AggregatedFeatureConf aggrFuncConf1 = createAggrFeatureConf3(aggrFeatureName1, AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureAvgStdNFunc.COUNT_BY_FIELD_NAME, null);

        /////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////

        String aggrFeatureName2 = "MyAggrFeature2";

        featureMap.put("feature1" + aggrFeatureName2, new Feature("feature1" + aggrFeatureName2, new FeatureNumericValue(22)));
        featureMap.put("feature2" + aggrFeatureName2, new Feature("feature2" + aggrFeatureName2, new FeatureNumericValue(2L)));
        featureMap.put("not relevant", new Feature("not relevant", new FeatureNumericValue(22)));

        AggregatedFeatureConf aggrFuncConf2 = createAggrFeatureConf3(aggrFeatureName2, AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, null);

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
        aggrFeatureConfs.add(aggrFuncConf1);
        aggrFeatureConfs.add(aggrFuncConf2);

        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(null, aggrFeatureConfs, null, featureMap);
        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = updatedAggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = updatedAggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNValues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double avg = (43.5) / 3.0;
        Double a10 = Math.pow((3.5 - avg), 2);
        Double a11 = Math.pow((10.0 - avg), 2);
        Double a12 = Math.pow((30.0 - avg), 2);
        Double sum = a10 + a11 + a12;
        Double std = Math.sqrt((sum) / 3);

        Assert.assertEquals((Long)3L, avgStdNValues.getN());
        Assert.assertEquals(avg, avgStdNValues.getAvg());
        Assert.assertEquals(std, avgStdNValues.getStd());
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);

        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals(2L, histValue.getN());
        Assert.assertEquals((Double)1.0, histValue.get(22));
        Assert.assertEquals((Double)1.0, histValue.get(2L));

        avg = 1d;
        Double one = Math.pow((1 - avg), 2);
        Double two = Math.pow((1 - avg), 2);
        Double sum2 = one + two;
        Double std2 = Math.sqrt((sum2) / 2);
        Assert.assertEquals(std2, (Double)histValue.getPopulationStandardDeviation());
    }

    @Test
    public void testUpdateWithNullFeatures() {
        /////////////////////////////////////////////////////
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

        /////////////////////////////////////////////////////
        // GenericHistogram
        /////////////////////////////////////////////////////
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(22, 0.5);
        histogram.add(22, 2.0);
        histogram.add(22L, 3.0);
        histogram.add("22", 3.5);
        histogram.add(2, 0.5);
        histogram.add("2", 2.0);
        histogram.add(2L, 3.0);
        histogram.add(2L, 3.5);
        histogram.add(2L, 10.0);
        histogram.add(2.0, 30.0);

        String aggrFeatureName2 = "MyAggrFeature2";

        /////////////////////////////////////////////////////
        List<AggregatedFeatureConf> aggrFeatureConfs = new ArrayList<>();
        AggregatedFeatureConf aggrFuncConf1 = createAggrFeatureConf3(aggrFeatureName1, AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureAvgStdNFunc.COUNT_BY_FIELD_NAME, null);
        AggregatedFeatureConf aggrFuncConf2 = createAggrFeatureConf3(aggrFeatureName2, AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE, AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, null);
        aggrFeatureConfs.add(aggrFuncConf1);
        aggrFeatureConfs.add(aggrFuncConf2);

        @SuppressWarnings("unchecked")
        Map<String, Feature> aggrFeatures = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(aggrFeatureName1, avgStdN),
                new ImmutablePair<>(aggrFeatureName2, histogram));

        Map<String, Feature> updatedAggrFeatures = funcService.updateAggrFeatures(null, aggrFeatureConfs, aggrFeatures, null);
        Assert.assertEquals(2, updatedAggrFeatures.size());

        Feature resAggrFeature1 = aggrFeatures.get(aggrFeatureName1);
        Feature resAggrFeature2 = aggrFeatures.get(aggrFeatureName2);

        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature1.getValue().getClass(), ContinuousValueAvgStdN.class);

        ContinuousValueAvgStdN avgStdNValues = (ContinuousValueAvgStdN)resAggrFeature1.getValue();
        Double avg = (0.5 + 2.0 + 3.0 + 1.0 + 3.5 + 0.5 + 1.0 + 2.0 + 3.0) / 9;
        Double a1 = Math.pow((0.5 - avg), 2);
        Double a2 = Math.pow((2.0 - avg), 2);
        Double a3 = Math.pow((3.0 - avg), 2);
        Double a4 = Math.pow((1.0 - avg), 2);
        Double a5 = Math.pow((3.5 - avg), 2);
        Double a6 = Math.pow((0.5 - avg), 2);
        Double a7 = Math.pow((1.0 - avg), 2);
        Double a8 = Math.pow((2.0 - avg), 2);
        Double a9 = Math.pow((3.0 - avg), 2);
        Double sum = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9;
        Double std = Math.sqrt((sum) / 9);

        Assert.assertEquals((Long)9L, avgStdNValues.getN());
        Assert.assertEquals(avg, avgStdNValues.getAvg());

        Assert.assertTrue(Math.abs(std - avgStdNValues.getStd()) < 0.000001);
        /////////////////////////////////////////////////////
        Assert.assertEquals(resAggrFeature2.getValue().getClass(), GenericHistogram.class);

        GenericHistogram histValue = (GenericHistogram)resAggrFeature2.getValue();

        Assert.assertEquals((Double)9.0, histValue.get("22"));
        Assert.assertEquals((Double)19.0, histValue.get(2L));
        Assert.assertEquals((Double)30.0, histValue.get("2.0"));

        Double avg2 = (9 + 19 + 30) / 3d;
        Double one = Math.pow((9 - avg2), 2);
        Double two = Math.pow((19 - avg2), 2);
        Double three = Math.pow((30 - avg2), 2);
        Double sum2 = one + two + three;
        Double std2 = Math.sqrt((sum2) / 3);
        Assert.assertEquals(std2, (Double)histValue.getPopulationStandardDeviation());
    }

    @Test
    public void testCalculateAggrFeature() {
        // Prepare AggrFeatureAvgStdNFunc arguments
        String aggregatedFeatureName = "aggregatedFeatureName";
        List<String> aggregatedFeatureNamesList = new ArrayList<>();
        aggregatedFeatureNamesList.add(aggregatedFeatureName);
        Map<String, List<String>> aggregatedFeatureNamesMap = new HashMap<>();
        aggregatedFeatureNamesMap.put(AggrFeatureAvgStdNFunc.COUNT_BY_FIELD_NAME, aggregatedFeatureNamesList);

        // Prepare AggrFeatureAvgStdNFunc function JSON
        JSONObject aggrFeatureAvgStdNFunc = new JSONObject();
        aggrFeatureAvgStdNFunc.put("type", AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE);
        // Function has no parameters

        // Prepare new aggregated feature event configuration (with AvgStdN as the function)
        String aggregatedFeatureEventName = "testAggregatedFeatureEvent";
        AggregatedFeatureEventConf aggregatedFeatureEventConf = new AggregatedFeatureEventConf(
                aggregatedFeatureEventName, "aggregated_feature_event_type_F", "testBucketConf",
                3, 1, aggregatedFeatureNamesMap, deserializeAggrFeatureEventFuncConf(aggrFeatureAvgStdNFunc));

        // Prepare a list of aggregated feature maps for multiple buckets
        @SuppressWarnings("unchecked")
        Map<String, Feature> aggregatedFeatureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(aggregatedFeatureName, new ContinuousValueAvgStdN()));
        List<Map<String, Feature>> listOfAggregatedFeatureMaps = new ArrayList<>();
        listOfAggregatedFeatureMaps.add(aggregatedFeatureMap);

        // Check function creation and execution
        Feature feature = funcService.calculateAggrFeature(aggregatedFeatureEventConf, listOfAggregatedFeatureMaps);
        Assert.assertNotNull(feature);
        Assert.assertEquals(aggregatedFeatureEventName, feature.getName());
        Assert.assertEquals(ContinuousValueAvgStdN.class, feature.getValue().getClass());

        // Prepare AggrFeatureHistogramFunc arguments
        aggregatedFeatureNamesMap.clear();
        aggregatedFeatureNamesMap.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, aggregatedFeatureNamesList);

        // Prepare AggrFeatureHistogramFunc function JSON
        JSONObject aggrFeatureHistogramFunc = new JSONObject();
        aggrFeatureHistogramFunc.put("type", AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE);
        // Function has no parameters

        // Prepare new aggregated feature event configuration (with Histogram as the function)
        aggregatedFeatureEventConf = new AggregatedFeatureEventConf(
                aggregatedFeatureEventName, "aggregated_feature_event_type_F", "testBucketConf",
                3, 1, aggregatedFeatureNamesMap, deserializeAggrFeatureEventFuncConf(aggrFeatureHistogramFunc));

        // Prepare a list of aggregated feature maps for multiple buckets
        aggregatedFeatureMap.clear();
        aggregatedFeatureMap.put(aggregatedFeatureName, new Feature(aggregatedFeatureName, new GenericHistogram()));

        // Check function creation and execution
        feature = funcService.calculateAggrFeature(aggregatedFeatureEventConf, listOfAggregatedFeatureMaps);
        Assert.assertNotNull(feature);
        Assert.assertEquals(aggregatedFeatureEventName, feature.getName());
        Assert.assertEquals(GenericHistogram.class, feature.getValue().getClass());

        // Prepare AggrFeatureAvgStdNFunc arguments
        aggregatedFeatureNamesMap.clear();
        aggregatedFeatureNamesMap.put(AggrFeatureAvgStdNFunc.COUNT_BY_FIELD_NAME, aggregatedFeatureNamesList);

        // Prepare a new JSON instance for the AggrFeatureAvgStdNFunc function
        JSONObject newAvgStdNFuncJson = new JSONObject();
        newAvgStdNFuncJson.put("type", AggrFeatureAvgStdNFunc.AGGR_FEATURE_FUNCTION_TYPE);
        // Function has no parameters

        // Prepare new aggregated feature event configuration (with AvgStdN as the function)
        aggregatedFeatureEventConf = new AggregatedFeatureEventConf(
                aggregatedFeatureEventName, "aggregated_feature_event_type_F", "testBucketConf",
                3, 1, aggregatedFeatureNamesMap, deserializeAggrFeatureEventFuncConf(newAvgStdNFuncJson));

        // Prepare a list of aggregated feature maps for multiple buckets
        aggregatedFeatureMap.clear();
        aggregatedFeatureMap.put(aggregatedFeatureName, new Feature(aggregatedFeatureName, new ContinuousValueAvgStdN()));

        // Check function execution (function was already created)
        feature = funcService.calculateAggrFeature(aggregatedFeatureEventConf, listOfAggregatedFeatureMaps);
        Assert.assertNotNull(feature);
        Assert.assertEquals(aggregatedFeatureEventName, feature.getName());
        Assert.assertEquals(ContinuousValueAvgStdN.class, feature.getValue().getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAggregatedFeatureEventConfInstantiationWithUnknownFunctionType() {
        Map<String, List<String>> aggregatedFeatureNamesMap = new HashMap<>();
        aggregatedFeatureNamesMap.put("dummyFunctionArgument", Collections.singletonList("aggregatedFeatureName"));
        JSONObject dummyFunction = new JSONObject();
        dummyFunction.put("type", "unknownFunctionType");
        JSONObject params = new JSONObject();
        params.put("dummyFunctionParam", 42);
        dummyFunction.put("params", params);
        new AggregatedFeatureEventConf(
                "testAggregatedFeatureEvent", "aggregated_feature_event_type_F", "testBucketConf",
                3, 1, aggregatedFeatureNamesMap, deserializeAggrFeatureEventFuncConf(dummyFunction));
    }

    @Test
    public void testCalculateAggrFeatureWithNullAggregatedFeatureEventConf() {
        String aggregatedFeatureName = "aggregatedFeatureName";
        @SuppressWarnings("unchecked")
        Map<String, Feature> aggregatedFeatureMap = AggrFeatureTestUtils.createFeatureMap(
                new ImmutablePair<>(aggregatedFeatureName, new ContinuousValueAvgStdN()));
        List<Map<String, Feature>> listOfAggregatedFeatureMaps = new ArrayList<>();
        listOfAggregatedFeatureMaps.add(aggregatedFeatureMap);
        Assert.assertNull(funcService.calculateAggrFeature(null, listOfAggregatedFeatureMaps));
    }

    @Test
    public void testCalculateAggrFeatureWithNullMultipleBucketsAggrFeaturesMapList() {
        List<String> aggregatedFeatureNamesList = new ArrayList<>();
        aggregatedFeatureNamesList.add("aggregatedFeatureName");
        Map<String, List<String>> aggregatedFeatureNamesMap = new HashMap<>();
        aggregatedFeatureNamesMap.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, aggregatedFeatureNamesList);
        JSONObject aggrFeatureHistogramFunc = new JSONObject();
        aggrFeatureHistogramFunc.put("type", AggrFeatureHistogramFunc.AGGR_FEATURE_FUNCTION_TYPE);
        AggregatedFeatureEventConf conf = new AggregatedFeatureEventConf(
                "testAggregatedFeatureEvent", "aggregated_feature_event_type_F", "testBucketConf",
                3, 1, aggregatedFeatureNamesMap, deserializeAggrFeatureEventFuncConf(aggrFeatureHistogramFunc));
        Assert.assertNull(funcService.calculateAggrFeature(conf, null));
    }

    private static IAggrFeatureFunction deserializeAggrFeatureFuncConf(JSONObject funcConf) {
        String jsonString = funcConf.toJSONString();

        try {
            return objectMapper.readValue(jsonString, IAggrFeatureFunction.class);
        } catch (Exception e) {
            String message = String.format("Could not deserialize aggregated feature function %s.", jsonString);
            throw new IllegalArgumentException(message, e);
        }
    }

    private static IAggrFeatureEventFunction deserializeAggrFeatureEventFuncConf(JSONObject funcConf) {
        String jsonString = funcConf.toJSONString();

        try {
            return objectMapper.readValue(jsonString, IAggrFeatureEventFunction.class);
        } catch (Exception e) {
            String message = String.format("Could not deserialize aggregated feature event function %s.", jsonString);
            throw new IllegalArgumentException(message, e);
        }
    }

    class TestAdeRecord extends AdeRecord {
        public double test;

        @Override
        public String getAdeEventType() {
            return null;
        }

        @Override
        public List<String> getDataSources() {
            return null;
        }
    }
}
