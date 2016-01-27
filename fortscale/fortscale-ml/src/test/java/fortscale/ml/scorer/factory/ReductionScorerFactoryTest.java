package fortscale.ml.scorer.factory;


import fortscale.common.event.Event;
import fortscale.common.event.EventMessage;
import fortscale.ml.scorer.FeatureScore;
import fortscale.ml.scorer.ReductionScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ReductionScorerConf;
import fortscale.ml.scorer.config.ReductionScorerConfParams;
import fortscale.utils.factory.FactoryConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/scorer-factory-tests-context.xml"})
public class ReductionScorerFactoryTest {

    @Autowired
    ReductionScorerFactory reductionScorerFactory;

    @Autowired
    ScorersFactoryService scorersFactoryService;

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
        IScorerConf dummyConf = new IScorerConf() {
            @Override public String getName() { return null; }
            @Override public String getFactoryName() {return null; }
        };

        ReductionScorerConf conf = new ReductionScorerConf(params.getName(),
                dummyConf,
                dummyConf,
                params.getReductionWeight()
        ).setReductionZeroScoreWeight(params.getReductionZeroScoreWeight());

        when(scorersFactoryService.getProduct(any())).thenReturn(new Scorer() {
            @Override
            public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
                return null;
            }
        });

        ReductionScorer scorer = reductionScorerFactory.getProduct(conf);

        Assert.assertEquals(params.getName(), scorer.getName());
        Assert.assertEquals(params.getReductionWeight(), scorer.getReductionWeight(), 0.0);
        Assert.assertEquals(params.getReductionZeroScoreWeight(), scorer.getReductionZeroScoreWeight(), 0.0);
    }

}
