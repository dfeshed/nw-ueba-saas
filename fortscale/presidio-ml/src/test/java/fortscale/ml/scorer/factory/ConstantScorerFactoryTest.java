package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ConstantScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ConstantRegexScorerConf;
import fortscale.ml.scorer.config.ConstantScorerConf;
import fortscale.ml.scorer.config.ConstantScorerConfTest;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.Factory;
import fortscale.utils.factory.FactoryService;
import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
public class ConstantScorerFactoryTest {

    public static final JSONObject constantRegexScorerConfJsonObject;

    static {
        constantRegexScorerConfJsonObject = new JSONObject();
        constantRegexScorerConfJsonObject.put("type", ConstantRegexScorerConf.SCORER_TYPE);
        constantRegexScorerConfJsonObject.put("name", "myConstantRegexScorer");
        constantRegexScorerConfJsonObject.put("regex", "42");
        constantRegexScorerConfJsonObject.put("regex-field-name", "myRegexField");
        constantRegexScorerConfJsonObject.put("constant-score", 100);
    }
    @Autowired
    private FactoryService<Scorer> scorerFactoryService;
    @Autowired
    private ConstantScorerFactory constantScorerFactory;

    @Test
    public void constant_scorer_factory_should_register_to_factory_service() {
        Factory<Scorer> scorerFactory = scorerFactoryService.getFactory(ConstantScorerConf.SCORER_TYPE);
        Assert.assertNotNull(scorerFactory);
        Assert.assertEquals(constantScorerFactory, scorerFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void get_product_should_fail_when_factory_config_is_null() {
        constantScorerFactory.getProduct(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void get_product_should_fail_when_factory_config_is_of_wrong_type() throws IOException {
        constantScorerFactory.getProduct(new ConstantRegexScorerConf("wrongScorer", "aaa", "fieldNameTest", 100));
    }

    @Test
    public void get_product_should_return_the_correct_scorer() throws IOException {
        String name = "myConstantScorer";
        Double constantScore = 30.0;
        Scorer scorer = constantScorerFactory.getProduct(ConstantScorerConfTest.getScorerConf(
                ConstantScorerConfTest.getConstantScorerConfJsonObject(ConstantScorerConf.SCORER_TYPE, name, constantScore)));
        Assert.assertNotNull(scorer);
        Assert.assertEquals(ConstantScorer.class, scorer.getClass());
    }

    @Configuration
    public static class ConditionalScorerFactoryTestConfig{
        @Autowired
        public List<AbstractServiceAutowiringFactory<Scorer>> scorersFactories;

        @Bean
        public ConstantScorerFactory getConstantScorerFactory(){
            return new ConstantScorerFactory();
        }

        @Bean
        public FactoryService<Scorer> scorerFactoryService() {
            FactoryService<Scorer> scorerFactoryService = new FactoryService<>();
            scorersFactories.forEach(x -> x.registerFactoryService(scorerFactoryService));
            return scorerFactoryService;
        }
    }
}
