package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.domain.core.FeatureScore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class ScoreAndCertaintyMultiplierScorerTest {
    private Scorer baseScorer;
    private Event eventMessage;
    private long evenEpochTime;

    @Before
    public void setup() {
        baseScorer = Mockito.mock(Scorer.class);
        eventMessage = Mockito.mock(Event.class);
        evenEpochTime = 1234;
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
        Mockito.when(baseScorer.calculateScore(eventMessage, evenEpochTime)).thenReturn(baseScore);
        String featureScoreName = "wrapped score";
        ScoreAndCertaintyMultiplierScorer scoreAndCertaintyMultiplierScorer =
                new ScoreAndCertaintyMultiplierScorer(featureScoreName, baseScorer);

        FeatureScore featureScore = scoreAndCertaintyMultiplierScorer.calculateScore(eventMessage, evenEpochTime);
        Assert.assertEquals(featureScoreName, featureScore.getName());
        Assert.assertEquals(score * certainty, featureScore.getScore(), 0.0001);
        Assert.assertEquals(1, featureScore.getFeatureScores().size());
        Assert.assertEquals(baseScore, featureScore.getFeatureScores().get(0));
    }
}
