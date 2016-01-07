package fortscale.ml.scorer.factory;

import fortscale.ml.scorer.Scorer;
import fortscale.utils.factory.FactoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScorerFactoryServiceConfiguration {

    @Bean
    public FactoryService<Scorer> scorerFactoryService() {
        return new FactoryService<>();
    }
}
