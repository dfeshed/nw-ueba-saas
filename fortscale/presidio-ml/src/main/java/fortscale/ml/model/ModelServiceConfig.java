package fortscale.ml.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by barak_schuster on 3/26/17.
 */
@Configuration
@Import(ModelConfServiceConfig.class)
public class ModelServiceConfig {

    @Autowired
    private ModelConfService modelConfService;

    @Bean
    public ModelService modelService()
    {
        return new ModelService(modelConfService);
    }
}
