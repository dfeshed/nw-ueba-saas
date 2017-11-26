package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.junit.Assert;
import org.junit.Test;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by YaronDL on 8/6/2017.
 */
public class ListConditionalScorerTest {

    @Test
    public void testGettingScoreWhenListContainsValue(){
        String conditionalField = "context.operationTypeCategories";
        String conditionalValue = "FILE_ACTION";
        double expectedScore = 98;
        ConditionalScorer conditionalScorer = new ListConditionalScorer("myConditionalScorer", getScorerMock(expectedScore), conditionalField, conditionalValue);
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        List<String> fieldValues = new ArrayList<>();
        fieldValues.add(conditionalValue);
        fieldValues.add("justAnotherValue");
        when(adeRecordReader.get(conditionalField, List.class)).thenReturn(fieldValues);

        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNotNull(featureScore);
        Assert.assertEquals(expectedScore, featureScore.getScore(),0.0);
    }

    @Test
    public void testGettingScoreWhenListContainsBothValues(){
        String conditionalField = "context.operationTypeCategories";
        String conditionalFileActionValue = "FILE_ACTION";
        String conditionalFilePermissionValue = "FILE_PERMISSION";
        double expectedScore = 98;
        ConditionalScorer conditionalScorer = new ListConditionalScorer("myConditionalScorer", getScorerMock(expectedScore), conditionalField,
                conditionalFileActionValue + ListConditionalScorer.CONDITIONAL_VALUE_CHAR_SPLIT + conditionalFilePermissionValue);
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        List<String> fieldValues = new ArrayList<>();
        fieldValues.add("someCategoryValue");
        fieldValues.add(conditionalFileActionValue);
        fieldValues.add("justAnotherCategoryValue");
        fieldValues.add(conditionalFilePermissionValue);
        fieldValues.add("yetAnotherCategoryValue");
        when(adeRecordReader.get(conditionalField, List.class)).thenReturn(fieldValues);

        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNotNull(featureScore);
        Assert.assertEquals(expectedScore, featureScore.getScore(),0.0);
    }

    @Test
    public void testGettingScoreWhenListDoesNotContainBothValues(){
        String conditionalField = "context.operationTypeCategories";
        String conditionalFileActionValue = "FILE_ACTION";
        String conditionalFilePermissionValue = "FILE_PERMISSION";
        double expectedScore = 98;
        ConditionalScorer conditionalScorer = new ListConditionalScorer("myConditionalScorer", getScorerMock(expectedScore), conditionalField,
                conditionalFileActionValue + ListConditionalScorer.CONDITIONAL_VALUE_CHAR_SPLIT + conditionalFilePermissionValue);
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        List<String> fieldValues = new ArrayList<>();
        fieldValues.add("someCategoryValue");
        fieldValues.add(conditionalFileActionValue);
        fieldValues.add("justAnotherCategoryValue");
        fieldValues.add("yetAnotherCategoryValue");
        when(adeRecordReader.get(conditionalField, List.class)).thenReturn(fieldValues);

        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNull(featureScore);
    }

    @Test
    public void testGettingNoFeatureScoreWhenListDoesNotContainValue(){
        String conditionalField = "context.operationTypeCategories";
        String conditionalValue = "FILE_ACTION";
        double expectedScore = 98;
        ConditionalScorer conditionalScorer = new ListConditionalScorer("myConditionalScorer", getScorerMock(expectedScore), conditionalField, conditionalValue);
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);
        List<String> fieldValues = new ArrayList<>();
        fieldValues.add("Value");
        fieldValues.add("justAnotherValue");
        when(adeRecordReader.get(conditionalField, List.class)).thenReturn(fieldValues);

        FeatureScore featureScore = conditionalScorer.calculateScore(adeRecordReader);
        Assert.assertNull(featureScore);
    }

    @Test
    public void testGettingNoFeatureScoreWhenFieldDoesNotExist(){
        String conditionalField = "context.operationTypeCategories";
        String conditionalValue = "FILE_ACTION";
        double expectedScore = 98;
        ConditionalScorer conditionalScorer = new ListConditionalScorer("myConditionalScorer", getScorerMock(expectedScore), conditionalField, conditionalValue);
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
