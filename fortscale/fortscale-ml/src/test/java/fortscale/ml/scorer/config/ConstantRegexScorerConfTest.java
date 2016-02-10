package fortscale.ml.scorer.config;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.ml.scorer.RegexScorer;
import fortscale.ml.scorer.params.ConstantRegexScorerParams;
import org.junit.Assert;
import org.junit.Test;

public class ConstantRegexScorerConfTest {

    private void doDeserialization(ConstantRegexScorerParams params) throws Exception{
        doDeserialization(params, null, null);
    }

    private void doDeserialization(ConstantRegexScorerParams params, String expectExceptionClassName, String expectedErrorMessage) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String confJsonStr = params.getScorerConfJsonString();
        System.out.println(confJsonStr);
        try {
            ConstantRegexScorerConf conf = objectMapper.readValue(confJsonStr, ConstantRegexScorerConf.class);
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


    private void assertConf(ConstantRegexScorerConf conf, ConstantRegexScorerParams params) {
        Assert.assertEquals(params.getName(), conf.getName());
        Assert.assertEquals(params.getRegexFieldName(), conf.getRegexFieldName());
        Assert.assertEquals(params.getRegexPatternString(), conf.getRegexPattern().toString());
        Assert.assertEquals(params.getConstantScore(), conf.getConstantScore());
    }


    @Test
    public void deserialization_test() throws Exception{
        ConstantRegexScorerParams params = new ConstantRegexScorerParams();
        doDeserialization(params);
     }

    //==========================================================================================
    // Scorer Name Tests
    //==========================================================================================
    @Test(expected = IllegalArgumentException.class)
    public void deserialization_with_null_name_test() throws Exception{
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setName(null);
        doDeserialization(params, "java.lang.IllegalArgumentException", AbstractScorerConf.NULL_OR_EMPTY_NAME_ERROR_MSG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialization_with_empty_name_test() throws Exception{
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setName("");
        doDeserialization(params, "java.lang.IllegalArgumentException", AbstractScorerConf.NULL_OR_EMPTY_NAME_ERROR_MSG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialization_with_blank_name_test() throws Exception{
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setName("   ");
        doDeserialization(params, "java.lang.IllegalArgumentException", AbstractScorerConf.NULL_OR_EMPTY_NAME_ERROR_MSG);
    }

    //==========================================================================================
    // Regex Field Name Tests
    //==========================================================================================

    @Test(expected = IllegalArgumentException.class)
    public void deserialization_with_null_regex_field_name_test() throws Exception{
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexFieldName(null);
        doDeserialization(params, "java.lang.IllegalArgumentException", RegexScorer.EMPTY_FEATURE_FIELD_NAME_ERROR_MSG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialization_with_empty_regex_field_name_test() throws Exception{
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexFieldName("");
        doDeserialization(params, "java.lang.IllegalArgumentException", RegexScorer.EMPTY_FEATURE_FIELD_NAME_ERROR_MSG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialization_with_blank_regex_filed_name_test() throws Exception{
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexFieldName("   ");
        doDeserialization(params, "java.lang.IllegalArgumentException", RegexScorer.EMPTY_FEATURE_FIELD_NAME_ERROR_MSG);
    }

    //==========================================================================================
    // Regex Pattern Tests
    //==========================================================================================
    @Test(expected = IllegalArgumentException.class)
    public void deserialization_with_null_regex_pattern_test() throws Exception{
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString(null);
        doDeserialization(params, "java.lang.IllegalArgumentException", RegexScorer.NULL_REGEX_ERROR_MSG);
    }
    @Test
    public void deserialization_with_blank_regex_pattern_test() throws Exception{
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("   ");
        doDeserialization(params);
    }
    @Test
    public void deserialization_with_empty_regex_pattern_test() throws Exception{
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("");
        doDeserialization(params);
    }
    @Test(expected = IllegalArgumentException.class)
    public void deserialization_with_illegal_regex_pattern_test() throws Exception{
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("[[]]");
        doDeserialization(params, "java.util.regex.PatternSyntaxException", null);
    }


}
