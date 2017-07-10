package fortscale.ml.scorer.factory.config;

import fortscale.ml.model.config.DataRetrieverFactoryServiceConfig;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.scorer.Scorer;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Configuration
@ComponentScan(value = {"fortscale.ml.scorer.factory"})
@Import(DataRetrieverFactoryServiceConfig.class)
public class ScorersFactoryConfig {
    @Autowired
    private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;
    @Autowired
    private List<AbstractServiceAutowiringFactory<Scorer>> scorersFactories;

    @Bean
    public FactoryService<Scorer> scorerFactoryService() {
        FactoryService<Scorer> scorerFactoryService = new FactoryService<>();
        scorersFactories.forEach(x -> x.registerFactoryService(scorerFactoryService));
        return scorerFactoryService;
    }
}
