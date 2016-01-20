package fortscale.ml.scorer.config;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ModelScorerConfTest {

    static String buildModelScorerConfJsonString(ModelScoererParams params) {
        return buildModelScorerConfJsonString(params.getName(), params.getNumberOfSamplesToInfluenceEnought(), params.getMinNumOfSamplesToInfluence(),
                params.getUseCertaintyToCalculateScore(), params.getModelName());
    }
    
    static String buildModelScorerConfJsonString(String name,
                                                 Integer numberOfSamplesToInfluenceEnought,
                                                 Integer minNumberOfSamplesToInfluence,
                                                 Boolean useCertaintyToCalculateScore,
                                                 String modelName) {

        String jName = name==null? null : String.format("\"name\":\"%s\"", name);
        String jType = "\"type\":\"model-scorer\"";
        String jNumberOfSamplesToInfluenceEnought = numberOfSamplesToInfluenceEnought==null ? null : String.format(" \"number-of-samples-to-influence-enough\":%d", numberOfSamplesToInfluenceEnought);
        String jMinNumberOfSamplesToInfluence = minNumberOfSamplesToInfluence == null ? null : String.format("\"min-number-of-samples-to-influence\":%d", minNumberOfSamplesToInfluence);
        String jUseCertaintyToCalculateScore = useCertaintyToCalculateScore==null ? null : String.format("\"use-certainty-to-calculate-score\":%s", useCertaintyToCalculateScore);
        String jModelName = modelName==null ? null : String.format("\"model\":{\"name\":\"%s\"}", modelName);

        // Building the json string and making sure that there is no redundant comma.
        String s = jModelName==null ? null : jModelName;
        s = jUseCertaintyToCalculateScore!=null ? (s!=null?jUseCertaintyToCalculateScore+", "+s:jUseCertaintyToCalculateScore) : s;
        s = jMinNumberOfSamplesToInfluence!=null ? (s!=null?jMinNumberOfSamplesToInfluence+", "+s:jMinNumberOfSamplesToInfluence) : s;
        s = jNumberOfSamplesToInfluenceEnought!=null ? (s!=null?jNumberOfSamplesToInfluenceEnought+", "+s:jNumberOfSamplesToInfluenceEnought) : s;
        s = jType!=null ? (s!=null?jType+", "+s:jType) : s;
        s = jName!=null ? (s!=null?jName+", "+s:jName) : s;
        s = "{" + s + "}";

        return s;
    }

    private void assertConf(ModelScorerConf conf, ModelScoererParams params) {
        assertConf(conf, params.getName(), params.getNumberOfSamplesToInfluenceEnought(), params.getMinNumOfSamplesToInfluence(), 
                params.getUseCertaintyToCalculateScore(), params.getModelName());
    }
    
    private void assertConf(ModelScorerConf conf,
                            String name,
                            Integer numberOfSamplesToInfluenceEnought,
                            Integer minNumberOfSamplesToInfluence,
                            Boolean useCertaintyToCalculateScore,
                            String modelName) {

        Assert.assertEquals(name, conf.getName());

        if(numberOfSamplesToInfluenceEnought==null) {
            Assert.assertEquals(ModelScorerConf.ENOUGH_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE, conf.getEnoughNumOfSamplesToInfluence());
        } else {
            Assert.assertEquals((long) numberOfSamplesToInfluenceEnought, conf.getEnoughNumOfSamplesToInfluence());
        }

        if(minNumberOfSamplesToInfluence==null) {
            Assert.assertEquals(ModelScorerConf.MIN_NUM_OF_SAMPLES_TO_INFLUENCE_DEFAULT_VALUE, conf.getMinNumOfSamplesToInfluence());
        } else {
            Assert.assertEquals((long) minNumberOfSamplesToInfluence, conf.getMinNumOfSamplesToInfluence());
        }

        if(useCertaintyToCalculateScore==null) {
            Assert.assertEquals(ModelScorerConf.IS_USE_CERTAINTY_TO_CALCULATE_SCORE_DEAFEST_VALUE, conf.isUseCertaintyToCalculateScore());
        } else {
            Assert.assertEquals(useCertaintyToCalculateScore, conf.isUseCertaintyToCalculateScore());
        }

        Assert.assertEquals(modelName, conf.getModelInfo().getModelName());

    }

    @Test
    public void jsonDeserialization_Test() throws IOException {
        ModelScoererParams params = new ModelScoererParams();
        String scorerConfJson = buildModelScorerConfJsonString(params);
        System.out.println("json = "+scorerConfJson);
        ObjectMapper objectMapper = new ObjectMapper();
        ModelScorerConf conf = objectMapper.readValue(scorerConfJson, ModelScorerConf.class);
        assertConf(conf, params);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_Null_Name_Test() throws IOException{
        ModelScoererParams params = new ModelScoererParams();
        params.setName(null);
        String scorerConfJson = buildModelScorerConfJsonString(params);
        System.out.println("json = "+scorerConfJson);
        ObjectMapper objectMapper = new ObjectMapper();
        ModelScorerConf conf = objectMapper.readValue(scorerConfJson, ModelScorerConf.class);
        assertConf(conf, params);
   }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_Blank_Name_Test() throws IOException{
        ModelScoererParams params = new ModelScoererParams();
        params.setName(" ");
        String scorerConfJson = buildModelScorerConfJsonString(params);
        System.out.println("json = "+scorerConfJson);
        ObjectMapper objectMapper = new ObjectMapper();
        ModelScorerConf conf = objectMapper.readValue(scorerConfJson, ModelScorerConf.class);
        assertConf(conf, params);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_Empty_Name_Test() throws IOException{
        ModelScoererParams params = new ModelScoererParams();
        params.setName("");
        String scorerConfJson = buildModelScorerConfJsonString(params);
        System.out.println("json = "+scorerConfJson);
        ObjectMapper objectMapper = new ObjectMapper();
        ModelScorerConf conf = objectMapper.readValue(scorerConfJson, ModelScorerConf.class);
        assertConf(conf, params);
    }

    @Test
    public void jsonDeserialization_Null_NumberOfSamplesToInfluenceEnough_Test() throws IOException{
        ModelScoererParams params = new ModelScoererParams();
        params.setNumberOfSamplesToInfluenceEnought(null);
        String scorerConfJson = buildModelScorerConfJsonString(params);
        System.out.println("json = "+scorerConfJson);
        ObjectMapper objectMapper = new ObjectMapper();
        ModelScorerConf conf = objectMapper.readValue(scorerConfJson, ModelScorerConf.class);
        assertConf(conf, params);
    }

    @Test
    public void jsonDeserialization_Null_MinNumOfSamplesToInfluence_Test() throws IOException{
        ModelScoererParams params = new ModelScoererParams();
        params.setMinNumOfSamplesToInfluence(null);
        String scorerConfJson = buildModelScorerConfJsonString(params);
        System.out.println("json = "+scorerConfJson);
        ObjectMapper objectMapper = new ObjectMapper();
        ModelScorerConf conf = objectMapper.readValue(scorerConfJson, ModelScorerConf.class);
        assertConf(conf, params);
    }

    @Test
    public void jsonDeserialization_Null_UseCertaintyToCalculateScore_Test() throws IOException{
        ModelScoererParams params = new ModelScoererParams();
        params.setUseCertaintyToCalculateScore(null);
        String scorerConfJson = buildModelScorerConfJsonString(params);
        System.out.println("json = "+scorerConfJson);
        ObjectMapper objectMapper = new ObjectMapper();
        ModelScorerConf conf = objectMapper.readValue(scorerConfJson, ModelScorerConf.class);
        assertConf(conf, params);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_Null_ModelName_Test() throws IOException{
        ModelScoererParams params = new ModelScoererParams();
        params.setModelName(null);
        String scorerConfJson = buildModelScorerConfJsonString(params);
        System.out.println("json = "+scorerConfJson);
        ObjectMapper objectMapper = new ObjectMapper();
        ModelScorerConf conf = objectMapper.readValue(scorerConfJson, ModelScorerConf.class);
        assertConf(conf, params);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_Empty_ModelName_Test() throws IOException{
        ModelScoererParams params = new ModelScoererParams();
        params.setModelName("");
        String scorerConfJson = buildModelScorerConfJsonString(params);
        System.out.println("json = "+scorerConfJson);
        ObjectMapper objectMapper = new ObjectMapper();
        ModelScorerConf conf = objectMapper.readValue(scorerConfJson, ModelScorerConf.class);
        assertConf(conf, params);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_Blank_ModelName_Test() throws IOException{
        ModelScoererParams params = new ModelScoererParams();
        params.setModelName(" ");
        String scorerConfJson = buildModelScorerConfJsonString(params);
        System.out.println("json = "+scorerConfJson);
        ObjectMapper objectMapper = new ObjectMapper();
        ModelScorerConf conf = objectMapper.readValue(scorerConfJson, ModelScorerConf.class);
        assertConf(conf, params);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_0_numberOfSamplesToInfluenceEnought_Test() throws IOException{
        ModelScoererParams params = new ModelScoererParams();
        params.setNumberOfSamplesToInfluenceEnought(0);
        String scorerConfJson = buildModelScorerConfJsonString(params);
        System.out.println("json = "+scorerConfJson);
        ObjectMapper objectMapper = new ObjectMapper();
        ModelScorerConf conf = objectMapper.readValue(scorerConfJson, ModelScorerConf.class);
        assertConf(conf, params);
    }

    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_0_minNumOfSamplesToInfluence_Test() throws IOException{
        ModelScoererParams params = new ModelScoererParams();
        params.setMinNumOfSamplesToInfluence(0);
        String scorerConfJson = buildModelScorerConfJsonString(params);
        System.out.println("json = "+scorerConfJson);
        ObjectMapper objectMapper = new ObjectMapper();
        ModelScorerConf conf = objectMapper.readValue(scorerConfJson, ModelScorerConf.class);
        assertConf(conf, params);
    }



    static class ModelScoererParams {
        String name = "Scorer1";
        Integer numberOfSamplesToInfluenceEnought = 10;
        Integer minNumOfSamplesToInfluence = 2;
        Boolean useCertaintyToCalculateScore = true;
        String modelName = "model1";

        public String getName() {
            return name;
        }

        public ModelScoererParams setName(String name) {
            this.name = name;
            return this;
        }

        public Integer getNumberOfSamplesToInfluenceEnought() {
            return numberOfSamplesToInfluenceEnought;
        }

        public ModelScoererParams setNumberOfSamplesToInfluenceEnought(Integer numberOfSamplesToInfluenceEnought) {
            this.numberOfSamplesToInfluenceEnought = numberOfSamplesToInfluenceEnought;
            return this;
        }

        public Integer getMinNumOfSamplesToInfluence() {
            return minNumOfSamplesToInfluence;
        }

        public ModelScoererParams setMinNumOfSamplesToInfluence(Integer minNumOfSamplesToInfluence) {
            this.minNumOfSamplesToInfluence = minNumOfSamplesToInfluence;
            return this;
        }

        public Boolean getUseCertaintyToCalculateScore() {
            return useCertaintyToCalculateScore;
        }

        public ModelScoererParams setUseCertaintyToCalculateScore(Boolean useCertaintyToCalculateScore) {
            this.useCertaintyToCalculateScore = useCertaintyToCalculateScore;
            return this;
        }

        public String getModelName() {
            return modelName;
        }

        public ModelScoererParams setModelName(String modelName) {
            this.modelName = modelName;
            return this;
        }
    }

}
