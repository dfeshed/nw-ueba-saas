package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ConditionalScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ConditionalScorerConf;
import fortscale.ml.scorer.config.ConditionalScorerConfTest;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.Factory;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

/**
 * Created by YaronDL on 8/6/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)

public class ConditionalScorerFactoryTest {
    @Autowired
    private FactoryService<Scorer> scorerFactoryService;
    @Autowired
    private ConditionalScorerFactory conditionalScorerFactory;

    @Test
    public void conditional_scorer_factory_should_register_to_factory_service() {
        Factory<Scorer> scorerFactory = scorerFactoryService.getFactory(ConditionalScorerConf.SCORER_TYPE);
        Assert.assertNotNull(scorerFactory);
        Assert.assertEquals(conditionalScorerFactory, scorerFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void get_product_should_fail_when_factory_config_is_null() {
        conditionalScorerFactory.getProduct(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void get_product_should_fail_when_factory_config_is_of_wrong_type() throws IOException {
        conditionalScorerFactory.getProduct(ConditionalScorerConfTest.getScorerConf(
                ConditionalScorerConfTest.defaultScorerConfJsonObject));
    }

    @Test
    public void get_product_should_return_the_correct_scorer() throws IOException {
        String name = "mySubScorer";
        String conditionalField = "myConditionalField";
        String conditionalValue = "myConditionalValue";
        Scorer scorer = conditionalScorerFactory.getProduct(ConditionalScorerConfTest.getScorerConf(
                ConditionalScorerConfTest.getConditionalScorerConfJsonObject(ConditionalScorerConf.SCORER_TYPE, name, ConditionalScorerConfTest.defaultScorerConfJsonObject, conditionalField, conditionalValue)));
        Assert.assertNotNull(scorer);
        Assert.assertEquals(ConditionalScorer.class, scorer.getClass());
    }

    @Configuration
    public static class ConditionalScorerFactoryTestConfig{
        @Autowired
        public List<AbstractServiceAutowiringFactory<Scorer>> scorersFactories;

        @Bean
        public ConditionalScorerFactory getConditionalScorerFactory(){
            return new ConditionalScorerFactory();
        }

        @Bean
        public ConstantRegexScorerFactory getConstantRegexScorer(){
            return new ConstantRegexScorerFactory();
        }

        @Bean
        public FactoryService<Scorer> scorerFactoryService() {
            FactoryService<Scorer> scorerFactoryService = new FactoryService<>();
            scorersFactories.forEach(x -> x.registerFactoryService(scorerFactoryService));
            return scorerFactoryService;
        }
    }
}
