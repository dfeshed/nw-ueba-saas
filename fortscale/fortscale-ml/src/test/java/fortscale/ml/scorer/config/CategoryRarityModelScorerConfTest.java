package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CategoryRarityModelScorerConfTest {

    static String buildCategoryRarityModelScorerConfJsonString(CategoryRarityModelScorerConfParams params) {
        return buildCategoryRarityModelScorerConfJsonString(params.getName(),
                params.getMaxRareCount(), params.getMaxNumOfRareFeatures(), params.getMinumumNumberOfDistinctValuesToInfluence(),
                params.getEnoughtNumberOfDistinctValuesToInfluence(), params.getNumberOfSamplesToInfluenceEnough(), params.getMinNumOfSamplesToInfluence(),
                params.getUseCertaintyToCalculateScore(), params.getModelName(), params.getAdditionalModelNames());
    }

    static String buildCategoryRarityModelScorerConfJsonString(String name, Integer maxRareCount, Integer maxNumOfRareFeatures,
                                                               Integer minimumNumberOfDistinctValuesToInfluence,
                                                               Integer enoughNumberOfDistinctValuesToInfluence,
                                                               Integer numberOfSamplesToInfluenceEnough,
                                                               Integer minNumberOfSamplesToInfluence,
                                                               Boolean useCertaintyToCalculateScore,
                                                               String modelName,
                                                               List<String> additionalModelNames) {

        String jName = name==null? null : String.format("\"name\":\"%s\"", name);
        String jType = "\"type\":\"category-rarity-model-scorer\"";
        String jMaxRareCount = maxRareCount==null ? null : String.format(" \"max-rare-count\":%d", maxRareCount);
        String jMaxNumOfRareFeatures = maxNumOfRareFeatures==null ? null : String.format("\"max-num-of-rare-features\":%d", maxNumOfRareFeatures);
        String jMinumumNumberOfDistinctValuesToInfluence = minimumNumberOfDistinctValuesToInfluence==null ? null : String.format("\"minimum-number-of-distinct-values-to-influence\":%d", minimumNumberOfDistinctValuesToInfluence);
        String jEnoughtNumberOfDistinctValuesToInfluence = enoughNumberOfDistinctValuesToInfluence==null ? null : String.format("\"enough-number-of-distinct-values-to-influence\":%d", enoughNumberOfDistinctValuesToInfluence);
        String jNumberOfSamplesToInfluenceEnough = numberOfSamplesToInfluenceEnough==null ? null : String.format(" \"number-of-samples-to-influence-enough\":%d", numberOfSamplesToInfluenceEnough);
        String jMinNumberOfSamplesToInfluence = minNumberOfSamplesToInfluence == null ? null : String.format("\"min-number-of-samples-to-influence\":%d", minNumberOfSamplesToInfluence);
        String jUseCertaintyToCalculateScore = useCertaintyToCalculateScore==null ? null : String.format("\"use-certainty-to-calculate-score\":%s", useCertaintyToCalculateScore);
        Function<String, String> modelNameToJSON = additionalModelName -> String.format("{\"name\":\"%s\"}", additionalModelName);
        String jModelName = modelName==null ? null : String.format("\"model\":%s", modelNameToJSON.apply(modelName));
        String jAdditionalModelNames = additionalModelNames==null ? null : String.format("\"additional-models\":[%s]",
                String.join(",", additionalModelNames.stream()
                        .map(modelNameToJSON)
                        .collect(Collectors.toList())));

        // Building the json string and making sure that there is no redundant comma.
        String s = jModelName==null ? null : jModelName;
        s = jAdditionalModelNames!=null ? (s!=null?jAdditionalModelNames+", "+s:jAdditionalModelNames) : s;
        s = jUseCertaintyToCalculateScore!=null ? (s!=null?jUseCertaintyToCalculateScore+", "+s:jUseCertaintyToCalculateScore) : s;
        s = jMinNumberOfSamplesToInfluence!=null ? (s!=null?jMinNumberOfSamplesToInfluence+", "+s:jMinNumberOfSamplesToInfluence) : s;
        s = jNumberOfSamplesToInfluenceEnough!=null ? (s!=null?jNumberOfSamplesToInfluenceEnough+", "+s:jNumberOfSamplesToInfluenceEnough) : s;
        s = jEnoughtNumberOfDistinctValuesToInfluence!=null ? (s!=null?jEnoughtNumberOfDistinctValuesToInfluence+", "+s:jEnoughtNumberOfDistinctValuesToInfluence) : s;
        s = jMinumumNumberOfDistinctValuesToInfluence!=null ? (s!=null?jMinumumNumberOfDistinctValuesToInfluence+", "+s:jMinumumNumberOfDistinctValuesToInfluence) : s;
        s = jMaxNumOfRareFeatures!=null ? (s!=null?jMaxNumOfRareFeatures+", "+s:jMaxNumOfRareFeatures) : s;
        s = jMaxRareCount!=null ? (s!=null?jMaxRareCount+", "+s:jMaxRareCount) : s;
        s = jType!=null ? (s!=null?jType+", "+s:jType) : s;
        s = jName!=null ? (s!=null?jName+", "+s:jName) : s;
        String scorerConfJsonString = "{" + s + "}";

        return scorerConfJsonString;

    }

    static void assertConf(CategoryRarityModelScorerConf conf, CategoryRarityModelScorerConfParams params) {
        assertConf(conf,
                params.getName(),
                params.getMaxRareCount(),
                params.getMaxNumOfRareFeatures(),
                params.getMinumumNumberOfDistinctValuesToInfluence(),
                params.getEnoughtNumberOfDistinctValuesToInfluence(),
                params.getNumberOfSamplesToInfluenceEnough(),
                params.getMinNumOfSamplesToInfluence(),
                params.getUseCertaintyToCalculateScore(),
                params.getModelName());
    }

    static void assertConf(CategoryRarityModelScorerConf conf,
                            String name, Integer maxRareCount, Integer maxNumOfRareFeatures,
                            Integer minumumNumberOfDistinctValuesToInfluence,
                            Integer enoughtNumberOfDistinctValuesToInfluence,
                            Integer numberOfSamplesToInfluenceEnough,
                            Integer minNumberOfSamplesToInfluence,
                            Boolean useCertaintyToCalculateScore,
                            String modelName) {

        Assert.assertEquals(name, conf.getName());
        Assert.assertEquals((long)maxRareCount, conf.getMaxRareCount());
        Assert.assertEquals((long)maxNumOfRareFeatures, conf.getMaxNumOfRareFeatures());

        if(minumumNumberOfDistinctValuesToInfluence==null) {
            Assert.assertEquals(0L, conf.getMinNumOfDistinctValuesToInfluence());
        } else {
            Assert.assertEquals((long) minumumNumberOfDistinctValuesToInfluence, conf.getMinNumOfDistinctValuesToInfluence());
        }
        if(enoughtNumberOfDistinctValuesToInfluence==null) {
            Assert.assertEquals(0L, conf.getEnoughNumOfDistinctValuesToInfluence());
        } else {
            Assert.assertEquals((long) enoughtNumberOfDistinctValuesToInfluence, conf.getEnoughNumOfDistinctValuesToInfluence());
        }

        if(numberOfSamplesToInfluenceEnough==null) {
            Assert.assertEquals(ModelScorerConf.ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE, conf.getEnoughNumOfSamplesToInfluence());
        } else {
            Assert.assertEquals((long) numberOfSamplesToInfluenceEnough, conf.getEnoughNumOfSamplesToInfluence());
        }
        if(minNumberOfSamplesToInfluence==null) {
            Assert.assertEquals(ModelScorerConf.MIN_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE, conf.getMinNumOfSamplesToInfluence());
        } else {
            Assert.assertEquals((long) minNumberOfSamplesToInfluence, conf.getMinNumOfSamplesToInfluence());
        }
        if(useCertaintyToCalculateScore==null) {
            Assert.assertEquals(ModelScorerConf.IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEFAULT_VALUE, conf.isUseCertaintyToCalculateScore());
        } else {
            Assert.assertEquals(useCertaintyToCalculateScore, conf.isUseCertaintyToCalculateScore());
        }
        Assert.assertEquals(modelName, conf.getModelInfo().getModelName());
    }

    private void doDeserialization(CategoryRarityModelScorerConfParams params, boolean doAssert) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String scorerConfJson = buildCategoryRarityModelScorerConfJsonString(params);
        CategoryRarityModelScorerConf conf = objectMapper.readValue(scorerConfJson, CategoryRarityModelScorerConf.class);
        if(doAssert) {assertConf(conf, params);}
    }

    @Test
    public void jsonDeserialization_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams();
        doDeserialization(params, true);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_Null_name_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setName(null);
        doDeserialization(params, false);
   }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_Empty_name_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setName("");
        doDeserialization(params, false);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_Blank_name_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setName(" ");
        doDeserialization(params, false);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_Null_maxRareCounte_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setMaxRareCount(null);
        doDeserialization(params, false);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_negative_maxRareCounte_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setMaxRareCount(-1);
        doDeserialization(params, false);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_Null_maxNumOfRareFeatures_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setMaxNumOfRareFeatures(null);
        doDeserialization(params, false);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_negative_maxNumOfRareFeatures_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setMaxNumOfRareFeatures(-1);
        doDeserialization(params, false);
    }

    @Test
    public void jsonDeserializatio_Null_minumumNumberOfDistinctValuesToInfluence_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setMinumumNumberOfDistinctValuesToInfluence(null);
        doDeserialization(params, true);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserializatio_negative_minumumNumberOfDistinctValuesToInfluence_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setMinumumNumberOfDistinctValuesToInfluence(-1);
        doDeserialization(params, false);
    }

    @Test
    public void jsonDeserializatio_Null_enoughtNumberOfDistinctValuesToInfluence_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setEnoughtNumberOfDistinctValuesToInfluence(null);
        doDeserialization(params, true);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserializatio_negative_enoughtNumberOfDistinctValuesToInfluence_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setEnoughtNumberOfDistinctValuesToInfluence(-1);
        doDeserialization(params, false);
    }

    @Test
    public void jsonDeserialization_Null_numberOfSamplesToInfluenceEnough_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setNumberOfSamplesToInfluenceEnough(null);
        doDeserialization(params, true);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_zero_numberOfSamplesToInfluenceEnough_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setNumberOfSamplesToInfluenceEnough(0);
        doDeserialization(params, false);
    }

    @Test
    public void jsonDeserialization_Null_minNumOfSamplesToInfluence_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setMinumumNumberOfDistinctValuesToInfluence(null);
        doDeserialization(params, true);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_zero_minNumOfSamplesToInfluence_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setMinNumOfSamplesToInfluence(0);
        doDeserialization(params, false);
    }

    @Test
    public void jsonDeserialization_Null_useCertaintyToCalculateScore_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setUseCertaintyToCalculateScore(null);
        doDeserialization(params, true);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_Null_modelName_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setModelName(null);
        doDeserialization(params, false);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_empty_modelName_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setModelName("");
        doDeserialization(params, false);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_blank_modelName_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setModelName(" ");
        doDeserialization(params, false);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_empty_additionalModelName_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setAdditionalModelNames(Collections.singletonList(""));
        doDeserialization(params, false);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_blank_additionalModelName_Test() throws IOException{
        CategoryRarityModelScorerConfParams params = new CategoryRarityModelScorerConfParams().setAdditionalModelNames(Collections.singletonList(" "));
        doDeserialization(params, false);
    }

    /**
     * CategoryRarityModelScorer params to ease the testing.
     * The default parameters here are intentionally different from the defaults in the conf itself.
     * Use the setters to override the specific parameter you want to test.
     */
    static class CategoryRarityModelScorerConfParams {
        String name = "Scorer1";
        Integer maxRareCount = 10;
        Integer maxNumOfRareFeatures = 6;
        Integer minumumNumberOfDistinctValuesToInfluence = 3;
        Integer enoughtNumberOfDistinctValuesToInfluence = 10;
        Integer numberOfSamplesToInfluenceEnough = 10;
        Integer minNumOfSamplesToInfluence = 2;
        Boolean useCertaintyToCalculateScore = true;
        String modelName = "model1";
        List<String> additionalModelNames = Collections.emptyList();

        public String getName() {
            return name;
        }

        public CategoryRarityModelScorerConfParams setName(String name) {
            this.name = name;
            return this;
        }

        public Integer getMaxRareCount() {
            return maxRareCount;
        }

        public CategoryRarityModelScorerConfParams setMaxRareCount(Integer maxRareCount) {
            this.maxRareCount = maxRareCount;
            return this;
        }

        public Integer getMaxNumOfRareFeatures() {
            return maxNumOfRareFeatures;
        }

        public CategoryRarityModelScorerConfParams setMaxNumOfRareFeatures(Integer maxNumOfRareFeatures) {
            this.maxNumOfRareFeatures = maxNumOfRareFeatures;
            return this;
        }

        public Integer getMinumumNumberOfDistinctValuesToInfluence() {
            return minumumNumberOfDistinctValuesToInfluence;
        }

        public CategoryRarityModelScorerConfParams setMinumumNumberOfDistinctValuesToInfluence(Integer minumumNumberOfDistinctValuesToInfluence) {
            this.minumumNumberOfDistinctValuesToInfluence = minumumNumberOfDistinctValuesToInfluence;
            return this;
        }

        public Integer getEnoughtNumberOfDistinctValuesToInfluence() {
            return enoughtNumberOfDistinctValuesToInfluence;
        }

        public CategoryRarityModelScorerConfParams setEnoughtNumberOfDistinctValuesToInfluence(Integer enoughtNumberOfDistinctValuesToInfluence) {
            this.enoughtNumberOfDistinctValuesToInfluence = enoughtNumberOfDistinctValuesToInfluence;
            return this;
        }

        public Integer getNumberOfSamplesToInfluenceEnough() {
            return numberOfSamplesToInfluenceEnough;
        }

        public CategoryRarityModelScorerConfParams setNumberOfSamplesToInfluenceEnough(Integer numberOfSamplesToInfluenceEnough) {
            this.numberOfSamplesToInfluenceEnough = numberOfSamplesToInfluenceEnough;
            return this;
        }

        public Integer getMinNumOfSamplesToInfluence() {
            return minNumOfSamplesToInfluence;
        }

        public CategoryRarityModelScorerConfParams setMinNumOfSamplesToInfluence(Integer minNumOfSamplesToInfluence) {
            this.minNumOfSamplesToInfluence = minNumOfSamplesToInfluence;
            return this;
        }

        public Boolean getUseCertaintyToCalculateScore() {
            return useCertaintyToCalculateScore;
        }

        public CategoryRarityModelScorerConfParams setUseCertaintyToCalculateScore(Boolean useCertaintyToCalculateScore) {
            this.useCertaintyToCalculateScore = useCertaintyToCalculateScore;
            return this;
        }

        public String getModelName() {
            return modelName;
        }

        public CategoryRarityModelScorerConfParams setModelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public CategoryRarityModelScorerConfParams setAdditionalModelNames(List<String> additionalModelNames) {
            this.additionalModelNames = additionalModelNames;
            return this;
        }

        public List<String> getAdditionalModelNames() {
            return additionalModelNames;
        }
    }
}
