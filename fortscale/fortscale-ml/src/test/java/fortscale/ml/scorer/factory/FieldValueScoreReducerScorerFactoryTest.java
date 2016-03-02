package fortscale.ml.scorer.factory;

import fortscale.common.event.Event;
import fortscale.ml.scorer.FeatureScore;
import fortscale.ml.scorer.FieldValueScoreReducerScorer;
import fortscale.ml.scorer.ReductionScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.FieldValueScoreReducerScorerConf;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ReductionScorerConf;
import fortscale.ml.scorer.params.FieldValueScoreReducerScorerConfParams;
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
public class FieldValueScoreReducerScorerFactoryTest {

    @Autowired
    FieldValueScoreReducerScorerFactory fieldValueScoreReducerScorerFactory;

    @Autowired
    FactoryService<Scorer> scorerFactoryService;


    @Test(expected = IllegalArgumentException.class)
    public void confNotOfExpectedType() {
        fieldValueScoreReducerScorerFactory.getProduct(new FactoryConfig() {
            @Override
            public String getFactoryName() {
                return null;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullConfTest() {
        fieldValueScoreReducerScorerFactory.getProduct(null);
    }


    @Test
    public void getProductTest() {
        FieldValueScoreReducerScorerConfParams params = new FieldValueScoreReducerScorerConfParams();
        IScorerConf dummyConf1 = new IScorerConf() {
            @Override public String getName() { return "scorer1"; }
            @Override public String getFactoryName() {return "scorer1Factory"; }
        };

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

        FieldValueScoreReducerScorerConf conf = new FieldValueScoreReducerScorerConf(params.getName(), dummyConf1, params.getLimiters());
        FieldValueScoreReducerScorer scorer = fieldValueScoreReducerScorerFactory.getProduct(conf);

        Assert.assertEquals(params.getName(), scorer.getName());
        Assert.assertEquals(params.getLimiters(), scorer.getLimiters());
        Assert.assertEquals(dummyConf1.getName(), scorer.getBaseScorer().getName());
    }

}
