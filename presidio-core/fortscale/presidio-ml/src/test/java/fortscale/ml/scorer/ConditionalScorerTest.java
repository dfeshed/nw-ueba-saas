package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.junit.Assert;
import org.junit.Test;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.predicate.BooleanAdeRecordReaderPredicate;
import presidio.ade.domain.record.predicate.ContainedInListAdeRecordReaderPredicate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConditionalScorerTest {
    @Test
    public void testGettingScoreWhenFieldEqualsToValue() {
        BooleanAdeRecordReaderPredicate predicate = new BooleanAdeRecordReaderPredicate("myBooleanField", true);
        ConditionalScorer conditionalScorer = new ConditionalScorer("myConditionalScorer", Collections.singletonList(predicate), getScorer(100));
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        when(adeRecordReader.get(eq("myBooleanField"), eq(Boolean.class))).thenReturn(true);
        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNotNull(featureScore);
        Assert.assertEquals(100, featureScore.getScore(), 0);
    }

    @Test
    public void testGettingScoreWhenFieldIsNotEqualToValue() {
        BooleanAdeRecordReaderPredicate predicate = new BooleanAdeRecordReaderPredicate("myBooleanField", true);
        ConditionalScorer conditionalScorer = new ConditionalScorer("myConditionalScorer", Collections.singletonList(predicate), getScorer(90));
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        when(adeRecordReader.get(eq("myBooleanField"), eq(Boolean.class))).thenReturn(false);
        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNull(featureScore);
    }

    @Test
    public void testGettingNoFeatureScoreWhenValueDoesNotExist() {
        BooleanAdeRecordReaderPredicate predicate = new BooleanAdeRecordReaderPredicate("myBooleanField", true);
        ConditionalScorer conditionalScorer = new ConditionalScorer("myConditionalScorer", Collections.singletonList(predicate), getScorer(80));
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        when(adeRecordReader.get(eq("myBooleanField"), eq(Boolean.class))).thenReturn(null);
        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNull(featureScore);
    }

    @Test
    public void testGettingScoreWhenListContainsOneValue() {
        ContainedInListAdeRecordReaderPredicate predicate = new ContainedInListAdeRecordReaderPredicate("operationTypeCategories", Collections.singletonList("FILE_ACTION"), "&");
        ConditionalScorer conditionalScorer = new ConditionalScorer("myConditionalScorer", Collections.singletonList(predicate), getScorer(70));
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        when(adeRecordReader.get(eq("operationTypeCategories"), eq(List.class))).thenReturn(Collections.singletonList("FILE_ACTION"));
        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNotNull(featureScore);
        Assert.assertEquals(70, featureScore.getScore(), 0);
    }

    @Test
    public void testGettingScoreWhenListContainsTwoValues() {
        ContainedInListAdeRecordReaderPredicate predicate = new ContainedInListAdeRecordReaderPredicate("operationTypeCategories", Arrays.asList("FILE_ACTION", "FILE_PERMISSION_CHANGE"), "&&");
        ConditionalScorer conditionalScorer = new ConditionalScorer("myConditionalScorer", Collections.singletonList(predicate), getScorer(60));
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        List<String> actualValues = Arrays.asList(
                "someOperationTypeCategory",
                "FILE_ACTION",
                "justAnotherOperationTypeCategory",
                "FILE_PERMISSION_CHANGE",
                "yetAnotherOperationTypeCategory");
        when(adeRecordReader.get(eq("operationTypeCategories"), eq(List.class))).thenReturn(actualValues);
        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNotNull(featureScore);
        Assert.assertEquals(60, featureScore.getScore(), 0);
    }

    @Test
    public void testGettingScoreWhenListDoesNotContainTwoValues() {
        ContainedInListAdeRecordReaderPredicate predicate = new ContainedInListAdeRecordReaderPredicate("operationTypeCategories", Arrays.asList("FILE_ACTION", "FILE_PERMISSION_CHANGE"), "and");
        ConditionalScorer conditionalScorer = new ConditionalScorer("myConditionalScorer", Collections.singletonList(predicate), getScorer(50));
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        List<String> actualValues = Arrays.asList(
                "someOperationTypeCategory",
                "FILE_ACTION",
                "justAnotherOperationTypeCategory");
        when(adeRecordReader.get(eq("operationTypeCategories"), eq(List.class))).thenReturn(actualValues);
        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNull(featureScore);
    }

    @Test
    public void testGettingNoFeatureScoreWhenListDoesNotContainAnyValue() {
        ContainedInListAdeRecordReaderPredicate predicate = new ContainedInListAdeRecordReaderPredicate("operationTypeCategories", Arrays.asList("FILE_ACTION", "FILE_PERMISSION_CHANGE"), "|");
        ConditionalScorer conditionalScorer = new ConditionalScorer("myConditionalScorer", Collections.singletonList(predicate), getScorer(40));
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        when(adeRecordReader.get(eq("operationTypeCategories"), eq(List.class))).thenReturn(Collections.singletonList("someOperationTypeCategory"));
        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNull(featureScore);
    }

    @Test
    public void testGettingNoFeatureScoreWhenListDoesNotExist() {
        ContainedInListAdeRecordReaderPredicate predicate = new ContainedInListAdeRecordReaderPredicate("operationTypeCategories", Collections.singletonList("FILE_PERMISSION_CHANGE"), null);
        ConditionalScorer conditionalScorer = new ConditionalScorer("myConditionalScorer", Collections.singletonList(predicate), getScorer(30));
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        when(adeRecordReader.get(eq("operationTypeCategories"), eq(List.class))).thenReturn(null);
        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNull(featureScore);
    }

    private static Scorer getScorer(double score) {
        Scorer scorer = mock(Scorer.class);
        when(scorer.calculateScore(any(AdeRecordReader.class))).thenReturn(new FeatureScore("myScorer", score));
        return scorer;
    }
}
