package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.ListConditionalScorer;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.ListConditionalScorerConf;
import fortscale.ml.scorer.config.ListConditionalScorerConfTest;
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

public class ListConditionalScorerFactoryTest {
    @Autowired
    private FactoryService<Scorer> scorerFactoryService;
    @Autowired
    private ListConditionalScorerFactory listConditionalScorerFactory;

    @Test
    public void conditional_scorer_factory_should_register_to_factory_service() {
        Factory<Scorer> scorerFactory = scorerFactoryService.getFactory(ListConditionalScorerConf.SCORER_TYPE);
        Assert.assertNotNull(scorerFactory);
        Assert.assertEquals(listConditionalScorerFactory, scorerFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void get_product_should_fail_when_factory_config_is_null() {
        listConditionalScorerFactory.getProduct(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void get_product_should_fail_when_factory_config_is_of_wrong_type() throws IOException {
        listConditionalScorerFactory.getProduct(ListConditionalScorerConfTest.getScorerConf(
                ListConditionalScorerConfTest.defaultScorerConfJsonObject));
    }

    @Test
    public void get_product_should_return_the_correct_scorer() throws IOException {
        String name = "mySubScorer";
        String conditionalField = "myConditionalField";
        String conditionalValue = "myConditionalValue";
        Scorer scorer = listConditionalScorerFactory.getProduct(ListConditionalScorerConfTest.getScorerConf(
                ListConditionalScorerConfTest.getConditionalScorerConfJsonObject(ListConditionalScorerConf.SCORER_TYPE, name, ListConditionalScorerConfTest.defaultScorerConfJsonObject, conditionalField, conditionalValue)));
        Assert.assertNotNull(scorer);
        Assert.assertEquals(ListConditionalScorer.class, scorer.getClass());
    }

    @Configuration
    public static class ConditionalScorerFactoryTestConfig{
        @Autowired
        public List<AbstractServiceAutowiringFactory<Scorer>> scorersFactories;

        @Bean
        public ListConditionalScorerFactory getConditionalScorerFactory(){
            return new ListConditionalScorerFactory();
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
