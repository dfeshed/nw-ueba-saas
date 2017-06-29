package fortscale.ml.model.config;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.factories.BuilderFactoriesConfig;
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
@Import(BuilderFactoriesConfig.class)
public class ModelBuilderFactoryServiceConfig {
    @Autowired
    private List<AbstractServiceAutowiringFactory<IModelBuilder>> modelBuildersFactories;

    @Bean
    public FactoryService<IModelBuilder> modelBuilderFactoryService() {
        FactoryService<IModelBuilder> factoryService = new FactoryService<>();
        modelBuildersFactories.forEach(x -> x.registerFactoryService(factoryService));
        return factoryService;
    }

}
