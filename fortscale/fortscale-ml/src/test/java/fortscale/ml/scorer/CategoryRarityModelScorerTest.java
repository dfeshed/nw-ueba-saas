package fortscale.ml.scorer;

import fortscale.common.event.EventMessage;
import fortscale.common.feature.Feature;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.builder.CategoryRarityModelBuilder;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.ml.model.cache.ModelsCacheService;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/model-scorer-tests-context.xml"})
public class CategoryRarityModelScorerTest {

    @Autowired
    ModelsCacheService modelsCacheService;

    @Autowired
    FeatureExtractService featureExtractService;

    static void assertScorer(CategoryRarityModelScorer scorer, CategoryRarityModelScorerParams params) {

        Assert.assertEquals(params.getName(), scorer.getName());
        Assert.assertEquals(params.getModelName(), scorer.getModelName());
        Assert.assertEquals(params.getContextFieldNames(), scorer.getContextFieldNames());
        Assert.assertEquals(params.getFeatureName(), scorer.getFeatureName());
        Assert.assertEquals((long)params.getMaxRareCount(), scorer.getAlgorithm().getMaxRareCount());
        Assert.assertEquals((long)params.getMaxNumOfRareFeatures(), scorer.getAlgorithm().getMaxNumOfRareFeatures());
        Assert.assertEquals(params.getMinimumNumberOfDistinctValuesToInfluence(), scorer.getMinNumOfDistinctValuesToInfluence(), scorer.getMinNumOfDistinctValuesToInfluence());
        Assert.assertEquals((long) params.getEnoughNumberOfDistinctValuesToInfluence(), scorer.getEnoughNumOfDistinctValuesToInfluence());
        Assert.assertEquals((long) params.getNumberOfSamplesToInfluenceEnough(), scorer.getEnoughNumOfSamplesToInfluence());
        Assert.assertEquals((long) params.getMinNumOfSamplesToInfluence(), scorer.getMinNumOfSamplesToInfluence());
        Assert.assertEquals(params.getUseCertaintyToCalculateScore(), scorer.isUseCertaintyToCalculateScore());

    }

    static CategoryRarityModelScorer createCategoryRarityModelScorer(CategoryRarityModelScorerParams params) {
        return new CategoryRarityModelScorer(params.getName(),
                params.getModelName(),
                Collections.emptyList(),
                params.getContextFieldNames(),
                params.getFeatureName(),
                params.getMinNumOfSamplesToInfluence(),
                params.getNumberOfSamplesToInfluenceEnough(),
                params.getUseCertaintyToCalculateScore(),
                params.getMinimumNumberOfDistinctValuesToInfluence(),
                params.getEnoughNumberOfDistinctValuesToInfluence(),
                params.getMaxRareCount(),
                params.getMaxNumOfRareFeatures());
    }


    protected EventMessage buildEventMessage(String fieldName, Object fieldValue){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(fieldName, fieldValue);
        jsonObject.put("username", "someone"); // The context
        return new EventMessage(jsonObject);
    }

    //==================================================================================================================
    // CONSTRUCTOR TESTS
    //==================================================================================================================

