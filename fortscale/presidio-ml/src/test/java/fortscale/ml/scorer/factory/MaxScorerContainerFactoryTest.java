package fortscale.ml.scorer.factory;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.scorer.MaxScorerContainer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.MaxScorerContainerConf;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { ScorerFactoriesTestConfig.class})
public class MaxScorerContainerFactoryTest {
    @MockBean
    private ModelConfService modelConfService;
    @Autowired
    private MaxScorerContainerFactory MaxScorerContainerFactory;

    @Autowired
    private FactoryService<Scorer> scorerFactoryService;

    @Test(expected = IllegalArgumentException.class)
    public void confNotOfExpectedType() {
        MaxScorerContainerFactory.getProduct(() -> null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullConfTest() {
        MaxScorerContainerFactory.getProduct(null);
    }

    @Test
    public void getProductTest() {
        IScorerConf dummyConf = new IScorerConf() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getFactoryName() {
                return "dummy factory";
            }
        };

        String scorerName = "scorer name";
        List<IScorerConf> scorerConfs = new ArrayList<>();
        scorerConfs.add(dummyConf);
        MaxScorerContainerConf conf = new MaxScorerContainerConf(scorerName, scorerConfs);

        scorerFactoryService.register(dummyConf.getFactoryName(), factoryConfig -> new Scorer() {
            @Override
            public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
                return null;
            }

            @Override
            public String getName() {
                return "scorer1";
            }
        });

        MaxScorerContainer scorer = MaxScorerContainerFactory.getProduct(conf);
        Assert.assertEquals(scorerName, scorer.getName());
        Assert.assertEquals(1, scorer.getScorers().size());
        Assert.assertEquals("scorer1", scorer.getScorers().get(0).getName());
    }
}
