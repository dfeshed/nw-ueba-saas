package fortscale.ml.scorer.factory;

import fortscale.ml.model.ModelConfService;
import fortscale.ml.scorer.ConstantRegexScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ConstantRegexScorerConf;
import fortscale.ml.scorer.params.ConstantRegexScorerParams;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { ScorerFactoriesTestConfig.class})
public class ConstantRegexScorerFactoryTest {
    @MockBean
    private ModelConfService modelConfService;
    @Autowired
    ConstantRegexScorerFactory constantRegexScorerFactory;

    @Autowired
    FactoryService<Scorer> scorerFactoryService;

    @Test(expected = IllegalArgumentException.class)
    public void confNotOfExpectedType() {
        constantRegexScorerFactory.getProduct(() -> null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullConfTest() {
        constantRegexScorerFactory.getProduct(null);
    }

    @Test
    public void getProductTest() {
        ConstantRegexScorerConf conf = (ConstantRegexScorerConf)new ConstantRegexScorerParams().getScorerConf();
        ConstantRegexScorer scorer = (ConstantRegexScorer)constantRegexScorerFactory.getProduct(conf);
        Assert.assertEquals(conf.getName(), scorer.getName());
        Assert.assertEquals(conf.getRegexFieldName(), scorer.getRegexFieldName());
        Assert.assertEquals(conf.getRegexPattern(), scorer.getRegexPattern());
        Assert.assertEquals(conf.getConstantScore(), scorer.getConstantScore());
    }
}