    @Test
    public void constructor_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_Null_name_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setName(null);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_Empty_name_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setName("");
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_Blank_name_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setName(" ");
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }


    @Test(expected = IllegalArgumentException.class)
    public void constructor_negative_maxRareCount_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(-1);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }


    @Test(expected = IllegalArgumentException.class)
    public void constructor_negative_maxNumOfRareFeatures_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxNumOfRareFeatures(-1);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }


    @Test(expected = IllegalArgumentException.class)
    public void constructor_negative_minimumNumberOfDistinctValuesToInfluence_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMinimumNumberOfDistinctValuesToInfluence(-1);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }


    @Test(expected = IllegalArgumentException.class)
    public void constructor_negative_enoughNumberOfDistinctValuesToInfluence_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setEnoughNumberOfDistinctValuesToInfluence(-1);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }


    @Test(expected = IllegalArgumentException.class)
    public void constructor_zero_numberOfSamplesToInfluenceEnough_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setNumberOfSamplesToInfluenceEnough(0);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }


    @Test(expected = IllegalArgumentException.class)
    public void constructor_zero_minNumOfSamplesToInfluence_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMinNumOfSamplesToInfluence(0);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test
    public void constructor_true_useCertaintyToCalculateScore_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setUseCertaintyToCalculateScore(true);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test
    public void constructor_false_useCertaintyToCalculateScore_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setUseCertaintyToCalculateScore(false);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_Null_modelName_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setModelName(null);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_modelName_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setModelName("");
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_blank_modelName_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setModelName(" ");
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }

    //==================================================================================================================
    // CALCUlATE SCORE ILLEGAL VALUES TESTS
    //==================================================================================================================
    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_null_model_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(null, Collections.emptyList(), new Feature("source-machine", "host1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_non_empty_additional_models_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(null, Collections.singletonList(new CategoryRarityModel()), new Feature("source-machine", "host1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_null_feature_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(new CategoryRarityModel(), Collections.emptyList(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_feature_with_null_name_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(new CategoryRarityModel(), Collections.emptyList(), new Feature(null, "host1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_empty__feature_name_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(new CategoryRarityModel(), Collections.emptyList(), new Feature("", "host1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_blank__feature_name_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(new CategoryRarityModel(), Collections.emptyList(), new Feature("     ", "host1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_null__feature_value_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(new CategoryRarityModel(), Collections.emptyList(), new Feature("source-machine", (String)null));
    }

    @Test
    public void calculateScore_empty__feature_value_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        double score = scorer.calculateScore(new CategoryRarityModel(), Collections.emptyList(), new Feature("source-machine", ""));
        Assert.assertEquals(0.0, score, 0.0);
    }

    @Test
    public void calculateScore_blank__feature_value_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        double score = scorer.calculateScore(new CategoryRarityModel(), Collections.emptyList(), new Feature("source-machine", "    "));
        Assert.assertEquals(0.0, score, 0.0);
    }

    //==================================================================================================================
    // CALCUlATE SCORE SIMPLE TESTS (more advanced tests are at the CategoryRarityModelScorerAlgorithmTest)
    //==================================================================================================================
    @Test
    public void calculateScore_elementaryCheck_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRareFeatures(5);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        long count = 100;
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }
        GenericHistogram histogram = new GenericHistogram();
        featureValueToCountMap.entrySet().forEach(entry -> histogram.add(entry.getKey(), entry.getValue().doubleValue()));
        CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(new CategoryRarityModelBuilderConf(100)).build(histogram);
        String featureWithCount100 = "feature-count-100";
        model.setFeatureCount(featureWithCount100, count);
        double score = scorer.calculateScore(model, Collections.emptyList(), new Feature("feature-with-count-100", featureWithCount100));
        Assert.assertEquals(0.0, score, 0.0);
        score = scorer.calculateScore(model, Collections.emptyList(), new Feature("feature-with-zero-count", "feature-zero-count")); // The scorer should handle it as if count=1
        Assert.assertEquals(100.0, score, 0.0);

    }

    @Test
    public void calculateScore_elementaryCheck2_test() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRareFeatures(5);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        long count = 100;
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }
        GenericHistogram histogram = new GenericHistogram();
        featureValueToCountMap.entrySet().forEach(entry -> histogram.add(entry.getKey(), entry.getValue().doubleValue()));
        CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(new CategoryRarityModelBuilderConf(100)).build(histogram);
        String featureWithCount100 = "feature-count-100";
        String featureWithZeroCount = "feature-zero-count"; // The scorer should handle it as if count=1
        model.setFeatureCount(featureWithCount100, count);
        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithCount100, eventMessage);
        FeatureScore featureScore = scorer.calculateScore(eventMessage, 0L);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
        Assert.assertEquals(params.getName(), featureScore.getName());

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);
        featureScore = scorer.calculateScore(eventMessage, 0L);
        Assert.assertEquals(100.0, featureScore.getScore(), 0.0);
        Assert.assertEquals(params.getName(), featureScore.getName());
    }

    @Test
    public void calculateScore_testing_featureScore_name_with_model_and_no_certainty_test() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRareFeatures(5)
                .setUseCertaintyToCalculateScore(false);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        long count = 100;
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }
        GenericHistogram histogram = new GenericHistogram();
        featureValueToCountMap.entrySet().forEach(entry -> histogram.add(entry.getKey(), entry.getValue().doubleValue()));
        CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(new CategoryRarityModelBuilderConf(100)).build(histogram);
        String featureWithCount100 = "feature-count-100";
        String featureWithZeroCount = "feature-zero-count"; // The scorer should handle it as if count=1
        model.setFeatureCount(featureWithCount100, count);
        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithCount100, eventMessage);
        FeatureScore featureScore = scorer.calculateScore(eventMessage, 0L);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
        Assert.assertEquals(params.getName(), featureScore.getName());

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);
        featureScore = scorer.calculateScore(eventMessage, 0L);
        Assert.assertEquals(100.0, featureScore.getScore(), 0.0);
        Assert.assertEquals(params.getName(), featureScore.getName());
    }

    @Test
    public void calculateScore_testing_featureScore_name_with_null_model_test() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRareFeatures(5);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        String featureWithCount100 = "feature-count-100";
        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, null, featureWithCount100, eventMessage);
        FeatureScore featureScore = scorer.calculateScore(eventMessage, 0L);
        Assert.assertEquals(params.getName(), featureScore.getName());
    }

    private void prepareMocks(AbstractModelScorer scorer, Model model, String feature, EventMessage eventMessage) {
        when(modelsCacheService.getModel(any(), any(), any(), any(Long.class) )).thenReturn(model);
        when(featureExtractService.extract(eq(scorer.getFeatureName()), eq(eventMessage))).thenReturn(new Feature("myFeature", feature));

    }

    @Test
    public void calculateScore_no_model_test() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRareFeatures(5);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1

        HashMap<String, Feature> context = new HashMap<>();
        context.put("dummy", new Feature("dummy", "dummy"));
        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        when(modelsCacheService.getModel(any(), any(), any(), any(Long.class) )).thenReturn(null);
        when(featureExtractService.extract(any(Set.class), eq(eventMessage))).thenReturn(context);
        when(featureExtractService.extract(scorer.getFeatureName(), eventMessage)).thenReturn(featureWithZeroCount);
        FeatureScore featureScore = scorer.calculateScore(eventMessage, 0L);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
    }

    @Test
    public void calculateScore_no_feature_in_event_test() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRareFeatures(5);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        long count = 100;
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }
        GenericHistogram histogram = new GenericHistogram();
        featureValueToCountMap.entrySet().forEach(entry -> histogram.add(entry.getKey(), entry.getValue().doubleValue()));
        CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(new CategoryRarityModelBuilderConf(100)).build(histogram);
        String featureWithCount100 = "feature-count-100";
        model.setFeatureCount(featureWithCount100, count);

        HashMap<String, Feature> context = new HashMap<>();
        context.put("dummy", new Feature("dummy", "dummy"));
        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        when(modelsCacheService.getModel(any(), any(), any(), any(Long.class) )).thenReturn(model);
        when(featureExtractService.extract(any(Set.class), eq(eventMessage))).thenReturn(context);
        when(featureExtractService.extract(scorer.getFeatureName(),eventMessage)).thenReturn(null);
        FeatureScore featureScore = scorer.calculateScore(eventMessage, 0L);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0);
    }

    @Test
    public void calculateScore_null_context_test() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMaxRareCount(15).setMaxNumOfRareFeatures(5);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);

        long count = 100;
        String featureWithCount100 = "feature-count-100";
        String featureWithZeroCount = "feature-zero-count"; // The scorer should handle it as if count=1
        CategoryRarityModel model = createModel(100, count, featureWithCount100);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, null);
        FeatureScore featureScore = scorer.calculateScore(eventMessage, 0L);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0); // With the right context it should return 100
    }

    private CategoryRarityModel createModel(int numOfDistinctValues, long count, String feature) {
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < numOfDistinctValues; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }
        GenericHistogram histogram = new GenericHistogram();
        featureValueToCountMap.entrySet().forEach(entry -> histogram.add(entry.getKey(), entry.getValue().doubleValue()));
        CategoryRarityModel model = (CategoryRarityModel)new CategoryRarityModelBuilder(new CategoryRarityModelBuilderConf(100)).build(histogram);
        model.setFeatureCount(feature, count);
        return model;
    }

    //==================================================================================================================
    // Certainty Tests
    //==================================================================================================================


    @Test
    public void testScoreAndCertaintyOfNumOfFeatureValuesBelowMin() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams()
                .setMinimumNumberOfDistinctValuesToInfluence(20)
                .setEnoughNumberOfDistinctValuesToInfluence(100)
                .setUseCertaintyToCalculateScore(false);

        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        long count = 100;
        String featureWithCount0 = "feature-count-0";
        String featureWithZeroCount = "feature-zero-count"; // The scorer should handle it as if count=1
        CategoryRarityModel model = createModel(19, count, featureWithCount0);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);

        FeatureScore score = scorer.calculateScore(eventMessage, 0);

        Assert.assertNotNull(score);
        Assert.assertEquals(100d, score.getScore(), 0.0);
        Assert.assertEquals(0d, score.getCertainty(), 0.0);
    }

    @Test
    public void testScoreAndCertaintyOfNumOfFeatureValuesBelowMinAndMinGreaterThanEnough() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams()
                .setMinimumNumberOfDistinctValuesToInfluence(20)
                .setEnoughNumberOfDistinctValuesToInfluence(10)
                .setUseCertaintyToCalculateScore(false);

        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        long count = 100;
        String featureWithCount0 = "feature-count-0";
        String featureWithZeroCount = "feature-zero-count"; // The scorer should handle it as if count=1
        CategoryRarityModel model = createModel(19, count, featureWithCount0);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);

        FeatureScore score = scorer.calculateScore(eventMessage, 0);

        Assert.assertNotNull(score);
        Assert.assertEquals(100d, score.getScore(), 0.0);
        Assert.assertEquals(0d, score.getCertainty(), 0.0);

    }

    @Test
    public void testScoreAndCertaintyOfNumOfFeatureValuesEqualsToEnough() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams()
                .setMinimumNumberOfDistinctValuesToInfluence(20)
                .setEnoughNumberOfDistinctValuesToInfluence(100)
                .setUseCertaintyToCalculateScore(false);

        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        long count = 100;
        String featureWithCount0 = "feature-count-0";
        String featureWithZeroCount = "feature-zero-count"; // The scorer should handle it as if count=1
        CategoryRarityModel model = createModel(100, count, featureWithCount0);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);

        FeatureScore score = scorer.calculateScore(eventMessage, 0);

        Assert.assertNotNull(score);
        Assert.assertEquals(100d, score.getScore(), 0.0);
        Assert.assertEquals(1d, score.getCertainty(), 0.0);

    }

    @Test
    public void testScoreAndCertaintyOfNumOfFeatureValuesEqualsToEnoughAndMinGreaterThanEnough() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams()
                .setMinimumNumberOfDistinctValuesToInfluence(200)
                .setEnoughNumberOfDistinctValuesToInfluence(100)
                .setUseCertaintyToCalculateScore(false);

        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        long count = 100;
        String featureWithCount0 = "feature-count-0";
        String featureWithZeroCount = "feature-zero-count"; // The scorer should handle it as if count=1
        CategoryRarityModel model = createModel(100, count, featureWithCount0);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);

        FeatureScore score = scorer.calculateScore(eventMessage, 0);

        Assert.assertNotNull(score);
        Assert.assertEquals(100d, score.getScore(), 0.0);
        Assert.assertEquals(0d, score.getCertainty(), 0.0);

    }

    @Test
    public void testScoreAndCertaintyOfNumOfFeatureValuesEqualsToMin() throws Exception{
        int min = 20;
        int enough = 100;
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams()
                .setMinimumNumberOfDistinctValuesToInfluence(min)
                .setEnoughNumberOfDistinctValuesToInfluence(enough)
                .setUseCertaintyToCalculateScore(false);

        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        long count = 100;
        String featureWithCount0 = "feature-count-0";
        String featureWithZeroCount = "feature-zero-count"; // The scorer should handle it as if count=1
        CategoryRarityModel model = createModel(min, count, featureWithCount0);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);

        FeatureScore score = scorer.calculateScore(eventMessage, 0);
        double expectedCertainty = 1d / (enough - min + 1);
        Assert.assertNotNull(score);
        Assert.assertEquals(100d, score.getScore(), 0.0);
        Assert.assertEquals(expectedCertainty, score.getCertainty(), 0.0);
    }

    @Test
    public void testScoreAndCertaintyOfNumOfFeatureValuesEqualsToMinAndMinGreaterThanEnough() throws Exception{
        int min = 200;
        int enough = 100;
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams()
                .setMinimumNumberOfDistinctValuesToInfluence(min)
                .setEnoughNumberOfDistinctValuesToInfluence(enough)
                .setUseCertaintyToCalculateScore(false);

        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        long count = 100;
        String featureWithCount0 = "feature-count-0";
        String featureWithZeroCount = "feature-zero-count"; // The scorer should handle it as if count=1
        CategoryRarityModel model = createModel(min, count, featureWithCount0);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);

        FeatureScore score = scorer.calculateScore(eventMessage, 0);
        double expectedCertainty = 1d;
        Assert.assertNotNull(score);
        Assert.assertEquals(100d, score.getScore(), 0.0);
        Assert.assertEquals(expectedCertainty, score.getCertainty(), 0.0);

    }


    /**
     * CategoryRarityModelScorer params to ease the testing.
     * The default parameters here are intentionally different from the defaults in the conf itself.
     * Use the setters to override the specific parameter you want to test.
     */
    static class CategoryRarityModelScorerParams {
        String name = "Scorer1";
        String featureName = "source-machine";
        Integer maxRareCount = 10;
        Integer maxNumOfRareFeatures = 6;
        Integer minimumNumberOfDistinctValuesToInfluence = 3;
        Integer enoughNumberOfDistinctValuesToInfluence = 10;
        Integer numberOfSamplesToInfluenceEnough = 10;
        Integer minNumOfSamplesToInfluence = 2;
        Boolean useCertaintyToCalculateScore = true;
        String modelName = "model1";
        List<String> contextFieldNames = new ArrayList<>();

        public CategoryRarityModelScorerParams() {
            contextFieldNames.add("username");
        }

        public String getFeatureName() {
            return featureName;
        }

        public List<String> getContextFieldNames() {
            return contextFieldNames;
        }

        public String getName() {
            return name;
        }

        public CategoryRarityModelScorerParams setName(String name) {
            this.name = name;
            return this;
        }

        public Integer getMaxRareCount() {
            return maxRareCount;
        }

        public CategoryRarityModelScorerParams setMaxRareCount(Integer maxRareCount) {
            this.maxRareCount = maxRareCount;
            return this;
        }

        public Integer getMaxNumOfRareFeatures() {
            return maxNumOfRareFeatures;
        }

        public CategoryRarityModelScorerParams setMaxNumOfRareFeatures(Integer maxNumOfRareFeatures) {
            this.maxNumOfRareFeatures = maxNumOfRareFeatures;
            return this;
        }

        public Integer getMinimumNumberOfDistinctValuesToInfluence() {
            return minimumNumberOfDistinctValuesToInfluence;
        }

        public CategoryRarityModelScorerParams setMinimumNumberOfDistinctValuesToInfluence(Integer minimumNumberOfDistinctValuesToInfluence) {
            this.minimumNumberOfDistinctValuesToInfluence = minimumNumberOfDistinctValuesToInfluence;
            return this;
        }

        public Integer getEnoughNumberOfDistinctValuesToInfluence() {
            return enoughNumberOfDistinctValuesToInfluence;
        }

        public CategoryRarityModelScorerParams setEnoughNumberOfDistinctValuesToInfluence(Integer enoughNumberOfDistinctValuesToInfluence) {
            this.enoughNumberOfDistinctValuesToInfluence = enoughNumberOfDistinctValuesToInfluence;
            return this;
        }

        public Integer getNumberOfSamplesToInfluenceEnough() {
            return numberOfSamplesToInfluenceEnough;
        }

        public CategoryRarityModelScorerParams setNumberOfSamplesToInfluenceEnough(Integer numberOfSamplesToInfluenceEnough) {
            this.numberOfSamplesToInfluenceEnough = numberOfSamplesToInfluenceEnough;
            return this;
        }

        public Integer getMinNumOfSamplesToInfluence() {
            return minNumOfSamplesToInfluence;
        }

        public CategoryRarityModelScorerParams setMinNumOfSamplesToInfluence(Integer minNumOfSamplesToInfluence) {
            this.minNumOfSamplesToInfluence = minNumOfSamplesToInfluence;
            return this;
        }

        public Boolean getUseCertaintyToCalculateScore() {
            return useCertaintyToCalculateScore;
        }

        public CategoryRarityModelScorerParams setUseCertaintyToCalculateScore(Boolean useCertaintyToCalculateScore) {
            this.useCertaintyToCalculateScore = useCertaintyToCalculateScore;
            return this;
        }

        public String getModelName() {
            return modelName;
        }

        public CategoryRarityModelScorerParams setModelName(String modelName) {
            this.modelName = modelName;
            return this;
        }
    }
}
