package fortscale.ml.scorer.factory;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.ParetoScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ParetoScorerConf;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.AdeRecord;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-factory-tests-context.xml"})
public class ParetoScorerFactoryTest {

    @MockBean
    ModelConfService modelConfService;

    @MockBean
    ModelsCacheService modelCacheService;

    @Autowired
    ParetoScorerFactory paretoScorerFactory;

    @Autowired
    FactoryService<Scorer> scorerFactoryService;

    @Test(expected = IllegalArgumentException.class)
    public void confNotOfExpectedType() {
        paretoScorerFactory.getProduct(() -> null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullConfTest() {
        paretoScorerFactory.getProduct(null);
    }


    @Test
    public void getProductTest() {
        IScorerConf dummyConf = new IScorerConf() {
            @Override public String getName() { return null; }
            @Override public String getFactoryName() {return "dummyFactoryName"; }
        };

        String scorerName = "scorer name";
        double highestScoreWeight = 0.75;

        List<IScorerConf> scorerConfs = new ArrayList<>();
        scorerConfs.add(dummyConf);

        ParetoScorerConf conf = new ParetoScorerConf(scorerName, highestScoreWeight, scorerConfs);

        scorerFactoryService.register(dummyConf.getFactoryName(), factoryConfig -> new Scorer() {
            @Override
            public FeatureScore calculateScore(AdeRecord record) {
                return null;
            }

            @Override
            public String getName() {
                return "scorer1";
            }
        });

        ParetoScorer scorer = paretoScorerFactory.getProduct(conf);

        Assert.assertEquals(scorerName, scorer.getName());
        Assert.assertEquals(highestScoreWeight, scorer.getHighestScoreWeight(), 0.0);
        Assert.assertEquals(1, scorer.getScorers().size());
        Assert.assertEquals("scorer1", scorer.getScorers().get(0).getName());
    }

}
