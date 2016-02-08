package fortscale.ml.scorer.factory;

import fortscale.common.event.Event;
import fortscale.ml.scorer.FeatureScore;
import fortscale.ml.scorer.ParetoScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ParetoScorerConf;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-factory-tests-context.xml"})
public class ParetoScorerFactoryTest {

    @Autowired
    ParetoScorerFactory paretoScorerFactory;

    @Autowired
    FactoryService<Scorer> scorerFactoryService;

    @Before
    public void setUp() {
        reset(scorerFactoryService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void confNotOfExpectedType() {
        paretoScorerFactory.getProduct(new FactoryConfig() {
            @Override
            public String getFactoryName() {
                return null;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullConfTest() {
        paretoScorerFactory.getProduct(null);
    }


    @Test
    public void getProductTest() {
        IScorerConf dummyConf = new IScorerConf() {
            @Override public String getName() { return null; }
            @Override public String getFactoryName() {return null; }
        };

        String scorerName = "scorer name";
        double highestScoreWeight = 0.75;

        List<IScorerConf> scorerConfs = new ArrayList<>();
        scorerConfs.add(dummyConf);

        ParetoScorerConf conf = new ParetoScorerConf(scorerName, highestScoreWeight, scorerConfs);

        when(scorerFactoryService.getProduct(any(IScorerConf.class))).thenReturn(new Scorer() {
            @Override
            public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
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
