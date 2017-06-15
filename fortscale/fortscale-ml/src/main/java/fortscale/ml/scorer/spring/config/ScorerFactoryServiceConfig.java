package fortscale.ml.scorer.spring.config;

import fortscale.ml.scorer.Scorer;
import fortscale.utils.factory.FactoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by barak_schuster on 6/15/17.
 */
@Configuration
public class ScorerFactoryServiceConfig {

    @Bean
    public FactoryService<Scorer> scorerFactoryService() {
        return new FactoryService<>();
    }

}
