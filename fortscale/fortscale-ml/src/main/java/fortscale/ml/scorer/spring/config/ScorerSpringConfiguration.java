package fortscale.ml.scorer.spring.config;

import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.ScorersService;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.utils.factory.FactoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = "fortscale.ml.scorer",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = {"fortscale.ml.scorer.spring.config.*"}
        )
)
public class ScorerSpringConfiguration {

    @Bean
    public ScorerConfService getScorerConfService(){return new ScorerConfService();}

    @Bean
    public FactoryService<Scorer> getScorerFactoryService(){return new FactoryService<>();}

    @Bean
    public ScorersService getScorersService(){return new ScorersService();}
}
