package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.domain.feature.score.ModelFeatureScore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import presidio.ade.domain.record.AdeRecord;


@RunWith(JUnit4.class)
public class ScoreAndCertaintyMultiplierScorerTest {
    private Scorer baseScorer;
    private AdeRecord eventMessage;

    @Before
    public void setup() {
        baseScorer = Mockito.mock(Scorer.class);
        eventMessage = Mockito.mock(AdeRecord.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsName() {
        new ScoreAndCertaintyMultiplierScorer(null, baseScorer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsBaseScorer() {
        new ScoreAndCertaintyMultiplierScorer("name", null);
    }

    @Test
    public void shouldMultiplyScoreByCertainty() throws Exception {
        double score = 56;
        double certainty = 0.4;
        ModelFeatureScore baseScore = new ModelFeatureScore("base score", score, certainty);
        Mockito.when(baseScorer.calculateScore(eventMessage)).thenReturn(baseScore);
        String featureScoreName = "wrapped score";
        ScoreAndCertaintyMultiplierScorer scoreAndCertaintyMultiplierScorer =
                new ScoreAndCertaintyMultiplierScorer(featureScoreName, baseScorer);

        FeatureScore featureScore = scoreAndCertaintyMultiplierScorer.calculateScore(eventMessage);
        Assert.assertEquals(featureScoreName, featureScore.getName());
        Assert.assertEquals(score * certainty, featureScore.getScore(), 0.0001);
        Assert.assertEquals(1, featureScore.getFeatureScores().size());
        Assert.assertEquals(baseScore, featureScore.getFeatureScores().get(0));
    }
}
