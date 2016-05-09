package fortscale.ml.scorer.factory;

import fortscale.common.event.Event;
import fortscale.domain.core.FeatureScore;
import fortscale.ml.scorer.ReductionScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ReductionScorerConf;
import fortscale.ml.scorer.config.ReductionScorerConfParams;
import fortscale.utils.factory.FactoryConfig;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-factory-tests-context.xml"})
public class ReductionScorerFactoryTest {

    @Autowired
    ReductionScorerFactory reductionScorerFactory;

    @Autowired
    FactoryService<Scorer> scorerFactoryService;


    @Test(expected = IllegalArgumentException.class)
    public void confNotOfExpectedType() {
        reductionScorerFactory.getProduct(new FactoryConfig() {
            @Override
            public String getFactoryName() {
                return null;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullConfTest() {
        reductionScorerFactory.getProduct(null);
    }


    @Test
    public void getProductTest() {
        ReductionScorerConfParams params = new ReductionScorerConfParams();
        IScorerConf dummyConf1 = new IScorerConf() {
            @Override public String getName() { return "scorer1"; }
            @Override public String getFactoryName() {return "scorer1Factory"; }
        };
        IScorerConf dummyConf2 = new IScorerConf() {
            @Override public String getName() { return "scorer2"; }
            @Override public String getFactoryName() {return "scorer2Factory"; }
        };

        ReductionScorerConf conf = new ReductionScorerConf(params.getName(),
                dummyConf1,
                dummyConf2,
                params.getReductionWeight()
        ).setReductionZeroScoreWeight(params.getReductionZeroScoreWeight());

        scorerFactoryService.register(dummyConf1.getFactoryName(), factoryConfig -> new Scorer() {
            @Override
            public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
                return null;
            }

            @Override
            public String getName() {
                return dummyConf1.getName();
            }
        });

        scorerFactoryService.register(dummyConf2.getFactoryName(), factoryConfig -> new Scorer() {
            @Override
            public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
                return null;
            }

            @Override
            public String getName() {
                return dummyConf2.getName();
            }
        });

        ReductionScorer scorer = reductionScorerFactory.getProduct(conf);

        Assert.assertEquals(params.getName(), scorer.getName());
        Assert.assertEquals(params.getReductionWeight(), scorer.getReductionWeight(), 0.0);
        Assert.assertEquals(params.getReductionZeroScoreWeight(), scorer.getReductionZeroScoreWeight(), 0.0);
        Assert.assertEquals(dummyConf1.getName(), scorer.getMainScorer().getName());
        Assert.assertEquals(dummyConf2.getName(), scorer.getReductionScorer().getName());
    }

}
