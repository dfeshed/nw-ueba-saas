package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.junit.Assert;
import org.junit.Test;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BooleanConditionalScorerTest {

    @Test
    public void testGettingScoreWhenFieldEqualToValue(){
        String conditionalField = "context.operationTypeCategories";
        Boolean conditionalValue = true;
        double expectedScore = 98;
        ConditionalScorer conditionalScorer = new BooleanConditionalScorer("myConditionalScorer", getScorerMock(expectedScore), conditionalField, conditionalValue);
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        when(adeRecordReader.get(conditionalField, Boolean.class)).thenReturn(conditionalValue);

        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNotNull(featureScore);
        Assert.assertEquals(expectedScore, featureScore.getScore(),0.0);
    }

    @Test
    public void testGettingScoreWhenFieldIsNotEqualToValue(){
        String conditionalField = "context.operationTypeCategories";
        Boolean conditionalValue = true;
        double expectedScore = 98;
        ConditionalScorer conditionalScorer = new BooleanConditionalScorer("myConditionalScorer", getScorerMock(expectedScore), conditionalField, conditionalValue);
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        when(adeRecordReader.get(conditionalField, Boolean.class)).thenReturn(!conditionalValue);

        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNull(featureScore);
    }

    @Test
    public void testGettingNoFeatureScoreWhenFieldDoesNotExist(){
        String conditionalField = "context.operationTypeCategories";
        Boolean conditionalValue = true;
        double expectedScore = 98;
        ConditionalScorer conditionalScorer = new BooleanConditionalScorer("myConditionalScorer", getScorerMock(expectedScore), conditionalField, conditionalValue);
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        when(adeRecordReader.get(conditionalField, List.class)).thenReturn(null);

        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNull(featureScore);
    }

    private static Scorer getScorerMock(double score) {
        Scorer scorer = mock(Scorer.class);
        when(scorer.calculateScore(any(AdeRecordReader.class))).thenReturn(new FeatureScore("myScorer", score));
        return scorer;
    }
}
