package fortscale.ml.scorer.config;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.ml.scorer.params.ConstantRegexScorerParams;
import fortscale.ml.scorer.params.FieldValueScoreReducerScorerConfParams;
import org.junit.Assert;
import org.junit.Test;

public class FieldValueScoreReducerScorerConfTest {


    private void assertConf(FieldValueScoreReducerScorerConf conf,
                            FieldValueScoreReducerScorerConfParams params) {
        Assert.assertEquals(params.getName(), conf.getName());
        Assert.assertEquals(params.getLimiters(), conf.getLimiters());
        ConstantRegexScorerConfTest.assertConf((ConstantRegexScorerConf) conf.getBaseScorerConf(), (ConstantRegexScorerParams) params.getBaseScorerParams());
    }
    private void doDeserialization(FieldValueScoreReducerScorerConfParams params) throws Exception {
        doDeserialization(params, null, null);
    }

    private void doDeserialization(FieldValueScoreReducerScorerConfParams params, String expectExceptionClassName, String expectedErrorMessage) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String confJsonStr = params.getScorerConfJsonString();
        System.out.println(confJsonStr);
        try {
            FieldValueScoreReducerScorerConf conf = objectMapper.readValue(confJsonStr, FieldValueScoreReducerScorerConf.class);
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


    /**=================================================================================================================
     *                                                  TESTS
     **=================================================================================================================*/

    @Test
    public void jsonDeserialization_Test() throws Exception {
        FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams();
        doDeserialization(params);
    }

    
    @Test(expected = IllegalArgumentException.class)
    public void jsonDeserialization_null_name_Test() throws Exception {
        FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams().setName(null);
        doDeserialization(params,"java.lang.IllegalArgumentException", FieldValueScoreReducerScorerConf.NULL_OR_EMPTY_NAME_ERROR_MSG);
    }
    @Test(expected = IllegalArgumentException.class)
    public void jsonDeserialization_empty_name_Test() throws Exception {
        FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams().setName("");
        doDeserialization(params,"java.lang.IllegalArgumentException", FieldValueScoreReducerScorerConf.NULL_OR_EMPTY_NAME_ERROR_MSG);
    }
    @Test(expected = IllegalArgumentException.class)
    public void jsonDeserialization_blank_name_Test() throws Exception {
        FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams().setName("   ");
        doDeserialization(params, "java.lang.IllegalArgumentException", FieldValueScoreReducerScorerConf.NULL_OR_EMPTY_NAME_ERROR_MSG);
    }
    @Test(expected = IllegalArgumentException.class)
    public void jsonDeserialization_null_baseScorerConf_Test() throws Exception {
        FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams().setBaseScorerParams(null);
        doDeserialization(params, "java.lang.IllegalArgumentException", FieldValueScoreReducerScorerConf.NULL_BASE_SCORER_ERROR_MSG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void jsonDeserialization_null_limiters_Test() throws Exception {
        FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams().setLimiters(null);
        doDeserialization(params, "java.lang.IllegalArgumentException", FieldValueScoreReducerScorerConf.NULL_LIMITERS_ERROR_MSG);
    }

}
