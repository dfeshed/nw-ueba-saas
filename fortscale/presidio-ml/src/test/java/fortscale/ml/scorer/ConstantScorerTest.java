package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import org.junit.Assert;
import org.junit.Test;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by YaronDL on 1/11/2018.
 */
public class ConstantScorerTest {

    @Test
    public void testGettingConstantScore(){
        String conditionalField = "context.operationTypeCategories";
        String conditionalValue = "FILE_ACTION";
        String name = "myConstantScorer";
        double expectedScore = 92;
        ConstantScorer constantScorer = new ConstantScorer(name, expectedScore);
        AdeRecordReader adeRecordReader = mock(AdeRecordReader.class);

        FeatureScore featureScore = constantScorer.calculateScore(adeRecordReader);
        Assert.assertNotNull(featureScore);
        Assert.assertEquals(name, featureScore.getName());
        Assert.assertEquals(expectedScore, featureScore.getScore(),0.0);
    }
}
