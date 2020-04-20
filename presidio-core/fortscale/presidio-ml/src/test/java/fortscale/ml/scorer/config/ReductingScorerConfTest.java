package fortscale.ml.scorer.config;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.ml.scorer.ReductionScorer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ReductingScorerConfTest {

    String buildScorerConfJsonString(ReductionScorerConfParams params) {

        String jName = params.getName()==null? null : String.format("\"name\":\"%s\"", params.getName());
        String jType = "\"type\":\""+ReductionScorerConf.SCORER_TYPE+"\"";
        String jMainScorer = params.getMainScorerConfParams()==null ? null : String.format(" \"main-scorer\":%s", CategoryRarityModelScorerConfTest.buildCategoryRarityModelScorerConfJsonString(params.getMainScorerConfParams()));
        String jRedtionScorer = params.getReductionScorerConfParams()==null ? null : String.format(" \"reduction-scorer\":%s", CategoryRarityModelScorerConfTest.buildCategoryRarityModelScorerConfJsonString(params.getReductionScorerConfParams()));
        String jReductionWeight = params.getReductionWeight() == null ? null : String.format("\"reduction-weight\":%f", params.getReductionWeight());
        String jReductionZeroScoreWeight = params.getReductionZeroScoreWeight()==null ? null : String.format("\"reduction-zero-score-weight\":%f", params.getReductionZeroScoreWeight());

        // Building the json string and making sure that there is no redundant comma.
        String s = jMainScorer==null ? null : jMainScorer;
        s = jRedtionScorer!=null ? (s!=null?jRedtionScorer+", "+s:jRedtionScorer) : s;
        s = jReductionWeight!=null ? (s!=null?jReductionWeight+", "+s:jReductionWeight) : s;
        s = jReductionZeroScoreWeight!=null ? (s!=null?jReductionZeroScoreWeight+", "+s:jReductionZeroScoreWeight) : s;
        s = jType!=null ? (s!=null?jType+", "+s:jType) : s;
        s = jName!=null ? (s!=null?jName+", "+s:jName) : s;
        s = "{" + s + "}";

        return s;
    }

    private void assertConf(ReductionScorerConf conf,
                            ReductionScorerConfParams params) {

        double diff = 0.000000001;
        Assert.assertEquals(params.getName(), conf.getName());
        Assert.assertEquals(params.getReductionWeight(), conf.getReductionWeight(), diff);
        CategoryRarityModelScorerConfTest.assertConf((CategoryRarityModelScorerConf) conf.getMainScorerConf(), params.getMainScorerConfParams());
        CategoryRarityModelScorerConfTest.assertConf((CategoryRarityModelScorerConf) conf.getReductionScorerConf(), params.getReductionScorerConfParams());

        if (params.getReductionZeroScoreWeight() == null) {
            Assert.assertEquals(ReductionScorer.REDUCTION_ZERO_SCORE_WEIGHT_DEFAULT, conf.getReductionZeroScoreWeight(), diff);
        } else {
            Assert.assertEquals(params.getReductionZeroScoreWeight(), conf.getReductionZeroScoreWeight(), diff);
        }
    }

    private void doDeserialization(ReductionScorerConfParams params, boolean doAssert) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String scorerConfJson = buildScorerConfJsonString(params);
        ReductionScorerConf conf = objectMapper.readValue(scorerConfJson, ReductionScorerConf.class);
        if(doAssert) {assertConf(conf, params);}
    }

    /**=================================================================================================================
     *                                                  TESTS
     **=================================================================================================================*/

    @Test
    public void jsonDeserialization_Test() throws IOException {
        ReductionScorerConfParams params = new ReductionScorerConfParams();
        doDeserialization(params, true);
    }
    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_null_name_Test() throws IOException {
        ReductionScorerConfParams params = new ReductionScorerConfParams().setName(null);
        doDeserialization(params, false);
    }
    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_empty_name_Test() throws IOException {
        ReductionScorerConfParams params = new ReductionScorerConfParams().setName("");
        doDeserialization(params, false);
    }
    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_blank_name_Test() throws IOException {
        ReductionScorerConfParams params = new ReductionScorerConfParams().setName("   ");
        doDeserialization(params, false);
    }
    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_null_mainScorerConf_Test() throws IOException {
        ReductionScorerConfParams params = new ReductionScorerConfParams().setMainScorerConfParams(null);
        doDeserialization(params, false);
    }
    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_null_reductionScorerConf_Test() throws IOException {
        ReductionScorerConfParams params = new ReductionScorerConfParams().setReductionScorerConfParams(null);
        doDeserialization(params, false);
    }
    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_null_reductionWeight_Test() throws IOException {
        ReductionScorerConfParams params = new ReductionScorerConfParams().setReductionWeight(null);
        doDeserialization(params, false);
    }
    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_negative_reductionWeight_Test() throws IOException {
        ReductionScorerConfParams params = new ReductionScorerConfParams().setReductionWeight(-1.0);
        doDeserialization(params, false);
    }
    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_equal_to_1_reductionWeight_Test() throws IOException {
        ReductionScorerConfParams params = new ReductionScorerConfParams().setReductionWeight(1.0);
        doDeserialization(params, false);
    }
    @Test(expected = JsonMappingException.class)
    public void jsonDeserialization_less_then_1_reductionWeight_Test() throws IOException {
        ReductionScorerConfParams params = new ReductionScorerConfParams().setReductionWeight(0.99999999);
        doDeserialization(params, false);
    }
    @Test
    public void jsonDeserialization_null_zeroWeight_Test() throws IOException {
        ReductionScorerConfParams params = new ReductionScorerConfParams().setReductionZeroScoreWeight(null);
        doDeserialization(params, true);
    }

}
