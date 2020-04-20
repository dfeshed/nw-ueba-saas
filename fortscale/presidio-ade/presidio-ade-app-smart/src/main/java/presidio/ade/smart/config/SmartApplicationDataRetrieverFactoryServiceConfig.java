package presidio.ade.smart.config;

import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.utils.factory.AbstractServiceAutowiringFactory;
import fortscale.utils.factory.FactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import presidio.ade.domain.store.accumulator.smart.SmartAccumulationDataStoreConfig;

import java.util.Collection;

/**
 * Created by YaronDL on 9/4/2017.
 */


@ComponentScan(value = "fortscale.ml.model.retriever.factories.smart")
@Import({
        SmartAccumulationDataStoreConfig.class,
        SmartApplicationContextSelectorFactoryServiceConfig.class,
})
public class SmartApplicationDataRetrieverFactoryServiceConfig {
    @Autowired
    private Collection<AbstractServiceAutowiringFactory<AbstractDataRetriever>> dataRetrieverFactories;

    @Bean
    public FactoryService<AbstractDataRetriever> dataRetrieverFactoriesFactoryService() {
        FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
        dataRetrieverFactories.forEach(factory -> factory.registerFactoryService(dataRetrieverFactoryService));
        return dataRetrieverFactoryService;
    }
}
