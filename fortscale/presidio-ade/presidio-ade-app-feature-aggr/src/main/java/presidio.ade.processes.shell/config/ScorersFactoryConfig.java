package presidio.ade.processes.shell.config;

import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithmConfig;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * Created by barak_schuster on 7/30/17.
 */
@Configuration
@ComponentScan(value = {"fortscale.ml.scorer.factory"})
@Import({
//        application-specific confs
        SmartWeightsScorerAlgorithmConfig.class,
        FeatureAggregationDataRetrieverFactoryServiceConfig.class,
        ScorersModelConfServiceConfig.class,
        EventModelsCacheServiceConfig.class
})
public class ScorersFactoryConfig {
    @Autowired
    public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
    @Autowired
    public List<AbstractServiceAutowiringFactory<Scorer>> scorersFactories;

    @Bean
    public FactoryService<Scorer> scorerFactoryService() {
        FactoryService<Scorer> scorerFactoryService = new FactoryService<>();
        scorersFactories.forEach(x -> x.registerFactoryService(scorerFactoryService));
        return scorerFactoryService;
    }
}
