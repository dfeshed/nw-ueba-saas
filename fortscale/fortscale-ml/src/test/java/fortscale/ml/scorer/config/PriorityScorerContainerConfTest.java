package fortscale.ml.scorer.config;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.ml.scorer.params.ConstantRegexScorerParams;
import fortscale.ml.scorer.params.PriorityScorerParams;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class PriorityScorerContainerConfTest {

    static List<IScorerConf> scorerConfList = new ArrayList<>();
    static {
        scorerConfList.add(new IScorerConf() {
            @Override
            public String getName() {
                return "dummyScorer";
            }

            @Override
            public String getFactoryName() {
                return "dummyScorerFactoryName";
            }
        });
    }

    private void doDeserialization(PriorityScorerParams params) throws Exception {
        doDeserialization(params, null, null);
    }

    private void doDeserialization(PriorityScorerParams params, String expectExceptionClassName, String expectedErrorMessage) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String confJsonStr = params.getScorerConfJsonString();
        System.out.println(confJsonStr);
        try {
            PriorityScorerContainerConf conf = objectMapper.readValue(confJsonStr, PriorityScorerContainerConf.class);
            assertConf(conf, params);
        } catch (Exception ex) {
            if(expectExceptionClassName!=null) {
                Assert.assertTrue(ex instanceof JsonMappingException);
                Throwable throwable = ex.getCause();
                Assert.assertTrue(Class.forName(expectExceptionClassName).isInstance(throwable));
                if(expectedErrorMessage!=null) {
                    Assert.assertEquals(expectedErrorMessage, throwable.getMessage());
                }
                throw (IllegalArgumentException) throwable;
            } else {
                throw ex;
            }
        }
    }


    private void assertConf(PriorityScorerContainerConf conf, PriorityScorerParams params) {
        Assert.assertEquals(params.getName(), conf.getName());
        Assert.assertEquals(params.getScorerParamsList().size(), conf.getScorerConfList().size());
        for(int i = 0; i<params.getScorerParamsList().size(); i++) {
            IScorerConf scorerConfFromParams = params.getScorerParamsList().get(i).getScorerConf();
            IScorerConf simpleTestScorerConf = conf.getScorerConfList().get(i);
            Assert.assertEquals(scorerConfFromParams, simpleTestScorerConf);
        }
    }


    @Test
    public void deserialization_with_one_scorer_test() throws Exception{
        PriorityScorerParams params = new PriorityScorerParams().addScorerParams(new ConstantRegexScorerParams().setName("Scorer1").setConstantScore(69));
        doDeserialization(params);
    }

    @Test
    public void deserialization_with_two_scorers_test()throws Exception{
        PriorityScorerParams params = new PriorityScorerParams().addScorerParams(new  ConstantRegexScorerParams().setName("Scorer1").setConstantScore(69))
                .addScorerParams(new  ConstantRegexScorerParams().setName("Scorer2").setConstantScore(70));
        doDeserialization(params);
    }

    @Test
    public void deserialization_with_three_scorers_test() throws Exception{
        PriorityScorerParams params = new PriorityScorerParams().addScorerParams(new  ConstantRegexScorerParams().setName("Scorer1").setConstantScore(69))
                .addScorerParams(new ConstantRegexScorerParams().setName("Scorer2").setConstantScore(70))
                .addScorerParams(new ConstantRegexScorerParams().setName("Scorer3").setConstantScore(72));
        doDeserialization(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialization_with_no_scorers_test() throws Exception{
        PriorityScorerParams params = new PriorityScorerParams();
        doDeserialization(params, "java.lang.IllegalArgumentException", PriorityScorerContainerConf.NULL_SCORER_CONF_LIST_ERROR_MSG);

    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialization_with_null_name_test() throws Exception{
        PriorityScorerParams params = new PriorityScorerParams()
                .addScorerParams(new  ConstantRegexScorerParams().setName("Scorer1").setConstantScore(69))
                .setName(null);
        doDeserialization(params, "java.lang.IllegalArgumentException", AbstractScorerConf.NULL_OR_EMPTY_NAME_ERROR_MSG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialization_with_empty_name_test() throws Exception {
        PriorityScorerParams params = new PriorityScorerParams()
                .addScorerParams(new  ConstantRegexScorerParams().setName("Scorer1").setConstantScore(69))
                .setName("");
        doDeserialization(params, "java.lang.IllegalArgumentException", AbstractScorerConf.NULL_OR_EMPTY_NAME_ERROR_MSG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialization_with_blank_name_test() throws Exception {
        PriorityScorerParams params = new PriorityScorerParams()
                .addScorerParams(new  ConstantRegexScorerParams().setName("Scorer1").setConstantScore(69))
                .setName(" ");
        doDeserialization(params, "java.lang.IllegalArgumentException", AbstractScorerConf.NULL_OR_EMPTY_NAME_ERROR_MSG);
    }

    @Test
    public void constructor_good_test() {
        new PriorityScorerContainerConf("name", scorerConfList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_name_test() {
        new PriorityScorerContainerConf(null, scorerConfList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_name_test() {
        new PriorityScorerContainerConf("", scorerConfList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_blank_name_test() {
        new PriorityScorerContainerConf("  ", scorerConfList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_scorerConfList_test() {
        new PriorityScorerContainerConf("name", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_scorerConfList_test() {
        new PriorityScorerContainerConf("name", new ArrayList<>());
    }

 }
