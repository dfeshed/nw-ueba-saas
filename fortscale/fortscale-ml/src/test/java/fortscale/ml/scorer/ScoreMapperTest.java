package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.ml.scorer.config.ScoreMappingConf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;


@RunWith(JUnit4.class)
public class ScoreMapperTest {
    private Scorer baseScorer;
    private ScoreMappingConf scoreMappingConf;
    private Event eventMessage;
    private long evenEpochTime;

    @Before
    public void setup() {
        baseScorer = Mockito.mock(Scorer.class);
        scoreMappingConf = new ScoreMappingConf();
        eventMessage = Mockito.mock(Event.class);
        evenEpochTime = 1234;
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsName() {
        new ScoreMapper(null, baseScorer, scoreMappingConf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsBaseScorer() {
        new ScoreMapper("name", null, scoreMappingConf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNullAsScorerMappingConf() {
        new ScoreMapper("name", baseScorer, null);
    }

    @Test
    public void shouldWrapBaseScoreWithTheSameScoreWhenMappingIsEmpty() throws Exception {
        double score = 56;
        FeatureScore baseScore = new FeatureScore("base score", score);
        Mockito.when(baseScorer.calculateScore(eventMessage, evenEpochTime)).thenReturn(baseScore);
        String featureScoreName = "mapped score";
        ScoreMapper scorer = new ScoreMapper(featureScoreName, baseScorer, scoreMappingConf);

        FeatureScore featureScore = scorer.calculateScore(eventMessage, evenEpochTime);
        Assert.assertEquals(featureScoreName, featureScore.getName());
        Assert.assertEquals(score, featureScore.getScore(), 0.0001);
        Assert.assertEquals(1, featureScore.getFeatureScores().size());
        Assert.assertEquals(baseScore, featureScore.getFeatureScores().get(0));
    }

    private class MappingAsserter {
        private Map<Double, Double> mapping = new HashMap<>();

        public MappingAsserter map(double from, double to) {
            mapping.put(from, to);
            return this;
        }

        public void doAssertMapping(double expectedMappedScore, double score) throws Exception {
            FeatureScore featureScore = getFeatureScore(score);

            Assert.assertEquals(expectedMappedScore, featureScore.getScore(), 0.0001);
        }

        public FeatureScore getFeatureScore(double score) throws Exception {
            FeatureScore baseScore = new FeatureScore("base score", score);
            Mockito.when(baseScorer.calculateScore(eventMessage, evenEpochTime)).thenReturn(baseScore);
            String featureScoreName = "mapped score";
            scoreMappingConf.setMapping(mapping);
            ScoreMapper scorer = new ScoreMapper(featureScoreName, baseScorer, scoreMappingConf);

            return scorer.calculateScore(eventMessage, evenEpochTime);
        }
    }

    @Test
    public void shouldMapScoreToItselfIfMappingIsRedundant() throws Exception {
        for (int score = 0; score <= 100; score++) {
            new MappingAsserter().map(50, 50).map(70, 70).doAssertMapping(score, score);
        }
    }

    @Test
    public void shouldMapSourceToDestinationWhenItIsSpecifiedByMapping() throws Exception {
        int source = 30;
        int destination = 50;
        new MappingAsserter().map(source, destination).doAssertMapping(destination, source);
    }

    @Test
    public void shouldMapPointsLinearlyBasedOnNeighbourPointsSpecifiedByMapping() throws Exception {
        int source = 20;
        int destination = 40;
        MappingAsserter asserter = new MappingAsserter().map(source, destination);
        asserter.doAssertMapping(10, 5);
        asserter.doAssertMapping(20, 10);
        asserter.doAssertMapping(30, 15);
        asserter.doAssertMapping(63, 50);
        asserter.doAssertMapping(70, 60);
        asserter.doAssertMapping(78, 70);
    }
}
