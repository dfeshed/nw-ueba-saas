package fortscale.ml.scorer.spring.config;

import fortscale.ml.scorer.ScorersService;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.factory.ScorersFactoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Service;

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
    public ScorersFactoryService getScorersFactoryService(){return new ScorersFactoryService();}

    @Bean
    public ScorersService getScorersService(){return new ScorersService();}
}
