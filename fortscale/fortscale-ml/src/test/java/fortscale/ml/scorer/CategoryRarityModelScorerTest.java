package fortscale.ml.scorer;

import fortscale.common.event.EventMessage;
import fortscale.common.feature.Feature;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.common.util.GenericHistogram;
import fortscale.ml.model.CategoryRarityModelWithFeatureOccurrencesData;
import fortscale.ml.model.Model;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.ml.model.builder.CategoryRarityModelWithFeatureOccurrencesDataBuilder;
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
        Assert.assertEquals(params.getMinumumNumberOfDistinctValuesToInfluence(), scorer.getMinNumOfDistinctValuesToInfluence(), scorer.getMinNumOfDistinctValuesToInfluence());
        Assert.assertEquals((long) params.getEnoughtNumberOfDistinctValuesToInfluence(), scorer.getEnoughNumOfDistinctValuesToInfluence());
        Assert.assertEquals((long) params.getNumberOfSamplesToInfluenceEnough(), scorer.getEnoughNumOfSamplesToInfluence());
        Assert.assertEquals((long) params.getMinNumOfSamplesToInfluence(), scorer.getMinNumOfSamplesToInfluence());
        Assert.assertEquals((boolean) params.getUseCertaintyToCalculateScore(), scorer.isUseCertaintyToCalculateScore());

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
                params.getMinumumNumberOfDistinctValuesToInfluence(),
                params.getEnoughtNumberOfDistinctValuesToInfluence(),
                params.getMaxRareCount(),
                params.getMaxNumOfRareFeatures());
    }


    protected EventMessage buildEventMessage(String fieldName, Object fieldValue){
        JSONObject jsonObject = null;
        jsonObject = new JSONObject();
        jsonObject.put(fieldName, fieldValue);
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
    public void constructor_negative_maxRareCounte_Test() throws IOException{
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
    public void constructor_negative_minumumNumberOfDistinctValuesToInfluence_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setMinumumNumberOfDistinctValuesToInfluence(-1);
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        assertScorer(scorer, params);
    }


    @Test(expected = IllegalArgumentException.class)
    public void constructor_negative_enoughtNumberOfDistinctValuesToInfluence_Test() throws IOException{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams().setEnoughtNumberOfDistinctValuesToInfluence(-1);
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
        scorer.calculateScore(null, Collections.singletonList(new CategoryRarityModelWithFeatureOccurrencesData()), new Feature("source-machine", "host1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_null_feature_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(new CategoryRarityModelWithFeatureOccurrencesData(), Collections.emptyList(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_feature_with_null_name_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(new CategoryRarityModelWithFeatureOccurrencesData(), Collections.emptyList(), new Feature(null, "host1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_empty__feature_name_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(null, Collections.emptyList(), new Feature("", "host1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_blank__feature_name_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(null, Collections.emptyList(), new Feature("     ", "host1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_null__feature_value_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(null, Collections.emptyList(), new Feature("source-machine", (String)null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_empty__feature_value_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(null, Collections.emptyList(), new Feature("source-machine", ""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateScore_blank__feature_value_test() {
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams();
        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        scorer.calculateScore(null, Collections.emptyList(), new Feature("source-machine", "    "));
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
        CategoryRarityModelWithFeatureOccurrencesData model = (CategoryRarityModelWithFeatureOccurrencesData)new CategoryRarityModelWithFeatureOccurrencesDataBuilder(new CategoryRarityModelBuilderConf(100)).build(histogram);
        Feature featureWithCount100 = new Feature("feature-with-count-100", "feature-count-100");
        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1
        model.setFeatureCount(featureWithCount100, count);
        double score = scorer.calculateScore(model, Collections.emptyList(), featureWithCount100);
        Assert.assertEquals(0.0, score, 0.0);
        score = scorer.calculateScore(model, Collections.emptyList(), featureWithZeroCount);
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
        CategoryRarityModelWithFeatureOccurrencesData model = (CategoryRarityModelWithFeatureOccurrencesData)new CategoryRarityModelWithFeatureOccurrencesDataBuilder(new CategoryRarityModelBuilderConf(100)).build(histogram);
        Feature featureWithCount100 = new Feature("feature-with-count-100", "feature-count-100");
        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1
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
        CategoryRarityModelWithFeatureOccurrencesData model = (CategoryRarityModelWithFeatureOccurrencesData)new CategoryRarityModelWithFeatureOccurrencesDataBuilder(new CategoryRarityModelBuilderConf(100)).build(histogram);
        Feature featureWithCount100 = new Feature("feature-with-count-100", "feature-count-100");
        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1
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

        Feature featureWithCount100 = new Feature("feature-with-count-100", "feature-count-100");
        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1
        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, null, featureWithCount100, eventMessage);
        FeatureScore featureScore = scorer.calculateScore(eventMessage, 0L);
        Assert.assertEquals(params.getName(), featureScore.getName());
    }

    private void prepareMocks(AbstractModelScorer scorer, Model model, Feature feature, EventMessage eventMessage) {
        HashMap<String, Feature> context = new HashMap<>();
        context.put("dummy", new Feature("dummy", "dummy"));

        when(modelsCacheService.getModel(any(), any(), any(), any(Long.class) )).thenReturn(model);
        when(featureExtractService.extract(eq(scorer.getFeatureName()),eq(eventMessage))).thenReturn(feature);
        when(featureExtractService.extract(any(Set.class), eq(eventMessage))).thenReturn(context);

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
                when(featureExtractService.extract(scorer.getFeatureName(),eventMessage)).thenReturn(featureWithZeroCount);
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
        CategoryRarityModelWithFeatureOccurrencesData model = (CategoryRarityModelWithFeatureOccurrencesData)new CategoryRarityModelWithFeatureOccurrencesDataBuilder(new CategoryRarityModelBuilderConf(100)).build(histogram);
        Feature featureWithCount100 = new Feature("feature-with-count-100", "feature-count-100");
        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1
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
        Feature featureWithCount100 = new Feature("feature-with-count-100", "feature-count-100");
        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1
        CategoryRarityModelWithFeatureOccurrencesData model = createModel(100, count, featureWithCount100);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, null);
        FeatureScore featureScore = scorer.calculateScore(eventMessage, 0L);
        Assert.assertEquals(0.0, featureScore.getScore(), 0.0); // With the right context it should return 100
    }

    private CategoryRarityModelWithFeatureOccurrencesData createModel(int numOfDistinctValues, long count, Feature feature) {
        Map<String, Long> featureValueToCountMap = new HashMap<>();
        for (int i = 0; i < numOfDistinctValues; i++) {
            featureValueToCountMap.put(String.format("test%d", i), count);
        }
        GenericHistogram histogram = new GenericHistogram();
        featureValueToCountMap.entrySet().forEach(entry -> histogram.add(entry.getKey(), entry.getValue().doubleValue()));
        CategoryRarityModelWithFeatureOccurrencesData model = (CategoryRarityModelWithFeatureOccurrencesData)new CategoryRarityModelWithFeatureOccurrencesDataBuilder(new CategoryRarityModelBuilderConf(100)).build(histogram);
        model.setFeatureCount(feature, count);
        return model;
    }

    //==================================================================================================================
    // Certainty Tests
    //==================================================================================================================


    @Test
    public void testScoreAndCertaintyOfNumOfFeatureValuesBelowMin() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams()
                .setMinumumNumberOfDistinctValuesToInfluence(20)
                .setEnoughtNumberOfDistinctValuesToInfluence(100)
                .setUseCertaintyToCalculateScore(false);

        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        long count = 100;
        Feature featureWithCount0 = new Feature("feature-with-count-0", "feature-count-0");
        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1
        CategoryRarityModelWithFeatureOccurrencesData model = createModel(19, count, featureWithCount0);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);

        FeatureScore score = scorer.calculateScore(eventMessage, 0l);

        Assert.assertNotNull(score);
        Assert.assertEquals(100d, score.getScore(), 0.0);
        Assert.assertEquals(0d, score.getCertainty(), 0.0);
    }

    @Test
    public void testScoreAndCertaintyOfNumOfFeatureValuesBelowMinAndMinGreaterThanEnough() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams()
                .setMinumumNumberOfDistinctValuesToInfluence(20)
                .setEnoughtNumberOfDistinctValuesToInfluence(10)
                .setUseCertaintyToCalculateScore(false);

        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        long count = 100;
        Feature featureWithCount0 = new Feature("feature-with-count-0", "feature-count-0");
        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1
        CategoryRarityModelWithFeatureOccurrencesData model = createModel(19, count, featureWithCount0);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);

        FeatureScore score = scorer.calculateScore(eventMessage, 0l);

        Assert.assertNotNull(score);
        Assert.assertEquals(100d, score.getScore(), 0.0);
        Assert.assertEquals(0d, score.getCertainty(), 0.0);

    }

    @Test
    public void testScoreAndCertaintyOfNumOfFeatureValuesEqualsToEnough() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams()
                .setMinumumNumberOfDistinctValuesToInfluence(20)
                .setEnoughtNumberOfDistinctValuesToInfluence(100)
                .setUseCertaintyToCalculateScore(false);

        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        long count = 100;
        Feature featureWithCount0 = new Feature("feature-with-count-0", "feature-count-0");
        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1
        CategoryRarityModelWithFeatureOccurrencesData model = createModel(100, count, featureWithCount0);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);

        FeatureScore score = scorer.calculateScore(eventMessage, 0l);

        Assert.assertNotNull(score);
        Assert.assertEquals(100d, score.getScore(), 0.0);
        Assert.assertEquals(1d, score.getCertainty(), 0.0);

    }

    @Test
    public void testScoreAndCertaintyOfNumOfFeatureValuesEqualsToEnoughAndMinGreaterThanEnough() throws Exception{
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams()
                .setMinumumNumberOfDistinctValuesToInfluence(200)
                .setEnoughtNumberOfDistinctValuesToInfluence(100)
                .setUseCertaintyToCalculateScore(false);

        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        long count = 100;
        Feature featureWithCount0 = new Feature("feature-with-count-0", "feature-count-0");
        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1
        CategoryRarityModelWithFeatureOccurrencesData model = createModel(100, count, featureWithCount0);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);

        FeatureScore score = scorer.calculateScore(eventMessage, 0l);

        Assert.assertNotNull(score);
        Assert.assertEquals(100d, score.getScore(), 0.0);
        Assert.assertEquals(0d, score.getCertainty(), 0.0);

    }

    @Test
    public void testScoreAndCertaintyOfNumOfFeatureValuesEqualsToMin() throws Exception{
        int min = 20;
        int enough = 100;
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams()
                .setMinumumNumberOfDistinctValuesToInfluence(min)
                .setEnoughtNumberOfDistinctValuesToInfluence(enough)
                .setUseCertaintyToCalculateScore(false);

        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        long count = 100;
        Feature featureWithCount0 = new Feature("feature-with-count-0", "feature-count-0");
        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1
        CategoryRarityModelWithFeatureOccurrencesData model = createModel(min, count, featureWithCount0);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);

        FeatureScore score = scorer.calculateScore(eventMessage, 0l);
        double expectedCertainty = 1d/(enough-min+1);
        Assert.assertNotNull(score);
        Assert.assertEquals(100d, score.getScore(), 0.0);
        Assert.assertEquals(expectedCertainty, score.getCertainty(), 0.0);
    }

    @Test
    public void testScoreAndCertaintyOfNumOfFeatureValuesEqualsToMinAndMinGreaterThanEnough() throws Exception{
        int min = 200;
        int enough = 100;
        CategoryRarityModelScorerParams params = new CategoryRarityModelScorerParams()
                .setMinumumNumberOfDistinctValuesToInfluence(min)
                .setEnoughtNumberOfDistinctValuesToInfluence(enough)
                .setUseCertaintyToCalculateScore(false);

        CategoryRarityModelScorer scorer = createCategoryRarityModelScorer(params);
        long count = 100;
        Feature featureWithCount0 = new Feature("feature-with-count-0", "feature-count-0");
        Feature featureWithZeroCount = new Feature("feature-with-zero-count", "feature-zero-count"); // The scorer should handle it as if count=1
        CategoryRarityModelWithFeatureOccurrencesData model = createModel(min, count, featureWithCount0);

        EventMessage eventMessage = buildEventMessage("dummy", "dummy"); // Anyhow the extracted value are mocked

        prepareMocks(scorer, model, featureWithZeroCount, eventMessage);

        FeatureScore score = scorer.calculateScore(eventMessage, 0l);
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
        Integer minumumNumberOfDistinctValuesToInfluence = 3;
        Integer enoughtNumberOfDistinctValuesToInfluence = 10;
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

        public CategoryRarityModelScorerParams setFeatureName(String featureName) {
            this.featureName = featureName;
            return this;
        }

        public List<String> getContextFieldNames() {
            return contextFieldNames;
        }

        public CategoryRarityModelScorerParams setContextFieldNames(List<String> contextFieldNames) {
            this.contextFieldNames = contextFieldNames;
            return this;
        }

        public CategoryRarityModelScorerParams addContextFieldName(String contextFieldName) {
            this.contextFieldNames.add(contextFieldName);
            return this;
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

        public Integer getMinumumNumberOfDistinctValuesToInfluence() {
            return minumumNumberOfDistinctValuesToInfluence;
        }

        public CategoryRarityModelScorerParams setMinumumNumberOfDistinctValuesToInfluence(Integer minumumNumberOfDistinctValuesToInfluence) {
            this.minumumNumberOfDistinctValuesToInfluence = minumumNumberOfDistinctValuesToInfluence;
            return this;
        }

        public Integer getEnoughtNumberOfDistinctValuesToInfluence() {
            return enoughtNumberOfDistinctValuesToInfluence;
        }

        public CategoryRarityModelScorerParams setEnoughtNumberOfDistinctValuesToInfluence(Integer enoughtNumberOfDistinctValuesToInfluence) {
            this.enoughtNumberOfDistinctValuesToInfluence = enoughtNumberOfDistinctValuesToInfluence;
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
