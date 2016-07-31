package fortscale.ml.scorer.factory;

import fortscale.common.event.Event;
import fortscale.domain.core.FeatureScore;
import fortscale.ml.scorer.ScoreMapper;
import fortscale.ml.scorer.ScoreMapping;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ScoreMapperConf;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-factory-tests-context.xml"})
public class ScoreMapperFactoryTest {

    @Autowired
    private ScoreMapperFactory scoreMapperFactory;

    @Autowired
    private FactoryService<Scorer> scorerFactoryService;

    private Scorer baseScorerMock = Mockito.mock(Scorer.class);


    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNull() {
        scoreMapperFactory.getProduct(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenIllegalConfType() {
        scoreMapperFactory.getProduct(() -> "factory-name");
    }

    public ScoreMapper createScorer(ScoreMapping.ScoreMappingConf scoreMappingConf, String scorerName) {
        IScorerConf baseScorerConf = new IScorerConf() {
            @Override public String getName() {
                return "base-scorer";
            }
            @Override public String getFactoryName() {
                return "baseScorerFactoryName";
            }
        };

        scorerFactoryService.register(baseScorerConf.getFactoryName(), factoryConfig -> baseScorerMock);

        ScoreMapperConf conf = new ScoreMapperConf(
                scorerName,
                baseScorerConf,
                scoreMappingConf
        );

        return scoreMapperFactory.getProduct(conf);
    }

    public ScoreMapper createScorer(ScoreMapping.ScoreMappingConf scoreMappingConf) {
        return createScorer(scoreMappingConf, "scorerName");
    }

    public ScoreMapper createScorer(String scorerName) {
        return createScorer(new ScoreMapping.ScoreMappingConf(), scorerName);
    }

    public ScoreMapper createScorer() {
        return createScorer(new ScoreMapping.ScoreMappingConf(), "scorerName");
    }

    @Test
    public void shouldCreateScorerWithTheRightName() throws Exception {
        String scorerName = "scorerName";
        Assert.assertEquals(scorerName, createScorer(scorerName).getName());
    }

    @Test
    public void shouldDelegateToBaseScorerStatedByConfiguration() throws Exception {
        Event eventMessage = Mockito.mock(Event.class);
        long evenEpochTime = 1234;

        double score = 56;
        Mockito.when(baseScorerMock.calculateScore(eventMessage, evenEpochTime))
                .thenReturn(new FeatureScore("name", score));

        Assert.assertEquals(score, createScorer().calculateScore(eventMessage, evenEpochTime).getScore(), 0.0001);
    }

    @Test
    public void shouldUseScoreMappingConfStatedByConfiguration() throws Exception {
        Event eventMessage = Mockito.mock(Event.class);
        long evenEpochTime = 1234;

        double score = 56;
        Mockito.when(baseScorerMock.calculateScore(eventMessage, evenEpochTime))
                .thenReturn(new FeatureScore("name", score));
        HashMap<Double, Double> mapping = new HashMap<>();
        double mappedScore = 50;
        mapping.put(score, mappedScore);
        ScoreMapping.ScoreMappingConf scoreMappingConf = new ScoreMapping.ScoreMappingConf().setMapping(mapping);

        Assert.assertEquals(mappedScore, createScorer(scoreMappingConf).calculateScore(eventMessage, evenEpochTime).getScore(), 0.0001);
    }
}
