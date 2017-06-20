package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.scorer.params.ConstantRegexScorerParams;
import fortscale.ml.scorer.record.JsonAdeRecord;
import fortscale.ml.scorer.record.JsonAdeRecordReader;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.AdeRecordReader;

import java.time.Instant;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-tests-context.xml"})
public class ConstantRegexScorerTest {
    ConstantRegexScorer createConstantRegexScorer(ConstantRegexScorerParams params) {
        return new ConstantRegexScorer(params.getName(), params.getRegexFieldName(), params.getRegexPattern(), params.getConstantScore());
    }

    private void assertScorerParams(ConstantRegexScorer scorer, ConstantRegexScorerParams params) {
        Assert.assertEquals(params.getName(), scorer.getName());
        Assert.assertEquals(params.getConstantScore(), scorer.getConstantScore());
        Assert.assertEquals(params.getRegexPattern().toString(), scorer.getRegexPattern().toString());
        Assert.assertEquals(params.getRegexFieldName(), scorer.getRegexFieldName());
    }

    private AdeRecordReader buildAdeRecordReader(String fieldName, Object fieldValue) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(fieldName, fieldValue);
        return new JsonAdeRecordReader(new JsonAdeRecord(Instant.now(), jsonObject));
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
        createConstantRegexScorer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_name_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setName("");
        createConstantRegexScorer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_blank_name_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setName(" ");
        createConstantRegexScorer(params);
    }

    //================================================================
    // Constructor tests - featureFieldName
    //================================================================
    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_featureFieldName_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexFieldName(null);
        createConstantRegexScorer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_empty_featureFieldName_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexFieldName("");
        createConstantRegexScorer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_blank_featureFieldName_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexFieldName("   ");
        createConstantRegexScorer(params);
    }

    //================================================================
    // Constructor tests - regexPattern
    //================================================================
    @Test(expected = IllegalArgumentException.class)
    public void constructor_null_regexPattern_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString(null);
        createConstantRegexScorer(params);
    }

    @Test
    public void constructor_empty_regexPattern_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("");
        createConstantRegexScorer(params);
    }

    @Test
    public void constructor_blank_regexPattern_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("    ");
        createConstantRegexScorer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_invalid_regexPattern_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("[[]]");
        createConstantRegexScorer(params);
    }

    //================================================================
    // Constructor tests - constantScore
    //================================================================
    @Test(expected = IllegalArgumentException.class)
    public void constructor_negative_constantScore_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setConstantScore(-1);
        createConstantRegexScorer(params);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_larger_then_100_constantScore_test() {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setConstantScore(101);
        createConstantRegexScorer(params);
    }

    //================================================================
    // calculateScore tests
    //================================================================
    @Test
    public void calculateScore_matching_pattern_test() throws Exception {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("[mynaeisgotyuklfthrpd]+");
        ConstantRegexScorer scorer = createConstantRegexScorer(params);

        AdeRecordReader adeRecordReader = buildAdeRecordReader(params.getRegexFieldName(), "mynameisinigomontoyayoukillmyfatherpreparetodie");
        FeatureScore featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertEquals((double)params.getConstantScore(), featureScore.getScore(), 0.0);

        adeRecordReader = buildAdeRecordReader(params.getRegexFieldName(), "mynameisinigomontoyayoukillmyfatherpreparetodie2");
        featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertNull(featureScore);
    }

    @Test
    public void calculateScore_matching_pattern2_test() throws Exception {
        ConstantRegexScorerParams params = new ConstantRegexScorerParams().setRegexPatternString("[a-u]{1,3}");
        ConstantRegexScorer scorer = createConstantRegexScorer(params);

        AdeRecordReader adeRecordReader = buildAdeRecordReader(params.getRegexFieldName(), "ave");
        FeatureScore featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertNull(featureScore);

        adeRecordReader = buildAdeRecordReader(params.getRegexFieldName(), "avee");
        featureScore = scorer.calculateScore(adeRecordReader);
        Assert.assertNull(featureScore);
    }
}
