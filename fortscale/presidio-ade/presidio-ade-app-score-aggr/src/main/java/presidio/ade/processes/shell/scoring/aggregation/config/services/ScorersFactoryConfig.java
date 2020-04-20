package presidio.ade.processes.shell.scoring.aggregation.config.services;

import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.scorer.Scorer;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;

import java.util.List;

/**
 * Created by barak_schuster on 7/25/17.
 */
@Configuration
@ComponentScan(
        value = {"fortscale.ml.scorer.factory"},
        excludeFilters = {@Filter(type = FilterType.REGEX, pattern = "fortscale.ml.scorer.factory.smart.*")}
        )
@Import({
//        application-specific confs
        DataRetrieverFactoryServiceConfig.class,
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
