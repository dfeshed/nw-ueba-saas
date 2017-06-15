package fortscale.ml.model.config;

import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.utils.factory.FactoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 6/15/17.
 */
@Configuration
public class DataRetrieverFactoryServiceConfig {
    @Bean
    public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
        return new FactoryService<>();
    }

}
