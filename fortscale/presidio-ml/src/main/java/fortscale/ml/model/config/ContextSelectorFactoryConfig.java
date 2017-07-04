package fortscale.ml.model.config;

import fortscale.ml.model.selector.IContextSelector;
import fortscale.ml.model.selector.factories.SelectorFactoriesConfig;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * Created by barak_schuster on 6/29/17.
 */
@Configuration
@Import(SelectorFactoriesConfig.class)
public class ContextSelectorFactoryConfig {
    @Autowired
    private List<AbstractServiceAutowiringFactory<IContextSelector>> contextSelectorsFactories;

    @Bean
    public FactoryService<IContextSelector> contextSelectorFactoryService() {

        FactoryService<IContextSelector> factoryService = new FactoryService<>();
        contextSelectorsFactories.forEach(x -> x.registerFactoryService(factoryService));
        return factoryService;
    }

}
