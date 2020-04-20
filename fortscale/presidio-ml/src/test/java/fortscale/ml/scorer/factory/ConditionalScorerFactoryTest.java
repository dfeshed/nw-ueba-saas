package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ConditionalScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ConditionalScorerConf;
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

import static fortscale.ml.scorer.config.ConditionalScorerConfTest.*;
import static java.util.Collections.singletonList;

@RunWith(SpringJUnit4ClassRunner.class)
public class ConditionalScorerFactoryTest {
    @Configuration
    public static class ConditionalScorerFactoryTestConfiguration {
        @Autowired
        private List<AbstractServiceAutowiringFactory<Scorer>> scorerFactories;

        @Bean
        public ConditionalScorerFactory conditionalScorerFactory() {
            return new ConditionalScorerFactory();
        }

        @Bean
        public ConstantRegexScorerFactory constantRegexScorerFactory() {
            return new ConstantRegexScorerFactory();
        }

        @Bean
        public FactoryService<Scorer> scorerFactoryService() {
            FactoryService<Scorer> scorerFactoryService = new FactoryService<>();
            scorerFactories.forEach(scorerFactory -> scorerFactory.registerFactoryService(scorerFactoryService));
            return scorerFactoryService;
        }
    }

    @Autowired
    private FactoryService<Scorer> scorerFactoryService;
    @Autowired
    private ConditionalScorerFactory conditionalScorerFactory;

    @Test
    public void conditional_scorer_factory_should_register_to_scorer_factory_service() {
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
        conditionalScorerFactory.getProduct(getScorerConf(defaultScorerConfJsonObject));
    }

    @Test
    public void get_product_should_return_the_correct_scorer() throws IOException {
        List<JSONObject> predicates = singletonList(getBooleanPredicateJsonObject("myBooleanField", true));
        JSONObject conditionalScorerConfJsonObject = getConditionalScorerConfJsonObject("myConditionalScorer", predicates, defaultScorerConfJsonObject);
        Scorer scorer = conditionalScorerFactory.getProduct(getScorerConf(conditionalScorerConfJsonObject));
        Assert.assertNotNull(scorer);
        Assert.assertEquals(ConditionalScorer.class, scorer.getClass());
    }
}
