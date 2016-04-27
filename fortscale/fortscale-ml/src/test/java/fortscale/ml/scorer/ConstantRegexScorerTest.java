package fortscale.ml.scorer;


import fortscale.common.event.Event;
import fortscale.common.event.EventMessage;
import fortscale.common.feature.Feature;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.domain.core.FeatureScore;
import fortscale.ml.scorer.params.ConstantRegexScorerParams;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-tests-context.xml"})
public class ConstantRegexScorerTest {

    @Autowired
    FeatureExtractService featureExtractService;

    ConstantRegexScorer createConstantRegexScorer(ConstantRegexScorerParams params) {
        return new ConstantRegexScorer(params.getName(), params.getRegexFieldName(), params.getRegexPattern(), params.getConstantScore());
    }

    private void assertScorerParams(ConstantRegexScorer scorer, ConstantRegexScorerParams params) {
        Assert.assertEquals(params.getName(), scorer.getName());
        Assert.assertEquals(params.getConstantScore(), scorer.getConstantScore());
        Assert.assertEquals(params.getRegexPattern().toString(), scorer.getRegexPattern().toString());
        Assert.assertEquals(params.getRegexFieldName(), scorer.getRegexFieldName());
    }

    private EventMessage buildEventMessage(String fieldName, Object fieldValue){
        JSONObject jsonObject = null;
        jsonObject = new JSONObject();
        jsonObject.put(fieldName, fieldValue);
        return new EventMessage(jsonObject);
    }

    //================================================================
    // Constructor tests - name
    //================================================================

    @Test
    public void constructor_good_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams();
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
        assertScorerParams(scorer, params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_name_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setName(null);
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_name_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setName("");
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_blank_name_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setName(" ");
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
    }

    //================================================================
    // Constructor tests - featureFieldName
    //================================================================
    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_featureFieldName_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexFieldName(null);
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_featureFieldName_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexFieldName("");
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_blank_featureFieldName_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexFieldName("   ");
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
    }

    //================================================================
    // Constructor tests - regexPattern
    //================================================================
    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_regexPattern_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString(null);
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
    }

    @Test
    public void constructor_empty_regexPattern_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("");
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
    }

    @Test
    public void constructor_blank_regexPattern_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("    ");
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_invalid_regexPattern_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("[[]]");
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
    }

    //================================================================
    // Constructor tests - constantScore
    //================================================================
    @Test(expected = IllegalArgumentException.class)
    public void constructor_negative_constantScore_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setConstantScore(-1);
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_larger_then_100_constantScore_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setConstantScore(101);
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
    }

    //================================================================
    // calculateScore tests
    //================================================================
    @Test
    public void calculateScore_matching_pattern_test() throws Exception {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("[mynaeisgotyuklfthrpd]+");
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
        String fieldName = "username";
        String fieldValue = "mynameisinigomontoyayoukillmyfatherpreparetodie";
        EventMessage eventMessage = buildEventMessage(fieldName, fieldValue);
        Feature feature = new Feature(fieldName, fieldValue);
        when(featureExtractService.extract(any(String.class), any(Event.class))).thenReturn(feature);
        FeatureScore featureScore = scorer.calculateScore(eventMessage, 0l);
        Assert.assertEquals((double)params.getConstantScore(), featureScore.getScore(), 0.0);

        fieldValue = "mynameisinigomontoyayoukillmyfatherpreparetodie2";
        eventMessage = buildEventMessage(fieldName, fieldValue);
        feature = new Feature(fieldName, fieldValue);
        when(featureExtractService.extract(any(String.class), any(Event.class))).thenReturn(feature);
        featureScore = scorer.calculateScore(eventMessage, 0l);
        Assert.assertNull(featureScore);

    }

    @Test
    public void calculateScore_matching_pattern2_test() throws Exception{
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("[a-u]{1,3}");
        ConstantRegexScorer scorer = createConstantRegexScorer(params);
        String fieldName = "username";
        String fieldValue = "ave";
        EventMessage eventMessage = buildEventMessage(fieldName, fieldValue);
        Feature feature = new Feature(fieldName, fieldValue);
        when(featureExtractService.extract(any(String.class), any(Event.class))).thenReturn(feature);
        FeatureScore featureScore = scorer.calculateScore(eventMessage, 0l);
        Assert.assertNull(featureScore);

        fieldValue = "avee";
        eventMessage = buildEventMessage(fieldName, fieldValue);
        feature = new Feature(fieldName, fieldValue);
        when(featureExtractService.extract(any(String.class), any(Event.class))).thenReturn(feature);
        featureScore = scorer.calculateScore(eventMessage, 0l);
        Assert.assertNull(featureScore);
    }

}
