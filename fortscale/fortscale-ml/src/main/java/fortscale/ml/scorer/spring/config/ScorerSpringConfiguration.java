package fortscale.ml.scorer.spring.config;

import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.ScorersService;
import fortscale.ml.scorer.factory.ScorersFactoryConfig;
import fortscale.utils.factory.FactoryService;
import org.springframework.context.annotation.*;

@Configuration
@Import(ScorersFactoryConfig.class)
public class ScorerSpringConfiguration {
    @Bean
    public FactoryService<Scorer> getScorerFactoryService(){return new FactoryService<>();}

    @Bean
    public ScorersService getScorersService(){return new ScorersService();}
}
