package fortscale.ml.scorer.factory;

import fortscale.common.event.Event;
import fortscale.ml.scorer.FeatureScore;
import fortscale.ml.scorer.ScoreMapper;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.*;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Before;
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

    private ScoreMapper scorer;
    private Scorer baseScorerMock;
    private String scorerName;
    private ScoreMappingConf scoreMappingConf;


    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenNull() {
        scoreMapperFactory.getProduct(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailGivenIllegalConfType() {
        scoreMapperFactory.getProduct(() -> "factory-name");
    }

    @Before
    public void createScorer() {
        IScorerConf baseScorerConf = new IScorerConf() {
            @Override public String getName() {
                return "base-scorer";
            }
            @Override public String getFactoryName() {
                return "baseScorerFactoryName";
            }
        };

        baseScorerMock = Mockito.mock(Scorer.class);
        scorerFactoryService.register(baseScorerConf.getFactoryName(), factoryConfig -> baseScorerMock);

        scoreMappingConf = new ScoreMappingConf();
        scorerName = "name";
        ScoreMapperConf conf = new ScoreMapperConf(
                scorerName,
                baseScorerConf,
                scoreMappingConf
        );

        scorer = scoreMapperFactory.getProduct(conf);
    }

    @Test
    public void shouldCreateScorerWithTheRightName() throws Exception {
        Assert.assertEquals(scorerName, scorer.getName());
    }

    @Test
    public void shouldDelegateToBaseScorerStatedByConfiguration() throws Exception {
        Event eventMessage = Mockito.mock(Event.class);
        long evenEpochTime = 1234;

        double score = 56;
        Mockito.when(baseScorerMock.calculateScore(eventMessage, evenEpochTime))
                .thenReturn(new FeatureScore("name", score));

        Assert.assertEquals(score, scorer.calculateScore(eventMessage, evenEpochTime).getScore(), 0.0001);
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
        scoreMappingConf.setMapping(mapping);

        Assert.assertEquals(mappedScore, scorer.calculateScore(eventMessage, evenEpochTime).getScore(), 0.0001);
    }
}
